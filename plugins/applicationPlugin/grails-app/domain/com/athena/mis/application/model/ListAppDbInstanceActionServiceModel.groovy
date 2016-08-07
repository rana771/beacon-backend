package com.athena.mis.application.model

class ListAppDbInstanceActionServiceModel {

    public static final String SQL_DB_INSTANCE_MODEL = """
        DROP TABLE IF EXISTS list_app_db_instance_action_service_model;
        DROP VIEW IF EXISTS list_app_db_instance_action_service_model;
        CREATE OR REPLACE VIEW list_app_db_instance_action_service_model AS
        SELECT
            db.id,
            db.version,
            db.company_id,
            db.db_name,
            db.generated_name,
            db.is_native,
            db.is_slave, db.is_tested,
            db.name, db.password,
            db.port,
            db.type_id,
            db.url, db.user_name,
            db.driver, db.vendor_id,
            vendor.name AS vendor_name,
            db.reserved_vendor_id,
            db.is_read_only,
            db.schema_name,
            db.is_deletable
        FROM app_db_instance db
        LEFT JOIN vendor vendor ON vendor.id = db.vendor_id
    """

    long id                 // AppDbInstance.id
    long version            // AppDbInstance.version
    long companyId          // AppDbInstance.companyId
    String dbName           // AppDbInstance.dbName
    String generatedName    // AppDbInstance.generatedName
    boolean isNative        // AppDbInstance.isNative
    boolean isSlave         // AppDbInstance.isSlave,
    boolean isTested        // AppDbInstance.isTested
    String name             // AppDbInstance.name
    String password         // AppDbInstance.password
    String port             // AppDbInstance.port
    String url              // AppDbInstance.url
    String userName         // AppDbInstance.userName
    String driver           // AppDbInstance.driver
    long vendorId           // AppDbInstance.vendorId
    String vendorName       // SystemEntity.key
    long reservedVendorId   // AppDbInstance.reservedVendorId
    boolean isReadOnly      // AppDbInstance.isReadOnly
    String schemaName       // AppDbInstance.schemaName
    boolean isDeletable     // AppDbInstance.isDeletable

    static mapping = {
        cache usage: "read-only"
    }
}
