package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListForTruncateSamplingStarActionServiceModel

class ListForTruncateSamplingStarActionServiceModelService extends BaseService {

    public void createDefaultSchema() {
        executeSql(ListForTruncateSamplingStarActionServiceModel.SQL_LIST_FOR_TRUNCATE_SAMPLING_STAR)
    }
}
