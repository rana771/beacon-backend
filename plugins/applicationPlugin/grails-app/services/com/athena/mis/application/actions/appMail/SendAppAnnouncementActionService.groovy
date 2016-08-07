package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.MailSenderService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.AppUserService
import groovy.sql.GroovyRowResult
import org.apache.commons.lang.StringEscapeUtils
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

import static grails.async.Promises.task

/**
 * Send mail
 * For details go through Use-Case doc named 'SendAppAnnouncementActionService'
 */
class SendAppAnnouncementActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SUCCESS_MSG = "Announcement has been sent successfully."
    private static final String APP_MAIL = "appMail"
    private static final String NOT_PRODUCTION_MODE = "Application has to be in production mode to send mail"

    AppMailService appMailService
    AppUserService appUserService
    MailSenderService mailSenderService
    AppConfigurationService appConfigurationService

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
     * 1. send mail
     * 2. update mail object
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppMail appMail = (AppMail) result.get(APP_MAIL)
            List<GroovyRowResult> lstUser = getUserNameAndEmailAddress(appMail.roleIds)
            // send mail
            task { sendMail(appMail, lstUser) }
            // update mail
            updateMail(appMail, lstUser)
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
            return super.setSuccess(result, SUCCESS_MSG)
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
     * 1. check required parameter
     * 2. check mail object existence
     * 3. check application deployment mode
     * @param params - serialized parameters from UI
     * @return - error message or null value
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
        if (!params.id) {
            return ERROR_FOR_INVALID_INPUT
        }
        return null
    }

    /**
     * Check mail object existence
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on object existence
     */
    private String checkMailObjectExistence(Map params) {
        long mailId = Long.parseLong(params.id.toString())
        AppMail mail = appMailService.read(mailId)
        // check mail object existence
        if (!mail) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        params.put(APP_MAIL, mail)
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

    /**
     * Send mail
     * @param appMail - object of AppMail
     */
    private void sendMail(AppMail appMail, List<GroovyRowResult> lstUser) {
        // get list of user
        for (int i = 0; i < lstUser.size(); i++) {
            AppMail newMail = new AppMail()
            newMail.companyId = appMail.companyId
            newMail.body = appMail.body
            newMail.displayName = appMail.displayName
            newMail.subject = appMail.subject
            newMail.body = StringEscapeUtils.unescapeHtml(newMail.body);
            newMail.recipients = lstUser[i][0].toString()
            Map paramsBody = [
                    userName: lstUser[i][1].toString()
            ]
            newMail.evaluateMailBody(paramsBody)
            mailSenderService.sendMail(newMail)
        }
    }

    /**
     * Update mail object
     * @param appMail - object of AppMail
     */
    private void updateMail(AppMail appMail, List<GroovyRowResult> lstUser) {
        // build object for update
        appMail = buildObjectForUpdate(appMail, lstUser)
        // update mail object
        appMailService.update(appMail)
    }

    /**
     * Build object for update
     * @param appMail - object of AppMail
     * @return - updated object of AppMail
     */
    private AppMail buildObjectForUpdate(AppMail appMail, List<GroovyRowResult> lstUser) {
        List<String> lstRecipients = []
        for (int i = 0; i < lstUser.size(); i++) {
            String recipient = lstUser[i][0].toString()
            lstRecipients << recipient
        }
        String recipients = appMailService.buildCommaSeparatedStringOfIds(lstRecipients)
        appMail.hasSend = true
        appMail.version = appMail.version + 1
        appMail.updatedBy = getAppUser().id
        appMail.updatedOn = new Date()
        appMail.isActive = false
        appMail.recipients = recipients
        return appMail
    }

    // get user list to send mail
    private List<GroovyRowResult> getUserNameAndEmailAddress(String roleIds) {
        String strQuery = """
            SELECT DISTINCT(login_id), username
            FROM app_user au
            WHERE au.enabled = true
            AND au.id IN
            (SELECT ur.user_id FROM user_role ur
            WHERE ur.role_id IN (${roleIds}))
        """
        List<GroovyRowResult> lstResult = executeSelectSql(strQuery)
        return lstResult
    }
}
