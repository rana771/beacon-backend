package com.athena.mis.application.actions.userrole

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Role
import com.athena.mis.application.entity.UserRole
import com.athena.mis.application.model.ListUserRoleForCompanyUserActionServiceModel
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.ListUserRoleForCompanyUserActionServiceModelService
import com.athena.mis.application.service.RoleService
import com.athena.mis.application.service.UserRoleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create User-role mapping object and to show on grid list
 *  For details go through Use-Case doc named 'CreateUserRoleForCompanyUserActionService'
 */
class CreateUserRoleForCompanyUserActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String USER_ROLE = "userRole"
    private static final String ROLE_NOT_FOUND = "Role not found"
    private static final String ALREADY_MAPPED = "User already mapped with this role"
    private static final String SAVED_SUCCESS_MESSAGE = "User role mapping has been successfully saved"
    private static final String USER_NOT_FOUND = "User not found"

    RoleService roleService
    AppUserService appUserService
    UserRoleService userRoleService
    ListUserRoleForCompanyUserActionServiceModelService listUserRoleForCompanyUserActionServiceModelService

    /**
     * 1. check required parameters
     * 2. check validation
     * 3. build UserRole object
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if ((!params.userId) || (!params.roleId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long userId = Long.parseLong(params.userId.toString())
            long roleId = Long.parseLong(params.roleId.toString())
            Role role = roleService.read(roleId)
            AppUser appUser = appUserService.read(userId)
            // check validation
            String errMsg = checkValidation(role, appUser)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            // build UserRole object
            UserRole userRole = getUserRoleMapping(appUser, role)
            params.put(USER_ROLE, userRole)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Save build UserRole object object in DB
     * This method is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            UserRole userRole = (UserRole) result.get(USER_ROLE)
            userRoleService.create(userRole)
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
     * 1. Get Saved UserRole object from Model
     * 2. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            UserRole userRole = (UserRole) result.get(USER_ROLE)
            ListUserRoleForCompanyUserActionServiceModel savedUserRole = listUserRoleForCompanyUserActionServiceModelService.read(userRole.userId, userRole.roleId)
            result.put(USER_ROLE, savedUserRole)
            return super.setSuccess(result, SAVED_SUCCESS_MESSAGE)
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

    /**
     * 1. check role object existence
     * 2. check appUser object existence
     * 3. check already user mapping
     * @param role - an object of Role
     * @param appUser - an object of AppUser
     * @return error message when error occurred or null
     */
    private String checkValidation(Role role, AppUser appUser) {
        String errMsg
        // check role object existence
        errMsg = isRoleExist(role)
        if (errMsg) return errMsg
        // check appUser object existence
        errMsg = isAppUserExist(appUser)
        if (errMsg) return errMsg
        // check already user mapping
        errMsg = isUserRoleMappingExist(role.id, appUser.id)
        if (errMsg) return errMsg
        return errMsg
    }

    /**
     * Check already user mapping
     * @param roleId - id of Role
     * @param userId - id of AppUser
     * @return error message when error occurred or null
     */
    private String isUserRoleMappingExist(long roleId, long userId) {
        UserRole userRole = userRoleService.read(userId, roleId)
        // check already user mapping
        if (userRole) {
            return ALREADY_MAPPED
        }
        return null
    }

    /**
     * 1. check role object existence
     * @param role - an object of Role
     * @return error message when error occurred or null
     */
    private String isRoleExist(Role role) {
        // check role object existence
        if (!role) {
            return ROLE_NOT_FOUND
        }
        return null
    }

    /**
     * 1. check appUser object existence
     * @param appUser - an object of AppUser
     * @return error message when error occurred or null
     */
    private String isAppUserExist(AppUser appUser) {
        // check appUser object existence
        if (!appUser) {
            return USER_NOT_FOUND
        }
        return null
    }

    /**
     * Build newUserRole Object
     * @param appUser - an object of AppUser
     * @param role - an object of Role
     * @return newUserRole - New UserRole object
     */
    private UserRole getUserRoleMapping(AppUser appUser, Role role) {
        UserRole newUserRole = new UserRole(userId: appUser.id, roleId: role.id)
        return newUserRole
    }
}
