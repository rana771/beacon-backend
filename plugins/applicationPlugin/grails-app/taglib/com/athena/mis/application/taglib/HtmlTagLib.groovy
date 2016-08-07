package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.RenderAppFormRemoteTagLibActionService
import org.codehaus.groovy.grails.web.taglib.GroovyPageAttributes
import org.codehaus.groovy.grails.web.taglib.GroovyPageTagBody

class HtmlTagLib extends BaseTagLibExecutor {

    static namespace = "app"
    RenderAppFormRemoteTagLibActionService renderAppFormRemoteTagLibActionService

    /**
     * Render form to with self ajax submit mechanism
     *
     * @attr name REQUIRED - name of the form control
     * @attr url REQUIRED - url to submit the ajax request e.g.: '/myController/myAction'
     * @attr id  - optional, if not given then id=name
     * @attr update - name of the container that will be updated on success
     * @attr before - the javascript function to call before submission
     * @attr after - the javascript function to call after submission
     * @attr onLoading - js function to call immediately before ajax call
     * @attr onFailure - js function to call on ajax failure
     * @attr onComplete - js function to call on ajax complete
     * @attr class - css class of form
     */
    def formRemote = { GroovyPageAttributes attrs, GroovyPageTagBody body ->
        attrs.body = body
//        Map params = [attrs: attrs, body: body]
        super.executeTag(renderAppFormRemoteTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
