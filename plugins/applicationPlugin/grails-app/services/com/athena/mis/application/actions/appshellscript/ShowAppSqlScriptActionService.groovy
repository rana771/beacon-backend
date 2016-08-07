package com.athena.mis.application.actions.appshellscript

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService

/**
 *  Show UI for AppSqlScript CRUD
 *  For details go through Use-Case doc named 'ShowAppSqlScriptActionService'
 */
class ShowAppSqlScriptActionService extends BaseService implements ActionServiceIntf {

    private static final String PLUGIN_ID = "pluginId"

    /**
     * 1. check required parameter
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        String plugin = params.plugin ? params.plugin : 1
        int pluginId = Integer.parseInt(plugin)

        // if plugin id is not desired then redirect to application plugin as default
        if (pluginId >= 14) {
            pluginId = 1
        }
        params.put(PLUGIN_ID, pluginId)
        return params
    }

    /**
     * There is no execute, so return the same map as received
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map execute(Map params) {
        return params
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
