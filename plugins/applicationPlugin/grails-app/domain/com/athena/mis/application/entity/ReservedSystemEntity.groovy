package com.athena.mis.application.entity

/**
 * Creating Procedure of Reserved System Entity.
 *  1. ID assigned(pluginId + lastCounter ex: pluginId = 1, lastCounter = 1, id = 11).
 *     i. to get lastCounter go to ReservedSystemEntityService's header documentation.
 *     ii. always update lastCounter after creating new one.
 *  2. Add reserved system entity to allowed method of corresponding module in 'ReservedSystemEntityService'.
 *  3. Script should be written for new reserved system entity for existing Database.
 */

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> ReservedSystemEntity is required for all Company.
 * ReservedSystemEntity has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Foreign Reference:</strong> Other domain, which has foreign key reference of ReservedSystemEntity:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.SystemEntity#reservedId}</li>
 * </ul>
 *
 * <p><strong>Local Reference:</strong> Has-a relationship with other domains:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.SystemEntity#id as type}</li>
 * </ul>
 *
 */
class ReservedSystemEntity {
    long id         // primary key assigned(pluginId + counter(always 7 digits) ex: pluginId = 1, counter = 0000001, id = 10000001)
    String key      // key of ReservedSystemEntity
    String value    // value of ReservedSystemEntity
    long type       // SystemEntityType.id
    long pluginId   // id of plugin

    static constraints = {
        key(nullable: false)
        value(nullable: true)
        type(nullable: false)
        pluginId(nullable: false)
    }

    static mapping = {
        id generator: 'assigned'
        version false
        type index: 'reserved_system_entity_type_idx'
        pluginId index: 'reserved_system_entity_plugin_id_idx'

        // unique index on "key" and "type" using ReservedSystemEntityService.createDefaultSchema()
        // <domain_name><property_name_1><property_name_2>idx
    }
}
