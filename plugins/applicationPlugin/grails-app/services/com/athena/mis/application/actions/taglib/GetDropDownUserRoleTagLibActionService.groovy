package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppUserService
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger

/*Renders html of 'select' for AppUser objects by role id*/

class GetDropDownUserRoleTagLibActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String NAME = 'name'
    private static final String ID = 'id'
    private static final String URL = 'url'
    private static final String ON_CHANGE = 'onchange'
    private static final String ROLE_ID = 'role_id'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'

    AppUserService appUserService

    /**
     * Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     *
     * @param parameters - a map of given attributes
     * @return - a map containing all necessary properties with value
     */
    public Map executePreCondition(Map params) {
        try {
            String strRoleId = params.get(ROLE_ID)
            String name = params.get(NAME)
            String id = params.get(ID)
            String url = params.get(URL)
            String dataModelName = params.get(DATA_MODEL_NAME)

            // check required parameters
            if ((!id) || (!name) || (!dataModelName) || (!strRoleId) || (strRoleId.length() == 0) || (!url) || (url.length() == 0)) {
                return setError(params, ERROR_FOR_INVALID_INPUT)
            }

            long roleId = Long.parseLong(strRoleId)
            params.put(ROLE_ID, new Long(roleId))               // set the roleId

            // Set default values for optional parameters
            params.put(HINTS_TEXT, params.hints_text ? params.hints_text : PLEASE_SELECT)
            params.put(SHOW_HINTS, params.show_hints ? new Boolean(Boolean.parseBoolean(params.show_hints.toString())) : Boolean.TRUE)
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validationmessage ? params.validationmessage : DEFAULT_MESSAGE)

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
     * Get the list of AppUser objects by roleId
     *  build the html for 'select'
     *
     * @param parameters - map returned from preCondition
     * @return - html string for 'select'
     */
    public Map execute(Map result) {
        try {
            Long roleId = (Long) result.get(ROLE_ID)
            List<GroovyRowResult> lstAppUser = (List<GroovyRowResult>) listAppUser(roleId)
            String html = buildDropDown(lstAppUser, result)
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
     * get list of User by roleId
     *
     * @param roleId - roleId
     * @return lstAppUser - List of User
     */
    private List<GroovyRowResult> listAppUser(long roleId) {
        long companyId = super.getCompanyId()
        String queryForList = """
            SELECT id, username || ' (' || id || ')' as name
            FROM app_user
            WHERE company_id = :companyId
            AND enabled = true
            AND is_reseller = false
            AND is_system_user = false
            AND id NOT IN
            (
                SELECT user_id
                FROM user_role
                WHERE role_id = :roleId
            )
            ORDER BY username
        """
        Map queryParams = [roleId: roleId, companyId: companyId]
        List<GroovyRowResult> lstAppUser = executeSelectSql(queryForList, queryParams)
        return lstAppUser
    }

    private static final String SELECT_END = "</select>"

    /**
     * Generate the html for select
     *  1.set attributes to strAttributes
     *  2.set value for refresh dropdown in strDefaultValue
     *  3.Build list for kendo dropdown
     *
     * @param lstAppUser - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<GroovyRowResult> lstAppUser, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)
        String strDefaultValue = defaultValue ? defaultValue : EMPTY_SPACE

        Map attributes = (Map) dropDownAttributes
        String strAttributes = EMPTY_SPACE
        attributes.each {
            if (it.value) {
                strAttributes = strAttributes + "${it.key} = '${it.value}' "
            }
        }
        if (defaultValue) {
            AppUser user = appUserService.read(defaultValue)
            lstAppUser << [id: user.id, name: user.username]
            strDefaultValue = defaultValue
        }
        String html = "<select ${strAttributes}>\n" + SELECT_END
        String strOnChange = paramOnChange ? "change: function(e) {${paramOnChange};}" : EMPTY_SPACE

        if (showHints.booleanValue()) {
            lstAppUser = appUserService.listForKendoDropdown(lstAppUser, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }
        String jsonData = lstAppUser as JSON
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
                            value:'${strDefaultValue}',
                            ${strOnChange}
                        });
                });
                ${dataModelName} = \$("#${name}").data("kendoDropDownList");
            </script>
        """
        return html + script
    }
}
