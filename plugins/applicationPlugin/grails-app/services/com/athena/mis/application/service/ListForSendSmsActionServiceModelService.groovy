package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListForSendSmsActionServiceModel

class ListForSendSmsActionServiceModelService extends BaseService {

    public void createDefaultSchema() {
        executeSql(ListForSendSmsActionServiceModel.SQL_LIST_SEND_SMS_MODEL)
    }
}
