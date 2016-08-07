package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppAnnouncementActionServiceModel

class ListAppAnnouncementActionServiceModelService extends BaseService {

    public ListAppAnnouncementActionServiceModel read(long id) {
        return ListAppAnnouncementActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListAppAnnouncementActionServiceModel.SQL_LIST_COMPOSE_MAIL_MODEL)
    }
}
