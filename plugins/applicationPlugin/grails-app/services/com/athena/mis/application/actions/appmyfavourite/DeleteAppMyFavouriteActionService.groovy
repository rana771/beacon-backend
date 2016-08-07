package com.athena.mis.application.actions.appmyfavourite

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMyFavourite
import com.athena.mis.application.service.AppMyFavouriteService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete AppMyFavourite object from DB
 *  For details go through Use-Case doc named 'DeleteAppMyFavouriteActionService'
 */
class DeleteAppMyFavouriteActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SUCCESS_MESSAGE = "Page has been successfully removed form 'My Favourite' page"
    private static final String MY_FAVOURITE = "myFavourite"
    private static final String NOT_FOUND_MSG = "Selected object not found"

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
            // check required parameter
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
            params.put(MY_FAVOURITE, myFavourite)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Delete AppMyFavourite object from DB
     * 1. get the AppMyFavourite object from map
     * 2. delete from db
     * 3. set default url null for user if AppMyFavourite object is default
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppMyFavourite myFavourite = (AppMyFavourite) result.get(MY_FAVOURITE)
            appMyFavouriteService.delete(myFavourite)
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
}
