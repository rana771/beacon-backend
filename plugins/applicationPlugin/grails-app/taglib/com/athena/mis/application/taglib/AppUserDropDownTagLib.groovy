package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownAppUserTagLibActionService
import com.athena.mis.application.actions.taglib.GetDropDownUserRoleTagLibActionService

class AppUserDropDownTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownAppUserTagLibActionService getDropDownAppUserTagLibActionService
    GetDropDownUserRoleTagLibActionService getDropDownUserRoleTagLibActionService

    /**
     * Render html select of appUser
     * example: <app:dropDownAppUser name="userId"}"></app:dropDownAppUser>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText - No selection text (Default is Please Select...)
     * @attr showHints - Hints-text will be shown (Default is 'true')
     * @attr isShowId REQUIRED - boolean value (true/false), if true show user id with user name
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     * @attr moduleId - if moduleId then show user of those role associated with the moduleId
     */
    def dropDownAppUser = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownAppUserTagLibActionService, attrs)
        out << (String) attrs.html
    }

    /**
     * Render html select of AppUser by role_id
     * example: <app:dropDownAppUserRole role_id="1"></app:dropDownAppUserRole>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name of html component
     * @attr role_id REQUIRED - role_id of UserRole (UserRole.role_id)
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr url REQUIRED - url for data source
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationmessage - validation message to be shown (Default is 'Required')
     * @attr data-bind - bind with kendo observable
     */
    def dropDownAppUserRole = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownUserRoleTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
