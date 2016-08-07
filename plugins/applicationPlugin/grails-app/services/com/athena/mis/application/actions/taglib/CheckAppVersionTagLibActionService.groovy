package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppVersion
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.AppVersionService
import com.athena.mis.application.service.CompanyService
import org.apache.log4j.Logger

import javax.servlet.http.HttpServletRequest

/**
 * Show message depending on version check
 */
class CheckAppVersionTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    AppVersionService appVersionService
    CompanyService companyService
    AppConfigurationService appConfigurationService

    public Map executePreCondition(Map params) {
        try {
            if (!params.pluginId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            return params
        } catch (Exception e) {
            log.error(e.message)
            throw new RuntimeException(e)
        }
    }

    public Map execute(Map result) {
        try {
            int pluginId = Integer.parseInt(result.pluginId.toString())
            String html = EMPTY_SPACE
            AppVersion appVersion = appVersionService.findByPluginIdAndIsCurrent(pluginId, true)
            Date validDate = appVersion.releasedOn + appVersion.span
            Date cautionDate = validDate - 7
            HttpServletRequest request = (HttpServletRequest) result.request
            long companyId
            if (result.companyId) {
                companyId = Long.parseLong(result.companyId.toString())
            } else {
                companyId = getCompanyId(request)
            }
            if (validDate < new Date()) {
                SysConfiguration sysConfig = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.ENFORCE_RELEASE_VERSION, companyId)
                if (sysConfig) {
                    html = sysConfig.message ? sysConfig.message : '<span style="color: green">' +
                            'New updates are available.<br>' +
                            '</span>' +
                            '<span style="color: red">' +
                            'Please update your application to avoid unexpected security threat.' +
                            '</span>'
                }
            } else if (cautionDate < new Date()) {
                html = '<span style="color: green">' +
                        'New updates will be available soon!<br>' +
                        '</span>' +
                        '<span style="color: red">' +
                        'Please update your application to avoid unexpected security threat.' +
                        '</span>'
            }
            result.html = html
            return result
        } catch (Exception e) {
            log.error(e.message)
            throw new RuntimeException(e)
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

    /**
     * get company id
     * @param request - HttpServletRequest
     * @return - companyId of given url
     */
    private long getCompanyId(HttpServletRequest request) {
        Company company = companyService.read(request)
        return company ? company.id : 0L
    }
}
