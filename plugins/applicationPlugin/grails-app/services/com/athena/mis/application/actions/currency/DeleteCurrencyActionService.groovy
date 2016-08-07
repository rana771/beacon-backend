package com.athena.mis.application.actions.currency

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.service.CurrencyService
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete currency object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteCurrencyActionService'
 */
class DeleteCurrencyActionService extends BaseService implements ActionServiceIntf {

    CurrencyService currencyService

    private static String CURRENCY_DELETE_SUCCESS_MSG = "Currency has been successfully deleted"
    private static String CURRENCY = "currency"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check association
     * @param parameters -serialize parameters from UI
     * @return - a map containing currency object & association message (if any)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            int currencyId = Integer.parseInt(params.id)
            Currency currency = currencyService.read(currencyId)
            String associationMessage = isAssociated(currency)
            if (associationMessage) {
                return super.setError(params, associationMessage)
            }
            params.put(CURRENCY, currency)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Delete currency object from DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - object from pre execute method
     * @return - map
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Currency currency = (Currency) result.get(CURRENCY)
            currencyService.delete(currency)
            return result
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
     * 1. set success message
     * @param result - map received from post method
     * @return - map
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, CURRENCY_DELETE_SUCCESS_MSG)
    }

    /**
     *
     * @param result - map received from previous method
     * @return - map
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Checking all association with currency table
     * @param currency - currency object
     * @return - int value of currency number
     */
    private String isAssociated(Currency currency) {
        Integer currencyId = currency.id
        String currencyName = currency.name
        Integer count = 0

        // has country
        count = countCountry(currencyId)
        if (count.intValue() > 0) {
            return getMessageOfAssociation(currencyName, count, DOMAIN_COUNTRY)
        }
        //has company
        count = countCompany(currencyId)
        if (count.intValue() > 0) {
            return getMessageOfAssociation(currencyName, count, DOMAIN_COMPANY)
        }

        if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
            // has currency conversion
            count = countCurrencyConversion(currencyId)
            if (count.intValue() > 0) {
                return getMessageOfAssociation(currencyName, count, DOMAIN_CURRENCY_CONVERSION)
            }
            // has task
            count = countTask(currencyId)
            if (count.intValue() > 0) {
                return getMessageOfAssociation(currencyName, count, DOMAIN_TASK)
            }
            // has task trace
            count = countTaskTrace(currencyId)
            if (count.intValue() > 0) {
                return getMessageOfAssociation(currencyName, count, DOMAIN_TASK_TRACE)
            }
            // has agent
            count = countAgent(currencyId)
            if (count.intValue() > 0) {
                return getMessageOfAssociation(currencyName, count, DOMAIN_AGENT)
            }
            // has agent currency posting
            count = countAgentCurrencyPosting(currencyId)
            if (count.intValue() > 0) {
                return getMessageOfAssociation(currencyName, count, DOMAIN_AGENT_CURRENCY_POSTING)
            }
        }

        if (PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)) {
            //has rms task
            count = countRmsTask(currencyId)
            if (count.intValue() > 0) {
                return getMessageOfAssociation(currencyName, count, DOMAIN_TASK)
            }
        }

        if (PluginConnector.isPluginInstalled(SarbPluginConnector.PLUGIN_NAME)) {
            //has sarb currency conversion
            count = countSarbCurrencyConversion(currencyId)
            if (count.intValue() > 0) {
                return getMessageOfAssociation(currencyName, count, DOMAIN_SARB_CURRENCY_CONVERSION)
            }
        }
        return null
    }

    /**
     * Get total currency number used in currency-conversion
     * @param currencyId - currency id
     * @return - int value of currency number
     */
    private int countCurrencyConversion(Integer currencyId) {
        String queryStr = """
            SELECT COUNT(id) as count
            FROM exh_currency_conversion
            WHERE from_currency = ${currencyId} OR to_currency = ${currencyId}
        """
        List currencyConversionCount = executeSelectSql(queryStr)
        int count = currencyConversionCount[0].count
        return count
    }

    /**
     * Get total currency number used in task
     * @param currencyId - currency id
     * @return - int value of currency number
     */
    private int countTask(Integer currencyId) {
        String queryStr = """
            SELECT COUNT(id) as count
            FROM exh_task
            WHERE from_currency_id = ${currencyId} OR to_currency_id = ${currencyId}
        """
        List taskCount = executeSelectSql(queryStr)
        int count = taskCount[0].count
        return count
    }

    /**
     * Get total currency number used in task trace
     * @param currencyId
     * @return - int value of currency number
     */
    private int countTaskTrace(Integer currencyId) {
        String queryStr = """
            SELECT COUNT(id) as count
            FROM exh_task_trace
            WHERE from_currency_id = ${currencyId} OR to_currency_id = ${currencyId}
        """
        List taskTraceCount = executeSelectSql(queryStr)
        int count = taskTraceCount[0].count
        return count
    }

    /**
     * Get total currency number used in country
     * @param currencyId - currency id
     * @return - int value of currency number
     */
    private int countCountry(Integer currencyId) {
        String queryStr = """
            SELECT COUNT(id) as count
            FROM app_country
            WHERE currency_id = ${currencyId}
        """
        List currencyCount = executeSelectSql(queryStr)
        int count = currencyCount[0].count
        return count
    }

    /**
     * Get total currency number used in company
     * @param currencyId
     * @return - int value of currency number
     */
    private int countCompany(Integer currencyId) {
        String queryStr = """
            SELECT COUNT(id) as count
            From company
            WHERE currency_id = ${currencyId}
        """
        List companyCount = executeSelectSql(queryStr)
        int count = companyCount[0].count
        return count
    }

    /**
     * Get total currency number used in agent
     * @param currencyId
     * @return - int value of currency number
     */
    private int countAgent(Integer currencyId) {
        String queryStr = """
            SELECT COUNT(id) as count
            FROM exh_agent
            WHERE currency_id = ${currencyId}
        """
        List agentCount = executeSelectSql(queryStr)
        int count = agentCount[0].count
        return count
    }

    /**
     * Get total currency number used in agent currency posting
     * @param currencyId
     * @return - int value of currency number
     */
    private int countAgentCurrencyPosting(Integer currencyId) {
        String queryStr = """
            SELECT COUNT(id) as count
            FROM exh_agent_currency_posting
            WHERE currency_id = ${currencyId}
        """
        List agentCurPostingCount = executeSelectSql(queryStr)
        int count = agentCurPostingCount[0].count
        return count
    }

    /**
     * Get total currency number used in rms task
     * @param currencyId
     * @return - int value of currency number
     */
    private int countRmsTask(Integer currencyId) {
        String queryStr = """
            SELECT COUNT(id) as count
            FROM rms_task
            WHERE local_currency_id = ${currencyId} OR currency_id = ${currencyId}
        """
        List rmsTaskCount = executeSelectSql(queryStr)
        int count = rmsTaskCount[0].count
        return count
    }

    /**
     * Get total currency number used in sarb currency conversion
     * @param currencyId
     * @return - int value of currency number
     */
    private int countSarbCurrencyConversion(Integer currencyId) {
        String queryStr = """
            SELECT COUNT(id) as count
            FROM sarb_currency_conversion
            WHERE foreign_currency_id = ${currencyId}
        """
        List currencyConversionCount = executeSelectSql(queryStr)
        int count = currencyConversionCount[0].count
        return count
    }
}


