package com.athena.mis.application.actions.role

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Role
import com.athena.mis.application.model.ListRoleActionServiceModel
import com.athena.mis.application.service.ListRoleActionServiceModelService
import com.athena.mis.application.service.RoleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create role object and to show on grid list
 *  For details go through Use-Case doc named 'CreateRoleActionService'
 */
class CreateRoleActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String ROLE = "role"
    private static final String APP_USER = "appUser"
    private static final String AUTHORITY_PREFIX = "ROLE_"
    private static final String ROLE_NAME_DUPLICATE = "Role name already exists"
    private static final String ROLE_SAVE_SUCCESS_MESSAGE = "Role has been successfully saved"

    RoleService roleService
    ListRoleActionServiceModelService listRoleActionServiceModelService

    /**
     * 1. Get parameters from UI
     * 2. build role object
     * 3. check unique role name
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.name) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            AppUser appUser = super.getAppUser()
            // check unique role name
            int countDuplicate = roleService.countByNameIlikeAndCompanyId(params.name, appUser.companyId)
            if (countDuplicate > 0) {
                return super.setError(params, ROLE_NAME_DUPLICATE)
            }

            params.put(APP_USER, appUser)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Save role object in DB
     * Also UPDATE configAttribute of common requestMap(s) e.g :root, logout, manage password, & change password
     * This method is in transactional boundary and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            //build role object
            Role role = buildRoleObject(result, appUser)
            //save in DB
            roleService.create(role)
            //UPDATE configAttribute of common requestMap(s) e.g :root, logout, manage password, & change password
            updateApplicationRequestMap(role.authority)
            result.put(ROLE, role)
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
     * 1. Get Saved UserRole object from Model
     * 2. put success message
     *
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            Role role = (Role) result.get(ROLE)
            ListRoleActionServiceModel actionServiceModel = listRoleActionServiceModelService.read(role.id)
            result.put(ROLE, actionServiceModel)
            return super.setSuccess(result, ROLE_SAVE_SUCCESS_MESSAGE)
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param obj - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    private static final String QUERY_SELECT_NEXTVAL_SEQUENCE = "SELECT NEXTVAL('role_id_seq') as id"

    /**
     * get latest role_id from role_id_sequence to generate code
     *
     * @return - long value
     */
    private long getRoleId() {
        List results = executeSelectSql(QUERY_SELECT_NEXTVAL_SEQUENCE)
        long roleId = results[0].id
        return roleId
    }

    /**
     * build role object to create
     *
     * @param params - a map from caller method
     * @param appUser - an object of AppUser
     * @return - role object
     */
    private Role buildRoleObject(Map params, AppUser appUser) {
        Role role = new Role(params)
        role.id = getRoleId()//get latest role_id from role_id_sequence
        role.companyId = appUser.companyId
        role.createdBy = appUser.id
        role.createdOn = new Date()
        role.authority = AUTHORITY_PREFIX + role.id.toString() + UNDERSCORE + role.companyId
        return role
    }

    /**
     * Set access in root, logout, manage password, & change password pages for newly created role
     *
     * @param role - Role authority
     * @return - boolean value
     */
    private boolean updateApplicationRequestMap(String role) {
        String updateQuery = """
           UPDATE request_map
           SET
           config_attribute = config_attribute || ',${role}'
           WHERE
           is_common = true;
        """
        int updateCount = executeUpdateSql(updateQuery)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating common requestMap')
        }
        return true
    }
}
