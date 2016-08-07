package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.SchemaInformationModel

class SchemaInformationModelService extends BaseService {

    public void createDefaultSchema() {
        executeSql(SchemaInformationModel.SQL_SCHEMA_INFO_MODEL)
    }
}
