package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.RenderDocMenuTagLibActionService

class DocMenuTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    RenderDocMenuTagLibActionService renderDocMenuTagLibActionService

    /**
     * render html of doc menu
     * @attr plugin_id REQUIRED - Plugin id of corresponding plugin
     * @attr depends_on - Plugin id of other plugin
     * @attr caption - caption of menu
     */
    def renderDocMenu = { attrs, body ->
        attrs.body = body
        executeTag(renderDocMenuTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
