package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppSysConfigCacheService
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.beacon.BeaconPluginConnector
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
import org.apache.log4j.Logger

class RenderDocMenuTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String PLUGIN_ID = "plugin_id"
    private static final String DEPENDS_ON = "depends_on"
    private static final String CAPTION = "caption"

    AppSysConfigCacheService appSysConfigCacheService

    Map executePreCondition(Map parameters) {
        try {
            if (!parameters.plugin_id) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            parameters.put(PLUGIN_ID, new Long(Long.parseLong(parameters.plugin_id.toString())))
            parameters.put(DEPENDS_ON, parameters.depends_on ? new Long(Long.parseLong(parameters.depends_on.toString())) : null)
            return parameters
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    Map execute(Map previousResult) {
        try {
            previousResult.html = buildHtml(previousResult)
            return previousResult
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    Map buildSuccessResultForUI(Map executeResult) {
        return executeResult
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    private String buildHtml(Map params) {
        Long pluginId = (Long) params.get(PLUGIN_ID)
        Long dependsOn = (Long) params.get(DEPENDS_ON)
        String caption = params.get(CAPTION)

        String html = EMPTY_SPACE
        if (!hasPlugin(pluginId)) {
            return html
        } else if (dependsOn && !hasPlugin(dependsOn)) {
            return html
        }
        Map result = getPluginInfo(pluginId)
        SysConfiguration sysConfiguration = (SysConfiguration) appSysConfigCacheService.readByKeyAndCompanyId(appSysConfigCacheService.DEFAULT_PLUGIN, super.getCompanyId())
        long defaultPluginId = Long.parseLong(sysConfiguration.value)
        boolean isDefault = false
        if (defaultPluginId == pluginId) {
            isDefault = true
        }
        if (result) {
            html = """<li id='${result.id}' ${isDefault? 'isDefault=true' : ''}><a>${caption ? caption : result.pluginName}</a></li>"""
        }
        return html
    }

    private boolean hasPlugin(long pluginId) {
        switch (pluginId) {
            case PluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(PluginConnector.PLUGIN_NAME)
                break
            case ExchangeHousePluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)
                break
            case SarbPluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(SarbPluginConnector.PLUGIN_NAME)
                break
            case ArmsPluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)
                break
            case DocumentPluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(DataPipeLinePluginConnector.PLUGIN_NAME)
                break
            case PtPluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)
                break
            case ProcPluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)
                break
            case BudgPluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)
                break
            case AccPluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)
                break
            case InvPluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)
                break
            case QsPluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(QsPluginConnector.PLUGIN_NAME)
                break
            case FxdPluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(FxdPluginConnector.PLUGIN_NAME)
                break
            case ELearningPluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)
                break
            case IctPoolPluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(IctPoolPluginConnector.PLUGIN_NAME)
                break
            case BeaconPluginConnector.PLUGIN_ID:
                return PluginConnector.isPluginInstalled(BeaconPluginConnector.PLUGIN_NAME)
                break
            default:
                return false
        }
    }

    private Map getPluginInfo(long pluginId) {
        switch (pluginId) {
            case PluginConnector.PLUGIN_ID:
                return [pluginName: PluginConnector.PLUGIN_NAME, id: 'dockMenuSettings']
                break
            case ExchangeHousePluginConnector.PLUGIN_ID:
                return [pluginName: ExchangeHousePluginConnector.PLUGIN_NAME, id: 'dockMenuExchangeHouse']
                break
            case SarbPluginConnector.PLUGIN_ID:
                return [pluginName: SarbPluginConnector.PLUGIN_NAME, id: 'dockMenuSarb']
                break
            case ArmsPluginConnector.PLUGIN_ID:
                return [pluginName: ArmsPluginConnector.PLUGIN_NAME, id: 'dockMenuArms']
                break
            case DocumentPluginConnector.PLUGIN_ID:
                return [pluginName: DocumentPluginConnector.PLUGIN_NAME, id: 'dockMenuDocument']
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                return [pluginName: DataPipeLinePluginConnector.PLUGIN_NAME, id: 'dockMenuDataPipeLine']
                break
            case PtPluginConnector.PLUGIN_ID:
                return [pluginName: PtPluginConnector.PLUGIN_NAME, id: 'dockMenuProjectTrack']
                break
            case ProcPluginConnector.PLUGIN_ID:
                return [pluginName: ProcPluginConnector.PLUGIN_NAME, id: 'dockMenuProc']
                break
            case BudgPluginConnector.PLUGIN_ID:
                return [pluginName: BudgPluginConnector.PLUGIN_NAME, id: 'dockMenuBudget']
                break
            case AccPluginConnector.PLUGIN_ID:
                return [pluginName: AccPluginConnector.PLUGIN_NAME, id: 'dockMenuAcc']
                break
            case InvPluginConnector.PLUGIN_ID:
                return [pluginName: InvPluginConnector.PLUGIN_NAME, id: 'dockMenuInv']
                break
            case QsPluginConnector.PLUGIN_ID:
                return [pluginName: QsPluginConnector.PLUGIN_NAME, id: 'dockMenuQs']
                break
            case FxdPluginConnector.PLUGIN_ID:
                return [pluginName: FxdPluginConnector.PLUGIN_NAME, id: 'dockMenuFixedAsset']
                break
            case ELearningPluginConnector.PLUGIN_ID:
                return [pluginName: ELearningPluginConnector.PLUGIN_NAME, id: 'dockMenuElearning']
                break
            case IctPoolPluginConnector.PLUGIN_ID:
                return [pluginName: IctPoolPluginConnector.PLUGIN_NAME, id: 'dockMenuIctPool']
                break
            case BeaconPluginConnector.PLUGIN_ID:
                return [pluginName: BeaconPluginConnector.PLUGIN_NAME, id: 'dockMenuBeacon']
                break
            default:
                return null
        }
    }
}
