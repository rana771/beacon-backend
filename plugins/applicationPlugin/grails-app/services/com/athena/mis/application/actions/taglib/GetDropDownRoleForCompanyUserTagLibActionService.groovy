package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Role
import com.athena.mis.application.service.RoleService
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/*Renders html of 'select' for Role objects by user id*/
class GetDropDownRoleForCompanyUserTagLibActionService extends BaseService implements ActionServiceIntf {

    RoleService roleService

    private static final String NAME = 'name'
    private static final String ID = 'id'
    private static final String URL = 'url'
    private static final String ON_CHANGE = 'onchange'
    private static final String USER_ID = 'user_id'
    private static final String PLUGIN_ID = 'plugin_id'
    private static final String HINTS_TEXT = 'hints_text'
    private static final String SHOW_HINTS = 'show_hints'
    private static final String DEFAULT_VALUE = 'default_value'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationmessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'data_model_name'

    private Logger log = Logger.getLogger(getClass())

    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @return - a map containing all necessary properties with value
     */
    public Map executePreCondition(Map params) {
        try {
            if (!params.id || !params.name || !params.data_model_name || !params.url) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            params.put(USER_ID, Long.parseLong(params.user_id.toString()))
            // Set default values for optional parameters

            if (params.default_value) {
                String strDefaultValue = params.get(DEFAULT_VALUE)
                long defaultValue = Long.parseLong(strDefaultValue)
                params.put(DEFAULT_VALUE, new Long(defaultValue))
            }
            if (params.plugin_id) {
                String strPluginId = params.get(PLUGIN_ID)
                long pluginId = Long.parseLong(strPluginId)
                params.put(PLUGIN_ID, new Long(pluginId))
            }

            if (params.required) {
                boolean required = Boolean.parseBoolean(params.required)
                params.put(REQUIRED, new Boolean(required))
            } else {
                params.put(REQUIRED, Boolean.FALSE)
            }

            if(params.show_hints) {
                params.put(SHOW_HINTS, Boolean.parseBoolean(params.show_hints))
            } else{
                params.put(SHOW_HINTS, Boolean.TRUE)
            }

            if (!params.hints_text) {
                params.put(HINTS_TEXT, PLEASE_SELECT)
            }
            if (params.validationmessage) {
                params.put(VALIDATION_MESSAGE, params.validationmessage)
            }else {
                params.put(VALIDATION_MESSAGE, DEFAULT_MESSAGE)
            }

            return params

        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /** Get the list of Role objects by user id
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @return - html string for 'select'
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            Long userId = (Long) result.get(USER_ID)
            Long pluginId = (Long) result.get(PLUGIN_ID)
            List<GroovyRowResult> lstRole = []
            if (pluginId > 0) {
                lstRole = (List<GroovyRowResult>) listRole(userId, pluginId)
            }
            String html = buildDropDown(lstRole, result)
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

    private List<GroovyRowResult> listRole(long userId, long pluginId) {
        String queryForList = """
            SELECT role.id, role.name
            FROM role
            LEFT JOIN app_user ON app_user.id = :userId
            WHERE role.company_id = app_user.company_id
            AND role.is_reseller = false
            AND role.plugin_id = :pluginId
            AND role.id NOT IN
                (
                    SELECT role_id
                    FROM user_role
                    WHERE user_id = :userId
                )
            ORDER BY name
        """
        Map queryParams = [
                userId   : userId,
                pluginId: pluginId
        ]
        List<GroovyRowResult> lstRole = executeSelectSql(queryForList, queryParams)
        return lstRole
    }

    private static final String SELECT_END = "</select>"

    /** Generate the html for select
     * @param lstRole - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<GroovyRowResult> lstRole, Map dropDownAttributes) {
        // read map values
        String name = dropDownAttributes.get(NAME)
        String dataModelName = dropDownAttributes.get(DATA_MODEL_NAME)
        String hintsText = dropDownAttributes.get(HINTS_TEXT)
        Boolean showHints = dropDownAttributes.get(SHOW_HINTS)
        String paramOnChange = dropDownAttributes.get(ON_CHANGE)
        Long defaultValue = (Long) dropDownAttributes.get(DEFAULT_VALUE)
        String strDefaultValue = EMPTY_SPACE

        Map attributes = (Map) dropDownAttributes
        String strAttributes = EMPTY_SPACE
        attributes.each {
            strAttributes = strAttributes + "${it.key} = '${it.value}' "
        }

        if (defaultValue) {
            Role role = roleService.read(defaultValue)
            lstRole << [id: role.id, name: role.name]
            strDefaultValue = defaultValue
        }

        if (showHints.booleanValue()) {
            lstRole = roleService.listForKendoDropdown(lstRole, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }
        String strOnChange = paramOnChange ? ",change: function(e) {${paramOnChange};}" : EMPTY_SPACE

        String html = "<select ${strAttributes}>\n" + SELECT_END
        String jsonData = lstRole as JSON
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
                        value:'${strDefaultValue}'
                        ${strOnChange}
                    });
                    ${dataModelName} = \$("#${name}").data("kendoDropDownList");
                });
            </script>
        """

        return html + script
    }
}
