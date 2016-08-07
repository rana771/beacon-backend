package com.athena.mis.application.actions.sysconfiguration

import com.athena.mis.BaseService
import com.athena.mis.ActionServiceIntf

/**
 *  Show UI for SysConfiguration CRUD and list of sysConfiguration object(s) for grid
 *  For details go through Use-Case doc named 'ShowSysConfigurationActionService'
 */
class ShowSysConfigurationActionService extends BaseService implements ActionServiceIntf {

    private static final String PLUGIN_ID = "pluginId"

    /**
     * set plugin id
     * @param params - serialized parameters from UI
     * @return - a map containing plugin id
     */
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
    /**
     * return the same map as received
     * @param result - resulting map from pre execute method
     * @return - same map received from previous method
     */
    public Map execute(Map params) {
        return params
    }

    /**
     * return the same map as received
     * @param result - resulting map from execute
     * @return - same map received from previous method
     */
    public Map executePostCondition(Map result) {
        return result
    }
    /**
     * return the same map as received
     * @param result - resulting map from post execute
     * @return - same map received from previous method
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }
    /**
     * return the same map as received
     * @param result - resulting map from previous method
     * @return - same map received from previous method
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
