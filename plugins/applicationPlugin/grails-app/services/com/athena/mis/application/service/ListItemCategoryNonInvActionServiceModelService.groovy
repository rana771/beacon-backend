package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListItemCategoryNonInvActionServiceModel

class ListItemCategoryNonInvActionServiceModelService extends BaseService {

    public ListItemCategoryNonInvActionServiceModel read(long id) {
        return ListItemCategoryNonInvActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListItemCategoryNonInvActionServiceModel.SQL_LIST_ITEM_CATEGORY_NON_INVENTORY_MODEL)
    }

}
