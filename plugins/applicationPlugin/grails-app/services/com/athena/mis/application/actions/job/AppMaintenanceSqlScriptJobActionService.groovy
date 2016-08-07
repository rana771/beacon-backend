package com.athena.mis.application.actions.job

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.MailSenderService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.*
import org.apache.log4j.Logger

import static grails.async.Promises.task

/**
 *  Action Service for Maintenance SQL Scheduler
 *  For details go through Use-Case doc named 'AppMaintenanceSqlScriptJobActionService'
 */
class AppMaintenanceSqlScriptJobActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String OBJECT_NOT_FOUND = "Maintenance SQL script not found"
    private static final String DB_INSTANCE_NOT_FOUND = "DB Instance not found"
    private static final String DOUBLE_QUOTE = '"'
    private static final String BR = '<br/>'
    private static final String DOUBLE_BR = '<br/><br/>'
    private static final String OUT_OF = ' out of '
    private static final String SUCCESS_MSG = " query executed successfully."
    private static final String ERROR_MSG = "Could not execute query "
    private static
    final String ERR_MSG = "Error occurred in AppMaintenanceSqlScriptJobActionService. Check log for details..."
    private static final String CONNECTION_TEST_MSG = "DB instance connection has to be tested before evaluate script."
    private static final String TRANSACTION_CODE = "AppMaintenanceSqlScriptJobActionService"
    private static
    final String MAIL_TEMPLATE_NOT_FOUND = "Maintenance SQL mail not send due to absence of mail template"
    private static
    final String NOT_PRODUCTION_MODE = "Maintenance sql script mail is not sent because application is not in production mode"

    AppShellScriptService appShellScriptService
    AppDbInstanceService appDbInstanceService
    DbInstanceQueryService dbInstanceQueryService
    AppMailService appMailService
    DataAdapterFactoryService dataAdapterFactoryService
    MailSenderService mailSenderService
    AppConfigurationService appConfigurationService
    AppSystemEntityCacheService appSystemEntityCacheService

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
            AppShellScript appShellScript = appShellScriptService.findByNameAndCompanyId(AppShellScript.MAINTENANCE_SQL, companyId)
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
            // build script for mail
            String scriptForMail = '<b>DB Instance : </b>' + dbInstance.generatedName + DOUBLE_BR
            SystemEntity queryType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_QUERY_MAINTENANCE, appSystemEntityCacheService.SYS_ENTITY_TYPE_QUERY, companyId)
            // check if native DB has any other maintenance query
            List<DbInstanceQuery> lstQuery = dbInstanceQueryService.findAllByDbInstanceIdAndQueryTypeId(dbInstance.id, queryType.id)
            String script = appShellScript.script
            String sqlForMail = BR + appShellScript.script
            if (lstQuery.size() > 0) {
                for (int i = 0; i < lstQuery.size(); i++) {
                    script = script + SEMICOLON + lstQuery[i].sqlQuery
                    sqlForMail = sqlForMail + BR + lstQuery[i].sqlQuery
                }
            }
            scriptForMail = scriptForMail + '<b>SQL : </b>' + sqlForMail + DOUBLE_BR
            String message = executeScript(script, dbInstance)
            scriptForMail = scriptForMail + '<b>Message : </b>' + message + BR
            // update execution date & time
            updateScript(appShellScript)
            scriptForMail = scriptForMail + '<b>Execution Count : </b>' + appShellScript.numberOfExecution + BR
            scriptForMail = scriptForMail + '<b>Executed On : </b>' + appShellScript.lastExecutedOn + DOUBLE_BR
            // check if any other DB Instance has any maintenance query
            lstQuery = dbInstanceQueryService.findAllByQueryTypeIdAndDbInstanceIdNotEqualAndCompanyId(queryType.id, dbInstance.id, companyId)
            List<Long> lstDbInstanceIds = lstQuery.collect { it.dbInstanceId }
            lstDbInstanceIds.unique()
            if (lstDbInstanceIds.size() > 0) {
                for (int i = 0; i < lstDbInstanceIds.size(); i++) {
                    dbInstance = appDbInstanceService.read(lstDbInstanceIds[i])
                    if (!dbInstance) {
                        println DB_INSTANCE_NOT_FOUND
                        return super.setError(parameters, DB_INSTANCE_NOT_FOUND)
                    }
                    // test connection
                    connMsg = testDbConnection(dbInstance)
                    if (connMsg) {
                        log.error(connMsg)
                        return parameters
                    }
                    scriptForMail = scriptForMail + DOUBLE_BR + '<b>DB Instance : </b>' + dbInstance.generatedName + DOUBLE_BR
                    lstQuery = dbInstanceQueryService.findAllByDbInstanceIdAndQueryTypeId(dbInstance.id, queryType.id)
                    script = EMPTY_SPACE
                    sqlForMail = BR
                    if (lstQuery.size() > 0) {
                        for (int j = 0; j < lstQuery.size(); j++) {
                            if (j == (lstQuery.size() - 1)) {
                                script = script + lstQuery[j].sqlQuery
                                sqlForMail = sqlForMail + lstQuery[i].sqlQuery
                            } else {
                                script = script + lstQuery[j].sqlQuery + SEMICOLON
                                sqlForMail = sqlForMail + lstQuery[i].sqlQuery + BR
                            }
                        }
                        scriptForMail = scriptForMail + '<b>SQL : </b>' + sqlForMail + DOUBLE_BR
                        message = executeScript(script, dbInstance)
                        scriptForMail = scriptForMail + '<b>Message : </b>' + message + DOUBLE_BR
                    }
                }
            }
            // send mail
            sendMail(appShellScript, scriptForMail)
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
    private String executeScript(String script, AppDbInstance dbInstance) {
        String output = EMPTY_SPACE
        int totalQuery = 0
        int successQuery = 0
        boolean isExecute = true
        String errMsg = EMPTY_SPACE
        for (String currentSqlQuery : script.split(SEMICOLON)) {
            if (isExecute && currentSqlQuery != EMPTY_SPACE) {
                DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(dbInstance)
                Map executeResult = dataAdapter.execute(currentSqlQuery)
                Boolean isError = (Boolean) executeResult.isError
                if (isError.booleanValue()) {
                    errMsg = errMsg + BR + ERROR_MSG + DOUBLE_QUOTE + currentSqlQuery + DOUBLE_QUOTE + BR + executeResult.exception.toString() + BR
                    isExecute = false
                } else {
                    successQuery++
                }
            }
            if (currentSqlQuery != EMPTY_SPACE) {
                totalQuery++
            }
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
     * @param scriptForMail - script for mail
     */
    private void sendMail(AppShellScript shellScript, String scriptForMail) {
        AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, shellScript.companyId, true)
        if (!appMail) {
            println MAIL_TEMPLATE_NOT_FOUND
        }
        // check deployment mode
        int deploymentMode = getDeploymentMode(appMail.companyId)
        if (deploymentMode != 1) {
            log.error(NOT_PRODUCTION_MODE)
        }

        appMail = buildMailObject(scriptForMail, appMail)
        task { mailSenderService.sendMail(appMail) }
    }

    /**
     * Build mail body
     * @param scriptForMail - script for mail
     * @param appMail - object of AppMail
     * @return - modified object of AppMail
     */
    private AppMail buildMailObject(String scriptForMail, AppMail appMail) {
        Map parameters = [
                script: scriptForMail
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
