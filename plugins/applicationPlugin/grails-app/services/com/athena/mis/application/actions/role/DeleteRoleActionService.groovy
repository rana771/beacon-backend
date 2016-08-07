package com.athena.mis.application.actions.role

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Role
import com.athena.mis.application.service.RoleService
import com.athena.mis.application.service.UserRoleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete role object from DB
 *  For details go through Use-Case doc named 'DeleteRoleActionService'
 */
class DeleteRoleActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    RoleService roleService
    UserRoleService userRoleService

    private static final String RESERVED_ROLE_MESSAGE = "Selected role is reserved and can not be deleted"
    private static
    final String ENTITY_NOT_FOUND_ERROR_MESSAGE = "No entity found with this id or might have been deleted by someone"
    private static final String DELETE_ROLE_SUCCESS_MESSAGE = "Role has been deleted successfully"
    private static final String HAS_ASSOCIATION_MESSAGE_USER_ROLE = " user is associated with selected role"
    private static final String ROLE = "ROLE"

    /**
     * Check different criteria to delete role object
     *      1) Check existence of role object
     *      2) Check if role is reserved or not
     *      3) check association with UserRole domain
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameter
            if (params.id == null) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // check existence of role object
            long roleId = Long.parseLong(params.id.toString())
            Role role = roleService.read(roleId)
            if (!role) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            // check if reserved role
            if (role.roleTypeId != 0) {
                return super.setError(params, RESERVED_ROLE_MESSAGE)
            }
            //check association with UserRole
            int countUserRole = userRoleService.countByRoleId(role.id)
            if (countUserRole > 0) {
                String msg = countUserRole + HAS_ASSOCIATION_MESSAGE_USER_ROLE
                return super.setError(params, msg)
            }
            params.put(ROLE, role)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Delete role object from DB
     * Also UPDATE configAttribute of common requestMap(s) e.g :root, logout, manage password, & change password
     * @param params -N/A
     * @param obj -map returned form executePreCondition method
     * @return -Boolean value (true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Role role = (Role) result.get(ROLE)
            roleService.delete(role) //delete from DB
            //UPDATE configAttribute of common requestMap(s) e.g :root, logout, manage password, & change password
            updateRequestMap(role)
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
        return super.setSuccess(result, DELETE_ROLE_SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * remove role from common request maps
     * @param role -Role object
     * @return -boolean value
     */
    private boolean updateRequestMap(Role role) {
        String queryStr = """
            UPDATE request_map
                SET config_attribute =
            CASE WHEN config_attribute LIKE '%,${role.authority},%' THEN
                REPLACE(config_attribute, ',${role.authority},' , ',')
            WHEN config_attribute LIKE '%,${role.authority}' THEN
                REPLACE(config_attribute, ',${role.authority}' , '')
                ELSE config_attribute
                END
        """
        int updateCount = executeUpdateSql(queryStr)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while deleting role from common requestMap')
        }
        return true
    }
}
