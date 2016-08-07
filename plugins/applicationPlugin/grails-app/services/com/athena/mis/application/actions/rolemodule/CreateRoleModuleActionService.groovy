package com.athena.mis.application.actions.rolemodule

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Role
import com.athena.mis.application.entity.RoleModule
import com.athena.mis.application.model.ListRoleModuleActionServiceModel
import com.athena.mis.application.service.ListRoleModuleActionServiceModelService
import com.athena.mis.application.service.RoleModuleService
import com.athena.mis.application.service.RoleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create Role-Module mapping object and to show on grid list
 *  For details go through Use-Case doc named 'CreateRoleModuleActionService'
 */
class CreateRoleModuleActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ROLE_MODULE = "roleModule"
    private static final String ROLE_NOT_FOUND = "Role is not found"
    private static final String ALREADY_MAPPED = "Selected module is already mapped with the role"
    private static final String SAVED_SUCCESS_MESSAGE = "Role module mapping has been successfully saved"
    private static final String RESERVED_ROLE_MESSAGE = "Role module mapping can not be created for Reserved role"

    RoleService roleService
    RoleModuleService roleModuleService
    ListRoleModuleActionServiceModelService listRoleModuleActionServiceModelService

    /**
     * 1. Get parameters from UI
     * 2. check Validation
     * 3. build UserRole object
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if ((!params.moduleId) || (!params.roleId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long moduleId = Long.parseLong(params.moduleId.toString())
            long roleId = Long.parseLong(params.roleId.toString())
            Role role = roleService.read(roleId)

            // check validation
            String errMsg = checkValidation(role, moduleId)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            RoleModule roleModule = buildRoleModuleObject(params)
            params.put(ROLE_MODULE, roleModule)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Save RoleModule object in DB
     * This method is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            RoleModule roleModule = (RoleModule) result.get(ROLE_MODULE)
            roleModuleService.create(roleModule)
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
     * 1. Get Saved RoleModule object from Model
     * 2. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            RoleModule roleModule = (RoleModule) result.get(ROLE_MODULE)
            ListRoleModuleActionServiceModel model = listRoleModuleActionServiceModelService.read(roleModule.id)
            result.put(ROLE_MODULE, model)
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
     * 2. check RoleModule object existence
     * @param role - an object of Role
     * @param moduleId - id of Module
     * @return error message when error occurred or null
     */
    private String checkValidation(Role role, long moduleId) {
        // check role object existence
        String errMsg = isRoleExist(role)
        if (errMsg) {
            return errMsg
        }
        // check RoleModule object existence
        errMsg = isRoleModuleMappingExist(role, moduleId)
        if (errMsg) {
            return errMsg
        }

        // check if reserved role
        if (role.roleTypeId != 0) {
            return RESERVED_ROLE_MESSAGE
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
     * Check RoleModule object existence
     * @param role - an object of Role
     * @param moduleId - id of Module
     * @return error message when error occurred or null
     */
    private String isRoleModuleMappingExist(Role role, long moduleId) {
        // check RoleModule object existence
        RoleModule roleModule = roleModuleService.findByRoleIdAndModuleIdAndCompanyId(role.id, moduleId, role.companyId)
        if (roleModule) {
            return ALREADY_MAPPED
        }
        return null
    }

    /**
     * Build RoleModule Object
     * @param params - serialized parameters from UI
     * @return newUserRole - New RoleModule object
     */
    private RoleModule buildRoleModuleObject(Map params) {
        RoleModule roleModule = new RoleModule(params)
        roleModule.companyId = getCompanyId()
        return roleModule
    }
}
