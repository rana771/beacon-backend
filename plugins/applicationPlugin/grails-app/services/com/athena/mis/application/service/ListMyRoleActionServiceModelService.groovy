package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListMyRoleActionServiceModel

class ListMyRoleActionServiceModelService extends BaseService {
    public void createDefaultSchema() {
        executeSql(ListMyRoleActionServiceModel.SQL_MY_ROLE_MODEL)
    }
}
