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
import com.athena.mis.integration.ictpool.IctPoolPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.integration.qsmeasurement.QsPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import javax.servlet.http.HttpServletRequest

class AuthActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    CompanyService companyService
    SpringSecurityService springSecurityService
    AppSysConfigCacheService appSysConfigCacheService

    private static final String VIEW = "view"
    private static final String IS_PORTAL_VIEW = "isPortalView"
    private static final String CONFIG = "config"
    private static final String POST_URL = "postUrl"
    private static final String COMPANY_ID = "companyId"
    private static final String VIEW_ERROR = "/app/errorPage"
    private static final String VIEW_AUTH = "/application/login/auth"
    private static final String FAILED_TO_LOGIN = "Failed to show the login page. Try again"

    public Map executePreCondition(Map params) {
        try {
            def config = SpringSecurityUtils.securityConfig
            if (springSecurityService.isLoggedIn()) {
                params.put(CONFIG, config)
                params.put(IS_ERROR, Boolean.TRUE)
                return params
            }
            params.put(CONFIG, config)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            params.put(IS_ERROR, Boolean.TRUE)
            params.put(MESSAGE, FAILED_TO_LOGIN)
            return params
        }
    }

    public Map execute(Map result) {
        try {
            def config = result.config
            GrailsParameterMap parameters = (GrailsParameterMap) result
            HttpServletRequest request = (HttpServletRequest) parameters.getRequest()
            Company company = companyService.read(request)
            long companyId = 0L
            String view = null
            boolean isPortalView = false
            if (company != null) {
                companyId = company.id
                Map mapResult = getPluginIndexPage(companyId)
                view = mapResult.indexUrl
                isPortalView = mapResult.isPortalView
            } else {
                view = VIEW_ERROR
            }
            String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"
            result.put(COMPANY_ID, companyId)
            result.put(VIEW, view)
            result.put(IS_PORTAL_VIEW, isPortalView)
            result.put(POST_URL, postUrl)
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
                    indexUrl = "/el/index"
                    return ["indexUrl": indexUrl, "isPortalView": true]
                }
                break
            case IctPoolPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(IctPoolPluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/ictPool/auth"
                    return ["indexUrl": indexUrl, "isPortalView": true]
                }
                break
            default:
                return ["indexUrl": indexUrl, "isPortalView": false]
        }
        return ["indexUrl": indexUrl, "isPortalView": false]
    }
}
