package com.athena.mis.application.actions.appsms

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.SmsSenderService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppSms
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.AppSmsService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

import static grails.async.Promises.task

/**
 *  Send sms to recipients
 *  For details go through Use-Case doc named 'SendSmsActionService'
 */
class SendSmsActionService extends BaseService implements ActionServiceIntf {

    private static final String SHOW_SUCCESS_MESSAGE = "SMS has been sent successfully"
    private static final String LST_SMS = "lstSms"
    private static final String NOT_FOUND_MSG = "The selected sms is not active"
    private static final String RECIPIENTS_NOT_FOUND = 'Recipients not found'
    private static final String NOT_PRODUCTION_MODE = "SMS is not sent because application is not in production mode."

    private Logger log = Logger.getLogger(getClass())

    AppSmsService appSmsService
    SmsSenderService smsSenderService
    AppConfigurationService appConfigurationService

    /**
     * 1. check required parameters
     * 2. get list of objects of sms by transactionCode, companyId & isActive = true
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Map executePreCondition(Map params) {
        try {
            if (!params.transactionCode) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long companyId = super.companyId
            String errMsg = getDeploymentMode(companyId)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            String transactionCode = params.transactionCode
            List<AppSms> lstSms = appSmsService.findAllByTransactionCodeAndCompanyIdAndIsActive(transactionCode, companyId, true)
            if (lstSms.size() <= 0) {
                return super.setError(params, NOT_FOUND_MSG)
            }
            params.put(LST_SMS, lstSms)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Send sms to recipients
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing isError(true/false) & relevant message depending on method success
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            List<AppSms> lstSms = (List<AppSms>) result.get(LST_SMS)
            String msg = sendSms(lstSms)
            if (msg) {
                return super.setError(result, msg)
            }
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
     * Do nothing for build success result
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SHOW_SUCCESS_MESSAGE)
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Send sms
     * @param lstSms -list of objects of sms
     * @return -a string containing null value or error message depending on method success
     */
    private String sendSms(List<AppSms> lstSms) {
        String msg = null
        for (int i = 0; i < lstSms.size(); i++) {
            AppSms sms = lstSms[i]
            if ((!sms.recipients) || (sms.recipients.length() == 0)) {
                msg = RECIPIENTS_NOT_FOUND
                return msg
            }
            sms.body = '"""' + sms.body + '"""'
            sms.body = new GroovyShell().evaluate(sms.body)
            task {smsSenderService.sendSms(sms)}
        }
        return msg
    }

    /**
     * get deployment mode config value
     * @param companyId - id of company
     * @return - value of deployment mode config
     */
    private String getDeploymentMode(long companyId) {
        int deploymentMode = 1
        SysConfiguration sysConfiguration = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.APPLICATION_DEPLOYMENT_MODE, companyId)
        if (sysConfiguration) {
            deploymentMode = Integer.parseInt(sysConfiguration.value)
        }
        if (deploymentMode != 1) {
            return NOT_PRODUCTION_MODE
        }
        return null
    }
}
