package com.athena.mis.application.entity

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> Reserved Roles are created for a company according to Role Types.
 * ReservedRole has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Foreign Reference:</strong> Other domain, which has foreign key reference of ReservedRole:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.Role#roleTypeId}</li>
 *     <li>{@link com.athena.mis.application.entity.RoleFeatureMapping#roleTypeId}</li>
 * </ul>
 *
 */
class ReservedRole {

    public static final String DEFAULT_SORT_FIELD = "name"

    long id             // primary key (Assigned and negative so that reserved role's authority does not conflict with custom created role's)
    String name         // ReservedRole Name e.g: Director, Project Manager etc.
    String authority    // ReservedRole authority format(ROLE_Role.id) e.g ROLE_-1, ROLE_5
    long pluginId       // Application = 1, Accounting = 2 etc.

    static mapping = {
        id generator: 'assigned'
        version false

        // unique index on "authority" ReservedRoleService.createDefaultSchema()
        // <domain_name><property_name_1>idx
    }

    static constraints = {
        name(nullable: false)
        authority(nullable: false)
    }
}
