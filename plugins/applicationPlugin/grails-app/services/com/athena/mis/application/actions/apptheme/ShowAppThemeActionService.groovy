package com.athena.mis.application.actions.apptheme

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService

/**
 *  Show UI for appTheme CRUD and list of appTheme for grid
 *  For details go through Use-Case doc named 'ShowThemeActionService'
 */
class ShowAppThemeActionService extends BaseService implements ActionServiceIntf {

    private static final String PLUGIN_ID = "pluginId"

    public Map executePreCondition(Map params) {
        String plugin = params.plugin ? params.plugin : 1
        int pluginId = Integer.parseInt(plugin)

        // if plugin id is not desired then redirect to application plugin as default
        if (pluginId >= 16) {
            pluginId = 1
        }
        params.put(PLUGIN_ID, pluginId)
        return params
    }

    public Map execute(Map params) {
        return params
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
