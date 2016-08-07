package com.athena.mis.application.actions.appbankbranch

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppBankBranch
import com.athena.mis.application.model.ListBankBranchActionServiceModel
import com.athena.mis.application.service.AppBankBranchService
import com.athena.mis.application.service.ListBankBranchActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class UpdateAppBankBranchActionService extends BaseService implements ActionServiceIntf {

    private final Logger log = Logger.getLogger(getClass())

    // constants
    private static final String BANK_BRANCH = "bankBranch"
    private static String BANK_UPDATE_SUCCESS_MESSAGE = "Bank branch has been updated successfully"
    private static final String DUPLICATED_PRINCIPLE_BRANCH = "Bank already has a principle branch"
    private static final String BANK_BRANCH_CODE_MUST_BE_UNIQUE = "Bank branch code must be unique"
    private static final String BANK_BRANCH_NAME_MUST_BE_UNIQUE = "Bank branch name must be unique"

    AppBankBranchService appBankBranchService
    ListBankBranchActionServiceModelService listBankBranchActionServiceModelService

    /**
     * Build bankBranch obj and check uniqueness
     * @param parameters - serialized parameters from UI
     * @return - a map containing all object necessary for execute
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map parameters) {
        try {
            if ((!parameters.id) || (!parameters.version)) {
                // check required parameters
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            long bankBranchId = Long.parseLong(parameters.id.toString())
            AppBankBranch oldBankBranch = (AppBankBranch) appBankBranchService.read(bankBranchId)
            long version = Long.parseLong(parameters.version.toString())
            // get bank object from cache utility
            if ((!oldBankBranch) || (version != oldBankBranch.version)) {
                // check whether selected bank object exists or not
                return super.setError(parameters, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }

            AppBankBranch bankBranch = buildBankBranch(parameters, oldBankBranch)
            String errMsg = checkUniqueness(bankBranch)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            if (bankBranch.isPrincipleBranch) {
                AppBankBranch principleBranch = appBankBranchService.getPrincipleBranch(bankBranch.bankId, bankBranch.companyId)
                if (principleBranch && principleBranch.id != oldBankBranch.id) {
                    return super.setError(parameters, DUPLICATED_PRINCIPLE_BRANCH)
                }
            }
            parameters.put(BANK_BRANCH, bankBranch)
            return parameters
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Update bankBranch object
     * This block is in transactional block and will rollBack in case of any exception
     * @param result - returned from previous method
     * @return - a map containing all object necessary for buildSuccess
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppBankBranch bankBranch = (AppBankBranch) result.get(BANK_BRANCH)
            // update BankBranch object in DB
            appBankBranchService.update(bankBranch)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no post condition, so return the same map as received
     */
    public Map executePostCondition(Map params) {
        return params
    }

    /**
     * 1. get bankBranch object from map
     * 2. read new bankBranch object from model
     * 3. put success message
     * @param result - returned from previous method
     * @return - a map containing all object to show on UI
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppBankBranch bankBranch = (AppBankBranch) result.get(BANK_BRANCH)
            ListBankBranchActionServiceModel bankBranchModel = listBankBranchActionServiceModelService.read(bankBranch.id)
            result.put(BANK_BRANCH, bankBranchModel)
            return super.setSuccess(result, BANK_UPDATE_SUCCESS_MESSAGE)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1.check uniqueness for branch code
     * 2.check uniqueness for branch name
     * @param bankBranch
     * @return error message or nothing
     */
    private String checkUniqueness(AppBankBranch bankBranch) {
        //bankBranch code must be unique with respect to bankId and districtId
        int count = appBankBranchService.countByCodeIlikeAndDistrictIdAndBankIdAndIdNotEqual(bankBranch.code, bankBranch.districtId, bankBranch.bankId, bankBranch.id)
        if (count > 0) {
            return BANK_BRANCH_CODE_MUST_BE_UNIQUE
        }
        //bankBranch name must be unique with respect to bankId and districtId
        count = appBankBranchService.countByNameIlikeAndDistrictIdAndBankIdAndIdNotEqual(bankBranch.name, bankBranch.districtId, bankBranch.bankId, bankBranch.id)
        if (count > 0) {
            return BANK_BRANCH_NAME_MUST_BE_UNIQUE
        }
        return null
    }
    /**
     * received map must contain isError-false with corresponding error message
     * return the same map as received
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build bankBranchObj for update
     * @param parameterMap - params from UI
     * @param oldBankBranch - existing obj
     * @return - bankBranch obj
     */
    private AppBankBranch buildBankBranch(Map parameterMap, AppBankBranch oldBankBranch) {
        AppBankBranch newBankBranch = new AppBankBranch(parameterMap)
        oldBankBranch.name = newBankBranch.name
        oldBankBranch.code = newBankBranch.code
        oldBankBranch.address = newBankBranch.address
        oldBankBranch.isGlobal = newBankBranch.isGlobal
        oldBankBranch.districtId = Long.parseLong(parameterMap.districtId.toString())
        oldBankBranch.bankId = Long.parseLong(parameterMap.bankId.toString())
        oldBankBranch.isSmeServiceCenter = newBankBranch.isSmeServiceCenter
        oldBankBranch.isPrincipleBranch = newBankBranch.isPrincipleBranch
        oldBankBranch.routingNo = newBankBranch.routingNo
        oldBankBranch.updatedOn = new Date()
        oldBankBranch.updatedBy = super.getAppUser().id
        return oldBankBranch
    }
}
