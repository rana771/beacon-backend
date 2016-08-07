package com.athena.mis.application.actions.sysconfiguration

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppConfigurationService
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
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get list of SysConfiguration object(s) to show on grid
 *  For details go through Use-Case doc named 'ListSysConfigurationActionService'
 */
class ListSysConfigurationActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    AppConfigurationService appConfigurationService
    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService
    @Autowired(required = false)
    ProcPluginConnector procProcurementImplService
    @Autowired(required = false)
    PtPluginConnector ptProjectTrackImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired(required = false)
    SarbPluginConnector sarbImplService
    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService
    @Autowired(required = false)
    BudgPluginConnector budgBudgetImplService
    @Autowired(required = false)
    FxdPluginConnector fxdFixedAssetImplService
    @Autowired(required = false)
    QsPluginConnector qsMeasurementImplService
    @Autowired(required = false)
    ELearningPluginConnector elearningImplService

    /**
     *
     * @param params - serialized parameters from UI
     * @return - a map containing plugin id
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * set plugin id
     * pull system-configuration list & count
     * @param params - map received from pre execute method
     * @return -a map containing sysConfiguration list & count
     */
    @Transactional(readOnly = true)
    public Map execute(Map params) {
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            super.initListing(parameterMap)
            Map resultMap
            int pluginId = Integer.parseInt(params.pluginId.toString())
            boolean isFiltering = super.isFiltering()
            if (isFiltering) {
                resultMap = getSearchListForGrid(pluginId)
            } else {
                resultMap = getListForGrid(pluginId)
            }
            params.put(LIST, resultMap.list)
            params.put(COUNT, resultMap.count)
            return params
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
     * return the same map as received
     * @param result - resulting map from post execute
     * @return - same map received from previous method
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * return the same map as received
     * @param result - resulting map from previous method
     * @return - same map received from previous method
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    // get list for grid
    private Map getListForGrid(int pluginId) {
        Map result = [list: [], count: 0]
        long companyId = super.getCompanyId()
        switch (pluginId) {
            case PluginConnector.PLUGIN_ID:
                result = appConfigurationService.appSysConfigCacheService.listForGrid(this, companyId)
                break
            case DocumentPluginConnector.PLUGIN_ID:
                result = documentImplService.sysConfigListForGrid(this, companyId)
                break
            case ELearningPluginConnector.PLUGIN_ID:
                result = elearningImplService.sysConfigListForGrid(this, companyId)
                break
            case AccPluginConnector.PLUGIN_ID:
                result = accAccountingImplService.sysConfigListForGrid(this, companyId)
                break
            case ArmsPluginConnector.PLUGIN_ID:
                result = armsImplService.sysConfigListForGrid(this, companyId)
                break
            case BudgPluginConnector.PLUGIN_ID:
                result = budgBudgetImplService.sysConfigListForGrid(this, companyId)
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                result = dataPipeLineImplService.sysConfigListForGrid(this, companyId)
                break
            case ExchangeHousePluginConnector.PLUGIN_ID:
                result = exchangeHouseImplService.sysConfigListForGrid(this, companyId)
                break
            case FxdPluginConnector.PLUGIN_ID:
                result = fxdFixedAssetImplService.sysConfigListForGrid(this, companyId)
                break
            case InvPluginConnector.PLUGIN_ID:
                result = invInventoryImplService.sysConfigListForGrid(this, companyId)
                break
            case ProcPluginConnector.PLUGIN_ID:
                result = procProcurementImplService.sysConfigListForGrid(this, companyId)
                break
            case PtPluginConnector.PLUGIN_ID:
                result = ptProjectTrackImplService.sysConfigListForGrid(this, companyId)
                break
            case QsPluginConnector.PLUGIN_ID:
                result = qsMeasurementImplService.sysConfigListForGrid(this, companyId)
                break
            case SarbPluginConnector.PLUGIN_ID:
                result = sarbImplService.sysConfigListForGrid(this, companyId)
                break
        }
        return result
    }

    // get list for grid search
    private Map getSearchListForGrid(int pluginId) {
        Map result = [list: [], count: 0]
        long companyId = super.getCompanyId()
        switch (pluginId) {
            case PluginConnector.PLUGIN_ID:
                result = appConfigurationService.appSysConfigCacheService.searchForGrid(this, companyId)
                break
            case DocumentPluginConnector.PLUGIN_ID:
                result = documentImplService.sysConfigSearchListForGrid(this, companyId)
                break
            case AccPluginConnector.PLUGIN_ID:
                result = accAccountingImplService.sysConfigSearchListForGrid(this, companyId)
                break
            case ArmsPluginConnector.PLUGIN_ID:
                result = armsImplService.sysConfigSearchListForGrid(this, companyId)
                break
            case BudgPluginConnector.PLUGIN_ID:
                result = budgBudgetImplService.sysConfigSearchListForGrid(this, companyId)
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                result = dataPipeLineImplService.sysConfigSearchListForGrid(this, companyId)
                break
            case ExchangeHousePluginConnector.PLUGIN_ID:
                result = exchangeHouseImplService.sysConfigSearchListForGrid(this, companyId)
                break
            case FxdPluginConnector.PLUGIN_ID:
                result = fxdFixedAssetImplService.sysConfigSearchListForGrid(this, companyId)
                break
            case InvPluginConnector.PLUGIN_ID:
                result = invInventoryImplService.sysConfigSearchListForGrid(this, companyId)
                break
            case ProcPluginConnector.PLUGIN_ID:
                result = procProcurementImplService.sysConfigSearchListForGrid(this, companyId)
                break
            case PtPluginConnector.PLUGIN_ID:
                result = ptProjectTrackImplService.sysConfigSearchListForGrid(this, companyId)
                break
            case QsPluginConnector.PLUGIN_ID:
                result = qsMeasurementImplService.sysConfigSearchListForGrid(this, companyId)
                break
            case SarbPluginConnector.PLUGIN_ID:
                result = sarbImplService.sysConfigSearchListForGrid(this, companyId)
                break
        }
        return result
    }
}
