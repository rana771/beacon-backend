package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListForSendAppMailActionServiceModel

class ListForSendAppMailActionServiceModelService extends BaseService {

    public void createDefaultSchema() {
        executeSql(ListForSendAppMailActionServiceModel.SQL_LIST_SEND_MAIL_MODEL)
    }
}
