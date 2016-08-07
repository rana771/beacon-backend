package com.athena.mis.application.deployment

import com.athena.mis.AppSql
import com.athena.mis.AppUpdatePatch
import com.athena.mis.application.model.ListAppServerInstanceActionServiceModel
import com.athena.mis.utility.DateUtility

class AppUpdatePatch9Service extends AppUpdatePatch {

    private static final String RELEASE_DATE = "12/07/2015" // DD/MM/YYYY

    public Date getReleaseDate() {
        return DateUtility.parseMaskedDate(RELEASE_DATE)
    }

    void init() {
        if (lstAppSql.size() > 0) return

        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: updateViewSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: alterTableSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertRequestMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertRoleFeatureMappingSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppServerInstanceActionServiceModel.SQL_LIST_APP_SERVER_INSTANCE_MODEL)
    }

    String updateViewSql = """
        DROP VIEW IF EXISTS list_app_server_instance_action_service_model;
    """

    String alterTableSql = """
        ALTER TABLE app_db_instance ADD COLUMN is_read_only boolean;
        UPDATE app_db_instance SET is_read_only = false;
        ALTER TABLE app_db_instance ALTER COLUMN is_read_only SET NOT NULL;
"""

    String insertRequestMapSql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appServerInstance/testServerConnection',
        'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3', 'Test Server Connection', 1, 'APP-394', FALSE, FALSE);
    """

    String insertRoleFeatureMappingSql = """
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-394', 1);
    """
}
