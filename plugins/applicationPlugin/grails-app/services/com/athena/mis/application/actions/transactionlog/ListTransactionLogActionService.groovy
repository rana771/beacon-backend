package com.athena.mis.application.actions.transactionlog

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.TransactionLog
import com.athena.mis.application.service.SystemEntityService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of all/filtered docDataTransferLog in grid
 *  For details go through Use-Case doc named 'ListDocDataTransferLogActionService'
 */
class ListTransactionLogActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    SystemEntityService systemEntityService

    /**
     * No pre conditions required for searching DocAnswer domains
     *
     * @param params - Request parameters
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * 1. initialize params for pagination of list
     *
     * 2. pull all DocAnswer list from database (if no criteria)
     *
     * 3. pull filtered result from database (if given criteria)
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            long entityTypeId = Long.parseLong(result.entityTypeId.toString())
            long entityId = Long.parseLong(result.entityId.toString())

            Closure additionalCondition = {
                eq('entityTypeId', entityTypeId)
                eq('entityId', entityId)
            }

            if (result.tableName) {
                additionalCondition = {
                    eq('entityTypeId', entityTypeId)
                    eq('entityId', entityId)
                    ilike('tableName', result.tableName)
                }
            }

            Map resultMap = super.getSearchResult(result, TransactionLog.class, additionalCondition)

            result.put(LIST, resultMap.list)
            result.put(COUNT, resultMap.count)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
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
     * Since there is no success message return the same map
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
