package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListRoleActionServiceModel

class ListRoleActionServiceModelService extends BaseService {

    public ListRoleActionServiceModel read(long id) {
        ListRoleActionServiceModel role = ListRoleActionServiceModel.read(id)
        return role
    }

    public void createDefaultSchema() {
        executeSql(ListRoleActionServiceModel.SQL_LIST_ROLE_MODEL)
    }
}
