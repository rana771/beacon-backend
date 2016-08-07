package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListVendorForDplDashboardActionServiceModel

class ListVendorForDplDashboardActionServiceModelService extends BaseService {

    public ListVendorForDplDashboardActionServiceModel read(long id) {
        return ListVendorForDplDashboardActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListVendorForDplDashboardActionServiceModel.SQL_LIST_VENDOR_FOR_DPL_DASH_BOARD)
    }
}
