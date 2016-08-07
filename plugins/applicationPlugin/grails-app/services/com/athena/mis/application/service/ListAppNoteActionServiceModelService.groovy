package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppNoteActionServiceModel


class ListAppNoteActionServiceModelService extends BaseService{

    public ListAppNoteActionServiceModel read(long id){
        return ListAppNoteActionServiceModel.read(id)
    }

    public void createDefaultSchema(){
        executeSql(ListAppNoteActionServiceModel.SQL_APP_NOTE_MODEL)
    }
}
