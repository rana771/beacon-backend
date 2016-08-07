package com.athena.mis.application.actions.appbankbranch

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppBank
import com.athena.mis.application.service.AppBankService
import com.athena.mis.application.service.AppMyFavouriteService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class ShowAppBankBranchActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String BANK_ID = "bankId"
    private static final String COUNTRY_ID = "countryId"
    private static final String BANK_NAME = "bankName"

    AppBankService appBankService
    AppMyFavouriteService appMyFavouriteService

    /**
     * There is no pre condition, so return the same map as received
     */
    public Map executePreCondition(Map params) {
        String oIdStr = params.oId?params.oId:params.pId
        if (!oIdStr) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        return params
    }

    /**
     * @param result - returned from previous method
     * @return - a map containing all object necessary for UI
     */
    @Transactional
    public Map execute(Map result) {
        try {
            String oIdStr = result.oId?result.oId:result.pId
            long bankId = Long.parseLong(oIdStr)
            AppBank bank = appBankService.read(bankId)
            if (!bank) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            result.put(BANK_ID, bankId)
            result.put(COUNTRY_ID, bank.countryId)
            result.put(BANK_NAME, bank.name)
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
     * There is no build success result, so return the same map as received
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * There is no build failure result for UI, so return the same map as received
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}