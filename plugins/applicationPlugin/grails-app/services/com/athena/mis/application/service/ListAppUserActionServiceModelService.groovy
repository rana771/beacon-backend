package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppUserActionServiceModel

class ListAppUserActionServiceModelService extends BaseService {

    public ListAppUserActionServiceModel read(long id) {
        return ListAppUserActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListAppUserActionServiceModel.SQL_LIST_APP_USER_MODEL)
    }
}
