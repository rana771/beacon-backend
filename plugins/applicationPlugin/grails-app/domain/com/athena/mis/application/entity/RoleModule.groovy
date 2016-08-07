package com.athena.mis.application.entity

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong>Entity of RoleModule information.
 * RoleModule has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Local Reference:</strong> Has-a relationship with other domains:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.Role#id as roleId}</li>
 *     <li>{@link com.athena.mis.application.entity.Company#id as companyId}</li>
 * </ul>
 *
 */
class RoleModule {

    long id             // object id (primary key - auto generated by own id_seq)
    long roleId		    // Role.id
    long moduleId       // moduleId
    long companyId		// Company.id

    static mapping = {
        version false		// version false for Role-Module mapping
        id generator: 'sequence', params: [sequence:'role_module_id_seq']
        roleId index: 'role_module_role_id_idx'         // indexing roleId
        moduleId index: 'role_module_module_id_idx'     // indexing moduleId
        companyId index: 'role_module_company_id_idx'   // indexing companyId

        // unique index on "role_id" and "module_id" using RoleModuleService.createDefaultSchema()
        // <domain_name><property_name_1><property_name_2>idx
    }
}