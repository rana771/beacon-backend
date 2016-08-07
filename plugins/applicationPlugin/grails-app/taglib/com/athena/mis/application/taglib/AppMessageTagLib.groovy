package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetCountUnreadAppMessageTaglibActionService

class AppMessageTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetCountUnreadAppMessageTaglibActionService getCountUnreadAppMessageTaglibActionService

    /**
     * Render unread message count
     */

    def unReadMessage = { attrs, body ->
        attrs.body = body
        super.executeTag(getCountUnreadAppMessageTaglibActionService, attrs)
        out << (String) attrs.html
    }
}
