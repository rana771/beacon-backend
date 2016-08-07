package com.athena.mis.application.actions.currency

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.service.CurrencyService
import com.athena.mis.ActionServiceIntf
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update new currency object and show in grid
 *  For details go through Use-Case doc named 'UpdateCurrencyActionService'
 */
class UpdateCurrencyActionService extends BaseService implements ActionServiceIntf {

    private static final String CURRENCY_UPDATE_SUCCESS_MESSAGE = "Currency has been updated successfully"
    private static final String CURRENCY_OBJ = "currency"
    private static final String CURRENCY_NOT_EXIST = "Currency does not exist"
    private static final String OBJ_CHANGED_MSG = "Selected currency has been changed by other user"
    private static final String NAME_EXIST_MESSAGE = "Same currency name already exists"
    private static final String SYMBOL_EXIST_MESSAGE = "Same currency symbol already exists"

    private Logger log = Logger.getLogger(getClass())

    CurrencyService currencyService

    /**
     * 1. check input validation
     * 2. ensure uniqueness of Currency name and symbol
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing currency object necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Map executePreCondition(Map params) {
        try {
            // check input validation
            LinkedHashMap checkValidation = checkInputValidation(params)
            Boolean isError = checkValidation.get(IS_ERROR)
            if (isError.booleanValue()) {
                String message = checkValidation.get(MESSAGE)
                return super.setError(params, message)
            }
            Currency currency = (Currency) checkValidation.get(CURRENCY_OBJ)
            // check uniqueness
            String msg = checkUniqueness(currency)
            if (msg) {
                return super.setError(params, msg)
            }
            params.put(CURRENCY_OBJ, currency)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }
/**
 * Update customer object in DB
 * @param result -customerObject from executePreCondition method
 * @return -a map containing customer object
 */
    @Transactional
    public Map execute(Map result) {
        try {
            Currency currency = (Currency) result.get(CURRENCY_OBJ)
            currencyService.update(currency)
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
        return super.setSuccess(result, CURRENCY_UPDATE_SUCCESS_MESSAGE)
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
     * 1. check required parameters
     * 2. get Currency object by id from cache utility
     * 3. check existence of Currency object
     * 4. build Currency object for update
     * @param parameterMap -serialized parameters from UI
     * @return -a map containing Currency object, isError (true/false) and relevant message in case of error
     */
    private LinkedHashMap checkInputValidation(Map parameterMap) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(IS_ERROR, Boolean.TRUE)
        // check required parameters
        if (!parameterMap.id || !parameterMap.version) {
            result.put(MESSAGE, ERROR_FOR_INVALID_INPUT)
            return result
        }
        long currencyId = Long.parseLong(parameterMap.id.toString())
        long version = Long.parseLong(parameterMap.version.toString())
        // get Currency object by id from cache utility
        Currency oldCurrency = currencyService.read(currencyId)
        if (!oldCurrency) { //check existence of object
            result.put(MESSAGE, CURRENCY_NOT_EXIST)
            return result
        }
        //build currency object
        Currency newCurrency = (Currency) buildCurrencyForUpdate(oldCurrency, parameterMap)
        if ((!oldCurrency) || (oldCurrency.version != version)) {
            result.put(MESSAGE, OBJ_CHANGED_MSG)
            return result
        }
        result.put(CURRENCY_OBJ, newCurrency)
        result.put(IS_ERROR, Boolean.FALSE)
        return result
    }

    /**
     * Build Currency object
     * @param params -serialized parameters from UI
     * @return -new Currency object
     */
    private Currency buildCurrencyForUpdate(Currency oldCurrency, Map parameterMap) {
        Currency newCurrency = new Currency(parameterMap)
        oldCurrency.name = newCurrency.name
        oldCurrency.symbol = newCurrency.symbol
        oldCurrency.otherCode = newCurrency.otherCode
        oldCurrency.updatedOn = new Date()
        oldCurrency.updatedBy = super.appUser.id
        return oldCurrency
    }

    /**
     * 1. ensure uniqueness of Currency name
     * 2. ensure uniqueness of Currency symbol
     * @param currency -object of Currency
     * @return - a string containing error message or null value depending on unique check
     */
    private String checkUniqueness(Currency currency) {
        // check for duplicate Currency name
        int countName = currencyService.countByNameIlikeAndCompanyIdAndIdNotEqual(currency.name,currency.companyId, currency.id)
        if (countName > 0) {
            return NAME_EXIST_MESSAGE
        }
        // check for duplicate Currency symbol
        int countCode = currencyService.countBySymbolIlikeAndCompanyIdAndIdNotEqual(currency.symbol,currency.companyId, currency.id)
        if (countCode > 0) {
            return SYMBOL_EXIST_MESSAGE
        }
        return null
    }
}

