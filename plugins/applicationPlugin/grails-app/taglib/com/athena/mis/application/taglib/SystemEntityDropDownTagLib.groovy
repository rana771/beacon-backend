package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownSystemEntityTagLibActionService

class SystemEntityDropDownTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownSystemEntityTagLibActionService getDropDownSystemEntityTagLibActionService

    /**
     * Render html select of systemEntity for the given systemEntityType id
     * example: <app:dropDownSystemEntity typeId="${AppSystemEntityCacheService.SYSTEM_ENTITY_TYPE_UNIT}"></app:dropDownSystemEntity>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr typeId REQUIRED - id of systemEntityType domain
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr pluginId REQUIRED - id of plugin
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText -No selection text (Default is Please Select...)
     * @attr showHints -Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr elements - List of individual elements to populate (reserved SystemEntity.id)
     * (objects should be available in List of given type)
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     * @attr data-bind - bind with kendo observable
     */

    def dropDownSystemEntity = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownSystemEntityTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
