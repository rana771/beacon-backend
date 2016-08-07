package com.athena.mis.application.entity

class AppServerInstance {
    long id
    long version
    String name
    String sshUserName              // source server login user
    String sshPassword              // source server login pwd
    String sshHost                  // source db server host name
    int sshPort                     // source server ssh port
    long osVendorId
    boolean isTested                // true if connection test is successful; otherwise false
    boolean isNative                // if native then true; otherwise false
    long companyId                  // Company.id
    Date createdOn                  // Object creation DateTime
    long createdBy                  // AppUser.id
    Date updatedOn                  // Object Updated DateTime
    long updatedBy                  // AppUser.id

    static mapping = {
        id generator: 'sequence', params: [sequence: 'app_server_instance_id_seq']
        osVendorId index: 'app_server_instance_os_vendor_id_idx'
        companyId index: 'app_server_instance_company_id_idx'
        createdBy index: 'app_server_instance_created_by_idx'
        updatedBy index: 'app_server_instance_updated_by_idx'

        // unique index on "name" using AppServerInstanceService.createDefaultSchema()
        // <domain_name><property_name_1>idx
    }

    static constraints = {
        name(nullable: false)
        sshUserName(nullable: false)
        sshPassword(nullable: false)
        sshHost(nullable: false)
        sshPort(nullable: false)
        updatedOn(nullable: true)
    }
}
