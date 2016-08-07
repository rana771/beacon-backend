package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownModulesTagLibActionService

class ModulesTagLib extends BaseTagLibExecutor {
    static namespace = "app"

    GetDropDownModulesTagLibActionService getDropDownModulesTagLibActionService

    /**
     * Render html select of module
     * example: <app:dropDownModules name="pluginId"></app:dropDownModules>
     *
     * @attr name REQUIRED - name of html component
     * @attr id REQUIRED - id of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text -No selection text (Default is Please Select...)
     * @attr show_hints -Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationmessage - validation message to be shown (Default is 'Required')
     * @attr data-bind - bind with kendo observable
     * @attr show_version - (true/false) show current config version of plugin if true; default value false
     * @attr is_mapped - true means role and module mapping
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr role_id - if roleId then show modules that are mapped with the role
     * @attr url - url for reload
     */
    def dropDownModules = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownModulesTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
