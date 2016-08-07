package com.athena.mis.application.actions.dbinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppSystemEntityCacheService
import grails.transaction.Transactional
import org.apache.log4j.Logger

/**
 * test source database connection for Dpl dashboard under settings
 * for details please go through use case named 'TestDbConnectionForDashboardActionService'
 */
class TestDbConnectionForDashboardActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String TEST_CON_SUCCESS = "Connection successful"
    private static final String DB_INSTANCE = 'dbInstance'
    private static final String DB_INSTANCE_NOT_FOUND = "Selected DB Instance not found"

    AppSystemEntityCacheService appSystemEntityCacheService
    AppDbInstanceService appDbInstanceService
    DataAdapterFactoryService dataAdapterFactoryService

    /**
     * check necessary parameters
     * read AppDbInstance object
     * check existence
     * @param parameters
     * @return Map
     */
    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try {
            if (!parameters.id) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            long dbInstanceId = Long.parseLong(parameters.id.toString())
            AppDbInstance dbInstance = appDbInstanceService.read(dbInstanceId)
            // check existence of DbInstance object
            String errMsg = checkExistence(dbInstance)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            parameters.put(DB_INSTANCE, dbInstance)
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * test connection
     * if successful update dbVersion
     * @param previousResult
     * @return
     */
    @Transactional
    Map execute(Map previousResult) {
        try {
            AppDbInstance dbInstance = (AppDbInstance) previousResult.get(DB_INSTANCE)
            if (!dbInstance.password) dbInstance.password = EMPTY_SPACE
            // test connection
            String errMsg = testConnection(dbInstance)
            if (errMsg) {
                previousResult.put(DB_INSTANCE, dbInstance)
                return super.setError(previousResult, errMsg)
            } else {
                String sql = getSelectVersionSql(dbInstance)
                // update db version
                errMsg = updateDbInstanceDbVersion(dbInstance, sql)
                if (errMsg) {
                    previousResult.put(DB_INSTANCE, dbInstance)
                    return super.setError(previousResult, errMsg)
                }
            }
            previousResult.put(DB_INSTANCE, dbInstance)
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return super.setError(previousResult, ex.getMessage())
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    Map buildSuccessResultForUI(Map executeResult) {
        return super.setSuccess(executeResult, TEST_CON_SUCCESS)
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * check whether database exists or not
     * @param dbInstance
     * @return - error message or null value depending on validation check
     */
    private String checkExistence(AppDbInstance dbInstance) {
        if (!dbInstance) {
            return DB_INSTANCE_NOT_FOUND
        }
        return null
    }

    /**
     * test connection
     * @param dbInstance
     * @return - error message or null value depending on success
     */
    private String testConnection(AppDbInstance dbInstance) {
        DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(dbInstance)
        Map testConnMap = dataAdapter.testConnection()
        boolean isConnError = (Boolean) testConnMap.get(IS_ERROR)
        if (isConnError.booleanValue()) {
            dbInstance.isTested = false
            appDbInstanceService.update(dbInstance)
            return testConnMap.get(MESSAGE).toString()
        }
        return null
    }

    /**
     * get select version sql by different sql
     * @param dbInstance
     * @return sql script
     */
    private String getSelectVersionSql(AppDbInstance dbInstance) {
        String sql
        if (appSystemEntityCacheService.SYS_ENTITY_VENDOR_AMAZON_REDSHIFT == dbInstance.reservedVendorId
                || appSystemEntityCacheService.SYS_ENTITY_VENDOR_POSTGRES == dbInstance.reservedVendorId
                || appSystemEntityCacheService.SYS_ENTITY_VENDOR_GREEN_PLUM == dbInstance.reservedVendorId) {
            sql = "select VERSION();"
        } else {
            sql = "SELECT @@version;"
        }
        return sql
    }

    /**
     * update AppDbInstance dbVersion
     * @param dbInstance
     * @param sql
     * @return - error message or null value depending on success
     */
    private String updateDbInstanceDbVersion(AppDbInstance dbInstance, String sql) {
        DataAdapterService daDbVersion = dataAdapterFactoryService.createAdapter(dbInstance)
        Map dataMap = daDbVersion.executeSelect(sql, true)
        Boolean isError = Boolean.parseBoolean(dataMap.get(IS_ERROR).toString())
        if (isError.booleanValue()) {
            dbInstance.isTested = false
            appDbInstanceService.update(dbInstance)
            return dataMap.get(MESSAGE).toString()
        }
        List lstResult = dataMap.lstResult
        dbInstance.dbVersion = (String) lstResult[0]
        dbInstance.isTested = true
        appDbInstanceService.update(dbInstance)
        return null
    }
}
