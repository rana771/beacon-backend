package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Benchmark
import com.athena.mis.application.service.BenchmarkService
import grails.converters.JSON
import org.apache.log4j.Logger

/*Renders html of 'select' for Benchmark objects */
class GetDropDownBenchmarkTagLibActionService extends BaseService implements ActionServiceIntf {

    BenchmarkService benchmarkService

    private static final String NAME = 'name'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String HINTS_TEXT = 'hintsText'
    private static final String SHOW_HINTS = 'showHints'
    private static final String DEFAULT_VALUE = 'defaultValue'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationMessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'dataModelName'
    private static final String SELECT_END = "</select>"

    private Logger log = Logger.getLogger(getClass())

    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param params - a map of given attributes
     * @return - a map containing all necessary properties with value
     */
    public Map executePreCondition(Map params) {
        try {
            String name = params.get(NAME)
            String dataModelName = params.get(DATA_MODEL_NAME)
            if ((!name) || (!dataModelName)) {
                return setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // Set default values for optional parameters
            params.put(HINTS_TEXT, params.hintsText ? params.hintsText : PLEASE_SELECT)
            params.put(SHOW_HINTS, params.showHints ? new Boolean(Boolean.parseBoolean(params.showHints.toString())) : Boolean.TRUE)
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validationMessage ? params.validationMessage : DEFAULT_MESSAGE)

            if (params.defaultValue && params.defaultValue !="null") {
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

    /** Get the list of Benchmark
     *  build the html for 'select'
     * @param result - map returned from preCondition
     * @return - html string for 'select'
     */
    public Map execute(Map result) {
        try {
            List<Benchmark> lstBenchmark = benchmarkService.list()
            String html = buildDropDown(lstBenchmark, result)
            result.html = html
            return result
        } catch (Exception e) {
            log.error(e.message)
            return super.setError(result, ERROR_FOR_INVALID_INPUT)
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
     * @param lstBenchmark - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<Benchmark> lstBenchmark, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String paramClass = dropDownAttributes.get(CLASS)
        String paramTabIndex = dropDownAttributes.get(TAB_INDEX)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)
        Boolean required = dropDownAttributes.get(REQUIRED)
        String validationMessage = dropDownAttributes.get(VALIDATION_MESSAGE)

        String htmlClass = paramClass ? "class='${paramClass}'" : EMPTY_SPACE
        String htmlTabIndex = paramTabIndex ? "tabindex='${paramTabIndex}'" : EMPTY_SPACE
        String htmlRequired = required.booleanValue() ? "required" : EMPTY_SPACE
        String htmlValidationMessage = required.booleanValue() ? "validationMessage='${validationMessage}'" : EMPTY_SPACE

        String html = "<select name = '${name}' id = '${name}' ${htmlClass} ${htmlTabIndex} ${htmlRequired} ${htmlValidationMessage}>\n" + SELECT_END
        String strDefaultValue = defaultValue ? defaultValue : EMPTY_SPACE

        if (showHints.booleanValue()) {
            lstBenchmark = benchmarkService.listForKendoDropdown(lstBenchmark, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }
        String jsonData = lstBenchmark as JSON

        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    \$('#${name}').kendoDropDownList({
                        dataTextField:'name',
                        dataValueField:'id',
                        dataSource:${jsonData},
                        value:'${strDefaultValue}'
                    });
                });
                ${dataModelName} = \$("#${name}").data("kendoDropDownList");
            </script>
        """
        return html + script
    }
}
