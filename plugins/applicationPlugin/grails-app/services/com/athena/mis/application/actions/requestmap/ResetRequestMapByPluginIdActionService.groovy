package com.athena.mis.application.actions.requestmap

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Role
import com.athena.mis.application.service.RequestMapService
import com.athena.mis.application.service.ReservedRoleService
import com.athena.mis.application.service.RoleService
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Reset request map with default value of individual plugin
 * For details go through Use-Case doc named 'ResetRequestMapByPluginIdActionService'
 */
class ResetRequestMapByPluginIdActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    RequestMapService requestMapService
    SpringSecurityService springSecurityService
    RoleService roleService

    private static final String NOT_ALLOWED = "Logged in user is not allowed to reset request map"
    private static final String ROLE_NOT_FOUND = "Selected role not found"

    /**
     * 1. check required parameters
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.pluginId || !params.roleId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            if (!appSessionService.hasRole(ReservedRoleService.ROLE_TYPE_APP_DEVELOPMENT)) {
                return super.setError(params, NOT_ALLOWED)
            }
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Reset default request map by company id, plugin id and role id
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            long pluginId = Long.parseLong(result.pluginId.toString())
            long roleId = Long.parseLong(result.roleId.toString())
            Role role = roleService.read(roleId)
            if (!role) {
                return super.setError(result, ROLE_NOT_FOUND)
            }
            requestMapService.resetDefaultRequestMapsByPluginId(pluginId, role)
            springSecurityService.clearCachedRequestmaps()
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
     * There is no success message
     * since the input-parameter already have "isError:false", just return the same map
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
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
