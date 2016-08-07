package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetTestDataGridTagLibActionService

class TestDataTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetTestDataGridTagLibActionService getTestDataGridTagLibActionService

    /**
     * Render test data kendo grid
     * @attr id REQUIRED - id of html content
     * @attr grid_model REQUIRED - variable for the grid model
     * @attr on_create REQUIRED - the javascript function to call for create test data
     * @attr on_delete REQUIRED - the javascript function to call for delete test data
     * @attr on_select - the javascript function to call for on select row
     */
    def testDataGrid = { attrs, body ->
        attrs.body = body
        executeTag(getTestDataGridTagLibActionService, attrs)
        out << attrs.html.toString()
    }
}
