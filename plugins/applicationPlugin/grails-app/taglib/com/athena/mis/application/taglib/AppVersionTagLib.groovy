package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.CheckAppVersionTagLibActionService

class AppVersionTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    CheckAppVersionTagLibActionService checkAppVersionTagLibActionService

    /**
     * Renders message depending on version check
     * example: <app:checkVersion pluginId="1"></app:checkVersion>
     *
     * @attr pluginId REQUIRED - plugin id
     */
    def checkVersion = { attrs, body ->
        attrs.body = body
        attrs.request = request
        super.executeTag(checkAppVersionTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
