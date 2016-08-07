package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Role
import com.athena.mis.application.service.ReservedRoleService
import com.athena.mis.application.service.RoleService
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import grails.converters.JSON
import org.apache.log4j.Logger

class GetDropDownRoleForInvitationTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String NAME = 'name'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String ON_CHANGE = 'onchange'
    private static final String IS_INVITATION = 'is_invitation'
    private static final String HINTS_TEXT = 'hintsText'
    private static final String SHOW_HINTS = 'showHints'
    private static final String DEFAULT_VALUE = 'defaultValue'
    private static final String RESERVED_VALUE = 'reserved_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationMessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'dataModelName'
    private static final String SINGLE_DOT = '.'
    private static final String ESCAPE_DOT = '\\\\.'
    private static final String ADD_ALL_ATTRIBUTES = 'addAllAttributes'

    RoleService roleService

    @Override
    Map executePreCondition(Map params) {
        try {
            String name = params.get(NAME)
            String dataModelName = params.get(DATA_MODEL_NAME)
            if ((!name) || (!dataModelName)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }

            // Set default values for optional parameters
            params.put(HINTS_TEXT, params.hintsText ? params.hintsText : PLEASE_SELECT)
            params.put(SHOW_HINTS, params.showHints ? new Boolean(Boolean.parseBoolean(params.showHints.toString())) : Boolean.TRUE)
            params.put(RESERVED_VALUE, params.reserved_value ? new Boolean(Boolean.parseBoolean(params.reserved_value.toString())) : Boolean.FALSE)
            params.put(IS_INVITATION, params.is_invitation ? new Boolean(Boolean.parseBoolean(params.is_invitation.toString())) : Boolean.FALSE)
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validationMessage ? params.validationMessage : DEFAULT_MESSAGE)
            params.put(ADD_ALL_ATTRIBUTES, params.addAllAttributes ? new Boolean(Boolean.parseBoolean(params.addAllAttributes.toString())) : Boolean.FALSE)

            if (params.defaultValue && params.defaultValue != "null") {
                String strDefaultValue = params.get(DEFAULT_VALUE)
                long defaultValue = Long.parseLong(strDefaultValue)
                params.put(DEFAULT_VALUE, new Long(defaultValue))
            } else {
                params.put(DEFAULT_VALUE, null)
            }

            return params

        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    @Override
    Map execute(Map result) {
        try {
            List<Role> lstRole = listForDropDown(result)
            String html = buildDropDown(lstRole, result)
            result.html = html
            return result
        } catch (Exception e) {
            log.error(e.message)
            throw new RuntimeException(e)
        }
    }

    @Override
    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    @Override
    Map buildSuccessResultForUI(Map executeResult) {
        return executeResult
    }

    @Override
    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    private List<Role> listForDropDown(Map result) {
        List<Role> roleList = []
        boolean isInvitation = (boolean) result.get(IS_INVITATION)
        if (ELearningPluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME) && isInvitation) {
            List<Long> roleTypeIdList = [(long) ReservedRoleService.ROLE_TYPE_E_LEARNING_ADMINISTRATOR, (long) ReservedRoleService.ROLE_TYPE_E_LEARNING_DEVELOPMENT, (long) ReservedRoleService.ROLE_TYPE_E_LEARNING_COORDINATOR]
            roleList = roleService.findAllByPluginIdAndCompanyIdAndRoleTypeId(ELearningPluginConnector.PLUGIN_ID, super.companyId, roleTypeIdList)
        } else if (ELearningPluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME) && !isInvitation) {
            List<Long> roleTypeIdList = [(long) ReservedRoleService.ROLE_TYPE_E_LEARNING_ADMINISTRATOR, (long) ReservedRoleService.ROLE_TYPE_E_LEARNING_DEVELOPMENT]
            roleList = roleService.findAllByPluginIdAndCompanyIdAndRoleTypeId(ELearningPluginConnector.PLUGIN_ID, super.companyId, roleTypeIdList)
        } else if (DocumentPluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
            List<Long> roleTypeIdList = [(long) ReservedRoleService.ROLE_TYPE_DOCUMENT_ADMINISTRATOR, (long) ReservedRoleService.ROLE_TYPE_DOCUMENT_DEVELOPMENT]
            roleList = roleService.findAllByPluginIdAndCompanyIdAndRoleTypeId(DocumentPluginConnector.PLUGIN_ID, super.companyId, roleTypeIdList)
        }
        return roleList
    }

    private static final String SELECT_END = "</select>"

    /** Generate the html for select
     * @param lstRole - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<Role> lstRole, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String paramClass = dropDownAttributes.get(CLASS)
        String paramTabIndex = dropDownAttributes.get(TAB_INDEX)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Boolean reservedValue = dropDownAttributes.get(RESERVED_VALUE)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)
        Boolean required = dropDownAttributes.get(REQUIRED)
        String validationMessage = dropDownAttributes.get(VALIDATION_MESSAGE)

        String value = reservedValue ? "roleTypeId" : "id"
        String htmlClass = paramClass ? "class='${paramClass}'" : EMPTY_SPACE
        String htmlTabIndex = paramTabIndex ? "tabindex='${paramTabIndex}'" : EMPTY_SPACE
        String htmlRequired = required.booleanValue() ? "required" : EMPTY_SPACE
        String htmlValidationMessage = required.booleanValue() ? "validationMessage='${validationMessage}'" : EMPTY_SPACE

        String html = "<select name = '${name}' id = '${name}' ${htmlClass} ${htmlTabIndex} ${htmlRequired} ${htmlValidationMessage}>\n" + SELECT_END
        String strOnChange = paramOnChange ? ",change: function(e) {${paramOnChange};}" : EMPTY_SPACE
        String strDefaultValue = defaultValue ? defaultValue : EMPTY_SPACE

        if (showHints.booleanValue()) {
            lstRole = roleService.listForKendoDropdown(lstRole, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }
        String jsonData = lstRole as JSON

        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    \$('#${escapeChar(name)}').kendoDropDownList({
                        dataTextField:'name',
                        dataValueField:'${value}',
                        dataSource:${jsonData},
                        value:'${strDefaultValue}'
                        ${strOnChange}
                    });
                });
                ${dataModelName} = \$("#${escapeChar(name)}").data("kendoDropDownList");
            </script>
        """
        return html + script
    }

    private String escapeChar(String str) {
        return str.replace(SINGLE_DOT, ESCAPE_DOT)
    }
}
