package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListSupplierActionServiceModel

class ListSupplierActionServiceModelService extends BaseService {

    public ListSupplierActionServiceModel read(long id) {
        return ListSupplierActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListSupplierActionServiceModel.SQL_LIST_SUPPLIER_MODEL)
    }
}
