package com.athena.mis.application.actions.userrole

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppUserService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for User-Role mapping CRUD for company user
 *  For details go through Use-Case doc named 'ShowUserRoleForCompanyUserActionService'
 */
class ShowUserRoleForCompanyUserActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String USER_ID = "userId"
    private static final String USER_NAME = "userName"
    private static final String LEFT_MENU = "leftMenu"
    private static final String USER_NOT_FOUND = "User not found"

    AppUserService appUserService

    /**
     * 1. check required parameter
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        // check required parameter
        if (!params.userId) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        return params
    }

    /**
     * 1. get user object from service
     * 2. check user object existence
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            long userId = Long.parseLong(result.userId.toString())
            // pull Role object form service
            AppUser user = appUserService.read(userId)
            // check Role object existence
            if (!user) {
                return super.setError(result, USER_NOT_FOUND)
            }
            result.put(USER_ID, user.id)
            result.put(USER_NAME, user.username)
            result.put(LEFT_MENU, result.leftMenu)
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
     * For show there is no success message
     * since the input-parameter already have "isError:false", just return the same map
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param obj - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
