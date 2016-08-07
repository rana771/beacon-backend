package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.service.ContentCategoryService
import grails.converters.JSON
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/*Renders html of 'select' for Content category objects */

class GetDropDownContentCategoryTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ID = 'id'
    private static final String NAME = 'name'
    private static final String URL = 'url'
    private static final String ON_CHANGE = 'onchange'
    private static final String CONTENT_TYPE_ID = 'content_type_id'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String SELECT_END = "</select>"

    ContentCategoryService contentCategoryService

    /**
     * check for required parameter
     * @param params - a map from UI
     * @return - a map with required parameter
     */
    public Map executePreCondition(Map params) {
        try {
            String id = params.get(ID)
            String name = params.get(NAME)
            String url = params.get(URL)
            String dataModelName = params.get(DATA_MODEL_NAME)
            if ((!id) ||(!name) || (!dataModelName) || (!url) || (url.length() == 0)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }

            String strTypeId = params.get(CONTENT_TYPE_ID)
            if (strTypeId) {
                long typeId = Long.parseLong(strTypeId)
                params.put(CONTENT_TYPE_ID, new Long(typeId)) // set typeId
            } else {
                params.put(CONTENT_TYPE_ID, null)
            }

            // Set default values for optional parameters
            params.put(HINTS_TEXT, params.hints_text ? params.hints_text : PLEASE_SELECT)
            params.put(SHOW_HINTS, params.show_hints ? new Boolean(Boolean.parseBoolean(params.show_hints.toString())) : Boolean.TRUE)
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validation_message ? params.validation_message : DEFAULT_MESSAGE)

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

    /**
     * Get html for dropdown
     * @param result - a map returned from precondition method
     * @return - a map consisting desired result
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            long contentTypeId = (Long) result.get(CONTENT_TYPE_ID)
            List lstCategory = contentCategoryService.listByContentTypeId(contentTypeId)
            String html = buildDropDown(lstCategory, result)
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
     * @param lstContentCategory - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List lstContentCategory, Map dropDownAttributes) {
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
            lstContentCategory = contentCategoryService.listForKendoDropdown(lstContentCategory, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }
        String jsonData = lstContentCategory as JSON

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
