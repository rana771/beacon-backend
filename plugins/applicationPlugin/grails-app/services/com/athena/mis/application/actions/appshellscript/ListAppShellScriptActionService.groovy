package com.athena.mis.application.actions.appshellscript

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppShellScriptActionServiceModel
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of shell script for grid
 *  For details go through Use-Case doc named 'ListAppShellScriptActionService'
 */
class ListAppShellScriptActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * get the list of shellScript object
     *
     * @param params - serialize parameters from UI
     * @return result - A map of containing all object necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Map execute(Map params) {
        try {
            String plugin = params.pluginId
            int pluginId = Integer.parseInt(plugin)
            long scriptTypeId = Long.parseLong(params.scriptTypeId.toString())
            Closure additionalCondition = {
                eq('pluginId', pluginId)
                eq("scriptTypeId", scriptTypeId)
            }
            Map shellScriptMap = super.getSearchResult(params, ListAppShellScriptActionServiceModel.class, additionalCondition)
            params.put(LIST, shellScriptMap.list)
            params.put(COUNT, shellScriptMap.count)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Do nothing for post condition
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Wrap list of shellScript for grid
     *
     * @param obj - a map returned from execute method
     * @return result - a map containing necessary information for show page
     * map contains isError(true/false) depending on method success
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Build failure result in case of any error
     *
     * @param obj -map returned from previous methods
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
