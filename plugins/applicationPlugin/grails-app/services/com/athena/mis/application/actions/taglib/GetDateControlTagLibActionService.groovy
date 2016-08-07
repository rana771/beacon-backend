package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger

/*Renders html of date field*/
class GetDateControlTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String INPUT_END = ">"
    private static final String NAME = 'name'
    private static final String ON_CHANGE = 'onchange'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationMessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String PLACE_HOLDER = 'placeholder'
    private static final String DISABLED = 'disabled'
    private static final String DATE_VALUE = 'dateValue'
    private static final String DATA_BIND = 'data-bind'

    /** Build a map containing properties of html date control
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @param obj - N/A
     * @return - a map containing all necessary properties with value
     */
    public Map executePreCondition(Map params) {
        try {
            String name = params.get(NAME)
            if (!name) {
                return setError(params, ERROR_FOR_INVALID_INPUT)
            }

            // Set default values for optional parameters
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validationMessage ? params.validationMessage : DEFAULT_MESSAGE)
            params.put(PLACE_HOLDER, params.placeholder ? params.placeholder : DateUtility.dd_MM_yyyy_SLASH)

            String strDate = getDateValue(params)
            params.put(DATE_VALUE, strDate)

            return params

        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /** Build the html for date field
     * @param parameters - map returned from executePreCondition
     * @param obj - N/A
     * @return - html string for date field
     */
    public Map execute(Map result) {
        try {
            String html = buildDateControl(result)
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
     * Get string form of date value to show on UI
     * @param params - a map of given attributes
     * @return - string form of date
     */
    private String getDateValue(Map params) {
        if (params.value != null) {
            return params.value.toString().trim()
        }
        Date ctlDate = new Date()
        if (params.diffWithCurrent) {
            int diff = Integer.parseInt(params.diffWithCurrent.toString())
            ctlDate = ctlDate + diff
        }
        return DateUtility.getDateForUI(ctlDate)
    }

    /** Generate the html for date control
     * @param dateControlAttributes - a map containing the attributes of date control
     * @return - html string for date control
     */
    private String buildDateControl(Map dateControlAttributes) {
        // read map values
        String name = dateControlAttributes.get(NAME)
        String paramOnChange = dateControlAttributes.get(ON_CHANGE)
        String paramClass = dateControlAttributes.get(CLASS)
        String paramTabIndex = dateControlAttributes.get(TAB_INDEX)
        Boolean required = dateControlAttributes.get(REQUIRED)
        String validationMessage = dateControlAttributes.get(VALIDATION_MESSAGE)
        String placeholder = dateControlAttributes.get(PLACE_HOLDER)
        String strDate = dateControlAttributes.get(DATE_VALUE)
        String disabled = dateControlAttributes.get(DISABLED)
        String dataBind = dateControlAttributes.get(DATA_BIND)

        String htmlClass = paramClass ? "class='${paramClass}'" : EMPTY_SPACE
        String htmlTabIndex = paramTabIndex ? "tabindex='${paramTabIndex}'" : EMPTY_SPACE
        String htmlRequired = required.booleanValue() ? "required" : EMPTY_SPACE
        String htmlValidationMessage = required.booleanValue() ? "validationMessage='${validationMessage}'" : EMPTY_SPACE
        String htmlPlaceholder = placeholder ? "placeholder='${placeholder}'" : EMPTY_SPACE
        String htmlDataBind = dataBind ? "data-bind='${dataBind}'": EMPTY_SPACE
        String htmlDisabled = disabled ? "\$('#${name}').data('kendoDatePicker').enable(false);" : EMPTY_SPACE
        String strOnChange = paramOnChange ? ", change: function(e) {${paramOnChange};}" : EMPTY_SPACE

        String html = "<input name = '${name}' id = '${name}' ${htmlClass} ${htmlTabIndex} ${htmlRequired} ${htmlValidationMessage} ${htmlPlaceholder} ${htmlDataBind}" + INPUT_END

        String script = """
            <script type="text/javascript">
                \$(document).ready(function () {
                    \$('#${name}').kendoDatePicker({
                        format: "dd/MM/yyyy",
                        parseFormats: ["yyyy-MM-dd"],
                        value: '${strDate}'
                        ${strOnChange}
                    });
                      \$("#${name}").kendoMaskedTextBox({mask: "00/00/0000"});
                    ${htmlDisabled}
                });
            </script>
        """
        return html + script
    }
}
