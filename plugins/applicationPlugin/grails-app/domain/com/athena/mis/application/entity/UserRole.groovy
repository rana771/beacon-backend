package com.athena.mis.application.entity

import org.apache.commons.lang.builder.HashCodeBuilder

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> Contains AppUser mapping relation with Role.
 * UserRole has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Local Reference:</strong> Has-a relationship with other domains:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as userId}</li>
 *     <li>{@link com.athena.mis.application.entity.Role#id as roleId}</li>
 * </ul>
 *
 */
class UserRole implements Serializable {
    long userId
    long roleId

    static mapping = {
        id composite: ['roleId', 'userId']
        version false
        userId index: 'user_role_user_idx'
        roleId index: 'user_role_role_idx'

        // unique index on "user_id" and "role_id" using UserRoleService.createDefaultSchema()
        // <domain_name><property_name_1><property_name_2>idx
    }
}
