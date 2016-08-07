package com.athena.mis.application.deployment

import com.athena.mis.AppSql
import com.athena.mis.AppUpdatePatch
import com.athena.mis.application.model.*
import com.athena.mis.utility.DateUtility

class AppUpdatePatch10Service extends AppUpdatePatch {

    private static final String RELEASE_DATE = "01/09/2015" // DD/MM/YYYY

    public Date getReleaseDate() {
        return DateUtility.parseMaskedDate(RELEASE_DATE)
    }

    void init() {
        if (lstAppSql.size() > 0) return
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertSystemEntityTypeSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertReservedSystemEntitySql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertSystemEntityMisSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: alterTableSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: deleteSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertRequestMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertRoleFeatureMappingSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: dropViewSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppServerInstanceActionServiceModel.SQL_LIST_APP_SERVER_INSTANCE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: removeReqMapAndRoleFeatMapTestDataShow)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: alterTableAppSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListRoleActionServiceModel.SQL_LIST_ROLE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: updateThemeSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppNoteActionServiceModel.SQL_APP_NOTE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListRoleModuleActionServiceModel.SQL_LIST_ROLE_MODULE)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: shellScriptSql)
        // MIS
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: roleModuleMisSql)
    }

    String dropViewSql = """
        DROP VIEW IF EXISTS list_app_server_instance_action_service_model;
        DROP VIEW IF EXISTS list_entity_content_action_service_model;
        DROP VIEW IF EXISTS list_role_action_service_model;
    """

    String alterTableSql = """
        UPDATE app_db_instance SET generated_name = name || ' ' || '(Master)' WHERE is_slave = false;
        UPDATE app_db_instance SET generated_name = name || ' ' || '(Slave)' WHERE is_slave = true;
        UPDATE app_db_instance SET generated_name = name || ' ' || '(Native & Master)' WHERE is_slave = false AND is_native = true;

        ALTER TABLE app_shell_script ADD COLUMN script_type_id bigint;
        UPDATE app_shell_script SET script_type_id = 0;
        UPDATE app_shell_script SET script_type_id = (SELECT id FROM system_entity WHERE reserved_id = 10000181 AND company_id = app_shell_script.company_id);
        ALTER TABLE app_shell_script ALTER COLUMN script_type_id SET NOT NULL;

        ALTER TABLE db_instance_query ADD COLUMN is_reserved boolean;
        UPDATE db_instance_query SET is_reserved = false;
        ALTER TABLE db_instance_query ALTER COLUMN is_reserved SET NOT NULL;

        ALTER TABLE app_db_instance ALTER COLUMN is_slave SET NOT NULL;
    """

    String deleteSql = """
        DELETE FROM app_schedule WHERE job_class_name = 'com.athena.mis.application.job.TestReminderJob';
    """

    String insertSystemEntityTypeSql = """
        INSERT INTO system_entity_type (id, version, description, name, plugin_id)
        VALUES(1743, 0, 'Table, View', 'DB Object Type', 1);

        INSERT INTO system_entity_type (id, version, description, name, plugin_id)
        VALUES(1744, 0, 'Shell, SQL', 'Script Type', 1);

        INSERT INTO system_entity_type (id, version, description, name, plugin_id)
        VALUES(1745, 0, 'Query for diagnostic report etc.', 'Query Type', 1);
    """

    String insertReservedSystemEntitySql = """
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000179, 'Table', 1743, 'Table', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000180, 'View', 1743, 'View', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000181, 'Shell', 1744, 'Shell', 1);
        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000182, 'SQL', 1744, 'SQL', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000183, 'Query', 703, 'Note for query', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000185, 'Script', 703, 'Note for script', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000184, 'Diagnostic', 1745, 'Query for diagnostic report', 1);

        INSERT INTO reserved_system_entity (id, key, type, value, plugin_id)
        VALUES (10000186, 'Maintenance', 1745, 'Query for Maintenance SQL Scheduler', 1);
    """

    String insertSystemEntityMisSql = """
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Table', 1743, 'Table', 1, 10000179, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'View', 1743, 'View', 1, 10000180, 1, 0, 12, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Table', 1743, 'Table', 2, 10000179, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'View', 1743, 'View', 2, 10000180, 1, 0, 55, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Table', 1743, 'Table', 3, 10000179, 1, 0, 58, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'View', 1743, 'View', 3, 10000180, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Shell', 1744, 'Shell', 1, 10000181, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'SQL', 1744, 'SQL', 1, 10000182, 1, 0, 12, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Shell', 1744, 'Shell', 2, 10000181, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'SQL', 1744, 'SQL', 2, 10000182, 1, 0, 55, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Shell', 1744, 'Shell', 3, 10000181, 1, 0, 58, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'SQL', 1744, 'SQL', 3, 10000182, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Query', 703, 'Note Entity Type Query', 1, 10000183, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Script', 703, 'Note Entity Type Script', 1, 10000185, 1, 0, 12, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Query', 703, 'Note Entity Type Query', 2, 10000183, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('2' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Script', 703, 'Note Entity Type Script', 2, 10000185, 1, 0, 55, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Query', 703, 'Note Entity Type Query', 3, 10000183, 1, 0, 58, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('3' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Script', 703, 'Note Entity Type Script', 3, 10000185, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Diagnostic', 1745, 'Query for diagnostic report', 1, 10000184, 1, 0, 12, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Maintenance', 1745, 'Query for Maintenance SQL Scheduler', 1, 10000186, 1, 0, 12, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Diagnostic', 1745, 'Query for diagnostic report', 2, 10000184, 1, 0, 55, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Maintenance', 1745, 'Query for Maintenance SQL Scheduler', 2, 10000186, 1, 0, 55, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Diagnostic', 1745, 'Query for diagnostic report', 3, 10000184, 1, 0, 58, now(), 0);
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Maintenance', 1745, 'Query for Maintenance SQL Scheduler', 3, 10000186, 1, 0, 58, now(), 0);
    """

    String insertRequestMapSql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appShellScript/showSql',
        'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3', 'Show SQL Script', 1, 'APP-400', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appShellScript/evaluateSqlScript',
        'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3', 'Evaluate SQL Script', 1, 'APP-401', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/dbInstanceQuery/downloadQueryResult',
        'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3', 'Download Query Result', 1, 'APP-402', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/roleModule/show',
        'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Show Role Module', 1, 'APP-403', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/roleModule/create',
        'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Create Role Module', 1, 'APP-404', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/roleModule/update',
        'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Update Role Module', 1, 'APP-405', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/roleModule/delete',
        'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Delete Role Module', 1, 'APP-406', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/roleModule/list',
        'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'List Role Module', 1, 'APP-407', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/roleModule/dropDownRoleModuleReload',
        'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3', 'Reload module dropdown', 1, 'APP-408', FALSE, FALSE);
    """

    String insertRoleFeatureMappingSql = """
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-400', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-401', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-402', 1);

        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-403', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-404', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-405', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-406', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-407', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-408', 1);
    """

    String removeReqMapAndRoleFeatMapTestDataShow = """
        DELETE FROM request_map WHERE transaction_code in('EXH-361','EXH-363','EXH-364','RMS-152','RMS-154','RMS-155','BUDG-102','BUDG-103','BUDG-104','PROC-102','PROC-103','PROC-104','ACC-282','ACC-283','ACC-284','INV-240','INV-241','INV-242','PT-160','PT-161','PT-162');
        DELETE FROM role_feature_mapping WHERE transaction_code in('EXH-361','EXH-363','EXH-364','RMS-152','RMS-154','RMS-155','BUDG-102','BUDG-103','BUDG-104','PROC-102','PROC-103','PROC-104','ACC-282','ACC-283','ACC-284','INV-240','INV-241','INV-242','PT-160','PT-161','PT-162');
    """

    String alterTableAppSql = """
        DROP TABLE app_attachment;
        ALTER TABLE entity_content RENAME TO app_attachment;

        ALTER TABLE app_attachment DROP CONSTRAINT entity_content_pkey;
        ALTER TABLE app_attachment ADD CONSTRAINT app_attachment_pkey PRIMARY KEY(id );

        DROP SEQUENCE app_attachment_id_seq;
        ALTER SEQUENCE entity_content_id_seq RENAME TO app_attachment_id_seq;

        UPDATE request_map SET url = '/appAttachment/show', feature_name = 'Show attachment' WHERE transaction_code = 'APP-144';
        UPDATE request_map SET url = '/appAttachment/list', feature_name = 'List attachment' WHERE transaction_code = 'APP-146';
        UPDATE request_map SET url = '/appAttachment/update', feature_name = 'Update attachment' WHERE transaction_code = 'APP-147';
        UPDATE request_map SET url = '/appAttachment/create', feature_name = 'Create attachment' WHERE transaction_code = 'APP-148';
        UPDATE request_map SET url = '/appAttachment/delete', feature_name = 'Delete attachment' WHERE transaction_code = 'APP-149';
        UPDATE request_map SET url = '/appAttachment/downloadContent', feature_name = 'Download attachment' WHERE transaction_code = 'APP-152';

        ALTER TABLE db_instance_query ADD COLUMN query_type_id bigint;
        UPDATE db_instance_query SET query_type_id = 0;
        ALTER TABLE db_instance_query ALTER COLUMN query_type_id SET NOT NULL;

        DROP TABLE app_note;
        ALTER TABLE entity_note RENAME TO app_note;

        ALTER TABLE app_note DROP CONSTRAINT entity_comment_pkey;
        ALTER TABLE app_note ADD CONSTRAINT app_note_pkey PRIMARY KEY(id );

        DROP SEQUENCE app_note_id_seq;
        ALTER SEQUENCE entity_note_id_seq RENAME TO app_note_id_seq;

        UPDATE request_map SET url = '/appNote/show', feature_name = 'Show app note' WHERE transaction_code = 'APP-245';
        UPDATE request_map SET url = '/appNote/list', feature_name = 'List app note' WHERE transaction_code = 'APP-247';
        UPDATE request_map SET url = '/appNote/update', feature_name = 'Update app note' WHERE transaction_code = 'APP-248';
        UPDATE request_map SET url = '/appNote/create', feature_name = 'Create app note' WHERE transaction_code = 'APP-249';
        UPDATE request_map SET url = '/appNote/delete', feature_name = 'Delete app note' WHERE transaction_code = 'APP-250';
        UPDATE request_map SET url = '/appNote/viewEntityNote', feature_name = 'View all app notes' WHERE transaction_code = 'APP-261';
        UPDATE request_map SET url = '/appNote/listEntityNote', feature_name = 'List all app notes for list view' WHERE transaction_code = 'APP-345';
    """

    String updateThemeSql = """
        UPDATE theme SET value = value || '
        /* override blockquote font-size */
            blockquote {
            font-size: 13px;
        }

        .popover {
        font-size: 13px;
        }' WHERE key = 'app.cssBootstrapCustom';
    """

    String roleModuleMisSql = """
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 1, -12);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 1, -3);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 3, 50);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 3, 51);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 4, 56);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 4, 57);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 5, 62);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 5, 63);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 2, 68);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 2, 69);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 6, 74);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 6, 75);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 7, 80);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 7, 81);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 10, 86);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 1, 10, 87);

        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 1, 16);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 1, 18);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 3, 52);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 3, 53);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 4, 58);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 4, 59);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 5, 64);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 5, 65);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 2, 70);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 2, 71);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 6, 76);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 6, 77);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 7, 82);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 7, 83);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 10, 88);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 2, 10, 89);

        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 1, 25);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 1, 27);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 3, 54);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 3, 55);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 4, 60);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 4, 61);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 5, 66);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 5, 67);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 2, 72);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 2, 73);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 6, 78);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 6, 79);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 7, 84);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 7, 85);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 10, 90);
        INSERT INTO role_module (id, company_id, module_id, role_id)
        VALUES (NEXTVAL('role_module_id_seq'), 3, 10, 91);
    """

    String shellScriptSql = """
        INSERT INTO app_server_instance (id, version, name, ssh_user_name, ssh_password, ssh_host, ssh_port,
        os_vendor_id, is_tested, is_native, company_id, created_on, created_by, updated_on, updated_by)
        VALUES (NEXTVAL('app_server_instance_id_seq'), 0, 'Native Server Instance', 'root', '123', '127.0.0.1', 22,
        0, true, true, 1, now(), 12, null, 0);

        INSERT INTO app_server_instance (id, version, name, ssh_user_name, ssh_password, ssh_host, ssh_port,
        os_vendor_id, is_tested, is_native, company_id, created_on, created_by, updated_on, updated_by)
        VALUES (NEXTVAL('app_server_instance_id_seq'), 0, 'Native Server Instance', 'root', '123', '127.0.0.1', 22,
        0, true, true, 2, now(), 55, null, 0);

        INSERT INTO app_server_instance (id, version, name, ssh_user_name, ssh_password, ssh_host, ssh_port,
        os_vendor_id, is_tested, is_native, company_id, created_on, created_by, updated_on, updated_by)
        VALUES (NEXTVAL('app_server_instance_id_seq'), 0, 'Native Server Instance', 'root', '123', '127.0.0.1', 22,
        0, true, true, 3, now(), 58, null, 0);

        UPDATE app_server_instance SET os_vendor_id = (SELECT id FROM system_entity WHERE reserved_id = 10000171 AND company_id = app_server_instance.company_id);

        UPDATE app_shell_script SET server_instance_id = (SELECT id FROM app_server_instance WHERE company_id = app_shell_script.company_id);

        INSERT INTO app_shell_script (id, version, name, script, server_instance_id, script_type_id, company_id, plugin_id, created_on, created_by, updated_on,
        last_executed_on, updated_by, is_reserved)
        VALUES (NEXTVAL('app_shell_script_id_seq'), 0, 'Backup SQL', 'select 1', 16, (SELECT id FROM system_entity WHERE reserved_id = 10000182 AND company_id = 1),
        1, 1, now(), 12, null, null, 0, true);

        INSERT INTO app_shell_script (id, version, name, script, server_instance_id, script_type_id, company_id, plugin_id, created_on, created_by, updated_on,
        last_executed_on, updated_by, is_reserved)
        VALUES (NEXTVAL('app_shell_script_id_seq'), 0, 'Maintenance SQL', 'select 2', 16, (SELECT id FROM system_entity WHERE reserved_id = 10000182 AND company_id = 1),
        1, 1, now(), 12, null, null, 0, true);

        INSERT INTO app_shell_script (id, version, name, script, server_instance_id, script_type_id, company_id, plugin_id, created_on, created_by, updated_on,
        last_executed_on, updated_by, is_reserved)
        VALUES (NEXTVAL('app_shell_script_id_seq'), 0, 'Backup SQL', 'select 1', 17, (SELECT id FROM system_entity WHERE reserved_id = 10000182 AND company_id = 2),
        2, 1, now(), 55, null, null, 0, true);

        INSERT INTO app_shell_script (id, version, name, script, server_instance_id, script_type_id, company_id, plugin_id, created_on, created_by, updated_on,
        last_executed_on, updated_by, is_reserved)
        VALUES (NEXTVAL('app_shell_script_id_seq'), 0, 'Maintenance SQL', 'select 2', 17, (SELECT id FROM system_entity WHERE reserved_id = 10000182 AND company_id = 2),
        2, 1, now(), 55, null, null, 0, true);

        INSERT INTO app_shell_script (id, version, name, script, server_instance_id, script_type_id, company_id, plugin_id, created_on, created_by, updated_on,
        last_executed_on, updated_by, is_reserved)
        VALUES (NEXTVAL('app_shell_script_id_seq'), 0, 'Backup SQL', 'select 1', 18, (SELECT id FROM system_entity WHERE reserved_id = 10000182 AND company_id = 3),
        3, 1, now(), 58, null, null, 0, true);

        INSERT INTO app_shell_script (id, version, name, script, server_instance_id, script_type_id, company_id, plugin_id, created_on, created_by, updated_on,
        last_executed_on, updated_by, is_reserved)
        VALUES (NEXTVAL('app_shell_script_id_seq'), 0, 'Maintenance SQL', 'select 2', 18, (SELECT id FROM system_entity WHERE reserved_id = 10000182 AND company_id = 3),
        3, 1, now(), 58, null, null, 0, true);

        INSERT INTO app_schedule (id, version, name, schedule_type_id, repeat_interval, repeat_count, cron_expression,
        job_class_name, enable, action_name, plugin_id, company_id, updated_by, updated_on)
        VALUES (NEXTVAL('app_schedule_id_seq'), 0, 'Backup SQL Scheduler', 113235, 10000, 0, null, 'com.athena.mis.application.job.AppBackupSqlScriptJob',
        false, 'AppBackupSqlScriptJobActionService', 1, 1, 0, null);

        INSERT INTO app_schedule (id, version, name, schedule_type_id, repeat_interval, repeat_count, cron_expression,
        job_class_name, enable, action_name, plugin_id, company_id, updated_by, updated_on)
        VALUES (NEXTVAL('app_schedule_id_seq'), 0, 'Maintenance SQL Scheduler', 113235, 10000, 0, null, 'com.athena.mis.application.job.AppMaintenanceSqlScriptJob',
        false, 'AppMaintenanceSqlScriptJobActionService', 1, 1, 0, null);

        INSERT INTO app_schedule (id, version, name, schedule_type_id, repeat_interval, repeat_count, cron_expression,
        job_class_name, enable, action_name, plugin_id, company_id, updated_by, updated_on)
        VALUES (NEXTVAL('app_schedule_id_seq'), 0, 'Backup SQL Scheduler', 213237, 10000, 0, null, 'com.athena.mis.application.job.AppBackupSqlScriptJob',
        false, 'AppBackupSqlScriptJobActionService', 1, 2, 0, null);

        INSERT INTO app_schedule (id, version, name, schedule_type_id, repeat_interval, repeat_count, cron_expression,
        job_class_name, enable, action_name, plugin_id, company_id, updated_by, updated_on)
        VALUES (NEXTVAL('app_schedule_id_seq'), 0, 'Maintenance SQL Scheduler', 213237, 10000, 0, null, 'com.athena.mis.application.job.AppMaintenanceSqlScriptJob',
        false, 'AppMaintenanceSqlScriptJobActionService', 1, 2, 0, null);

        INSERT INTO app_schedule (id, version, name, schedule_type_id, repeat_interval, repeat_count, cron_expression,
        job_class_name, enable, action_name, plugin_id, company_id, updated_by, updated_on)
        VALUES (NEXTVAL('app_schedule_id_seq'), 0, 'Backup SQL Scheduler', 313239, 10000, 0, null, 'com.athena.mis.application.job.AppBackupSqlScriptJob',
        false, 'AppBackupSqlScriptJobActionService', 1, 3, 0, null);

        INSERT INTO app_schedule (id, version, name, schedule_type_id, repeat_interval, repeat_count, cron_expression,
        job_class_name, enable, action_name, plugin_id, company_id, updated_by, updated_on)
        VALUES (NEXTVAL('app_schedule_id_seq'), 0, 'Maintenance SQL Scheduler', 313239, 10000, 0, null, 'com.athena.mis.application.job.AppMaintenanceSqlScriptJob',
        false, 'AppMaintenanceSqlScriptJobActionService', 1, 3, 0, null);
    """
}
