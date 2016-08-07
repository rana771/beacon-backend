package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListSupplierItemActionServiceModel

class ListSupplierItemActionServiceModelService extends BaseService {

    public ListSupplierItemActionServiceModel read(long id) {
        return ListSupplierItemActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListSupplierItemActionServiceModel.SQL_LIST_SUPPLIER_ITEM_MODEL)
    }
}
