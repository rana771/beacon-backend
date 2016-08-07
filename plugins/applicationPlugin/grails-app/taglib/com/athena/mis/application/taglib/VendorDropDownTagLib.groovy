package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownVendorTagLibActionService

class VendorDropDownTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownVendorTagLibActionService getDropDownVendorTagLibActionService

    /**
     * Render html select of Vendor
     * example: <app:dropDownVendor id="vendorId" name="vendorId"  data_model_name="dropDownVendor"></app:dropDownVendor>
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
     * @attr validation_message - validation message to be shown (Default is 'Required')
     */
    def dropDownVendor = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownVendorTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
