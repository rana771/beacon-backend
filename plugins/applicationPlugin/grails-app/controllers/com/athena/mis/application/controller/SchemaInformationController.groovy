package com.athena.mis.application.controller

import com.athena.mis.application.actions.schemainformation.ListSchemaInformationActionService

class SchemaInformationController extends BaseController{

    static allowedMethods = [
            listSchemaInfo: "POST"
    ]

    ListSchemaInformationActionService listSchemaInformationActionService

    def listSchemaInfo() {
        renderOutput(listSchemaInformationActionService, params)
    }

}
