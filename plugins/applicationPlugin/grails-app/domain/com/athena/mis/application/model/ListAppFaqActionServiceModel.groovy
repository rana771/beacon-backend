package com.athena.mis.application.model

class ListAppFaqActionServiceModel {

    public static final String SQL_APP_FAQ_MODEL = """
        DROP TABLE IF EXISTS list_app_faq_action_service_model;
        DROP VIEW IF EXISTS list_app_faq_action_service_model;
        CREATE OR REPLACE VIEW list_app_faq_action_service_model AS
        SELECT
            faq.id,
            faq.version,
            faq.company_id,
            faq.entity_type_id,
            faq.entity_id,
            faq.question,
            faq.answer,
            faq.created_on,
            faq.created_by,
            au.username
        FROM app_faq faq
        LEFT JOIN app_user au ON au.id = faq.created_by;
    """

    long id
    long version
    long companyId
    long entityTypeId
    long entityId
    String question
    String answer
    Date createdOn
    String username

    static mapping = {
        cache usage: "read-only"
    }
}
