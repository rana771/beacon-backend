package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.service.CurrencyService
import org.apache.log4j.Logger

class GetLocalCurrencyTagLibActionService extends BaseService implements ActionServiceIntf{

    CurrencyService currencyService

    private static final String PROPERTY = 'property'
    private Logger log = Logger.getLogger(getClass())

    /**
     * Check required parameter
     * @param params - a map from UI
     * @return - a map consisting isError flag value
     */
    public Map executePreCondition(Map params) {
        String prop = params.get(PROPERTY)
        if (!prop) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
       return params
    }

    /** Get currency object of native country
     *  pull the value of given property
     * @param result -  a map of given attributes
     * @return - value of given property
     */
    public Map execute(Map result) {
        try {
            String property = result.get(PROPERTY)
            Currency currency = currencyService.readLocalCurrency()
            String html = currency.properties.get(property)
            result.html = html
            return result
        } catch (Exception e) {
            log.error(e.message)
            throw new RuntimeException(e)
        }
    }


    /**
     * Do nothing in post condition
     * @param result - A map returned by execute method
     * @return - returned the received map
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * do nothing for build success operation
     * @param result - A map returned by post condition method.
     * @return - returned the same received map containing isError = false
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Do nothing here
     * @param result - map returned from previous any of method
     * @return - a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

}
