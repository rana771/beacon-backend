package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppMessageActionServiceModel

class ListAppMessageActionServiceModelService extends BaseService {

    public ListAppMessageActionServiceModel read(long id) {
        return ListAppMessageActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListAppMessageActionServiceModel.SQL_LIST_APP_MESSAGE_MODEL)
    }
}
