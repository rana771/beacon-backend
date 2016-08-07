package com.athena.mis.application.actions.appsms

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppSms
import com.athena.mis.application.entity.Role
import com.athena.mis.application.model.ListForComposeSmsActionServiceModel
import com.athena.mis.application.service.AppSmsService
import com.athena.mis.application.service.ListForComposeSmsActionServiceModelService
import com.athena.mis.application.service.RoleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new sms object and show in grid
 *  For details go through Use-Case doc named 'CreateAppSmsActionService'
 */
class CreateAppSmsActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SMS_OBJ = "sms"
    private static final String SUCCESS_MESSAGE = "SMS has been saved successfully"
    private static final String TRANSACTION_CODE = "CreateAppSmsActionService"

    AppSmsService appSmsService
    RoleService roleService
    ListForComposeSmsActionServiceModelService listForComposeSmsActionServiceModelService

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
            // get role ids
            if (params.roleId.equals(EMPTY_SPACE)) {
                List<Role> lstRole = roleService.listForDropDown()
                List<Long> lstRoleIds = lstRole*.id
                String roleIds = roleService.buildCommaSeparatedStringOfIds(lstRoleIds)
                params.roleId = roleIds
            }
            // build mail object
            AppSms sms = getSmsObject(params)
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
    @Transactional(readOnly = true)
    Map buildSuccessResultForUI(Map result) {
        try{
            AppSms sms = (AppSms) result.get(SMS_OBJ)
            ListForComposeSmsActionServiceModel savedSms = listForComposeSmsActionServiceModelService.read(sms.id)
            result.put(SMS_OBJ, savedSms)
            return super.setSuccess(result, SUCCESS_MESSAGE)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
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
     * Build sms object
     * @param params - serialized parameters from UI
     * @return - sms object
     */
    private AppSms getSmsObject(Map params) {
        AppSms sms = new AppSms(params)
        sms.body = params.body
        sms.companyId = super.getCompanyId()
        sms.isActive = true
        sms.transactionCode = TRANSACTION_CODE
        sms.isManualSend = true
        return sms
    }

    /**
     * 1. check required parameters
     * @param params - serialized parameters from UI
     * @return - a string containing null value or error message depending on validation check
     */
    private String checkValidation(Map params) {
        // check required parameters
        if (!params.body) {
            return ERROR_FOR_INVALID_INPUT
        }
        return null
    }
}
