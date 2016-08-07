package com.athena.mis.application.model

class ListAppServerDbInstanceMappingActionServiceModel {

    public static final String SQL_LIST_APP_SERVER_DB_INSTANCE_MAPPING_MODEL = """
    DROP TABLE IF EXISTS list_app_server_db_instance_mapping_action_service_model;
    DROP VIEW IF EXISTS list_app_server_db_instance_mapping_action_service_model;
    CREATE OR REPLACE VIEW list_app_server_db_instance_mapping_action_service_model AS
        SELECT
            asdim.id,
            asdim.version,
            asdim.app_server_instance_id,
            asdim.app_db_instance_id,
            asdim.db_vendor_id,
            asi.name as app_server_instance_name,
            adi.generated_name as app_db_instance_name,
            type.key as db_vendor_name
        FROM app_server_db_instance_mapping asdim
        LEFT JOIN app_server_instance asi on asi.id = asdim.app_server_instance_id
        LEFT JOIN app_db_instance adi on adi.id = asdim.app_db_instance_id
        LEFT JOIN system_entity type ON type.id = asdim.db_vendor_id
    """

    long id
    long version
    long appServerInstanceId        // appServerInstance.id
    long appDbInstanceId            // appDbInstance.id
    long dbVendorId                 // dbVendor.id

    String appServerInstanceName    // appServerInstance name
    String appDbInstanceName        // appDbInstance name
    String dbVendorName             // dbVendor name

    static mapping = {
        cache usage: "read-only"
    }
}
