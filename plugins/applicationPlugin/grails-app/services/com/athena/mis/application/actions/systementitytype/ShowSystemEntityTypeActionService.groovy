package com.athena.mis.application.actions.systementitytype

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for systemEntityType CRUD
 *  For details go through Use-Case doc named 'ShowSystemEntityTypeActionService'
 */
class ShowSystemEntityTypeActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String LEFT_MENU_PLUGIN = "pluginId"
    private static final String PARENT_ID = "oId"

    /**
     * 1. check required parameter
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        // check required parameter
        if (!params.plugin) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        return params
    }

    /**
     * 1. Get plugin id from parameters
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            String plugin = result.plugin ? result.plugin : 1
            int pluginId = Integer.parseInt(plugin)

            // if plugin id is not desired then redirect to application plugin as default
            if (pluginId >= 16) {
                pluginId = 1
            }
            result.put(PARENT_ID, result.oId?result.oId:null)
            result.put(LEFT_MENU_PLUGIN, pluginId)
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
     * For show there is no success message
     * since the input-parameter already have "isError:false", just return the same map
     *
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
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
}
