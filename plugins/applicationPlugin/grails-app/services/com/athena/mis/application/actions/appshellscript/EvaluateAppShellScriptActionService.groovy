package com.athena.mis.application.actions.appshellscript

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppServerInstance
import com.athena.mis.application.entity.AppShellScript
import com.athena.mis.application.service.AppServerInstanceService
import com.athena.mis.application.service.AppShellScriptService
import com.athena.mis.application.utility.AppServerConnection
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Evaluate specific object of shell script
 *  For details go through Use-Case doc named 'EvaluateAppShellScriptActionService'
 */
class EvaluateAppShellScriptActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_SHELL_SCRIPT = "appShellScript"
    private static final String SERVER_INSTANCE_NOT_FOUND = "Server instance not found"
    private static final String NOT_FOUND_ERROR_MESSAGE = "Selected Shell Script is not found"
    private static final String OUTPUT = "output"
    private static final String MSG_ERROR = "Error: "

    AppShellScriptService appShellScriptService
    AppServerInstanceService appServerInstanceService

    public Map executePreCondition(Map params) {
        // do nothing for pre operation
        return params
    }

    /**
     * get Shell Script object by id
     *
     * @params parameters - Serialize parameters from UI
     * @return - A map of Entity or error message
     */
    @Transactional
    public Map execute(Map params) {
        try {
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(params.id.toString())
            AppShellScript appShellScript = appShellScriptService.read(id)
            if (!appShellScript) {
                return super.setError(params, NOT_FOUND_ERROR_MESSAGE)
            }
            AppServerInstance appServerInstance = appServerInstanceService.read(appShellScript.serverInstanceId)
            if (!appServerInstance) {
                return super.setError(params, SERVER_INSTANCE_NOT_FOUND)
            }
            String message = checkServerInstance(appServerInstance)
            if (message) {
                return super.setError(params, message)
            }
            String script = appShellScript.script
            List<String> lstScript = script.split(NEW_LINE)
            String output = EMPTY_SPACE

            try {
                output = evaluateScript(appServerInstance, lstScript, output)
            } catch (Exception e) {
                return super.setError(params, MSG_ERROR + e.getMessage())
            }
            appShellScript.lastExecutedOn = new Date()
            appShellScript.numberOfExecution = appShellScript.numberOfExecution + 1
            appShellScriptService.updateExecution(appShellScript)
            params.put(OUTPUT, output)
            params.put(MESSAGE, "Shell script successfully evaluated.")
            params.put(APP_SHELL_SCRIPT, appShellScript)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    private String evaluateScript(AppServerInstance appServerInstance, List<String> lstScript, String output) {
        String strScript = EMPTY_SPACE
        for (int i = 0; i < lstScript.size(); i++) {
            if (i == lstScript.size() - 1) {
                strScript = strScript + lstScript[i]
            } else {
                strScript = strScript + lstScript[i] + SINGLE_SPACE + "&&" + SINGLE_SPACE
            }
        }
        output = output + AppServerConnection.execute(appServerInstance, strScript)
        return output
    }

    public Map executePostCondition(Map result) {
        // do nothing for post operation
        return result
    }

    /**
     * Build Success Results
     *
     * @params obj - Map return from execute method
     * @return a map of containing all object necessary for edit/delete page
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Build Failure result for UI
     *
     * @params obj - A map from execute method
     * @return a Map containing IsError and default error message/relevant error message to display
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Check server instance connection
     * @param appServerInstance - object AppServerInstance
     * @return corresponding error message or null value depending on connection check
     */
    private String checkServerInstance(AppServerInstance appServerInstance) {
        Map output = AppServerConnection.testConnection(appServerInstance, 'ls /tmp')
        boolean isError = Boolean.parseBoolean(output.isError.toString())
        if (isError) {
            return output.exception.toString()
        }
        return null
    }
}
