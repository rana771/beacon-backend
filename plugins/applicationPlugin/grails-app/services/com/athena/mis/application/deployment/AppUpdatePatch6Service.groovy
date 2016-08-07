package com.athena.mis.application.deployment

import com.athena.mis.AppSql
import com.athena.mis.AppUpdatePatch
import com.athena.mis.application.model.ListContentCategoryActionServiceModel
import com.athena.mis.utility.DateUtility

class AppUpdatePatch6Service extends AppUpdatePatch {

    private static final String RELEASE_DATE = "31/05/2015" // DD/MM/YYYY

    public Date getReleaseDate() {
        return DateUtility.parseMaskedDate(RELEASE_DATE)
    }

    void init() {
        if (lstAppSql.size() > 0) return

        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: idSeqForTestData)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: dropViewSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListContentCategoryActionServiceModel.SQL_LIST_CONTENT_CATEGORY_MODEL)

        // MIS
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: requestMapMisSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: roleFeatureMappingMisSql)

        //Exh Sfsl
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: sysConfigSfsl)

        //Exh Sfsa
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: sysConfigSfsa)

        //Exh Secl
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: sysConfigSecl)
    }

    String dropViewSql = """
        DROP VIEW IF EXISTS list_content_category_action_service_model;
    """

    String idSeqForTestData = """
        DROP SEQUENCE IF EXISTS test_data_id_seq;
        CREATE SEQUENCE test_data_id_seq
        INCREMENT  -1
        MINVALUE -9223372036854775807
        MAXVALUE -1
        START -1;
    """

    String sysConfigSfsl = """
        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(nextVal('sys_configuration_id_seq'), 100, 'If value = 1 then Application runs in Production mode and mail and sms is sendable, if value = 2 then Application runs in Development mode and mail and sms is not sendable. Default value is 1. If the value is not 1 or 2 then it will considered as 1', 'mis.application.deploymentMode', 1, 0, '1', 0, null);

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(nextVal('sys_configuration_id_seq'), 100, 'Determines phone number pattern for user', 'mis.application.phonePattern', 1, 0, '\\d{11}', 0, null);
    """

    String sysConfigSfsa = """
        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(nextVal('sys_configuration_id_seq'), 101, 'If value = 1 then Application runs in Production mode and mail and sms is sendable, if value = 2 then Application runs in Development mode and mail and sms is not sendable. Default value is 1. If the value is not 1 or 2 then it will considered as 1', 'mis.application.deploymentMode', 1, 0, '1', 0, null);

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(nextVal('sys_configuration_id_seq'), 101, 'Determines phone number pattern for user', 'mis.application.phonePattern', 1, 0, '\\d{11}', 0, null);
    """

    String sysConfigSecl = """
        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(nextVal('sys_configuration_id_seq'), 102, 'If value = 1 then Application runs in Production mode and mail and sms is sendable, if value = 2 then Application runs in Development mode and mail and sms is not sendable. Default value is 1. If the value is not 1 or 2 then it will considered as 1', 'mis.application.deploymentMode', 1, 0, '1', 0, null);

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(nextVal('sys_configuration_id_seq'), 102, 'Determines phone number pattern for user', 'mis.application.phonePattern', 1, 0, '\\d{11}', 0, null);
    """

    String requestMapMisSql = """
        UPDATE request_map SET is_common = false WHERE transaction_code IN ('APP-367','APP-370','APP-376','APP-380');

        UPDATE request_map SET
        config_attribute = config_attribute || ',ROLE_-20_1,ROLE_-20_2,ROLE_-20_3,ROLE_-26_1,ROLE_-26_2,ROLE_-26_3,ROLE_-24_1,ROLE_-24_2,ROLE_-24_3,ROLE_-22_1,ROLE_-22_2,ROLE_-22_3,ROLE_-32_1,ROLE_-32_2,ROLE_-32_3'
        WHERE transaction_code = 'APP-385';

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/testData/list',
        'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3,ROLE_-20_1,ROLE_-20_2,ROLE_-20_3,ROLE_-26_1,ROLE_-26_2,ROLE_-26_3,ROLE_-24_1,ROLE_-24_2,ROLE_-24_3,ROLE_-22_1,ROLE_-22_2,ROLE_-22_3,ROLE_-32_1,ROLE_-32_2,ROLE_-32_3',
        'List Test Data', 1, 'APP-386', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/testData/show', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Show Test Data', 1, 'APP-387', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/testData/create', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Create Test Data', 1, 'APP-388', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/testData/delete', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Delete Test Data', 1, 'APP-389', FALSE, FALSE);
    """

    String roleFeatureMappingMisSql = """
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-20, 'APP-385', 3);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-26, 'APP-385', 2);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-24, 'APP-385', 4);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-22, 'APP-385', 5);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-32, 'APP-385', 10);

        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-20, 'APP-386', 3);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-26, 'APP-386', 2);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-24, 'APP-386', 4);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-22, 'APP-386', 5);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-32, 'APP-386', 10);

        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-387', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-388', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-389', 1);
    """
}
