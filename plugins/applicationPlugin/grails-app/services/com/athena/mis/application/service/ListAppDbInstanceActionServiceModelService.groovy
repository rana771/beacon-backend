package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppDbInstanceActionServiceModel

class ListAppDbInstanceActionServiceModelService extends BaseService {

    public ListAppDbInstanceActionServiceModel read(long id) {
        return ListAppDbInstanceActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListAppDbInstanceActionServiceModel.SQL_DB_INSTANCE_MODEL)
    }
}
