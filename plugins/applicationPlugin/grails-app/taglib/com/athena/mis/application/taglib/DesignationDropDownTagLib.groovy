package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownDesignationTagLibActionService

class DesignationDropDownTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownDesignationTagLibActionService getDropDownDesignationTagLibActionService

    /**
     * Render html select of designation
     * example: <app:dropDownDesignation name="designationId"}"></app:dropDownDesignation>
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
     * @attr data-bind - bind with kendo observable
     */
    def dropDownDesignation = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownDesignationTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
