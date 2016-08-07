package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListForComposeSmsActionServiceModel

class ListForComposeSmsActionServiceModelService extends BaseService {

    public ListForComposeSmsActionServiceModel read(long id) {
        return ListForComposeSmsActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListForComposeSmsActionServiceModel.SQL_LIST_COMPOSE_SMS_MODEL)
    }
}
