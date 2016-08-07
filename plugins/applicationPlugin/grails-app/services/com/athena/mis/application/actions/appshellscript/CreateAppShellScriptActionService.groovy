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
 *  Create new shell script object and show in grid
 *  For details go through Use-Case doc named 'CreateAppShellScriptActionService'
 */
class CreateAppShellScriptActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SHELL_SCRIPT = "appShellScript"
    private static final String NAME_MUST_BE_UNIQUE = "Script name must be unique"
    private static final String CREATE_SUCCESS_MESSAGE = "Script has been successfully saved"

    AppShellScriptService appShellScriptService
    ListAppShellScriptActionServiceModelService listAppShellScriptActionServiceModelService

    /**
     * Check required parameters
     * Build AppShellScript object with parameters
     * Ensure uniqueness of Shell Script name
     *
     * @params parameters - serialize parameters form UI
     * @return - a map of containing necessary objects for execute
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // Check required parameters
            if (!params.pluginId || !params.scriptTypeId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long scriptTypeId = Long.parseLong(params.scriptTypeId.toString())
            AppShellScript shellScript = getAppShellScript(params)     // build ShellScript object
            int duplicateCount = appShellScriptService.countByNameIlikeAndScriptTypeIdAndCompanyId(shellScript.name, scriptTypeId, super.getCompanyId())
            if (duplicateCount > 0) {
                return super.setError(params, NAME_MUST_BE_UNIQUE)
            }
            params.put(SHELL_SCRIPT, shellScript)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Create Shell Script object to DB
     * Ths method is in Transactional boundary so will rollback in case of any exception
     *
     * @params obj - map returned from executePreCondition method
     * @return - A map of saved Shell Script object or error messages
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppShellScript appShellScript = (AppShellScript) result.get(SHELL_SCRIPT)
            appShellScriptService.create(appShellScript)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Do nothing for post operation
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Show newly created Shell Script to grid
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
            return super.setSuccess(successResultMap, CREATE_SUCCESS_MESSAGE)
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Build failure result in case of any error
     *
     * @param obj - map returned from previous methods, can be null
     * @return - a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build Shell Script object for create
     *
     * @param params - a map from caller method
     * @return appShellScript - AppShellScript object
     */
    private AppShellScript getAppShellScript(Map params) {
        AppShellScript appShellScript = new AppShellScript(params)
        appShellScript.companyId = super.companyId
        appShellScript.createdBy = super.appUser.id
        appShellScript.createdOn = new Date()
        appShellScript.updatedBy = 0L
        appShellScript.updatedOn = null
        return appShellScript
    }
}
