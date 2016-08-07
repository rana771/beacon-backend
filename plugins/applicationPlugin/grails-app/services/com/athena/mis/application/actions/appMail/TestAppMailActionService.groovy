package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.MailSenderService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.AppMailService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

import static grails.async.Promises.task

/**
 * Send mail for test purpose.
 * For details go through Use-Case doc named 'TestAppMailActionService'
 */
class TestAppMailActionService extends BaseService implements ActionServiceIntf {

    AppMailService appMailService
    MailSenderService mailSenderService
    AppConfigurationService appConfigurationService

    private Logger log = Logger.getLogger(getClass())

    private static final String INVALID_INPUT = "Unable to sent mail due to invalid input"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Mail template not found"
    private static final String CANNOT_SEND_MANUALLY = "Selected mail cannot sent manually"
    private static final String MAIL_SENDING_SUCCESS = "Mail sent successfully"
    private static final String TRANSACTION_CODE = "TestAppMailActionService"
    private static final String APP_MAIL = "appMail"
    private static final String NOT_PRODUCTION_MODE = "Application has to be in production mode to send mail"

    /**
     * Check validation
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
     * Get desired report providing all required parameters
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            AppMail appMail = (AppMail) result.get(APP_MAIL)
            // get recipients
            if (appMail.isRequiredRoleIds) {
                appMail.recipients = appMailService.getAllMailAddresses(appMail.companyId, appMail.roleIds, null, null)
            }
            // send mail
            task { mailSenderService.sendMail(appMail) }
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
     * set success message
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        try {
            return super.setSuccess(result, MAIL_SENDING_SUCCESS)
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
     * 1. check required parameters
     * 2. check mail object existence
     * 3. check manual send true or false
     * 4. check application deployment mode
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on check validation
     */
    private String checkValidation(Map params) {
        // check required parameter
        String errMsg = checkRequiredParameters(params)
        if (errMsg) {
            return errMsg
        }
        // check mail object existence
        errMsg = checkMailObjectExistence(params)
        if (errMsg) {
            return errMsg
        }
        // check manual send true or false
        errMsg = checkManualSend(params)
        if (errMsg) {
            return errMsg
        }
        // check application deployment mode
        errMsg = checkDeploymentMode(params)
        if (errMsg) {
            return errMsg
        }
        return null
    }

    /**
     * Check required parameter
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on check validation
     */
    private String checkRequiredParameters(Map params) {
        // check required parameter
        if (!params.transactionCode) {
            return INVALID_INPUT
        }
        return null
    }

    /**
     * Check mail object existence
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on object existence
     */
    private String checkMailObjectExistence(Map params) {
        // get mail object
        AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, companyId, true)
        if (!appMail) {
            return MAIL_TEMPLATE_NOT_FOUND
        }
        params.put(APP_MAIL, appMail)
        return null
    }

    /**
     * check manual send true or false
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on check validation
     */
    private String checkManualSend(Map params) {
        AppMail appMail = (AppMail) params.get(APP_MAIL)
        if (!appMail.isManualSend) {
            return CANNOT_SEND_MANUALLY
        }
        return null
    }

    /**
     * Check application deployment mode
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on application deployment mode
     */
    private String checkDeploymentMode(Map params) {
        AppMail appMail = (AppMail) params.get(APP_MAIL)
        int deploymentMode = 1
        SysConfiguration sysConfiguration = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.APPLICATION_DEPLOYMENT_MODE, appMail.companyId)
        if (sysConfiguration) {
            deploymentMode = Integer.parseInt(sysConfiguration.value)
        }
        if (deploymentMode != 1) {
            return NOT_PRODUCTION_MODE
        }
        return null
    }
}
