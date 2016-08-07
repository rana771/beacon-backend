package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetAppNoteTagLibActionService

class AppNoteTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetAppNoteTagLibActionService getAppNoteTagLibActionService

    /**
     * Render task details
     * @attr id REQUIRED - id of html component
     * @attr datasource REQUIRED - variable for the dataSource
     * @attr entity_id REQUIRED - Entity.id
     * @attr entity_type_id REQUIRED - reservedId of entity type
     * @attr sort_order - Ordering asc/desc. default: asc
     * @attr title - Show title with domain property
     * @attr template - template of listView
     * @attr result_per_page  - result to show per page. default: 5
     * @attr url - url to reload entity notes
     * @attr hide_pager - if given then hide pager
     * @attr pager_id - id of pager (default is pagerNote)
     */

    def appNote = { attrs, body ->
        attrs.body = body
        super.executeTag(getAppNoteTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
