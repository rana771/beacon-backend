package com.athena.mis.application.actions.login

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppSysConfigCacheService
import com.athena.mis.application.entity.SysConfiguration
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

class RenderIndexActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    AppSysConfigCacheService appSysConfigCacheService

    private static final String INDEX_URL = "indexUrl"

    Map executePreCondition(Map params) {
        return params
    }

    Map execute(Map result) {
        try {
            String indexUrl = getPluginIndexPage(super.getCompanyId())
            result.put(INDEX_URL, indexUrl)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    Map executePostCondition(Map result) {
        return result
    }

    Map buildSuccessResultForUI(Map result) {
        return result
    }

    Map buildFailureResultForUI(Map result) {
        return result
    }

    private String getPluginIndexPage(long companyId) {
        SysConfiguration sysConfiguration = (SysConfiguration) appSysConfigCacheService.readByKeyAndCompanyId(appSysConfigCacheService.DEFAULT_PLUGIN, companyId)
        long pluginId = Long.parseLong(sysConfiguration.value)
        String indexUrl = "/app/index"
        switch (pluginId) {
            case ExchangeHousePluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/exh/index"
                    return indexUrl
                }
                break
            case SarbPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(SarbPluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/sarb/index"
                    return indexUrl
                }
                break
            case ArmsPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ArmsPluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/arms/index"
                    return indexUrl
                }
                break
            case DocumentPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/doc/index"
                    return indexUrl
                }
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(DataPipeLinePluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/dpl/index"
                    return indexUrl
                }
                break
            case PtPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/pt/index"
                    return indexUrl
                }
                break
            case ProcPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/proc/index"
                    return indexUrl
                }
                break
            case BudgPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/budg/index"
                    return indexUrl
                }
                break
            case AccPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/acc/index"
                    return indexUrl
                }
                break
            case InvPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/inv/index"
                    return indexUrl
                }
                break
            case QsPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(QsPluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/qs/index"
                    return indexUrl
                }
                break
            case FxdPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(FxdPluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/fxd/index"
                    return indexUrl
                }
                break
            case ELearningPluginConnector.PLUGIN_ID:
                if (PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)) {
                    indexUrl = "/el/index"
                    return indexUrl
                }
                break
            default:
                return indexUrl
        }
        return indexUrl
    }
}
