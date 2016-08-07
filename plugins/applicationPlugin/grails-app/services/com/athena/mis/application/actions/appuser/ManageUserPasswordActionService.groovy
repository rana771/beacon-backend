package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import org.apache.log4j.Logger

/**
 *  Show UI for changing password
 *  For details go through Use-Case doc named 'ManageUserPasswordActionService'
 */
class ManageUserPasswordActionService extends BaseService implements ActionServiceIntf {

    private static final String NOT_FOUND_MSG = 'User not found'

    private Logger log = Logger.getLogger(getClass())

    /**
     * No pre conditions required
     * @param params - parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * Get logged in user and check existence of user
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map execute(Map result) {
        try {
            AppUser userInstance = getAppUser()  // get logged in user
            // check if user exists or not
            if (!userInstance) {
                return super.setError(result, NOT_FOUND_MSG)
            }
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
     * There is no success message
     * since the input-parameter already have "isError:false", just return the same map
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
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
