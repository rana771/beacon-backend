package com.athena.mis.application.actions.dbinstancequery

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.DbInstanceQuery
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.*
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  show data feed query result page
 *  For details go through Use-Case doc named 'ShowQueryResultDocOfflineDataFeedQueryActionService'
 */
class ShowDbInstanceQueryResultActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DB_INSTANCE_QUERY = 'dbInstanceQuery'
    private static final String DB_INSTANCE = "dbInstance"
    private static final String VENDOR_NAME = "vendor"
    private static final String DB_NAME = "databaseName"
    private static final String NOTE_COUNT = "noteCount"
    private static final String SUCCESS_MSG = " query executed successfully."
    private static final String ERROR_MSG = "Could not execute query "
    private static final String DOUBLE_QUOTE = '"'
    private static final String SELECT = 'select'
    private static final String START = '<p>'
    private static final String END = '</p>'
    private static final String OUT_OF = ' out of '
    private static final String NOT_TESTED = "DB instance is not tested"
    private static
    final String CAN_NOT_PERFORM_DELETE_OPERATION = "Delete operation can not be performed for this database";

    AppSystemEntityCacheService appSystemEntityCacheService
    DbInstanceQueryService dbInstanceQueryService
    AppDbInstanceService appDbInstanceService
    AppNoteService appNoteService
    DataAdapterFactoryService dataAdapterFactoryService
    AppMyFavouriteService appMyFavouriteService

    /**
     * 1. check required inputs
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map executePreCondition(Map params) {
        // check required parameter
        if (!params.cId) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        long dbInstanceQueryId = Long.parseLong(params.cId.toString())
        DbInstanceQuery dbInstanceQuery = dbInstanceQueryService.read(dbInstanceQueryId)
        AppDbInstance appDbInstance = appDbInstanceService.read(dbInstanceQuery.dbInstanceId)
        String errMsg = validation(appDbInstance, dbInstanceQuery)
        // Check validation
        if (errMsg) {
            appMyFavouriteService.setIsDirtyAndIsDefault(params)
            return super.setError(params, errMsg)
        }
        params.put(DB_INSTANCE_QUERY, dbInstanceQuery)
        params.put(DB_INSTANCE, appDbInstance)
        return params
    }

    /**
     * 1. read DocOfflineDataFeedQuery from service
     * 2. check Validation
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            DbInstanceQuery dbInstanceQuery = (DbInstanceQuery) result.get(DB_INSTANCE_QUERY)
            AppDbInstance dbInstance = (AppDbInstance) result.get(DB_INSTANCE)
            SystemEntity vendor = appSystemEntityCacheService.readByReservedId(dbInstance.reservedVendorId, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, dbInstance.companyId)
            result.put(VENDOR_NAME, vendor.key)
            result.put(DB_NAME, dbInstance.dbName)
            SystemEntity noteEntityType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_NOTE_DB_QUERY, appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, dbInstanceQuery.companyId)
            int noteCount = appNoteService.countByEntityTypeIdAndEntityIdAndCompanyId(noteEntityType.id, dbInstanceQuery.id, dbInstanceQuery.companyId)
            result.put(NOTE_COUNT, noteCount)

            String errMsg = checkDeleteRestriction(dbInstance.isDeletable, dbInstanceQuery.sqlQuery)
            if (errMsg) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, errMsg)
            }

            String sqlQuery = dbInstanceQuery.sqlQuery
            dbInstanceQuery.message = EMPTY_SPACE
            int totalQuery = 0
            int successQuery = 0
            boolean isExecute = true
            for (String currentSqlQuery : sqlQuery.split(SEMICOLON)) {
                if (isExecute) {
                    DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(dbInstance)
                    Map executeResult = dataAdapter.execute(currentSqlQuery)
                    Boolean isError = (Boolean) executeResult.isError
                    if (isError.booleanValue()) {
                        dbInstanceQuery.message = dbInstanceQuery.message + START + ERROR_MSG + DOUBLE_QUOTE + currentSqlQuery + DOUBLE_QUOTE + END +
                                START + executeResult.exception.toString() + END
                        isExecute = false
                    } else {
                        successQuery++
                    }
                }
                totalQuery++
            }
            String message = START + successQuery.toString() + OUT_OF + totalQuery.toString() + SUCCESS_MSG + END +
                    dbInstanceQuery.message
            dbInstanceQuery.message = message
            dbInstanceQuery.numberOfExecution = dbInstanceQuery.numberOfExecution + 1
            dbInstanceQueryService.update(dbInstanceQuery)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
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
     * For Show there is no success message
     * since the input-parameter already have "isError:false", just return the same map
     *
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param obj -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Check docOfflineDataFeedQuery object existence
     *
     * @param appDbInstance - an object of docOfflineDataFeed
     * @param dbInstanceQuery - an object of docOfflineDataFeedQuery
     * @return error message when error occurred or null
     */
    private String validation(AppDbInstance appDbInstance, DbInstanceQuery dbInstanceQuery) {
        String errMsg
        //check docOfflineDataFeedQuery object existence
        errMsg = checkDbInstanceQueryExists(dbInstanceQuery)
        if (errMsg != null) return errMsg
        errMsg = testDbConnection(appDbInstance)
        if (errMsg != null) return errMsg
        return null
    }


    private String checkDbInstanceQueryExists(DbInstanceQuery dbInstanceQuery) {
        // Check docOfflineDataFeedQuery object existence
        if (!dbInstanceQuery) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

    /**
     * Test Database connection
     *
     * @param appDbInstance
     * @return
     */
    private String testDbConnection(AppDbInstance appDbInstance) {
        if (!appDbInstance.isTested) {
            return NOT_TESTED
        }
        return null
    }

    private String checkDeleteRestriction(boolean isDeletable, String script) {
        if (!isDeletable && StringUtils.containsIgnoreCase(script, "delete ")) {
            return CAN_NOT_PERFORM_DELETE_OPERATION
        }
        return null
    }
}

