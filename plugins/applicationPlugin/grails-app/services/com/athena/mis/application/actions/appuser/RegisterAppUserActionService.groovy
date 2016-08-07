package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.MailSenderService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.*
import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.grails.plugin.jcaptcha.JcaptchaService

import javax.servlet.http.HttpServletRequest

import static grails.async.Promises.task

class RegisterAppUserActionService extends BaseService implements ActionServiceIntf{

    private Logger log = Logger.getLogger(getClass())

    SpringSecurityService springSecurityService
    AppUserService appUserService
    UserRoleService userRoleService
    AppMailService appMailService
    JcaptchaService jcaptchaService
    LinkGenerator grailsLinkGenerator
    CompanyService companyService
    RoleService roleService
    MailSenderService mailSenderService
    AppConfigurationService appConfigurationService

    private static final String APP_USER_SAVE_FAILURE_MESSAGE = "Application user registration failed"
    private static final String NOT_AVAILABLE = ", login id  is not available."
    private static final String CAPTCHA_INVALID_ERROR = "Invalid security code, please refresh the page."
    private static final String CAPTCHA_NOT_MATCHED_ERROR = "Security code did not match. Try again."
    private static final String COMPANY_NOT_FOUND = "Company not found"
    private static final String INVALID_CAPTCHA_SESSION = "Invalid captcha session"
    private static final String REGISTRATION_SUCCESS_MSG = 'Registration Success! Check e-mail for activation.'
    private static final String APP_USER_OBJ = "appUser"
    private static final String IMAGE = "image"
    private static final String APP_USER = "appUser"
    private static final String ACTIVATION = "activate"
    private static final String USECASE_SEND_MAIL_USER_REGISTRATION = "RegisterAppUserActionService"
    private static final String COMPANY_ID = "companyId"
    private static final String ROLE_NOT_FOUND = "No role found to map with app user"
    private static final String ROLE = "role"
    private static final String LOGIN_ID_EXISTS_MSG = "Same login id already exists"
    private static final String COMPANY = "company"
    private static final String MAIL_TEMPLATE_NOT_FOUND = "User registration mail not send due to absence of mail template."
    private static final String NOT_PRODUCTION_MODE = "User registration mail is not sent because application is not in production mode"

