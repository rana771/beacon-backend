package com.athena.mis.application.actions.appsms

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppSms
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Role
import com.athena.mis.application.model.ListForComposeSmsActionServiceModel
import com.athena.mis.application.service.AppSmsService
import com.athena.mis.application.service.ListForComposeSmsActionServiceModelService
import com.athena.mis.application.service.RoleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update sms object and show in grid
 *  For details go through Use-Case doc named 'UpdateForComposeSmsActionService'
 */
class UpdateForComposeSmsActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SMS_OBJ = "sms"
    private static final String SUCCESS_MESSAGE = "SMS has been updated successfully"

    RoleService roleService
    AppSmsService appSmsService
    ListForComposeSmsActionServiceModelService listForComposeSmsActionServiceModelService

    /**
     * 1. check validation
     * 2. build sms object for update
     * @param params - serialized parameters from UI
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
            // get role ids
            if (params.roleIds.equals(EMPTY_SPACE)) {
                List<Role> lstRole = roleService.listForDropDown()
                List<Long> lstRoleIds = lstRole*.id
                String roleIds = roleService.buildCommaSeparatedStringOfIds(lstRoleIds)
                params.roleIds = roleIds
            }
            // build sms object for update
            buildSmsObject(params)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the sms object from map
     * 2. Update existing sms object in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppSms sms = (AppSms) result.get(SMS_OBJ)
            appSmsService.update(sms)
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
     * Build sms object for update
     * @param params - serialize parameters from UI
     * @return - updated sms object
     */
    private AppSms buildSmsObject(Map params) {
        AppSms oldSms = (AppSms) params.get(SMS_OBJ)
        AppSms newSms = new AppSms(params)
        AppUser systemUser = super.getAppUser()
        oldSms.roleId = newSms.roleId
        oldSms.body = newSms.body
        oldSms.updatedOn = new Date()
        oldSms.updatedBy = systemUser.id
        return oldSms
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
        if (!params.id || !params.version || !params.body) {
            return ERROR_FOR_INVALID_INPUT
        }
        long smsId = Long.parseLong(params.id.toString())
        AppSms sms = appSmsService.read(smsId)
        // check sms object existence
        errMsg = checkSmsObjectExistence(sms, params)
        if (errMsg) {
            return errMsg
        }
        params.put(SMS_OBJ, sms)
        return null
    }

    /**
     * check sms object existence
     * @param sms - an object of AppSms
     * @param params - serialized parameters from UI
     * @return - error message or null value
     */
    private String checkSmsObjectExistence(AppSms sms, Map params) {
        long version = Long.parseLong(params.version.toString())
        if (!sms || sms.version != version) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }
}
