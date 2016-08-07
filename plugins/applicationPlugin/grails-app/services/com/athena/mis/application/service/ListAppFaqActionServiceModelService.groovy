package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppFaqActionServiceModel

class ListAppFaqActionServiceModelService extends BaseService{

    public ListAppFaqActionServiceModel read(long id){
        return ListAppFaqActionServiceModel.read(id)
    }

    public void createDefaultSchema(){
        executeSql(ListAppFaqActionServiceModel.SQL_APP_FAQ_MODEL)
    }
}
