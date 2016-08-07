package com.athena.mis.application.entity

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> Contains the mapping of features (transactionCode of RequestMap) with ReservedRole.
 * RoleFeatureMapping has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Local Reference:</strong> Has-a relationship with other domains:</p>
 * <ul>
 *     <li>{@link ReservedRole#id as roleTypeId}</li>
 *     <li>{@link com.athena.mis.application.entity.RequestMap#transactionCode as transactionCode}</li>
 * </ul>
 *
 */
class RoleFeatureMapping implements Serializable {
    long roleTypeId             // ReservedRole.id
    String transactionCode      // RequestMap.transactionCode
    long pluginId               // plugin id e.g : 1 for Application, 2 for Accounting etc

    static mapping = {
        version false
        id composite: ['roleTypeId', 'transactionCode']

        // unique index on "role_type_id" and "transaction_code" using RoleFeatureMappingService.createDefaultSchema()
        // <domain_name><property_name_1><property_name_2>idx
    }

    static constraints = {
        roleTypeId(nullable: false)
        transactionCode(nullable: false)
        pluginId(nullable: false)
    }
}
