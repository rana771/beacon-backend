package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import grails.converters.JSON
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/*Renders html of 'select' for  SystemEntity objects */

class GetDropDownSystemEntityTagLibActionService extends BaseService implements ActionServiceIntf {

    SystemEntityService systemEntityService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Autowired(required = false)
    BudgPluginConnector budgBudgetImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    PtPluginConnector ptProjectTrackImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService
    @Autowired(required = false)
    SarbPluginConnector sarbImplService
    @Autowired(required = false)
    ELearningPluginConnector elearningImplService
    @Autowired(required = false)
    ProcPluginConnector procProcurementImplService

    private static final String NAME = 'name'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String ON_CHANGE = 'onchange'
    private static final String TYPE_ID = 'typeId'
    private static final String HINTS_TEXT = 'hintsText'
    private static final String SHOW_HINTS = 'showHints'
    private static final String DEFAULT_VALUE = 'defaultValue'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String ELEMENTS = 'elements'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationMessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'dataModelName'
    private static final String DATA_BIND = 'data-bind'

    private Logger log = Logger.getLogger(getClass())

    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @return - a map containing all necessary properties with value
     */
    public Map executePreCondition(Map params) {
        try {
            String strTypeId = params.get(TYPE_ID)
            String name = params.get(NAME)
            String dataModelName = params.get(DATA_MODEL_NAME)
            if ((!name) || (!dataModelName) || (!strTypeId) || (strTypeId.length() == 0)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }

            long systemEntityTypeId = Long.parseLong(strTypeId)
            params.put(TYPE_ID, new Long(systemEntityTypeId)) // set the typeId

            // Set default values for optional parameters
            params.put(HINTS_TEXT, params.hintsText ? params.hintsText : PLEASE_SELECT)
            params.put(SHOW_HINTS, params.showHints ? new Boolean(Boolean.parseBoolean(params.showHints.toString())) : Boolean.TRUE)
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validationMessage ? params.validationMessage : DEFAULT_MESSAGE)
            params.put(ELEMENTS, params.elements ? params.elements : [])
            if (params.defaultValue && params.defaultValue != "null") {
                String strDefaultValue = params.get(DEFAULT_VALUE)
                long defaultValue = Long.parseLong(strDefaultValue)
                params.put(DEFAULT_VALUE, new Long(defaultValue))
            }
            return params

        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /** Get the list of SystemEntity objects by type
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @return - html string for 'select'
     */
    public Map execute(Map result) {
        try {
            Long systemEntityTypeId = (Long) result.get(TYPE_ID)
            List<Long> lstElements = (List<Long>) result.get(ELEMENTS)

            long pluginId = Long.parseLong(result.pluginId.toString())
            List<SystemEntity> lstSystemEntity = (List<SystemEntity>) listSystemEntityFromCacheService(systemEntityTypeId, pluginId)

            String html
            if (lstElements.size() == 0) {
                html = buildDropDown(lstSystemEntity, result)
            } else {
                List<SystemEntity> lstSystemEntityFiltered = []
                for (int i = 0; i < lstElements.size(); i++) {
                    long reservedId = lstElements[i]
                    for (int j = 0; j < lstSystemEntity.size(); j++) {
                        SystemEntity systemEntity = lstSystemEntity[j]
                        if (reservedId == systemEntity.reservedId) {
                            lstSystemEntityFiltered << systemEntity
                            break
                        }
                    }
                }
                html = buildDropDown(lstSystemEntityFiltered, result)
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
     * Get list of active system entity
     * @param systemEntityTypeId - typeId of systemEntity
     * @param pluginId - id of plugin
     * @return - list of active system entity
     */
    private List listSystemEntityFromCacheService(long systemEntityTypeId, long pluginId) {
        List lstSystemEntity
        long companyId = super.getCompanyId()
        switch (pluginId) {
            case PluginConnector.PLUGIN_ID:
                lstSystemEntity = appSystemEntityCacheService.listByIsActive(systemEntityTypeId, companyId)
                break
            case DocumentPluginConnector.PLUGIN_ID:
                lstSystemEntity = documentImplService.listByIsActive(systemEntityTypeId, companyId)
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                lstSystemEntity = dataPipeLineImplService.listByIsActive(systemEntityTypeId, companyId)
                break
            case PtPluginConnector.PLUGIN_ID:
                lstSystemEntity = ptProjectTrackImplService.listByIsActive(systemEntityTypeId, companyId)
                break
            case AccPluginConnector.PLUGIN_ID:
                lstSystemEntity = accAccountingImplService.listByIsActive(systemEntityTypeId, companyId)
                break
            case InvPluginConnector.PLUGIN_ID:
                lstSystemEntity = invInventoryImplService.listByIsActive(systemEntityTypeId, companyId)
                break
            case BudgPluginConnector.PLUGIN_ID:
                lstSystemEntity = budgBudgetImplService.listByIsActive(systemEntityTypeId, companyId)
                break
            case ExchangeHousePluginConnector.PLUGIN_ID:
                lstSystemEntity = exchangeHouseImplService.listByIsActive(systemEntityTypeId, companyId)
                break
            case SarbPluginConnector.PLUGIN_ID:
                lstSystemEntity = sarbImplService.listByIsActive(systemEntityTypeId, companyId)
                break
            case ArmsPluginConnector.PLUGIN_ID:
                lstSystemEntity = armsImplService.listByIsActive(systemEntityTypeId, companyId)
                break
            case ProcPluginConnector.PLUGIN_ID:
                lstSystemEntity = procProcurementImplService.listByIsActive(systemEntityTypeId, companyId)
                break
            case ELearningPluginConnector.PLUGIN_ID:
                lstSystemEntity = elearningImplService.listByIsActive(systemEntityTypeId, companyId)
                break
            default:
                lstSystemEntity = []
        }
        return lstSystemEntity
    }

    /** Get the list from corresponding cacheUtility
     * @param systemEntityTypeId - typeId of systemEntity
     * @return -List of SystemEntity
     */
    private List listSystemEntity(long systemEntityTypeId) {
        List lstSystemEntity
        long companyId = super.getCompanyId()
        switch (systemEntityTypeId) {
            default:
                lstSystemEntity = []
        }
        return lstSystemEntity
    }

    private static final String SELECT_END = "</select>"
    private static final String KEY = "key"

    /** Generate the html for select
     * @param lstSystemEntity - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<SystemEntity> lstSystemEntity, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String paramClass = dropDownAttributes.get(CLASS)
        String paramTabIndex = dropDownAttributes.get(TAB_INDEX)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)
        Boolean required = dropDownAttributes.get(REQUIRED)
        String validationMessage = dropDownAttributes.get(VALIDATION_MESSAGE)
        String dataBind = dropDownAttributes.get(DATA_BIND)

        String htmlClass = paramClass ? "class='${paramClass}'" : EMPTY_SPACE
        String htmlTabIndex = paramTabIndex ? "tabindex='${paramTabIndex}'" : EMPTY_SPACE
        String htmlRequired = required.booleanValue() ? "required" : EMPTY_SPACE
        String htmlValidationMessage = required.booleanValue() ? "validationMessage='${validationMessage}'" : EMPTY_SPACE
        String htmlDataBind = dataBind ? "data-bind='${dataBind}'" : EMPTY_SPACE

        String html = "<select name = '${name}' id = '${name}' ${htmlClass} ${htmlTabIndex} ${htmlRequired} ${htmlValidationMessage} ${htmlDataBind}>\n" + SELECT_END
        String strOnChange = paramOnChange ? ",change: function(e) {${paramOnChange};}" : EMPTY_SPACE
        String strDefaultValue = defaultValue ? defaultValue : EMPTY_SPACE

        if (showHints.booleanValue()) {
            lstSystemEntity = systemEntityService.listForKendoDropdown(lstSystemEntity, KEY, hintsText)
            // the KEY parameter indicates that dataTextField is key
        }
        String jsonData = lstSystemEntity as JSON

        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    \$('#${name}').kendoDropDownList({
                        dataTextField:'key',
                        dataValueField:'id',
                        dataSource:${jsonData},
                        value:'${strDefaultValue}'
                        ${strOnChange}
                    });
                });
                ${dataModelName} = \$("#${name}").data("kendoDropDownList");
            </script>
        """
        return html + script
    }
}
