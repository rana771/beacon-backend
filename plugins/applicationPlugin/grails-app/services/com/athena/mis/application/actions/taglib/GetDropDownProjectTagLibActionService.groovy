package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.service.ProjectService
import com.athena.mis.utility.DateUtility
import grails.converters.JSON
import org.apache.log4j.Logger

/*Renders html of 'select' for Project objects */

class GetDropDownProjectTagLibActionService extends BaseService implements ActionServiceIntf {

    ProjectService projectService

    private static final String NAME = 'name'
    private static final String CLASS = 'class'
    private static final String TAB_INDEX = 'tabindex'
    private static final String ON_CHANGE = 'onchange'
    private static final String FILTER_MAPPING = 'filterMapping'
    private static final String ADD_ALL_ATTRIBUTES = 'addAllAttributes'
    private static final String HINTS_TEXT = 'hintsText'
    private static final String SHOW_HINTS = 'showHints'
    private static final String DEFAULT_VALUE = 'defaultValue'
    private static final String PLEASE_SELECT = 'Please Select...'
    private static final String REQUIRED = 'required'
    private static final String VALIDATION_MESSAGE = 'validationMessage'
    private static final String DEFAULT_MESSAGE = 'Required'
    private static final String DATA_MODEL_NAME = 'dataModelName'
    private static final String DATA_BIND = 'data-bind'

    private Logger log = Logger.getLogger(getClass())

    /** Build a map containing properties of html select
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @return - a map containing all necessary properties with value
     */
    public Map executePreCondition(Map params) {
        try {
            String name = params.get(NAME)
            String dataModelName = params.get(DATA_MODEL_NAME)
            if ((!name) || (!dataModelName)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }

            // Set default values for optional parameters
            params.put(HINTS_TEXT, params.hintsText ? params.hintsText : PLEASE_SELECT)
            params.put(SHOW_HINTS, params.showHints ? new Boolean(Boolean.parseBoolean(params.showHints.toString())) : Boolean.TRUE)
            params.put(REQUIRED, params.required ? new Boolean(Boolean.parseBoolean(params.required.toString())) : Boolean.FALSE)
            params.put(VALIDATION_MESSAGE, params.validationMessage ? params.validationMessage : DEFAULT_MESSAGE)
            params.put(FILTER_MAPPING, params.filterMapping ? new Boolean(Boolean.parseBoolean(params.filterMapping.toString())) : Boolean.TRUE)
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

    /** Get the list of Projects
     *  build the html for 'select'
     * @param parameters - map returned from preCondition
     * @return - html string for 'select'
     */
    public Map execute(Map result) {
        try {
            Boolean filterMapping = (Boolean) result.get(FILTER_MAPPING)
            Boolean addAllAttributes = (Boolean) result.get(ADD_ALL_ATTRIBUTES)
            List<Project> lstProject = listProject(filterMapping)
            List lstForDropDown = buildListForDropDown(lstProject, addAllAttributes)
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

    /** Get the list of projects
     * @param filterMapping - true/false
     * @return - list of projects
     */
    private List<Project> listProject(boolean filterMapping) {
        List<Project> lstProject
        if (filterMapping) {
            lstProject = (List<Project>) appSessionService.getUserProjects()
        } else {
            lstProject = projectService.list()
        }
        return lstProject
    }

    /**
     * Build list for drop down
     * @param lstProject - list of project
     * @param addAllAttributes - boolean value (true/false)
     * @return - list for drop down
     */
    private List buildListForDropDown(List<Project> lstProject, boolean addAllAttributes) {
        List lstDropDown = []
        if ((lstProject == null) || (lstProject.size() <= 0))
            return lstDropDown
        if (addAllAttributes) {
            for (int i = 0; i < lstProject.size(); i++) {
                Project project = lstProject[i]
                lstDropDown << [id       : project.id,
                                name     : project.name,
                                createdon: DateUtility.getDateForUI(project.createdOn),
                                startDate: DateUtility.getDateForUI(project.startDate),
                                endDate  : DateUtility.getDateForUI(project.endDate)
                ]
            }
        } else {
            for (int i = 0; i < lstProject.size(); i++) {
                Project project = lstProject[i]
                lstDropDown << [id: project.id, name: project.name]
            }
        }
        return lstDropDown
    }

    private static final String SELECT_END = "</select>"

    /** Generate the html for select
     * @param lstProject - list for select 'options'
     * @param dropDownAttributes - a map containing the attributes of drop down
     * @return - html string for select
     */
    private String buildDropDown(List lstProject, Map dropDownAttributes) {
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
            lstProject = projectService.listForKendoDropdown(lstProject, null, hintsText)
            // the null parameter indicates that dataTextField is name
        }
        String jsonData = lstProject as JSON

        String script = """ \n
            <script type="text/javascript">
                \$(document).ready(function () {
                    \$('#${name}').kendoDropDownList({
                        dataTextField:'name',
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
