package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListItemTypeActionServiceModel

class ListItemTypeActionServiceModelService extends BaseService {

    public ListItemTypeActionServiceModel read(long id) {
        return ListItemTypeActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListItemTypeActionServiceModel.SQL_LIST_ITEM_TYPE_MODEL)
    }
}
