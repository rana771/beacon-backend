package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownAppServerInstanceTagLibActionService

class AppServerInstanceDropDownTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownAppServerInstanceTagLibActionService getDropDownAppServerInstanceTagLibActionService

    /**
     * Render html select of AppDbInstance
     * example: <app:dropDownAppServerInstance id="appServerInstanceId" name="appServerInstanceId"  data_model_name="dropDownServerInstance"></app:dropDownAppServerInstance>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     * @attr is_tested - if true then shows only successfully tested serverInstances; if false shows all serverInstances
     */
    def dropDownAppServerInstance = { attrs, body ->
        attrs.body = body
        executeTag(getDropDownAppServerInstanceTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
