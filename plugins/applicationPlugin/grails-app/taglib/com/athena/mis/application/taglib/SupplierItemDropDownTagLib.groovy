package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownSupplierItemTagLibActionService

class SupplierItemDropDownTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownSupplierItemTagLibActionService getDropDownSupplierItemTagLibActionService

    /**
     * Render html select of item for the given type id (optional: Material, Fixed Asset etc.)
     * example: <app:dropDownItem typeId="401"></app:dropDownItem>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr url REQUIRED - controller action to render this taglib
     * @attr supplier_id - id of supplier
     * @attr class - css or validation class
     * @attr type_id - id of item type (e.g. Material, Fixed Asset etc.)
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validation_message - validation message to be shown (Default is 'Required')
     */
    def dropDownSupplierItem = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownSupplierItemTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
