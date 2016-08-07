package com.athena.mis.application.actions.userrole

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.UserRole
import com.athena.mis.application.model.ListUserRoleForCompanyUserActionServiceModel
import com.athena.mis.application.service.ListUserRoleForCompanyUserActionServiceModelService
import com.athena.mis.application.service.UserRoleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update User-Role mapping object and to show on grid list
 *  For details go through Use-Case doc named 'UpdateUserRoleForCompanyUserActionService'
 */
class UpdateUserRoleForCompanyUserActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SUCCESS_MESSAGE = "user-role has been successfully updated"
    private static final String OBJECT_NOT_FOUND = "user-role not found"
    private static final String OBJ_USER_ROLE = "userRole"

    UserRoleService userRoleService
    ListUserRoleForCompanyUserActionServiceModelService listUserRoleForCompanyUserActionServiceModelService

    /**
     * 1. check required parameters
     * 2. read UserRole object form service
     * 3. check UserRole existence
     * @param parameters - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.existingRoleId || !params.userId || !params.roleId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long existingUserId = Long.parseLong(params.userId.toString())
            long existingRoleId = Long.parseLong(params.existingRoleId.toString())
            UserRole existingUserRole = userRoleService.read(existingUserId, existingRoleId)
            // check existence of old User-Role object
            if (!existingUserRole) {
                return super.setError(params, OBJECT_NOT_FOUND)
            }
            params.put(OBJ_USER_ROLE, existingUserRole)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. get UserRole object form map
     * 2. update UserRole object to DB
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            UserRole oldUserRole = (UserRole) result.get(OBJ_USER_ROLE)
            long roleId = Long.parseLong(result.roleId.toString())
            // update user role mapping
            userRoleService.updateForCompanyUser(oldUserRole, roleId)
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
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            UserRole userRole = (UserRole) result.get(OBJ_USER_ROLE)
            long roleId = Long.parseLong(result.roleId.toString())
            ListUserRoleForCompanyUserActionServiceModel savedUserRole = listUserRoleForCompanyUserActionServiceModelService.read(userRole.userId, roleId)
            result.put(OBJ_USER_ROLE, savedUserRole)
            return super.setSuccess(result, SUCCESS_MESSAGE)
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
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
