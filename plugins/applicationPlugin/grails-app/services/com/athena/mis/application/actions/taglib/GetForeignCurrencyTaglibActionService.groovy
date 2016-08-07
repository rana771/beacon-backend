package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.service.CurrencyService
import org.apache.log4j.Logger

class GetForeignCurrencyTaglibActionService extends BaseService implements ActionServiceIntf {
    private static Logger log = Logger.getLogger(getClass())

    private static final String PROPERTY = "property"
    private static final String COUNTRY_ID = "country_id"

    CurrencyService currencyService

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

    /**
     * if country_id exists get currency by country id
     * else get currency of system bank
     * @param parameters - attributes of tag
     * @return - String property value
     */
    Map execute(Map result) {
        try {
            String property = (String) result.get(PROPERTY)
            long countryId
            Currency currency
            if (result.country_id) {
                countryId = Long.parseLong(result.get(COUNTRY_ID).toString())
                currency = currencyService.readByCountryId(countryId)
            } else {
                currency = currencyService.readForeignCurrency()
            }
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
