package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListUserRoleForCompanyUserActionServiceModel

class ListUserRoleForCompanyUserActionServiceModelService extends BaseService {

    public ListUserRoleForCompanyUserActionServiceModel read(long userId, long roleId) {
        return ListUserRoleForCompanyUserActionServiceModel.findByUserIdAndRoleId(userId, roleId, [readOnly: true])
    }

    public void createDefaultSchema() {
        executeSql(ListUserRoleForCompanyUserActionServiceModel.SQL_USER_ROLE_FOR_COMPANY_USER_MODEL)
    }
}
