package com.athena.mis.application.actions.transactionlog

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.Vendor
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.VendorService
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import grails.transaction.Transactional
import org.apache.log4j.Logger

class ShowTransactionLogForDplDataImportActionService extends BaseService implements ActionServiceIntf {

    AppSystemEntityCacheService appSystemEntityCacheService

    private Logger log = Logger.getLogger(getClass())

    private static final String ENTITY_ID = 'entityId'
    private static final String ENTITY_TYPE_ID = 'entityTypeId'
    private static final String TABLE_NAME = 'tableName'
    private static final String DATA_IMPORT_NAME = 'dataImportName'
    private static final String ENTITY_TYPE_NAME = 'entityTypeName'
    private static final String BENCHMARK = 'Benchmark'
    private static final String BENCHMARK_STAR = 'Benchmark Star'
    private static final String MENU_ID = 'menuId'
    private static final String TARGET_DB_NAME = "targetDbName"
    private static final String VENDOR_NAME = "vendorName"
    private static final String DB_INSTANCE_NOT_FOUND = "DB Instance is not found"
    private static final String DATA_IMPORT_NOT_FOUND = "Data Import is not found"
    private static final String NOT_TESTED = "DB Instance is not tested"


    AppDbInstanceService appDbInstanceService
    VendorService vendorService
    AppMyFavouriteService appMyFavouriteService
    DataPipeLinePluginConnector dataPipeLineImplService

    /**
     * 1. check required inputs
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        String entityIdStr = params.oId ? params.oId : params.pId
        // check required parameter
        if (!entityIdStr || !params.entityTypeId) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        params.put(ENTITY_ID, entityIdStr)
        return params
    }

    /**
     * 1. read docAnswer from service
     * 2. Check dplDataImport object existence
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     * */

    @Transactional
    public Map execute(Map result) {
        try {
            String entityIdStr = result.get(ENTITY_ID)
            long entityId = Long.parseLong(entityIdStr)
            long entityTypeId = Long.parseLong(result.entityTypeId.toString())
            long targetId = Long.parseLong(result.targetInstanceId.toString())
            AppDbInstance target = appDbInstanceService.read(targetId)
            if (!target) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, DB_INSTANCE_NOT_FOUND)
            }
            if (!target.isTested) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, NOT_TESTED)
            }

            Vendor vendor = vendorService.read(target.vendorId)

            String errMsg = checkDataImportExistence(entityId)
            if (errMsg) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, errMsg)
            }

            if (result.tableName) {
                result.put(TABLE_NAME, result.tableName)
            }

            if (result.dataImportName) {
                result.put(DATA_IMPORT_NAME, result.dataImportName)
            }

            SystemEntity transactionLogType = appSystemEntityCacheService.read(entityTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG, target.companyId)
            if (transactionLogType) {
                result.put(ENTITY_TYPE_NAME, transactionLogType.key)
            }

            result.put(TARGET_DB_NAME, target.dbName)
            result.put(VENDOR_NAME, vendor.name)
            result.put(ENTITY_TYPE_ID, entityTypeId)
            result.put(ENTITY_ID, entityId)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
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
     * For select there is no success message
     * since the input-parameter already have "isError:false", just return the same map
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param obj -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * check whether data import exists or not
     * @param entityId - DplDataImport.id
     * @return - error message or null depending on validation
     */
    private String checkDataImportExistence(long entityId) {
        Object dataImport = dataPipeLineImplService.readDplDataImport(entityId)
        if (!dataImport) {
            return DATA_IMPORT_NOT_FOUND
        }
        return null
    }
}
