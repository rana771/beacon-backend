package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.CompanyService
import com.athena.mis.application.service.SysConfigurationService
import org.apache.log4j.Logger

import javax.servlet.http.HttpServletRequest

/**
 * check sys configuration value
 * For details go through use-case doc named "CheckExhSysConfigTagLibActionService"
 */
class CheckSysConfigTagLibActionService extends BaseService implements ActionServiceIntf {

    private static final String KEY = 'key'
    private static final String VALUE = 'value'
    private static final String PLUGIN_ID = 'pluginId'
    private static final String EXCEPTION_OCCURRED = 'some exception occurred'
    private static final String SYS_CONF_NOT_FOUND = 'SysConfiguration not found'

    private Logger log = Logger.getLogger(getClass())

    SysConfigurationService sysConfigurationService
    CompanyService companyService

    /**
     * Do nothing here
     * @param params - a map with some necessary parameter
     * @return - returned the same map
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * @param result - a map returned from precondition method
     * @return - a map consisting hasSysConf value
     */
    public Map execute(Map result) {
        try {
            result.hasSysConf = Boolean.FALSE
            String key = result.get(KEY)
            String value = result.get(VALUE)
            int pluginId = Integer.parseInt(result.get(PLUGIN_ID).toString())
            if ((!key) || (key.length() == 0) || (!value) || (value.length() == 0) || (pluginId == 0)) {
                return setError(result, ERROR_FOR_INVALID_INPUT)
            }
            HttpServletRequest request = (HttpServletRequest) result.request
            Company company = (Company) companyService.read(request)
            long companyId = company.id
            SysConfiguration sysConfiguration = sysConfigurationService.readByKeyAndPluginIdAndCompanyId(key, pluginId, companyId)
            if (!sysConfiguration) {
                return setError(result, SYS_CONF_NOT_FOUND)
            }
            if (!sysConfiguration.value.equals(value)) {
                return setError(result, SYS_CONF_NOT_FOUND)
            }
            result.hasSysConf = Boolean.TRUE
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
}
