package com.athena.mis.application.actions.appbank

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppBank
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.model.ListBankActionServiceModel
import com.athena.mis.application.service.AppBankService
import com.athena.mis.application.service.ListBankActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new bank object and show in grid
 *  For details go through Use-Case doc named 'CreateBankActionService'
 */
class CreateAppBankActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String BANK = "bank"
    private static final String BANK_CREATE_SUCCESS_MSG = "Bank has been successfully saved"
    private static final String BANK_NAME_MUST_BE_UNIQUE = "Bank name must be unique"
    private static final String BANK_CODE_MUST_BE_UNIQUE = "Bank code must be unique"
    private static final String SYSTEM_BANK_ALREADY_EXISTS = "System bank already exists"

    AppBankService appBankService
    ListBankActionServiceModelService listBankActionServiceModelService

    /**
     * 1. Get params from UI and build bank object
     * 2. ensure uniqueness of name and code of Bank and system bank
     * @param params -serialized parameters from UI
     * @return -a map containing all object necessary for execute
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required params
            if (!params.countryId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // build bank object
            AppBank bank = getBank(params)
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
     * 2. Save bank object in DB
     * This method is in transactional boundary and will roll back in case of any exception
     * @param obj -a Bank obj returned from controller
     * @return -a bank object necessary for buildSuccessResultForUI
     */
    @Transactional
    public Map execute(Map result) {
        try {
            // get bank object from map
            AppBank bank = (AppBank) result.get(BANK)
            appBankService.create(bank)      // save new Bank object in DB
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no for post operation, so return the same map as received
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. get newly created bank object from model
     * 2. put success message
     * @param result -map from execute method
     * @return -a map containing all objects necessary for show
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppBank bank = (AppBank) result.get(BANK)
            ListBankActionServiceModel bankModel = listBankActionServiceModelService.read(bank.id)
            result.put(BANK, bankModel)
            return super.setSuccess(result, BANK_CREATE_SUCCESS_MSG)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Received map must contain isError- false with corresponding error message
     * @return -a map same as received
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build Bank object
     * @param params -serialized parameters from UI
     * @return -new Bank object
     */
    private AppBank getBank(Map params) {
        AppUser appUser = super.getAppUser()
        AppBank bank = new AppBank(params)
        bank.companyId = appUser.companyId
        bank.createdOn = new Date()
        bank.createdBy = appUser.id
        bank.updatedOn = null
        bank.updatedBy = 0L
        return bank
    }

    /**
     * 1. ensure uniqueness of bank name
     * 2. ensure uniqueness of bank code
     * 3. ensure one system bank for each company
     * @param bank -object of Bank
     * @return -a string containing error message or null value depending on unique check
     */
    private String checkUniqueness(AppBank bank) {
        long companyId = super.getCompanyId()
        // check for duplicate bank name
        if (appBankService.countByNameIlikeAndCountryIdAndCompanyId(bank.name, bank.countryId, companyId) > 0) {
            return BANK_NAME_MUST_BE_UNIQUE
        }
        // check for duplicate bank code
        if (appBankService.countByCodeIlikeAndCountryIdAndCompanyId(bank.code, bank.countryId, companyId) > 0) {
            return BANK_CODE_MUST_BE_UNIQUE
        }
        // ensure one system bank for each company
        if (bank.isSystemBank.booleanValue()) {
            if (appBankService.countByIsSystemBankAndCompanyId(true, bank.companyId) > 0) {
                return SYSTEM_BANK_ALREADY_EXISTS
            }
        }
        return null
    }
}
