package com.athena.mis.application.actions.login

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppSysConfigCacheService
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
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
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.apache.log4j.Logger
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

class AuthFailActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    SpringSecurityService springSecurityService
    CompanyService companyService
    AppSysConfigCacheService appSysConfigCacheService

    private static final String SESSION = "session"
    private static final String FAILED_TO_LOGIN = "Failed to login. Try again"
    private static final String DEFAULT_LOGIN_PAGE = "/app/loginPage"
    private static final String PORTAL_LOGIN_PAGE = "/el/loginPage"
    private static final String VIEW = "view"

    public Map executePreCondition(Map params) {
        return params
    }

    public Map execute(Map result) {
        result.put(IS_ERROR, Boolean.TRUE)
        try {
            HttpServletRequest request = (HttpServletRequest) result.request
            Company company = companyService.read(request)
            Map mapResult = getLoginPage(company.id)
            boolean  isPortalView = mapResult.isPortalView
            String view = mapResult.indexUrl
            result.put(VIEW, view)
            result.put("isPortalView", isPortalView)

            HttpSession session = (HttpSession) result.get(SESSION)
            String message = EMPTY_SPACE
            def exception = session[AbstractAuthenticationProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY]
            if (exception) {
                if (exception instanceof AccountExpiredException) {
                    message = SpringSecurityUtils.securityConfig.errors.login.expired
                    result.put(MESSAGE, message)
                    return result
                } else if (exception instanceof CredentialsExpiredException) {
                    message = SpringSecurityUtils.securityConfig.errors.login.passwordExpired
                    result.put(MESSAGE, message)
                    return result
                } else if (exception instanceof DisabledException) {
                    message = SpringSecurityUtils.securityConfig.errors.login.disabled
                    result.put(MESSAGE, message)
                    return result
                } else if (exception instanceof LockedException) {
                    message = SpringSecurityUtils.securityConfig.errors.login.locked
                    result.put(MESSAGE, message)
                    return result
                } else {
                    message = SpringSecurityUtils.securityConfig.errors.login.fail
                    result.put(MESSAGE, message)
                    return result
                }
            }
            result.put(IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(IS_ERROR, Boolean.TRUE)
            result.put(MESSAGE, FAILED_TO_LOGIN)
            return result
        }
    }

    public Map executePostCondition(Map result) {
        return result
    }

    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    public Map buildFailureResultForUI(Map result) {
        return result
    }

    private Map getLoginPage(long companyId) {
        SysConfiguration sysConfiguration = (SysConfiguration) appSysConfigCacheService.readByKeyAndCompanyId(appSysConfigCacheService.DEFAULT_PLUGIN, companyId)
        long pluginId = Long.parseLong(sysConfiguration.value)
        String indexUrl = DEFAULT_LOGIN_PAGE
        boolean isPortalView = false
        switch (pluginId) {
            case ExchangeHousePluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
                    indexUrl = DEFAULT_LOGIN_PAGE
                    isPortalView = false
                }
                break
            case SarbPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(SarbPluginConnector.PLUGIN_NAME)) {
                    indexUrl = DEFAULT_LOGIN_PAGE
                    isPortalView = false
                }
                break
            case ArmsPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)) {
                    indexUrl = DEFAULT_LOGIN_PAGE
                    isPortalView = false
                }
                break
            case DocumentPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
                    indexUrl = DEFAULT_LOGIN_PAGE
                    isPortalView = false
                }
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(DataPipeLinePluginConnector.PLUGIN_NAME)) {
                    indexUrl = DEFAULT_LOGIN_PAGE
                    isPortalView = false
                }
                break
            case PtPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    indexUrl = DEFAULT_LOGIN_PAGE
                    isPortalView = false
                }
                break
            case ProcPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)) {
                    indexUrl = DEFAULT_LOGIN_PAGE
                    isPortalView = false
                }
                break
            case BudgPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
                    indexUrl = DEFAULT_LOGIN_PAGE
                    isPortalView = false
                }
                break
            case AccPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
                    indexUrl = DEFAULT_LOGIN_PAGE
                    isPortalView = false
                }
                break
            case InvPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
                    indexUrl = DEFAULT_LOGIN_PAGE
                    isPortalView = false
                }
                break
            case QsPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(QsPluginConnector.PLUGIN_NAME)) {
                    indexUrl = DEFAULT_LOGIN_PAGE
                    isPortalView = false
                }
                break
            case FxdPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(FxdPluginConnector.PLUGIN_NAME)) {
                    indexUrl = DEFAULT_LOGIN_PAGE
                    isPortalView = false
                }
                break
            case ELearningPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)) {
                    indexUrl = PORTAL_LOGIN_PAGE
                    isPortalView = true
                }
                break
            default:
                indexUrl = DEFAULT_LOGIN_PAGE
                isPortalView = false
        }
        return ["indexUrl": indexUrl, "isPortalView": isPortalView]
    }
}
