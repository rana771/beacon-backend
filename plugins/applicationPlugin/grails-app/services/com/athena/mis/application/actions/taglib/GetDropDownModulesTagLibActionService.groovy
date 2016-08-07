package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.RoleModule
import com.athena.mis.application.service.AppVersionService
import com.athena.mis.application.service.RoleModuleService
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
import grails.converters.JSON
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetDropDownModulesTagLibActionService extends BaseService implements ActionServiceIntf {

    private static final String NAME = 'name'
    private static final String ON_CHANGE = 'onchange'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String SELECT_END = "</select>"
    private static final String SHOW_VERSION = 'show_version'
    private static final String VERSION = 'App version: '
    private static final String ROLE_ID = 'role_id'
    private static final String IS_MAPPED = 'is_mapped'

    private Logger log = Logger.getLogger(getClass())

    RoleModuleService roleModuleService
    AppVersionService appVersionService
    AppConfigurationService appConfigurationService

    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService
    @Autowired(required = false)
    PtPluginConnector ptProjectTrackImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired(required = false)
    BudgPluginConnector budgBudgetImplService
    @Autowired(required = false)
    FxdPluginConnector fxdFixedAssetImplService
    @Autowired(required = false)
    ProcPluginConnector procProcurementImplService
    @Autowired(required = false)
    QsPluginConnector qsMeasurementImplService
    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    ELearningPluginConnector elearningImplService
    @Autowired(required = false)
    SarbPluginConnector sarbImplService

    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @return - a map containing all necessary properties with value
     */
    public Map executePreCondition(Map params) {
        try {
            if (!params.name || !params.data_model_name) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // Set default values for optional parameters
            if (!params.hints_text) {
                params.put(HINTS_TEXT, PLEASE_SELECT)
            }
            if (params.show_hints) {
                boolean showHints = Boolean.parseBoolean(params.show_hints)
                params.put(SHOW_HINTS, new Boolean(showHints))
            } else {
                params.put(SHOW_HINTS, Boolean.TRUE)
            }
            if (params.default_value) {
                String strDefaultValue = params.get(DEFAULT_VALUE)
                long defaultValue = Long.parseLong(strDefaultValue)
                params.put(DEFAULT_VALUE, new Long(defaultValue))
            } else {
                params.put(DEFAULT_VALUE, 0L)
            }
            if (params.required) {
                boolean required = Boolean.parseBoolean(params.required)
                params.put(REQUIRED, new Boolean(required))
            }
            if (!params.validationmessage) {
                params.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)
            }
            if (params.show_version) {
                boolean showVersion = Boolean.parseBoolean(params.show_version)
                params.put(SHOW_VERSION, new Boolean(showVersion))
            } else {
                params.put(SHOW_VERSION, Boolean.FALSE)
            }
            if (params.role_id) {
                String strRoleId = params.get(ROLE_ID)
                long roleId = Long.parseLong(strRoleId)
                params.put(ROLE_ID, new Long(roleId))
            }
            if (params.is_mapped) {
                params.put(IS_MAPPED, params.is_mapped)
            }
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /** Get the list of module
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @return - html string for 'select'
     */
    public Map execute(Map result) {
        try {
            Boolean showVersion = (Boolean) result.get(SHOW_VERSION)
            List moduleList
            if (result.is_mapped) {
                long roleId = (Long) result.get(ROLE_ID)
                long defaultValue = (Long) result.get(DEFAULT_VALUE)
                moduleList = listMappedModule(roleId, defaultValue)
            } else if (result.role_id || result.role_id.toString().equals("0")) {
                Long roleId = (Long) result.get(ROLE_ID)
                moduleList = listPlugins(roleId)
            } else {
                moduleList = listAllPlugins(showVersion)
            }
            String html = buildDropDown(moduleList, result)
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

    /** Generate the html for select
     * @param lstItemType - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List moduleList, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)

        Map attributes = (Map) dropDownAttributes
        String strAttributes = EMPTY_SPACE
        attributes.each {
            if (it.value) {
                strAttributes = strAttributes + "${it.key} = '${it.value}' "
            }
        }

        String html = "<select ${strAttributes}>\n" + SELECT_END
        String strOnChange = paramOnChange ? ",change: function(e) {${paramOnChange};}" : EMPTY_SPACE
        String strDefaultValue = defaultValue ? defaultValue : EMPTY_SPACE

        if (showHints.booleanValue()) {
            moduleList = appVersionService.listForKendoDropdown(moduleList, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }
        String jsonData = moduleList as JSON

        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    \$('#${name}').kendoDropDownList({
                        dataTextField:'name',
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

    /**
     * Get list of all plugins
     * @return - plugins list
     */
    private List listPlugins(long roleId) {
        List lstPlugins = []
        Collection<PluginConnector> allPlugins = (Collection<PluginConnector>) PluginConnector.getAllPlugins().sort {
            it.name
        }
        if (roleId != 0) {
            List<RoleModule> lstRoleModule = roleModuleService.findAllByRoleId(roleId)
            if (lstRoleModule.size() > 0) {
                List<Long> lstMappedModuleIds = lstRoleModule.collect { it.moduleId }
                for (int i = 0; i < lstMappedModuleIds.size(); i++) {
                    allPlugins.each {
                        if (it.id == lstMappedModuleIds[i]) {
                            lstPlugins.add([id: it.id, name: it.name])
                        }
                    }
                }
            }
        }
        return lstPlugins
    }

    private List listAllPlugins(boolean showVersion) {
        List lstPlugins = []
        Collection<PluginConnector> allPlugins = (Collection<PluginConnector>) PluginConnector.getAllPlugins().sort {
            it.name
        }
        if (showVersion) {
            allPlugins.each {
                int version = getVersion(it.id)
                String name = it.name + PARENTHESIS_START + VERSION + version + PARENTHESIS_END
                lstPlugins.add([id: it.id, name: name])
            }
        } else {
            allPlugins.each {
                lstPlugins.add([id: it.id, name: it.name])
            }
        }
        return lstPlugins
    }

    private List listMappedModule(long roleId, long defaultValue) {
        List lstPlugins = []
        if (roleId == 0) {
            return lstPlugins
        }
        Collection<PluginConnector> allPlugins = (Collection<PluginConnector>) PluginConnector.getAllPlugins().sort {
            it.name
        }
        List<Long> allModuleIds = []
        allPlugins.each {
            allModuleIds << it.id
        }
        List<RoleModule> lstRoleModule = roleModuleService.findAllByRoleId(roleId)
        List<Long> lstMappedModuleIds = lstRoleModule.collect { it.moduleId }
        List ids = allModuleIds - lstMappedModuleIds
        if (defaultValue != 0) {
            ids << defaultValue
        }
        if (ids.size() > 0) {
            for (int i = 0; i < ids.size(); i++) {
                allPlugins.each {
                    if (it.id == ids[i]) {
                        lstPlugins.add([id: it.id, name: it.name])
                    }
                }
            }
        }
        return lstPlugins
    }

    /**
     * get plugin wise config version
     * @param pluginId - id of plugin
     * @return - version of plugin
     */
    private int getVersion(int pluginId) {
        int version = 0
        switch (pluginId) {
            case PluginConnector.PLUGIN_ID:
                version = appConfigurationService.getAppPluginVersion()
                break
            case BudgPluginConnector.PLUGIN_ID:
                version = budgBudgetImplService.getPluginVersion()
                break
            case AccPluginConnector.PLUGIN_ID:
                version = accAccountingImplService.getPluginVersion()
                break
            case FxdPluginConnector.PLUGIN_ID:
                version = fxdFixedAssetImplService.getPluginVersion()
                break
            case InvPluginConnector.PLUGIN_ID:
                version = invInventoryImplService.getPluginVersion()
                break
            case ProcPluginConnector.PLUGIN_ID:
                version = procProcurementImplService.getPluginVersion()
                break
            case PtPluginConnector.PLUGIN_ID:
                version = ptProjectTrackImplService.getPluginVersion()
                break
            case QsPluginConnector.PLUGIN_ID:
                version = qsMeasurementImplService.getPluginVersion()
                break
            case ExchangeHousePluginConnector.PLUGIN_ID:
                version = exchangeHouseImplService.getPluginVersion()
                break
            case ArmsPluginConnector.PLUGIN_ID:
                version = armsImplService.getPluginVersion()
                break
            case SarbPluginConnector.PLUGIN_ID:
                version = sarbImplService.getPluginVersion()
                break
            case DocumentPluginConnector.PLUGIN_ID:
                version = documentImplService.getPluginVersion()
                break
            case ELearningPluginConnector.PLUGIN_ID:
                version = elearningImplService.getPluginVersion()
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                version = Integer.parseInt((String) dataPipeLineImplService.getPluginVersion())
                break
            default:
                break
        }
        return version
    }
}