    /**
     * Get params from UI and build appUser
     * @param parameters
     * @return
     */
    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try {
            long companyId = Long.parseLong(parameters.companyId.toString())
            Company company
            if(companyId > 0) {
                company = companyService.read(companyId)
            } else {
                GrailsParameterMap params = (GrailsParameterMap) parameters
                HttpServletRequest request = params.getRequest()
                company =  companyService.read(request)
            }
            if (!company) {
                return super.setError(parameters, COMPANY_NOT_FOUND)
            }
            parameters.put(COMPANY, company)    //in case other pre-condition returns
            String errMsg = checkPreConditionForSignUp(parameters)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            Map roleMap = getRoleForAppUser(companyId)
            Role role = (Role) roleMap.get(ROLE)
            if (!role) {
                return super.setError(parameters, roleMap.errMsg)
            }
            AppUser appUser = buildAppUser(parameters)
            errMsg = checkDuplicateLoginId(appUser, companyId)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            parameters.put(ROLE, role)
            parameters.put(COMPANY_ID, companyId)
            parameters.put(APP_USER_OBJ, appUser)
            return parameters
        } catch (Exception e) {
            log.error(e.getMessage())
            return super.setError(parameters, APP_USER_SAVE_FAILURE_MESSAGE)
        }
    }

    /**
     * Save appUser and userRole
     * @param previousResult
     * @return
     */
    @Transactional
    Map execute(Map previousResult) {
        try {
            AppUser appUser = (AppUser) previousResult.get(APP_USER_OBJ)
            Role role = (Role) previousResult.get(ROLE)
            AppUser newAppUser = appUserService.create(appUser)
            UserRole newUserRole = new UserRole(userId: newAppUser.id, roleId: role.id)
            userRoleService.create(newUserRole)
            previousResult.put(APP_USER_OBJ, newAppUser)
            return previousResult
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * send mail for activation
     * @param previousResult
     * @return
     */
    @Transactional(readOnly = true)
    Map executePostCondition(Map previousResult) {
        try {
            AppUser appUser = (AppUser) previousResult.get(APP_USER_OBJ)
            sendActivationMail(appUser)              // sent mail for activation
            return previousResult
        } catch (Exception e) {
            log.error(e.getMessage())
            return super.setError(previousResult, APP_USER_SAVE_FAILURE_MESSAGE)
        }
    }

    /**
     * @param executeResult
     * @return
     */
    Map buildSuccessResultForUI(Map executeResult) {
        return super.setSuccess(executeResult, REGISTRATION_SUCCESS_MSG)
    }

    /**
     * do nothing
     * @param executeResult
     * @return
     */
    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    private Map getRoleForAppUser(long companyId) {
        SysConfiguration roleId = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.ROLE_ID_FOR_APP_USER_REGISTRATION, companyId)
        Role role = roleService.read(Long.parseLong(roleId.value.toString()))
        String errMsg = (roleId.message && !roleId.message.equals(EMPTY_SPACE)) ? roleId.message : ROLE_NOT_FOUND
        return [role: role, errMsg: errMsg]
    }

    /**
     * check pre condition for sign up
     * validate captcha
     * @param parameters
     * @return
     */
    private String checkPreConditionForSignUp(Map parameters) {
        String sessionId = parameters.get("sessionId")

        if (!(parameters.captcha) || parameters.captcha.toString().equals(EMPTY_SPACE)) {
            return CAPTCHA_INVALID_ERROR
        }
        boolean matched = false
        try {
            matched = jcaptchaService.validateResponse(IMAGE, sessionId, parameters.captcha)
        } catch (Exception ignored) {
            return INVALID_CAPTCHA_SESSION
        }
        if (!matched) {
            return CAPTCHA_NOT_MATCHED_ERROR
        }
        return null
    }

    /**
     * Build appUser object
     * @param params -serialized parameters from UI
     * @return -new appUser object
     */
    private AppUser buildAppUser(Map params) {
        long companyId = Long.parseLong(params.companyId.toString())
        AppUser appUser = new AppUser(params)
        SysConfiguration sysConfig = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.DEFAULT_PASSWORD_EXPIRE_DURATION, companyId)
        if (sysConfig) {
            appUser.nextExpireDate = new Date() + Integer.parseInt(sysConfig.value)
        } else {
            appUser.nextExpireDate = new Date()
        }
        appUser.enabled = Boolean.FALSE
        appUser.companyId = companyId
        appUser.password = springSecurityService.encodePassword(params.password)
        appUser.activationLink = springSecurityService.encodePassword(appUser.loginId + new Date().toString())
        appUser.createdOn = new Date()
        appUser.email = appUser.loginId
        return appUser
    }

    /**
     * a mail sent to newly created user with activation link
     */
    private boolean sendActivationMail(AppUser appUser) {
        String link = grailsLinkGenerator.link(controller: APP_USER, action: ACTIVATION, absolute: true, params: [link: appUser.activationLink])
        // activation link generate
        AppMail appMail = appMailService.findByTransactionCodeAndCompanyIdAndIsActive(USECASE_SEND_MAIL_USER_REGISTRATION, appUser.companyId, Boolean.TRUE)
        if (!appMail) {
            println MAIL_TEMPLATE_NOT_FOUND
            return
        }
        // check deployment mode
        String deploymentMode = getDeploymentMode(appMail.companyId)
        if (deploymentMode) {
            log.error(deploymentMode)
            return
        }
        appMail.setRecipients(appUser.loginId)
        // get evaluated mail object
        appMail = getAppMail(appUser, appMail, link)
        task { mailSenderService.sendMail(appMail) }
    }

    /**
     * 1. get evaluated mail object
     *
     * @param appUser- an object of AppUser
     * @param appMail- an object of AppMail
     * @param link - a link of user activation
     * @return appMail
     */
    private AppMail getAppMail(AppUser appUser, AppMail appMail, String link) {
        Map parameters = [email: appUser.loginId, name: appUser.username, link: link]
        appMail.evaluateMailBody(parameters)
        return appMail
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
     * Check if duplicate login id exists either in the same company or other company
     * @param appUser - AppUser obj
     * @param companyId - companyId
     * @return - errMsg or null
     */
    private String checkDuplicateLoginId(AppUser appUser, long companyId) {
        String duplicateErrorMsg = null
        int count = appUserService.countByLoginIdIlikeAndCompanyId(appUser.loginId, companyId)
        if (count > 0) {
            return LOGIN_ID_EXISTS_MSG
        }
        count = appUserService.countByLoginIdIlike(appUser.loginId)
        if (count > 0) {
            duplicateErrorMsg = appUser.loginId + NOT_AVAILABLE
            return duplicateErrorMsg
        }
        return duplicateErrorMsg
    }
}
