package com.athena.mis.application.entity

class AppPage {
    public static final int BODY_MAX_LENGTH = 2040


    long id
    long version
    String title
    String body

    long entityTypeId           // SystemEntity.id
    long entityId

    boolean isCommentable
    boolean isActive
    boolean isPin

    long companyId
    long createdBy
    Date createdOn
    long updatedBy
    Date updatedOn

    static constraints = {
        body(nullable: false, maxSize: BODY_MAX_LENGTH)
        updatedOn(nullable: true)
    }

    static mapping = {
        id generator: 'sequence', params: [sequence: 'app_page_id_seq']
        companyId index: 'app_page_company_id_idx'
        entityTypeId index: 'app_page_entity_type_id_idx'
        entityId index: 'app_page_entity_id_idx'
        createdBy index: 'app_page_created_by_idx'
        updatedBy index: 'app_page_updated_by_idx'
    }
}
