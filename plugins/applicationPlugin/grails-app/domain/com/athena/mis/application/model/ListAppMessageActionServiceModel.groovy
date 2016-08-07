package com.athena.mis.application.model

class ListAppMessageActionServiceModel {

    public static final String SQL_LIST_APP_MESSAGE_MODEL = """
        DROP TABLE IF EXISTS list_app_message_action_service_model;
        DROP VIEW IF EXISTS list_app_message_action_service_model;
        CREATE OR REPLACE VIEW list_app_message_action_service_model AS
            SELECT message.id,
                    message.app_mail_id,
                    message.app_user_id,
                    app_user.email as sender_email,
                    message.subject,
                    message.body,
                    message.sender_name,
                    message.created_on,
                    message.is_read,
                    CASE WHEN COUNT(attachment.id) > 0 THEN 't' ELSE 'f' END AS has_attachment
            FROM app_message message
            LEFT JOIN app_attachment attachment ON attachment.entity_id = message.app_mail_id
            LEFT JOIN app_user ON app_user.id = message.sender_id
            GROUP BY message.id, message.app_mail_id, message.app_user_id, message.subject,
            message.sender_name, message.created_on, message.is_read, message.body, app_user.email
    """

    long id                     // AppMessage.id
    long appMailId              // AppMessage.appMailId
    long appUserId              // AppMessage.appUserId
    String senderEmail
    String subject              // AppMessage.subject (redundant AppMail.subject)
    String body
    String senderName           // AppMessage.sender_name
    Date createdOn              // AppMessage.createdOn
    boolean isRead              // AppMessage.isRead
    boolean hasAttachment       // AppMessage.hasAttachment

    static mapping = {
        cache usage: "read-only"
        version false
    }
}
