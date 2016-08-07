package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDateControlTagLibActionService

class DateControlTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDateControlTagLibActionService getDateControlTagLibActionService

    /**
     * Renders date control
     * example: <app:dateControl value="${toDate}"></app:dateControl>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr value - date value to be shown (default is current date)
     * @attr onchange - method to call on change
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     * @attr diffWithCurrent - difference with current date
     * @attr placeholder - text to be shown
     * @attr disabled - disables the field
     * @attr data-bind - bind with kendo observable
     */
    def dateControl = { attrs, body ->
        attrs.body = body
        super.executeTag(getDateControlTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
