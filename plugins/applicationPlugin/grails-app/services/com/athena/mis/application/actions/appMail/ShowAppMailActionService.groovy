package com.athena.mis.application.actions.appMail

import com.athena.mis.BaseService
import com.athena.mis.ActionServiceIntf

class ShowAppMailActionService extends BaseService implements ActionServiceIntf {

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
