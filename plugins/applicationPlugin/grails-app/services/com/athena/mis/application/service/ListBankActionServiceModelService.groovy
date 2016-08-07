package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListBankActionServiceModel

class ListBankActionServiceModelService extends BaseService {

    public ListBankActionServiceModel read(long id) {
        return ListBankActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListBankActionServiceModel.SQL_LIST_BANK_MODEL)
    }
}
