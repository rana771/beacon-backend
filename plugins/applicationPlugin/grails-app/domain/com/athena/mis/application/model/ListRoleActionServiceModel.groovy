package com.athena.mis.application.model

class ListRoleActionServiceModel {
    public static final String SQL_LIST_ROLE_MODEL = """
    DROP TABLE IF EXISTS list_role_action_service_model;
    DROP VIEW IF EXISTS list_role_action_service_model;
    CREATE OR REPLACE VIEW list_role_action_service_model AS
        SELECT
        role.id,
        role.version,
        role.name,
        COUNT(au.id) AS count,
        (SELECT COUNT(rm.id) FROM role_module rm WHERE rm.role_id = role.id) AS module_count,
        role.company_id,
        role.role_type_id
        FROM role
    LEFT JOIN user_role ur ON ur.role_id = role.id
    LEFT JOIN app_user au ON au.id = ur.user_id AND au.enabled = true
    WHERE role.is_reseller = false
    GROUP BY role.id, role.name,role.version,role.company_id, role.role_type_id
    """

    long id                 // Role.id
    long version            // Role.version
    String name             // Role.name
    long count              // AppUser count
    int moduleCount         // RoleModule count
    long companyId          // Role.companyId
    long roleTypeId         // Role.roleTypeId

    static mapping = {
        cache usage: "read-only"
    }
}
