package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class CheckRoleTagLibActionService extends BaseService implements ActionServiceIntf {

    SpringSecurityService springSecurityService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService
    @Autowired(required = false)
    ProcPluginConnector procProcurementImplService
    @Autowired(required = false)
    PtPluginConnector ptProjectTrackImplService

    private static final String STR_KEY = 'key'
    private static final String PLUGIN_ID = 'pluginId'
    private static final String NOT_LOGGED = 'not LoggedIn'
    private static final String SYS_CONF_NOT_FOUND = 'SysConfiguration not found'
    private static final String EXCEPTION_OCCURRED = 'some exception occurred'
    private Logger log = Logger.getLogger(getClass())


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
     * @return - a map consisting of desired hasRole value
     */
    public Map execute(Map result) {
        try {
            result.hasRole = Boolean.FALSE
            if (!springSecurityService.isLoggedIn()) {
                return setError(result, NOT_LOGGED)
            }
            String strKey = result.get(STR_KEY)
            String strPluginId = result.get(PLUGIN_ID)
            if ((!strKey) || (strKey.length() == 0) || (!strPluginId) || (strPluginId.length() == 0)) {
                return setError(result, ERROR_FOR_INVALID_INPUT)
            }
            long pluginId = Long.parseLong(strPluginId)
            SysConfiguration sysConfiguration = getSysConfig(strKey, pluginId)
            if (!sysConfiguration) {
                return setError(result, SYS_CONF_NOT_FOUND)
            }
            boolean hasRole = appSessionService.hasRole(sysConfiguration.value)
            result.hasRole = new Boolean(hasRole)
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

    private SysConfiguration getSysConfig(String key, long pluginId) {
        SysConfiguration sysConfiguration = null
        switch (pluginId) {
            case InvPluginConnector.PLUGIN_ID:
                sysConfiguration = invInventoryImplService?.readSysConfiguration(key)
                break
            case AccPluginConnector.PLUGIN_ID:
                sysConfiguration = accAccountingImplService?.readSysConfiguration(key)
                break
            case ProcPluginConnector.PLUGIN_ID:
                sysConfiguration = procProcurementImplService?.readSysConfiguration(key)
                break
            case PtPluginConnector.PLUGIN_ID:
                sysConfiguration = ptProjectTrackImplService?.readSysConfiguration(key)
                break
            default:
                break
        }
        return sysConfiguration
    }
}
