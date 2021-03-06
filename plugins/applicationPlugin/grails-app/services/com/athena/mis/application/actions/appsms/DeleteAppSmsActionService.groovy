package com.athena.mis.application.actions.appsms

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppSms
import com.athena.mis.application.service.AppSmsService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete sms object from DB
 *  For details go through Use-Case doc named 'DeleteAppSmsActionService'
 */
class DeleteAppSmsActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SUCCESS_MESSAGE = "SMS has been deleted successfully"
    private static final String SMS_OBJ = "sms"

    AppSmsService appSmsService

    /**
     * 1. check validation
     * @param params - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check validation
            String errMsg = checkValidation(params)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Delete sms object from DB
     * 1. get the sms object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppSms sms = (AppSms) result.get(SMS_OBJ)
            appSmsService.delete(sms)
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
     * 1. check required parameter
     * 2. check sms object existence
     * @param params - serialized parameters from UI
     * @return - error message or null value
     */
    private String checkValidation(Map params) {
        // check required parameter
        if (!params.id) {
            return ERROR_FOR_INVALID_INPUT
        }
        long smsId = Long.parseLong(params.id.toString())
        AppSms sms = appSmsService.read(smsId)
        // check mail object existence
        if (!sms) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        params.put(SMS_OBJ, sms)
        return null
    }
}
