package com.athena.mis.application.actions.appmyfavourite

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMyFavourite
import com.athena.mis.application.service.AppMyFavouriteService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Set page as default
 *  For details go through Use-Case doc named 'SetDefaultAppMyFavouriteActionService'
 */
class SetDefaultAppMyFavouriteActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String MY_FAVOURITE = "myFavourite"
    private static final String SUCCESS_MESSAGE = "This page has been set as default"
    private static final String NOT_FOUND_MSG = "Selected object not found"
    private static final String ALREADY_DEFAULT_MSG = "Selected feature is already set as default"

    AppMyFavouriteService appMyFavouriteService

    /**
     * 1. check required parameter
     * 2. get AppMyFavourite object by id
     * 3. check existence of object
     * @param params - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(params.id.toString())
            // get AppMyFavourite object by id
            AppMyFavourite myFavourite = appMyFavouriteService.read(id)
            // check existence of object
            if (!myFavourite) {
                return super.setError(params, NOT_FOUND_MSG)
            }
            if (myFavourite.isDefault) {
                return super.setError(params, ALREADY_DEFAULT_MSG)
            }
            params.put(MY_FAVOURITE, myFavourite)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive AppMyFavourite object from executePreCondition method
     * 2. set other objects isDefault false
     * 3. make selected object's isDefault true
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppMyFavourite myFavourite = (AppMyFavourite) result.get(MY_FAVOURITE)
            long userId = super.getAppUser().id
            // set other objects isDefault false
            setIsDefaultFalse(userId)
            // make selected object's isDefault true
            setAsDefault(myFavourite)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
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
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    private static final String STR_QUERY = """
        UPDATE app_my_favourite
        SET is_default = false,
        version = version + 1
        WHERE user_id = :userId
        AND is_default = true
    """

    /**
     * Set isDefault false
     * @param userId - id of logged in user
     */
    private void setIsDefaultFalse(long userId) {
        Map queryParams = [
                userId: userId
        ]
        executeUpdateSql(STR_QUERY, queryParams)
    }

    private static final String UPDATE_QUERY = """
        UPDATE app_my_favourite
        SET is_default = true,
        version = version + 1
        WHERE id = :id
    """

    /**
     * Set as default
     * @param myFavourite - object of AppMyFavourite
     */
    private void setAsDefault(AppMyFavourite myFavourite) {
        Map queryParams = [
                id: myFavourite.id
        ]
        executeUpdateSql(UPDATE_QUERY, queryParams)
    }
}
