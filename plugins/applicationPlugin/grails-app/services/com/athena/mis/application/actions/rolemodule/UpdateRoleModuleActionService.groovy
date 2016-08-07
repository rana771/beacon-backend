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
 *  Update Role-Module mapping object and show in grid
 *  For details go through Use-Case doc named 'UpdateRoleModuleActionService'
 */
class UpdateRoleModuleActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SUCCESS_MESSAGE = "Role module mapping has been successfully updated"
    private static final String OBJECT_NOT_FOUND = "Selected object not found"
    private static final String ROLE_MODULE = "roleModule"
    private static final String ALREADY_MAPPED = "Selected module is already mapped with the role"
    private static final String RESERVED_ROLE_MESSAGE = "Role module mapping can not be updated for Reserved role"

    RoleModuleService roleModuleService
    RoleService roleService
    ListRoleModuleActionServiceModelService listRoleModuleActionServiceModelService

    /**
     * 1. check required parameters
     * 2. read RoleModule object form service
     * 3. check RoleModule existence
     * @param parameters - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long roleModuleId = Long.parseLong(params.id.toString())
            RoleModule roleModule = roleModuleService.read(roleModuleId)
            // check existence of old RoleModule object
            if (!roleModule) {
                return super.setError(params, OBJECT_NOT_FOUND)
            }
            Role role = roleService.read(roleModule.roleId)
            // check if reserved role
            if (role.roleTypeId != 0) {
                super.setError(params, RESERVED_ROLE_MESSAGE)
            }
            long moduleId = Long.parseLong(params.moduleId.toString())
            if (roleModule.moduleId != moduleId) {
                RoleModule existingRoleModule = roleModuleService.findByRoleIdAndModuleIdAndCompanyId(roleModule.roleId, moduleId, roleModule.companyId)
                if (existingRoleModule) {
                    return super.setError(params, ALREADY_MAPPED)
                }
            }
            RoleModule newRoleModule = buildRoleModuleObject(params, roleModule)
            params.put(ROLE_MODULE, newRoleModule)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. get RoleModule object form map
     * 2. update RoleModule object to DB
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            RoleModule roleModule = (RoleModule) result.get(ROLE_MODULE)
            // update RoleModule mapping
            roleModuleService.update(roleModule)
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
            RoleModule roleModule = (RoleModule) result.get(ROLE_MODULE)
            ListRoleModuleActionServiceModel model = listRoleModuleActionServiceModelService.read(roleModule.id)
            result.put(ROLE_MODULE, model)
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

    /**
     * Build RoleModule Object
     * @param params - serialized parameters from UI
     * @param oldRoleModule - old object of RoleModule
     * @return newRoleModule - New RoleModule object
     */
    private RoleModule buildRoleModuleObject(Map params, RoleModule oldRoleModule) {
        RoleModule roleModule = new RoleModule(params)
        oldRoleModule.moduleId = roleModule.moduleId
        return oldRoleModule
    }
}
