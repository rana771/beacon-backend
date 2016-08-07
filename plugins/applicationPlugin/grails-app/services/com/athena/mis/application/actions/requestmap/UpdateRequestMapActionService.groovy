package com.athena.mis.application.actions.requestmap

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Role
import com.athena.mis.application.service.RequestMapService
import com.athena.mis.application.service.RoleService
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update request map object
 *  For details go through Use-Case doc named 'UpdateRequestMapActionService'
 */
class UpdateRequestMapActionService extends BaseService implements ActionServiceIntf {

    private static String REQUEST_MAP_UPDATE_SUCCESS_MESSAGE = "Request map has been updated successfully"

    private Logger log = Logger.getLogger(getClass())

    SpringSecurityService springSecurityService
    RoleService roleService
    RequestMapService requestMapService

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
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Update request map
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            int pluginId = Integer.parseInt(result.pluginId.toString())
            long roleId = Long.parseLong(result.roleId.toString())
            List<Long> requestMapIds = []

            if (result.assignedFeatureIds.toString().length() > 0) {
                List<String> lstTemp = result.assignedFeatureIds.split(UNDERSCORE)
                for (int i = 0; i < lstTemp.size(); i++) {
                    requestMapIds << lstTemp[i].toLong()
                }
            }
            Role role = roleService.read(roleId)
            requestMapService.update(requestMapIds, role.authority, pluginId)
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
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, REQUEST_MAP_UPDATE_SUCCESS_MESSAGE)
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
