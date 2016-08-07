package com.athena.mis.application.actions.rolemodule

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Role
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.RoleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for Role-Module mapping CRUD
 *  For details go through Use-Case doc named 'ShowRoleModuleActionService'
 */
class ShowRoleModuleActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ROLE_ID = "roleId"
    private static final String ROLE_NAME = "roleName"
    private static final String ROLE_NOT_FOUND = "Selected role not found"

    RoleService roleService
    AppMyFavouriteService appMyFavouriteService

    /**
     * 1. check required parameter
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        // check required parameter
        if (!params.oId) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        return params
    }

    /**
     * 1. read Role object from service
     * 2. check Role object existence
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            long roleId = Long.parseLong(result.oId.toString())
            // pull Role object form service
            Role role = roleService.read(roleId)
            String errMsg = isRoleExist(role)
            // check Role object existence
            if (errMsg) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, errMsg)
            }
            result.put(ROLE_ID, role.id)
            result.put(ROLE_NAME, role.name)
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

    /**
     * 1. check Role instance existence
     * @param role - an object of Role
     * @return error message when error occurred or null
     */
    private String isRoleExist(Role role) {
        // check Role object existence
        if (!role) {
            return ROLE_NOT_FOUND
        }
        return null
    }
}
