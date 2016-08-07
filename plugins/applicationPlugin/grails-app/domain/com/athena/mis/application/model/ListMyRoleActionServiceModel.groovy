package com.athena.mis.application.model

class ListMyRoleActionServiceModel {
    public static final String SQL_MY_ROLE_MODEL = """
        DROP TABLE IF EXISTS list_my_role_action_service_model;
        DROP VIEW IF EXISTS list_my_role_action_service_model;
        CREATE OR REPLACE VIEW list_my_role_action_service_model AS
            SELECT
                role.id,
                role.name as role_name,
                role.company_id,
                ur.user_id
            FROM role
            LEFT JOIN user_role ur ON ur.role_id = role.id
        """

    long id                 // Role.id
    String roleName         // Role.name
    long companyId          // Role.companyId
    long userId             // UserRole.userId

    static mapping = {
        version false
        cache usage: "read-only"
    }
}
