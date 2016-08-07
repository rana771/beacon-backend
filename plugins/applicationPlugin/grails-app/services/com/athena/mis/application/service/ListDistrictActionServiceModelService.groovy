package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListDistrictActionServiceModel

class ListDistrictActionServiceModelService extends BaseService {

    public ListDistrictActionServiceModel read(long id){
        return ListDistrictActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListDistrictActionServiceModel.SQL_LIST_DISTRICT_MODEL)
    }
}
