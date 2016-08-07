package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListForTruncateSamplingActionServiceModel

class ListForTruncateSamplingActionServiceModelService extends BaseService {

    public void createDefaultSchema() {
        executeSql(ListForTruncateSamplingActionServiceModel.SQL_LIST_FOR_TRUNCATE_SAMPLING)
    }
}
