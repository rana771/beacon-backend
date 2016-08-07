package com.athena.mis.application.model

class ListUserRoleForCompanyUserActionServiceModel implements Serializable {

    public static final String SQL_USER_ROLE_FOR_COMPANY_USER_MODEL = """
        DROP TABLE IF EXISTS list_user_role_for_company_user_action_service_model;
        DROP VIEW IF EXISTS list_user_role_for_company_user_action_service_model;
        CREATE OR REPLACE VIEW list_user_role_for_company_user_action_service_model AS
        SELECT user_role.user_id, user_role.role_id, role.name, role.plugin_id
        FROM user_role
        LEFT JOIN role ON role.id = user_role.role_id;
    """

    long userId     // UserRole.userId
    long roleId     // UserRole.roleId
    String name     // Role.name
    long pluginId   // Role.pluginId

    static mapping = {
        id composite: ['roleId', 'userId']
        version false
        cache usage: "read-only"
    }
}
