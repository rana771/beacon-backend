package com.athena.mis.application.actions.requestmap

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Role
import com.athena.mis.application.service.RoleService
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class ListAvailableRoleActionService extends BaseService implements ActionServiceIntf {

    private static final String PLUGIN_ID = "pluginId "
    private static final String ROLE = "role"

    private Logger log = Logger.getLogger(getClass())

    RoleService roleService

    /**
     * Do nothing for pre operation
     */
    public Map executePreCondition(Map params) {
        if (!params.pluginId || !params.roleId) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        Long roleId = Long.valueOf(params.roleId.toString())
        Role role = roleService.read(roleId)
        Integer pluginId = Integer.valueOf(params.pluginId.toString())
        params.put(ROLE, role)
        params.put(PLUGIN_ID, pluginId)
        return params
    }

    /**
     * Get list of assigned & available features
     * 1. Get role object
     * 2. Check required parameters
     * @param parameters - parameters from UI
     * @param obj - N/A
     * @return - a map containing list of assigned, available features, isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map execute(Map params) {
        try {

            Role role = (Role) params.get(ROLE)
            Long pluginId = (Long) params.get(PLUGIN_ID)
            List lstAvailableFeatures = getAvailableFeatureByRole(role.authority, pluginId.intValue())
            params.put(LIST, lstAvailableFeatures)
            params.put(COUNT, lstAvailableFeatures.size())
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Do nothing for post operation
     */
    public Map executePostCondition(Map params) {
        return params
    }

    /**
     * Build a map with request map object & other necessary properties to show on UI
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for show
     */
    public Map buildSuccessResultForUI(Map params) {
        return params
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map params) {
        return params
    }

    /**
     * Get list of available features
     * @param roleAuthority - role authority is a string comes from caller method
     * @param pluginId - plugin id
     * @return - a list of available features
     */
    private List<GroovyRowResult> getAvailableFeatureByRole(String roleAuthority, int pluginId) {
        String queryStr = """
            SELECT id , feature_name, transaction_code, url
            FROM request_map
            WHERE id NOT IN (
                SELECT id
                FROM request_map
                WHERE
                config_attribute LIKE '%,${roleAuthority},%'
                OR
                config_attribute LIKE '${roleAuthority},%'
                OR
                config_attribute LIKE '%,${roleAuthority}'
                OR
                config_attribute = '${roleAuthority}'
            )
            AND plugin_id = ${pluginId}
            AND feature_name IS NOT NULL
            AND config_attribute <> 'IS_AUTHENTICATED_ANONYMOUSLY'
            AND config_attribute <> 'ROLE_-2,ROLE_RESELLER'
            ORDER BY feature_name ASC
        """

        List<GroovyRowResult> lstAvailableFeatures = executeSelectSql(queryStr)
        return lstAvailableFeatures
    }
}
