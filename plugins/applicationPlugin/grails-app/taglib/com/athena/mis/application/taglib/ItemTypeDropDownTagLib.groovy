package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownItemTypeTagLibActionService

class ItemTypeDropDownTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownItemTypeTagLibActionService getDropDownItemTypeTagLibActionService

    /**
     * Render html select of itemType for the given systemEntity id (optional: category id)
     * example: <app:dropDownItemType categoryId="${AppSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_INVENTORY}"></app:dropDownItemType>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr categoryId - id of systemEntity (e.g. Inventory, Non-Inventory, Fixed Asset)
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText -No selection text (Default is Please Select...)
     * @attr showHints -Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     * @attr data-bind - bind with kendo observable
     */
    def dropDownItemType = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownItemTypeTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
