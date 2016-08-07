package com.athena.mis.application.actions.dbinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.service.AppDbInstanceService
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger

import static org.apache.commons.lang.StringUtils.containsIgnoreCase
import static org.springframework.util.StringUtils.countOccurrencesOf

/**
 * list & show result for DPL sql console
 * for details please go through use case named 'ListResultForDplSqlConsoleActionService'
 */
class ListResultForDplSqlConsoleActionService extends BaseService implements ActionServiceIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static final String LST_RESULT = "lstResult";
    private static final String DB_INSTANCE_NOT_FOUND = "Db Instance not found.";
    private static final String LST_DML_SCRIPT = "lstDmlScript";
    private static final String LST_DDL_SCRIPT = "lstDdlScript";
    private static final String DB_INSTANCE = "dbInstance";
    private static final String NOT_TESTED = "dbInstance is not tested";
    private static
    final String CAN_NOT_PERFORM_DELETE_OPERATION = "Delete operation can not be performed for this database";

    AppDbInstanceService appDbInstanceService
    DataAdapterFactoryService dataAdapterFactoryService

    /**
     * check necessary parameters
     * split DML and SELECT script into different list
     * put empty list into map in case of error occurred
     * read AppDbInstance by id
     * check validation
     * @param parameters
     * @return Map
     */
    Map executePreCondition(Map parameters) {
        try {
            // check necessary parameters
            if (!parameters.dbInstanceId || !parameters.script) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            // put empty list into map in case of error occurred
            List<GroovyRowResult> lstResult = []
            parameters.put(LST_RESULT, lstResult)
            long dbInstanceId = Long.parseLong(parameters.dbInstanceId.toString())
            String script = parameters.script.toString().replaceAll("\n", EMPTY_SPACE);
            // split DML and SELECT script into different list
            Map scriptMap = getFragmentedScript(script)
            List<String> dmlScript = scriptMap.dmlScriptList
            AppDbInstance dbInstance = appDbInstanceService.read(dbInstanceId)
            // check validation
            String errMsg = checkValidation(dbInstance, dmlScript)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            parameters.put(DB_INSTANCE, dbInstance)
            parameters.put(LST_DML_SCRIPT, dmlScript)
            parameters.put(LST_DDL_SCRIPT, scriptMap.ddlScriptList)
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * execute DML script
     * execute select script
     * @param previousResult
     * @return Map
     */
    @Transactional
    Map execute(Map previousResult) {
        try {
            List<String> dmlScript = (List<String>) previousResult.get(LST_DML_SCRIPT)
            List<String> ddlScript = (List<String>) previousResult.get(LST_DDL_SCRIPT)
            AppDbInstance dbInstance = (AppDbInstance) previousResult.get(DB_INSTANCE)
            List<GroovyRowResult> lstResult = []
            // execute DML script
            boolean isError = executeScriptForDml(dmlScript, dbInstance, previousResult, lstResult)
            if (isError) return previousResult
            // execute select script
            Map result = executeScriptForDdl(ddlScript, dbInstance, previousResult, lstResult)
            String errMsg = (String) result.get(MESSAGE)
            lstResult = (List) result.get(LST_RESULT)
            if (errMsg) return previousResult
            previousResult.put(LST_RESULT, lstResult)
            previousResult.put(COUNT, lstResult.size())
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    Map buildSuccessResultForUI(Map executeResult) {
        return executeResult
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * execute DML script
     * execute transaction rollback if error occurred
     * @param dmlScript
     * @param dbInstance
     * @param result
     * @param lstResult
     * @return ( true / false ) depending on success
     */
    private boolean executeScriptForDml(List<String> dmlScript, AppDbInstance dbInstance, Map result, List<GroovyRowResult> lstResult) {
        Map executeResult
        if (dmlScript.size() > 0) {
            executeResult = executeDmlScript(dmlScript, dbInstance)
            Boolean isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                // put empty list into map
                result.put(LST_RESULT, lstResult)
                DataAdapterService adapter = dataAdapterFactoryService.createAdapter(dbInstance)
                String query = "ROLLBACK TRANSACTION;"
                // execute transaction rollback
                adapter.execute(query)
                super.setError(result, executeResult.exception.toString())
                return true
            }
//            int count = Integer.parseInt(executeResult.rowCount.toString())
            super.setSuccess(result, "Query executed successfully")
        }
        return false
    }

    /**
     *
     * @param ddlScript
     * @param dbInstance
     * @param result
     * @param lstResult
     * @return Map containing result and error message if occurred
     */
    private Map executeScriptForDdl(List<String> ddlScript, AppDbInstance dbInstance, Map result, List<GroovyRowResult> lstResult) {
        if (ddlScript.size() > 0) {
            Map executeResult = executeDdlScript(ddlScript, dbInstance)
            Boolean isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                // put empty list into map
                result.put(LST_RESULT, lstResult)
                super.setError(result, executeResult.exception.toString())
                return [message: executeResult.exception.toString(), lstResult: lstResult]
            }
            lstResult = (List) executeResult.lstResult
        }
        return [message: null, lstResult: lstResult]
    }

    /**
     * build script and wrap it by transactional block
     * execute DML by dataAdapterService
     * @param dmlScript
     * @param dbInstance
     * @return
     */
    private Map executeDmlScript(List<String> dmlScript, AppDbInstance dbInstance) {
        String scriptDml = EMPTY_SPACE
        dmlScript.each {
            scriptDml += it
        }
        DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(dbInstance)
        // wrap with transactional block
        scriptDml = dataAdapter.wrapWithTransactionBlock(scriptDml)
        Map result = dataAdapter.executeUpdate(scriptDml)
        return result
    }

    /**
     * execute select by dataAdapterService
     * @param ddlScript
     * @param dbInstance
     * @return
     */
    private Map executeDdlScript(List<String> ddlScript, AppDbInstance dbInstance) {
        String scriptDdl = ddlScript[ddlScript.size() - 1]
        DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(dbInstance)
        Map result = dataAdapter.executeSelect(scriptDdl, true)
        return result
    }

    /**
     * split DML and SELECT script into different list
     * @param script
     * @return Map
     */
    private Map getFragmentedScript(String script) {
        List<String> dmlScript = []
        List<String> ddlScript = []
        // put semicolon at the end if there isn't
        if (!SEMICOLON.equals(script.substring(script.length() - 1))) {
            script += SEMICOLON
        }
        // count script by semicolon
        int scriptCount = countOccurrencesOf(script, SEMICOLON)
        // split
        for (int i = 0; i < scriptCount; i++) {
            String subScript = script.substring(0, script.indexOf(SEMICOLON) + 1)
            if (containsIgnoreCase(subScript, "select")) {
                ddlScript << subScript
            } else {
                dmlScript << subScript
            }
            script = script.replace(subScript, EMPTY_SPACE)
        }
        Map result = [dmlScriptList: dmlScript, ddlScriptList: ddlScript]
        return result
    }

    /**
     * 1. check whether database exists or not
     * 2. check whether database is tested or not
     * 3. restrict delete operation if database is not deletable
     * @param dbInstance
     * @param dmlScript
     * @return - error message or null value depending on validation check
     */
    private String checkValidation(AppDbInstance dbInstance, List<String> dmlScript) {
        String errMsg = checkDbExistence(dbInstance)
        if (errMsg) return errMsg
        errMsg = checkTested(dbInstance)
        if (errMsg) return errMsg
        errMsg = checkValidScript(dbInstance.isDeletable, dmlScript)
        if (errMsg) return errMsg
        return null
    }

    /**
     * check whether database exists or not
     * @param dbInstance
     * @return - error message or null value depending on validation check
     */
    private String checkDbExistence(AppDbInstance dbInstance) {
        if (!dbInstance) {
            return DB_INSTANCE_NOT_FOUND
        }
        return null
    }

    /**
     * check whether database is tested or not
     * @param dbInstance
     * @return - error message or null value depending on validation check
     */
    private String checkTested(AppDbInstance dbInstance) {
        if (!dbInstance.isTested) {
            return NOT_TESTED
        }
        return null
    }

    /**
     * restrict delete operation if database is not deletable
     * @param isDeletable
     * @param dmlScript
     * @return - error message or null value depending on validation check
     */
    private String checkValidScript(boolean isDeletable, List<String> dmlScript) {
        boolean isRestrictDelete = false
        if (!isDeletable && dmlScript.size() > 0) {
            for (int i = 0; i < dmlScript.size(); i++) {
                if (containsIgnoreCase(dmlScript[i], "delete ")) {
                    isRestrictDelete = true
                    break
                }
            }
        }
        if (isRestrictDelete)
            return CAN_NOT_PERFORM_DELETE_OPERATION
        return null
    }
}
