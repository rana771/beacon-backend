package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.Vendor
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.VendorService
import grails.converters.JSON
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Render drop down of Vendor
 */
class GetDropDownVendorTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ID = 'id'
    private static final String NAME = 'name'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String KENDO_DROP_DOWN_LIST = 'kendoDropDownList'
    private static final String ON_CHANGE = 'onchange'

    VendorService vendorService
    AppSystemEntityCacheService appSystemEntityCacheService

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
            params.put(REQUIRED, Boolean.FALSE)
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

            if (params.required) {
                boolean required = Boolean.parseBoolean(params.required)
                params.put(REQUIRED, new Boolean(required))
            } else {
                params.put(REQUIRED, Boolean.FALSE)
            }

            if (!params.validation_message) {
                params.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)
            }
            return params

        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     *  Get the list of Vendor object
     *  build the html for 'select'
     *
     * @param result - map returned from preCondition
     * @return - html string for 'select'
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            List<Vendor> lstVendor = vendorService.list()
            List lstForDropDown = buildListForDropDown(lstVendor)
            String html = buildUI(lstForDropDown, result)
            result.html = html
            return result
        } catch (Exception e) {
            log.error(e.message)
            throw new RuntimeException(e)
        }
    }

    private List buildListForDropDown(List<Vendor> lstVendor) {
        List lstDropDown = []
        if ((lstVendor == null) || (lstVendor.size() <= 0)) {
            return lstDropDown
        }
        SystemEntity source = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_DB_INSTANCE_SOURCE, appSystemEntityCacheService.SYS_ENTITY_TYPE_DB_INSTANCE, companyId)
        for (int i = 0; i < lstVendor.size(); i++) {
            Vendor vendor = lstVendor[i]
            if (vendor.dbTypeId == source.id) {
                lstDropDown << [id: vendor.id, name: vendor.name + PARENTHESIS_START + "Source" + PARENTHESIS_END]
            } else {
                lstDropDown << [id: vendor.id, name: vendor.name + PARENTHESIS_START + "Target" + PARENTHESIS_END]
            }
        }
        return lstDropDown
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

    private static final String SELECT_END = "</select>"

    /**
     * Generate the html for select
     *
     * @param lstVendor - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildUI(List lstVendor, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)
        String strDefaultValue = defaultValue ? defaultValue : EMPTY_SPACE

        Map attributes = (Map) dropDownAttributes
        String strAttributes = EMPTY_SPACE
        attributes.each {
            strAttributes = strAttributes + "${it.key} = '${it.value}' "
        }
        String controlType = KENDO_DROP_DOWN_LIST
        if (showHints.booleanValue()) {
            lstVendor = vendorService.listForKendoDropdown(lstVendor, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }
        String html = "<select ${strAttributes}>\n" + SELECT_END
        String strOnChange = paramOnChange ? "change: function(e) {${paramOnChange};}" : EMPTY_SPACE


        String jsonData = lstVendor as JSON
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
