package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetSchemaInformationGridTagLibActionService

class SchemaInformationTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetSchemaInformationGridTagLibActionService getSchemaInformationGridTagLibActionService

    /**
     * Render schema information grid
     * @attr id REQUIRED - id of html component
     * @attr grid_model REQUIRED - variable for the grid model
     */
    def schemaInfoGrid = { attrs, body ->
        attrs.body = body
        executeTag(getSchemaInformationGridTagLibActionService, attrs)
        out << attrs.html.toString()
    }

}
