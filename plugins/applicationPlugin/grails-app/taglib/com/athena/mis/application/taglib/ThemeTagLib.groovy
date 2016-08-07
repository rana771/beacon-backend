package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetThemeContentTagLibActionService

/**
 * ThemeTagLib methods used to render appTheme content(text,css etc.) for a given key
 */
class ThemeTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetThemeContentTagLibActionService getThemeContentTagLibActionService

    /**
     * Shows different contents of appTheme e.g. companyImage, copyright text
     * attr takes the attribute 'name'
     * example: <app:themeContent name="copyrightText"></app:themeContent>
     *
     * @attr id - name of html component
     * @attr url - url to reload
     * @attr plugin_id REQUIRED - plugin id
     * @attr name REQUIRED -the key name
     * @attr css -if true, then appTheme content will be enclosed by style tag
     */

    def themeContent = { attrs, body ->
        attrs.body = body
        attrs.request = request
        super.executeTag(getThemeContentTagLibActionService, attrs)
        out << (String) attrs.html
    }

}
