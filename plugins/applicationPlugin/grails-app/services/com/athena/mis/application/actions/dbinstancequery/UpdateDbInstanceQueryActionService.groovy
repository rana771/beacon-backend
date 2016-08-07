package com.athena.mis.application.actions.dbinstancequery

import com.athena.mis.BaseService
import com.athena.mis.ActionServiceIntf
import com.athena.mis.application.entity.DbInstanceQuery
import com.athena.mis.application.service.DbInstanceQueryService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update docOfflineDataFeedQuery object and show in grid
 *  For details go through Use-Case doc named 'UpdateDocOfflineDataFeedQueryActionService'
 */
class UpdateDbInstanceQueryActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String UPDATE_SUCCESS_MESSAGE = "Query has been successfully updated"
    private static final String DB_INSTANCE_QUERY = "dbInstanceQuery"
    private static final String NAME_MUST_BE_UNIQUE = "Name must be unique"

    DbInstanceQueryService dbInstanceQueryService

    /**
     * 1. check required parameters
     * 2. pull docOfflineDataFeedQuery object
     * 3. check unique docOfflineDataFeedQuery
     * 4. build newDocOfflineDataFeedQuery object for update
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if ((!params.id) || (!params.version)) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }

            long dataFeedQueryId = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())

            DbInstanceQuery dbInstanceQuery = dbInstanceQueryService.read(dataFeedQueryId)

            // check unique docOfflineDataFeedQuery
            String errMsg = checkUniqueDocOfflineDataFeedQuery(params, dbInstanceQuery, version)
            if (errMsg) {
                return super.setError(params, errMsg)
            }

            //build docOfflineDataFeedQuery object
            DbInstanceQuery newDbInstanceQuery = getDbInstanceQuery(params, dbInstanceQuery)

            params.put(DB_INSTANCE_QUERY, newDbInstanceQuery)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }
    /**
     * 1. get the docOfflineDataFeedQuery object
     * 2. Update existing docOfflineDataFeedQuery in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            DbInstanceQuery docOfflineDataFeedQuery = (DbInstanceQuery) result.get(DB_INSTANCE_QUERY)
            // update docOfflineDataFeedQuery object into DB
            dbInstanceQueryService.update(docOfflineDataFeedQuery)
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
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build docOfflineDataFeedQuery object for update
     *
     * @param parameters - serialize parameters form UI
     * @return - new docOfflineDataFeedQuery object
     */
    private DbInstanceQuery getDbInstanceQuery(Map params, DbInstanceQuery oldDbInstanceQuery) {
        DbInstanceQuery dbInstanceQuery = new DbInstanceQuery(params)
        oldDbInstanceQuery.name = dbInstanceQuery.name
        oldDbInstanceQuery.sqlQuery = dbInstanceQuery.sqlQuery
        oldDbInstanceQuery.updatedBy = super.getAppUser().id
        oldDbInstanceQuery.updatedOn = new Date()
        oldDbInstanceQuery.resultPerPage = dbInstanceQuery.resultPerPage
        oldDbInstanceQuery.queryTypeId = dbInstanceQuery.queryTypeId
        return oldDbInstanceQuery
    }

    /**
     * Check duplicate DocOfflineDataFeedQuery object
     *
     * @param parameters - serialize parameters form UI
     * @return error message when error occurred or null
     */
    private String checkUniqueDocOfflineDataFeedQuery(Map params, DbInstanceQuery dbInstanceQuery, long version) {
        if ((!dbInstanceQuery) || (dbInstanceQuery.version != version)) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        int duplicateCount = dbInstanceQueryService.countByNameIlikeAndDbInstanceIdAndIdNotEqual(params.name.toString(), dbInstanceQuery)
        // Check duplicate DocOfflineDataFeedQuery object
        if (duplicateCount > 0) {
            return NAME_MUST_BE_UNIQUE
        }
        return null
    }
}
