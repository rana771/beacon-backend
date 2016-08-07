package com.athena.mis.application.model

class ListRoleModuleActionServiceModel {

    public static final String SQL_LIST_ROLE_MODULE = """
        DROP TABLE IF EXISTS list_role_module_action_service_model;
        DROP VIEW IF EXISTS list_role_module_action_service_model;
        CREATE OR REPLACE VIEW list_role_module_action_service_model AS
        SELECT
        id,
        company_id,
        role_id,
        module_id,
        CASE WHEN module_id = 1 THEN 'Application'
        WHEN module_id = 2 THEN 'Accounting'
        WHEN module_id = 3 THEN 'Budget'
        WHEN module_id = 4 THEN 'Inventory'
        WHEN module_id = 5 THEN 'Procurement'
        WHEN module_id = 6 THEN 'QS'
        WHEN module_id = 7 THEN 'Fixed Asset'
        WHEN module_id = 9 THEN 'Exchange House'
        WHEN module_id = 10 THEN 'Project Track'
        WHEN module_id = 11 THEN 'ARMS'
        WHEN module_id = 12 THEN 'SARB'
        WHEN module_id = 13 THEN 'Document'
        WHEN module_id = 14 THEN 'Data Pipeline'
        ELSE 'Any'
        END module_name
        FROM role_module
    """

    long id                 // RoleModule.id
    long companyId          // RoleModule.companyId
    long roleId             // Role.id
    long moduleId           // moduleId
    String moduleName       // module name

    static mapping = {
        version false
        cache usage: "read-only"
    }
}
