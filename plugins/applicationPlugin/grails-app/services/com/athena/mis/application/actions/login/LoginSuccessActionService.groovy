package com.athena.mis.application.actions.login

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.config.AppSysConfigCacheService
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.*
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpSession

//  check whether fit for logging or not
class LoginSuccessActionService extends BaseService implements ActionServiceIntf {

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
    public static final String PORTAL_VIEW = '/el/loginPage'

    AppUserService appUserService
    SpringSecurityService springSecurityService
    CompanyService companyService
    AppMyFavouriteService appMyFavouriteService
    AppVersionService appVersionService
    ApplicationSessionService applicationSessionService
    AppConfigurationService appConfigurationService
    AppSysConfigCacheService appSysConfigCacheService
    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService
    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    PtPluginConnector ptProjectTrackImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService
    @Autowired(required = false)
    FxdPluginConnector fxdFixedAssetImplService
    @Autowired(required = false)
    BudgPluginConnector budgBudgetImplService
    @Autowired(required = false)
    ProcPluginConnector procProcurementImplService
    @Autowired(required = false)
    QsPluginConnector qsMeasurementImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired(required = false)
    ELearningPluginConnector elearningImplService
    @Autowired(required = false)
    SarbPluginConnector sarbImplService

    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            def config = SpringSecurityUtils.securityConfig
            HttpServletRequest request = (HttpServletRequest) params.request
            HttpServletResponse response = (HttpServletResponse) params.response
            SecurityContextLogoutHandler securityContextLogoutHandler = new SecurityContextLogoutHandler()
            String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"
            params.put(IS_ERROR, Boolean.TRUE)
            params.put(IS_EXPIRED, Boolean.FALSE)
            params.put(POST_URL, postUrl)
            params.put(CONFIG, config)

            def userDetails = springSecurityService.principal
            AppUser appUser = (AppUser) appUserService.read(userDetails.id)
            params.put(APP_USER, appUser)
            String strView = getView(params)

