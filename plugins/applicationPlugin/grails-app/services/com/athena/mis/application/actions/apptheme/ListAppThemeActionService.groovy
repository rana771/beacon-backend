package com.athena.mis.application.actions.apptheme

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppTheme
import com.athena.mis.ActionServiceIntf
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of appTheme for grid
 *  For details go through Use-Case doc named 'ListThemeActionService'
 */
class ListAppThemeActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String PLUGIN_ID = "pluginId"

    /**
     * Do nothing for pre operation
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * Get list of appTheme
     * @param params - parameters from UI
     * @return - a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map execute(Map params) {
        try {
            String plugin = params.pluginId
            int pluginId = Integer.parseInt(plugin)
            Closure additionalCondition = {
                eq(PLUGIN_ID, pluginId)
            }
            Map themeMap = super.getSearchResult(params, AppTheme.class, additionalCondition)
            params.put(LIST, themeMap.list)
            params.put(COUNT, themeMap.count)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * do nothing for post operation
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Wrap appTheme list for grid
     * @param obj - map returned from execute method
     * @return - a map containing all objects necessary for grid view
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
