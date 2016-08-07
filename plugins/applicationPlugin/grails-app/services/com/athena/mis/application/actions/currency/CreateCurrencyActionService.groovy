package com.athena.mis.application.actions.currency

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.service.CurrencyService
import com.athena.mis.ActionServiceIntf
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new currency object and show in grid
 *  For details go through Use-Case doc named 'CreateCurrencyActionService'
 */
class CreateCurrencyActionService extends BaseService implements ActionServiceIntf {

    CurrencyService currencyService

    private static final String CURRENCY_CREATE_SUCCESS_MSG = "Currency has been successfully saved"
    private static final String INVALID_INPUT_MSG = "Failed to create due to invalid input"
    private static final String CURRENCY_OBJECT = "currency"
    private static final String NAME_EXIST_MESSAGE = "Same currency name already exists"
    private static final String SYMBOL_EXIST_MESSAGE = "Same currency symbol already exists"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. Build currency object
     * 2. Ensure uniqueness of Currency name and symbol
     * @param params -serialized parameters from UI
     * @return -a map containing currency object necessary for execute
     */
    public Map executePreCondition(Map params) {
        try {
            //Check parameters
            if (!params.name) {
                return super.setError(params, INVALID_INPUT_MSG)
            }
            Currency currency = buildCurrencyObject(params)
            // check uniqueness
            String msg = checkUniqueness(currency)
            if (msg) {
                return super.setError(params, msg)
            }
            params.put(CURRENCY_OBJECT, currency)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Save currency object in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Currency currency = (Currency) result.get(CURRENCY_OBJECT)
            currencyService.create(currency)
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
     * @param result - map received from execute method
     * @return - map
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, CURRENCY_CREATE_SUCCESS_MSG)
    }

    /**
     * 1. set success message
     * @param result - map received from previous method
     * @return - map
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build Currency object
     * @param params -serialized parameters from UI
     * @return -new Currency object
     */
    private Currency buildCurrencyObject(Map params) {
        Currency currency = new Currency(params)
        AppUser appUser = super.appUser
        currency.companyId = appUser.companyId
        currency.version = 0
        currency.createdOn = new Date()
        currency.createdBy = appUser.id
        currency.updatedOn = null
        currency.updatedBy = 0L
        return currency
    }

    /**
     * 1. ensure uniqueness of Currency name
     * 2. ensure uniqueness of Currency symbol
     * @param currency -object of Currency
     * @return -a string containing error message or null value depending on unique check
     */
    private String checkUniqueness(Currency currency) {
        // check for duplicate Currency name
        int countName = currencyService.countByNameIlikeAndCompanyId(currency.name, currency.companyId)
        if (countName > 0) {
            return NAME_EXIST_MESSAGE
        }
        // check for duplicate Currency symbol
        int countCode = currencyService.countBySymbolIlikeAndCompanyId(currency.symbol, currency.companyId)
        if (countCode > 0) {
            return SYMBOL_EXIST_MESSAGE
        }
        return null
    }
}


