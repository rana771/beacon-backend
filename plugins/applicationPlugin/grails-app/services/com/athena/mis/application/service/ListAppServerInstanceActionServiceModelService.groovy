package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppServerInstanceActionServiceModel

class ListAppServerInstanceActionServiceModelService extends BaseService {


    public ListAppServerInstanceActionServiceModel read(long id) {
        return ListAppServerInstanceActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListAppServerInstanceActionServiceModel.SQL_LIST_APP_SERVER_INSTANCE_MODEL)
    }
}
