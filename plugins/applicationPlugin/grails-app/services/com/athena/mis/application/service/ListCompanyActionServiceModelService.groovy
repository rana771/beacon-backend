package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListCompanyActionServiceModel

class ListCompanyActionServiceModelService extends BaseService {

    public ListCompanyActionServiceModel read(long id) {
        return ListCompanyActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListCompanyActionServiceModel.SQL_LIST_COMPANY_MODEL)
    }
}
