package com.athena.mis.application.model

class ListAppNoteActionServiceModel {

    public static final String SQL_APP_NOTE_MODEL="""
        DROP TABLE IF EXISTS list_app_note_action_service_model;
        DROP VIEW IF EXISTS list_app_note_action_service_model;
        CREATE OR REPLACE VIEW list_app_note_action_service_model AS
        SELECT
            en.id,
            en.version,
            en.company_id,
            en.entity_type_id,
            en.entity_id,
            en.note,
            en.entity_note_type_id,
            se.key entity_note_type,
            en.created_on,
            en.created_by,
            au.username
        FROM app_note en
        LEFT JOIN system_entity se ON se.id=en.entity_note_type_id
        LEFT JOIN app_user au ON au.id=en.created_by;
    """

    long id
    long version
    long companyId
    long entityTypeId
    long entityId
    String note
    long entityNoteTypeId
    String entityNoteType
    Date createdOn
    String username

    static mapping = {
        cache usage: "read-only"
    }
}
