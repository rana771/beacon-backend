package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownCompanyTagLibActionService

class CompanyDropDownTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownCompanyTagLibActionService getDropDownCompanyTagLibActionService

    /**
     * Render html select of company
     * example: <app:dropDownCompany name="companyId"}"></app:dropDownCompany>
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
    def dropDownCompany = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownCompanyTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
