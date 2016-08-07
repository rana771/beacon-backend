package com.athena.mis.application.actions.dbinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.model.ListAppDbInstanceActionServiceModel
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ListAppDbInstanceActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Test connection result of DocDbInstance object
 *  For details go through Use-Case doc named 'TestDbInstanceConnectionActionService'
 */
class TestDbInstanceConnectionActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String TEST_CON_SUCCESS = "Connection successful"
    private static final String DB_INSTANCE = 'dbInstance'
    private static final String DB_INSTANCE_NOT_FOUND = "Selected DB Instance not found"

    AppSystemEntityCacheService appSystemEntityCacheService
    AppDbInstanceService appDbInstanceService
    ListAppDbInstanceActionServiceModelService listAppDbInstanceActionServiceModelService
    DataAdapterFactoryService dataAdapterFactoryService

    /**
     * 1. check required parameter
     * 2. get dbInstance object
     * 3. check existence of dbInstance object
     * @param params - Request parameters
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long dbInstanceId = Long.parseLong(params.id.toString())
            AppDbInstance dbInstance = appDbInstanceService.read(dbInstanceId)
            // check existence of DbInstance object
            if (!dbInstance) {
                return super.setError(params, DB_INSTANCE_NOT_FOUND)
            }
            params.put(DB_INSTANCE, dbInstance)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. initialize jdbcConnection connection by dbInstance
     * 2. check connection error
     * 3. if connection is successful then update isTested = true
     * @param result - parameter from pre-condition
     * @return result- same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppDbInstance dbInstance = (AppDbInstance) result.get(DB_INSTANCE)
            if (!dbInstance.password) dbInstance.password = EMPTY_SPACE
            DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(dbInstance)
            Map testConnMap = dataAdapter.testConnection()
            boolean isConnError = (Boolean) testConnMap.get(IS_ERROR)
            if (isConnError.booleanValue()) {
                dbInstance.isTested = false
                appDbInstanceService.update(dbInstance)
                return super.setError(result, testConnMap.get(MESSAGE).toString())
            } else {
                long dbVendorRedShiftId = appSystemEntityCacheService.SYS_ENTITY_VENDOR_AMAZON_REDSHIFT
                long dbVendorPGId = appSystemEntityCacheService.SYS_ENTITY_VENDOR_POSTGRES
                long dbVendorGreenPlumId = appSystemEntityCacheService.SYS_ENTITY_VENDOR_GREEN_PLUM
                dbInstance.isTested = true
                String sql = EMPTY_SPACE
                // update db version
                if (dbVendorRedShiftId == dbInstance.reservedVendorId || dbVendorPGId == dbInstance.reservedVendorId || dbVendorGreenPlumId == dbInstance.reservedVendorId) {
                    sql = "select VERSION();"
                } else {
                    sql = "SELECT @@version;"
                }
                DataAdapterService daDbVersion = dataAdapterFactoryService.createAdapter(dbInstance)
                Map dataMap = daDbVersion.executeSelect(sql, true)
                Boolean isError = Boolean.parseBoolean(dataMap.get(IS_ERROR).toString())
                if (isError.booleanValue()) {
                    dbInstance.isTested = false
                    appDbInstanceService.update(dbInstance)
                    return super.setError(result, dataMap.get(MESSAGE).toString())
                }
                List lstResult = dataMap.lstResult
                dbInstance.dbVersion = (String) lstResult[0]
                appDbInstanceService.update(dbInstance)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return super.setError(result, ex.getMessage())
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * There is no success message
     * since the input-parameter already have "isError:false", just return the same map
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppDbInstance appDbInstance = (AppDbInstance) result.get(DB_INSTANCE)
            ListAppDbInstanceActionServiceModel model = listAppDbInstanceActionServiceModelService.read(appDbInstance.id)
            result.put(DB_INSTANCE, model)
            return super.setSuccess(result, TEST_CON_SUCCESS)
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param obj - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
