package com.athena.mis.application.actions.appsms

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppSms
import com.athena.mis.application.service.AppSmsService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update sms object and grid data
 *  For details go through Use-Case doc named 'UpdateAppSmsActionService'
 */
class UpdateAppSmsActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    AppSmsService appSmsService

    private static final String UPDATE_SUCCESS_MSG = "SMS has been updated successfully"
    private static final String SMS_NOT_FOUND = "SMS not found to be updated, refresh the page"
    private static final String RECIPIENTS_NOT_FOUND = "Recipients not found"
    private static final String SMS_OBJECT = "sms"

    /**
     * Get parameters from UI and build sms object for update
     * 1. Check the existence of old sms object
     * 2. Build new sms object
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long smsId = Long.parseLong(params.id.toString())
            AppSms oldSms = appSmsService.read(smsId)
            if (!oldSms) {
                return super.setError(params, SMS_NOT_FOUND)
            }

            AppSms smsObject = buildSmsObjectForUpdate(params, oldSms) // build sms object
            if (smsObject.isManualSend) {
                String recipients = params.recipients
                List<String> lstRecipients = recipients.split(COMA)
                if (lstRecipients.size() <= 0) {
                    return super.setError(params, RECIPIENTS_NOT_FOUND)
                }
            }
            params.put(SMS_OBJECT, smsObject)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Update sms object in DB
     * 1. This function is in transactional block and will roll back in case of any exception
     * @param result - serialized parameters from pre method
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppSms smsObject = (AppSms) result.get(SMS_OBJECT)
            appSmsService.update(smsObject)
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
     * Show updated sms object in grid
     * Show success message
     * @param result -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, UPDATE_SUCCESS_MSG)
    }

    /**
     * @param result -map returned from previous method
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build sms object for update
     * 1. Update oldSms object by newSms object
     * @param params - serialized parameters from UI
     * @param oldSms - old sms object
     * @return - updated sms object
     */
    private AppSms buildSmsObjectForUpdate(Map params, AppSms oldSms) {
        AppSms newSms = new AppSms(params)
        oldSms.isActive = newSms.isActive
        oldSms.body = newSms.body
        if (oldSms.isManualSend) {
            oldSms.recipients = newSms.recipients
        }
        oldSms.updatedOn = new Date()
        oldSms.updatedBy = super.appUser.id
        return oldSms
    }
}
