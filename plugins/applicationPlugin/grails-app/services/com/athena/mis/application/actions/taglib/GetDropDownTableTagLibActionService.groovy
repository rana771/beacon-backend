package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import grails.converters.JSON
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 * Render drop down of table
 */
class GetDropDownTableTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String NAME = 'name'
    private static final String INSTANCE_NAME = 'name'
    private static final String ON_CHANGE = 'onchange'
    private static final String DB_INSTANCE_ID = 'db_instance_id'
    private static final String IS_DATA_COUNT = 'is_data_count'
    private static final String ID = 'id'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String KENDO_DROP_DOWN_LIST = 'kendoDropDownList'
    private static final String URL = 'url'

    private static final String TABLE_NAME = "table_name"
    private static final String TABLE_ROWS = "table_rows"
    private static final String SELECT_END = "</select>"

    AppSystemEntityCacheService appSystemEntityCacheService
    AppDbInstanceService appDbInstanceService
    DataAdapterFactoryService dataAdapterFactoryService

    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService

    /**
     * Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     *
     * @param params - a map of given attributes
     * @return - a map containing all necessary properties with value
     */
    public Map executePreCondition(Map params) {
        try {
            String name = params.get(NAME)
            String id = params.get(ID)
            String strDbInstanceId = params.get(DB_INSTANCE_ID)
            String dataModelName = params.get(DATA_MODEL_NAME)
            if ((!id) || (!name) || (!dataModelName) || (!strDbInstanceId)) {
                return setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long sourceDbInstanceId = Long.parseLong(strDbInstanceId)// set id
            params.put(DB_INSTANCE_ID, new Long(sourceDbInstanceId))

            // Set default values for optional parameters
            params.put(HINTS_TEXT, params.hints_text ? params.hints_text : PLEASE_SELECT)
            params.put(SHOW_HINTS, params.show_hints ? new Boolean(Boolean.parseBoolean(params.show_hints.toString())) : Boolean.TRUE)
            params.put(IS_DATA_COUNT, params.is_data_count ? new Boolean(Boolean.parseBoolean(params.is_data_count.toString())) : Boolean.FALSE)
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validation_message ? params.validation_message : DEFAULT_MESSAGE)

            if (params.default_value && params.default_value != "null") {
                String strDefaultValue = params.get(DEFAULT_VALUE)
                params.put(DEFAULT_VALUE, strDefaultValue)
            } else {
                params.put(DEFAULT_VALUE, null)
            }
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     *  Get the list of DbInstance object
     *  build the html for 'select'
     *
     * @param result - map returned from preCondition
     * @return - html string for 'select'
     */
    public Map execute(Map result) {
        try {
            Long sourceDbInstanceId = (Long) result.get(DB_INSTANCE_ID)
            List lstTable = []
            if (result.dpl_data_export_id) {
                long dataExportId = Long.parseLong(result.dpl_data_export_id.toString())
                List lstMappedTable = dataPipeLineImplService.findAllByDplDataExportId(dataExportId)
                lstMappedTable = lstMappedTable.collect { it.tableName }
                lstTable = listTableNameForDataExport(sourceDbInstanceId, result, lstMappedTable)
            } else if (result.dpl_data_import_id) {
                long dataImportId = Long.parseLong(result.dpl_data_import_id.toString())
                List lstMappedTable = dataPipeLineImplService.findAllByDplDataImportId(dataImportId)
                lstMappedTable = lstMappedTable.collect { it.tableName }
                lstTable = listTableNameForDataExport(sourceDbInstanceId, result, lstMappedTable)
            } else if (result.dpl_cdc_id) {
                long dplCdcId = Long.parseLong(result.dpl_cdc_id.toString())
                lstTable = dataPipeLineImplService.listTableNameForCdc(dplCdcId)
            }
            String html = buildDropDown(lstTable, result)
            result.html = html
            return result
        } catch (Exception e) {
            log.error(e.message)
            throw new RuntimeException(e)
        }
    }

    private List listTableNameForDataExport(Long sourceDbInstanceId, Map result, List lstMappedTable) {
        List lstTable = []
        AppDbInstance appDbInstance = appDbInstanceService.read(sourceDbInstanceId)
        if (!appDbInstance || !appDbInstance.isTested) return lstTable
        DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(appDbInstance)
        Map params
        SystemEntity vendor = appSystemEntityCacheService.readByReservedId(appDbInstance.reservedVendorId, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, appDbInstance.companyId)
        if (vendor.reservedId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_AMAZON_REDSHIFT && result.dpl_data_import_id) {
            params = [allTable: true, isMapped: true, tblName: buildCommaSeparatedStringOfTables(lstMappedTable)]
        } else {
            params = [isMapped: true, tblName: buildCommaSeparatedStringOfTables(lstMappedTable)]
        }
        dataAdapter.setParams(params)
        Map dataAdapterResult = dataAdapter.listTable(params)
        Boolean isError = Boolean.parseBoolean(dataAdapterResult.get(IS_ERROR).toString())
        if (isError.booleanValue()) {
            return lstTable
        }
        List<String> tables = (List<String>) dataAdapterResult.lstResult
        if (vendor.reservedId == appSystemEntityCacheService.SYS_ENTITY_VENDOR_AMAZON_REDSHIFT && result.dpl_data_import_id) {
            for (int i = 0; i < tables.size(); i++) {
                String element = tables[i].getAt(TABLE_NAME)
                lstTable << ["id": element, "name": element, "count": 0]
            }
        } else {
            for (int i = 0; i < tables.size(); i++) {
                long tableRow = (long) tables[i].getAt(TABLE_ROWS)
                String element = tables[i].getAt(TABLE_NAME) + PARENTHESIS_START + tableRow + PARENTHESIS_END
                lstTable << ["id": tables[i].getAt(TABLE_NAME), "name": element, "count": tables[i].getAt(TABLE_ROWS)]
            }
        }
        return lstTable
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
     * Generate the html for select
     *
     * @param lstDbInstance - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List tables, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)
        String defaultValue = dropDownAttributes.get(DEFAULT_VALUE)

        Map attributes = (Map) dropDownAttributes
        String strAttributes = EMPTY_SPACE
        attributes.each {
            strAttributes = strAttributes + "${it.key} = '${it.value}' "
        }
        String controlType = KENDO_DROP_DOWN_LIST
        if (showHints.booleanValue()) {
            tables.add(0, ["id": EMPTY_SPACE, "name": hintsText, "count": 0])
            // the null parameter indicates that hint test is Please Select...
        }
        String html = "<select ${strAttributes}>\n" + SELECT_END
        String strOnChange = paramOnChange ? "change: function(e) {${paramOnChange};}" : EMPTY_SPACE
        String strDefaultValue = defaultValue ? defaultValue : EMPTY_SPACE

        String jsonData = tables as JSON
        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                        \$('#${name}').${controlType}({
                            dataTextField:'${NAME}',
                            dataValueField:'${ID}',
                            dataSource:${jsonData},
                            value:'${strDefaultValue}',
                            ${strOnChange}
                        });
                        ${dataModelName} = \$("#${name}").data("${controlType}");
                });
            </script>
        """
        return html + script
    }

    private static String buildCommaSeparatedStringOfTables(List lstTables) {
        String strRecipients = ""
        for (int i = 0; i < lstTables.size(); i++) {
            if ((i + 1) < lstTables.size()) strRecipients += SINGLE_QUOTE + lstTables[i] + SINGLE_QUOTE + COMA
            else strRecipients += SINGLE_QUOTE + lstTables[i] + SINGLE_QUOTE
        }
        return strRecipients
    }
}
