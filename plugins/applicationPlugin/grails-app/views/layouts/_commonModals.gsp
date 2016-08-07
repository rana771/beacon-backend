<%@ page import="com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector; com.athena.mis.integration.arms.ArmsPluginConnector; com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector; com.athena.mis.integration.accounting.AccPluginConnector; com.athena.mis.integration.procurement.ProcPluginConnector; com.athena.mis.integration.budget.BudgPluginConnector; com.athena.mis.PluginConnector; com.athena.mis.integration.document.DocumentPluginConnector" %>

<!-- FOR APPLICATION MODULE -->
<app:ifPlugin name="${PluginConnector.PLUGIN_NAME}">

    <g:render template='/application/appCommonModal/appResetAllRolesConfirmationModal'/>
    <g:render template='/application/appCommonModal/runTimeExceptionErrorModal'/>
    <g:render template='/application/appCommonModal/viewQueryExecuteMessageModal'/>

</app:ifPlugin>

<!-- FOR BUDGET MODULE -->
<app:ifPlugin name="${BudgPluginConnector.PLUGIN_NAME}">

    <app:renderModalHtml pluginId="${BudgPluginConnector.PLUGIN_ID}">
    </app:renderModalHtml>

</app:ifPlugin>

<!-- FOR PROCUREMENT MODULE -->
<app:ifPlugin name="${ProcPluginConnector.PLUGIN_NAME}">

    <app:renderModalHtml pluginId="${ProcPluginConnector.PLUGIN_ID}">
    </app:renderModalHtml>

</app:ifPlugin>

<!-- FOR ACCOUNTING MODULE -->
<app:ifPlugin name="${AccPluginConnector.PLUGIN_NAME}">

    <app:renderModalHtml pluginId="${AccPluginConnector.PLUGIN_ID}">
    </app:renderModalHtml>

</app:ifPlugin>

<!-- FOR ExchangeHouse MODULE -->
<app:ifPlugin name="${ExchangeHousePluginConnector.PLUGIN_NAME}">

    <app:renderModalHtml pluginId="${ExchangeHousePluginConnector.PLUGIN_ID}">
    </app:renderModalHtml>

</app:ifPlugin>

<!-- FOR ARMS MODULE -->
<app:ifPlugin name="${ArmsPluginConnector.PLUGIN_NAME}">

    <app:renderModalHtml pluginId="${ArmsPluginConnector.PLUGIN_ID}">
    </app:renderModalHtml>

</app:ifPlugin>

<!-- FOR DATA PIPELINE MODULE -->
<app:ifPlugin name="${DataPipeLinePluginConnector.PLUGIN_NAME}">

    <app:renderModalHtml pluginId="${DataPipeLinePluginConnector.PLUGIN_ID}">
    </app:renderModalHtml>

</app:ifPlugin>

<!-- FOR DOCUMENT MODULE -->
<app:ifPlugin name="${DocumentPluginConnector.PLUGIN_NAME}">

    <app:renderModalHtml pluginId="${DocumentPluginConnector.PLUGIN_ID}">
    </app:renderModalHtml>

    <!-- place holders for doc document window -->
    <div id="docDocumentAudioWindow" style="display:none"></div>
    <div id="docDocumentVideoWindow" style="display:none"></div>
    <div id="docDocumentFileWindow" style="display:none"></div>
    <div id="docDocumentImageWindow" style="display:none"></div>
    <div id="docDocumentArticleWindow" style="display:none"></div>

</app:ifPlugin>

