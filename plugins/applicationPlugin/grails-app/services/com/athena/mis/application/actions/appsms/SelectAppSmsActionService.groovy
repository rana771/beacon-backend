package com.athena.mis.application.actions.appsms

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppSms
import com.athena.mis.application.service.AppSmsService
import org.apache.log4j.Logger

/**
 *  Select object specific sms while selecting grid row
 *  For details go through Use-Case doc named 'SelectAppSmsActionService'
 */
class SelectAppSmsActionService extends BaseService implements ActionServiceIntf {

    private static final String NOT_FOUND_MASSAGE = "Selected sms not found"

    private Logger log = Logger.getLogger(getClass())

    AppSmsService appSmsService

    /**
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * Get sms details of selected sms
     * 1. Check the existence of sms object
     * @param parameters - get params from UI
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Map execute(Map params) {
        try {
            Integer smsId = Integer.parseInt(params.id.toString())
            AppSms smsObject = appSmsService.read(smsId)
            if (!smsObject) {
                return super.setError(params, NOT_FOUND_MASSAGE)
            }
            params.put(ENTITY, smsObject)
            return params
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
     * Build a map with AppMail object & other related properties to show on UI
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
