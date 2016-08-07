package com.athena.mis.application.deployment

import com.athena.mis.AppSql
import com.athena.mis.AppUpdatePatch
import com.athena.mis.utility.DateUtility

class AppUpdatePatch8Service extends AppUpdatePatch {

    private static final String RELEASE_DATE = "18/06/2015" // DD/MM/YYYY

    public Date getReleaseDate() {
        return DateUtility.parseMaskedDate(RELEASE_DATE)
    }

    void init() {
        if (lstAppSql.size() > 0) return

        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: alterTableSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: deleteRequestMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertRequestMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertRoleFeatureMappingSql)
        // MIS
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: systemEntityOsVendorMisSql)
    }

    String alterTableSql = """
        ALTER TABLE app_db_instance ADD COLUMN is_tested boolean;
        UPDATE app_db_instance SET is_tested = false;
        ALTER TABLE app_db_instance ALTER COLUMN is_tested SET NOT NULL;

        ALTER TABLE app_db_instance ADD COLUMN reserved_vendor_id bigint;

        UPDATE app_db_instance SET reserved_vendor_id = 0;

        UPDATE app_db_instance SET reserved_vendor_id = 1143
        WHERE vendor_id = (SELECT id FROM system_entity WHERE reserved_id = 1143
        AND company_id = app_db_instance.company_id);

        UPDATE app_db_instance SET reserved_vendor_id = 1144
        WHERE vendor_id = (SELECT id FROM system_entity WHERE reserved_id = 1144
        AND company_id = app_db_instance.company_id);

        UPDATE app_db_instance SET reserved_vendor_id = 1145
        WHERE vendor_id = (SELECT id FROM system_entity WHERE reserved_id = 1145
        AND company_id = app_db_instance.company_id);

        UPDATE app_db_instance SET reserved_vendor_id = 1146
        WHERE vendor_id = (SELECT id FROM system_entity WHERE reserved_id = 1146
        AND company_id = app_db_instance.company_id);

        UPDATE app_db_instance SET reserved_vendor_id = 1147
        WHERE vendor_id = (SELECT id FROM system_entity WHERE reserved_id = 1147
        AND company_id = app_db_instance.company_id);

        ALTER TABLE app_db_instance ALTER COLUMN reserved_vendor_id SET NOT NULL;

        ALTER TABLE app_db_instance ADD COLUMN is_slave boolean;
        UPDATE app_db_instance SET is_slave = false;
    """

    String systemEntityOsVendorMisSql = """
        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Linux', 1741, 'Linux', 1, 10000171, 1, 0, 12, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Free BDS', 1741, 'Free BDS', 1, 10000172, 1, 0, 12, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Linux', 1741, 'Linux', 2, 10000171, 1, 0, 55, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Free BDS', 1741, 'Free BDS', 2, 10000172, 1, 0, 55, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Linux', 1741, 'Linux', 3, 10000171, 1, 0, 58, now(), 0);

        INSERT INTO system_entity (id, is_active, key, type, value, company_id, reserved_id, plugin_id, version, created_by, created_on, updated_by)
        VALUES( ('1' || lpad(nextval('system_entity_id_seq')::text,9,'0'))::bigint, true, 'Free BDS', 1741, 'Free BDS', 3, 10000172, 1, 0, 58, now(), 0);
    """

    String deleteRequestMapSql = """
        DELETE FROM request_map WHERE transaction_code = 'ACC-252';
        DELETE FROM role_feature_mapping WHERE transaction_code = 'ACC-252';
    """

    String insertRequestMapSql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appUserEntity/listUserByEntityTypeAndEntity',
        'ROLE_-2,ROLE_-16_1,ROLE_-16_2,ROLE_-17_1,ROLE_-17_2,ROLE_-18_1,ROLE_-18_2',
        'Get list of mapped entity', 1, 'APP-392', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appDbInstance/dropDownDbInstanceReload', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Reload DB Instance Drop down', 1, 'APP-393', FALSE, FALSE);
    """

    String insertRoleFeatureMappingSql = """
         INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-393', 1);
    """
}
