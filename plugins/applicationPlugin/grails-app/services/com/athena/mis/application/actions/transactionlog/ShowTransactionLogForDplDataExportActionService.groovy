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

class ShowTransactionLogForDplDataExportActionService extends BaseService implements ActionServiceIntf {

    AppSystemEntityCacheService appSystemEntityCacheService

    private Logger log = Logger.getLogger(getClass())

    private static final String ENTITY_ID = 'entityId'
    private static final String ENTITY_TYPE_ID = 'entityTypeId'
    private static final String TABLE_NAME = 'tableName'
    private static final String DATA_EXPORT_NAME = 'dataExportName'
    private static final String ENTITY_TYPE_NAME = 'entityTypeName'
    private static final String BENCHMARK = 'Benchmark'
    private static final String BENCHMARK_STAR = 'Benchmark Star'
    private static final String MENU_ID = 'menuId'
    private static final String SOURCE_DB_NAME = "sourceDbName"
    private static final String VENDOR_NAME = "vendorName"
    private static final String DB_INSTANCE_NOT_FOUND = "DB Instance is not found"
    private static final String DATA_EXPORT_NOT_FOUND = "Data Export is not found"
    private static final String NOT_TESTED = "DB Instance is not tested"

    AppDbInstanceService appDbInstanceService
    VendorService vendorService
    DataPipeLinePluginConnector dataPipeLineImplService
    AppMyFavouriteService appMyFavouriteService

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
     * 2. Check dplDataExport object existence
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     * */

    @Transactional
    public Map execute(Map result) {
        try {
            String entityIdStr = result.get(ENTITY_ID)
            long entityId = Long.parseLong(entityIdStr)
            long entityTypeId = Long.parseLong(result.entityTypeId.toString())
            long sourceId = Long.parseLong(result.sourceInstanceId.toString())
            AppDbInstance source = appDbInstanceService.read(sourceId)
            if (!source) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, DB_INSTANCE_NOT_FOUND)
            }
            if (!source.isTested) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, NOT_TESTED)
            }
            Vendor vendor = vendorService.read(source.vendorId)

            String errMsg = checkDataExportExistence(entityId)
            if (errMsg) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, errMsg)
            }

            if (result.tableName) {
                result.put(TABLE_NAME, result.tableName)
            }

            if (result.dataExportName) {
                result.put(DATA_EXPORT_NAME, result.dataExportName)
            }

            SystemEntity transactionLogType = appSystemEntityCacheService.read(entityTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG, source.companyId)
            if (transactionLogType) {
                result.put(ENTITY_TYPE_NAME, transactionLogType.key)
            }

            result.put(SOURCE_DB_NAME, source.dbName)
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
     * check whether data export object exists or not
     * @param entityId - DplDataExport.id
     * @return - error message or null depending on validation
     */
    private String checkDataExportExistence(long entityId) {
        Object dataExport = dataPipeLineImplService.readDplDataExport(entityId)
        if (!dataExport) {
            return DATA_EXPORT_NOT_FOUND

        }
        return null
    }
}
