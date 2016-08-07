package com.athena.mis.application.actions.appbankbranch

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppBank
import com.athena.mis.application.entity.AppBankBranch
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.model.ListBankBranchActionServiceModel
import com.athena.mis.application.service.AppBankBranchService
import com.athena.mis.application.service.AppBankService
import com.athena.mis.application.service.ListBankBranchActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class CreateAppBankBranchActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String BANK_BRANCH = "bankBranch"
    private static final String BANK_CREATE_SUCCESS_MSG = "Bank branch has been successfully saved"
    private static final String DUPLICATED_PRINCIPLE_BRANCH = "Bank already has a principle branch"
    private static final String BANK_BRANCH_CODE_MUST_BE_UNIQUE = "Bank branch code must be unique"
    private static final String BANK_BRANCH_NAME_MUST_BE_UNIQUE = "Bank branch name must be unique"
    private static final String BANK_NOT_FOUND = "Bank can not be found or might have been deleted by someone"


    AppBankBranchService appBankBranchService
    AppBankService appBankService
    ListBankBranchActionServiceModelService listBankBranchActionServiceModelService

    /**
     * 1. create a bankBranch instance with parameters
     * 2. check uniqueness of BankBranch code and principle branch
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            //Check parameters
            if (!params.bankId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long bankId = Long.parseLong(params.bankId.toString())
            AppBank bank = appBankService.read(bankId)
            // check DocQuestion object existence
            if (bank == null) {
                return super.setError(params, BANK_NOT_FOUND)
            }
            //create a bankBranch instance
            AppBankBranch bankBranch = getBankBranch(params)
            // check uniqueness
            String msg = checkUniqueness(bankBranch)
            if (msg) {
                return super.setError(params, msg)
            }
            params.put(BANK_BRANCH, bankBranch)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Save BankBranch object in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppBankBranch bankBranch = (AppBankBranch) result.get(BANK_BRANCH)
            appBankBranchService.create(bankBranch)
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
     * 1. get newly created BankBranch object from model
     * 2. put success message
     * @param result -map from execute method
     * @return -a map containing all objects necessary for grid view
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppBankBranch bankBranch = (AppBankBranch) result.get(BANK_BRANCH)
            ListBankBranchActionServiceModel bankBranchModel = listBankBranchActionServiceModelService.read(bankBranch.id)
            result.put(BANK_BRANCH, bankBranchModel)
            return super.setSuccess(result, BANK_CREATE_SUCCESS_MSG)
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * received map must contain isError-true with corresponding error message
     * return a map same as received
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build object of BankBranch
     * @param params -serialized parameters from UI
     * @return -object of BankBranch
     */
    private AppBankBranch getBankBranch(Map params) {
        AppBankBranch bankBranch = new AppBankBranch(params)
        AppUser appUser = super.getAppUser()
        bankBranch.companyId = appUser.companyId
        bankBranch.createdOn = new Date()
        bankBranch.createdBy = appUser.id
        bankBranch.updatedOn = null
        bankBranch.updatedBy = 0L
        return bankBranch
    }

    /**
     * 1. ensure uniqueness of BankBranch code
     * 2. ensure one principle branch for each bank
     * @param bankBranch -object of BankBranch
     * @return -a string containing error message or null value depending on unique check
     */
    private String checkUniqueness(AppBankBranch bankBranch) {
        int count = appBankBranchService.countByNameIlikeAndDistrictIdAndBankId(bankBranch.name, bankBranch.districtId, bankBranch.bankId)
        if (count > 0) {
            return BANK_BRANCH_NAME_MUST_BE_UNIQUE
        }
        // check for duplicate BankBranch code
        count = appBankBranchService.countByCodeIlikeAndBankId(bankBranch.code, bankBranch.bankId)
        if (count > 0) {
            return BANK_BRANCH_CODE_MUST_BE_UNIQUE
        }

        // check for duplicate principle branch
        if (bankBranch.isPrincipleBranch) {
            AppBankBranch principleBranch = appBankBranchService.getPrincipleBranch(bankBranch.bankId, bankBranch.companyId)
            if (principleBranch) {
                return DUPLICATED_PRINCIPLE_BRANCH
            }
        }
        return null
    }
}
