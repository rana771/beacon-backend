package com.athena.mis.application.entity

class AppServerDbInstanceMapping {
    long id
    long version
    long appServerInstanceId
    long appDbInstanceId
    long dbVendorId

    long companyId                  // Company.id
    Date createdOn                  // Object creation DateTime
    long createdBy                  // AppUser.id
    Date updatedOn                  // Object Updated DateTime
    long updatedBy                  // AppUser.id

    static mapping = {
        id generator: 'sequence', params: [sequence: 'app_server_db_instance_mapping_id_seq']
        appServerInstanceId index: 'app_server_db_instance_mapping_app_server_instance_id_idx'
        appDbInstanceId index: 'app_server_db_instance_mapping_app_db_instance_id_idx'
        dbVendorId index: 'app_server_db_instance_mapping_db_vendor_id_idx'
        companyId index: 'app_server_db_instance_mapping_company_id_idx'
        createdBy index: 'app_server_db_instance_mapping_created_by_idx'
        updatedBy index: 'app_server_db_instance_mapping_updated_by_idx'

        // unique index on "app_server_instance_id" and "app_db_instance_id" using AppServerDbInstanceMappingService.createDefaultSchema()
        // <domain_name><property_name_1><property_name_2>idx
    }

    static constraints = {
        updatedOn(nullable: true)
    }
}
