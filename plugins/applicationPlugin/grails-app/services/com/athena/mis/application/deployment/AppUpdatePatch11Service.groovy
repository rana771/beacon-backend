package com.athena.mis.application.deployment

import com.athena.mis.AppSql
import com.athena.mis.AppUpdatePatch
import com.athena.mis.application.model.*
import com.athena.mis.utility.DateUtility

class AppUpdatePatch11Service extends AppUpdatePatch {

    private static final String RELEASE_DATE = "19/01/2016" // DD/MM/YYYY

    public Date getReleaseDate() {
        return DateUtility.parseMaskedDate(RELEASE_DATE)
    }

    void init() {
        if (lstAppSql.size() > 0) return
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: addColumnOnAppUserSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: addColumnVersionSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: addColumnIsDraftSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertSystemEntityTypeSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertReservedSystemEntitySql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertSystemEntityMisSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: alterTableAppSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: addColumnAppUserDetailsSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: addSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: insertAppMailSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertRequestMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertRoleFeatureMappingSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: createDomainUniqueIndex)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: dropViewSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppShellScriptActionServiceModel.SQL_LIST_APP_SHELL_SCRIPT_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppDbInstanceActionServiceModel.SQL_DB_INSTANCE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListForTruncateSamplingActionServiceModel.SQL_LIST_FOR_TRUNCATE_SAMPLING)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListForTruncateSamplingStarActionServiceModel.SQL_LIST_FOR_TRUNCATE_SAMPLING_STAR)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppMessageActionServiceModel.SQL_LIST_APP_MESSAGE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAllAppUserActionServiceModel.SQL_LIST_ALL_APP_USER_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListUserRoleActionServiceModel.SQL_USER_ROLE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: updateSystemEntitySql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: deleteReqMap)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: updateReqMap)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: deleteRoleFeatMap)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: dropColumn)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: sysConfigSmsUrl)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: sysConfigMis)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: appSysConfig)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListCompanyActionServiceModel.SQL_LIST_COMPANY_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListCompanyForResellerActionServiceModel.SQL_LIST_COMPANY_FOR_RESELLER_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppEmployeeActionServiceModel.SQL_EMPLOYEE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppUserActionServiceModel.SQL_LIST_APP_USER_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: dropConstraint)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: requestMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: roleFeatureMappingSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: updateThemeSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListForSendAppMailActionServiceModel.SQL_LIST_SEND_MAIL_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppAnnouncementActionServiceModel.SQL_LIST_COMPOSE_MAIL_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppFaqActionServiceModel.SQL_APP_FAQ_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: addThemeReqMap)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: updateReqMapRoleForApp)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100, query: updateReqMapRoleForExh100)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101, query: updateReqMapRoleForExh101)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102, query: updateReqMapRoleForExh102)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: updateSysEntityPluginIdSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAnnouncementAppMailActionServiceModel.SQL_LIST_ANNOUNCEMENT_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppVendorActionServiceModel.SQL_VENDOR_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListVendorForDplDashboardActionServiceModel.SQL_LIST_VENDOR_FOR_DPL_DASH_BOARD)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: updateAppMessageSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: insertReqMapMisSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: insertReqMapExh100Sql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: insertReqMapExh101Sql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: insertReqMapExh102Sql)
    }

    String addColumnAppUserDetailsSql = """
        ALTER TABLE app_user_details DROP COLUMN IF EXISTS point;
        ALTER TABLE app_user_details ADD COLUMN point bigint;
        UPDATE app_user_details SET point=0;
        ALTER TABLE app_user_details ALTER COLUMN point SET NOT NULL;
    """

    String insertAppMailSql = """
        INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
        controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send,
        is_announcement, created_by, created_on)
        VALUES (NEXTVAL('app_mail_id_seq'), 0,
        '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Maintenance Script Scheduler. <br/><br/>
                        <b>Server Instance : </b> \${serverInstance} <br/><br/>
                        <b>Script : </b> \${script} <br/><br/>
                        <b>Execution Count : </b> \${executionCount} <br/>
                        <b>Executed On : </b> \${executedOn} <br/><br/>
                        <b>Message : </b> \${message} <br/><br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
        </div>',
        1, true, 'html', 'Maintenance Script Scheduler', 'AppMaintenanceShellScriptJobActionService', null, 1, false, false, null, null, true,
        'abc@gmail.com', null, 0, null, null, false, false, 12, now());

        INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
        controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send,
        is_announcement, created_by, created_on)
        VALUES (NEXTVAL('app_mail_id_seq'), 0,
        '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Maintenance SQL Scheduler. <br/><br/>
                        \${script}
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
        </div>',
        1, true, 'html', 'Maintenance SQL Scheduler', 'AppMaintenanceSqlScriptJobActionService', null, 1, false, false, null, null, true,
        'abc@gmail.com', null, 0, null, null, false, false, 12, now());

        INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
        controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send,
        is_announcement, created_by, created_on)
        VALUES (NEXTVAL('app_mail_id_seq'), 0,
        '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Maintenance Script Scheduler. <br/><br/>
                        <b>Server Instance : </b> \${serverInstance} <br/><br/>
                        <b>Script : </b> \${script} <br/><br/>
                        <b>Execution Count : </b> \${executionCount} <br/>
                        <b>Executed On : </b> \${executedOn} <br/><br/>
                        <b>Message : </b> \${message} <br/><br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
        </div>',
        2, true, 'html', 'Maintenance Script Scheduler', 'AppMaintenanceShellScriptJobActionService', null, 1, false, false, null, null, true,
        'abc@gmail.com', null, 0, null, null, false, false, 55, now());

        INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
        controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send,
        is_announcement, created_by, created_on)
        VALUES (NEXTVAL('app_mail_id_seq'), 0,
        '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Maintenance SQL Scheduler. <br/><br/>
                        \${script}
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
        </div>',
        2, true, 'html', 'Maintenance SQL Scheduler', 'AppMaintenanceSqlScriptJobActionService', null, 1, false, false, null, null, true,
        'abc@gmail.com', null, 0, null, null, false, false, 55, now());

        INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
        controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send,
        is_announcement, created_by, created_on)
        VALUES (NEXTVAL('app_mail_id_seq'), 0,
        '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Maintenance Script Scheduler. <br/><br/>
                        <b>Server Instance : </b> \${serverInstance} <br/><br/>
                        <b>Script : </b> \${script} <br/><br/>
                        <b>Execution Count : </b> \${executionCount} <br/>
                        <b>Executed On : </b> \${executedOn} <br/><br/>
                        <b>Message : </b> \${message} <br/><br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
        </div>',
        3, true, 'html', 'Maintenance Script Scheduler', 'AppMaintenanceShellScriptJobActionService', null, 1, false, false, null, null, true,
        'abc@gmail.com', null, 0, null, null, false, false, 58, now());

        INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
        controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send,
        is_announcement, created_by, created_on)
        VALUES (NEXTVAL('app_mail_id_seq'), 0,
        '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Maintenance SQL Scheduler. <br/><br/>
                        \${script}
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
        </div>',
        3, true, 'html', 'Maintenance SQL Scheduler', 'AppMaintenanceSqlScriptJobActionService', null, 1, false, false, null, null, true,
        'abc@gmail.com', null, 0, null, null, false, false, 58, now());

        INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
        controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send,
        is_announcement, created_by, created_on)
        VALUES (NEXTVAL('app_mail_id_seq'), 0,
        '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Backup Script Scheduler. <br/><br/>
                        <b>Server Instance : </b> \${serverInstance} <br/><br/>
                        <b>Script : </b> \${script} <br/><br/>
                        <b>Execution Count : </b>\${executionCount} <br/>
                        <b>Executed On : </b> \${executedOn} <br/><br/>
                        <b>Message : </b> \${message} <br/><br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
        </div>',
        1, true, 'html', 'Backup Script Scheduler', 'AppBackupShellScriptJobActionService', null, 1, false, false, null, null, true,
        'abc@gmail.com', null, 0, null, null, false, false, 12, now());

        INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
        controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send,
        is_announcement, created_by, created_on)
        VALUES (NEXTVAL('app_mail_id_seq'), 0,
        '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Backup Script Scheduler. <br/><br/>
                        <b>Server Instance : </b> \${serverInstance} <br/><br/>
                        <b>Script : </b> \${script} <br/><br/>
                        <b>Execution Count : </b> \${executionCount} <br/>
                        <b>Executed On : </b> \${executedOn} <br/><br/>
                        <b>Message : </b> \${message} <br/><br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
        </div>',
        2, true, 'html', 'Backup Script Scheduler', 'AppBackupShellScriptJobActionService', null, 1, false, false, null, null, true,
        'abc@gmail.com', null, 0, null, null, false, false, 55, now());

        INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
        controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send,
        is_announcement, created_by, created_on)
        VALUES (NEXTVAL('app_mail_id_seq'), 0,
        '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Backup Script Scheduler. <br/><br/>
                        <b>Server Instance : </b> \${serverInstance} <br/><br/>
                        <b>Script : </b> \${script} <br/><br/>
                        <b>Execution Count : </b> \${executionCount} <br/>
                        <b>Executed On : </b> \${executedOn} <br/><br/>
                        <b>Message : </b> \${message} <br/><br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
        </div>',
        3, true, 'html', 'Backup Script Scheduler', 'AppBackupShellScriptJobActionService', null, 1, false, false, null, null, true,
        'abc@gmail.com', null, 0, null, null, false, false, 58, now());

        INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
        controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send,
        is_announcement, created_by, created_on)
        VALUES (NEXTVAL('app_mail_id_seq'), 0,
        '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Backup SQL Scheduler. <br/><br/>
                        <b>DB instance : </b> \${dbInstance} <br/><br/>
                        <b>Script : </b> \${script} <br/><br/>
                        <b>Message : </b> \${message} <br/><br/>
                        <b>Execution count : </b> \${executionCount} <br/>
                        <b>Executed On : </b> \${executedOn} <br/><br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
        </div>',
        1, true, 'html', 'Backup SQL Scheduler', 'AppBackupSqlScriptJobActionService', null, 1, false, false, null, null, true,
        'abc@gmail.com', null, 0, null, null, false, false, 12, now());

        INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
        controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send,
        is_announcement, created_by, created_on)
        VALUES (NEXTVAL('app_mail_id_seq'), 0,
        '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Backup SQL Scheduler. <br/><br/>
                        <b>DB instance : </b> \${dbInstance} <br/><br/>
                        <b>Script : </b> \${script} <br/><br/>
                        <b>Message : </b> \${message} <br/><br/>
                        <b>Execution count : </b> \${executionCount} <br/>
                        <b>Executed On : </b> \${executedOn} <br/><br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
        </div>',
        2, true, 'html', 'Backup SQL Scheduler', 'AppBackupSqlScriptJobActionService', null, 1, false, false, null, null, true,
        'abc@gmail.com', null, 0, null, null, false, false, 55, now());

        INSERT INTO app_mail (id, version, body, company_id, is_active, mime_type, subject, transaction_code, role_ids, plugin_id, is_required_role_ids, is_manual_send,
        controller_name, action_name, is_required_recipients, recipients, updated_on, updated_by, recipients_cc, display_name, has_send,
        is_announcement, created_by, created_on)
        VALUES (NEXTVAL('app_mail_id_seq'), 0,
        '<div>
            <p>
                Dear Concerned, <br/>
                This is a notification mail about Backup SQL Scheduler. <br/><br/>
                        <b>DB instance : </b> \${dbInstance} <br/><br/>
                        <b>Script : </b> \${script} <br/><br/>
                        <b>Message : </b> \${message} <br/><br/>
                        <b>Execution count : </b> \${executionCount} <br/>
                        <b>Executed On : </b> \${executedOn} <br/><br/>
            </p>
            <i>Note: This is an auto-generated email, which does not need to reply.<br/></i>
        </div>',
        3, true, 'html', 'Backup SQL Scheduler', 'AppBackupSqlScriptJobActionService', null, 1, false, false, null, null, true,
        'abc@gmail.com', null, 0, null, null, false, false, 58, now());
    """

    String insertRequestMapSql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appShellScript/downloadReport',
        'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3', 'Download Script Report', 1, 'APP-409', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appDbInstance/listForDashboard',
        'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3', 'List/Search DB Instance for DPL Dashboard', 1, 'APP-410', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/vendor/show', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3', 'Show Vendor', 1, 'APP-411', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/vendor/list', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3', 'List/Search Vendor', 1, 'APP-412', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/vendor/create', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3', 'Create Vendor', 1, 'APP-413', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/vendor/update', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3', 'Update Vendor', 1, 'APP-414', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/vendor/delete', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3', 'Delete Vendor', 1, 'APP-415', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMessage/show', 'ROLE_-2,ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3', 'Show Message', 1, 'APP-422', TRUE, TRUE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMessage/list', 'ROLE_2,ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3', 'List/Search Message', 1, 'APP-423', FALSE, TRUE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMessage/delete', 'ROLE_2,ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3', 'Delete Message', 1, 'APP-424', FALSE, TRUE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMessage/markAsUnRead', 'ROLE_2,ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3', 'Mark as Un-Read Message', 1, 'APP-426', FALSE, TRUE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/listAnnouncementForDashboard', 'ROLE_2,ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3', 'List Announcement in Dashboard', 1, 'APP-433', FALSE, TRUE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMessage/preview', 'ROLE_2,ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3', 'Message show in Details', 1, 'APP-434', FALSE, TRUE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/showForComposeMail', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Show Mail Compose', 1, 'APP-427', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/listMail', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'List Mail', 1, 'APP-428', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/createMail', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Create Mail', 1, 'APP-429', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/UpdateMail', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Update mail', 1, 'APP-430', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/sendMail', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Send Mail', 1, 'APP-431', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/deleteMail', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Delete Mail', 1, 'APP-432', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/showForSentMail', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Show Sent Mail', 1, 'APP-435', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/listForSentMail', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'List Sent Mail', 1, 'APP-436', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/previewMail', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Preview Sent Mail', 1, 'APP-437', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/createAndSend', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Create and send Mail', 1, 'APP-438', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/uploadAttachment', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Upload attachment for Mail', 1, 'APP-439', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appFaq/show', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Show App Faq', 1, 'APP-440', TRUE, FALSE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appFaq/list', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'List App Faq', 1, 'APP-441', FALSE, FALSE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appFaq/create', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Create App Faq', 1, 'APP-442', FALSE, FALSE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appFaq/update', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Update App Faq', 1, 'APP-443', FALSE, FALSE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appFaq/delete', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Delete App Faq', 1, 'APP-444', FALSE, FALSE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appFaq/listFaq', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'List All App Faqs for List View', 1, 'APP-445', FALSE, FALSE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appFaq/reloadFaq', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Refresh App Faq TagLib', 1, 'APP-446', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appDbInstance/connect', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3', 'Connect DB Instance', 1, 'APP-418', FALSE, FALSE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appDbInstance/disconnect', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3', 'Disconnect DB Instance', 1, 'APP-419', FALSE, FALSE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appDbInstance/reconnect', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3', 'Reconnect DB Instance', 1, 'APP-420', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appUser/showProfile', 'ROLE_-2,ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3', 'Show User Profile', 1, 'APP-457', TRUE, TRUE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appUser/updateProfile', 'ROLE_-2,ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3', 'Update User Profile', 1, 'APP-458', FALSE, TRUE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appUser/uploadProfileImage', 'ROLE_-2,ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3', 'Upload User Profile Image', 1, 'APP-460', TRUE, TRUE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appUser/renderProfileImage', 'ROLE_-2,ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3', 'Render User Profile Image', 1, 'APP-459', FALSE, TRUE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appUser/uploadDocument', 'ROLE_-2,ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3', 'Upload User Document', 1, 'APP-463', TRUE, TRUE);
    """

    String insertRoleFeatureMappingSql = """
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-410', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-411', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-412', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-413', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-414', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-415', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-427', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-428', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-429', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-430', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-431', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-432', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-435', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-436', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-437', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-438', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-439', 1);

        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-440', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-441', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-442', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-443', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-444', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-445', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-446', 1);

        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-418', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-419', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-420', 1);

        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-464', 1);
    """

    String alterTableAppSql = """
        ALTER TABLE sys_configuration ADD COLUMN transaction_code character varying(5000);
        UPDATE sys_configuration SET transaction_code = '';
        UPDATE sys_configuration SET transaction_code = 'UpdateAppCompanyUserActionService, CreateAppUserActionService, RegisterAppUserActionService, ResetExpiredPasswordActionService, ExhCreateForCustomerUserActionService'
        WHERE key = 'mis.application.defaultPasswordExpireDuration';
        UPDATE sys_configuration SET transaction_code = 'LoginSuccessActionService' WHERE key = 'mis.application.isMaintenanceMode';
        UPDATE sys_configuration SET transaction_code = 'N/A' WHERE key = 'mis.application.supportedExtensions';
        UPDATE sys_configuration SET transaction_code = 'N/A' WHERE key = 'mis.application.nativeIpAddress';
        UPDATE sys_configuration SET transaction_code = 'CreateAccVoucherActionService, LoginSuccessActionService, CheckAppVersionTagLibActionService, ExhSendExceptionalTaskToBankWithRestActionService, CreateBudgBudgetActionService, CreateForInvInventoryInFromInventoryActionService, CreateForInvInventoryInFromSupplierActionService, CreateProcPurchaseRequestActionService, CreatePtBacklogActionService'
        WHERE key = 'mis.application.enforceReleaseVersion';
        UPDATE sys_configuration SET transaction_code = 'RegisterAppUserActionService' WHERE key = 'mis.application.roleIdForUserRegistration';
        UPDATE sys_configuration SET transaction_code = 'GetSysConfigLoginTemplateActionService, AppCreateTestDataActionService, AppDeleteTestDataActionService, AppMailService, AppSmsService, LoginController'
        WHERE key = 'mis.application.deploymentMode';
        UPDATE sys_configuration SET transaction_code = 'ShowSysConfigTagLibActionService' WHERE key = 'mis.application.phonePattern';
        ALTER TABLE sys_configuration ALTER COLUMN transaction_code SET NOT NULL;

        ALTER TABLE app_shell_script ADD COLUMN number_of_execution integer;
        UPDATE app_shell_script SET number_of_execution = 0;
        ALTER TABLE app_shell_script ALTER COLUMN number_of_execution SET NOT NULL;

        ALTER TABLE app_db_instance ADD COLUMN type_id bigint;
        UPDATE app_db_instance SET type_id = 0;
        UPDATE app_db_instance SET type_id = (SELECT id FROM system_entity WHERE reserved_id = 10000187 AND company_id = app_db_instance.company_id)
        WHERE reserved_vendor_id IN(1143,1144,1145,1146);
        ALTER TABLE app_db_instance ALTER COLUMN type_id SET NOT NULL;

        UPDATE app_db_instance SET url = 'jdbc:sqlserver://localhost'
        WHERE reserved_vendor_id = 1147 AND company_id = app_db_instance.company_id;

        ALTER TABLE app_db_instance ADD COLUMN connection_string character varying(255);
        UPDATE app_db_instance SET connection_string = url || ':' ||  port || '/' || db_name || '?user=' || user_name || '&password=' || password
        WHERE reserved_vendor_id = 1143 AND company_id = app_db_instance.company_id;
        UPDATE app_db_instance SET connection_string = url || ':' ||  port || '/' || db_name || '?user=' || user_name || '&password=' || password
        WHERE reserved_vendor_id = 1144 AND company_id = app_db_instance.company_id;
        UPDATE app_db_instance SET connection_string = url || ':' ||  port || '/' || db_name || '?user=' || user_name || '&password=' || password
        WHERE reserved_vendor_id = 1145 AND company_id = app_db_instance.company_id;
        UPDATE app_db_instance SET connection_string = url || ':' ||  port || '/' || db_name || '?user=' || user_name || '&password=' || password
        WHERE reserved_vendor_id = 1146 AND company_id = app_db_instance.company_id;
        UPDATE app_db_instance SET connection_string = url || ';databaseName=' || db_name || ';user=' || user_name || ';password=' || password
        WHERE reserved_vendor_id = 1147 AND company_id = app_db_instance.company_id;
        UPDATE app_db_instance SET connection_string = url || ':' ||  port || '/' || db_name || '?user=' || user_name || '&password=' || password
        WHERE reserved_vendor_id = 10000189 AND company_id = app_db_instance.company_id;
        ALTER TABLE app_db_instance ALTER COLUMN connection_string SET NOT NULL;

        ALTER TABLE benchmark DROP COLUMN db_instance_id;
        ALTER TABLE benchmark_star DROP COLUMN db_instance_id;
        ALTER TABLE app_user DROP COLUMN default_url;

        DROP TABLE app_theme;
        ALTER TABLE theme RENAME TO app_theme;

        DROP TABLE app_employee;
        ALTER TABLE employee RENAME TO app_employee;

        ALTER TABLE app_employee DROP CONSTRAINT employee_pkey;
        ALTER TABLE app_employee ADD CONSTRAINT app_employee_pkey PRIMARY KEY(id );

        DROP SEQUENCE app_employee_id_seq;
        ALTER SEQUENCE employee_id_seq RENAME TO app_employee_id_seq;

        DROP TABLE app_designation;
        ALTER TABLE designation RENAME TO app_designation;

        ALTER TABLE app_designation DROP CONSTRAINT designation_pkey;
        ALTER TABLE app_designation ADD CONSTRAINT app_designation_pkey PRIMARY KEY(id );

        DROP SEQUENCE app_designation_id_seq;
        ALTER SEQUENCE designation_id_seq RENAME TO app_designation_id_seq;

        DROP TABLE app_customer;
        ALTER TABLE customer RENAME TO app_customer;

        ALTER TABLE app_customer DROP CONSTRAINT customer_pkey;
        ALTER TABLE app_customer ADD CONSTRAINT app_customer_pkey PRIMARY KEY(id );

        DROP SEQUENCE app_customer_id_seq;
        ALTER SEQUENCE customer_id_seq RENAME TO app_customer_id_seq;

        DROP TABLE app_sms;
        ALTER TABLE sms RENAME TO app_sms;

        ALTER TABLE app_sms DROP CONSTRAINT sms_pkey;
        ALTER TABLE app_sms ADD CONSTRAINT app_sms_pkey PRIMARY KEY(id );

        DROP SEQUENCE app_sms_id_seq;
        ALTER SEQUENCE sms_id_seq RENAME TO app_sms_id_seq;

        DROP TABLE app_bank;
        ALTER TABLE bank RENAME TO app_bank;

        ALTER TABLE app_bank DROP CONSTRAINT bank_pkey;
        ALTER TABLE app_bank ADD CONSTRAINT app_bank_pkey PRIMARY KEY(id );

        DROP SEQUENCE app_bank_id_seq;
        ALTER SEQUENCE bank_id_seq RENAME TO app_bank_id_seq;

        DROP TABLE app_bank_branch;
        ALTER TABLE bank_branch RENAME TO app_bank_branch;

        ALTER TABLE app_bank_branch DROP CONSTRAINT bank_branch_pkey;
        ALTER TABLE app_bank_branch ADD CONSTRAINT app_bank_branch_pkey PRIMARY KEY(id );

        DROP SEQUENCE app_bank_branch_id_seq;
        ALTER SEQUENCE bank_branch_id_seq RENAME TO app_bank_branch_id_seq;

        DROP TABLE app_country;
        ALTER TABLE country RENAME TO app_country;

        ALTER TABLE app_country DROP CONSTRAINT country_pkey;
        ALTER TABLE app_country ADD CONSTRAINT app_country_pkey PRIMARY KEY(id );

        DROP SEQUENCE app_country_id_seq;
        ALTER SEQUENCE country_id_seq RENAME TO app_country_id_seq;

        ALTER TABLE app_mail ADD COLUMN is_announcement boolean;
        UPDATE app_mail set is_announcement = false;
        ALTER TABLE app_mail ALTER COLUMN is_announcement SET NOT NULL;

        ALTER TABLE app_mail ADD COLUMN created_by bigint;
        UPDATE app_mail set created_by = (select id from app_user where is_system_user = 't' and company_id = app_mail.company_id);
        ALTER TABLE app_mail ALTER COLUMN created_by SET NOT NULL;

        ALTER TABLE app_mail ADD COLUMN created_on timestamp without time zone;
        UPDATE app_mail SET created_on = LOCALTIMESTAMP;
        ALTER TABLE app_mail ALTER COLUMN created_on SET NOT NULL;

        ALTER TABLE company ADD COLUMN is_default_data_loaded boolean;
        UPDATE company SET is_default_data_loaded = true;
        ALTER TABLE company ALTER COLUMN is_default_data_loaded SET NOT NULL;

        ALTER TABLE company ADD COLUMN is_test_data_loaded boolean;
        UPDATE company SET is_test_data_loaded = true;
        ALTER TABLE company ALTER COLUMN is_test_data_loaded SET NOT NULL;

        ALTER TABLE db_instance_query ALTER COLUMN sql_query TYPE character varying (5000);
        ALTER TABLE request_map ALTER COLUMN config_attribute TYPE character varying (5000);

        ALTER TABLE app_db_instance ADD COLUMN is_deletable boolean;
        UPDATE app_db_instance SET is_deletable = true;
        ALTER TABLE app_db_instance ALTER COLUMN is_deletable SET NOT NULL;

        ALTER TABLE app_bank_branch DROP COLUMN is_drawn_on;
        ALTER TABLE app_bank_branch DROP COLUMN sme_service_center;
        ALTER TABLE app_bank_branch DROP COLUMN within_dhaka_zone;
        ALTER TABLE app_bank_branch DROP COLUMN control_no;
    """

    String createDomainUniqueIndex = """
        DROP INDEX app_db_instance_name_company_id_idx;
        DROP INDEX theme_key_company_id_idx;
        DROP INDEX designation_name_company_id_idx;
        DROP INDEX designation_short_name_company_id_idx;
        DROP INDEX country_name_company_id_idx;
        DROP INDEX country_code_company_id_idx;

        UPDATE reserved_system_entity SET key = 'PT Task' WHERE id = 1094;

        create unique index app_script_name_script_type_id_company_id_idx on app_shell_script(lower(name),script_type_id,company_id);
        CREATE UNIQUE INDEX app_my_favourite_user_id_url_company_id_idx ON app_my_favourite(lower(url),user_id,company_id);
        CREATE UNIQUE INDEX app_server_db_instance_mapping_app_server_instance_id_app_db_instance_id_company_id_idx ON app_server_db_instance_mapping(app_server_instance_id,app_db_instance_id,company_id);
        CREATE UNIQUE INDEX app_server_instance_name_company_id_idx ON app_server_instance(lower(name),company_id);
        CREATE UNIQUE INDEX app_version_release_no_plugin_id_idx ON app_version(release_no,plugin_id);
        CREATE UNIQUE INDEX reserved_role_authority_idx ON reserved_role(lower(authority));
        CREATE UNIQUE INDEX reserved_system_entity_key_type_idx ON reserved_system_entity(lower(key),type);
        CREATE UNIQUE INDEX role_feature_mapping_transaction_code_role_type_id_idx ON role_feature_mapping(lower(transaction_code),role_type_id);
        CREATE UNIQUE INDEX supplier_item_supplier_id_item_id_idx ON supplier_item(supplier_id,item_id);
        CREATE UNIQUE INDEX system_entity_type_name_plugin_id_idx ON system_entity_type(lower(name),plugin_id);
        CREATE UNIQUE INDEX user_role_user_id_role_id_idx ON user_role(user_id,role_id);
        CREATE UNIQUE INDEX app_db_instance_name_type_id_company_id_idx ON app_db_instance(lower(name),type_id,company_id);
        create unique index app_designation_name_company_id_idx on app_designation(lower(name), company_id);
        create unique index app_designation_short_name_company_id_idx on app_designation(lower(short_name), company_id);

        create unique index app_bank_name_country_id_idx on app_bank(lower(name),country_id);
        create unique index app_bank_code_country_id_idx on app_bank(lower(code),country_id);
        create unique index app_bank_branch_name_bank_id_district_id_idx on app_bank_branch(lower(name),bank_id, district_id);
        create unique index app_bank_branch_code_bank_id_district_id_idx on app_bank_branch(lower(code),bank_id, district_id);
        create unique index app_country_name_company_id_idx on app_country(lower(name), company_id);
        create unique index app_country_code_company_id_idx on app_country(lower(code), company_id);

        CREATE UNIQUE INDEX app_theme_key_plugin_id_company_id_idx ON app_theme(lower(key), plugin_id, company_id);
    """

    String updateSystemEntitySql = """
        UPDATE system_entity SET value = 'com.microsoft.sqlserver.jdbc.SQLServerDriver' WHERE reserved_id = 1146;
    """

    String insertSystemEntityTypeSql = """
        INSERT INTO system_entity_type (id, version, description, name, plugin_id)
        VALUES(1746, 0, 'DB Instance Type i.e. Source or Target Database', 'DB Instance Type', 1);

        INSERT INTO system_entity_type (id, version, description, name, plugin_id)
        VALUES(1754, 0, 'Supported mime type for attachment ie pdf, doc, xls, jpg etc', 'Mime Type', 1);

        INSERT INTO system_entity_type (id, version, description, name, plugin_id)
        VALUES(1741, 0, 'Linux, Free BDS', 'OS Vendor', 1);

        INSERT INTO system_entity_type (id, version, description, name, plugin_id)
        VALUES(1751, 0, 'Configuration for mail e.g. smtp email, password, port, host', 'Mail Configuration', 1);

        INSERT INTO system_entity_type (id, version, description, name, plugin_id)
        VALUES(1755, 0, 'e.g. Document', 'FAQ', 1);

        INSERT INTO system_entity_type (id, version, description, name, plugin_id)
        VALUES(1757, 0, 'Hierarchy level of entity eg. sub category', 'Hierarchy Level', 1);

        INSERT INTO system_entity_type (id, version, description, name, plugin_id)
        VALUES(1717, 0, 'Male; Female', 'Gender', 1);

        INSERT INTO system_entity_type (id, version, description, name, plugin_id)
        VALUES(1760, 0, 'Page Type i.e Blog, Post', 'Page Type', 1);
    """

    String insertReservedSystemEntitySql = """
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000187, 'Source DB', 1746, 'DB Instance Type for Source Database', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000188, 'Target DB', 1746, 'DB Instance Type for Target Database', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000189, 'Green Plum', 1735, 'org.postgresql.Driver', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000171, 'Linux', 1741, 'Linux', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000172, 'Free BDS', 1741, 'Free BDS', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000323, 'PDF', 1754, 'application/pdf', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000324, 'TXT', 1754, 'text/plain', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000325, 'DOC', 1754, 'application/msword', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000326, 'DOCX', 1754, 'application/msword', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000327, 'XLS', 1754, 'application/vnd.ms-excel', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000328, 'XLSX', 1754, 'application/vnd.ms-excel', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000329, 'PPT', 1754, 'application/vnd.ms-powerpoint', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000330, 'PPTX', 1754, 'application/vnd.ms-powerpoint', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000331, 'CSV', 1754, 'text/csv', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000332, 'JPG', 1754, 'image/jpeg', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000333, 'JPEG', 1754, 'image/jpeg', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000334, 'PNG', 1754, 'image/png', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000335, 'GIF', 1754, 'image/gif', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000336, 'MP3', 1754, 'audio/mpeg', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000337, 'MP4', 1754, 'video/mp4', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000338, 'BMP', 1754, 'image/bmp', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000339, 'Doc Document', 1754, 'Doc Document', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)VALUES (10000341, 'Doc Sub Category', 1754, 'Doc Sub Category', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id) VALUES (10000322, 'Mail', 701, 'attachment for mail', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id) VALUES (10000363, 'User Document', 701, 'Attachment for User Document', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id) VALUES (10000306, 'Email From', 1751, 'noreply@athena.com.bd', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id) VALUES (10000307, 'Smtp Host', 1751, 'smtp.gmail.com', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id) VALUES (10000308, 'Smtp Port', 1751, '465', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id) VALUES (10000309, 'Smtp Email', 1751, 'noreply.athenamis@gmail.com', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id) VALUES (10000310, 'Smtp Password', 1751, 'athena@123', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id) VALUES (10000344, 'Course', 701, 'Attachment for Course', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id) VALUES (10000345, 'Lesson', 701, 'Attachment for Lesson', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000346, 'Root', 1757, 'Root', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000347, 'Selected node', 1757, 'Selected node', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (176, 'Male', 1717, 'Male', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (177, 'Female', 1717, 'Female', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id) VALUES (10000355, 'Blog', 1760, 'Page for Blog', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id) VALUES (10000361, 'Blog', 1760, 'Page for Post', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id) VALUES (10000356, 'Blog', 703, 'Comment for Blog', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id) VALUES (10000361, 'Blog', 703, 'Comment for Post', 1);
    """

    String insertSystemEntityMisSql = """
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Source DB', 1746, 'DB Instance Type for Source Database', 1, 10000187, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Target DB', 1746, 'DB Instance Type for Target Database', 1, 10000188, 1, 0, 12, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Source DB', 1746, 'DB Instance Type for Source Database', 2, 10000187, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Target DB', 1746, 'DB Instance Type for Target Database', 2, 10000188, 1, 0, 55, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Source DB', 1746, 'DB Instance Type for Source Database', 3, 10000187, 1, 0, 58, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Target DB', 1746, 'DB Instance Type for Target Database', 3, 10000188, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Green Plum', 1735, 'org.postgresql.Driver', 1, 10000189, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Green Plum', 1735, 'org.postgresql.Driver', 2, 10000189, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Green Plum', 1735, 'org.postgresql.Driver', 3, 10000189, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'PDF', 1754, 'application/pdf', 1, 10000323, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'PDF', 1754, 'application/pdf', 2, 10000323, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'PDF', 1754, 'application/pdf', 3, 10000323, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'TXT', 1754, 'text/plain', 1, 10000324, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'TXT', 1754, 'text/plain', 2, 10000324, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'TXT', 1754, 'text/plain', 3, 10000324, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'DOC', 1754, 'application/msword', 1, 10000325, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'DOC', 1754, 'application/msword', 2, 10000325, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'DOC', 1754, 'application/msword', 3, 10000325, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'DOCX', 1754, 'application/msword', 1, 10000326, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'DOCX', 1754, 'application/msword', 2, 10000326, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'DOCX', 1754, 'application/msword', 3, 10000326, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'XLS', 1754, 'application/vnd.ms-excel', 1, 10000327, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'XLS', 1754, 'application/vnd.ms-excel', 2, 10000327, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'XLS', 1754, 'application/vnd.ms-excel', 3, 10000327, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'XLSX', 1754, 'application/vnd.ms-excel', 1, 10000328, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'XLSX', 1754, 'application/vnd.ms-excel', 2, 10000328, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'XLSX', 1754, 'application/vnd.ms-excel', 3, 10000328, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'PPT', 1754, 'application/vnd.ms-powerpoint', 1, 10000329, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'PPT', 1754, 'application/vnd.ms-powerpoint', 2, 10000329, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'PPT', 1754, 'application/vnd.ms-powerpoint', 3, 10000329, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'PPTX', 1754, 'application/vnd.ms-powerpoint', 1, 10000330, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'PPTX', 1754, 'application/vnd.ms-powerpoint', 2, 10000330, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'PPTX', 1754, 'application/vnd.ms-powerpoint', 3, 10000330, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'CSV', 1754, 'text/csv', 1, 10000331, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'CSV', 1754, 'text/csv', 2, 10000331, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'CSV', 1754, 'text/csv', 3, 10000331, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'JPG', 1754, 'image/jpeg', 1, 10000332, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'JPG', 1754, 'image/jpeg', 2, 10000332, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'JPG', 1754, 'image/jpeg', 3, 10000332, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'JPEG', 1754, 'image/jpeg', 1, 10000333, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'JPEG', 1754, 'image/jpeg', 2, 10000333, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'JPEG', 1754, 'image/jpeg', 3, 10000333, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'PNG', 1754, 'image/png', 1, 10000334, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'PNG', 1754, 'image/png', 2, 10000334, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'PNG', 1754, 'image/png', 3, 10000334, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'GIF', 1754, 'image/gif', 1, 10000335, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'GIF', 1754, 'image/gif', 2, 10000335, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'GIF', 1754, 'image/gif', 3, 10000335, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'MP3', 1754, 'audio/mpeg', 1, 10000336, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'MP3', 1754, 'audio/mpeg', 2, 10000336, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'MP3', 1754, 'audio/mpeg', 3, 10000336, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'MP4', 1754, 'video/mp4', 1, 10000337, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'MP4', 1754, 'video/mp4', 2, 10000337, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'MP4', 1754, 'video/mp4', 3, 10000337, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'BMP', 1754, 'image/bmp', 1, 10000338, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'BMP', 1754, 'image/bmp', 2, 10000338, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'BMP', 1754, 'image/bmp', 3, 10000338, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Doc Document', 1755, 'Doc Document', 1, 10000339, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Doc Document', 1755, 'Doc Document', 2, 10000339, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Doc Document', 1755, 'Doc Document', 3, 10000339, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Mail', 701, 'attachment for mail', 1, 10000322, 1, 0, 12, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Mail', 701, 'attachment for mail', 2, 10000322, 1, 0, 55, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Mail', 701, 'attachment for mail', 3, 10000322, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Doc Sub Category', 1755, 'Doc Sub Category', 1, 10000341, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Doc Sub Category', 1755, 'Doc Sub Category', 2, 10000341, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Doc Sub Category', 1755, 'Doc Sub Category', 3, 10000341, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Email From', 1751, 'noreply@athena.com.bd', 1, 10000306, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Smtp Host', 1751, 'smtp.gmail.com', 1, 10000307, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Smtp Port', 1751, '465', 1, 10000308, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Smtp Email', 1751, 'noreply.athenamis@gmail.com', 1, 10000309, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Smtp Password', 1751, 'athena@123', 1, 10000310, 1, 0, 12, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Email From', 1751, 'noreply@athena.com.bd', 2, 10000306, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Smtp Host', 1751, 'smtp.gmail.com', 2, 10000307, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Smtp Port', 1751, '465', 2, 10000308, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Smtp Email', 1751, 'noreply.athenamis@gmail.com', 2, 10000309, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Smtp Password', 1751, 'athena@123', 2, 10000310, 1, 0, 55, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Email From', 1751, 'noreply@athena.com.bd', 3, 10000306, 1, 0, 58, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Smtp Host', 1751, 'smtp.gmail.com', 3, 10000307, 1, 0, 58, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Smtp Port', 1751, '465', 3, 10000308, 1, 0, 58, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Smtp Email', 1751, 'noreply.athenamis@gmail.com', 3, 10000309, 1, 0, 58, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Smtp Password', 1751, 'athena@123', 3, 10000310, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Course', 701, 'Attachment for Course', 1, 10000344, 1, 0, 12, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Course', 701, 'Attachment for Course', 2, 10000344, 1, 0, 55, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Course', 701, 'Attachment for Course', 3, 10000344, 1, 0, 58, now(), 0);

         INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Lesson', 701, 'Attachment for Lesson', 1, 10000345, 1, 0, 12, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Lesson', 701, 'Attachment for Lesson', 2, 10000345, 1, 0, 55, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Lesson', 701, 'Attachment for Lesson', 3, 10000345, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Root', 1757, 'Root', 1, 10000346, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Selected node', 1757, 'Selected node', 1, 10000347, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Root', 1757, 'Root', 2, 10000346, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Selected node', 1757, 'Selected node', 2, 10000347, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Root', 1757, 'Root', 3, 10000346, 1, 0, 58, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Selected node', 1757, 'Selected node', 3, 10000347, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Male', 1717, 'Male', 1, 176, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Female', 1717, 'Female', 1, 177, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Male', 1717, 'Male', 2, 176, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Female', 1717, 'Female', 2, 177, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Male', 1717, 'Male', 3, 176, 1, 0, 58, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Female', 1717, 'Female', 3, 177, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Blog', 1760, 'Page for Blog', 1, 10000355, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Blog', 1760, 'Page for Blog', 2, 10000355, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Blog', 1760, 'Page for Blog', 3, 10000355, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Post', 1760, 'Page for Post', 1, 10000361, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Post', 1760, 'Page for Post', 2, 10000361, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Post', 1760, 'Page for Post', 3, 10000361, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Blog', 703, 'Comment for Blog', 1, 10000356, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Blog', 703, 'Comment for Blog', 2, 10000356, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Blog', 703, 'Comment for Blog', 3, 10000356, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Post', 703, 'Comment for Post', 1, 10000362, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Post', 703, 'Comment for Post', 2, 10000362, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Post', 703, 'Comment for Post', 3, 10000362, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'User Document', 701, 'Attachment for User Document', 1, 10000363, 1, 0, 12, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'User Document', 701, 'Attachment for User Document', 2, 10000363, 1, 0, 55, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'User Document', 701, 'Attachment for User Document', 3, 10000363, 1, 0, 58, now(), 0);


    """

    String insertSql = """
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'Postgres Server',1,(SELECT id FROM system_entity WHERE reserved_id = 1143 AND company_id = 1),(SELECT value FROM system_entity WHERE reserved_id = 1143 AND company_id = 1),(SELECT id FROM system_entity WHERE reserved_id = 10000187 AND company_id = 1),1,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'MySQL Server',2,(SELECT id FROM system_entity WHERE reserved_id = 1144 AND company_id = 1),(SELECT value FROM system_entity WHERE reserved_id = 1144 AND company_id = 1),(SELECT id FROM system_entity WHERE reserved_id = 10000187 AND company_id = 1),1,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'Oracle Server',3,(SELECT id FROM system_entity WHERE reserved_id = 1145 AND company_id = 1),(SELECT value FROM system_entity WHERE reserved_id = 1145 AND company_id = 1),(SELECT id FROM system_entity WHERE reserved_id = 10000187 AND company_id = 1),1,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'MSSQL Server',4,(SELECT id FROM system_entity WHERE reserved_id = 1146 AND company_id = 1),(SELECT value FROM system_entity WHERE reserved_id = 1146 AND company_id = 1),(SELECT id FROM system_entity WHERE reserved_id = 10000187 AND company_id = 1),1,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'Amazon RedShift',5,(SELECT id FROM system_entity WHERE reserved_id = 1147 AND company_id = 1),(SELECT value FROM system_entity WHERE reserved_id = 1147 AND company_id = 1),(SELECT id FROM system_entity WHERE reserved_id = 10000188 AND company_id = 1),1,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'Green Plum',6,(SELECT id FROM system_entity WHERE reserved_id = 10000189 AND company_id = 1),(SELECT value FROM system_entity WHERE reserved_id = 10000189 AND company_id = 1),(SELECT id FROM system_entity WHERE reserved_id = 10000188 AND company_id = 1),1,12,now(),0);

        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'Postgres Server',1,(SELECT id FROM system_entity WHERE reserved_id = 1143 AND company_id = 2),(SELECT value FROM system_entity WHERE reserved_id = 1143 AND company_id = 2),(SELECT id FROM system_entity WHERE reserved_id = 10000187 AND company_id = 2),2,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'MySQL Server',2,(SELECT id FROM system_entity WHERE reserved_id = 1144 AND company_id = 2),(SELECT value FROM system_entity WHERE reserved_id = 1144 AND company_id = 2),(SELECT id FROM system_entity WHERE reserved_id = 10000187 AND company_id = 2),2,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'Oracle Server',3,(SELECT id FROM system_entity WHERE reserved_id = 1145 AND company_id = 2),(SELECT value FROM system_entity WHERE reserved_id = 1145 AND company_id = 2),(SELECT id FROM system_entity WHERE reserved_id = 10000187 AND company_id = 2),2,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'MSSQL Server',4,(SELECT id FROM system_entity WHERE reserved_id = 1146 AND company_id = 2),(SELECT value FROM system_entity WHERE reserved_id = 1146 AND company_id = 2),(SELECT id FROM system_entity WHERE reserved_id = 10000187 AND company_id = 2),2,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'Amazon RedShift',5,(SELECT id FROM system_entity WHERE reserved_id = 1147 AND company_id = 2),(SELECT value FROM system_entity WHERE reserved_id = 1147 AND company_id = 2),(SELECT id FROM system_entity WHERE reserved_id = 10000188 AND company_id = 2),2,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'Green Plum',6,(SELECT id FROM system_entity WHERE reserved_id = 10000189 AND company_id = 2),(SELECT value FROM system_entity WHERE reserved_id = 10000189 AND company_id = 2),(SELECT id FROM system_entity WHERE reserved_id = 10000188 AND company_id = 2),2,12,now(),0);

        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'Postgres Server',1,(SELECT id FROM system_entity WHERE reserved_id = 1143 AND company_id = 3),(SELECT value FROM system_entity WHERE reserved_id = 1143 AND company_id = 3),(SELECT id FROM system_entity WHERE reserved_id = 10000187 AND company_id = 3),3,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'MySQL Server',2,(SELECT id FROM system_entity WHERE reserved_id = 1144 AND company_id = 3),(SELECT value FROM system_entity WHERE reserved_id = 1144 AND company_id = 3),(SELECT id FROM system_entity WHERE reserved_id = 10000187 AND company_id = 3),3,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'Oracle Server',3,(SELECT id FROM system_entity WHERE reserved_id = 1145 AND company_id = 3),(SELECT value FROM system_entity WHERE reserved_id = 1145 AND company_id = 3),(SELECT id FROM system_entity WHERE reserved_id = 10000187 AND company_id = 3),3,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'MSSQL Server',4,(SELECT id FROM system_entity WHERE reserved_id = 1146 AND company_id = 3),(SELECT value FROM system_entity WHERE reserved_id = 1146 AND company_id = 3),(SELECT id FROM system_entity WHERE reserved_id = 10000187 AND company_id = 3),3,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'Amazon RedShift',5,(SELECT id FROM system_entity WHERE reserved_id = 1147 AND company_id = 3),(SELECT value FROM system_entity WHERE reserved_id = 1147 AND company_id = 3),(SELECT id FROM system_entity WHERE reserved_id = 10000188 AND company_id = 3),3,12,now(),0);
        INSERT INTO vendor (id,version,name,sequence,vendor_id,driver,db_type_id,company_id,created_by,created_on,updated_by)
        VALUES(NEXTVAL('vendor_id_seq'),0,'Green Plum',6,(SELECT id FROM system_entity WHERE reserved_id = 10000189 AND company_id = 3),(SELECT value FROM system_entity WHERE reserved_id = 10000189 AND company_id = 3),(SELECT id FROM system_entity WHERE reserved_id = 10000188 AND company_id = 3),3,12,now(),0);

        UPDATE app_db_instance
            SET vendor_id = (SELECT id FROM vendor WHERE vendor_id = (SELECT id FROM system_entity WHERE reserved_id = 1143 AND company_id = app_db_instance.company_id) AND company_id = app_db_instance.company_id)
        WHERE reserved_vendor_id IN(1143);

        UPDATE app_db_instance
        SET vendor_id = (SELECT id FROM vendor WHERE vendor_id = (SELECT id FROM system_entity WHERE reserved_id = 1144 AND company_id = app_db_instance.company_id) AND company_id = app_db_instance.company_id)
        WHERE reserved_vendor_id IN(1144);

        UPDATE app_db_instance
        SET vendor_id = (SELECT id FROM vendor WHERE vendor_id = (SELECT id FROM system_entity WHERE reserved_id = 1145 AND company_id = app_db_instance.company_id) AND company_id = app_db_instance.company_id)
        WHERE reserved_vendor_id IN(1145);

        UPDATE app_db_instance
        SET vendor_id = (SELECT id FROM vendor WHERE vendor_id = (SELECT id FROM system_entity WHERE reserved_id = 1146 AND company_id = app_db_instance.company_id) AND company_id = app_db_instance.company_id)
        WHERE reserved_vendor_id IN(1146);

        UPDATE app_db_instance
        SET type_id = (SELECT db_type_id FROM vendor WHERE vendor_id = (SELECT id FROM system_entity WHERE reserved_id = 1147 AND company_id = app_db_instance.company_id) AND company_id = app_db_instance.company_id),
        vendor_id = (SELECT id FROM vendor WHERE vendor_id = (SELECT id FROM system_entity WHERE reserved_id = 1147 AND company_id = app_db_instance.company_id) AND company_id = app_db_instance.company_id)
        WHERE reserved_vendor_id IN(1147);


        INSERT INTO app_db_instance (id,version,name,generated_name,password,port,reserved_vendor_id, vendor_id,type_id,db_name,url,user_name,connection_string,driver,db_version,company_id,is_native,is_read_only,is_slave,is_tested,created_by,created_on,updated_by,is_deletable)
        VALUES (NEXTVAL('app_db_instance_id_seq'),0,'Green Plum','Green Plum Server (Master)','',5432,10000189,(SELECT id FROM system_entity WHERE reserved_id = 10000189 AND company_id = 1),0,'demo_db_6','jdbc:postgresql://192.168.1.155','postgres','jdbc:postgresql://192.168.1.155:5432/demo_db_6?user=postgres&password=','org.postgresql.Driver','',1,false,false,false,false,12,now(),0, true);

        INSERT INTO app_db_instance (id,version,name,generated_name,password,port,reserved_vendor_id, vendor_id,type_id,db_name,url,user_name,connection_string,driver,db_version,company_id,is_native,is_read_only,is_slave,is_tested,created_by,created_on,updated_by,is_deletable)
        VALUES (NEXTVAL('app_db_instance_id_seq'),0,'Green Plum','Green Plum Server (Master)','',5432,10000189,(SELECT id FROM system_entity WHERE reserved_id = 10000189 AND company_id = 2),0,'demo_db_6','jdbc:postgresql://192.168.1.155','postgres','jdbc:postgresql://192.168.1.155:5432/demo_db_6?user=postgres&password=','org.postgresql.Driver','',2,false,false,false,false,12,now(),0, true);

        INSERT INTO app_db_instance (id,version,name,generated_name,password,port,reserved_vendor_id, vendor_id,type_id,db_name,url,user_name,connection_string,driver,db_version,company_id,is_native,is_read_only,is_slave,is_tested,created_by,created_on,updated_by,is_deletable)
        VALUES (NEXTVAL('app_db_instance_id_seq'),0,'Green Plum','Green Plum Server (Master)','',5432,10000189,(SELECT id FROM system_entity WHERE reserved_id = 10000189 AND company_id = 3),0,'demo_db_6','jdbc:postgresql://192.168.1.155','postgres','jdbc:postgresql://192.168.1.155:5432/demo_db_6?user=postgres&password=','org.postgresql.Driver','',3,false,false,false,false,12,now(),0, true);

        UPDATE app_db_instance
        SET type_id = (SELECT db_type_id FROM vendor WHERE vendor_id = (SELECT id FROM system_entity WHERE reserved_id = 10000189 AND company_id = app_db_instance.company_id) AND company_id = app_db_instance.company_id),
        vendor_id = (SELECT id FROM vendor WHERE vendor_id = (SELECT id FROM system_entity WHERE reserved_id = 10000189 AND company_id = app_db_instance.company_id) AND company_id = app_db_instance.company_id)
        WHERE reserved_vendor_id IN(10000189);
    """

    String dropViewSql = """
        DROP VIEW IF EXISTS list_company_action_service_model;
        DROP VIEW IF EXISTS list_company_for_reseller_action_service_model;
        DROP VIEW IF EXISTS list_employee_action_service_model;
        DROP VIEW IF EXISTS list_app_user_action_service_model;
        DROP VIEW IF EXISTS list_for_send_app_mail_action_service_model;
        DROP VIEW IF EXISTS list_for_compose_app_mail_action_service_model;
        DROP VIEW IF EXISTS list_app_attachment_action_service_model;
        DROP VIEW IF EXISTS list_all_app_user_action_service_model;
        DROP VIEW IF EXISTS list_user_role_action_service_model;
    """

    String deleteReqMap = """
        DELETE FROM request_map WHERE transaction_code = 'APP-183';
        DELETE FROM request_map WHERE transaction_code = 'APP-310';
        DELETE FROM request_map WHERE transaction_code = 'APP-311';
    """

    String deleteRoleFeatMap = """
        DELETE FROM role_feature_mapping WHERE transaction_code = 'APP-183';
        DELETE FROM role_feature_mapping WHERE transaction_code = 'APP-310';
        DELETE FROM role_feature_mapping WHERE transaction_code = 'APP-311';
        DELETE FROM role_feature_mapping WHERE transaction_code = 'APP-152';
    """

    String updateReqMapRoleForExh100 = """
            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_2,ROLE_-206_100,ROLE_-205_100,ROLE_-204_100,ROLE_-203_100,ROLE_-202_100,ROLE_-201_100,ROLE_-12_100,ROLE_-3_100,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/application/renderApplicationMenu';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_2,ROLE_-206_100,ROLE_-205_100,ROLE_-204_100,ROLE_-203_100,ROLE_-202_100,ROLE_-201_100,ROLE_-12_100,ROLE_-3_100,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appTheme/reloadTheme';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_2,ROLE_-206_100,ROLE_-205_100,ROLE_-204_100,ROLE_-203_100,ROLE_-202_100,ROLE_-201_100,ROLE_-12_100,ROLE_-3_100,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/create';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_2,ROLE_-206_100,ROLE_-205_100,ROLE_-204_100,ROLE_-203_100,ROLE_-202_100,ROLE_-201_100,ROLE_-12_100,ROLE_-3_100,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/list';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_2,ROLE_-206_100,ROLE_-205_100,ROLE_-204_100,ROLE_-203_100,ROLE_-202_100,ROLE_-201_100,ROLE_-12_100,ROLE_-3_100,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/delete';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_2,ROLE_-206_100,ROLE_-205_100,ROLE_-204_100,ROLE_-203_100,ROLE_-202_100,ROLE_-201_100,ROLE_-12_100,ROLE_-3_100,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/setAsDefault';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_2,ROLE_-206_100,ROLE_-205_100,ROLE_-204_100,ROLE_-203_100,ROLE_-202_100,ROLE_-201_100,ROLE_-12_100,ROLE_-3_100,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/select';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_2,ROLE_-206_100,ROLE_-205_100,ROLE_-204_100,ROLE_-203_100,ROLE_-202_100,ROLE_-201_100,ROLE_-12_100,ROLE_-3_100,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/show';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_2,ROLE_-206_100,ROLE_-205_100,ROLE_-204_100,ROLE_-203_100,ROLE_-202_100,ROLE_-201_100,ROLE_-12_100,ROLE_-3_100,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/list';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_2,ROLE_-206_100,ROLE_-205_100,ROLE_-204_100,ROLE_-203_100,ROLE_-202_100,ROLE_-201_100,ROLE_-12_100,ROLE_-3_100,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/delete';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_2,ROLE_-206_100,ROLE_-205_100,ROLE_-204_100,ROLE_-203_100,ROLE_-202_100,ROLE_-201_100,ROLE_-12_100,ROLE_-3_100,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/markAsUnRead';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_2,ROLE_-206_100,ROLE_-205_100,ROLE_-204_100,ROLE_-203_100,ROLE_-202_100,ROLE_-201_100,ROLE_-12_100,ROLE_-3_100,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/preview';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_2,ROLE_-206_100,ROLE_-205_100,ROLE_-204_100,ROLE_-203_100,ROLE_-202_100,ROLE_-201_100,ROLE_-12_100,ROLE_-3_100,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMail/listAnnouncementForDashboard';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-201_100,ROLE_-205_100,ROLE_-206_100',
                is_common = TRUE
            WHERE url = '/appAttachment/upload';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_2,ROLE_-206_100,ROLE_-205_100,ROLE_-204_100,ROLE_-203_100,ROLE_-202_100,ROLE_-201_100,ROLE_-12_100,ROLE_-3_100,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appAttachment/downloadContent';
        """


    String updateReqMapRoleForExh101 = """
            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-206_101,ROLE_-205_101,ROLE_-204_101,ROLE_-203_101,ROLE_-202_101,ROLE_-201_101,ROLE_-12_101,ROLE_-3_101,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/application/renderApplicationMenu';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-206_101,ROLE_-205_101,ROLE_-204_101,ROLE_-203_101,ROLE_-202_101,ROLE_-201_101,ROLE_-12_101,ROLE_-3_101,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appTheme/reloadTheme';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-206_101,ROLE_-205_101,ROLE_-204_101,ROLE_-203_101,ROLE_-202_101,ROLE_-201_101,ROLE_-12_101,ROLE_-3_101,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/create';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-206_101,ROLE_-205_101,ROLE_-204_101,ROLE_-203_101,ROLE_-202_101,ROLE_-201_101,ROLE_-12_101,ROLE_-3_101,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/list';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-206_101,ROLE_-205_101,ROLE_-204_101,ROLE_-203_101,ROLE_-202_101,ROLE_-201_101,ROLE_-12_101,ROLE_-3_101,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/delete';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-206_101,ROLE_-205_101,ROLE_-204_101,ROLE_-203_101,ROLE_-202_101,ROLE_-201_101,ROLE_-12_101,ROLE_-3_101,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/setAsDefault';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-206_101,ROLE_-205_101,ROLE_-204_101,ROLE_-203_101,ROLE_-202_101,ROLE_-201_101,ROLE_-12_101,ROLE_-3_101,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/select';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-206_101,ROLE_-205_101,ROLE_-204_101,ROLE_-203_101,ROLE_-202_101,ROLE_-201_101,ROLE_-12_101,ROLE_-3_101,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/show';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-206_101,ROLE_-205_101,ROLE_-204_101,ROLE_-203_101,ROLE_-202_101,ROLE_-201_101,ROLE_-12_101,ROLE_-3_101,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/list';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-206_101,ROLE_-205_101,ROLE_-204_101,ROLE_-203_101,ROLE_-202_101,ROLE_-201_101,ROLE_-12_101,ROLE_-3_101,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/delete';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-206_101,ROLE_-205_101,ROLE_-204_101,ROLE_-203_101,ROLE_-202_101,ROLE_-201_101,ROLE_-12_101,ROLE_-3_101,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/markAsUnRead';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-206_101,ROLE_-205_101,ROLE_-204_101,ROLE_-203_101,ROLE_-202_101,ROLE_-201_101,ROLE_-12_101,ROLE_-3_101,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/preview';

             UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-206_101,ROLE_-205_101,ROLE_-204_101,ROLE_-203_101,ROLE_-202_101,ROLE_-201_101,ROLE_-12_101,ROLE_-3_101,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMail/listAnnouncementForDashboard';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-201_101,ROLE_-205_101,ROLE_-206_101',
                is_common = TRUE
            WHERE url = '/appAttachment/upload';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-206_101,ROLE_-205_101,ROLE_-204_101,ROLE_-203_101,ROLE_-202_101,ROLE_-201_101,ROLE_-12_101,ROLE_-3_101,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appAttachment/downloadContent';
        """


    String updateReqMapRoleForExh102 = """
            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-302_102,ROLE_-301_102,ROLE_-206_102,ROLE_-205_102,ROLE_-204_102,ROLE_-203_102,ROLE_-202_102,ROLE_-201_102,ROLE_-12_102,ROLE_-3_102,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/application/renderApplicationMenu';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-302_102,ROLE_-301_102,ROLE_-206_102,ROLE_-205_102,ROLE_-204_102,ROLE_-203_102,ROLE_-202_102,ROLE_-201_102,ROLE_-12_102,ROLE_-3_102,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appTheme/reloadTheme';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-302_102,ROLE_-301_102,ROLE_-206_102,ROLE_-205_102,ROLE_-204_102,ROLE_-203_102,ROLE_-202_102,ROLE_-201_102,ROLE_-12_102,ROLE_-3_102,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/create';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-302_102,ROLE_-301_102,ROLE_-206_102,ROLE_-205_102,ROLE_-204_102,ROLE_-203_102,ROLE_-202_102,ROLE_-201_102,ROLE_-12_102,ROLE_-3_102,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/list';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-302_102,ROLE_-301_102,ROLE_-206_102,ROLE_-205_102,ROLE_-204_102,ROLE_-203_102,ROLE_-202_102,ROLE_-201_102,ROLE_-12_102,ROLE_-3_102,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/delete';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-302_102,ROLE_-301_102,ROLE_-206_102,ROLE_-205_102,ROLE_-204_102,ROLE_-203_102,ROLE_-202_102,ROLE_-201_102,ROLE_-12_102,ROLE_-3_102,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/setAsDefault';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-302_102,ROLE_-301_102,ROLE_-206_102,ROLE_-205_102,ROLE_-204_102,ROLE_-203_102,ROLE_-202_102,ROLE_-201_102,ROLE_-12_102,ROLE_-3_102,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMyFavourite/select';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-302_102,ROLE_-301_102,ROLE_-206_102,ROLE_-205_102,ROLE_-204_102,ROLE_-203_102,ROLE_-202_102,ROLE_-201_102,ROLE_-12_102,ROLE_-3_102,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/show';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-302_102,ROLE_-301_102,ROLE_-206_102,ROLE_-205_102,ROLE_-204_102,ROLE_-203_102,ROLE_-202_102,ROLE_-201_102,ROLE_-12_102,ROLE_-3_102,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/list';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-302_102,ROLE_-301_102,ROLE_-206_102,ROLE_-205_102,ROLE_-204_102,ROLE_-203_102,ROLE_-202_102,ROLE_-201_102,ROLE_-12_102,ROLE_-3_102,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/delete';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-302_102,ROLE_-301_102,ROLE_-206_102,ROLE_-205_102,ROLE_-204_102,ROLE_-203_102,ROLE_-202_102,ROLE_-201_102,ROLE_-12_102,ROLE_-3_102,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/markAsUnRead';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-302_102,ROLE_-301_102,ROLE_-206_102,ROLE_-205_102,ROLE_-204_102,ROLE_-203_102,ROLE_-202_102,ROLE_-201_102,ROLE_-12_102,ROLE_-3_102,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMessage/preview';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-302_102,ROLE_-301_102,ROLE_-206_102,ROLE_-205_102,ROLE_-204_102,ROLE_-203_102,ROLE_-202_102,ROLE_-201_102,ROLE_-12_102,ROLE_-3_102,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appMail/listAnnouncementForDashboard'';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-201_102,ROLE_-205_102,ROLE_-206_102',
                is_common = TRUE
            WHERE url = '/appAttachment/upload';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-302_102,ROLE_-301_102,ROLE_-206_102,ROLE_-205_102,ROLE_-204_102,ROLE_-203_102,ROLE_-202_102,ROLE_-201_102,ROLE_-12_102,ROLE_-3_102,ROLE_RESELLER',
                is_common = TRUE
            WHERE url = '/appAttachment/downloadContent';
        """


    String updateReqMapRoleForApp = """
            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3',
                is_common = TRUE
            WHERE url = '/application/renderApplicationMenu';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3',
                is_common = TRUE
            WHERE url = '/appTheme/reloadTheme';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3',
                is_common = TRUE
            WHERE url = '/appMyFavourite/create';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3',
                is_common = TRUE
            WHERE url = '/appMyFavourite/list';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3',
                is_common = TRUE
            WHERE url = '/appMyFavourite/delete';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3',
                is_common = TRUE
            WHERE url = '/appMyFavourite/setAsDefault';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3',
                is_common = TRUE
            WHERE url = '/appMyFavourite/select';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-19_1,ROLE_-19_2,ROLE_-19_3,ROLE_-31_1,ROLE_-31_2,ROLE_-31_3ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_-25_1,ROLE_-25_2,ROLE_-25_3',
                is_common = TRUE
            WHERE url = '/appAttachment/upload';

            UPDATE request_map
            SET config_attribute = config_attribute || ',ROLE_-18_2,ROLE_-17_2,ROLE_-16_2,ROLE_-12_1,ROLE_-11_1,ROLE_-10_1,ROLE_-8_1,ROLE_-7_1,ROLE_-6_1,ROLE_-5_1,ROLE_-4_1,ROLE_-3_1,ROLE_1_1,ROLE_2_1,ROLE_4_1,ROLE_5_1,ROLE_6_1,ROLE_7_1,ROLE_8_1,ROLE_9_1,ROLE_10_1,ROLE_11_1,ROLE_12_1,ROLE_13_1,ROLE_-7_2,ROLE_-3_2,ROLE_-10_2,ROLE_-12_2,ROLE_-4_2,ROLE_-11_2,ROLE_-8_2,ROLE_-5_2,ROLE_-6_2,ROLE_-7_3,ROLE_-3_3,ROLE_-10_3,ROLE_-12_3,ROLE_-4_3,ROLE_-11_3,ROLE_-8_3,ROLE_-5_3,ROLE_-6_3,ROLE_-18_1,ROLE_-17_1,ROLE_-16_1,ROLE_36_1,ROLE_RESELLER,ROLE_-19_1,ROLE_-20_1,ROLE_-19_2,ROLE_-20_2,ROLE_-19_3,ROLE_-20_3,ROLE_-23_1,ROLE_-24_1,ROLE_-23_2,ROLE_-24_2,ROLE_-23_3,ROLE_-24_3,ROLE_-21_1,ROLE_-22_1,ROLE_-21_2,ROLE_-22_2,ROLE_-21_3,ROLE_-22_3,ROLE_-25_1,ROLE_-26_1,ROLE_-25_2,ROLE_-26_2,ROLE_-25_3,ROLE_-26_3,ROLE_-29_1,ROLE_-30_1,ROLE_-29_2,ROLE_-30_2,ROLE_-29_3,ROLE_-30_3,ROLE_-27_1,ROLE_-28_1,ROLE_-27_2,ROLE_-28_2,ROLE_-27_3,ROLE_-28_3,ROLE_-31_1,ROLE_-32_1,ROLE_-31_2,ROLE_-32_2,ROLE_-31_3',
                is_common = TRUE
            WHERE url = '/appAttachment/downloadContent';
        """

    String updateReqMap = """
        UPDATE request_map SET url = '/appTheme/showTheme' WHERE transaction_code = 'APP-124';
        UPDATE request_map SET url = '/appTheme/updateTheme' WHERE transaction_code = 'APP-125';
        UPDATE request_map SET url = '/appTheme/listTheme' WHERE transaction_code = 'APP-126';

        UPDATE request_map SET url = '/appSms/showSms' WHERE transaction_code = 'APP-180';
        UPDATE request_map SET url = '/appSms/updateSms' WHERE transaction_code = 'APP-181';
        UPDATE request_map SET url = '/appSms/listSms' WHERE transaction_code = 'APP-182';
        UPDATE request_map SET url = '/appSms/sendSms' WHERE transaction_code = 'APP-237';
        UPDATE request_map SET url = '/appSms/create' WHERE transaction_code = 'APP-374';
        UPDATE request_map SET url = '/appSms/delete' WHERE transaction_code = 'APP-375';
        UPDATE request_map SET url = '/appSms/showForCompose' WHERE transaction_code = 'APP-376';
        UPDATE request_map SET url = '/appSms/listForCompose' WHERE transaction_code = 'APP-377';
        UPDATE request_map SET url = '/appSms/updateForCompose' WHERE transaction_code = 'APP-378';
        UPDATE request_map SET url = '/appSms/sendForCompose' WHERE transaction_code = 'APP-379';
        UPDATE request_map SET url = '/appSms/showForSend' WHERE transaction_code = 'APP-380';
        UPDATE request_map SET url = '/appSms/listForSend' WHERE transaction_code = 'APP-381';
        UPDATE request_map SET url = '/appSms/reCompose' WHERE transaction_code = 'APP-382';

        UPDATE request_map SET url = '/appBank/show' WHERE transaction_code = 'APP-192';
        UPDATE request_map SET url = '/appBank/create' WHERE transaction_code = 'APP-193';
        UPDATE request_map SET url = '/appBank/update' WHERE transaction_code = 'APP-194';
        UPDATE request_map SET url = '/appBank/list' WHERE transaction_code = 'APP-196';
        UPDATE request_map SET url = '/appBank/delete' WHERE transaction_code = 'APP-197';
        UPDATE request_map SET url = '/appBank/reloadBankDropDownTagLib' WHERE transaction_code = 'APP-260';

        UPDATE request_map SET url = '/appBankBranch/show' WHERE transaction_code = 'APP-203';
        UPDATE request_map SET url = '/appBankBranch/create' WHERE transaction_code = 'APP-204';
        UPDATE request_map SET url = '/appBankBranch/update' WHERE transaction_code = 'APP-205';
        UPDATE request_map SET url = '/appBankBranch/list' WHERE transaction_code = 'APP-207';
        UPDATE request_map SET url = '/appBankBranch/delete' WHERE transaction_code = 'APP-208';
        UPDATE request_map SET url = '/appBankBranch/reloadBranchesDropDownByBankAndDistrict' WHERE transaction_code = 'APP-209';
        UPDATE request_map SET url = '/appBankBranch/listDistributionPoint' WHERE transaction_code = 'APP-210';

        UPDATE request_map SET url = '/appEmployee/show' WHERE transaction_code = 'APP-44';
        UPDATE request_map SET url = '/appEmployee/create' WHERE transaction_code = 'APP-45';
        UPDATE request_map SET url = '/appEmployee/update' WHERE transaction_code = 'APP-47';
        UPDATE request_map SET url = '/appEmployee/delete' WHERE transaction_code = 'APP-48';
        UPDATE request_map SET url = '/appEmployee/list' WHERE transaction_code = 'APP-49';

        UPDATE request_map SET url = '/appDesignation/show' WHERE transaction_code = 'APP-118';
        UPDATE request_map SET url = '/appDesignation/create' WHERE transaction_code = 'APP-119';
        UPDATE request_map SET url = '/appDesignation/update' WHERE transaction_code = 'APP-120';
        UPDATE request_map SET url = '/appDesignation/delete' WHERE transaction_code = 'APP-121';
        UPDATE request_map SET url = '/appDesignation/list' WHERE transaction_code = 'APP-122';

        UPDATE request_map SET url = '/appCustomer/show' WHERE transaction_code = 'APP-38';
        UPDATE request_map SET url = '/appCustomer/create' WHERE transaction_code = 'APP-39';
        UPDATE request_map SET url = '/appCustomer/update' WHERE transaction_code = 'APP-41';
        UPDATE request_map SET url = '/appCustomer/delete' WHERE transaction_code = 'APP-42';
        UPDATE request_map SET url = '/appCustomer/list' WHERE transaction_code = 'APP-43';

        UPDATE request_map SET url = '/appCountry/show' WHERE transaction_code = 'APP-93';
        UPDATE request_map SET url = '/appCountry/create' WHERE transaction_code = 'APP-94';
        UPDATE request_map SET url = '/appCountry/update' WHERE transaction_code = 'APP-96';
        UPDATE request_map SET url = '/appCountry/delete' WHERE transaction_code = 'APP-95';
        UPDATE request_map SET url = '/appCountry/list' WHERE transaction_code = 'APP-98';

        UPDATE request_map SET url = '/appMail/createAnnouncement', feature_name = 'Create Announcement' WHERE transaction_code = 'APP-364';
        UPDATE request_map SET url = '/appMail/deleteAnnouncement', feature_name = 'Delete Announcement' WHERE transaction_code = 'APP-365';
        UPDATE request_map SET url = '/appMail/sendAnnouncement', feature_name = 'Send Announcement' WHERE transaction_code = 'APP-366';
        UPDATE request_map SET url = '/appMail/showAnnouncement', feature_name = 'Show Announcement' WHERE transaction_code = 'APP-367';
        UPDATE request_map SET url = '/appMail/listAnnouncement', feature_name = 'List/Search Announcement' WHERE transaction_code = 'APP-368';
        UPDATE request_map SET url = '/appMail/updateAnnouncement', feature_name = 'Update Announcement' WHERE transaction_code = 'APP-369';
        UPDATE request_map SET url = '/appMail/reComposeAnnouncement', feature_name = 'Re Compose Announcement' WHERE transaction_code = 'APP-372';
    """

    String dropColumn = """
        ALTER TABLE app_mail DROP COLUMN recipients_bcc;
        ALTER TABLE company DROP COLUMN email;
        ALTER TABLE company DROP COLUMN smtp_host;
        ALTER TABLE company DROP COLUMN smtp_port;
        ALTER TABLE company DROP COLUMN smtp_email;
        ALTER TABLE company DROP COLUMN smtp_pwd;
        ALTER TABLE company DROP COLUMN sms_url;
    """

    String sysConfigSmsUrl = """
        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message, transaction_code)
        VALUES(nextVal('sys_configuration_id_seq'), 1, 'Determines sms url for sending sms', 'mis.application.smsUrl', 1, 0, '"http://mySmsApi?user=username&password=pwd&sender=abc&SMSText=" + \${content} + "&GSM=" + \${recipient}', 0, null,'');

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message, transaction_code)
        VALUES(nextVal('sys_configuration_id_seq'), 2, 'Determines sms url for sending sms', 'mis.application.smsUrl', 1, 0, '"http://mySmsApi?user=username&password=pwd&sender=abc&SMSText=" + \${content} + "&GSM=" + \${recipient}', 0, null,'');

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message, transaction_code)
        VALUES(nextVal('sys_configuration_id_seq'), 3, 'Determines sms url for sending sms', 'mis.application.smsUrl', 1, 0, '"http://mySmsApi?user=username&password=pwd&sender=abc&SMSText=" + \${content} + "&GSM=" + \${recipient}', 0, null,'');
    """
    String sysConfigMis = """
        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message,transaction_code)
        VALUES(nextVal('sys_configuration_id_seq'), 1, 'Determine attachment size. Maximum size is 5 MB. Value should be in byte.', 'mis.application.attachmentSize', 1, 0, '5242880', 0, null,'UploadAppAttachmentActionService');

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message,transaction_code)
        VALUES(nextVal('sys_configuration_id_seq'), 2, 'Determine attachment size. Maximum size is 5 MB. Value should be in byte.', 'mis.application.attachmentSize', 1, 0, '5242880', 0, null,'UploadAppAttachmentActionService');

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message,transaction_code)
        VALUES(nextVal('sys_configuration_id_seq'), 3, 'Determine attachment size. Maximum size is 5 MB. Value should be in byte.', 'mis.application.attachmentSize', 1, 0, '5242880', 0, null,'UploadAppAttachmentActionService');

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message,transaction_code)
        VALUES(nextVal('sys_configuration_id_seq'), 1, 'Determines plugin id. Render plugin wise index page.', 'mis.application.defaultPlugin', 1, 0, '1', 0, null,'RenderIndexActionService');

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message,transaction_code)
        VALUES(nextVal('sys_configuration_id_seq'), 2, 'Determines plugin id. Render plugin wise index page.', 'mis.application.defaultPlugin', 1, 0, '1', 0, null,'RenderIndexActionService');

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message,transaction_code)
        VALUES(nextVal('sys_configuration_id_seq'), 3, 'Determines plugin id. Render plugin wise index page.', 'mis.application.defaultPlugin', 1, 0, '1', 0, null,'RenderIndexActionService');
    """

    String dropConstraint = """
        ALTER TABLE user_role DROP CONSTRAINT fk143bf46a8bcf6d52;
        ALTER TABLE user_role DROP CONSTRAINT fk143bf46ae9151943;
    """

    String requestMapSql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appAttachment/upload',
        'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Upload attachment', 1, 'APP-421', FALSE, FALSE);
    """

    String roleFeatureMappingSql = """
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-421', 1);
    """

    String updateThemeSql = """
        UPDATE app_theme SET value = value || '
        .expand-div {
            cursor: pointer;
        }' WHERE key = 'app.cssMainComponents';

        UPDATE app_theme set description = 'DEFAULT = kendo.default.min.css;
                    BLACK = kendo.black.min.css;
                    BLUEOPAL = kendo.blueopal.min.css;
                    BOOTSTRAP = kendo.bootstrap.min.css;
                    FIORI = kendo.fiori.min.css;
                    FLAT = kendo.fiori.min.css;
                    HIGH_CONYRAST = kendo.highcontrast.min.css;
                    MATERIAL = kendo.material.min.css;
                    MATERIAL_BLACK = kendo.materialblack.min.css;
                    METRO = kendo.metro.min.css;
                    METRO_BLACK = kendo.metroblack.min.css;
                    MOONLIGHT = kendo.moonlight.min.css;
                    NOVA = kendo.nova.min.css;
                    OFFICE_365 = kendo.office365.min.css;
                    SILVER = kendo.silver.min.css;
                    UNIFORM = kendo.uniform.min.css;'
        where key = 'app.kendoTheme';

         UPDATE app_theme SET value = '
         .panel-primary > .panel-heading {
             background-color: #F5F5F5;
             border-color: #CCCCCC;
             color: #515967;
         }

         .panel-footer {
             border-top: 1px solid #cccccc;
         }

         .panel-title {
            font-size: 15px;
            margin-left: 25px;
         }

         .panel-primary {
            border-color: #cccccc;
         }

         .panel-heading {
            padding: 5px 15px;
         }

         .panel-footer {
            padding: 5px 15px;
         }

         .form-group {
            margin-bottom: 7px;
         }

         body {
            font-size: 13px;
            line-height: 1.2;
         }

         .form-horizontal .control-label {
           padding-top: 2px;
         }

         .panel-body {
           padding: 7px;
         }

         .panel {
            margin-bottom: 7px;
         }

         .table {
          margin-bottom:7px;
         }

         /* For fixed width of html report label */
         td.active {
            width:15%;
         }

         /* For Container,Row etc. with no-padding */
         .no-padding-margin {
            padding-left:0;
            padding-right:0;
            margin-left:0;
            margin-right:0;
         }

         .text-right {
            text-align: right !important;
         }

         .doc-icon {
             font-size:  1.5em;
         }

         /* with no right padding */
         .no-right-padding {
           padding-right: 0 !important;
           margin: 0 !important;
         }

         .form-control {
            height: 30px;
            box-shadow: 0 0 1px rgba(0, 0, 0, 0.075) inset;
         }

         .btn {
            padding: 3px 12px;
         }

         /* override blockquote font-size */
            blockquote {
            font-size: 13px;
         }

         .popover {
            font-size: 13px;
         }

         .panel-icon {
            font-size: 15px;
            color: #428bca;
            padding-top: 2px;
         }'
         WHERE key = 'app.cssBootstrapCustom';

        UPDATE app_theme SET value = value || '
            <link rel="stylesheet" href="/plugins/applicationplugin-0.1/theme/application/css/kendo/kendo.silver.mobile.min.css"/>
        ' WHERE key = 'app.kendoTheme';
    """

    String updateSysEntityPluginIdSql = """
        UPDATE system_entity SET plugin_id =  1 WHERE type in (651, 701, 703);
        UPDATE reserved_system_entity SET plugin_id =  1 WHERE type in (651, 701, 703);
    """

    String addSql = """
        Alter table app_theme Add column plugin_id integer;
        update app_theme set plugin_id = 1;
        Alter table app_theme ALTER COLUMN plugin_id SET NOT NULL;

        ALTER TABLE app_sms ADD COLUMN is_required_recipients boolean;
        UPDATE app_sms SET is_required_recipients = false;
        ALTER TABLE app_sms ALTER COLUMN is_required_recipients SET NOT NULL;
    """

    String addThemeReqMap = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appTheme/reloadTheme',
        'ROLE_-2', 'Reload Theme', 1, 'APP-447', FALSE, TRUE);
    """

    String addColumnVersionSql = """
        ALTER TABLE app_version ADD COLUMN plugin_name character varying(255);
        UPDATE app_version SET plugin_name = 'Application' where plugin_id = 1;
        UPDATE app_version SET plugin_name = 'Accounting' where plugin_id = 2;
        UPDATE app_version SET plugin_name = 'Budget' where plugin_id = 3;
        UPDATE app_version SET plugin_name = 'Inventory' where plugin_id = 4;
        UPDATE app_version SET plugin_name = 'Procurement' where plugin_id = 5;
        UPDATE app_version SET plugin_name = 'QS' where plugin_id = 6;
        UPDATE app_version SET plugin_name = 'FixedAsset' where plugin_id = 7;
        UPDATE app_version SET plugin_name = 'ExchangeHouse' where plugin_id = 9;
        UPDATE app_version SET plugin_name = 'ProjectTrack' where plugin_id = 10;
        UPDATE app_version SET plugin_name = 'ARMS' where plugin_id = 11;
        UPDATE app_version SET plugin_name = 'SARB' where plugin_id = 12;
        UPDATE app_version SET plugin_name = 'Document' where plugin_id = 13;
        UPDATE app_version SET plugin_name = 'DataPipeLine' where plugin_id = 14;
        ALTER TABLE app_version ALTER COLUMN plugin_name SET NOT NULL;

        ALTER TABLE app_version ADD COLUMN plugin_prefix character varying(255);
        UPDATE app_version SET plugin_prefix = 'APP' where plugin_id = 1;
        UPDATE app_version SET plugin_prefix = 'ACC' where plugin_id = 2;
        UPDATE app_version SET plugin_prefix = 'BUDG' where plugin_id = 3;
        UPDATE app_version SET plugin_prefix = 'INV' where plugin_id = 4;
        UPDATE app_version SET plugin_prefix = 'PROC' where plugin_id = 5;
        UPDATE app_version SET plugin_prefix = 'QS' where plugin_id = 6;
        UPDATE app_version SET plugin_prefix = 'FXD' where plugin_id = 7;
        UPDATE app_version SET plugin_prefix = 'EXH' where plugin_id = 9;
        UPDATE app_version SET plugin_prefix = 'PT' where plugin_id = 10;
        UPDATE app_version SET plugin_prefix = 'ARMS' where plugin_id = 11;
        UPDATE app_version SET plugin_prefix = 'SARB' where plugin_id = 12;
        UPDATE app_version SET plugin_prefix = 'DOC' where plugin_id = 13;
        UPDATE app_version SET plugin_prefix = 'DPL' where plugin_id = 14;
        ALTER TABLE app_version ALTER COLUMN plugin_prefix SET NOT NULL;
    """

    String addColumnIsDraftSql = """
        ALTER TABLE app_attachment ADD COLUMN is_draft boolean;
        UPDATE app_attachment SET is_draft = false;
        ALTER TABLE app_attachment ALTER COLUMN is_draft SET NOT NULL;
    """

    String addColumnOnAppUserSql = """
        ALTER TABLE app_user DROP COLUMN IF EXISTS is_switchable;
        ALTER TABLE app_user DROP COLUMN IF EXISTS is_reserved;
        ALTER TABLE app_user DROP COLUMN IF EXISTS gender_id;
        ALTER TABLE app_user DROP COLUMN IF EXISTS email;

        ALTER TABLE app_user ADD COLUMN is_switchable boolean;
        UPDATE app_user SET is_switchable = false;
        ALTER TABLE app_user ALTER COLUMN is_switchable SET NOT NULL;

        ALTER TABLE app_user ADD COLUMN is_reserved boolean;
        UPDATE app_user SET is_reserved = false;
        ALTER TABLE app_user ALTER COLUMN is_reserved SET NOT NULL;

        ALTER TABLE app_user ADD COLUMN gender_id bigint;
        UPDATE app_user SET gender_id = 0;

        ALTER TABLE app_user ADD COLUMN email character varying(255);
    """

    String updateAppMessageSql = """
        ALTER TABLE app_message ADD COLUMN body character varying(2040);
        UPDATE app_message SET body = (SELECT body FROM app_mail WHERE id = app_message.app_mail_id);
        ALTER TABLE app_message ALTER COLUMN body SET NOT NULL;
    """

    String insertReqMapMisSql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/dbInstanceQuery/executeQuery', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Execute query from create panel', 1, 'APP-464', FALSE, FALSE);
    """

    String insertReqMapExh100Sql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/dbInstanceQuery/executeQuery', 'ROLE_-2,ROLE_-12_100',
        'Execute query from create panel', 1, 'APP-464', FALSE, FALSE);
    """

    String insertReqMapExh101Sql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/dbInstanceQuery/executeQuery', 'ROLE_-2,ROLE_-12_101',
        'Execute query from create panel', 1, 'APP-464', FALSE, FALSE);
    """

    String insertReqMapExh102Sql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/dbInstanceQuery/executeQuery', 'ROLE_-2,ROLE_-12_102',
        'Execute query from create panel', 1, 'APP-464', FALSE, FALSE);
    """

    String appSysConfig = """
        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, transaction_code, updated_by, value, version, message)
        VALUES(nextVal('sys_configuration_id_seq'), 1, 'Determine application menu show or not. default value true, means show', 'mis.application.showApplicationMenu', 1, '', 0, 'true', 0, null);
    """
}
