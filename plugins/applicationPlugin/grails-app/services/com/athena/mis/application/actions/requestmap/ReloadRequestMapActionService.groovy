package com.athena.mis.application.actions.requestmap

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.service.ReservedRoleService
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger

/**
 *  Reload request map
 *  For details go through Use-Case doc named 'ReloadRequestMapActionService'
 */
class ReloadRequestMapActionService extends BaseService implements ActionServiceIntf {

    private static final String SUCCESS_MSG = "Request map has been reloaded successfully"
    private static final String NOT_ALLOWED = "Logged in user is not allowed to reload request map"

    private Logger log = Logger.getLogger(getClass())

    SpringSecurityService springSecurityService

    /**
     * 1. check user access
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        try {
            if (!appSessionService.hasRole(ReservedRoleService.ROLE_TYPE_APP_DEVELOPMENT)) {
                return super.setError(params, NOT_ALLOWED)
            }
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Reload request map
     * Reload Role
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map execute(Map result) {
        try {
            appSessionService.resetRole()
            springSecurityService.clearCachedRequestmaps()
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
        return super.setSuccess(result, SUCCESS_MSG)
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
