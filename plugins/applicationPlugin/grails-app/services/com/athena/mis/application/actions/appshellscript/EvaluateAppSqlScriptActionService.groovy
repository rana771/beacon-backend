package com.athena.mis.application.actions.appshellscript

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.AppShellScript
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppShellScriptService
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Evaluate specific object of shell script
 *  For details go through Use-Case doc named 'EvaluateAppShellScriptActionService'
 */
class EvaluateAppSqlScriptActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String OUTPUT = "output"
    private static final String NOT_FOUND_ERROR_MESSAGE = "Selected SQL Script is not found"
    private static final String DB_INSTANCE_NOT_FOUND = "Db Instance not found"
    private static final String APP_SHELL_SCRIPT = "appShellScript"
    private static final String DONE = "Done"
    private static final String DOUBLE_QUOTE = '"'
    private static final String START = '<p>'
    private static final String END = '</p>'
    private static final String OUT_OF = ' out of '
    private static final String SUCCESS_MSG = " query executed successfully."
    private static final String ERROR_MSG = "Could not execute query "
    private static final String CONNECTION_TEST_MSG = "DB instance connection must have tested before evaluate script."
    private static
    final String CAN_NOT_PERFORM_DELETE_OPERATION = "Delete operation can not be performed for this database";

    AppShellScriptService appShellScriptService
    AppDbInstanceService appDbInstanceService
    DataAdapterFactoryService dataAdapterFactoryService

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
            // check required parameters
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(params.id.toString())
            AppShellScript appShellScript = appShellScriptService.read(id)
            if (!appShellScript) {
                return super.setError(params, NOT_FOUND_ERROR_MESSAGE)
            }
            // get db instance object
            AppDbInstance dbInstance = appDbInstanceService.read(appShellScript.serverInstanceId)
            // check existence of db instance object
            if (!dbInstance) {
                return super.setError(params, DB_INSTANCE_NOT_FOUND)
            }
            // test connection
            String connMsg = testDbConnection(dbInstance)
            if (connMsg) {
                return super.setError(params, connMsg)
            }
            String errorMsg = checkDeleteRestriction(dbInstance.isDeletable, appShellScript.script)
            if (errorMsg) {
                return super.setError(params, errorMsg)
            }
            String script = appShellScript.script
            int totalQuery = 0
            int successQuery = 0
            boolean isExecute = true
            String errMsg = EMPTY_SPACE
            String errMsgForResult = EMPTY_SPACE
            for (String currentSqlQuery : script.split(SEMICOLON)) {
                if (isExecute) {
                    DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(dbInstance)
                    Map executeResult = dataAdapter.execute(currentSqlQuery)
                    Boolean isError = (Boolean) executeResult.isError
                    if (isError.booleanValue()) {
                        errMsg = errMsg + START + ERROR_MSG + DOUBLE_QUOTE + currentSqlQuery + DOUBLE_QUOTE + END + START + executeResult.exception.toString() + END
                        errMsgForResult = errMsgForResult + "\n" + ERROR_MSG + DOUBLE_QUOTE + currentSqlQuery + DOUBLE_QUOTE + "\n" + executeResult.exception.toString()
                        isExecute = false
                    } else {
                        successQuery++
                    }
                }
                totalQuery++
            }
            String message = START + successQuery.toString() + OUT_OF + totalQuery.toString() + SUCCESS_MSG + END + errMsg
            appShellScript.lastExecutedOn = new Date()
            appShellScript.numberOfExecution = appShellScript.numberOfExecution + 1
            appShellScriptService.updateExecution(appShellScript)
            String output = DONE + "\n" + successQuery.toString() + OUT_OF + totalQuery.toString() + SUCCESS_MSG + errMsgForResult
            params.put(OUTPUT, output)
            params.put(APP_SHELL_SCRIPT, appShellScript)
            return super.setSuccess(params, message)
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
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
     * Test Database connection
     *
     * @param appDbInstance
     * @return
     */
    private String testDbConnection(AppDbInstance appDbInstance) {
        if (!appDbInstance.isTested) {
            return CONNECTION_TEST_MSG
        }
        return null
    }

    private String checkDeleteRestriction(boolean isDeletable, String script) {
        if (!isDeletable && StringUtils.containsIgnoreCase(script, "delete ")) {
            return CAN_NOT_PERFORM_DELETE_OPERATION
        }
        return null
    }
}
