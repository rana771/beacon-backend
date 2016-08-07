package com.athena.mis.application.actions.login

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppMyFavourite
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppVersion
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.AppVersionService
import com.athena.mis.application.service.ApplicationSessionService
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
import grails.plugin.springsecurity.SpringSecurityUtils
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

class SwitchUserLoginActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String FAILED_TO_LOGIN = "Failed to login. Try again"
    private static final String APP_USER = "appUser"
    private static final String APP_USER_MAP = "userInfoMap"
    private static final String REQUEST = "request"
    private static final String RESPONSE = "response"
    private static final String VIEW = "view"
    private static final String CONFIG = "config"
    private static final String SESSION = "session"
    private static final String USER_HAS_NO_ROLE = "User has no role."
    private static final String DEFAULT_URL = "defaultUrl"
    private static final String USER_IP_MIS_MATCH = "User IP address did not match."
    private static final String INVALID_ID_PASS = "Invalid ID or Password."
    private static final String IS_EXPIRED = "isExpired"
    private static
    final String NOT_AUTHORIZED = "You are not authorized to log in. Please contact with your administrator."
    private static final String COMPANY_ID = "companyId"
    private static final String POST_URL = "postUrl"
    private static final String SECURITY_CONTEXT_LOGOUT_HANDLER = "securityContextLogoutHandler"
    private static final String VIEW_SHOW_RESET_PASS = '/application/login/showResetPassword'
    private static final String VIEW_AUTH = "/application/login/auth"

    AppUserService appUserService
    SpringSecurityService springSecurityService
    CompanyService companyService
    AppMyFavouriteService appMyFavouriteService
    AppVersionService appVersionService
    ApplicationSessionService applicationSessionService
    AppConfigurationService appConfigurationService

    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            params.put(IS_ERROR, Boolean.TRUE)
            params.put(IS_EXPIRED, Boolean.FALSE)

            def userDetails = springSecurityService.principal
            AppUser appUser = appUserService.read(userDetails.id)
            params.put(APP_USER, appUser)
            params.put(COMPANY_ID, appUser.companyId)
            params.put(IS_ERROR, Boolean.FALSE)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            params.put(IS_ERROR, Boolean.TRUE)
            params.put(MESSAGE, FAILED_TO_LOGIN)
            return params
        }
    }

    public Map execute(Map result) {
        HttpServletRequest request = (HttpServletRequest) result.get(REQUEST)
        try {
            result.put(IS_ERROR, Boolean.TRUE)
            AppUser user = (AppUser) result.get(APP_USER)
            HttpSession session = (HttpSession) result.get(SESSION)
            result.put(VIEW, VIEW_AUTH)

            println session.id

            // add user session object to session map
            applicationSessionService.init(user, request, session)
            // init appSessionService
            appSessionService.init(user, session)
            result.put(IS_ERROR, Boolean.FALSE)
            result.put(DEFAULT_URL, getDefaultUrl(user))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(IS_ERROR, Boolean.TRUE)
            result.put(MESSAGE, FAILED_TO_LOGIN)
            return result
        }

    }

    public Map executePostCondition(Map result) {
        result.remove(REQUEST)
        result.remove(SESSION)
        return result
    }

    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Get post login url from favourite list
     * 1. check if user has any default url
     * 2. if not, check if user has any favourite list (pick last one)
     * 3. otherwise render the root page
     * @param appUser
     * @return - the default url to redirect
     */
    private String getDefaultUrl(AppUser appUser) {
        String url = SLASH
        AppMyFavourite favourite = appMyFavouriteService.findByUserIdAndIsDefault(appUser.id, true)
        if (favourite) {
            return "/#${favourite.url}"
        }
        return url
    }
}
