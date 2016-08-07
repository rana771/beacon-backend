package com.athena.mis.application.model

class ListAppAnnouncementActionServiceModel {

    public static final String SQL_LIST_COMPOSE_MAIL_MODEL = """
        DROP TABLE IF EXISTS list_app_announcement_action_service_model;
        DROP VIEW IF EXISTS list_app_announcement_action_service_model;
        CREATE OR REPLACE VIEW list_app_announcement_action_service_model AS
        SELECT mail.id, mail.version, mail.subject, mail.body, mail.display_name, mail.created_by,
        CASE
        WHEN mail.role_ids LIKE '%,%'
            THEN ''
        ELSE mail.role_ids
        END AS role_id,
        CASE
        WHEN mail.role_ids LIKE '%,%'
            THEN 'ALL'
        ELSE (SELECT name FROM role WHERE id = mail.role_ids::bigint)
        END AS role_name
        FROM app_mail mail
        WHERE mail.has_send = false
        AND mail.plugin_id = 0
        AND mail.is_announcement = true;
    """

    long id                 // AppMail.id
    long version            // AppMail.version
    String subject          // AppMAil.subject
    String body             // AppMail.body
    String displayName      // AppMail.displayName
    String roleId           // role id
    String roleName         // role name
    long createdBy          // AppUser.id

    static mapping = {
        cache usage: "read-only"
    }
}
