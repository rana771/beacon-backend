package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetModalHtmlTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String HTML = "html"

    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    BudgPluginConnector budgBudgetImplService
    @Autowired(required = false)
    ProcPluginConnector procProcurementImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService

    @Override
    Map executePreCondition(Map params) {
        if (!params.pluginId) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        return params
    }

    @Override
    Map execute(Map result) {
        try {
            int pluginId = Integer.parseInt(result.pluginId.toString())
            String html = getHtml(pluginId)
            result.put(HTML, html)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    @Override
    Map executePostCondition(Map result) {
        return result
    }

    @Override
    Map buildSuccessResultForUI(Map result) {
        return result
    }

    @Override
    Map buildFailureResultForUI(Map result) {
        return result
    }

    private String getHtml(int pluginId) {
        String html = EMPTY_SPACE
        switch (pluginId) {
            case DocumentPluginConnector.PLUGIN_ID :
                html = documentImplService.renderModalHtml()
                break
            case BudgPluginConnector.PLUGIN_ID:
                html = budgBudgetImplService.renderModalHtml()
                break
            case ProcPluginConnector.PLUGIN_ID:
                html = procProcurementImplService.renderModalHtml()
                break
            case AccPluginConnector.PLUGIN_ID:
                html = accAccountingImplService.renderModalHtml()
                break
            case ExchangeHousePluginConnector.PLUGIN_ID:
                html = exchangeHouseImplService.renderModalHtml()
                break
            case ArmsPluginConnector.PLUGIN_ID:
                html = armsImplService.renderModalHtml()
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                html = dataPipeLineImplService.renderModalHtml()
                break
        }
        return html
    }
}
