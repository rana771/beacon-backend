package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownContentCategoryTagLibActionService

class ContentCategoryDropDownTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownContentCategoryTagLibActionService getDropDownContentCategoryTagLibActionService

    /**
     * Render html select of category for the given content_type_id id (optional: Document, Image etc.)
     * example: <app:dropDownContentCategory typeId="1201"></app:dropDownContentCategory>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr url REQUIRED - controller action to render this taglib
     * @attr content_type_id - id of content type (e.g. Document, Image etc.)
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validation_message - validation message to be shown (Default is 'Required')
     */
    def dropDownContentCategory = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownContentCategoryTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
