package com.athena.mis.application.deployment

import com.athena.mis.AppSql
import com.athena.mis.AppUpdatePatch
import com.athena.mis.application.model.*
import com.athena.mis.utility.DateUtility
import groovy.sql.GroovyRowResult

/**
 * Script version: 1
 * Start Date: 19-Feb-15
 * Release Dates By Project:
 * MIS: n/a
 * EXH: 25-Feb-15
 */
class AppUpdatePatch1Service extends AppUpdatePatch {

    private static final String RELEASE_DATE = "31/12/2014" // DD/MM/YYYY

    public Date getReleaseDate() {
        return DateUtility.parseMaskedDate(RELEASE_DATE)
    }

    public void init() {
        if (lstAppSql.size() > 0) return
        // common sql
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: companyIsActiveSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: kendoCustomSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: dbInstanceSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: deleteReqMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: appShellScriptAddColumnSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: systemEntityTypeSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: themeSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: userRoleRoleFeatureMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: versionSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppCompanyUserActionServiceModel.SQL_LIST_APP_COMPANY_USER_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppUserEntityActionServiceModel.SQL_APP_USER_ENTITY_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListBankActionServiceModel.SQL_LIST_BANK_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListBankBranchActionServiceModel.SQL_LIST_BANK_BRANCH_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListCompanyActionServiceModel.SQL_LIST_COMPANY_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListContentCategoryActionServiceModel.SQL_LIST_CONTENT_CATEGORY_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListDistrictActionServiceModel.SQL_LIST_DISTRICT_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppNoteActionServiceModel.SQL_APP_NOTE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListItemCategoryInventoryActionServiceModel.SQL_LIST_ITEM_CATEGORY_INVENTORY_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListItemCategoryNonInvActionServiceModel.SQL_LIST_ITEM_CATEGORY_NON_INVENTORY_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListItemTypeActionServiceModel.SQL_LIST_ITEM_TYPE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListMyRoleActionServiceModel.SQL_MY_ROLE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListSupplierActionServiceModel.SQL_LIST_SUPPLIER_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListSupplierItemActionServiceModel.SQL_LIST_SUPPLIER_ITEM_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListSystemEntityTypeActionServiceModel.SQL_LIST_SYSTEM_ENTITY_TYPE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListUserRoleActionServiceModel.SQL_USER_ROLE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListItemCategoryFixedAssetActionServiceModel.SQL_LIST_ITEM_CATEGORY_FIX_ASSET_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: alterUpdateRoleTypeSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: alterUpdateRoleSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: alterAppUser)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppUserActionServiceModel.SQL_LIST_APP_USER_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListRoleActionServiceModel.SQL_LIST_ROLE_MODEL)

        // for SFSL (UK 100)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: updateSuperUserToCompanyUserSqlSFSL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: appScheduleSqlSFSL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: insertDefaultDataShellScriptSqlSFSL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: userRoleSqlSFSL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: insertResellerRoleSqlSFSL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: insertAppUserSFSL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: roleRightMappingRequestMapSqlSFSL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: dbInstanceRequestMapSfsl)

        // for SFSA (AU 101)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: updateSuperUserToCompanyUserSqlSFSA)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: appScheduleSqlSFSA)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: insertDefaultDataShellScriptSqlSFSA)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: userRoleSqlSFSA)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: insertResellerRoleSqlSFSA)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: insertAppUserSFSA)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: roleRightMappingRequestMapSqlSFSA)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: dbInstanceRequestMapSfsa)

        // for SECL (SA 102)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: updateSuperUserToCompanyUserSqlSECL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: appScheduleSqlSECL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: insertDefaultDataShellScriptSqlSECL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: userRoleSqlSECL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: insertResellerRoleSqlSECL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: insertAppUserSECL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: roleRightMappingRequestMapSqlSECL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: dropConstraintSecl)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: dbInstanceRequestMapSecl)

        // common sql
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: updateRequestMapRoleFeatureForResellerSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAllAppUserActionServiceModel.SQL_LIST_ALL_APP_USER_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertReqMapForAllAppUserSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: updateEntityNoteTypeSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: roleRightMappingRoleFeatureMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: deleteRequestMap)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: updateFeatureName)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListCompanyForResellerActionServiceModel.SQL_LIST_COMPANY_FOR_RESELLER_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: resellerRequestMap)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: companyEmailSql)
        lstAppSql << new AppSql(type: TYPE_METHOD, companyId: 0L, methodName: "updateCompanySequence")
    }

    String kendoCustomSql = """
    update theme set value =
        '
        /*Application Kendo CSS is used to overwrite the default behaviour of kendo ui components*/
        input[type=text].k-textbox,input[type=password].k-textbox {
            height: 2em;
        }
        .k-textbox[type=text]:disabled,.k-textbox[type=password]:disabled,textarea:hover:disabled,textarea:disabled {
            background-color: #E3E3E3;
            border-color: #C5C5C5;
            color: #9F9E9E;
            opacity: 0.7;
            cursor: default;
            outline: 0 none
        }
        .k-menu .k-item > .k-link {
            padding: 0 0.9em 0;
        }

        #application_top_panel .k-widget,
        #application_top_panel .k-textbox {
            width: 100%;
        }

        /* Omit kendo behaviors on List view of html Reports */
        tbody.k-widget {
          border-style:none;
          position:static;
        }
        span.k-tooltip {
            padding: 2px 0;
        }/* For kendo grid filter text box */

        .k-filter-row .k-dropdown-operator {
           right:0;
        }
        .k-grid tbody>tr:last-child>td {
           border-bottom: 1px solid #F9F0F0;
        }
        .k-filtercell > span {
           padding-right:2.1em;
        }.k-grid .k-button{
          margin:0;
        }
        .k-grid .k-menu.k-menu-horizontal{
            border-style: none;
        }'
        where key = 'app.cssKendoCustom';
    """

    String companyIsActiveSql = """
        ALTER TABLE company ADD COLUMN is_active boolean;
        UPDATE company SET is_active = true;
        ALTER TABLE company ALTER COLUMN is_active SET NOT NULL;
    """

    String dbInstanceSql = """
        ALTER TABLE app_db_instance DROP COLUMN sql_query;
        ALTER TABLE db_instance_query DROP COLUMN message;
        ALTER TABLE db_instance_query DROP COLUMN result_per_page;
        ALTER TABLE db_instance_query DROP COLUMN number_of_execution;
        ALTER TABLE db_instance_query ADD COLUMN message character varying(255);
        ALTER TABLE db_instance_query ADD COLUMN result_per_page integer;
        ALTER TABLE db_instance_query ADD COLUMN number_of_execution integer;
        UPDATE db_instance_query SET number_of_execution=0, result_per_page=0 ;
        ALTER TABLE db_instance_query ALTER COLUMN result_per_page SET NOT NULL;
        ALTER TABLE db_instance_query ALTER COLUMN number_of_execution SET NOT NULL;
    """

    String deleteReqMapSql = """
        DELETE FROM request_map WHERE transaction_code IN('APP-52','APP-139','APP-225','APP-179','APP-243','APP-195','APP-246','APP-206','APP-214','APP-21','APP-90','APP-34','APP-97','APP-132','APP-70','APP-256');
        DELETE FROM role_feature_mapping WHERE transaction_code IN('APP-52','APP-139','APP-225','APP-179','APP-243','APP-195','APP-246','APP-206','APP-214','APP-21','APP-90','APP-34','APP-97','APP-132','APP-70','APP-256');
    """

    String appScheduleSqlSFSL = """
        INSERT INTO app_schedule
        (id, version, company_id, cron_expression, enable, job_class_name, name,
        plugin_id, repeat_count, repeat_interval, schedule_type_id, updated_by,
        updated_on, action_name)
        VALUES(nextval('app_schedule_id_seq'), 0, 100, null, false, 'com.athena.mis.application.job.AppBackupScriptJob',
        'Backup Scheduler', 1, 0, 10000,
        (SELECT id FROM system_entity WHERE reserved_id = 1148 AND company_id = 100),
        0, null, 'AppBackupScriptJobActionService');


        INSERT INTO app_schedule
        (id, version, company_id, cron_expression, enable, job_class_name, name,
        plugin_id, repeat_count, repeat_interval, schedule_type_id, updated_by,
        updated_on, action_name)
        VALUES(nextval('app_schedule_id_seq'), 0, 100, null, false, 'com.athena.mis.application.job.AppMaintenanceScriptJob',
        'Maintenance Scheduler', 1, 0, 10000,
        (SELECT id FROM system_entity WHERE reserved_id = 1148 AND company_id = 100),
        0, null, 'AppMaintenanceScriptJobActionService');
    """

    String appScheduleSqlSFSA = """
        INSERT INTO app_schedule
        (id, version, company_id, cron_expression, enable, job_class_name, name,
        plugin_id, repeat_count, repeat_interval, schedule_type_id, updated_by,
        updated_on, action_name)
        VALUES(nextval('app_schedule_id_seq'), 0, 101, null, false, 'com.athena.mis.application.job.AppBackupScriptJob',
        'Backup Scheduler', 1, 0, 10000,
        (SELECT id FROM system_entity WHERE reserved_id = 1148 AND company_id = 101),
        0, null, 'AppBackupScriptJobActionService');


        INSERT INTO app_schedule
        (id, version, company_id, cron_expression, enable, job_class_name, name,
        plugin_id, repeat_count, repeat_interval, schedule_type_id, updated_by,
        updated_on, action_name)
        VALUES(nextval('app_schedule_id_seq'), 0, 101, null, false, 'com.athena.mis.application.job.AppMaintenanceScriptJob',
        'Maintenance Scheduler', 1, 0, 10000,
        (SELECT id FROM system_entity WHERE reserved_id = 1148 AND company_id = 101),
        0, null, 'AppMaintenanceScriptJobActionService');
    """

    String appScheduleSqlSECL = """
        INSERT INTO app_schedule
        (id, version, company_id, cron_expression, enable, job_class_name, name,
        plugin_id, repeat_count, repeat_interval, schedule_type_id, updated_by,
        updated_on, action_name)
        VALUES(nextval('app_schedule_id_seq'), 0, 102, null, false, 'com.athena.mis.application.job.AppBackupScriptJob',
        'Backup Scheduler', 1, 0, 10000,
        (SELECT id FROM system_entity WHERE reserved_id = 1148 AND company_id = 102),
        0, null, 'AppBackupScriptJobActionService');


        INSERT INTO app_schedule
        (id, version, company_id, cron_expression, enable, job_class_name, name,
        plugin_id, repeat_count, repeat_interval, schedule_type_id, updated_by,
        updated_on, action_name)
        VALUES(nextval('app_schedule_id_seq'), 0, 102, null, false, 'com.athena.mis.application.job.AppMaintenanceScriptJob',
        'Maintenance Scheduler', 1, 0, 10000,
        (SELECT id FROM system_entity WHERE reserved_id = 1148 AND company_id = 102),
        0, null, 'AppMaintenanceScriptJobActionService');
    """

    String appShellScriptAddColumnSql = """
        ALTER TABLE app_shell_script DROP COLUMN is_reserved;
        alter table app_shell_script add column is_reserved boolean;
        update app_shell_script set is_reserved = 't';
        alter table app_shell_script alter column is_reserved set not null;
    """

    String insertDefaultDataShellScriptSqlSFSL = """
        INSERT INTO app_shell_script(id, version, company_id, created_by, created_on, is_reserved, name, plugin_id, script, updated_by, updated_on)
        VALUES (NEXTVAL('app_shell_script_id_seq'), 0, 100, 3, '2015-02-03 15:06:54.407', TRUE, 'Backup Script',1, 'echo "backup"',0,null);
        INSERT INTO app_shell_script(id, version, company_id, created_by, created_on, is_reserved, name, plugin_id, script, updated_by, updated_on)
        VALUES (NEXTVAL('app_shell_script_id_seq'), 0, 100, 3, '2015-02-03 15:06:54.407', TRUE, 'Maintenance Script',1, 'echo "maintenance"',0,null);
    """

    String insertDefaultDataShellScriptSqlSFSA = """
        INSERT INTO app_shell_script(id, version, company_id, created_by, created_on, is_reserved, name, plugin_id, script, updated_by, updated_on)
        VALUES (NEXTVAL('app_shell_script_id_seq'), 0, 101, 3, '2015-02-03 15:06:54.407', TRUE, 'Backup Script',1, 'echo "backup"',0,null);
        INSERT INTO app_shell_script(id, version, company_id, created_by, created_on, is_reserved, name, plugin_id, script, updated_by, updated_on)
        VALUES (NEXTVAL('app_shell_script_id_seq'), 0, 101, 3, '2015-02-03 15:06:54.407', TRUE, 'Maintenance Script',1, 'echo "maintenance"',0,null);
    """

    String insertDefaultDataShellScriptSqlSECL = """
        INSERT INTO app_shell_script(id, version, company_id, created_by, created_on, is_reserved, name, plugin_id, script, updated_by, updated_on)
        VALUES (NEXTVAL('app_shell_script_id_seq'), 0, 102, 3, '2015-02-03 15:06:54.407', TRUE, 'Backup Script',1, 'echo "backup"',0,null);
        INSERT INTO app_shell_script(id, version, company_id, created_by, created_on, is_reserved, name, plugin_id, script, updated_by, updated_on)
        VALUES (NEXTVAL('app_shell_script_id_seq'), 0, 102, 3, '2015-02-03 15:06:54.407', TRUE, 'Maintenance Script',1, 'echo "maintenance"',0,null);
    """

    String systemEntityTypeSql = """
        ALTER TABLE system_entity_type ALTER COLUMN version SET DATA TYPE bigint;
        DELETE from request_map where transaction_code like 'APP-110';
        DELETE from role_feature_mapping where transaction_code like 'APP-110';
        DELETE from request_map where transaction_code like 'APP-111';
        DELETE from role_feature_mapping where transaction_code like 'APP-111';
    """

    String themeSql = """
        update theme set value = value ||
        '.c-red{color: #C80000 !important;}'
        where key = 'app.cssMainComponents';
    """

    String userRoleSqlSFSL = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/userRole/dropDownAppUserForRoleReload', 'ROLE_-2,ROLE_-3_100', 'User Role Dropdown reload', 1, 'APP-328',
        FALSE, FALSE);
    """

    String userRoleSqlSFSA = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/userRole/dropDownAppUserForRoleReload', 'ROLE_-2,ROLE_-3_101', 'User Role Dropdown reload', 1, 'APP-328',
        FALSE, FALSE);
    """

    String userRoleSqlSECL = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/userRole/dropDownAppUserForRoleReload', 'ROLE_-2,ROLE_-3_102', 'User Role Dropdown reload', 1, 'APP-328',
        FALSE, FALSE);
    """

    String userRoleRoleFeatureMapSql = """
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-328', 1);
    """

    String versionSql = """
        ALTER TABLE content_category ALTER COLUMN version SET DATA TYPE bigint;
        ALTER TABLE company ALTER COLUMN version SET DATA TYPE bigint;
        ALTER TABLE supplier_item ALTER COLUMN version SET DATA TYPE bigint;
        ALTER TABLE supplier ALTER COLUMN version SET DATA TYPE bigint;
        ALTER TABLE item_type ALTER COLUMN version SET DATA TYPE bigint;
        ALTER TABLE entity_content ALTER COLUMN version SET DATA TYPE bigint;
        ALTER TABLE entity_note ALTER COLUMN version SET DATA TYPE bigint;
        ALTER TABLE app_user ALTER COLUMN version SET DATA TYPE bigint;
    """

    String alterUpdateRoleTypeSql = """
        DELETE FROM role_type where id = -2;
        ALTER TABLE role_type ADD COLUMN plugin_id bigint;
        UPDATE role_type SET plugin_id = 1 WHERE id IN (-3,-12);
        UPDATE role_type SET plugin_id = 9 WHERE id IN (-201,-202,-203,-204);
        ALTER TABLE role_type ALTER COLUMN plugin_id SET NOT NULL;
    """

    String alterUpdateRoleSql = """
        DELETE FROM role where id = -2;
        ALTER TABLE role ADD COLUMN plugin_id bigint;
        UPDATE role SET plugin_id = 1 WHERE role_type_id IN (-3,-12);
        UPDATE role SET plugin_id = 9 WHERE role_type_id IN (-201,-202,-203,-204);
        ALTER TABLE role ALTER COLUMN plugin_id SET NOT NULL;

        ALTER TABLE role ADD COLUMN is_reseller boolean;
        UPDATE role SET is_reseller = false;
        ALTER TABLE role ALTER COLUMN is_reseller SET NOT NULL;
    """

    String insertResellerRoleSqlSFSL = """
        INSERT INTO role (id, version, role_type_id, authority, name, company_id, created_on, created_by, updated_by, plugin_id, is_reseller)
        VALUES (NEXTVAL('role_id_seq'), 0, 0, 'ROLE_RESELLER', 'Reseller', 100, '2015-02-12 13:39:20',
        (select id from app_user where is_system_user = 't'), 0, 1, true);
    """

    String insertResellerRoleSqlSFSA = """
        INSERT INTO role (id, version, role_type_id, authority, name, company_id, created_on, created_by, updated_by, plugin_id, is_reseller)
        VALUES (NEXTVAL('role_id_seq'), 0, 0, 'ROLE_RESELLER', 'Reseller', 101, '2015-02-12 13:39:20',
        (select id from app_user where is_system_user = 't'), 0, 1, true);
    """

    String insertResellerRoleSqlSECL = """
        INSERT INTO role (id, version, role_type_id, authority, name, company_id, created_on, created_by, updated_by, plugin_id, is_reseller)
        VALUES (NEXTVAL('role_id_seq'), 0, 0, 'ROLE_RESELLER', 'Reseller', 102, '2015-02-12 13:39:20',
        (select id from app_user where is_system_user = 't'), 0, 1, true);
    """

    String alterAppUser = """
        ALTER TABLE app_user ADD COLUMN is_reseller boolean;
        UPDATE app_user SET is_reseller = false;
        ALTER TABLE app_user ALTER COLUMN is_reseller SET NOT NULL;
    """

    String insertAppUserSFSL = """
        INSERT INTO app_user (id, version, account_expired, account_locked, company_id, enabled, login_id, next_expire_date, password, password_expired, username, has_signature,
          cell_number, ip_address, is_company_user, employee_id, activation_link, is_activated_by_mail, is_power_user, is_config_manager, is_disable_password_expiration,
          password_reset_code, password_reset_link, password_reset_validity, created_on, created_by, updated_on, updated_by, is_system_user, is_reseller)
        VALUES (NEXTVAL('app_user_id_seq'), 0, false, false, 100, true, 'reseller@athena.com', '2016-02-12 14:20:30', 'ddd92ca212a97fbb5411ebe802026aeeb32077f6989eb677f11ec78218eea257',
        false, 'Reseller', false, null, null, false, 0, null, false, true, true, true, null, null, null, '2015-02-12 14:26:30',
        (select id from app_user where is_system_user = 't'), null, 0, false, true);
    """

    String insertAppUserSFSA = """
        INSERT INTO app_user (id, version, account_expired, account_locked, company_id, enabled, login_id, next_expire_date, password, password_expired, username, has_signature,
          cell_number, ip_address, is_company_user, employee_id, activation_link, is_activated_by_mail, is_power_user, is_config_manager, is_disable_password_expiration,
          password_reset_code, password_reset_link, password_reset_validity, created_on, created_by, updated_on, updated_by, is_system_user, is_reseller)
        VALUES (NEXTVAL('app_user_id_seq'), 0, false, false, 101, true, 'reseller@athena.com', '2016-02-12 14:20:30', 'ddd92ca212a97fbb5411ebe802026aeeb32077f6989eb677f11ec78218eea257',
        false, 'Reseller', false, null, null, false, 0, null, false, true, true, true, null, null, null, '2015-02-12 14:26:30',
        (select id from app_user where is_system_user = 't'), null, 0, false, true);
    """

    String insertAppUserSECL = """
        INSERT INTO app_user (id, version, account_expired, account_locked, company_id, enabled, login_id, next_expire_date, password, password_expired, username, has_signature,
          cell_number, ip_address, is_company_user, employee_id, activation_link, is_activated_by_mail, is_power_user, is_config_manager, is_disable_password_expiration,
          password_reset_code, password_reset_link, password_reset_validity, created_on, created_by, updated_on, updated_by, is_system_user, is_reseller)
        VALUES (NEXTVAL('app_user_id_seq'), 0, false, false, 102, true, 'reseller@athena.com', '2016-02-12 14:20:30', 'ddd92ca212a97fbb5411ebe802026aeeb32077f6989eb677f11ec78218eea257',
        false, 'Reseller', false, null, null, false, 0, null, false, true, true, true, null, null, null, '2015-02-12 14:26:30',
        (select id from app_user where is_system_user = 't'), null, 0, false, true);
    """

    String updateRequestMapRoleFeatureForResellerSql = """
        INSERT INTO user_role (role_id, user_id)
        VALUES ((SELECT role.id FROM role WHERE is_reseller = true), (SELECT app_user.id FROM app_user WHERE is_reseller = true));

        UPDATE request_map SET config_attribute = config_attribute || ',ROLE_RESELLER' WHERE is_common = true;
        UPDATE request_map SET config_attribute = config_attribute || ',ROLE_RESELLER' WHERE transaction_code IN ('APP-92','APP-51');

        DELETE FROM role_feature_mapping WHERE transaction_code IN ('APP-86','APP-87','APP-88','APP-89','APP-91');

        UPDATE request_map SET config_attribute = 'ROLE_-2,ROLE_RESELLER' WHERE transaction_code IN ('APP-86','APP-87','APP-88','APP-89','APP-91');
    """

    String insertReqMapForAllAppUserSql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appUser/showAllUser', 'ROLE_-2,ROLE_RESELLER', 'Show All User', 1, 'APP-333', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appUser/listAllUser', 'ROLE_-2,ROLE_RESELLER', 'Get List of All User', 1, 'APP-334', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appUser/updateAllUser', 'ROLE_-2,ROLE_RESELLER', 'Update All User', 1, 'APP-335', FALSE, FALSE);
    """

    String updateEntityNoteTypeSql = """
        UPDATE system_entity_type SET name='Entity Note Type' WHERE id=1729;
    """

    String roleRightMappingRequestMapSqlSFSL = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/requestMap/listAvailableRole', 'ROLE_-2,ROLE_-12_100', 'Get list of available features', 1, 'APP-331', FALSE, FALSE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/requestMap/listAssignedRole', 'ROLE_-2,ROLE_-12_100', 'Get list of assigned features', 1, 'APP-332', FALSE, FALSE);
    """

    String roleRightMappingRequestMapSqlSFSA = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/requestMap/listAvailableRole', 'ROLE_-2,ROLE_-12_101', 'Get list of available features', 1, 'APP-331', FALSE, FALSE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/requestMap/listAssignedRole', 'ROLE_-2,ROLE_-12_101', 'Get list of assigned features', 1, 'APP-332', FALSE, FALSE);
    """

    String roleRightMappingRequestMapSqlSECL = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/requestMap/listAvailableRole', 'ROLE_-2,ROLE_-12_102', 'Get list of available features', 1, 'APP-331', FALSE, FALSE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/requestMap/listAssignedRole', 'ROLE_-2,ROLE_-12_102', 'Get list of assigned features', 1, 'APP-332', FALSE, FALSE);
    """

    String roleRightMappingRoleFeatureMapSql = """
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-331', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-332', 1);
    """

    String deleteRequestMap = """
        DELETE FROM request_map WHERE transaction_code = 'APP-27';
        DELETE FROM role_feature_mapping WHERE transaction_code = 'APP-27';
    """

    String updateFeatureName = """
        update request_map set feature_name = 'Unblock exchange house customer' where transaction_code = 'EXH-259';
    """

    String resellerRequestMap = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq')
        , 0, '/company/showForReseller', 'ROLE_-2,ROLE_RESELLER', 'Show company for Reseller',1, 'APP-336', FALSE,TRUE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/company/listForReseller', 'ROLE_-2,ROLE_RESELLER', 'List company for Reseller',1, 'APP-337', FALSE,TRUE);
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/company/updateForReseller', 'ROLE_-2,ROLE_RESELLER', 'Update company for Reseller',1, 'APP-338', FALSE,TRUE);
    """

    String companyEmailSql = """
        ALTER TABLE company ALTER COLUMN email SET NOT NULL;
    """

    String updateSuperUserToCompanyUserSqlSFSL = """
        UPDATE app_user SET is_company_user  = 't' WHERE is_config_manager = 't' AND company_id = 100;
    """

    String updateSuperUserToCompanyUserSqlSFSA = """
        UPDATE app_user SET is_company_user  = 't' WHERE is_config_manager = 't' AND company_id = 101;
    """

    String updateSuperUserToCompanyUserSqlSECL = """
        UPDATE app_user SET is_company_user  = 't' WHERE is_config_manager = 't' AND company_id = 102;
    """

    String dropConstraintSecl = """
        ALTER TABLE app_bank_branch DROP CONSTRAINT bank_branch_code_key;
        ALTER TABLE currency DROP CONSTRAINT currency_symbol_key;
    """

    private void updateCompanySequence() {
        List<GroovyRowResult> result = executeSelectSql("SELECT MAX(company.id) FROM company;")
        long start = result[0][0] as long
        start += 1

        String createSequence = """
            DROP SEQUENCE IF EXISTS company_id_seq;
            CREATE SEQUENCE company_id_seq
              INCREMENT 1
              MINVALUE ${start}
              MAXVALUE 2147483647
              START ${start}
              CACHE 1;
        """
        executeSql(createSequence)
    }

    String dbInstanceRequestMapSfsl = """
    INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
    VALUES (NEXTVAL('request_map_id_seq'), 0, '/dbInstanceQuery/execute', 'ROLE_-2,ROLE_-12_100', 'Execute query', 1, 'APP-329', FALSE, FALSE);

    INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
    VALUES (NEXTVAL('request_map_id_seq'), 0, '/appDbInstance/listDbTable', 'ROLE_-2,ROLE_-12_100', 'Get List of Table for db instance', 1, 'APP-330', FALSE, FALSE);
    """


    String dbInstanceRequestMapSfsa = """
    INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
    VALUES (NEXTVAL('request_map_id_seq'), 0, '/dbInstanceQuery/execute', 'ROLE_-2,ROLE_-12_101', 'Execute query', 1, 'APP-329', FALSE, FALSE);

    INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
    VALUES (NEXTVAL('request_map_id_seq'), 0, '/appDbInstance/listDbTable', 'ROLE_-2,ROLE_-12_101', 'Get List of Table for db instance', 1, 'APP-330', FALSE, FALSE);
    """

    String dbInstanceRequestMapSecl = """
    INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
    VALUES (NEXTVAL('request_map_id_seq'), 0, '/dbInstanceQuery/execute', 'ROLE_-2,ROLE_-12_102', 'Execute query', 1, 'APP-329', FALSE, FALSE);

    INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
    VALUES (NEXTVAL('request_map_id_seq'), 0, '/appDbInstance/listDbTable', 'ROLE_-2,ROLE_-12_102', 'Get List of Table for db instance', 1, 'APP-330', FALSE, FALSE);
    """

}
