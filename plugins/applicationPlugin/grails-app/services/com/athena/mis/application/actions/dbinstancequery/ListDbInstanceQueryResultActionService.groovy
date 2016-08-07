package com.athena.mis.application.actions.dbinstancequery

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.DbInstanceQuery
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.DbInstanceQueryService
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Get list of sql query result of DocOfflineDataFeedQuery object
 *  For details go through Use-Case doc named 'ListResultForDocOfflineDataFeedQueryActionService'
 */
class ListDbInstanceQueryResultActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String LST_RESULT = "lstResult";
    private static final String DB_INSTANCE_NOT_FOUND = "Db Instance not found.";
    private static final String DB_INSTANCE_QUERY = "DB Instance Query not found.";

    DbInstanceQueryService dbInstanceQueryService
    AppDbInstanceService appDbInstanceService
    DataAdapterFactoryService dataAdapterFactoryService

    /**
     * 1. check required inputs
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        // check required parameter
        if (!params.dbInstanceQueryId) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        return params
    }

    /**
     * Get list of result for selected query
     * 1. read DocOfflineDataFeedQuery from service
     * 2. check DocOfflineDataFeedQuery object existence
     * 3. check DocOfflineDataFeed object existence
     * 4. check DocDbInstance object existence
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            List<GroovyRowResult> lstResult = []
            long dbInstanceQueryId = Long.parseLong(result.dbInstanceQueryId.toString())
            DbInstanceQuery dbInstanceQuery = dbInstanceQueryService.read(dbInstanceQueryId)
            //check DocOfflineDataFeedQuery object existence
            if (!dbInstanceQuery) {
                result.put(LST_RESULT, lstResult)
                return super.setError(result, DB_INSTANCE_QUERY)
            }
            //check DocDbInstance object existence
            AppDbInstance appDbInstance = appDbInstanceService.read(dbInstanceQuery.dbInstanceId)
            if (!appDbInstance) {
                result.put(LST_RESULT, lstResult)
                return super.setError(result, DB_INSTANCE_NOT_FOUND)
            }
            DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(appDbInstance)
            Map executeResult = dataAdapter.executeSelect(dbInstanceQuery.sqlQuery, true)
            Boolean isError = (Boolean) executeResult.isError
            if (isError.booleanValue()) {
                result.put(LST_RESULT, lstResult)
                return super.setError(result, executeResult.exception.toString())
            }
            lstResult = (List) executeResult.lstResult
            result.put(LST_RESULT, lstResult)
            result.put(COUNT, lstResult.size())
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     *
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * For List Result there is no success message
     * since the input-parameter already have "isError:false", just return the same map
     *
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param obj - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
