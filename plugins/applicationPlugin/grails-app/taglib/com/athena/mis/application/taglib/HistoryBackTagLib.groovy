package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.HistoryBackTaglibActionService

class HistoryBackTagLib extends BaseTagLibExecutor {

    static namespace = "app"
    HistoryBackTaglibActionService historyBackTaglibActionService

    /**
     *
     * @attr o_id REQUIRED - parent id
     * @attr url REQUIRED - where to back
     * @attr c_id - (optional) for second stage navigation
     * @attr p_url - (optional) for second stage navigation
     * @attr plugin - (optional) plugin id (used to back from system entity to system entity type)
     */
    def historyBack = { attrs, body ->
        attrs.body = body
        super.executeTag(historyBackTaglibActionService, attrs)
        out << (String) attrs.html?attrs.html:""
    }
}
