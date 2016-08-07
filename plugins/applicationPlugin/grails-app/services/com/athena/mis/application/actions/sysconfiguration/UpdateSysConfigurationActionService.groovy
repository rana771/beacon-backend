package com.athena.mis.application.actions.sysconfiguration

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.SysConfigurationService
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update SysConfiguration object and to show on grid list
 *  For details go through Use-Case doc named 'UpdateSysConfigurationActionService'
 */
class UpdateSysConfigurationActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    SysConfigurationService sysConfigurationService
    AppConfigurationService appConfigurationService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService
    @Autowired(required = false)
    ProcPluginConnector procProcurementImplService
    @Autowired(required = false)
    PtPluginConnector ptProjectTrackImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    SarbPluginConnector sarbImplService
    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService
    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    ELearningPluginConnector elearningImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService

    private static final String UPDATE_SUCCESS_MESSAGE = "System Configuration Information has been updated successfully"
    private static final String NOT_FOUND_MASSAGE = "System-Configuration not found or changed by someone"
    private static final String SYS_CONFIGURATION = "systemConfiguration"

    /**
     * 1. check input validation
     * 2. check existence of old SysConfiguration object
     * 3. build sysConfig object
     * @param params -serialized parameters from UI
     * @return -a map containing SysConfiguration object
     */
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.id || !params.version) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            int sysConfigId = Integer.parseInt(params.id.toString())
            int version = Integer.parseInt(params.version.toString())
            SysConfiguration oldSysConfig = sysConfigurationService.read(sysConfigId)
            if (!oldSysConfig || oldSysConfig.version != version) {
                return super.setError(params, NOT_FOUND_MASSAGE)
            }
            SysConfiguration sysConfig = buildSysConfigObject(params, oldSysConfig) // build sysConfig object
            params.put(SYS_CONFIGURATION, sysConfig)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * update SysConfiguration object in DB as well as in cache
     * @param result - map received from pre execute method
     * @return - received map
     */
    @Transactional
    public Map execute(Map result) {
        try {
            SysConfiguration newSysConfig = (SysConfiguration) result.get(SYS_CONFIGURATION)
            sysConfigurationService.update(newSysConfig)
            updateCacheUtility(newSysConfig) //update cache
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * return the same map as received
     * @param result - resulting map from execute
     * @return - same map received from previous method
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * set success message
     * @param result - resulting map from post execute
     * @return - same map received from previous method
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
    }

    /**
     * return the same map as received
     * @param result - resulting map from previous method
     * @return - same map received from previous method
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build sysConfig object for update
     * @param params - serialized parameters from UI
     * @param oldSysConfig - old sysConfig object
     * @return - updated sysConfig object
     */
    private SysConfiguration buildSysConfigObject(Map params, SysConfiguration oldSysConfig) {
        SysConfiguration newSysConfig = new SysConfiguration(params)
        oldSysConfig.value = newSysConfig.value
        oldSysConfig.message = newSysConfig.message
        oldSysConfig.updatedOn = new Date()
        oldSysConfig.updatedBy = super.appUser.id
        return oldSysConfig
    }

    /**
     * Init cache utility of a specific plugin
     * @param sysConfiguration -SysConfiguration
     */
    private void updateCacheUtility(SysConfiguration sysConfiguration) {
        // check and re-init sysConfig cache
        switch (sysConfiguration.pluginId) {
            case PluginConnector.PLUGIN_ID:
                appConfigurationService.appSysConfigCacheService.init()
                break
            case InvPluginConnector.PLUGIN_ID:
                invInventoryImplService?.initInvSysConfiguration()
                break
            case AccPluginConnector.PLUGIN_ID:
                accAccountingImplService?.initAccSysConfiguration()
                break
            case ProcPluginConnector.PLUGIN_ID:
                procProcurementImplService?.initProcSysConfiguration()
                break
            case PtPluginConnector.PLUGIN_ID:
                ptProjectTrackImplService?.initPtSysConfiguration()
                break
            case ExchangeHousePluginConnector.PLUGIN_ID:
                exchangeHouseImplService?.initExhSysConfigCacheUtility()
                break
            case SarbPluginConnector.PLUGIN_ID:
                sarbImplService?.initSarbSysConfiguration()
                break
            case DocumentPluginConnector.PLUGIN_ID:
                documentImplService?.initDocumentSysConfigCacheService(sysConfiguration.key)
                break
            case ELearningPluginConnector.PLUGIN_ID:
                elearningImplService?.initElearningSysConfigCacheService(sysConfiguration.key)
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                dataPipeLineImplService?.initDataPipeLineSysConfigCacheService(sysConfiguration.key)
                break
            case ArmsPluginConnector.PLUGIN_ID:
                armsImplService?.initSysConfigCacheUtility()
                break
            default:
                break
        }
    }
}
