package com.athena.mis.application.actions.vendor

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppVendorActionServiceModel
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of Vendor for grid
 *  For details go through Use-Case doc named 'ListAppVendorActionService'
 */
class ListAppVendorActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    /**
     * No pre conditions required for searching Vendor domain
     * @param params - Request parameters
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * 1. initialize params for pagination of list
     * 2. pull all Vendor list from database (if no criteria)
     * 3. pull filtered result from database (if given criteria)
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            Map resultMap = super.getSearchResult(result, ListAppVendorActionServiceModel.class)
            result.put(LIST, resultMap.list)
            result.put(COUNT, resultMap.count)
            return result
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
     * @param obj - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}