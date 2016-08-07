package com.athena.mis.application.actions.rolemodule

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Role
import com.athena.mis.application.entity.RoleModule
import com.athena.mis.application.service.RoleModuleService
import com.athena.mis.application.service.RoleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete Role-Module mapping object
 *  For details go through Use-Case doc named 'DeleteRoleModuleActionService'
 */
class DeleteRoleModuleActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ROLE_MODULE = "roleModule"
    private static final String SUCCESS_MESSAGE = "Role module mapping has been successfully deleted"
    private static final String RESERVED_ROLE_MESSAGE = "Role module mapping can not be deleted for Reserved role"

    RoleModuleService roleModuleService
    RoleService roleService

    /**
     * 1. check required parameter
     * 2. pull RoleModule object from service
     * 3. Check existence of RoleModule mapping object
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check existence of required parameter
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long roleModuleId = Long.parseLong(params.id.toString())
            RoleModule roleModule = roleModuleService.read(roleModuleId)
            // check existence of RoleModule mapping object
            if (!roleModule) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            Role role = roleService.read(roleModule.roleId)
            // check if reserved role
            if (role.roleTypeId != 0) {
                super.setError(params, RESERVED_ROLE_MESSAGE)
            }
            params.put(ROLE_MODULE, roleModule)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. Delete RoleModule object from DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            RoleModule roleModule = (RoleModule) result.get(ROLE_MODULE)
            // delete object from DB
            roleModuleService.delete(roleModule)
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
        return super.setSuccess(result, SUCCESS_MESSAGE)
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
