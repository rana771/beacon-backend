package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.MailSenderService
import com.athena.mis.SmsSenderService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.*
import grails.plugin.springsecurity.SpringSecurityService
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.mapping.LinkGenerator
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import javax.servlet.http.HttpServletRequest

import static grails.async.Promises.task

class RegisterAppUserForElActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_USER_SAVE_FAILURE_MESSAGE = "Application user registration failed"
    private static final String NOT_AVAILABLE = ", login id  is not available."
    private static final String COMPANY_NOT_FOUND = "Company not found"
    private static final String REGISTRATION_SUCCESS_MSG = 'Registration Success! Check e-mail for activation.'
    private static final String APP_USER_OBJ = "appUser"
    private static final String EL_PORTAL = "elPortal"
    private static final String ACTIVATION = "activate"
    private static final String USECASE_SEND_MAIL_USER_REGISTRATION = "RegisterAppUserActionService"
    private static final String COMPANY_ID = "companyId"
    private static final String ROLE_NOT_FOUND = "No role found to map with app user"
    private static final String ROLE = "role"
    private static final String LOGIN_ID_EXISTS_MSG = "Same login id already exists"
    private static final String EMAIL_ID_EXISTS_MSG = "Email ID already exists."
    private static final String COMPANY = "company"
    private static final String EMAIL = "email"
    private static final String PHONE = "phone"
    private static final String MALE = "male"
    private static final String FEMALE = "female"
    private static final String TEACHER = "teacher"
    private static final String STUDENT = "student"
    private static final String TRANSACTION_CODE = "RegisterAppUserForElActionService"
    private static
    final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})\$";
    private static
    final String MAIL_TEMPLATE_NOT_FOUND = "User registration mail not send due to absence of mail template."
    private static
    final String SMS_TEMPLATE_NOT_FOUND = "User registration sms not send due to absence of sms template."
    private static
    final String NOT_PRODUCTION_MODE = "User registration mail is not sent because application is not in production mode"
    private static final String INVALID_PHONE_NUMBER = "Please type valid phone number"
    private static final String INVALID_EMAIL = "Please type valid email"

    RoleService roleService
    AppMailService appMailService
    AppSmsService appSmsService
    SmsSenderService smsSenderService
    CompanyService companyService
    AppUserService appUserService
    UserRoleService userRoleService
    LinkGenerator grailsLinkGenerator
    MailSenderService mailSenderService
    SpringSecurityService springSecurityService
    AppConfigurationService appConfigurationService
    AppSystemEntityCacheService appSystemEntityCacheService
    AppCountryService appCountryService

    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            HttpServletRequest request = params.getRequest()
            Company company = companyService.read(request)
            if (!company) {
                return super.setError(parameters, COMPANY_NOT_FOUND)
            }
            parameters.put(COMPANY, company)    //in case other pre-condition returns
            Map roleMap = getRoleForAppUser(parameters, company.id)
            Role role = (Role) roleMap.get(ROLE)
            if (!role) {
                return super.setError(parameters, roleMap.errMsg)
            }
            AppUser appUser = buildAppUser(parameters, company.id)
            String errMsg = checkDuplicateLoginId(appUser, company.id)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            errMsg = checkInvalidCellNumber(appUser)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            errMsg = checkInvalidEmail(appUser)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            parameters.put(ROLE, role)
            parameters.put(COMPANY_ID, company.id)
            parameters.put(APP_USER_OBJ, appUser)
            return parameters
        } catch (Exception e) {
            log.error(e.getMessage())
            return super.setError(parameters, APP_USER_SAVE_FAILURE_MESSAGE)
        }
    }

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
            return super.setError(previousResult, APP_USER_SAVE_FAILURE_MESSAGE)
        }
    }

    @Transactional(readOnly = true)
    Map executePostCondition(Map previousResult) {
        try {
            AppUser appUser = (AppUser) previousResult.get(APP_USER_OBJ)
            sendActivationMail(appUser, previousResult)              // sent mail for activation
            sendSmsToUser(appUser)                         // send sms to user
            return previousResult
        } catch (Exception e) {
            log.error(e.getMessage())
            return super.setError(previousResult, APP_USER_SAVE_FAILURE_MESSAGE)
        }
    }

    @Override
    Map buildSuccessResultForUI(Map executeResult) {
        return super.setSuccess(executeResult, REGISTRATION_SUCCESS_MSG)
    }

    @Override
    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    private Map getRoleForAppUser(Map params, long companyId) {
        if (params.role) {
            if (params.role.equals(TEACHER)) {
                Role role = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_E_LEARNING_TEACHER)
                if (!role) {
                    return getSysConfigRole(companyId)
                }
                return [role: role, errMsg: null]
            } else if(params.role.equals(STUDENT)) {
                Role role = roleService.findByCompanyIdAndRoleTypeId(companyId, ReservedRoleService.ROLE_TYPE_E_LEARNING_STUDENT)
                if (!role) {
                    return getSysConfigRole(companyId)
                }
                return [role: role, errMsg: null]
            } else {
                return getSysConfigRole(companyId)
            }
        }

    }

    private Map getSysConfigRole(long companyId) {
        SysConfiguration roleId = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.ROLE_ID_FOR_APP_USER_REGISTRATION, companyId)
        Role role = (Role) roleService.read(Long.parseLong(roleId.value.toString()))
        String errMsg = (roleId.message && !roleId.message.equals(EMPTY_SPACE)) ? roleId.message : ROLE_NOT_FOUND
        return [role: role, errMsg: errMsg]
    }

    /**
     * Build appUser object
     * @param params -serialized parameters from UI
     * @return -new appUser object
     */
    private AppUser buildAppUser(Map params, long companyId) {
        AppUser appUser = new AppUser(params)
        SysConfiguration sysConfig = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.DEFAULT_PASSWORD_EXPIRE_DURATION, companyId)
        SystemEntity male = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_GENDER_MALE, appSystemEntityCacheService.SYS_ENTITY_TYPE_GENDER, super.companyId)
        SystemEntity female = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_GENDER_FEMALE, appSystemEntityCacheService.SYS_ENTITY_TYPE_GENDER, super.companyId)
        if (sysConfig) {
            appUser.nextExpireDate = new Date() + Integer.parseInt(sysConfig.value)
        } else {
            appUser.nextExpireDate = new Date()
        }
        if (params.username.equals(EMAIL)) {
            appUser.loginId = appUser.email
        } else if (params.username.equals(PHONE)) {
            appUser.loginId = appUser.cellNumber
        }

        if (params.gender.equals(MALE)) {
            appUser.genderId = male.id
        } else if (params.gender.equals(FEMALE)) {
            appUser.genderId = female.id
        }
        appUser.enabled = Boolean.FALSE
        appUser.companyId = companyId
        appUser.password = springSecurityService.encodePassword(params.password)
        appUser.username = params.firstName
        appUser.activationLink = springSecurityService.encodePassword(appUser.loginId + new Date().toString())
        appUser.createdOn = new Date()
        return appUser
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

        int countEmail = appUserService.countByEmailIlikeAndCompanyId(appUser.email, companyId)
        if (countEmail > 0) {
            return EMAIL_ID_EXISTS_MSG
        }
        count = appUserService.countByLoginIdIlike(appUser.loginId)
        if (count > 0) {
            duplicateErrorMsg = appUser.loginId + NOT_AVAILABLE
            return duplicateErrorMsg
        }
        return duplicateErrorMsg
    }

    private String checkInvalidCellNumber(AppUser appUser) {
        AppCountry bd = appCountryService.findByCodeIlike("BD")
        String cellNumber = appUser.cellNumber
        if (bd) {
            if (cellNumber.startsWith('+88')) {
                cellNumber = cellNumber.substring(3)
                appUser.cellNumber = cellNumber
            } else if(cellNumber.startsWith('88')) {
                cellNumber = cellNumber.substring(2)
                appUser.cellNumber = cellNumber
            }
            if (!cellNumber.matches(bd.phoneNumberPattern)) {
                return INVALID_PHONE_NUMBER
            }
        }
        return null
    }

    private String checkInvalidEmail(AppUser appUser) {
        if (!appUser.email.matches(EMAIL_PATTERN)) {
            return INVALID_EMAIL
        }
        return null
    }

    /**
     * a mail sent to newly created user with activation link
     */
    private boolean sendActivationMail(AppUser appUser, Map params) {
        Company company = (Company) params.get(COMPANY)
        String link = grailsLinkGenerator.link(controller: EL_PORTAL, action: ACTIVATION, absolute: true, params: [link: appUser.activationLink])
//        String link = company.webUrl + SLASH + EL_PORTAL + SLASH + ACTIVATION + "/login/auth#elPortal/showLoginPage?link=" + appUser.activationLink
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
        // get evaluated mail object
        appMail = getAppMail(appUser, appMail, link)
        task { mailSenderService.sendMail(appMail) }
    }

    private boolean sendSmsToUser(AppUser appUser) {
        // check deployment mode
        String deploymentMode = getDeploymentMode(appUser.companyId)
        if (deploymentMode) {
            log.error(deploymentMode)
            return
        }

        AppSms sms = appSmsService.findByTransactionCodeAndCompanyIdAndIsActive(TRANSACTION_CODE, appUser.companyId, true)
        if (!sms) {
            println SMS_TEMPLATE_NOT_FOUND
            return
        }
        task {
            sms.recipients = "+88" + appUser.cellNumber
            smsSenderService.sendSms(sms)
        }
    }

    /**
     * 1. get evaluated mail object
     *
     * @param appUser - an object of AppUser
     * @param appMail - an object of AppMail
     * @param link - a link of user activation
     * @return appMail
     */
    private AppMail getAppMail(AppUser appUser, AppMail appMail, String link) {
        Map parameters = [email: appUser.email, name: appUser.username, link: link]
        appMail.evaluateMailBody(parameters)
        appMail.recipients = appUser.email
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
}
