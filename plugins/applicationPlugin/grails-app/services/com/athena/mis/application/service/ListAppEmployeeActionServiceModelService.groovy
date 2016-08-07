package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppEmployeeActionServiceModel

class ListAppEmployeeActionServiceModelService extends BaseService{

    public ListAppEmployeeActionServiceModel read(long id) {
        return ListAppEmployeeActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListAppEmployeeActionServiceModel.SQL_EMPLOYEE_MODEL)
    }
}
