package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.service.ItemService
import grails.converters.JSON
import org.apache.log4j.Logger

/*Renders html of 'select' for Item objects */

class GetDropDownItemTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ID = 'id'
    private static final String NAME = 'name'
    private static final String URL = 'url'
    private static final String ON_CHANGE = 'onchange'
    private static final String TYPE_ID = 'type_id'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String FILTER_BY_TYPE_ID = "filter_by_type_id"

    ItemService itemService

    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param params - a map of given attributes
     * @return - a map containing all necessary properties with value
     */
    public Map executePreCondition(Map params) {
        try {
            String name = params.get(NAME)
            String url = params.get(URL)
            String dataModelName = params.get(DATA_MODEL_NAME)
            if ((!name) || (!dataModelName) || (!url) || (url.length() == 0)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }

            String strTypeId = params.get(TYPE_ID)
            if (strTypeId) {
                long typeId = Long.parseLong(strTypeId)
                params.put(TYPE_ID, new Long(typeId)) // set typeId
            } else {
                params.put(TYPE_ID, null)
            }

            // Set default values for optional parameters
            params.put(HINTS_TEXT, params.hints_text ? params.hints_text : PLEASE_SELECT)
            params.put(SHOW_HINTS, params.show_hints ? new Boolean(Boolean.parseBoolean(params.show_hints.toString())) : Boolean.TRUE)
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validation_message ? params.validation_message : DEFAULT_MESSAGE)
            params.put(FILTER_BY_TYPE_ID, params.filter_by_type_id ? new Boolean(Boolean.parseBoolean(params.filter_by_type_id.toString())) : Boolean.FALSE)

            if (params.default_value && params.default_value !="null") {
                String strDefaultValue = params.get(DEFAULT_VALUE)
                long defaultValue = Long.parseLong(strDefaultValue)
                params.put(DEFAULT_VALUE, new Long(defaultValue))
            }else {
                params.put(DEFAULT_VALUE, null)
            }

            return params

        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /** Get the list of Item objects by type
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @return - html string for 'select'
     */
    public Map execute(Map result) {
        try {
            boolean filterByTypeId = ((Boolean) result.get(FILTER_BY_TYPE_ID)).booleanValue()
            List lstItem = []
            if (filterByTypeId) {
                long itemTypeId = (Long) result.get(TYPE_ID)
                lstItem = itemService.listByItemTypeId(itemTypeId)
            } else {
                lstItem = itemService.list()
            }
            String html = buildDropDown(lstItem, result)
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

    private static final String SELECT_END = "</select>"

    /** Generate the html for select
     * @param lstItem - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<Item> lstItem, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)

        Map attributes = (Map) dropDownAttributes
        String strAttributes = EMPTY_SPACE
        attributes.each {
            strAttributes = strAttributes + "${it.key} = '${it.value}' "
        }

        String html = "<select ${strAttributes}'>\n" + SELECT_END

        String strOnChange = paramOnChange ? ",change: function(e) {${paramOnChange};}" : EMPTY_SPACE

        if (showHints.booleanValue()) {
            lstItem = itemService.listForKendoDropdown(lstItem, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }
        String jsonData = lstItem as JSON

        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                 if (${dataModelName}){
                        ${dataModelName}.destroy();
                    }
                    \$('#${name}').kendoDropDownList({
                        dataTextField:'name',
                        dataValueField:'id',
                        dataSource:${jsonData},
                        value:'${defaultValue}'
                        ${strOnChange}
                    });
                    ${dataModelName} = \$("#${name}").data("kendoDropDownList");
                });

            </script>
        """
        return html + script
    }
}
