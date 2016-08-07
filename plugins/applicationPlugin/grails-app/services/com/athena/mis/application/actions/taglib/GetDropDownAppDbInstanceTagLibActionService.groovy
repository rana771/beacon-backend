package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppSystemEntityCacheService
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Render drop down of AppDbInstance
 */
class GetDropDownAppDbInstanceTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ID = 'id'
    private static final String NAME = 'name'
    private static final String INSTANCE_NAME = 'generatedName'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String DEFAULT_SELECTED_VALUE = 'default_selected_value'
    private static final String INCLUDE_VENDOR_ID = 'include_vendor_id'
    private static final String EXCLUDE_VENDOR_ID = 'exclude_vendor_id'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String KENDO_DROP_DOWN_LIST = 'kendoDropDownList'
    private static final String SERVER_INSTANCE_ID = "server_instance_id"
    private static final String ON_CHANGE = 'onchange'
    private static final String IS_TESTED = "is_tested"
    private static final String IS_MAPPED = "is_mapped"
    private static final String IS_SOURCE = "is_source"
    private static final String IS_NATIVE = "is_native"

    AppSystemEntityCacheService appSystemEntityCacheService
    AppDbInstanceService appDbInstanceService

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
            String dataModelName = params.get(DATA_MODEL_NAME)
            if ((!id) || (!name) || (!dataModelName)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // Set default values for optional parameters
            params.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)

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
                params.put(DEFAULT_VALUE, null)
            }
            if (params.default_selected_value && params.default_selected_value !="null") {
                String strDefaultValue = params.get(DEFAULT_SELECTED_VALUE)
                long defaultValue = Long.parseLong(strDefaultValue)
                params.put(DEFAULT_SELECTED_VALUE, new Long(defaultValue))
            } else {
                params.put(DEFAULT_SELECTED_VALUE, null)
            }
            if (params.include_vendor_id) {
                long includeVendorId = Long.parseLong(params.include_vendor_id)
                params.put(INCLUDE_VENDOR_ID, new Long(includeVendorId))
            }

            if (params.exclude_vendor_id) {
                long excludeVendorId = Long.parseLong(params.exclude_vendor_id)
                params.put(EXCLUDE_VENDOR_ID, new Long(excludeVendorId))
            }

            if (params.required) {
                boolean required = Boolean.parseBoolean(params.required)
                params.put(REQUIRED, new Boolean(required))
            }
            if (params.validation_message) {
                params.put(VALIDATION_MESSAGE, params.validation_message)
            }
            if (params.is_tested) {
                boolean isTested = Boolean.parseBoolean(params.is_tested)
                params.put(IS_TESTED, new Boolean(isTested))
            } else {
                params.put(IS_TESTED, Boolean.FALSE)
            }
            if (params.is_mapped) {
                long serverInstanceId = Long.parseLong(params.server_instance_id.toString())
                params.put(SERVER_INSTANCE_ID, serverInstanceId)
                params.put(IS_MAPPED, params.is_mapped)
            }

            if (params.is_native) {
                boolean isNative = Boolean.parseBoolean(params.is_native)
                params.put(IS_NATIVE, new Boolean(isNative))
            }

            if (params.is_source) {
                long sourceId = Long.parseLong(params.is_source.toString())
                params.put(IS_SOURCE, sourceId)
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
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            long companyId = super.getCompanyId()
            Boolean isTested = (Boolean) result.get(IS_TESTED)
            List<AppDbInstance> lstDbInstance
            if (isTested.booleanValue()) {
                if (result.is_mapped) {
                    long appServerInstanceId = (Long) result.get(SERVER_INSTANCE_ID)
                    lstDbInstance = (List<AppDbInstance>) listMappedDbInstance(appServerInstanceId)
                } else if (result.is_source) {
                    long sourceId = (Long) result.get(IS_SOURCE)
                    SystemEntity sourceDb = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_DB_INSTANCE_SOURCE, appSystemEntityCacheService.SYS_ENTITY_TYPE_DB_INSTANCE, companyId)
                    SystemEntity targetDb = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_DB_INSTANCE_TARGET, appSystemEntityCacheService.SYS_ENTITY_TYPE_DB_INSTANCE, companyId)
                    if (sourceId == 1) {
                        lstDbInstance = appDbInstanceService.findAllByIsTestedAndTypeIdAndCompanyId(isTested, sourceDb.id, companyId)
                    } else {
                        lstDbInstance = appDbInstanceService.findAllByIsTestedAndTypeIdAndCompanyId(isTested, targetDb.id, companyId)
                    }
                } else if (result.is_native) {
                    boolean isNative = (Boolean) result.get(IS_NATIVE)
                    lstDbInstance = appDbInstanceService.findAllByCompanyIdAndIsNative(companyId, isNative)
                } else {
                    lstDbInstance = appDbInstanceService.findAllByIsTestedAndCompanyId(isTested, companyId)
                }
            } else {
                lstDbInstance = appDbInstanceService.listByCompany(companyId)
            }

            SystemEntity vendorMYSQL = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_MYSQL, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, companyId)
            SystemEntity vendorMSSQL2008 = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2008, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, companyId)
            SystemEntity vendorMSSQL2012 = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_MSSQL_2012, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, companyId)
            if (result.include_vendor_id) {
                long includeVendorId = (Long) result.get(INCLUDE_VENDOR_ID)
                if ((includeVendorId == vendorMYSQL.reservedId) && isTested.booleanValue()) {
                    lstDbInstance = appDbInstanceService.findAllByCompanyIdAndReservedVendorIdAndIsTested(companyId, vendorMYSQL.reservedId, isTested)
                } else if (includeVendorId == vendorMSSQL2008.reservedId) {
                    lstDbInstance = appDbInstanceService.findAllByCompanyIdAndReservedVendorIdAndIsTested(companyId, vendorMSSQL2008.reservedId, isTested)
                } else if (includeVendorId == vendorMYSQL.reservedId) {
                    lstDbInstance = appDbInstanceService.findAllByCompanyIdAndVendorId(companyId, vendorMYSQL.id)
                } else if (includeVendorId == vendorMSSQL2012.reservedId) {
                    lstDbInstance = appDbInstanceService.findAllByCompanyIdAndReservedVendorIdAndIsTested(companyId, vendorMSSQL2012.reservedId, isTested)
                }
            }
            if (result.exclude_vendor_id) {
                long excludeVendorId = (Long) result.get(EXCLUDE_VENDOR_ID)
                if ((excludeVendorId == vendorMYSQL.reservedId) && isTested.booleanValue()) {
                    lstDbInstance = appDbInstanceService.findAllByCompanyIdAndReservedVendorIdNotEqualAndIsTested(companyId, vendorMYSQL.reservedId, isTested)
                } else if (excludeVendorId == vendorMYSQL.reservedId) {
                    lstDbInstance = appDbInstanceService.findAllByCompanyIdAndReservedVendorIdNotEqual(companyId, vendorMYSQL.reservedId)
                }
            }
            String html = buildDropDown(lstDbInstance, result)
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

    private List<Map> listMappedDbInstance(long serverInstanceId) {
        String queryForList = """
            SELECT id, generated_name as name, is_slave
            FROM app_db_instance
            WHERE id NOT IN
                (
                    SELECT app_db_instance_id
                    FROM app_server_db_instance_mapping
                    WHERE app_server_instance_id = :serverInstanceId
                )
            AND is_tested = true
            ORDER BY name
        """
        Map queryParams = [serverInstanceId: serverInstanceId]
        List<GroovyRowResult> lstInstance = executeSelectSql(queryForList, queryParams)
        List<Map> dbInstanceList = []
        for (int i = 0; i < lstInstance.size(); i++) {
            GroovyRowResult row = lstInstance[i]
            dbInstanceList << [id: row.id, generatedName: row.name, isSlave: row.is_slave]
        }
        return dbInstanceList
    }
    private static final String SELECT_END = "</select>"

    /**
     * Generate the html for select
     *
     * @param lstDbInstance - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List lstDbInstance, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)
        Long defaultSelectedValue = (Long) dropDownAttributes.get(DEFAULT_SELECTED_VALUE)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)
        String strDefaultValue = defaultValue ? defaultValue : EMPTY_SPACE

        Map attributes = (Map) dropDownAttributes
        String strAttributes = EMPTY_SPACE
        attributes.each {
            strAttributes = strAttributes + "${it.key} = '${it.value}' "
        }
        String controlType = KENDO_DROP_DOWN_LIST
        if (showHints.booleanValue()) {
            lstDbInstance = appDbInstanceService.listForKendoDropdown(lstDbInstance, INSTANCE_NAME, hintsText)
            // the null parameter indicates that hint test is Please Select...
        }
        if (defaultValue) {                          //set value to strDefaultValue for refresh dropdown
            AppDbInstance appDbInstance = appDbInstanceService.read(defaultValue)
            lstDbInstance << [id: appDbInstance.id, generatedName: appDbInstance.generatedName, isSlave: appDbInstance.isSlave]
            strDefaultValue = defaultValue
        }

        if (defaultSelectedValue) {
            strDefaultValue = defaultSelectedValue
        }
        String html = "<select ${strAttributes}>\n" + SELECT_END
        String strOnChange = paramOnChange ? "change: function(e) {${paramOnChange};}" : EMPTY_SPACE


        String jsonData = lstDbInstance as JSON
        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                        \$('#${name}').${controlType}({
                            dataTextField:'${INSTANCE_NAME}',
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
