package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppMessage

class AppMessageService extends BaseDomainService {

    @Override
    public void init() {
        domainClass = AppMessage.class
    }

    @Override
    public void createDefaultSchema() {}

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
