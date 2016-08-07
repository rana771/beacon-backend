package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.RoleModule
import com.athena.mis.application.entity.UserRole
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.RoleModuleService
import com.athena.mis.application.service.UserRoleService
import grails.converters.JSON
import org.apache.log4j.Logger

/*Renders html of 'select' for AppUser objects */

class GetDropDownAppUserTagLibActionService extends BaseService implements ActionServiceIntf {

    AppUserService appUserService
    RoleModuleService roleModuleService
    UserRoleService userRoleService

    private static final String NAME = 'name'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String ON_CHANGE = 'onchange'
    private static final String HINTS_TEXT = 'hintsText'
    private static final String SHOW_HINTS = 'showHints'
    private static final String DEFAULT_VALUE = 'defaultValue'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationMessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'dataModelName'
    private static final String SINGLE_DOT = '.'
    private static final String ESCAPE_DOT = '\\\\.'
    private static final String MODULE_ID = 'moduleId'
    private static final String IS_SHOW_ID = "isShowId"

    private Logger log = Logger.getLogger(getClass())

    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @param obj - N/A
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

            if (params.defaultValue && params.defaultValue != "null") {
                String strDefaultValue = params.get(DEFAULT_VALUE)
                long defaultValue = Long.parseLong(strDefaultValue)
                params.put(DEFAULT_VALUE, new Long(defaultValue))
            } else {
                params.put(DEFAULT_VALUE, null)
            }
            if (params.moduleId) {
                String strModuleId = params.get(MODULE_ID)
                long moduleId = Long.parseLong(strModuleId)
                params.put(MODULE_ID, new Long(moduleId))
            } else {
                params.put(MODULE_ID, new Long(0))
            }

            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /** Get the list of AppUsers
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @param obj - N/A
     * @return - html string for 'select'
     */
    public Map execute(Map result) {
        try {
            List<AppUser> lstAppUser = []
            Long moduleId = (Long) result.get(MODULE_ID)
            boolean isShowId = Boolean.parseBoolean(result.get(IS_SHOW_ID).toString())
            if (moduleId != 0) {
                List<RoleModule> lstRoleModule = roleModuleService.findAllByModuleId(moduleId)
                if (lstRoleModule.size() > 0) {
                    List<Long> lstRoleIds = lstRoleModule.collect { it.roleId }
                    List<UserRole> lstUserRole = userRoleService.findAllByRoleIdInList(lstRoleIds)
                    List<Long> lstUserIds = lstUserRole.collect { it.userId }
                    lstAppUser = appUserService.findAllByIdInListAndEnabled(lstUserIds, true)
                }
            } else {
                lstAppUser = appUserService.enableUserList()
            }
            List lstForDropDown = buildListForDropDown(lstAppUser, isShowId)
            String html = buildDropDown(lstForDropDown, result)
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
     * Build list for drop down
     * @param lstAppUser - list of AppUser
     * @return - list for drop down
     */
    private List buildListForDropDown(List<AppUser> lstAppUser, boolean isShowId) {
        List lstDropDown = []
        if ((lstAppUser == null) || (lstAppUser.size() <= 0))
            return lstDropDown
        if (isShowId) {
            for (int i = 0; i < lstAppUser.size(); i++) {
                AppUser user = lstAppUser[i]
                lstDropDown << [id: user.id, name: user.username + PARENTHESIS_START + user.id + PARENTHESIS_END]
            }
        } else {
            for (int i = 0; i < lstAppUser.size(); i++) {
                AppUser user = lstAppUser[i]
                lstDropDown << [id: user.id, name: user.username]
            }
        }

        return lstDropDown
    }

    private static final String SELECT_END = "</select>"

    /** Generate the html for select
     * @param lstAppUser - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List lstAppUser, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String paramClass = dropDownAttributes.get(CLASS)
        String paramTabIndex = dropDownAttributes.get(TAB_INDEX)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)
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
        String strOnChange = paramOnChange ? ",change: function(e) {${paramOnChange};}" : EMPTY_SPACE
        String strDefaultValue = defaultValue ? defaultValue : EMPTY_SPACE

        if (showHints.booleanValue()) {
            lstAppUser = appUserService.listForKendoDropdown(lstAppUser, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }
        String jsonData = lstAppUser as JSON

        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    \$('#${escapeChar(name)}').kendoDropDownList({
                        dataTextField:'name',
                        dataValueField:'id',
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
