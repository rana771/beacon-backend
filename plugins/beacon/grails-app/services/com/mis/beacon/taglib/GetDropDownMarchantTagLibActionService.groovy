package com.mis.beacon.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppCountry
import com.athena.mis.application.service.AppCountryService
import com.mis.beacon.Marchant
import com.mis.beacon.service.MarchantService
import grails.converters.JSON
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/*Renders html of 'select' for Country objects */

class GetDropDownMarchantTagLibActionService extends BaseService implements ActionServiceIntf {

    MarchantService marchantService

    private static final String NAME = 'name'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String TEXT_MEMBER = 'textMember'
    private static final String ON_CHANGE = 'onchange'
    private static final String HINTS_TEXT = 'hintsText'
    private static final String SHOW_HINTS = 'showHints'
    private static final String DEFAULT_VALUE = 'defaultValue'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationMessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'dataModelName'
    private static final String DATA_BIND = 'data-bind'
    private static final String ERROR_MSG = 'Required property not found'

    private Logger log = Logger.getLogger(getClass())

    /**
     * if optional params don't exists, put default
     * @param parameters
     * @return
     */
    Map executePreCondition(Map parameters) {
        Map attrs = (Map) parameters
        try {
            String name = attrs.get(NAME)
            String dataModelName = attrs.get(DATA_MODEL_NAME)
            if ((!name) || (!dataModelName)) {
                return super.setError(attrs, ERROR_MSG)
            }
            if (!attrs.hintsText) {
                attrs.put(HINTS_TEXT, PLEASE_SELECT)
            }
            if (!attrs.showHints) {
                attrs.put(SHOW_HINTS, Boolean.TRUE)
            }
            if (!attrs.textMember) {
                attrs.put(TEXT_MEMBER, NAME)
            }
            if (!attrs.required) {
                attrs.put(REQUIRED, Boolean.FALSE)
            }
            if (!attrs.validationMessage) {
                attrs.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)
            }
            return attrs
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * get country list and build dropdown
     * @param result
     * @return
     */
    @Transactional(readOnly = true)
    Map execute(Map result) {
        Map attrs = (Map) result
        try {
            List<Marchant> lstMarchant = marchantService.list()
            String html = buildDropDown(lstMarchant, attrs)
            attrs.html = html
            return attrs
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    Map executePostCondition(Map result) {
        return result
    }

    Map buildSuccessResultForUI(Map result) {
        return result
    }

    Map buildFailureResultForUI(Map result) {
        return result
    }

    private static final String SELECT_END = "</select>"

    /** Generate the html for select
     * @param lstCountry - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<Marchant> lstMarchant, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String paramClass = dropDownAttributes.get(CLASS)
        String paramTabIndex = dropDownAttributes.get(TAB_INDEX)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        String textMember = dropDownAttributes.get(TEXT_MEMBER)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)
        Boolean required = dropDownAttributes.get(REQUIRED)
        String validationMessage = dropDownAttributes.get(VALIDATION_MESSAGE)
        String dataBind = dropDownAttributes.get(DATA_BIND)

        String htmlClass = paramClass ? "class='${paramClass}'" : EMPTY_SPACE
        String htmlTabIndex = paramTabIndex ? "tabindex='${paramTabIndex}'" : EMPTY_SPACE
        String htmlRequired = required.booleanValue() ? "required" : EMPTY_SPACE
        String htmlValidationMessage = required.booleanValue() ? "validationMessage='${validationMessage}'" : EMPTY_SPACE
        String htmlDataBind = dataBind ? "data-bind='${dataBind}'" : EMPTY_SPACE

        String html = "<select name = '${name}' id = '${name}' ${htmlClass} ${htmlTabIndex} ${htmlRequired} ${htmlValidationMessage} ${htmlDataBind}>\n" + SELECT_END
        String strOnChange = paramOnChange ? ",change: function(e) {${paramOnChange};}" : EMPTY_SPACE
        String strDefaultValue = defaultValue ? defaultValue : EMPTY_SPACE

        if (showHints.booleanValue()) {
            lstMarchant = marchantService.listForKendoDropdown(lstMarchant, textMember, hintsText)
        }
        String jsonData = lstMarchant as JSON

        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    \$('#${name}').kendoDropDownList({
                        dataTextField:'${textMember}',
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

}
