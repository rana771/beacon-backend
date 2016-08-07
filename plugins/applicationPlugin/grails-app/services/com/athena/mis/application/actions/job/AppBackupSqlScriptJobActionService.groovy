package com.athena.mis.application.actions.job

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.MailSenderService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.AppShellScript
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.AppShellScriptService
import org.apache.log4j.Logger

import static grails.async.Promises.task

/**
 *  Action Service for Backup SQL Scheduler
 *  For details go through Use-Case doc named 'AppBackupSqlScriptJobActionService'
 */
class AppBackupSqlScriptJobActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String OBJECT_NOT_FOUND = "Backup sql script not found"
    private static final String DB_INSTANCE_NOT_FOUND = "DB Instance not found"
    private static final String DOUBLE_QUOTE = '"'
    private static final String BR = '<br/>'
    private static final String OUT_OF = ' out of '
    private static final String SUCCESS_MSG = " query executed successfully."
    private static final String ERROR_MSG = "Could not execute query "
    private static
    final String ERR_MSG = "Error occurred in AppBackupSqlScriptJobActionService. Check log for details..."
    private static final String CONNECTION_TEST_MSG = "DB instance connection has to be tested before evaluate script."
    private static final String TRANSACTION_CODE = "AppBackupSqlScriptJobActionService"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Backup SQL mail not send due to absence of mail template"
    private static
    final String NOT_PRODUCTION_MODE = "Backup sql script mail is not sent because application is not in production mode"

    AppShellScriptService appShellScriptService
    AppDbInstanceService appDbInstanceService
    AppMailService appMailService
    DataAdapterFactoryService dataAdapterFactoryService
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
     * 1. Execute SQL
     * 2. Update Execution Date & Time
     * 3. Send mail
     * @param parameters - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map execute(Map parameters) {
        long companyId = (long) parameters.companyId
        try {
            AppShellScript appShellScript = appShellScriptService.findByNameAndCompanyId(AppShellScript.BACKUP_SQL, companyId)
            if (!appShellScript) {
                log.error(OBJECT_NOT_FOUND)
                return parameters
            }
            // get db instance object
            AppDbInstance dbInstance = appDbInstanceService.read(appShellScript.serverInstanceId)
            // check existence of db instance object
            if (!dbInstance) {
                log.error(DB_INSTANCE_NOT_FOUND)
                return parameters
            }
            // test connection
            String connMsg = testDbConnection(dbInstance)
            if (connMsg) {
                log.error(connMsg)
                return parameters
            }
            String message = executeScript(appShellScript, dbInstance)
            updateScript(appShellScript)
            // send mail
            sendMail(appShellScript, dbInstance, message)
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
     * Test Database connection
     * @param appDbInstance - object of AppDbInstance
     * @return - error message or null value depending on connection test
     */
    private String testDbConnection(AppDbInstance appDbInstance) {
        if (!appDbInstance.isTested) {
            return CONNECTION_TEST_MSG
        }
        return null
    }

    /**
     * Execute Sql Query
     * @param script - script query
     * @param dbInstance - object of AppDbInstance
     * @return - output message
     */
    private String executeScript(AppShellScript appShellScript, AppDbInstance dbInstance) {
        String script = appShellScript.script
        String output = EMPTY_SPACE
        int totalQuery = 0
        int successQuery = 0
        boolean isExecute = true
        String errMsg = EMPTY_SPACE
        for (String currentSqlQuery : script.split(SEMICOLON)) {
            if (isExecute) {
                DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(dbInstance)
                Map executeResult = dataAdapter.execute(currentSqlQuery)
                Boolean isError = (Boolean) executeResult.isError
                if (isError.booleanValue()) {
                    errMsg = executeResult.exception.toString() + BR + ERROR_MSG + DOUBLE_QUOTE + currentSqlQuery + DOUBLE_QUOTE + BR + executeResult.exception.toString() + BR
                    isExecute = false
                } else {
                    successQuery++
                }
            }
            totalQuery++
        }
        output = BR + successQuery.toString() + OUT_OF + totalQuery.toString() + SUCCESS_MSG + BR + errMsg
        return output
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
     * @param dbInstance - object of AppDbInstance
     * @param message - execution message
     */
    private void sendMail(AppShellScript shellScript, AppDbInstance dbInstance, String message) {
        AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, shellScript.companyId, true)
        if (!appMail) {
            log.error(MAIL_TEMPLATE_NOT_FOUND)
        }
        // check deployment mode
        int deploymentMode = getDeploymentMode(appMail.companyId)
        if (deploymentMode != 1) {
            log.error(NOT_PRODUCTION_MODE)
        }

        appMail = buildMailObject(shellScript, dbInstance, message, appMail)
        task { mailSenderService.sendMail(appMail) }
    }

    /**
     * Build mail body
     * @param shellScript - object of AppShellScript
     * @param dbInstance - object of AppDbInstance
     * @param message - execution message
     * @param appMail - object of AppMail
     * @return - modified object of AppMail
     */
    private AppMail buildMailObject(AppShellScript shellScript, AppDbInstance dbInstance, String message, AppMail appMail) {
        Map parameters = [
                script        : BR + shellScript.script,
                dbInstance    : dbInstance.name,
                message       : message,
                executionCount: shellScript.numberOfExecution,
                executedOn    : shellScript.lastExecutedOn
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
