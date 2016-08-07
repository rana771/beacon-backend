package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppTheme
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppThemeService
import com.athena.mis.application.service.CompanyService
import org.apache.log4j.Logger

import javax.servlet.http.HttpServletRequest

class GetThemeContentTagLibActionService extends BaseService implements ActionServiceIntf {

    AppThemeService appThemeService
    CompanyService companyService

    private static final String STR_NAME = 'name'
    private static final String STR_CSS = 'css'
    private static final String PLUGIN_ID = 'plugin_id'
    private static final String STYLE_TAG_START = "<style>\n"
    private static final String STYLE_TAG_END = "\n</style>"
    private static final String HTML = "html"
    private static final String DIV_TAG_END = "</div>"
    private static final String COMPANY_ID = "companyId"
    private static final String ID = "id"
    private static final String URL = "url"

    private Logger log = Logger.getLogger(getClass())

    /**
     * check required parameter
     * @param params - parameter from UI
     * @return - a map consisting necessary information
     */
    public Map executePreCondition(Map params) {
        String name = params.get(STR_NAME)
        if (!name) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        HttpServletRequest request = (HttpServletRequest) params.request
        long companyId
        if (params.companyId) {
            companyId = Long.parseLong(params.companyId.toString())
        } else {
            companyId = getCompanyId(request)
        }
        params.put(COMPANY_ID, companyId)
        return params
    }

    /**
     * get value of theme
     * @param result
     * @return
     */
    public Map execute(Map result) {
        try {
            String html = EMPTY_SPACE
            long companyId = ((Long) result.get(COMPANY_ID)).longValue()
            if (companyId == 0L) {
                result.put(HTML, html)
                return result
            }
            html = getThemeHtml(result)
            result.put(HTML, html)
            return result
        } catch (Exception e) {
            log.error(e.message)
            throw new RuntimeException(e)
        }
    }

    /**
     * Do nothing in post condition
     * @param result - A map returned by execute method
     * @return - returned the received map
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * do nothing for build success operation
     * @param result - A map returned by post condition method.
     * @return - returned the same received map containing isError = false
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Do nothing here
     * @param result - map returned from previous any of method
     * @return - a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    private long getCompanyId(HttpServletRequest request) {
        AppUser appUser = super.getAppUser()
        if (appUser) {
            return appUser.companyId
        }
        Company company = companyService.read(request)
        return company ? company.id : 0L
    }

    private String getThemeHtml(Map result) {
        String id = result.get(ID)
        String url = result.get(URL)
        String name = result.get(STR_NAME)
        long companyId = (long) result.get(COMPANY_ID)
        int pluginId = Integer.parseInt(result.get(PLUGIN_ID).toString())
        String html = EMPTY_SPACE
        boolean isCss = false   // default value
        if (result.css) {
            isCss = Boolean.parseBoolean(result.get(STR_CSS).toString())
        }

        if (id && url) {
            return getThemeValueForReload(id, url, name, companyId, pluginId, isCss)
        } else {
            AppTheme theme = getTheme(name, pluginId, companyId)
            if (!theme) {
                return html
            }
            // now check if CSS content
            if (!isCss) {
                return theme.value
            } else {
                // enclose style tag
                return STYLE_TAG_START + theme.value + STYLE_TAG_END
            }
        }
    }

    private String getThemeValueForReload(String id, String url, String name, long companyId, int pluginId, boolean isCss) {
        String html = """<div id='${id}' url='${url}' name='${name}'>"""
        AppTheme theme = getTheme(name, pluginId, companyId)
        if (!theme) {
            return html + DIV_TAG_END
        }
        // now check if CSS content
        if (!isCss) {
            return html + theme.value + DIV_TAG_END
        } else {
            // enclose style tag
            return STYLE_TAG_START + theme.value + STYLE_TAG_END
        }
    }

    private AppTheme getTheme(String name, int pluginId, long companyId) {
        AppTheme theme
        if (PluginConnector.PLUGIN_ID != pluginId) {
            theme = appThemeService.findByKeyAndPluginAndCompanyId(name, pluginId, companyId)
        }
        if (theme == null) {
            theme = appThemeService.findByKeyAndPluginAndCompanyId(name, PluginConnector.PLUGIN_ID, companyId)
        }
        return theme
    }
}
