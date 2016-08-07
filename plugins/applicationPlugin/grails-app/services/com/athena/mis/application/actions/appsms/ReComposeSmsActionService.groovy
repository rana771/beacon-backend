package com.athena.mis.application.actions.appsms

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppSms
import com.athena.mis.application.service.AppSmsService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new sms object and show in grid
 *  For details go through Use-Case doc named 'ReComposeSmsActionService'
 */
class ReComposeSmsActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SMS_OBJ = "sms"
    private static final String SUCCESS_MESSAGE = "SMS has been re-composed successfully"

    AppSmsService appSmsService

    /**
     * 1. check input validation
     * 2. build sms object
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check input validation
            String errMsg = checkValidation(params)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            // build sms object for create
            AppSms sms = buildSmsObject(params)
            params.put(SMS_OBJ, sms)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive sms object from executePreCondition method
     * 2. create new sms object
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppSms sms = (AppSms) result.get(SMS_OBJ)
            // save new sms object in DB
            appSmsService.create(sms)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. check required parameters
     * 2. check sms object existence
     * @param params - serialized parameters from UI
     * @return - a string containing null value or error message depending on validation check
     */
    private String checkValidation(Map params) {
        String errMsg
        // check required parameters
        if (!params.id) {
            return ERROR_FOR_INVALID_INPUT
        }
        long smsId = Long.parseLong(params.id.toString())
        AppSms sms = appSmsService.read(smsId)
        // check sms object existence
        errMsg = checkSmsObjectExistence(sms)
        if (errMsg) {
            return errMsg
        }
        params.put(SMS_OBJ, sms)
        return null
    }

    /**
     * check sms object existence
     * @param sms - an object of AppSms
     * @return - error message or null value
     */
    private String checkSmsObjectExistence(AppSms sms) {
        if (!sms) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

    /**
     * Build sms object for create
     * @param params - serialize parameters from UI
     * @return - new sms object
     */
    private AppSms buildSmsObject(Map params) {
        AppSms oldSms = (AppSms) params.get(SMS_OBJ)
        AppSms newSms = new AppSms(params)
        newSms.body = oldSms.body
        newSms.transactionCode = oldSms.transactionCode
        newSms.companyId = oldSms.companyId
        newSms.isActive = true
        newSms.isManualSend = true
        newSms.roleId = oldSms.roleId
        return newSms
    }
}
