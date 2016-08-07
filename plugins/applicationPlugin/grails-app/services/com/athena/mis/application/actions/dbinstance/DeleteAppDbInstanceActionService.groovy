package com.athena.mis.application.actions.dbinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.DbInstanceQueryService
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete appDbInstance object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAppDbInstanceActionService'
 */
class DeleteAppDbInstanceActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass());

    private static final String DELETE_SUCCESS_MSG = "DB Instance has been successfully deleted!"
    private static final String APP_DB_INSTANCE = 'appDbInstance'
    private static
    final String ASSOCIATION_MSG_WITH_DATA_EXPORT = " data export is associated with this DB instance"
    private static
    final String ASSOCIATION_MSG_WITH_DATA_IMPORT = " data import is associated with this DB instance"
    private static
    final String ASSOCIATION_MSG_WITH_CDC = " cdc is associated with this DB instance"
    private static
    final String ASSOCIATION_MSG_WITH_DATA_INDEX = " data index is associated with this DB instance"
    private static final String ASSOCIATION_MSG_WITH_QUERY = " query is associated with this DB instance"
    private static final String DELETE_NATIVE_DB_INSTANCE_MSG = "Native DB Instance could not be deleted"

    AppDbInstanceService appDbInstanceService
    DbInstanceQueryService dbInstanceQueryService

    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService

    /**
     * 1. Check required parameter
     * 2. Pull AppDbInstance object from service
     * 3. Check for appDbInstance existence
     * 4. Association check for appDbInstance with different domains
     *
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameter
            if (params.id == null) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }

            long dbInstanceId = Long.parseLong(params.id.toString())
            AppDbInstance appDbInstance = (AppDbInstance) appDbInstanceService.read(dbInstanceId)

            // Check appDbInstance object existence
            if (appDbInstance == null) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }

            String associationMsg = hasAssociation(appDbInstance)
            // association check for appDbInstance with different domains
            if (associationMsg) {
                return super.setError(params, associationMsg)
            }

            params.put(APP_DB_INSTANCE, appDbInstance)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Delete AppDbInstance object from DB
     * 1. get the AppDbInstance object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */

    @Transactional
    public Map execute(Map result) {
        try {
            AppDbInstance appDbInstance = (AppDbInstance) result.get(APP_DB_INSTANCE)

            // Delete appDbInstance object from DB
            appDbInstanceService.delete(appDbInstance)

            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     *
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. put success message
     *
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DELETE_SUCCESS_MSG)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. Check association with docOfflineDataFeed
     * 2. Check association with docDataTransfer
     * @param appDbInstance - AppDbInstance object
     * @return - specific association message
     */
    private String hasAssociation(AppDbInstance appDbInstance) {

        if (appDbInstance.isNative) {
            return DELETE_NATIVE_DB_INSTANCE_MSG
        }

        int countQuery = dbInstanceQueryService.countByDbInstanceId(appDbInstance.id)
        if (countQuery > 0) {
            return countQuery.toString() + ASSOCIATION_MSG_WITH_QUERY
        }

        if (dataPipeLineImplService) {

            int countDataExport = dataPipeLineImplService.countDplDataExportByDbInstanceId(appDbInstance.id)
            if (countDataExport > 0) {
                return countDataExport.toString() + ASSOCIATION_MSG_WITH_DATA_EXPORT
            }

            countDataExport = dataPipeLineImplService.countDplDataExportByTargetDbInstanceId(appDbInstance.id)
            if (countDataExport > 0) {
                return countDataExport.toString() + ASSOCIATION_MSG_WITH_DATA_EXPORT
            }

            int countDataImport = dataPipeLineImplService.countDplDataImportByDbInstance(appDbInstance.id)
            if (countDataImport > 0) {
                return countDataImport.toString() + ASSOCIATION_MSG_WITH_DATA_IMPORT
            }

            int countDplCdc = dataPipeLineImplService.countDplCdcMySqlBySourceDbInstanceId(appDbInstance.id)
            if (countDplCdc > 0) {
                return countDplCdc.toString() + ASSOCIATION_MSG_WITH_CDC
            }

            countDplCdc = dataPipeLineImplService.countDplCdcMySqlByTargetDbInstanceId(appDbInstance.id)
            if (countDplCdc > 0) {
                return countDplCdc.toString() + ASSOCIATION_MSG_WITH_CDC
            }
        }

        if (documentImplService) {
            int countDataIndex = documentImplService.countDataIndexByDbInstanceId(appDbInstance.id)
            if (countDataIndex > 0) {
                return countDataIndex.toString() + ASSOCIATION_MSG_WITH_DATA_INDEX
            }
        }
        return null
    }
}
