package com.athena.mis.application.actions.dbinstancequery

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.AppUser
import com.athena.mis.ActionServiceIntf
import com.athena.mis.application.entity.DbInstanceQuery
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.DbInstanceQueryService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional


/**
 *  Create new docOfflineDataFeedQuery object and show in grid
 *  For details go through Use-Case doc named 'CreateDocOfflineDataFeedQueryActionService'
 */

class CreateDbInstanceQueryActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String CREATE_SUCCESS_MESSAGE = 'Query has been successfully saved'
    private static final String DB_INSTANCE_QUERY = "dbInstanceQuery"
    private static final String NAME_MUST_BE_UNIQUE = "Name must be unique"

    DbInstanceQueryService dbInstanceQueryService
    AppDbInstanceService appDbInstanceService

    /**
     * 1. check required parameters
     * 2. build docOfflineDataFeedQuery object
     * 3. check validation
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.dbInstanceId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long dbInstanceId = Long.parseLong(params.dbInstanceId.toString())
            // check validation
            String errMsg = checkValidation(params, dbInstanceId)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            //build docOfflineDataFeedQuery object
            DbInstanceQuery dbInstanceQuery = getDbInstanceQuery(params)

            params.put(DB_INSTANCE_QUERY, dbInstanceQuery)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    //Do nothing for post - operation
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * 1. receive docOfflineDataFeedQuery object from executePreCondition method
     * 2. create new docOfflineDataFeedQuery
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            DbInstanceQuery dbInstanceQuery = (DbInstanceQuery) result.get(DB_INSTANCE_QUERY)

            // Save docOfflineDataFeedQuery object into DB
            dbInstanceQueryService.create(dbInstanceQuery)

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
        return super.setSuccess(result, CREATE_SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /*
    * Build docOfflineDataFeedQuery object for create
    *
    * @params parameters - serialize parameters form UI
    * @return -new docOfflineDataFeedQuery object
    * */

    private DbInstanceQuery getDbInstanceQuery(Map params) {
        AppUser appUser = super.getAppUser()
        if (!params.queryTypeId) {
            params.queryTypeId = 0
        }
        DbInstanceQuery dbInstanceQuery = new DbInstanceQuery(params)
        dbInstanceQuery.companyId = appUser.companyId
        dbInstanceQuery.createdBy = appUser.id
        dbInstanceQuery.createdOn = new Date()
        dbInstanceQuery.updatedBy = 0L
        dbInstanceQuery.updatedOn = null
        return dbInstanceQuery
    }

    /**
     * Check docOfflineDataFeed object existence
     * Check duplicate DocOfflineDataFeedQuery object
     *
     * @param parameters - serialize parameters form UI
     * @return error message when error occurred or null
     * */
    private String checkValidation(Map params, long dbInstanceId) {
        AppDbInstance appDbInstance = appDbInstanceService.read(dbInstanceId)
        // Check docOfflineDataFeed object existence
        if (appDbInstance == null) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        // @todo ensure company wise
        int duplicateCount = dbInstanceQueryService.countByNameIlikeAndDbInstanceId(params.name, dbInstanceId)
        // Check duplicate DocOfflineDataFeedQuery object
        if (duplicateCount > 0) {
            return NAME_MUST_BE_UNIQUE
        }
        return null
    }
}
