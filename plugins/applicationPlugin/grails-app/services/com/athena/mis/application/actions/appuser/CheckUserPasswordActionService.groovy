package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger

/**
 * Check user password for reset request map
 * For details go through Use-Case doc named 'CheckUserPasswordActionService'
 */
class CheckUserPasswordActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    SpringSecurityService springSecurityService

    private static final String INVALID_PASSWORD = 'Invalid password'

    /**
     * 1. check required inputs
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        try {
            // check required parameter
            if (!params.pass) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. get password from parameter
     * 2. check password
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map execute(Map result) {
        try {
            String testPass = springSecurityService.encodePassword(result.pass) // encode password
            AppUser appUser = getAppUser()   // get logged in user
            // check if input password matches with user password
            if (!testPass.equals(appUser.password)) {
                return super.setError(result, INVALID_PASSWORD)
            }
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

