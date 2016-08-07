package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppCompanyUserActionServiceModel

class ListAppCompanyUserActionServiceModelService extends BaseService {

    public ListAppCompanyUserActionServiceModel read(long id) {
        return ListAppCompanyUserActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListAppCompanyUserActionServiceModel.SQL_LIST_APP_COMPANY_USER_MODEL)
    }
}
