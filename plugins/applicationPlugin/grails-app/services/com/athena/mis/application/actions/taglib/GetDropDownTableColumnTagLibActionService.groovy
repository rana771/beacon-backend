package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.Vendor
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.VendorService
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import grails.converters.JSON
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 * Render drop down of DbInstance
 */
class GetDropDownTableColumnTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ID = 'id'
    private static final String NAME = 'name'
    private static final String INSTANCE_NAME = 'name'
    private static final String ON_CHANGE = 'onchange'
    private static final String DB_INSTANCE_ID = 'db_instance_id'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String IS_PRIMARY = 'is_primary'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String KENDO_DROP_DOWN_LIST = 'kendoDropDownList'
    private static final String URL = 'url'
    private static final String IS_MAPPED = "is_mapped"
    private static final String TABLE_NAME = "table_name"
    private static final String SELECT_END = "</select>"

    AppSystemEntityCacheService appSystemEntityCacheService
    AppDbInstanceService appDbInstanceService
    VendorService vendorService
    DataAdapterFactoryService dataAdapterFactoryService

    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService

    /**
     * Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     *
     * @param parameters - a map of given attributes
     * @return - a map containing all necessary properties with value
     */
    public Map executePreCondition(Map params) {
        try {
            String id = params.get(ID)
            String name = params.get(NAME)
            String url = params.get(URL)
            String strDbInstanceId = params.get(DB_INSTANCE_ID)
            String dataModelName = params.get(DATA_MODEL_NAME)
            if ((!id) || (!name) || (!dataModelName) || (!strDbInstanceId) || (!url)) {
                return setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long sourceDbInstanceId = Long.parseLong(strDbInstanceId)// set id
            params.put(DB_INSTANCE_ID, new Long(sourceDbInstanceId))
            params.put(URL, url)   // set url

            // Set default values for optional parameters
            params.put(CLASS, params.class ? params.class : null)
            params.put(TAB_INDEX, params.tabindex ? params.tabindex : null)
            params.put(ON_CHANGE, params.onchange ? params.onchange : null)
            params.put(HINTS_TEXT, params.hints_text ? params.hints_text : PLEASE_SELECT)
            params.put(SHOW_HINTS, params.show_hints ? new Boolean(Boolean.parseBoolean(params.show_hints.toString())) : Boolean.TRUE)
            params.put(TABLE_NAME, params.table_name ? params.table_name : null)
            params.put(DEFAULT_VALUE, params.default_value ? params.default_value : EMPTY_SPACE)
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(IS_PRIMARY, params.is_primary ? new Boolean(Boolean.parseBoolean(params.is_primary.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validation_message ? params.validation_message : DEFAULT_MESSAGE)
            if (params.is_mapped) {
                params.put(IS_MAPPED, params.is_mapped)
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
     * @param parameters - map returned from preCondition
     * @param obj - N/A
     * @return - html string for 'select'
     */
    public Map execute(Map result) {
        try {
            Long sourceDbInstanceId = (Long) result.get(DB_INSTANCE_ID)
            String tableName = (String) result.get(TABLE_NAME)
            List listColumns
            if (result.dpl_data_export_id) {
                long dplDataExportId = Long.parseLong(result.dpl_data_export_id.toString())
                listColumns = getMappingTableColumns(sourceDbInstanceId, tableName, dplDataExportId)
            } else {
                listColumns = getTableColumns(sourceDbInstanceId, tableName)
            }
            String html = buildDropDown(listColumns, result)
            result.html = html
            return result
        } catch (Exception e) {
            log.error(e.message)
            throw new RuntimeException(e)
        }
    }

    private List getTableColumns(Long sourceDbInstanceId, String tableName) {
        SystemEntity msSqlVendor2008 = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2008, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, companyId)
        SystemEntity msSqlVendor2012 = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2012, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, companyId)
        List lstColumns = []
        AppDbInstance appDbInstance = appDbInstanceService.read(sourceDbInstanceId)
        if (!appDbInstance || !appDbInstance.isTested) return lstColumns
        Vendor vendor = vendorService.read(appDbInstance.vendorId)
        DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(appDbInstance)
        if (vendor.vendorId == msSqlVendor2008.id || vendor.vendorId == msSqlVendor2012.id) {
            dataAdapter.setParams([isPrimary: Boolean.TRUE])
            Map dataAdapterResult = dataAdapter.selectColumnDetails(tableName)
            Boolean isError = Boolean.parseBoolean(dataAdapterResult.get(IS_ERROR).toString())
            if (isError.booleanValue()) {
                return lstColumns
            }
            List<String> columns = (List<String>) dataAdapterResult.lstResult
            for (int i = 0; i < columns.size(); i++) {
                String element = columns[i].column_name
                lstColumns << ["id": element, "name": element]
            }
        } else {
            Map dataAdapterResult = dataAdapter.selectColumnDetails(tableName)
            Boolean isError = Boolean.parseBoolean(dataAdapterResult.get(IS_ERROR).toString())
            if (isError.booleanValue()) {
                return lstColumns
            }
            List<String> columns = (List<String>) dataAdapterResult.lstResult
            for (int i = 0; i < columns.size(); i++) {
                String element = columns[i].column_name
                String elementName = EMPTY_SPACE
                String columnKey = columns[i].column_key
                if (columnKey.equals(EMPTY_SPACE) || columnKey.equals(STR_ZERO)) {
                    elementName = element
                } else {
                    elementName = element + " (" + columnKey + ")"
                }
                lstColumns << ["id": element, "name": elementName]
            }
        }
        return lstColumns
    }

    private List getMappingTableColumns(Long sourceDbInstanceId, String tableName, long dplDataExportId) {
        SystemEntity msSqlVendor2008 = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2008, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, companyId)
        SystemEntity msSqlVendor2012 = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2012, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, companyId)
        List lstColumns = []
        AppDbInstance appDbInstance = appDbInstanceService.read(sourceDbInstanceId)
        if (!appDbInstance || !appDbInstance.isTested) return lstColumns
        Vendor vendor = vendorService.read(appDbInstance.vendorId)
        DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(appDbInstance)
        if (vendor.vendorId == msSqlVendor2008.id || vendor.vendorId == msSqlVendor2012.id) {
            dataAdapter.setParams([isPrimary: Boolean.TRUE])
            Map dataAdapterResult = dataAdapter.selectColumnDetails(tableName)
            Boolean isError = Boolean.parseBoolean(dataAdapterResult.get(IS_ERROR).toString())
            if (isError.booleanValue()) {
                return lstColumns
            }
            List mappedCols = dataPipeLineImplService.findAllByDplDataExportIdAndTableNameIlike(dplDataExportId, tableName)
            List<String> columns = (List<String>) dataAdapterResult.lstResult

            for (int i = 0; i < columns.size(); i++) {
                String element = columns[i].column_name
                boolean add = true
                for (int j = 0; j < mappedCols.size(); j++) {
                    String mapElement = mappedCols[j]
                    if (element.equalsIgnoreCase(mapElement)) {
                        add = false
                        break
                    }
                }
                if (add) {
                    lstColumns << ["id": element, "name": element]
                }
            }
        } else {
            Map dataAdapterResult = dataAdapter.selectColumnDetails(tableName)
            Boolean isError = Boolean.parseBoolean(dataAdapterResult.get(IS_ERROR).toString())
            if (isError.booleanValue()) {
                return lstColumns
            }
            List mappedCols = dataPipeLineImplService.findAllByDplDataExportIdAndTableNameIlike(dplDataExportId, tableName)
            List<String> columns = (List<String>) dataAdapterResult.lstResult

            for (int i = 0; i < columns.size(); i++) {
                String element = columns[i].column_name
                boolean add = true
                for (int j = 0; j < mappedCols.size(); j++) {
                    String mapElement = mappedCols[j]
                    if (element.equalsIgnoreCase(mapElement)) {
                        add = false
                        break
                    }
                }
                if (add) {
                    String elementName = EMPTY_SPACE
                    String columnKey = columns[i].column_key
                    if (columns[i].column_name && (!columnKey.equals(EMPTY_SPACE)) && (!columnKey.equals(STR_ZERO))) {
                        elementName = element + " (" + columnKey + ")"
                        lstColumns << ["id": element, "name": elementName]
                    }
                }
            }
        }
        return lstColumns
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
    private String buildDropDown(List columns, Map dropDownAttributes) {
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
            columns.add(0, ["id": EMPTY_SPACE, "name": hintsText, "count": 0])
            // the null parameter indicates that hint test is Please Select...
        }
        String html = "<select ${strAttributes}>\n" + SELECT_END
        String strOnChange = paramOnChange ? "change: function(e) {${paramOnChange};}" : EMPTY_SPACE
        String strDefaultValue = defaultValue ? defaultValue : EMPTY_SPACE

        String jsonData = columns as JSON
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
}
