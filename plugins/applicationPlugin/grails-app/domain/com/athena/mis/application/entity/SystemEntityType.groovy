package com.athena.mis.application.entity

/**
 * Creating Procedure of System Entity Type.
 * 1. Declare a constant in corresponding system entity cache service (e.g. 'AppSystemEntityCacheService'.
 *    e.g - 'public static final long SYS_ENTITY_TYPE_UNIT = 251L'.
 * 2. Plugin id and lastCounter combined-ly generate ID. (pluginId + lastCounter ex: pluginId = 1, lastCounter = 1 --->> id = 11).
 *   i. To get lastCounter go to SystemEntityTypeService's header documentation.
 *   ii. ID = plugin id + lastCounter.
 *   iii. after created new type, update last counter info.
 * 3. Add new System Entity Type to 'SystemEntityTypeService'.
 *   i. add system entity type to allowed method for corresponding module.
 *      e.g - 'createDefaultDataForApp()' allowed for application module.
 *   ii. create new method if module is new and declare that method in 'AppBootStrapActionService'.
 * 4. Script should be written for new System Entity Type for existing Database.
 */

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> determines the type of SystemEntity.
 * SystemEntityType has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Foreign Reference:</strong> Other domain, which has foreign key reference of SystemEntityType:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.ReservedSystemEntity#type}</li>
 *     <li>{@link com.athena.mis.application.entity.SystemEntity#type}</li>
 * </ul>
 *
 */
class SystemEntityType {

    public static final String DEFAULT_SORT_FIELD = "id"

    long id             // primary key (pluginId + sequence ex: pluginId = 1, sequence = 1, id = 11)
    long version        // entity version in the persistence layer
    String name         // name of SystemEntityType
    String description  // description of SystemEntityType
    long pluginId       // id of plugin (e.g. 1 for Application, 3 for Budget etc.)

    static constraints = {
        name(nullable: false)
        description(nullable: false)
        pluginId(nullable: false)
    }

    static mapping = {
        id generator: 'assigned'
        pluginId index: 'system_entity_type_plugin_id_idx'

        // unique index on "name" and "plugin_id" using SystemEntityTypeService.createDefaultSchema()
        // <domain_name><property_name_1><property_name_2>idx
    }
}
