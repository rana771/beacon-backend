package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetModalHtmlTagLibActionService

class RenderModalHtmlTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetModalHtmlTagLibActionService getModalHtmlTagLibActionService

    /**
     * Render plugin wise html of modal
     * example: <app:renderModalHtml pluginId="${AccPluginConnector.PLUGIN_ID}"}"></app:renderModalHtml>
     *
     * @attr pluginId REQUIRED - id of plugin
     */
    def renderModalHtml = { attrs, body ->
        attrs.body = body
        super.executeTag(getModalHtmlTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
