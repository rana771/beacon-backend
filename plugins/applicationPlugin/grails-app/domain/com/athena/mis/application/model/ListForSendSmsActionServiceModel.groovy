package com.athena.mis.application.model

class ListForSendSmsActionServiceModel {

    public static final String SQL_LIST_SEND_SMS_MODEL = """
        DROP TABLE IF EXISTS list_for_send_sms_action_service_model;
        DROP VIEW IF EXISTS list_for_send_sms_action_service_model;
        CREATE OR REPLACE VIEW list_for_send_sms_action_service_model AS
        SELECT sms.id, sms.body, sms.updated_on, send_by.username AS send_by
        FROM app_sms sms
        LEFT JOIN app_user send_by ON send_by.id = sms.updated_by
        WHERE sms.has_send = true;
    """

    long id             // AppSms.id
    String body         // AppSms.body
    Date updatedOn      // Object updated on DateTime
    String sendBy       // Send by user name

    static mapping = {
        version false
        cache usage: "read-only"
    }
}
