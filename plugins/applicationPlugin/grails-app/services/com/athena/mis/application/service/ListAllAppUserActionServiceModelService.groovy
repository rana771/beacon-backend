package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAllAppUserActionServiceModel

class ListAllAppUserActionServiceModelService extends BaseService {

    public ListAllAppUserActionServiceModel read(long id) {
        return ListAllAppUserActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListAllAppUserActionServiceModel.SQL_LIST_ALL_APP_USER_MODEL)
    }
}
