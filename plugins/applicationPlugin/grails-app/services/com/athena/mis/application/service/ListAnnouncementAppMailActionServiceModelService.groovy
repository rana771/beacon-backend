package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAnnouncementAppMailActionServiceModel

class ListAnnouncementAppMailActionServiceModelService extends BaseService {

    public void createDefaultSchema() {
        executeSql(ListAnnouncementAppMailActionServiceModel.SQL_LIST_ANNOUNCEMENT_MODEL)
    }
}
