package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListItemCategoryInventoryActionServiceModel

class ListItemCategoryInventoryActionServiceModelService extends BaseService {

    public ListItemCategoryInventoryActionServiceModel read(long id) {
        return ListItemCategoryInventoryActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListItemCategoryInventoryActionServiceModel.SQL_LIST_ITEM_CATEGORY_INVENTORY_MODEL)
    }
}
