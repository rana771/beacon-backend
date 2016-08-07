package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppSchedule
import com.athena.mis.application.entity.SystemEntity
import org.apache.log4j.Logger

class AppScheduleService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    SystemEntityService systemEntityService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Override
    public void init() {
        domainClass = AppSchedule.class
    }

    public AppSchedule findByCompanyIdAndJobClassName(long companyId, String jobClassName) {
        return AppSchedule.findByCompanyIdAndJobClassName(companyId, jobClassName, [readOnly: true])
    }

    public void executeByPlugin(long pluginId) {
        List<AppSchedule> lstSchedule = AppSchedule.findAllByPluginIdAndEnable(pluginId, true, [readOnly: true])
        lstSchedule.each {
            executeJobBootstrap(it)
        }
    }


    public void executeJobBootstrap(AppSchedule schedule) {
        Class clazz = Class.forName(schedule.jobClassName)
        SystemEntity type = systemEntityService.read(schedule.scheduleTypeId)
        if (type.reservedId == appSystemEntityCacheService.SIMPLE) {
            clazz.schedule(schedule.repeatInterval, schedule.repeatCount, ['companyId': schedule.companyId])
        } else if (type.reservedId == appSystemEntityCacheService.CRON) {
            clazz.schedule(schedule.cronExpression, ['companyId': schedule.companyId])
        }
    }

    public void executeJob(AppSchedule schedule) {
        Class clazz = Class.forName(schedule.jobClassName)
        SystemEntity type = appSystemEntityCacheService.read(schedule.scheduleTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_SCHEDULE, schedule.companyId)
        if (type.reservedId == appSystemEntityCacheService.SIMPLE) {
            clazz.schedule(schedule.repeatInterval, schedule.repeatCount, ['companyId': schedule.companyId])
        } else if (type.reservedId == appSystemEntityCacheService.CRON) {
            clazz.schedule(schedule.cronExpression, ['companyId': schedule.companyId])
        }
    }

    private static final String DELETE_ALL = """
        DELETE FROM app_schedule WHERE company_id = :companyId
    """

    /**
     * Delete all AppSchedule by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    public boolean createDefaultData(long companyId) {
        try {
            SystemEntity scheduleTypeSimple = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SIMPLE, companyId)

            new AppSchedule(version: 0, companyId: companyId, cronExpression: null, enable: false, jobClassName: 'com.athena.mis.application.job.AppBackupShellScriptJob', actionName: 'AppBackupShellScriptJobActionService',
                    name: 'Backup Shell Scheduler', pluginId: 1, repeatCount: 0, repeatInterval: 10000, scheduleTypeId: scheduleTypeSimple.id, updatedBy: 0, updatedOn: null).save()

            new AppSchedule(version: 0, companyId: companyId, cronExpression: null, enable: false, jobClassName: 'com.athena.mis.application.job.AppMaintenanceShellScriptJob', actionName: 'AppMaintenanceShellScriptJobActionService',
                    name: 'Maintenance Shell Scheduler', pluginId: 1, repeatCount: 0, repeatInterval: 10000, scheduleTypeId: scheduleTypeSimple.id, updatedBy: 0, updatedOn: null).save()

            new AppSchedule(version: 0, companyId: companyId, cronExpression: null, enable: false, jobClassName: 'com.athena.mis.application.job.AppBackupSqlScriptJob', actionName: 'AppBackupSqlScriptJobActionService',
                    name: 'Backup SQL Scheduler', pluginId: 1, repeatCount: 0, repeatInterval: 10000, scheduleTypeId: scheduleTypeSimple.id, updatedBy: 0, updatedOn: null).save()

            new AppSchedule(version: 0, companyId: companyId, cronExpression: null, enable: false, jobClassName: 'com.athena.mis.application.job.AppMaintenanceSqlScriptJob', actionName: 'AppMaintenanceSqlScriptJobActionService',
                    name: 'Maintenance SQL Scheduler', pluginId: 1, repeatCount: 0, repeatInterval: 10000, scheduleTypeId: scheduleTypeSimple.id, updatedBy: 0, updatedOn: null).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForExchangeHouse(long companyId) {
        try {
            SystemEntity scheduleTypeCron = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.CRON, companyId)

            new AppSchedule(version: 0, companyId: companyId, cronExpression: '0 0 1 1-31 1-12 ?', enable: false, jobClassName: 'com.athena.mis.exchangehouse.job.ExhAutoUpdateSanctionJob',
                    actionName: 'ExhAutoUpdateSanctionListJobActionService', name: 'Sanction auto update scheduler', pluginId: 9, repeatCount: 0, repeatInterval: 0, scheduleTypeId: scheduleTypeCron.id, updatedBy: 0, updatedOn: null).save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForArms(long companyId) {
        try {
            SystemEntity scheduleTypeCron = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.CRON, companyId)

            new AppSchedule(version: 0, companyId: companyId, cronExpression: '0 0 1 1-31 1-12 ?', enable: false, jobClassName: 'com.athena.mis.arms.job.RmsAutoUpdateSanctionJob',
                    actionName: 'RmsAutoUpdateSanctionListJobActionService', name: 'Auto update sanction scheduler', pluginId: 11, repeatCount: 0, repeatInterval: 0, scheduleTypeId: scheduleTypeCron.id, updatedBy: 0, updatedOn: null).save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForDocument(long companyId) {
        try {
            SystemEntity scheduleTypeCron = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.CRON, companyId)

            new AppSchedule(version: 0, companyId: companyId, cronExpression: '0/30 * * 1-31 1-12 ?', enable: false, jobClassName: 'com.athena.mis.document.job.DocIndexDocumentJob',
                    actionName: 'IndexDocDocumentJobActionService', name: 'Index all documents that marked as indexable', pluginId: 13, repeatCount: 0, repeatInterval: 0, scheduleTypeId: scheduleTypeCron.id, updatedBy: 0, updatedOn: null).save()

            new AppSchedule(version: 0, companyId: companyId, cronExpression: '20 * * 1-31 1-12 ?', enable: false, jobClassName: 'com.athena.mis.document.job.DocRetrieveOcrContentJob',
                    actionName: 'RetrieveDocDocumentOCRContentJobActionService', name: 'Retrieve the text content from OCR service', pluginId: 13, repeatCount: 0, repeatInterval: 0, scheduleTypeId: scheduleTypeCron.id, updatedBy: 0, updatedOn: null).save()

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForDataPipeline(long companyId) {
        try {
            SystemEntity scheduleTypeCron = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.CRON, companyId)

            new AppSchedule(version: 0, companyId: companyId, cronExpression: '20 * * 1-31 1-12 ?', enable: false, jobClassName: 'com.athena.mis.datapipeline.job.DplProcessMySqlJob',
                    actionName: 'DplProcessMySqlLogJobActionService', name: 'Get cdc log from MySql DB', pluginId: 14, repeatCount: 0, repeatInterval: 0, scheduleTypeId: scheduleTypeCron.id, updatedBy: 0, updatedOn: null).save()

            new AppSchedule(version: 0, companyId: companyId, cronExpression: '20 * * 1-31 1-12 ?', enable: false, jobClassName: 'com.athena.mis.datapipeline.job.DplProcessMSSqlJob',
                    actionName: 'DplProcessMSSqlLogJobActionService', name: 'Get cdc log from MSSql DB', pluginId: 14, repeatCount: 0, repeatInterval: 0, scheduleTypeId: scheduleTypeCron.id, updatedBy: 0, updatedOn: null).save()

            new AppSchedule(version: 0, companyId: companyId, cronExpression: '20 * * 1-31 1-12 ?', enable: false, jobClassName: 'com.athena.mis.datapipeline.job.DplCdcSendMailJob',
                    actionName: 'DplCdcSendMailDailyJobActionService', name: 'CDC day end summary report', pluginId: 14, repeatCount: 0, repeatInterval: 0, scheduleTypeId: scheduleTypeCron.id, updatedBy: 0, updatedOn: null).save()

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public void createDefaultSchema() {
        String sqlIndex = "create unique index app_schedule_name_company_id_idx on app_schedule(lower(name),company_id);"
        executeSql(sqlIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
