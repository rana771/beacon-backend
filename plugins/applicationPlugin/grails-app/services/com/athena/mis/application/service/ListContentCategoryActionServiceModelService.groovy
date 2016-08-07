package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListContentCategoryActionServiceModel

class ListContentCategoryActionServiceModelService extends BaseService {

    public ListContentCategoryActionServiceModel read(long id) {
        return ListContentCategoryActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListContentCategoryActionServiceModel.SQL_LIST_CONTENT_CATEGORY_MODEL)
    }
}
