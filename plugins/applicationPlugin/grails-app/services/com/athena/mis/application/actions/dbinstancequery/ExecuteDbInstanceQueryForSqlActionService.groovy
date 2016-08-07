package com.athena.mis.application.actions.dbinstancequery

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.DbInstanceQueryService
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class ExecuteDbInstanceQueryForSqlActionService extends BaseService implements ActionServiceIntf {

    private static final String DB_INSTANCE = "dbInstance"
    private static final String DB_INSTANCE_NOT_FOUND = "Db Instance not found"
    private static final String SUCCESS_MSG = " query executed successfully"
    private static final String ERROR_MSG = "Could not execute query "
    private static final String DOUBLE_QUOTE = '"'
    private static final String START = '<p>'
    private static final String END = '</p>'
    private static final String OUT_OF = ' out of '
    private static final String NOT_TESTED = "DB instance is not tested"
    private static
    final String CAN_NOT_PERFORM_DELETE_OPERATION = "Delete operation can not be performed for this database"

    private Logger log = Logger.getLogger(getClass())

    DbInstanceQueryService dbInstanceQueryService
    AppDbInstanceService appDbInstanceService
    DataAdapterFactoryService dataAdapterFactoryService

    /**
     * 1. check required parameters
     * 2. get db instance object
     * 3. check existence of db instance object
     * 4. check connection
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.sqlQuery || !params.dbInstanceId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            String sqlQuery = params.sqlQuery
            long dbInstanceId = Long.parseLong(params.dbInstanceId.toString())
            // get db instance object
            AppDbInstance dbInstance = (AppDbInstance) appDbInstanceService.read(dbInstanceId)
            // check existence of db instance object
            if (!dbInstance) {
                return super.setError(params, DB_INSTANCE_NOT_FOUND)
            }
            // check connection
            if (!dbInstance.isTested) {
                return super.setError(params, NOT_TESTED)
            }
            String errMsg = checkDeleteRestriction(dbInstance.isDeletable, sqlQuery)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            if (!dbInstance.password) dbInstance.password = EMPTY_SPACE
            params.put(DB_INSTANCE, dbInstance)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive query, db instance object from executePreCondition method
     * 2. execute query
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppDbInstance dbInstance = (AppDbInstance) result.get(DB_INSTANCE)
            String sqlQuery = result.sqlQuery
            String executionMessage = EMPTY_SPACE
            int totalQuery = 0
            int successQuery = 0
            boolean isExecute = true
            for (String currentSqlQuery : sqlQuery.split(SEMICOLON)) {
                if (isExecute) {
                    DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(dbInstance)
                    Map executeResult = dataAdapter.execute(currentSqlQuery)
                    Boolean isError = (Boolean) executeResult.isError
                    if (isError.booleanValue()) {
                        executionMessage = executionMessage + START + ERROR_MSG + DOUBLE_QUOTE + currentSqlQuery + DOUBLE_QUOTE + END +
                                START + executeResult.exception.toString() + END
                        isExecute = false
                    } else {
                        successQuery++
                    }
                }
                totalQuery++
            }
            String message = START + successQuery.toString() + OUT_OF + totalQuery.toString() + SUCCESS_MSG + END +
                    executionMessage
            executionMessage = message
            return super.setSuccess(result, executionMessage)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
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

    private String checkDeleteRestriction(boolean isDeletable, String script) {
        if (!isDeletable && StringUtils.containsIgnoreCase(script, "delete")) {
            return CAN_NOT_PERFORM_DELETE_OPERATION
        }
        return null
    }
}
