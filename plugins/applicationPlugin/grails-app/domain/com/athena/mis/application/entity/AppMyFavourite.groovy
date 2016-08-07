package com.athena.mis.application.entity

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> Keeps track of most visited pages by user.
 * AppMyFavourite has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Local Reference:</strong> Has-a relationship with other domains:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as userId}</li>
 *     <li>{@link com.athena.mis.application.entity.Company#id as companyId}</li>
 * </ul>
 *
 */
class AppMyFavourite {

    public static final String DEFAULT_SORT_FIELD = "id"

    long id
    long version
    long userId
    String url
    String featureName
    long pluginId
    boolean isDefault
    boolean isDirty
    long companyId
    Date createdOn

    static mapping = {
        id generator: 'sequence', params: [sequence: 'app_my_favourite_id_seq']
        userId index: 'app_my_favourite_user_id_idx'
        companyId index: 'app_my_favourite_company_id_idx'

        // unique index on "user_id" and "url" using AppMyFavouriteService.createDefaultSchema()
        // <domain_name><property_name_1><property_name_2>idx
    }

    static constraints = {
        userId(nullable: false)
        url(nullable: false)
        pluginId(nullable: false)
        isDefault(nullable: false)
        companyId(nullable: false)
    }
}
