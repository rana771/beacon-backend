package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppServerDbInstanceMappingActionServiceModel

class ListAppServerDbInstanceMappingActionServiceModelService extends BaseService {

    public ListAppServerDbInstanceMappingActionServiceModel read(long id) {
        return ListAppServerDbInstanceMappingActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListAppServerDbInstanceMappingActionServiceModel.SQL_LIST_APP_SERVER_DB_INSTANCE_MAPPING_MODEL)
    }
}
