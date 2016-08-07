package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppServerInstance
import com.athena.mis.application.service.AppServerInstanceService
import grails.converters.JSON
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Render drop down of AppServerInstance
 */
class GetDropDownAppServerInstanceTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String NAME = 'name'
    private static final String INSTANCE_NAME = 'name'
    private static final String ID = 'id'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationMessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'
    private static final String KENDO_DROP_DOWN_LIST = 'kendoDropDownList'
    private static final String ON_CHANGE = 'onchange'
    private static final String IS_TESTED = "is_tested"
    private static final String NATIVE = "Native"

    AppServerInstanceService appServerInstanceService

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
            if (!params.id || !params.name || !params.data_model_name) {
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
            }
            if (params.required) {
                boolean required = Boolean.parseBoolean(params.required)
                params.put(REQUIRED, new Boolean(required))
            }
            if (!params.validationMessage) {
                params.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)
            }
            if (params.is_tested) {
                boolean isTested = Boolean.parseBoolean(params.is_tested)
                params.put(IS_TESTED, new Boolean(isTested))
            } else {
                params.put(IS_TESTED, Boolean.FALSE)
            }
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     *  Get the list of AppServerInstance object
     *  build the html for 'select'
     *
     * @param result - map returned from preCondition
     * @return - html string for 'select'
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            Boolean isTested = (Boolean) result.get(IS_TESTED)
            List<AppServerInstance> lstServerInstance
            long companyId = getCompanyId()
            if (isTested.booleanValue()) {
                lstServerInstance = appServerInstanceService.findAllByIsTestedAndCompanyId(true, companyId)
            } else {
                lstServerInstance = appServerInstanceService.listByCompany(companyId)
            }
            List lstForDropDown = buildListForDropDown(lstServerInstance)
            String html = buildDropDown(lstForDropDown, result)
            result.html = html
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * do not nothing for post condition
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * do not nothing for build success result
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * do not nothing for build failure result
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build list for drop down
     * @param lstServerInstance - list of serverInstance
     * @return - list for drop down
     */
    private List buildListForDropDown(List<AppServerInstance> lstServerInstance) {
        List lstDropDown = []
        if ((lstServerInstance == null) || (lstServerInstance.size() <= 0)) {
            return lstDropDown
        }
        for (int i = 0; i < lstServerInstance.size(); i++) {
            AppServerInstance serverInstance = lstServerInstance[i]
            if (serverInstance.isNative) {
                lstDropDown << [id: serverInstance.id, name: serverInstance.name + PARENTHESIS_START + NATIVE + PARENTHESIS_END]
            } else {
                lstDropDown << [id: serverInstance.id, name: serverInstance.name]
            }
        }
        return lstDropDown
    }

    private static final String SELECT_END = "</select>"

    /**
     * Generate the html for select
     *
     * @param lstAppServerInstance - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List lstAppServerInstance, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        String defaultValue = dropDownAttributes.get(DEFAULT_VALUE)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)
        String strDefaultValue = defaultValue ? defaultValue : EMPTY_SPACE

        Map attributes = (Map) dropDownAttributes
        String strAttributes = EMPTY_SPACE
        attributes.each {
            strAttributes = strAttributes + "${it.key} = '${it.value}' "
        }
        String controlType = KENDO_DROP_DOWN_LIST
        if (showHints.booleanValue()) {
            lstAppServerInstance = appServerInstanceService.listForKendoDropdown(lstAppServerInstance, INSTANCE_NAME, hintsText)
            // the null parameter indicates that hint test is Please Select...
        }
        String html = "<select ${strAttributes}>\n" + SELECT_END
        String strOnChange = paramOnChange ? ",change: function(e) {${paramOnChange};}" : EMPTY_SPACE

        String jsonData = lstAppServerInstance as JSON
        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                        \$('#${name}').${controlType}({
                            dataTextField:'${INSTANCE_NAME}',
                            dataValueField:'${ID}',
                            dataSource:${jsonData}
                            ${strDefaultValue}
                            ${strOnChange}

                        });
                        ${dataModelName} = \$("#${name}").data("${controlType}");
                });
            </script>
        """
        return html + script
    }
}
