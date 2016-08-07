package com.athena.mis.application.actions.appshellscript

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppShellScript
import com.athena.mis.application.service.AppShellScriptService
import com.athena.mis.ActionServiceIntf
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete shell script object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAppShellScriptActionService'
 */
class DeleteAppShellScriptActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String SHELL_SCRIPT = "appShellScript"
    private static final String OBJ_NOT_FOUND = "Script not found"
    private static final String SHELL_SCRIPT_RESERVED = "Script is reserved"
    private static final String DELETE_SUCCESS_MSG = "Script has been successfully deleted!"

    AppShellScriptService appShellScriptService

    /**
     * Check for invalid input, object
     *
     * @param params - Serialize parameters from UI
     * @return - A map of containing Shell Script object or error messages
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(params.id.toString())
            AppShellScript appShellScript = appShellScriptService.read(id)
            String errMsg = checkValidation(appShellScript)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            params.put(SHELL_SCRIPT, appShellScript)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Delete Shell Script object in DB
     * This function is in transactional block and will roll back in case of any exception
     *
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for build result
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppShellScript appShellScript = (AppShellScript) result.get(SHELL_SCRIPT)
            appShellScriptService.delete(appShellScript)
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
     * Show success message
     *
     * @params obj - map from execute method
     * @return - A map containing all necessary object for show
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DELETE_SUCCESS_MSG)
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
     * Check Shell Script object existence
     * Check Shell Script reserved
     *
     * @param appShellScript
     * @return error message or null
     */
    private String checkValidation(AppShellScript appShellScript) {
        //Check Shell Script object existence
        if (!appShellScript) {
            return OBJ_NOT_FOUND
        }
        //Check Shell Script reserved
        if (appShellScript.isReserved) {
            return SHELL_SCRIPT_RESERVED
        }
        return null
    }
}
