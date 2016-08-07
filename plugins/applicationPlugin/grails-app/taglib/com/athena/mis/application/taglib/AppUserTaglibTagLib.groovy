package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetSwitchUserTagLibActionService

class AppUserTaglibTagLib extends BaseTagLibExecutor {
    static namespace = "app"

    GetSwitchUserTagLibActionService getSwitchUserTagLibActionService

    /**
     * render switch user menu
     */
    def switchUser = { attrs, body ->
        attrs.body = body
        super.executeTag(getSwitchUserTagLibActionService, attrs)
        out << attrs.html.toString()
    }
}
