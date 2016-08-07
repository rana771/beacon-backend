package com.athena.mis.application.actions.systementity

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of system entity for grid
 *  For details go through Use-Case doc named 'ListSystemEntityActionService'
 */
class ListSystemEntityActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SYSTEM_ENTITY_TYPE_NOT_FOUND = "System entity type not found"

    /**
     * No pre conditions required for searching project domains
     * @param params - Request parameters
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * 1. initialize params for pagination of list
     * 2. pull all project list from database (if no criteria)
     * 3. pull filtered result from database (if given criteria)
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            long systemEntityTypeId = Long.parseLong(result.systemEntityTypeId.toString())
            // checking is system entity type id or not
            if (systemEntityTypeId < 0) {
                return super.setError(result, SYSTEM_ENTITY_TYPE_NOT_FOUND)
            }
            // get map of system entity list and it's total count
            Closure additionalCondition = {
                eq('type', systemEntityTypeId)
            }
            Map resultMap = super.getSearchResult(result, SystemEntity.class, additionalCondition)
            result.put(LIST, resultMap.list)
            result.put(COUNT, resultMap.count)
            return result
        }  catch (Exception ex) {
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
}
