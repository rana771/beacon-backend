package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppUserEntityActionServiceModel

class ListAppUserEntityActionServiceModelService extends BaseService {

    public ListAppUserEntityActionServiceModel read(long id) {
        return ListAppUserEntityActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListAppUserEntityActionServiceModel.SQL_APP_USER_ENTITY_MODEL)
    }
}
