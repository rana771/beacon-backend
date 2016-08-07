package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.AppUserService
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger

/*Renders html of 'select' for AppUser objects by entity type id and entity id*/

class GetDropDownAppUserEntityTagLibActionService extends BaseService implements ActionServiceIntf {

    AppUserService appUserService
    AppSystemEntityCacheService appSystemEntityCacheService

    private static final String NAME = 'name'
    private static final String ID = 'id'
    private static final String URL = 'url'
    private static final String ON_CHANGE = 'onchange'
    private static final String ENTITY_TYPE_ID = 'entity_type_id'
    private static final String ENTITY_ID = 'entity_id'
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
            String strEntityTypeId = params.get(ENTITY_TYPE_ID)
            String strEntityId = params.get(ENTITY_ID)
            String name = params.get(NAME)
            String id = params.get(ID)
            String url = params.get(URL)
            String dataModelName = params.get(DATA_MODEL_NAME)
            if ((!id) || (!name) || (!dataModelName) || (!strEntityTypeId) || (strEntityTypeId.length() == 0) || (!strEntityId) ||
                    (strEntityId.length() == 0) || (!url) || (url.length() == 0)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long entityTypeId = Long.parseLong(strEntityTypeId)
            long entityId = Long.parseLong(strEntityId)
            params.put(ENTITY_TYPE_ID, new Long(entityTypeId))  // set the entityTypeId
            params.put(ENTITY_ID, new Long(entityId))           // set the entityId

            // Set default values for optional parameters
            params.put(HINTS_TEXT, params.hints_text ? params.hints_text : PLEASE_SELECT)
            params.put(SHOW_HINTS, params.show_hints ? new Boolean(Boolean.parseBoolean(params.show_hints.toString())) : Boolean.TRUE)
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validationmessage ? params.validationmessage : DEFAULT_MESSAGE)

            if (params.default_value && params.default_value != "null") {
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

    /** Get the list of AppUser objects by entity type id and entity id
     *  build the html for 'select'
     * @param result - map returned from preCondition
     * @return - html string for 'select'
     */
    public Map execute(Map result) {
        try {
            Long entityTypeId = (Long) result.get(ENTITY_TYPE_ID)
            Long entityId = (Long) result.get(ENTITY_ID)
            List<GroovyRowResult> lstAppUser = (List<GroovyRowResult>) listAppUser(entityTypeId, entityId)
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

    private List<GroovyRowResult> listAppUser(long reservedId, long entityId) {
        long companyId = super.getCompanyId()
        SystemEntity entityTypeId = appSystemEntityCacheService.readByReservedId(reservedId, appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, companyId)
        String queryForList = """
            SELECT id, username || ' (' || id || ')' as name
            FROM app_user
            WHERE company_id = :companyId
            AND enabled = true
            AND is_system_user = 'f'
            AND id NOT IN
                (
                    SELECT app_user_id
                    FROM app_user_entity
                    WHERE entity_type_id = :entityTypeId
                    AND entity_id = :entityId
                    AND company_id = :companyId
                )
            ORDER BY name
        """
        Map queryParams = [
                entityTypeId: entityTypeId.id,
                entityId    : entityId,
                companyId   : companyId
        ]
        List<GroovyRowResult> lstAppUser = executeSelectSql(queryForList, queryParams)
        return lstAppUser
    }

    private static final String SELECT_END = "</select>"

    /** Generate the html for select
     * @param lstAppUser - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List<GroovyRowResult> lstAppUser, Map dropDownAttributes) {
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
            AppUser user = appUserService.read(defaultValue)
            lstAppUser << [id: user.id, name: user.username]
            strDefaultValue = defaultValue
        }

        if (showHints.booleanValue()) {
            lstAppUser = appUserService.listForKendoDropdown(lstAppUser, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }
        String strOnChange = paramOnChange ? ",change: function(e) {${paramOnChange};}" : EMPTY_SPACE

        String html = "<select ${strAttributes}>\n" + SELECT_END
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
