package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.AppMyFavouriteTagLibActionService

class AppMyFavouriteTagLib extends BaseTagLibExecutor {

    static namespace = "app"
    AppMyFavouriteTagLibActionService appMyFavouriteTagLibActionService

    /**
     * Render icon for bookmark
     * example: <app:myFavourite></app:myFavourite>
     */
    def myFavourite = { attrs, body->
        attrs.body = body
        attrs.request = request
        super.executeTag(appMyFavouriteTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
