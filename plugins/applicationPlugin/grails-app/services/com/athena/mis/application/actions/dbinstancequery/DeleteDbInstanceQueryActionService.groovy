package com.athena.mis.application.actions.dbinstancequery

import com.athena.mis.BaseService
import com.athena.mis.ActionServiceIntf
import com.athena.mis.application.entity.DbInstanceQuery
import com.athena.mis.application.service.DbInstanceQueryService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete docOfflineDataFeedQuery object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteDocOfflineDataFeedQueryActionService'
 */
class DeleteDbInstanceQueryActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass());

    private static final String DELETE_SUCCESS_MSG = "Db Instance Query has been successfully deleted"
    private static final String DB_INSTANCE_QUERY = "dbInstanceQuery"
    private static final String RESERVED_MSG = "Selected query is reserved and can not be deleted"

    DbInstanceQueryService dbInstanceQueryService

    /**
     * 1. check required parameter
     * 2. pull docOfflineDataFeedQuery object from DB
     * 3. check Validation
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

            long dataFeedQueryId = Long.parseLong(params.id.toString())

            //pull docOfflineDataFeedQuery object from DB
            DbInstanceQuery dbInstanceQuery = dbInstanceQueryService.read(dataFeedQueryId)

            //check Validation
            String errMsg = validation(dbInstanceQuery)
            if (errMsg) {
                return super.setError(params, errMsg)
            }

            params.put(DB_INSTANCE_QUERY, dbInstanceQuery)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Delete docOfflineDataFeedQuery object from DB
     * 1. get the docOfflineDataFeedQuery object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            DbInstanceQuery docOfflineDataFeedQuery = (DbInstanceQuery) result.get(DB_INSTANCE_QUERY)
            dbInstanceQueryService.delete(docOfflineDataFeedQuery)
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
     * 1. put success message
     *
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DELETE_SUCCESS_MSG)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param obj - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Check docOfflineDataFeedQuery instance existence
     *
     * @param docOfflineDataFeedQuery - and object of docOfflineDataFeedQuery
     * @return error message when error occurred or null
     * */

    private String validation(DbInstanceQuery docOfflineDataFeedQuery) {
        String errMsg
        // check docOfflineDataFeedQuery instance existence
        errMsg = isDocOfflineDataFeedQueryExist(docOfflineDataFeedQuery)
        if (errMsg != null) return errMsg
        return null
    }

    /**
     * Check docOfflineDataFeedQuery instance existence
     *
     * @param dbInstanceQuery - and object of docOfflineDataFeedQuery
     * @return error message when error occurred or null
     * */
    private String isDocOfflineDataFeedQueryExist(DbInstanceQuery dbInstanceQuery) {
        // check docOfflineDataFeedQuery instance existence
        if (!dbInstanceQuery) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        if (dbInstanceQuery.isReserved) {
            return RESERVED_MSG
        }
        return null
    }
}
