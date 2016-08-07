package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/*Renders the value if sysConfiguration object exists*/

class ShowSysConfigTagLibActionService extends BaseService implements ActionServiceIntf {

    private static final String KEY = 'key'
    private static final String PLUGIN_ID = 'pluginId'
    private static final String COMPANY_ID = 'companyId'

    private Logger log = Logger.getLogger(getClass())

    AppConfigurationService appConfigurationService
    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    SarbPluginConnector sarbImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService

    /**
     * Do nothing for pre condition
     */
    public Map executePreCondition(Map params) {
        String key = params.get(KEY)
        String strPluginId = params.get(PLUGIN_ID)
        if ((!key) || (key.length() == 0) || (!strPluginId) || (strPluginId.length() == 0)) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        return params
    }

    /**
     * Returns value if system configuration exists
     * @param result - a map of given attributes
     * @return - value of sysConfig or null depending on existence of object
     */
    public Map execute(Map result) {
        try {
            String key = result.get(KEY)
            String strPluginId = result.get(PLUGIN_ID)
            String strCompanyId = result.get(COMPANY_ID)

            long companyId
            long pluginId = Long.parseLong(strPluginId)
            if (strCompanyId) {
                // when no access to session
                companyId = Long.parseLong(strCompanyId)
            } else {
                companyId = super.getCompanyId()
            }
            SysConfiguration sysConfiguration = getObjectOfSysConfig(key, pluginId, companyId)
            String html = EMPTY_SPACE
            if (sysConfiguration) {
                html = sysConfiguration.value
            }
            result.html = html
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

    /**
     * get object of sysConfiguration
     * @param key - key of sysConfiguration
     * @param pluginId - plugin id
     * @param companyId -id of company
     * @return - object of sysConfiguration
     */
    private SysConfiguration getObjectOfSysConfig(String key, long pluginId, long companyId) {
        SysConfiguration sysConfiguration
        switch (pluginId) {
            case PluginConnector.PLUGIN_ID:
                sysConfiguration = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(key, companyId)
                break
            case DocumentPluginConnector.PLUGIN_ID:
                sysConfiguration = (SysConfiguration) documentImplService.readSysConfig(key, companyId)
                break
            case ExchangeHousePluginConnector.PLUGIN_ID:
                sysConfiguration = (SysConfiguration) exchangeHouseImplService.readSysConfig(key, companyId)
                break
            case SarbPluginConnector.PLUGIN_ID:
                sysConfiguration = (SysConfiguration) sarbImplService.readSysConfig(key, companyId)
                break
            case ArmsPluginConnector.PLUGIN_ID:
                sysConfiguration = (SysConfiguration) armsImplService.readSysConfig(key, companyId)
                break
            default:
                return null
        }
        return sysConfiguration
    }
}
