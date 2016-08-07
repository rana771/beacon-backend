package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetAppFaqTagLibActionService

class AppFaqTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetAppFaqTagLibActionService getAppFaqTagLibActionService

    /**
     * Render faq
     * @attr id REQUIRED - id of html component
     * @attr datasource REQUIRED - variable for the dataSource
     * @attr entity_id REQUIRED - Entity.id
     * @attr entity_type_id REQUIRED - reservedId of entity type
     * @attr sort_order - Ordering asc/desc. default: asc
     * @attr title - Show title with domain property
     * @attr template - template of listView
     * @attr result_per_page  - result to show per page. default: 5
     * @attr url - url to reload entity notes
     */

    def appFaq = { attrs, body ->
        attrs.body = body
        super.executeTag(getAppFaqTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
