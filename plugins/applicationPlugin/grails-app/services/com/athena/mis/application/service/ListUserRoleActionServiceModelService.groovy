package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListUserRoleActionServiceModel

class ListUserRoleActionServiceModelService extends BaseService {

    public ListUserRoleActionServiceModel read(long userId, long roleId) {
        ListUserRoleActionServiceModel userRole = ListUserRoleActionServiceModel.findByUserIdAndRoleId(userId, roleId, [readOnly: true])
        return userRole
    }

    public void createDefaultSchema() {
        executeSql(ListUserRoleActionServiceModel.SQL_USER_ROLE_MODEL)
    }
}
