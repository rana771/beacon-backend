package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppGroupEntity

class AppGroupEntityService extends BaseDomainService {

    static transactional = true

    @Override
    public void init() {
        domainClass = AppGroupEntity.class
    }

    @Override
    public void createDefaultSchema() {
        String sqlIndex = "create unique index app_group_entity_group_id_entt_id_entt_type_id_company_id_idx on app_group_entity(group_id, entity_id, entity_type_id, company_id);"
        executeSql(sqlIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
