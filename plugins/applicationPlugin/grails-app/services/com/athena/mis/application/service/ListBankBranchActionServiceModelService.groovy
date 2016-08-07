package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListBankBranchActionServiceModel

class ListBankBranchActionServiceModelService extends BaseService {

    public ListBankBranchActionServiceModel read(long id) {
        return ListBankBranchActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListBankBranchActionServiceModel.SQL_LIST_BANK_BRANCH_MODEL)
    }
}
