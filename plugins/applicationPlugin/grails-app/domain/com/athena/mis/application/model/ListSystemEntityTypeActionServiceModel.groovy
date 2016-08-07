package com.athena.mis.application.model

class ListSystemEntityTypeActionServiceModel {
    public static final String SQL_LIST_SYSTEM_ENTITY_TYPE_MODEL = """
        DROP TABLE IF EXISTS list_system_entity_type_action_service_model;
        DROP VIEW IF EXISTS list_system_entity_type_action_service_model;
        CREATE OR REPLACE VIEW list_system_entity_type_action_service_model AS
        select c.id company_id, set.name, set.id system_entity_id, set.id, set.version, set.description, set.plugin_id,
        (select count(id) from system_entity where type = set.id and company_id = c.id) system_entity_count
        from
        (select id from company) c,
        (select set.id, set.name, set.version, set.description, set.plugin_id
        from system_entity_type set
        ) set
        order by set.name
    """

    long id                     // SystemEntityType.id
    long version                // SystemEntityType.version
    String name                 // SystemEntityType.name
    String description          // SystemEntityType.description
    long pluginId               // SystemEntityType.pluginId
    long systemEntityId         // SystemEntity.id
    long systemEntityCount      // SystemEntity count
    long companyId              // SystemEntity.companyId

    static mapping = {
        cache usage: "read-only"
    }
}
