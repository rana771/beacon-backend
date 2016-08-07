package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppUserDetails

class AppUserDetailsService extends BaseDomainService {

    @Override
    public void init() {
        domainClass = AppUserDetails.class
    }

    public AppUserDetails readByUser(long userId){
        AppUserDetails elUserDetails = AppUserDetails.findByUserId(userId, [readOnly: true])
        return elUserDetails
    }

    public int countByUserId(long userId) {
        int countUser = AppUserDetails.countByUserId(userId)
        return countUser
    }

    @Override
    public void createDefaultSchema() {}

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
