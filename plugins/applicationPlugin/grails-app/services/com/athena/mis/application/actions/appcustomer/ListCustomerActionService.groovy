package com.athena.mis.application.actions.appcustomer

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppCustomer
import com.athena.mis.ActionServiceIntf
import org.apache.log4j.Logger

/**
 *  Class to get list of customer to show on grid
 *  For details go through Use-Case doc named 'ListCustomerActionService'
 */
class ListCustomerActionService extends BaseService implements ActionServiceIntf {

    protected Logger log = Logger.getLogger(getClass())

    /**
     *
     * @param params - serialized parameters from UI
     * @return - map
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * 1. pull all customer list or specific list depending on search pattern
     * @param result - map received from preExecute method
     * @return
     */
    public Map execute(Map params) {
        try {
            Map customerMap = getSearchResult(params, AppCustomer.class)
            params.put(LIST, customerMap.list)
            params.put(COUNT, customerMap.count)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     *
     * @param result - map received from execute method
     * @return - map
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     *
     * @param result - map received from post execute method
     * @return - map
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     *
     * @param result - map received from previous method
     * @return - map
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
