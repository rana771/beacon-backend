package com.athena.mis.application.model

class ListUserRoleActionServiceModel implements Serializable {
    public static final String SQL_USER_ROLE_MODEL = """
        DROP TABLE IF EXISTS list_user_role_action_service_model;
        DROP VIEW IF EXISTS list_user_role_action_service_model;
        CREATE OR REPLACE VIEW list_user_role_action_service_model AS
            SELECT user_role.user_id,
                   user_role.role_id,
                   role.name as role_name,
                   app_user.username as user_name,
                   app_user.company_id,
                   app_user.login_id,
                   app_user.email,
                   app_user.cell_number
            FROM user_role
            LEFT JOIN app_user ON app_user.id = user_role.user_id
            LEFT JOIN role ON role.id = user_role.role_id
            WHERE app_user.enabled = true
        """

    long userId             // UserRole.userId
    long roleId             // UserRole.roleId
    String roleName         // Role.name
    String loginId          // AppUser.loginId
    String email            // AppUser.email
    String cellNumber       // AppUser.cellNumber
    String userName         // AppUser.username
    long companyId          // AppUser.companyId

    static mapping = {
        id composite: ['roleId', 'userId']
        version false
        cache usage: "read-only"
    }
}
