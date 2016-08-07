package com.athena.mis.application.actions.dbinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.service.AppDbInstanceService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of table for db instance
 *  For details go through Use-Case doc named 'ListTableForDbInstanceActionService'
 */
class ListTableForDbInstanceActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DB_INSTANCE = "dbInstance"
    private static final String DB_INSTANCE_NOT_FOUND = "Db Instance not found"
    private static final String NOT_TESTED = "DB instance is not tested"

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
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long dbInstanceId = Long.parseLong(params.id.toString())
            // get db instance object
            AppDbInstance dbInstance = appDbInstanceService.read(dbInstanceId)
            // check existence of db instance object
            if (!dbInstance) {
                return super.setError(params, DB_INSTANCE_NOT_FOUND)
            }
            // check connection
            if (!dbInstance.isTested) {
                return super.setError(params, NOT_TESTED)
            }
            params.put(DB_INSTANCE, dbInstance)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * get list of table
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map execute(Map result) {
        try {
            AppDbInstance dbInstance = (AppDbInstance) result.get(DB_INSTANCE)
            DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(dbInstance)
            List<String> lstTables = getTablesWithCount(dataAdapter)
            result.put(LIST, lstTables)
            result.put(COUNT, lstTables.size())
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private List<String> getTablesWithCount(DataAdapterService dataAdapter) {
        List<String> lstTables = []
        Map mapLstTable = dataAdapter.listTable(null)
        Boolean isError = Boolean.parseBoolean(mapLstTable.get(IS_ERROR).toString())
        if (isError.booleanValue()) {
            return lstTables
        }
        lstTables = mapLstTable.lstResult
        return lstTables
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
     * @param obj - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
