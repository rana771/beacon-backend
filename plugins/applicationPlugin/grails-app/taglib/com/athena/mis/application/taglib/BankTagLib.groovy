package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownBankTagLibActionService
import com.athena.mis.application.actions.taglib.GetSystemBankTagLibActionService

class BankTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownBankTagLibActionService getDropDownBankTagLibActionService
    GetSystemBankTagLibActionService getSystemBankTagLibActionService

    /**
     * Render html select of Bank
     * example: <app:dropDownBank name="bankId"}"></app:dropDownBank>
     *
     * @attr name REQUIRED - name of html component
     * @attr id REQUIRED - id of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationmessage - validation message to be shown (Default is 'Required')
     * @attr url - url to reload dropDown
     * @attr process - reserved id of RmsProcessType
     * @attr instrument - reserved id of RmsInstrumentType
     * @attr country_id - if null get native banks otherwise get bank by countryId
     * @attr disable_system_bank - boolean value (true/false)
     * @attr data-bind - bind data for kendo
     */
    def dropDownBank = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownBankTagLibActionService, attrs)
        out << (String) attrs.html
    }

    /**
     * Render hidden input html of system bank id
     * @attr name REQUIRED - name & id of html component
     */
    def systemBank = { attrs, body ->
        attrs.body = body
        super.executeTag(getSystemBankTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
