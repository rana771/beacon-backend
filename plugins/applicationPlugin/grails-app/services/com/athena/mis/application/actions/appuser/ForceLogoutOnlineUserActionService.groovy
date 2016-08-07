package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.service.ApplicationSessionService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Used to do force logout of online user
 *  For details go through Use-Case doc named 'ForceLogoutOnlineUserActionService'
 */
class ForceLogoutOnlineUserActionService extends BaseService implements ActionServiceIntf {

    ApplicationSessionService applicationSessionService

    private Logger log = Logger.getLogger(getClass())

    private static final String SUCCESS_SESSION_EXPIRED = 'Selected user session has expired'

    /**
     * No pre conditions required
     * @param params - parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * Check required parameter
     * Get customSessionObj of selected online user by sessionId
     * Destroy current session of user
     * Remove customSessionObj of user from List
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            List sessionIds = (List<String>) result.ids.split(UNDERSCORE)
            // check required parameter
            if (sessionIds.size() == 0) {
                return super.setError(result, ERROR_FOR_INVALID_INPUT)
            }
            applicationSessionService.forceLogOut(sessionIds)
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
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SUCCESS_SESSION_EXPIRED)
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
