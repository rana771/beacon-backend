package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListSystemEntityTypeActionServiceModel

class ListSystemEntityTypeActionServiceModelService extends BaseService {
    public ListSystemEntityTypeActionServiceModel read(long id) {
        ListSystemEntityTypeActionServiceModel systemEntityType = ListSystemEntityTypeActionServiceModel.read(id)
        return systemEntityType
    }

    public void createDefaultSchema() {
        executeSql(ListSystemEntityTypeActionServiceModel.SQL_LIST_SYSTEM_ENTITY_TYPE_MODEL)
    }
}
