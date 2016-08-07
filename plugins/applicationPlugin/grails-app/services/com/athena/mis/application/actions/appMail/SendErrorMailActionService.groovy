package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.MailSenderService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.CompanyService
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

import static grails.async.Promises.task

/**
 * Send mail to report error
 * For details go through Use-Case doc named 'SendErrorMailActionService'
 */
class SendErrorMailActionService extends BaseService implements ActionServiceIntf {

    AppMailService appMailService
    CompanyService companyService
    MailSenderService mailSenderService
    AppConfigurationService appConfigurationService

    private Logger log = Logger.getLogger(getClass())

    private static final String INVALID_INPUT = "Unable to report error due to invalid input"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Mail template not found"
    private static final String MAIL_SENDING_SUCCESS = "Error has been reported successfully"
    private static final String RECIPIENTS_NOT_FOUND = "Recipient(s) not found to report error"
    private static final String TRANSACTION_CODE = "SendErrorMailActionService"
    private static final String NOT_PRODUCTION_MODE = "Application has to be in production mode to send mail"

    /**
     * Check required parameters
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        try {
            if (!params.transactionCode && !params.message && !params.classSignature) {
                return super.setError(params, INVALID_INPUT)
            }
            String errMsg = checkDeploymentMode()
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
     * Send mail
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map params) {
        try {
            String message = params.message
            String classSignature = params.classSignature
            String comments = params.txtErrorComments

            String returnResult = sendMail(message, classSignature, comments)    // send mail
            if (returnResult) {
                return super.setError(params, returnResult)
            }
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
     * set success message
     * @param result - map from execute/executePost method
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
     * 1. get mail template by transaction code
     * 2. build mail body template and subject
     * 3. send mail
     * @param message - error message
     * @param classSignature - name of actionService
     * @param comments - comments
     * @return -a string containing message or null value depending on method success
     */
    private String sendMail(String message, String classSignature, String comments) {
        AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, companyId, true)
        if (!appMail) {
            return MAIL_TEMPLATE_NOT_FOUND
        }
        if (!appMail.recipients) {
            return RECIPIENTS_NOT_FOUND
        }

        AppUser appUser = super.getAppUser()
        Company company = companyService.read(appUser.companyId)

        Map params = buildParamsForMailBody(company, message, classSignature, comments, appUser)
        appMail.evaluateMailBody(params)

        task { mailSenderService.sendMail(appMail) }
        return null
    }

    // build mail params
    private Map buildParamsForMailBody(Company company, String message, String classSignature, String comments, AppUser appUser) {
        String companyName = company.name + PARENTHESIS_START + company.id + PARENTHESIS_END
        String userName = appUser.username + PARENTHESIS_START + appUser.id + PARENTHESIS_END
        Date currentDate = new Date()
        String errorReportTime = DateUtility.formatDateTimeLongAmPm(currentDate)
        Map paramsBody = [
                message        : message,
                classSignature : classSignature,
                comments       : comments,
                companyName    : companyName,
                userName       : userName,
                errorReportTime: errorReportTime
        ]
        return paramsBody
    }

    /**
     * Check application deployment mode
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on application deployment mode
     */
    private String checkDeploymentMode() {
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
