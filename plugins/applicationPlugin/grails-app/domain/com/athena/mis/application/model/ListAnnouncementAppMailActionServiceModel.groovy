package com.athena.mis.application.model

class ListAnnouncementAppMailActionServiceModel {
    public static final String SQL_LIST_ANNOUNCEMENT_MODEL = """
        DROP TABLE IF EXISTS list_announcement_app_mail_action_service_model;
        DROP VIEW IF EXISTS list_announcement_app_mail_action_service_model;
        CREATE OR REPLACE VIEW list_announcement_app_mail_action_service_model AS
        SELECT
             mail.id,
             mail.subject,
             mail.body,
             mail.created_on,
             mail.is_announcement,
             mail.has_send,
             mail.recipients,
             appuser.username AS created_by,
             mail.company_id
        FROM app_mail mail
        LEFT JOIN app_user appuser ON appuser.id = mail.created_by
        WHERE mail.has_send = TRUE
        AND mail.is_announcement = TRUE
    """

    long id                 // AppMail.id
    String subject          // AppMAil.subject
    String body             // AppMail.body
    Date createdOn          // role name
    String createdBy        //
    boolean hasSend
    boolean isAnnouncement
    String recipients

    static mapping = {
        cache usage: "read-only"
        version false
    }
}
