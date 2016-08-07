package com.athena.mis.application.actions.currency

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Currency
import com.athena.mis.ActionServiceIntf
import org.apache.log4j.Logger

/**
 *  Show list of currency for grid
 *  For details go through Use-Case doc named 'ListCurrencyActionService'
 */
class ListCurrencyActionService extends BaseService implements ActionServiceIntf {

    protected Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre operation
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * 1. pull all currency list or specific list depending on search pattern
     * @param result - map received from preExecute method
     * @return
     */
    public Map execute(Map params) {
        try {
            Map currencyMap = getSearchResult(params, Currency.class)
            params.put(LIST, currencyMap.list)
            params.put(COUNT, currencyMap.count)
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
