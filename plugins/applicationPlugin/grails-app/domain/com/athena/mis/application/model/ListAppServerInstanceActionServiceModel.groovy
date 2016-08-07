package com.athena.mis.application.model

class ListAppServerInstanceActionServiceModel {

    public static final String SQL_LIST_APP_SERVER_INSTANCE_MODEL = """
        DROP TABLE IF EXISTS list_app_server_instance_action_service_model;
        DROP VIEW IF EXISTS list_app_server_instance_action_service_model;
        CREATE OR REPLACE VIEW list_app_server_instance_action_service_model AS
        SELECT
            asi.id,
            asi.version,
            asi.name,
            asi.is_tested,
            asi.ssh_user_name,
            asi.ssh_password,
            asi.ssh_host,
            asi.ssh_port,
            asi.os_vendor_id,
            se.value as os_vendor_name,
            asi.company_id,
            asi.is_native
        FROM app_server_instance asi
        LEFT JOIN system_entity se ON se.id = asi.os_vendor_id;
    """

    long id
    long version
    String name
    boolean isTested
    String sshHost                  // source db server host name
    int sshPort                     // source server ssh port
    String sshUserName              // source server login user
    long osVendorId
    String osVendorName
    String sshPassword
    boolean isNative
    long companyId                  // Company.id

    static mapping = {
        cache usage: "read-only"
    }
}
