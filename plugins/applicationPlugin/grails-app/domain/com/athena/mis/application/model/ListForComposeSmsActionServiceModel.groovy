package com.athena.mis.application.model

class ListForComposeSmsActionServiceModel {

    public static final String SQL_LIST_COMPOSE_SMS_MODEL = """
        DROP TABLE IF EXISTS list_for_compose_sms_action_service_model;
        DROP VIEW IF EXISTS list_for_compose_sms_action_service_model;
        CREATE OR REPLACE VIEW list_for_compose_sms_action_service_model AS
        SELECT sms.id, sms.version, sms.body,
        CASE
        WHEN sms.role_id LIKE '%,%'
            THEN ''
        ELSE sms.role_id
        END AS role_id,
        CASE
        WHEN sms.role_id LIKE '%,%'
            THEN 'ALL'
        ELSE (SELECT name FROM role WHERE id = sms.role_id::bigint)
        END AS role_name
        FROM app_sms sms
        WHERE sms.has_send = false
        AND sms.plugin_id = 0;
    """

    long id                 // AppMail.id
    long version            // AppMail.version
    String body             // AppMail.body
    String roleId           // role id
    String roleName         // role name

    static mapping = {
        cache usage: "read-only"
    }
}
