package com.athena.mis.application.actions.role

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Role
import com.athena.mis.application.model.ListRoleActionServiceModel
import com.athena.mis.application.service.ListRoleActionServiceModelService
import com.athena.mis.application.service.RoleService
import com.athena.mis.application.service.UserRoleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update role CRUD and to show on grid list
 *  For details go through Use-Case doc named 'UpdateRoleActionService'
 */
class UpdateRoleActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String ROLE = "role"
    private static final String ROLE_NAME_DUPLICATE = "Role name already exists"
    private static final String ROLE_UPDATE_SUCCESS_MESSAGE = "Role has been successfully updated"
    private static final String RESERVED_ROLE_MESSAGE = "Selected role is reserved and can not be updated"


    RoleService roleService
    UserRoleService userRoleService
    ListRoleActionServiceModelService listRoleActionServiceModelService

    /**
     * check required parameters
     * Check Validation
     * build role object
     *
     * @param parameters - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if ((!params.id) || (!params.version)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long roleId = Long.parseLong(params.id.toString())
            Role oldRole = roleService.read(roleId)
            AppUser appUser = super.getAppUser()
            //Check Validation
            String errMsg = checkValidation(oldRole, params, appUser, roleId)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            //build role object
            Role newRole = buildRoleObject(params, oldRole)
            params.put(ROLE, newRole)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * check existence of role object
     * check if reserved role
     * check duplicate role name
     *
     * @param oldRole - an object of Role
     * @param params - a map from caller method
     * @param appUser - an object of AppUser
     * @param roleId - id of Role
     * @return - error message or null
     */
    private String checkValidation(Role oldRole, Map params, AppUser appUser, long roleId) {
        long version = Long.parseLong(params.version)
        //check existence of role object
        if (!oldRole || (oldRole.version != version)) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        // check if reserved role
        if (oldRole.roleTypeId < 0) {
            return RESERVED_ROLE_MESSAGE
        }
        //check duplicate role name
        int countDuplicate = roleService.countByNameIlikeAndCompanyIdAndIdNotEqual(params.name, appUser.companyId, roleId)
        if (countDuplicate > 0) {
            return ROLE_NAME_DUPLICATE
        }
        return null
    }

    /**
     * 1. get UserRole object form map
     * 2. update UserRole object to DB
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Role role = (Role) result.get(ROLE)
            //Update in Db
            roleService.update(role)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
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
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            Role role = (Role) result.get(ROLE)
            ListRoleActionServiceModel actionServiceModel = listRoleActionServiceModelService.read(role.id)
            result.put(ROLE, actionServiceModel)
            return super.setSuccess(result, ROLE_UPDATE_SUCCESS_MESSAGE)
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param obj -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * build roleObject to update
     *
     * @param params - a map from caller method
     * @param oldRole - Role object
     * @return - Role object
     */
    private Role buildRoleObject(Map params, Role oldRole) {
        Role newRole = new Role(params)
        oldRole.name = newRole.name
        oldRole.updatedBy = super.getAppUser().id
        oldRole.updatedOn = new Date()
        return oldRole
    }
}
