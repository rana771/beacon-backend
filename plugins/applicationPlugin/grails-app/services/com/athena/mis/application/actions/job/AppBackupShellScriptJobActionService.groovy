package com.athena.mis.application.actions.job

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.MailSenderService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.AppServerInstance
import com.athena.mis.application.entity.AppShellScript
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.AppServerInstanceService
import com.athena.mis.application.service.AppShellScriptService
import com.athena.mis.application.utility.AppServerConnection
import org.apache.log4j.Logger

import static grails.async.Promises.task

/**
 *  Action Service for Backup Script Scheduler
 *  For details go through Use-Case doc named 'AppBackupScriptJobActionService'
 */
class AppBackupShellScriptJobActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String OBJECT_NOT_FOUND = "Backup shell script not found"
    private static final String SERVER_INSTANCE_NOT_FOUND = "Native server instance not found"
    private static
    final String ERR_MSG = "Error occurred in AppBackupShellScriptJobActionService. Check log for details..."
    private static final String TRANSACTION_CODE = "AppBackupShellScriptJobActionService"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Backup Script mail not send due to absence of mail template"
    private static final String SUCCESS_MESSAGE = "Backup Script has been executed successfully."
    private static
    final String NOT_PRODUCTION_MODE = "Backup shell script mail is not sent because application is not in production mode"

    AppShellScriptService appShellScriptService
    AppServerInstanceService appServerInstanceService
    AppMailService appMailService
    MailSenderService mailSenderService
    AppConfigurationService appConfigurationService

    /**
     * No pre conditions required
     * @param parameters - Request parameters
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map parameters) {
        return parameters
    }

    /**
     * 1. Execute Script
     * 2. Update Execution Date & Time
     * 3. Send mail
     * @param parameters - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map execute(Map parameters) {
        long companyId = (long) parameters.companyId
        try {
            AppShellScript shellScript = appShellScriptService.findByNameAndCompanyId(AppShellScript.BACKUP_SCRIPT, companyId)
            if (!shellScript) {
                log.error(OBJECT_NOT_FOUND)
                return parameters
            }
            AppServerInstance serverInstance = appServerInstanceService.read(shellScript.serverInstanceId)
            if (!serverInstance) {
                log.error(SERVER_INSTANCE_NOT_FOUND)
                return parameters
            }
            String message = checkServerInstance(serverInstance)
            if (message) {
                log.error(message)
                return parameters
            }
            executeScript(shellScript, serverInstance)
            updateScript(shellScript)
            sendMail(shellScript, serverInstance)
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            println ERR_MSG
            return parameters
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Since there is no success message return the same map
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
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

    /**
     * Execute shell script
     * @param appShellScript - object of AppShellScript
     * @param serverInstance - object of AppServerInstance
     */
    private void executeScript(AppShellScript appShellScript, AppServerInstance serverInstance) {
        String script = appShellScript.script
        List<String> lstScript = script.split(NEW_LINE)
        String strScript = EMPTY_SPACE
        for (int i = 0; i < lstScript.size(); i++) {
            if (i == lstScript.size() - 1) {
                strScript = strScript + lstScript[i]
            } else {
                strScript = strScript + lstScript[i] + SINGLE_SPACE + "&&" + SINGLE_SPACE
            }
        }
        AppServerConnection.execute(serverInstance, strScript)
    }

    /**
     * Update executed date & time
     * @param appShellScript - object of AppShellScript
     */
    private void updateScript(AppShellScript appShellScript) {
        appShellScript.lastExecutedOn = new Date()
        appShellScript.numberOfExecution = appShellScript.numberOfExecution + 1
        appShellScriptService.updateExecution(appShellScript)
    }

    /**
     * Send mail
     * @param shellScript - object of AppShellScript
     * @param serverInstance - object of AppServerInstance
     */
    private void sendMail(AppShellScript shellScript, AppServerInstance serverInstance) {
        AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, shellScript.companyId, true)
        if (!appMail) {
            log.error(MAIL_TEMPLATE_NOT_FOUND)
            return
        }
        // check deployment mode
        int deploymentMode = getDeploymentMode(appMail.companyId)
        if (deploymentMode != 1) {
            log.error(NOT_PRODUCTION_MODE)
            return
        }

        appMail = buildMailObject(shellScript, appMail, serverInstance)
        task { mailSenderService.sendMail(appMail) }
    }

    /**
     * Build mail body
     * @param shellScript - object of AppShellScript
     * @param appMail - object of AppMail
     * @param serverInstance - object of AppServerInstance
     * @return - modified object of AppMail
     */
    private AppMail buildMailObject(AppShellScript shellScript, AppMail appMail, AppServerInstance serverInstance) {
        Map parameters = [
                serverInstance: serverInstance.name,
                script        : shellScript.script,
                message       : SUCCESS_MESSAGE,
                executedOn    : shellScript.lastExecutedOn,
                executionCount: shellScript.numberOfExecution
        ]
        appMail.evaluateMailBody(parameters)
        return appMail
    }

    /**
     * get deployment mode config value
     * @param companyId - id of company
     * @return - value of deployment mode config
     */
    private int getDeploymentMode(long companyId) {
        int deploymentMode = 1
        SysConfiguration sysConfiguration = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.APPLICATION_DEPLOYMENT_MODE, companyId)
        if (sysConfiguration) {
            deploymentMode = Integer.parseInt(sysConfiguration.value)
        }
        return deploymentMode
    }
}
