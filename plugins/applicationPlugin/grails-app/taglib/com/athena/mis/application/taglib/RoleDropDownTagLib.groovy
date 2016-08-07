package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownRoleForCompanyUserTagLibActionService
import com.athena.mis.application.actions.taglib.GetDropDownRoleForInvitationTagLibActionService
import com.athena.mis.application.actions.taglib.GetDropDownRoleTagLibActionService

class RoleDropDownTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownRoleTagLibActionService getDropDownRoleTagLibActionService

    /**
     * Render html select of role
     * example: <app:dropDownRole name="roleId"}"></app:dropDownRole>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText - No selection text (Default is Please Select...)
     * @attr showHints - Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     * @attr addAllAttributes - default value false
     *      - if true add all attributes of project in list
     */
    def dropDownRole = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownRoleTagLibActionService, attrs)
        out << (String) attrs.html
    }

    GetDropDownRoleForCompanyUserTagLibActionService getDropDownRoleForCompanyUserTagLibActionService
    /**
     * Render html select of Role by user id
     * example: <app:dropDownRoleForCompanyUser user_id="${userId}"></app:dropDownRoleForCompanyUser>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name & id of html component
     * @attr user_id REQUIRED - id of user
     * @attr plugin_id - id of plugin
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
     */
    def dropDownRoleForCompanyUser = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownRoleForCompanyUserTagLibActionService, attrs)
        out << (String) attrs.html
    }

    GetDropDownRoleForInvitationTagLibActionService getDropDownRoleForInvitationTagLibActionService

    /**
     * Render html for Role dropdown for send invitation
     *
     * @attr name REQUIRED - name & id of html component
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText - No selection text (Default is Please Select...)
     * @attr showHints - Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr reserved_value - boolean value (true/false), if true value will be role_type_id
     * @attr required - boolean value (true/false), if true append required
     * @attr is_invitation - boolean value (true/false), true if came from send invitation
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     */
    def dropdownRoleInvitation = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownRoleForInvitationTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
