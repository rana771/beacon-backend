package com.athena.mis.application.deployment

import com.athena.mis.AppSql
import com.athena.mis.AppUpdatePatch
import com.athena.mis.utility.DateUtility

class AppUpdatePatch7Service extends AppUpdatePatch {

    private static final String RELEASE_DATE = "31/05/2015" // DD/MM/YYYY

    public Date getReleaseDate() {
        return DateUtility.parseMaskedDate(RELEASE_DATE)
    }

    void init() {
        if (lstAppSql.size() > 0) return

        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: alterTableSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: roleFeatureMappingSql)

        // MIS
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: requestMapMisSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: sysConfigMisSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: updateAppVersionMisSql)

        //SFSL
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: updateAppVersionSqlSfsl)

        //SFSA
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: updateAppVersionSqlSfsa)

        //SECL
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: updateAppVersionSqlSecl)
    }

    String requestMapMisSql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appVersion/show', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Show Release History', 1, 'APP-390', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appVersion/list', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'List Release History', 1, 'APP-391', FALSE, FALSE);
    """

    String roleFeatureMappingSql = """
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-390', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-391', 1);
    """

    String alterTableSql = """
        ALTER TABLE app_shell_script DROP COLUMN IF EXISTS server_instance_id;
        ALTER TABLE app_shell_script ADD COLUMN server_instance_id bigint;
        UPDATE app_shell_script SET server_instance_id = 0;
        ALTER TABLE app_shell_script ALTER COLUMN server_instance_id SET NOT NULL;
    """

    String sysConfigMisSql = """
        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(NEXTVAL('sys_configuration_id_seq'), 1, 'enforce release version if true',
        'mis.application.enforceReleaseVersion', 1, 0, 'true', 0,
        '<span style="color: green">
            New updates are available.<br>
        </span>
        <span style="color: red">
            Please update your application to avoid unexpected security threat.
        </span>');

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(NEXTVAL('sys_configuration_id_seq'), 2, 'enforce release version if true',
        'mis.application.enforceReleaseVersion', 1, 0, 'true', 0,
        '<span style="color: green">
            New updates are available.<br>
        </span>
        <span style="color: red">
            Please update your application to avoid unexpected security threat.
        </span>');

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(NEXTVAL('sys_configuration_id_seq'), 3, 'enforce release version if true',
        'mis.application.enforceReleaseVersion', 1, 0, 'true', 0,
        '<span style="color: green">
            New updates are available.<br>
        </span>
        <span style="color: red">
            Please update your application to avoid unexpected security threat.
        </span>');
    """

    String updateAppVersionMisSql = """
        DELETE FROM app_version WHERE company_id <> 1;
        ALTER TABLE app_version DROP COLUMN company_id;
        UPDATE app_version SET is_current = false WHERE release_no = 5 and plugin_id = 1;
    """

    String updateAppVersionSqlSfsl = """
        DELETE FROM app_version WHERE company_id <> 100;
        ALTER TABLE app_version DROP COLUMN company_id;
    """

    String updateAppVersionSqlSfsa = """
        DELETE FROM app_version WHERE company_id <> 101;
        ALTER TABLE app_version DROP COLUMN company_id;
    """

    String updateAppVersionSqlSecl = """
        DELETE FROM app_version WHERE company_id <> 102;
        ALTER TABLE app_version DROP COLUMN company_id;
    """
}