            // check url
            String message = checkServerName(request, appUser)
            if (message) {
                securityContextLogoutHandler.logout(request, response, null)
                params.put(VIEW, strView)
                params.put(MESSAGE, message)
                return params
            }
            // check IP address(if any)
            message = checkIP(request, appUser)
            if (message) {
                params.put(VIEW, strView)
                params.put(MESSAGE, message)
                return params
            }
            // check application maintenance mode
            SysConfiguration sysConfig = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.IS_MAINTENANCE_MODE, appUser.companyId)
            if (sysConfig) {
                boolean isDisableLogin = Boolean.parseBoolean(sysConfig.value)
                if (isDisableLogin && !appUser.isConfigManager) {
                    params.put(VIEW, strView)
                    params.put(MESSAGE, sysConfig.message)
                    return params
                }
            }
            // check password expire date
            Map userInfoMap = checkExpireDate(appUser)
            if (userInfoMap) {
                securityContextLogoutHandler.logout(request, response, null)
                params.put(VIEW, VIEW_SHOW_RESET_PASS)
                params.put(IS_EXPIRED, Boolean.TRUE)
                params.put(APP_USER_MAP, userInfoMap)
                return params
            }
            params.put(VIEW, strView)
            params.put(REQUEST, request)
            params.put(RESPONSE, response)
            params.put(IS_ERROR, Boolean.FALSE)
            params.put(SECURITY_CONTEXT_LOGOUT_HANDLER, securityContextLogoutHandler)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            params.put(IS_ERROR, Boolean.TRUE)
            params.put(MESSAGE, FAILED_TO_LOGIN)
            return params
        }
    }

    public Map execute(Map result) {
        SecurityContextLogoutHandler securityContextLogoutHandler = (SecurityContextLogoutHandler) result.get(SECURITY_CONTEXT_LOGOUT_HANDLER)
        HttpServletRequest request = (HttpServletRequest) result.get(REQUEST)
        HttpServletResponse response = (HttpServletResponse) result.get(RESPONSE)
        try {
            result.put(IS_ERROR, Boolean.TRUE)
            AppUser user = (AppUser) result.get(APP_USER)
            HttpSession session = (HttpSession) result.get(SESSION)
            result.put(DEFAULT_URL, SLASH)

            if (appSessionService.appUser) {   // check if session already exists
                result.put(IS_ERROR, Boolean.FALSE)
                return result
            }
            // add user session object to session map
            applicationSessionService.init(user, request, session)
            // init appSessionService
            appSessionService.init(user, session)
            // check user role mapping
            String validationMsg = checkUserRole()
            if (validationMsg) {
                securityContextLogoutHandler.logout(request, response, null)
                result.put(MESSAGE, validationMsg)
                return result
            }
            // check application version
            validationMsg = checkApplicationVersion()
            if (validationMsg) {
                securityContextLogoutHandler.logout(request, response, null)
                result.put(MESSAGE, validationMsg)
                return result
            }
            result.put(IS_ERROR, Boolean.FALSE)
            result.put(DEFAULT_URL, getDefaultUrl(appUser))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            securityContextLogoutHandler.logout(request, response, null)
            result.put(IS_ERROR, Boolean.TRUE)
            result.put(MESSAGE, FAILED_TO_LOGIN)
            return result
        }

    }

    private String checkIP(HttpServletRequest request, AppUser appUser) {
        if (appUser.ipAddress && (appUser.ipAddress.length() > 0)) {
            String remoteIP = request.getRemoteAddr()
            if (!appUser.ipAddress.equals(remoteIP)) {
                return USER_IP_MIS_MATCH
            }
        }
        return null
    }

    private static final String HTTP_SLASH = 'http://'

    private String checkServerName(HttpServletRequest request, AppUser appUser) {
        Company company = (Company) companyService.read(appUser.companyId)

        String requestUrl = request.getServerName()
        if (request.getServerPort() > 80) {
            requestUrl = requestUrl + COLON + request.getServerPort()
        }

        String urlWithHttp = HTTP_SLASH + requestUrl;

        if (!requestUrl.equalsIgnoreCase(company.webUrl) && !urlWithHttp.equalsIgnoreCase(company.webUrl)) {
            return INVALID_ID_PASS
        }
        if (!company.isActive) {
            return NOT_AUTHORIZED
        }
        return null
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

    private Map checkExpireDate(AppUser user) {
        Date currentDate = new Date()
        if ((!user.isDisablePasswordExpiration) && (currentDate > user.nextExpireDate)) {
            Map userInfoMap = [
                    userName  : user.username,
                    userId    : user.id,
                    expireDate: DateUtility.getDateFormatAsString(user.nextExpireDate)
            ]
            return userInfoMap
        }
        return null
    }

    /**
     * 1. check if user has any role
     * @return -a string containing error message or null value depending on user role mapping
     */
    private String checkUserRole() {
        // check if user has any role
        if (appSessionService.getUserRoleIds().size() == 0) {
            return USER_HAS_NO_ROLE
        }
        return null
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

    /**
     * check application version
     * @return - message or null value depending on version check
     */
    private String checkApplicationVersion() {
        AppVersion appVersion = appVersionService.findByPluginIdAndIsCurrent(PluginConnector.PLUGIN_ID, true)
        Date validDate = appVersion.releasedOn + appVersion.span
        SysConfiguration enforceReleaseVersion = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.ENFORCE_RELEASE_VERSION, companyId)
        boolean isEnforced =false// Boolean.parseBoolean(enforceReleaseVersion.value)
        if ((validDate < new Date()) && isEnforced) {
            return enforceReleaseVersion.message
        }
        int configCurrentVersion = appConfigurationService.getAppPluginVersion()
        int dbCurrentVersion = appVersion.releaseNo
        if (dbCurrentVersion > configCurrentVersion) {
            return "Back-dated Plugin (Application) Deployed. Plugin ver. = ${configCurrentVersion} & DB ver. = ${dbCurrentVersion}"
        }
        if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
            appVersion = appVersionService.findByPluginIdAndIsCurrent(AccPluginConnector.PLUGIN_ID, true)
            configCurrentVersion = accAccountingImplService.getPluginVersion()
            dbCurrentVersion = appVersion.releaseNo
            if (dbCurrentVersion > configCurrentVersion) {
                return "Back-dated Plugin (Accounting) Deployed. Plugin ver. = ${configCurrentVersion} & DB ver. = ${dbCurrentVersion}"
            }
        }
        if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
            appVersion = appVersionService.findByPluginIdAndIsCurrent(BudgPluginConnector.PLUGIN_ID, true)
            configCurrentVersion = budgBudgetImplService.getPluginVersion()
            dbCurrentVersion = appVersion.releaseNo
            if (dbCurrentVersion > configCurrentVersion) {
                return "Back-dated Plugin (Budget) Deployed. Plugin ver. = ${configCurrentVersion} & DB ver. = ${dbCurrentVersion}"
            }
        }
        if (PluginConnector.isPluginInstalled(FxdPluginConnector.PLUGIN_NAME)) {
            appVersion = appVersionService.findByPluginIdAndIsCurrent(FxdPluginConnector.PLUGIN_ID, true)
            configCurrentVersion = fxdFixedAssetImplService.getPluginVersion()
            dbCurrentVersion = appVersion.releaseNo
            if (dbCurrentVersion > configCurrentVersion) {
                return "Back-dated Plugin (Fixed Asset) Deployed. Plugin ver. = ${configCurrentVersion} & DB ver. = ${dbCurrentVersion}"
            }
        }
        if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
            appVersion = appVersionService.findByPluginIdAndIsCurrent(InvPluginConnector.PLUGIN_ID, true)
            configCurrentVersion = invInventoryImplService.getPluginVersion()
            dbCurrentVersion = appVersion.releaseNo
            if (dbCurrentVersion > configCurrentVersion) {
                return "Back-dated Plugin (Inventory) Deployed. Plugin ver. = ${configCurrentVersion} & DB ver. = ${dbCurrentVersion}"
            }
        }
        if (PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)) {
            appVersion = appVersionService.findByPluginIdAndIsCurrent(ProcPluginConnector.PLUGIN_ID, true)
            configCurrentVersion = procProcurementImplService.getPluginVersion()
            dbCurrentVersion = appVersion.releaseNo
            if (dbCurrentVersion > configCurrentVersion) {
                return "Back-dated Plugin (Procurement) Deployed. Plugin ver. = ${configCurrentVersion} & DB ver. = ${dbCurrentVersion}"
            }
        }
        if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
            appVersion = appVersionService.findByPluginIdAndIsCurrent(PtPluginConnector.PLUGIN_ID, true)
            configCurrentVersion = ptProjectTrackImplService.getPluginVersion()
            dbCurrentVersion = appVersion.releaseNo
            if (dbCurrentVersion > configCurrentVersion) {
                return "Back-dated Plugin (Project Track) Deployed. Plugin ver. = ${configCurrentVersion} & DB ver. = ${dbCurrentVersion}"
            }
        }
        if (PluginConnector.isPluginInstalled(QsPluginConnector.PLUGIN_NAME)) {
            appVersion = appVersionService.findByPluginIdAndIsCurrent(QsPluginConnector.PLUGIN_ID, true)
            configCurrentVersion = qsMeasurementImplService.getPluginVersion()
            dbCurrentVersion = appVersion.releaseNo
            if (dbCurrentVersion > configCurrentVersion) {
                return "Back-dated Plugin (QS) Deployed. Plugin ver. = ${configCurrentVersion} & DB ver. = ${dbCurrentVersion}"
            }
        }
        if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
            appVersion = appVersionService.findByPluginIdAndIsCurrent(ExchangeHousePluginConnector.PLUGIN_ID, true)
            configCurrentVersion = exchangeHouseImplService.getPluginVersion()
            dbCurrentVersion = appVersion.releaseNo
            if (dbCurrentVersion > configCurrentVersion) {
                return "Back-dated Plugin (Exchange House) Deployed. Plugin ver. = ${configCurrentVersion} & DB ver. = ${dbCurrentVersion}"
            }
        }
        if (PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)) {
            appVersion = appVersionService.findByPluginIdAndIsCurrent(ArmsPluginConnector.PLUGIN_ID, true)
            configCurrentVersion = armsImplService.getPluginVersion()
            dbCurrentVersion = appVersion.releaseNo
            if (dbCurrentVersion > configCurrentVersion) {
                return "Back-dated Plugin (ARMS) Deployed. Plugin ver. = ${configCurrentVersion} & DB ver. = ${dbCurrentVersion}"
            }
        }
        if (PluginConnector.isPluginInstalled(SarbPluginConnector.PLUGIN_NAME)) {
            appVersion = appVersionService.findByPluginIdAndIsCurrent(SarbPluginConnector.PLUGIN_ID, true)
            configCurrentVersion = sarbImplService.getPluginVersion()
            dbCurrentVersion = appVersion.releaseNo
            if (dbCurrentVersion > configCurrentVersion) {
                return "Back-dated Plugin (SARB) Deployed. Plugin ver. = ${configCurrentVersion} & DB ver. = ${dbCurrentVersion}"
            }
        }
        if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
            appVersion = appVersionService.findByPluginIdAndIsCurrent(DocumentPluginConnector.PLUGIN_ID, true)
            configCurrentVersion = documentImplService.getPluginVersion()
            dbCurrentVersion = appVersion.releaseNo
            if (dbCurrentVersion > configCurrentVersion) {
                return "Back-dated Plugin (Document) Deployed. Plugin ver. = ${configCurrentVersion} & DB ver. = ${dbCurrentVersion}"
            }
        }
        if (PluginConnector.isPluginInstalled(DataPipeLinePluginConnector.PLUGIN_NAME)) {
            appVersion = appVersionService.findByPluginIdAndIsCurrent(DataPipeLinePluginConnector.PLUGIN_ID, true)
            configCurrentVersion = dataPipeLineImplService.getPluginVersion()
            dbCurrentVersion = appVersion.releaseNo
            if (dbCurrentVersion > configCurrentVersion) {
                return "Back-dated Plugin (Data Pipe Line) Deployed. Plugin ver. = ${configCurrentVersion} & DB ver. = ${dbCurrentVersion}"
            }
        }
        if (PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)) {
            appVersion = appVersionService.findByPluginIdAndIsCurrent(ELearningPluginConnector.PLUGIN_ID, true)
            configCurrentVersion = elearningImplService.getPluginVersion()
            dbCurrentVersion = appVersion.releaseNo
            if (dbCurrentVersion > configCurrentVersion) {
                return "Back-dated Plugin (E-Learning) Deployed. Plugin ver. = ${configCurrentVersion} & DB ver. = ${dbCurrentVersion}"
            }
        }
        return null
    }

    private String getView(Map params) {
        HttpServletRequest request = (HttpServletRequest) params.request
        Company company = companyService.read(request)
        params.put(COMPANY_ID, company.id)
        SysConfiguration sysConfiguration = (SysConfiguration) appSysConfigCacheService.readByKeyAndCompanyId(appSysConfigCacheService.DEFAULT_PLUGIN, company.id)
        long pluginId = Long.parseLong(sysConfiguration.value)
        String strView = VIEW_AUTH
        switch (pluginId) {
            case ExchangeHousePluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
                    strView = VIEW_AUTH
                }
                break
            case SarbPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(SarbPluginConnector.PLUGIN_NAME)) {
                    strView = VIEW_AUTH
                }
                break
            case ArmsPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)) {
                    strView = VIEW_AUTH
                }
                break
            case DocumentPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
                    strView = VIEW_AUTH
                }
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(DataPipeLinePluginConnector.PLUGIN_NAME)) {
                    strView = VIEW_AUTH
                }
                break
            case PtPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    strView = VIEW_AUTH
                }
                break
            case ProcPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)) {
                    strView = VIEW_AUTH
                }
                break
            case BudgPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
                    strView = VIEW_AUTH
                }
                break
            case AccPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
                    strView = VIEW_AUTH
                }
                break
            case InvPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
                    strView = VIEW_AUTH
                }
                break
            case QsPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(QsPluginConnector.PLUGIN_NAME)) {
                    strView = VIEW_AUTH
                }
                break
            case FxdPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(FxdPluginConnector.PLUGIN_NAME)) {
                    strView = VIEW_AUTH
                }
                break
            case ELearningPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)) {
                    strView = PORTAL_VIEW
                }
                break
            default:
                strView = VIEW_AUTH
        }
        return strView
    }
}
