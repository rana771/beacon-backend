package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.AppServerInstance
import com.athena.mis.application.entity.AppShellScript
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger

// AppShellScriptService is used to handle only CRUD related object manipulation (e.g. read, create, update, delete etc.)
class AppShellScriptService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    SystemEntityService systemEntityService
    AppSystemEntityCacheService appSystemEntityCacheService
    AppServerInstanceService appServerInstanceService
    AppDbInstanceService appDbInstanceService

    @Override
    public void init() {
        domainClass = AppShellScript.class
    }

    private static final String UPDATE_LAST_EXECUTED_ON_QUERY = """
                  UPDATE app_shell_script
                  SET last_executed_on = :lastExecutedOn,
                       number_of_execution = :numberOfExecution
                  WHERE id=:id
    """

    public int updateExecution(AppShellScript appShellScript) {
        Map queryParams = [
                id               : appShellScript.id, lastExecutedOn: DateUtility.getSqlDateWithSeconds(appShellScript.lastExecutedOn),
                numberOfExecution: appShellScript.numberOfExecution]
        int updateCount = executeUpdateSql(UPDATE_LAST_EXECUTED_ON_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Error occurred at appShellScript.lastExecutedOn")
        }
        return updateCount
    }

    /**
     * Get count of AppShellScript by name
     * @param name -name of AppShellScript
     * @return -an integer containing the value of count
     */
    public int countByNameIlikeAndScriptTypeIdAndCompanyId(String name, long scriptTypeId, long companyId) {
        int count = AppShellScript.countByNameIlikeAndScriptTypeIdAndCompanyId(name, scriptTypeId, companyId)
        return count
    }

    public findByNameAndCompanyId(String name, long companyId) {
        AppShellScript script = AppShellScript.findByNameAndCompanyId(name, companyId, [readOnly: true])
        return script
    }

    /**
     * Get count of AppShellScript by name and id
     * @param name -name of AppShellScript
     * @param id -AppShellScript.id
     * @return -an integer containing the value of count
     */
    public int countByNameIlikeAndScriptTypeIdAndCompanyIdAndIdNotEqual(String name, long scriptTypeId, long companyId, long id) {
        int count = AppShellScript.countByNameIlikeAndScriptTypeIdAndCompanyIdAndIdNotEqual(name, scriptTypeId, companyId, id)
        return count
    }

    public boolean createDefaultData(long companyId, long userId) {
        try {
            AppServerInstance serverInstance = appServerInstanceService.findByNameAndCompanyId('Native Server Instance', companyId)
            AppDbInstance appDbInstance = appDbInstanceService.findByNameAndCompanyId('System DB', companyId)
            SystemEntity sysEntityShell = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_SCRIPT_SHELL, companyId)
            SystemEntity sysEntitySql = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_SCRIPT_SQL, companyId)
            // script type shell
            new AppShellScript(name: AppShellScript.BACKUP_SCRIPT, script: 'hostname', scriptTypeId: sysEntityShell.id, companyId: companyId, pluginId: 1L, createdBy: userId, createdOn: new Date(), isReserved: true, serverInstanceId: serverInstance.id).save()
            new AppShellScript(name: AppShellScript.MAINTENANCE_SCRIPT, script: 'hostname', scriptTypeId: sysEntityShell.id, companyId: companyId, pluginId: 1L, createdBy: userId, createdOn: new Date(), isReserved: true, serverInstanceId: serverInstance.id).save()
            // script type sql
            new AppShellScript(name: AppShellScript.BACKUP_SQL, script: 'select 1', scriptTypeId: sysEntitySql.id, companyId: companyId, pluginId: 1L, createdBy: userId, createdOn: new Date(), isReserved: true, serverInstanceId: appDbInstance.id).save()
            new AppShellScript(name: AppShellScript.MAINTENANCE_SQL, script: 'select 2', scriptTypeId: sysEntitySql.id, companyId: companyId, pluginId: 1L, createdBy: userId, createdOn: new Date(), isReserved: true, serverInstanceId: appDbInstance.id).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private static final String DELETE_ALL = """
        DELETE FROM app_shell_script WHERE company_id = :companyId
    """

    /**
     * Delete all systemEntity by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    @Override
    public void createDefaultSchema() {
        String sqlIndex = "create unique index app_script_name_script_type_id_company_id_idx on app_shell_script(lower(name),script_type_id,company_id);"
        executeSql(sqlIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
