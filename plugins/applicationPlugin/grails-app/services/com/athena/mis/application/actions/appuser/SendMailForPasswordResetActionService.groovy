package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.MailSenderService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.config.AppSysConfigCacheService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.CompanyService
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.fixedasset.FxdPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.integration.qsmeasurement.QsPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import com.athena.mis.utility.DateUtility
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

import static grails.async.Promises.task

/**
 * Generate password reset link and code and send mail to user
 * For details go through Use-Case doc named 'SendMailForPasswordResetActionService'
 */
class SendMailForPasswordResetActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_USER = "appUser"
    private static final String VIEW = "view"
    private static final String IS_PORTAL_VIEW = "isPortalView"
    private static final String VIEW_ERROR = "/app/errorPage"
    private static final String VIEW_AUTH = "/application/login/auth"
    private static final String COMPANY = "company"
    private static final String INVALID_INPUT = "Enter your login ID"
    private static final String USER_NOT_FOUND_MSG = "User not found with the given login ID"
    private static final String USER_NOT_ENABLE_MSG = "Your account is disabled, contact with administrator"
    private static final String USER_ACC_LOCKED_MSG = "Your account is locked, contact with administrator"
    private static final String SHOW_RESET_PASSWORD = "showResetPassword"
    private static final String DATE_FORMAT = "dd_MMM_yy_hh_mm_ss"
    private static final String TRANSACTION_CODE = "SendMailForPasswordResetActionService"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "Mail sending for password reset is not activated"
    private static final String SUCCESS_MESSAGE = "Please check your mail and follow the given instructions"
    private static final String EMAIL_PATTERN = """^[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\\.[a-zA-Z]{2,4}\$"""
    private static final String INVALID_EMAIL = "Invalid email address"
    private static
    final String NOT_PRODUCTION_MODE = "Password reset mail is not sent because application is not in production mode."

    SpringSecurityService springSecurityService
    AppMailService appMailService
    LinkGenerator grailsLinkGenerator
    AppUserService appUserService
    CompanyService companyService
    MailSenderService mailSenderService
    AppConfigurationService appConfigurationService
    AppSysConfigCacheService appSysConfigCacheService

    /**
     * 1. check input validation
     * 2. check AppUser object validation
     * @param parameters -parameters from UI
     * @param obj -HttpServletRequest request
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map parameters) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            HttpServletRequest request = params.getRequest()
            Company company = companyService.read(request)
            String view = null
            boolean isPortalView = false
            if (company != null) {
                Map mapResult = getPluginIndexPage(company.id)
                view = mapResult.indexUrl
                isPortalView = mapResult.isPortalView
            } else {
                view = VIEW_ERROR
            }
            params.put(VIEW, view)
            params.put(IS_PORTAL_VIEW, isPortalView)
            // check input validation
            AppUser appUser = appUserService.findByLoginIdAndCompanyId(params.loginId, companyId)
            String checkValidationMsg = checkInputValidation(params, appUser)
            if (checkValidationMsg) {
                return super.setError(params, checkValidationMsg)
            }
            // check AppUser object validation
            checkValidationMsg = checkAppUserMapValidation(appUser)
            if (checkValidationMsg) {
                return super.setError(params, checkValidationMsg)
            }
            checkValidationMsg = getDeploymentMode(companyId)
            if (checkValidationMsg) {
                return super.setError(params, checkValidationMsg)
            }
            params.put(COMPANY, company)
            params.put(APP_USER, appUser)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Set and update properties of AppUser for reset password
     * Send mail to user
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map contains isError(true/false) depending on method success and related message
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            Company company = (Company) result.get(COMPANY)
            setPasswordResetProperties(appUser)    // set properties for reset password
            updatePasswordResetProperties(appUser) // update properties for reset password
            String msg = sendMail(appUser, company) // send mail to user
            if (msg) {
                return super.setError(result, msg)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Do nothing for post operation
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Do nothing for build success result for UI
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SUCCESS_MESSAGE)
    }

    /**
     * Do nothing for build failure result for UI
     */
    public Map buildFailureResultForUI(Map result) {
        return result
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

    /**
     * 1. check required parameter
     * 2. check loginId
     * 3. get AppUser object by loginId and companyId
     * 4. check existence of user
     * @param params -serialized parameters from UI
     * @param companyId -Company.id
     * @return -a map containing AppUser object, isError (true/false) and relevant message
     */
    private String checkInputValidation(GrailsParameterMap params, AppUser appUser) {
        // check required parameter
        if (!params.loginId) {
            return INVALID_INPUT
        }
        // check if user exists or not
        if (!appUser) {
            return USER_NOT_FOUND_MSG
        }
        return null
    }

    /**
     * 1. check if user is enabled or not
     * 2. check if user is locked or not
     * @param appUser -object of AppUser
     * @return -a map containing isError (true/false) and relevant message
     */
    private String checkAppUserMapValidation(AppUser appUser) {
        // check if user is enable or not
        if (!appUser.enabled) {
            return USER_NOT_ENABLE_MSG
        }
        // check if user is locked or not
        if (appUser.accountLocked) {
            return USER_ACC_LOCKED_MSG
        }
        return null
    }

    /**
     * Set properties of AppUser for reset password
     * @param user -object of AppUser
     */
    private void setPasswordResetProperties(AppUser user) {
        user.passwordResetLink = springSecurityService.encodePassword(user.loginId + new Date().format(DATE_FORMAT))
        user.passwordResetCode = generateSecurityCode() // generate security code
        user.passwordResetValidity = new Date() + 1
    }

    private static final String CHAR_SET = "ABCDEFGHIJKLMNPQRSTUVWXYZ1234567891234567891234567"
    private static final int CODE_LENGTH = 5

    /**
     * Generate security code for reset password
     * @return -generated security code
     */
    private String generateSecurityCode() {
        Random random = new Random()
        char[] code = new char[CODE_LENGTH]
        for (int i = 0; i < CODE_LENGTH; i++) {
            code[i] = CHAR_SET.charAt(random.nextInt(CHAR_SET.length()))
        }
        return new String(code)
    }

    /**
     * Generate link and send mail to user
     * @param user -object of AppUser
     * @return -error message if mail template is not found else null
     */
    private String sendMail(AppUser user, Company company) {
        String link = grailsLinkGenerator.link(controller: APP_USER, action: SHOW_RESET_PASSWORD, absolute: true, params: [link: user.passwordResetLink, companyId: company.id])
        AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, user.companyId, true)
        if (!appMail) {
            return MAIL_TEMPLATE_NOT_FOUND
        }
        // get evaluated mail object
        appMail.recipients = user.email
        appMail = getAppMail(user, appMail, link, company)
        task { mailSenderService.sendMail(appMail) }
        return null
    }

    /**
     * 1. get evaluated mail object
     *
     * @param appUser - an object of AppUser
     * @param appMail - an object of AppMail
     * @param link - a link of user activation
     * @return appMail
     */
    private AppMail getAppMail(AppUser appUser, AppMail appMail, String link, Company company) {
        Map parameters = [userName: appUser.username, loginId: appUser.loginId,
                          link    : link, securityCode: appUser.passwordResetCode, company: company.name]
        appMail.evaluateMailBody(parameters)
        return appMail
    }

    private static final String UPDATE_USER = """
        UPDATE app_user
        SET
            password_reset_link=:passwordResetLink,
            password_reset_code=:passwordResetCode,
            password_reset_validity=:passwordResetValidity,
            version=version+1
        WHERE
            id=:id AND
            version=:version
    """

    /**
     * Update AppUser properties for reset password
     * @param user -object of AppUser
     * @return -an integer containing the value of update count
     */
    private int updatePasswordResetProperties(AppUser user) {
        Map queryParams = [
                passwordResetLink    : user.passwordResetLink,
                passwordResetCode    : user.passwordResetCode,
                passwordResetValidity: DateUtility.getSqlDateWithSeconds(user.passwordResetValidity),
                id                   : user.id,
                version              : user.version
        ]
        int updateCount = executeUpdateSql(UPDATE_USER, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating user information for password reset')
        }
        user.version = user.version + 1
        return updateCount
    }

    private Map getPluginIndexPage(long companyId) {
        SysConfiguration sysConfiguration = (SysConfiguration) appSysConfigCacheService.readByKeyAndCompanyId(appSysConfigCacheService.DEFAULT_PLUGIN, companyId)
        long pluginId = Long.parseLong(sysConfiguration.value)
        String indexUrl = VIEW_AUTH
        switch (pluginId) {
            case ExchangeHousePluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
                    indexUrl = VIEW_AUTH
                    return ["indexUrl": indexUrl, "isPortalView": false]
                }
                break
            case SarbPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(SarbPluginConnector.PLUGIN_NAME)) {
                    indexUrl = VIEW_AUTH
                    return ["indexUrl": indexUrl, "isPortalView": false]
                }
                break
            case ArmsPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)) {
                    indexUrl = VIEW_AUTH
                    return ["indexUrl": indexUrl, "isPortalView": false]
                }
                break
            case DocumentPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
                    indexUrl = VIEW_AUTH
                    return ["indexUrl": indexUrl, "isPortalView": false]
                }
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(DataPipeLinePluginConnector.PLUGIN_NAME)) {
                    indexUrl = VIEW_AUTH
                    return ["indexUrl": indexUrl, "isPortalView": false]
                }
                break
            case PtPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    indexUrl = VIEW_AUTH
                    return ["indexUrl": indexUrl, "isPortalView": false]
                }
                break
            case ProcPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)) {
                    indexUrl = VIEW_AUTH
                    return ["indexUrl": indexUrl, "isPortalView": false]
                }
                break
            case BudgPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
                    indexUrl = VIEW_AUTH
                    return ["indexUrl": indexUrl, "isPortalView": false]
                }
                break
            case AccPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
                    indexUrl = VIEW_AUTH
                    return ["indexUrl": indexUrl, "isPortalView": false]
                }
                break
            case InvPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
                    indexUrl = VIEW_AUTH
                    return ["indexUrl": indexUrl, "isPortalView": false]
                }
                break
            case QsPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(QsPluginConnector.PLUGIN_NAME)) {
                    indexUrl = VIEW_AUTH
                    return ["indexUrl": indexUrl, "isPortalView": false]
                }
                break
            case FxdPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(FxdPluginConnector.PLUGIN_NAME)) {
                    indexUrl = VIEW_AUTH
                    return ["indexUrl": indexUrl, "isPortalView": false]
                }
                break
            case ELearningPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/el/loginPage"
                    return ["indexUrl": indexUrl, "isPortalView": true]
                }
                break
            default:
                return ["indexUrl": indexUrl, "isPortalView": false]
        }
        return ["indexUrl": indexUrl, "isPortalView": false]
    }
}
