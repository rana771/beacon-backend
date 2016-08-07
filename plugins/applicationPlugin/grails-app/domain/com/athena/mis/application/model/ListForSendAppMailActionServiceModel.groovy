package com.athena.mis.application.model

class ListForSendAppMailActionServiceModel {
    public static final String SQL_LIST_SEND_MAIL_MODEL = """
        DROP TABLE IF EXISTS list_for_send_app_mail_action_service_model;
        DROP VIEW IF EXISTS list_for_send_app_mail_action_service_model;
        CREATE OR REPLACE VIEW list_for_send_app_mail_action_service_model AS
        SELECT mail.id, mail.subject, mail.updated_on, mail.created_by,
        CASE
        WHEN mail.role_ids LIKE '%,%'
            THEN 'ALL'
        ELSE (SELECT name FROM role WHERE id = mail.role_ids::bigint)
        END AS send_to
        FROM app_mail mail
        WHERE mail.has_send = true
        AND mail.is_announcement = true;
    """

    long id             // AppMail.id
    String subject      // AppMAil.subject
    Date updatedOn      // Object updated on DateTime
    long createdBy      // AppUser.id
    String sendTo       // name of user role

    static mapping = {
        version false
        cache usage: "read-only"
    }
}
