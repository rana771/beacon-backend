package com.athena.mis.application.actions.appshellscript

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppShellScript
import com.athena.mis.application.model.ListAppShellScriptActionServiceModel
import com.athena.mis.application.service.AppShellScriptService
import com.athena.mis.ActionServiceIntf
import com.athena.mis.application.service.ListAppShellScriptActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update new Shell Script object and show in grid
 *  For details go through Use-Case doc named 'UpdateAppShellScriptActionService'
 */
class UpdateAppShellScriptActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SHELL_SCRIPT = "appShellScript"
    private static final String NAME_MUST_BE_UNIQUE = "Script name must be unique"
    private static final String UPDATE_SUCCESS_MESSAGE = "Script has been successfully updated"
    private static final String RESERVED_NAME_UPDATE_ERROR_MSG = "Script is reserved, Unable to update name!"

    AppShellScriptService appShellScriptService
    ListAppShellScriptActionServiceModelService listAppShellScriptActionServiceModelService

    /**
     * Check for invalid input, object
     * Build new Shell Script
     * check validation
     *
     * @params parameters - Serialize parameters from UI
     * @return - A map on containing new Shell Script object or error messages
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // Check required param
            if ((!params.id) || (!params.version) || (!params.pluginId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long shellScriptId = Long.parseLong(params.id.toString())
            AppShellScript oldAppShellScript = appShellScriptService.read(shellScriptId)
            //check validation
            String errMsg = checkValidation(params, oldAppShellScript)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            AppShellScript newAppShellScript = getAppShellScript(params, oldAppShellScript)
            params.put(SHELL_SCRIPT, newAppShellScript)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    public Map executePostCondition(Map result) {
        //Do Nothing for post - operation
        return result
    }

    /**
     * Update Shell Script object in DB
     * This function is in transactional block and will roll back in case of any exception
     *
     * @param obj - map returned from executePreCondition method
     * @return - a map containing all objects necessary for build result
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppShellScript appShellScript = (AppShellScript) result.get(SHELL_SCRIPT)
            appShellScriptService.update(appShellScript)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Show newly updated Shell Script to grid
     * Show success message
     *
     * @params obj - map from execute method
     * @return - A map containing all necessary object for show
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppShellScript appShellScript = (AppShellScript) result.get(SHELL_SCRIPT)
            ListAppShellScriptActionServiceModel actionServiceModel = listAppShellScriptActionServiceModelService.read(appShellScript.id)
            Map successResultMap = new LinkedHashMap()
            successResultMap.put(SHELL_SCRIPT, actionServiceModel)
            return super.setSuccess(successResultMap, UPDATE_SUCCESS_MESSAGE)
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Build Failure result in case of any error
     *
     * @params obj - A map from execute method
     * @return - A map containing all necessary message for show
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build AppShellScript object for update
     *
     * @param params - a map from caller method
     * @param oldAppShellScript - an object of AppShellScript
     * @return appShellScript - AppShellScript object with new value
     */
    private AppShellScript getAppShellScript(Map params, AppShellScript oldAppShellScript) {
        AppShellScript newAppShellScript = new AppShellScript(params)
        oldAppShellScript.name = newAppShellScript.name
        oldAppShellScript.script = newAppShellScript.script
        oldAppShellScript.serverInstanceId = newAppShellScript.serverInstanceId
        oldAppShellScript.updatedOn = new Date()
        oldAppShellScript.updatedBy = super.appUser.id
        return oldAppShellScript
    }

    /**
     * Check plugin base and version validation
     * Check Reserved object name
     * Check duplicate name
     *
     * @param params - a map from caller method
     * @param oldAppShellScript - an object of AppShellScript
     * @return error message or null
     */
    private String checkValidation(Map params, AppShellScript oldAppShellScript) {
        // Check plugin base and version validation
        long version = Long.parseLong(params.version.toString())
        int pluginId = Integer.parseInt(params.pluginId.toString())
        if ((!oldAppShellScript) || (oldAppShellScript.version != version) || (oldAppShellScript.pluginId != pluginId)) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        //Check Reserved object name
        if (!oldAppShellScript.name.equalsIgnoreCase(params.name.toString()) && oldAppShellScript.isReserved) {
            return RESERVED_NAME_UPDATE_ERROR_MSG
        }
        long scriptTypeId = Long.parseLong(params.scriptTypeId.toString())
        //Check duplicate name
        int duplicateCount = appShellScriptService.countByNameIlikeAndScriptTypeIdAndCompanyIdAndIdNotEqual(params.name, scriptTypeId, super.getCompanyId(), oldAppShellScript.id)
        if (duplicateCount > 0) {
            return NAME_MUST_BE_UNIQUE
        }
        return null
    }
}
