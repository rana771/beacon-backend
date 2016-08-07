package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListRoleModuleActionServiceModel

class ListRoleModuleActionServiceModelService extends BaseService {

    public ListRoleModuleActionServiceModel read(long id) {
        return ListRoleModuleActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListRoleModuleActionServiceModel.SQL_LIST_ROLE_MODULE)
    }
}
