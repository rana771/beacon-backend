package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppServerDbInstanceMapping

class AppServerDbInstanceMappingService extends BaseDomainService {

    @Override
    public void init() {
        domainClass = AppServerDbInstanceMapping.class
    }

    public int countByAppServerInstanceId(long serverInstanceId) {
        return AppServerDbInstanceMapping.countByAppServerInstanceId(serverInstanceId)
    }

    public int countByAppServerInstanceIdAndDbVendorId(long appServerInstanceId, long dbVendorId) {
        int count = AppServerDbInstanceMapping.countByAppServerInstanceIdAndDbVendorId(appServerInstanceId, dbVendorId)
        return count
    }

    public int countByAppServerInstanceIdAndDbVendorIdAndIdNotEqual(long appServerInstanceId, long dbVendorId, long oldServerMappingId) {
        int count = AppServerDbInstanceMapping.countByAppServerInstanceIdAndDbVendorIdAndIdNotEqual(appServerInstanceId, dbVendorId, oldServerMappingId)
        return count
    }

    @Override
    public void createDefaultSchema() {
        String sqlIndex = "CREATE UNIQUE INDEX app_server_db_instance_mapping_app_server_instance_id_app_db_instance_id_company_id_idx ON app_server_db_instance_mapping(app_server_instance_id,app_db_instance_id,company_id);"
        executeSql(sqlIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }

}
