package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListItemCategoryFixedAssetActionServiceModel

class ListItemCategoryFixedAssetActionServiceModelService extends BaseService {

    public ListItemCategoryFixedAssetActionServiceModel read(long id) {
        return ListItemCategoryFixedAssetActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListItemCategoryFixedAssetActionServiceModel.SQL_LIST_ITEM_CATEGORY_FIX_ASSET_MODEL)
    }
}
