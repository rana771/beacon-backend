package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetSystemEntityByReservedTagLibActionService

class SystemEntityByReservedTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetSystemEntityByReservedTagLibActionService getSystemEntityByReservedTagLibActionService

    /**
     * Renders the id of system entity in a hidden field
     * example: <app:systemEntityByReserved typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT}"
     *          reservedId="${AppSystemEntityCacheService.SYS_ENTITY_CONTENT_DOCUMENT}"></app:systemEntityByReserved>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr typeId REQUIRED - type of system entity
     * @attr reservedId REQUIRED - reserved id of system entity
     * @attr pluginId REQUIRED - id of plugin
     */
    def systemEntityByReserved = { attrs, body ->
        attrs.body = body
        super.executeTag(getSystemEntityByReservedTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
