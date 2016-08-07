package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListCompanyForResellerActionServiceModel

class ListCompanyForResellerActionServiceModelService extends BaseService {

    public ListCompanyForResellerActionServiceModel read(long id) {
        return ListCompanyForResellerActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListCompanyForResellerActionServiceModel.SQL_LIST_COMPANY_FOR_RESELLER_MODEL)
    }
}
