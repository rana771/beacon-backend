package com.athena.mis.application.actions.userrole

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.UserRole
import com.athena.mis.application.service.UserRoleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete User-Role mapping object
 *  For details go through Use-Case doc named 'DeleteUserRoleActionService'
 */
class DeleteUserRoleActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String USER_ROLE = "userRole"
    private static final String SUCCESS_MESSAGE = "User-Role mapping has been successfully deleted"

    UserRoleService userRoleService

    /**
     * 1. check required parameter
     * 2. pull UserRole object from service
     * 3. Check existence of userRole mapping object
     *
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */

    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            //check existence of required parameter
            if (!params.userId || !params.roleId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            Long userId = Long.parseLong(params.userId.toString())
            Long roleId = Long.parseLong(params.roleId.toString())
            UserRole userRole = userRoleService.read(userId, roleId)
            //Check existence of userRole mapping object
            if (!userRole) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            params.put(USER_ROLE, userRole)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. Delete UserRole object from DB
     * This function is in transactional boundary and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            UserRole userRole = (UserRole) result.get(USER_ROLE)
            //delete object from DB
            userRoleService.delete(userRole)
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
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
