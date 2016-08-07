package com.athena.mis.application.actions.appbank

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppBank
import com.athena.mis.application.model.ListBankActionServiceModel
import com.athena.mis.application.service.AppBankService
import com.athena.mis.application.service.ListBankActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional


/**
 *  Update bank object and grid data
 *  For details go through Use-Case doc named 'ExhUpdateBankActionService'
 */
class UpdateAppBankActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String BANK = "bank"
    private static final String BANK_UPDATE_SUCCESS_MESSAGE = "Bank has been updated successfully"
    private static final String BANK_NAME_MUST_BE_UNIQUE = "Bank name must be unique"
    private static final String BANK_CODE_MUST_BE_UNIQUE = "Bank code must be unique"
    private static final String ALREADY_SYSTEM_BANK_EXISTS = "Already system bank exists"

    AppBankService appBankService
    ListBankActionServiceModelService listBankActionServiceModelService

    /**
     * 1. check input validation
     * 2. check uniqueness of name and code of Bank and system bank
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required params
            if ((!params.id) || (!params.version) || (!params.countryId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // check input validation
            long bankId = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())
            AppBank oldBank = appBankService.read(bankId)     // get bank object from cache utility
            if ((!oldBank) || (oldBank.version != version)) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            AppBank bank = buildBank(params, oldBank)
            // check uniqueness
            String msg = checkUniqueness(bank)
            if (msg) {
                return super.setError(params, msg)
            }
            params.put(BANK, bank)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. get bank object from map
     * 2.Update Bank object in DB*
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            // get bank object from map
            AppBank bank = (AppBank) result.get(BANK)
            // update Bank object in DB
            appBankService.update(bank)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no post condition, so return the same map as received
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Builds success result map to returned to the UI layer
     * @param result -returned from previous method must contain isError-true
     * @return result map wrapped within a grid entity
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppBank bank = (AppBank) result.get(BANK)
            ListBankActionServiceModel bankModel = listBankActionServiceModelService.read(bank.id)
            result.put(BANK, bankModel)
            return super.setSuccess(result, BANK_UPDATE_SUCCESS_MESSAGE)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Received map must contain isError-false with corresponding error msg
     * return a map same as received
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build Bank object for update
     * @param params -serialized params from UI
     * @param oldBank -old Bank object
     * @return -updated Bank object
     */
    private AppBank buildBank(Map params, AppBank oldBank) {
        AppBank newBank = new AppBank(params)
        oldBank.name = newBank.name
        oldBank.code = newBank.code
        oldBank.isSystemBank = newBank.isSystemBank
        oldBank.countryId = newBank.countryId
        oldBank.updatedOn = new Date()
        oldBank.updatedBy = super.getAppUser().id
        return oldBank
    }

    /**
     * 1. ensure uniqueness of bank name
     * 2. ensure uniqueness of bank code
     * 3. ensure one system bank for each company
     * @param bank -object of Bank
     * @return -a string containing error message or null value depending on unique check
     */
    private String checkUniqueness(AppBank bank) {
        // check for duplicate bank name
        if (appBankService.countByNameIlikeAndCountryIdAndCompanyIdAndIdNotEqual(bank.name, bank.countryId, bank.companyId, bank.id) > 0) {
            return BANK_NAME_MUST_BE_UNIQUE
        }
        // check for duplicate bank code
        if (appBankService.countByCodeIlikeAndCountryIdAndCompanyIdAndIdNotEqual(bank.code, bank.countryId, bank.companyId, bank.id) > 0) {
            return BANK_CODE_MUST_BE_UNIQUE
        }
        // ensure one system bank for each company
        if (bank.isSystemBank.booleanValue()) {
            if (appBankService.countByIsSystemBankAndCompanyIdAndIdNotEqual(true, bank.companyId, bank.id) > 0) {
                return ALREADY_SYSTEM_BANK_EXISTS
            }
        }
        return null
    }
}