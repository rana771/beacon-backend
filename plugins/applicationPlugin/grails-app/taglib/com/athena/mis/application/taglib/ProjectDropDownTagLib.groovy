package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownProjectTagLibActionService

class ProjectDropDownTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownProjectTagLibActionService getDropDownProjectTagLibActionService

    /**
     * Render html select of project
     * example: <app:dropDownProject filterMapping="false"}"></app:dropDownProject>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr filterMapping - default value true
     *      - if true shows list of all project mapped with user
     *      - if false shows list of all project by company id
     * @attr addAllAttributes - default value false
     *      - if true add all attributes of project in list
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText - No selection text (Default is Please Select...)
     * @attr showHints - Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     * @attr data-bind - bind with kendo observable
     */
    def dropDownProject = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownProjectTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
