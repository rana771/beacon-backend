package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppVendorActionServiceModel

class ListAppVendorActionServiceModelService extends BaseService {


    public ListAppVendorActionServiceModel read(long id) {
        return ListAppVendorActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListAppVendorActionServiceModel.SQL_VENDOR_MODEL)
    }
}
