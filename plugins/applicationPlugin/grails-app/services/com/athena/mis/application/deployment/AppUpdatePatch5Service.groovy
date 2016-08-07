package com.athena.mis.application.deployment

import com.athena.mis.AppSql
import com.athena.mis.AppUpdatePatch
import com.athena.mis.application.model.*
import com.athena.mis.utility.DateUtility

/**
 * Script version: 5
 * Start Date: 19-April-15
 * Release Dates By Project:
 * MIS:
 * EXH:
 */
class AppUpdatePatch5Service extends AppUpdatePatch {

    private static final String RELEASE_DATE = "31/04/2015" // DD/MM/YYYY

    public Date getReleaseDate() {
        return DateUtility.parseMaskedDate(RELEASE_DATE)
    }

    public void init() {
        if (lstAppSql.size() > 0) return

        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: deleteRequestMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: deleteRoleFeatureMappingSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: requestMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: reqMapAppUserRegistration)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: alterTableSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: dropAppDbInstanceColumn)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: insertRoleFeatureMappingSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: dropListRoleModel)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: updateSmsSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: dropListSupplierModel)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListSupplierActionServiceModel.SQL_LIST_SUPPLIER_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListForSendAppMailActionServiceModel.SQL_LIST_SEND_MAIL_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListForComposeSmsActionServiceModel.SQL_LIST_COMPOSE_SMS_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListForSendSmsActionServiceModel.SQL_LIST_SEND_SMS_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListRoleActionServiceModel.SQL_LIST_ROLE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppServerInstanceActionServiceModel.SQL_LIST_APP_SERVER_INSTANCE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppServerDbInstanceMappingActionServiceModel.SQL_LIST_APP_SERVER_DB_INSTANCE_MAPPING_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: SchemaInformationModel.SQL_SCHEMA_INFO_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: TestDataModel.SQL_TEST_DATA_MODEL)

        // for MIS
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: appUserRegistrationSysConfigMis)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: requestMapMisSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: newSysConfigMisSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: themeMisSql)

        //for exh sfsl
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: appUserRegistrationSysConfigSfsl)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: appUserRegistrationActionMailSfsl)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: reqMapForSchemaInfoSfsl)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: reqMapForTestDataSfsl)

        //for exh sfsa
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: appUserRegistrationSysConfigSfsa)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: appUserRegistrationActionMailSfsa)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: reqMapForSchemaInfoSfsa)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: reqMapForTestDataSfsa)

        //for exh secl
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: appUserRegistrationSysConfigSecl)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: appUserRegistrationActionMailSecl)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: reqMapForSchemaInfoSecl)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: reqMapForTestDataSecl)
    }

    String deleteRequestMapSql = """
        DELETE FROM request_map WHERE transaction_code = 'APP-127';
    """

    String deleteRoleFeatureMappingSql = """
        DELETE FROM role_feature_mapping WHERE transaction_code = 'APP-127';
    """

    String alterTableSql = """
        ALTER TABLE app_mail DROP COLUMN IF EXISTS has_send;
        ALTER TABLE app_sms DROP COLUMN IF EXISTS has_send;
        ALTER TABLE app_mail ADD COLUMN has_send boolean;
        UPDATE app_mail SET has_send = false;
        ALTER TABLE app_mail ALTER COLUMN has_send SET NOT NULL;

        ALTER TABLE app_sms ADD COLUMN has_send boolean;
        UPDATE app_sms SET has_send = false;
        ALTER TABLE app_sms ALTER COLUMN has_send SET NOT NULL;

        DROP TABLE reserved_role;
        ALTER TABLE role_type rename to reserved_role;

        ALTER TABLE reserved_role DROP CONSTRAINT role_type_pkey;
        ALTER TABLE reserved_role ADD CONSTRAINT reserved_role_pkey PRIMARY KEY(id );
    """

    String requestMapSql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMyFavourite/create', 'IS_AUTHENTICATED_ANONYMOUSLY',
        'Add page to my favourite', 1, 'APP-346', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMyFavourite/list', 'IS_AUTHENTICATED_ANONYMOUSLY',
        'List of my favourite pages', 1, 'APP-347', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMyFavourite/delete', 'IS_AUTHENTICATED_ANONYMOUSLY',
        'Delete my favourite page', 1, 'APP-348', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMyFavourite/setAsDefault', 'IS_AUTHENTICATED_ANONYMOUSLY',
        'Set my favourite page as default', 1, 'APP-349', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMyFavourite/select', 'IS_AUTHENTICATED_ANONYMOUSLY',
        'Select My Favourite', 1, 'APP-353', FALSE, FALSE);
    """

    String requestMapMisSql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appServerInstance/show', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Show Server Instance', 1, 'APP-354', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appServerInstance/list', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'List Server Instance', 1, 'APP-355', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appServerInstance/create', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Create Server Instance', 1, 'APP-356', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appServerInstance/update', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Update Server Instance', 1, 'APP-357', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appServerInstance/delete', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Delete Server Instance', 1, 'APP-358', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appServerDbInstanceMapping/show', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Show Server Instance Mapping', 1, 'APP-359', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appServerDbInstanceMapping/list', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'List Server Instance Mapping', 1, 'APP-360', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appServerDbInstanceMapping/create', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Create Server Instance Mapping', 1, 'APP-361', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appServerDbInstanceMapping/update', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Update Server Instance Mapping', 1, 'APP-362', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appServerDbInstanceMapping/delete', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Delete Server Instance Mapping', 1, 'APP-363', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/create', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'Create App Mail', 1, 'APP-364', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/delete', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'Delete App Mail', 1, 'APP-365', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/send', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'Send App Mail', 1, 'APP-366', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/showForCompose', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'Show App Mail for Compose', 1, 'APP-367', TRUE, TRUE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/listForCompose', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'List App Mail for Compose', 1, 'APP-368', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/updateForCompose', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'Update App Mail for Compose', 1, 'APP-369', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/showForSend', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'Show Sent App Mail', 1, 'APP-370', TRUE, TRUE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/listForSend', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'List Sent App Mail', 1, 'APP-371', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appMail/reCompose', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'Re-Compose App Mail', 1, 'APP-372', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appSms/create', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'Create sms', 1, 'APP-374', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appSms/delete', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'Delete sms', 1, 'APP-375', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appSms/showForCompose', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'Show sms for compose', 1, 'APP-376', TRUE, TRUE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appSms/listForCompose', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'List compose sms', 1, 'APP-377', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appSms/updateForCompose', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'Update compose sms', 1, 'APP-378', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appSms/sendForCompose', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'Send compose sms', 1, 'APP-379', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appSms/showForSend', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'Show sent sms', 1, 'APP-380', TRUE, TRUE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appSms/listForSend', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'List sent sms', 1, 'APP-381', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appSms/reCompose', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3',
        'Re-Compose sms', 1, 'APP-382', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appDbInstance/dropDownTableColumnReload', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Reload Table Column Drop down', 1, 'APP-383', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appDbInstance/dropDownTableReload', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'Reload Table Drop down', 1, 'APP-384', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/SchemaInformation/listSchemaInfo', 'ROLE_-2,ROLE_-12_1,ROLE_-12_2,ROLE_-12_3',
        'List Schema Information', 1, 'APP-385', FALSE, FALSE);

        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-25, 'APP-144', 2);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-25, 'APP-146', 2);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-25, 'APP-147', 2);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-25, 'APP-148', 2);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-25, 'APP-149', 2);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-25, 'APP-152', 2);

        DELETE FROM role_feature_mapping WHERE role_type_id IN (-4,-5,-6,-7,-8,8,-10,-11,-16,-17,-18,-91);
        DELETE FROM request_map WHERE id = -999999;
    """

    String insertRoleFeatureMappingSql = """
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-354', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-355', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-356', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-357', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-358', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-359', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-360', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-361', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-362', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-363', 1);

        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-364', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-365', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-366', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-367', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-368', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-369', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-370', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-371', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-372', 1);

        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-374', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-375', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-376', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-377', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-378', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-379', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-380', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-381', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-382', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-383', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-384', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-385', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-12, 'APP-386', 1);
    """

    String reqMapAppUserRegistration = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appUser/showRegistration', 'IS_AUTHENTICATED_ANONYMOUSLY',
        'Show app user registration', 1, 'APP-350', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appUser/register', 'IS_AUTHENTICATED_ANONYMOUSLY',
        'Register app user', 1, 'APP-351', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/appUser/activate', 'IS_AUTHENTICATED_ANONYMOUSLY',
        'Activate app user account', 1, 'APP-352', FALSE, FALSE);
    """

    String appUserRegistrationSysConfigMis = """
        INSERT INTO sys_configuration("id", "company_id", "description", "key", "plugin_id", "updated_by", "value", "version", "message")
        VALUES(nextVal('sys_configuration_id_seq'), 1, 'Check if app user registration link will be enabled or not. true = Enabled; false = Disabled. If config not found then default value is false.',
        'mis.application.enableNewUserRegistration', 1, 0, 'false', 0, 'New User? Click Here');

        INSERT INTO sys_configuration("id", "company_id", "description", "key", "plugin_id", "updated_by", "value", "version", "message")
        VALUES(nextVal('sys_configuration_id_seq'), 1, 'Role id to map with app user at registration. 0 or incorrect value will not assign any role',
        'mis.application.roleIdForUserRegistration', 1, 0, '0', 0, 'No role found to map with app user');

        INSERT INTO sys_configuration("id", "company_id", "description", "key", "plugin_id", "updated_by", "value", "version", "message")
        VALUES(nextVal('sys_configuration_id_seq'), 2, 'Check if app user registration link will be enabled or not. true = Enabled; false = Disabled. If config not found then default value is false.',
        'mis.application.enableNewUserRegistration', 1, 0, 'false', 0, 'New User? Click Here');

        INSERT INTO sys_configuration("id", "company_id", "description", "key", "plugin_id", "updated_by", "value", "version", "message")
        VALUES(nextVal('sys_configuration_id_seq'), 2, 'Role id to map with app user at registration. 0 or incorrect value will not assign any role',
        'mis.application.roleIdForUserRegistration', 1, 0, '0', 0, 'No role found to map with app user');

        INSERT INTO sys_configuration("id", "company_id", "description", "key", "plugin_id", "updated_by", "value", "version", "message")
        VALUES(nextVal('sys_configuration_id_seq'), 3, 'Check if app user registration link will be enabled or not. true = Enabled; false = Disabled. If config not found then default value is false.',
        'mis.application.enableNewUserRegistration', 1, 0, 'false', 0, 'New User? Click Here');

        INSERT INTO sys_configuration("id", "company_id", "description", "key", "plugin_id", "updated_by", "value", "version", "message")
        VALUES(nextVal('sys_configuration_id_seq'), 3, 'Role id to map with app user at registration. 0 or incorrect value will not assign any role',
        'mis.application.roleIdForUserRegistration', 1, 0, '0', 0, 'No role found to map with app user');
    """

    String appUserRegistrationSysConfigSfsl = """
        INSERT INTO sys_configuration("id", "company_id", "description", "key", "plugin_id", "updated_by", "value", "version", "message")
        VALUES(nextVal('sys_configuration_id_seq'), 100, 'Check if app user registration link will be enabled or not. true = Enabled; false = Disabled. If config not found then default value is false.',
        'mis.application.enableNewUserRegistration', 1, 0, 'true', 0, 'New User? Click Here');

        INSERT INTO sys_configuration("id", "company_id", "description", "key", "plugin_id", "updated_by", "value", "version", "message")
        VALUES(nextVal('sys_configuration_id_seq'), 100, 'Role id to map with app user at registration. 0 or incorrect value will not assign any role',
        'mis.application.roleIdForUserRegistration', 1, 0, '-202', 0, 'No role found to map with app user');
    """

    String appUserRegistrationSysConfigSfsa = """
        INSERT INTO sys_configuration("id", "company_id", "description", "key", "plugin_id", "updated_by", "value", "version", "message")
        VALUES(nextVal('sys_configuration_id_seq'), 101, 'Check if app user registration link will be enabled or not. true = Enabled; false = Disabled. If config not found then default value is false.',
        'mis.application.enableNewUserRegistration', 1, 0, 'true', 0, 'New User? Click Here');

        INSERT INTO sys_configuration("id", "company_id", "description", "key", "plugin_id", "updated_by", "value", "version", "message")
        VALUES(nextVal('sys_configuration_id_seq'), 101, 'Role id to map with app user at registration. 0 or incorrect value will not assign any role',
        'mis.application.roleIdForUserRegistration', 1, 0, '-202', 0, 'No role found to map with app user');
    """

    String appUserRegistrationSysConfigSecl = """
        INSERT INTO sys_configuration("id", "company_id", "description", "key", "plugin_id", "updated_by", "value", "version", "message")
        VALUES(nextVal('sys_configuration_id_seq'), 102, 'Check if app user registration link will be enabled or not. true = Enabled; false = Disabled. If config not found then default value is false.',
        'mis.application.enableNewUserRegistration', 1, 0, 'true', 0, 'New User? Click Here');

        INSERT INTO sys_configuration("id", "company_id", "description", "key", "plugin_id", "updated_by", "value", "version", "message")
        VALUES(nextVal('sys_configuration_id_seq'), 102, 'Role id to map with app user at registration. 0 or incorrect value will not assign any role',
        'mis.application.roleIdForUserRegistration', 1, 0, '-202', 0, 'No role found to map with app user');
    """

    String appUserRegistrationActionMailSfsl = """
        INSERT INTO app_mail ("id", "version", "company_id", "body", "is_active", "is_required_role_ids",
        "mime_type", "plugin_id", "subject", "transaction_code", "is_manual_send", "controller_name",
        "action_name", "is_required_recipients", "recipients", "updated_by", "has_send")
        VALUES (NEXTVAL('app_mail_id_seq'), 0, '100',
        '<div>
            <p>
                Dear Mr/Ms \${name}, <br/>
                Thank you for completing your registration. Please activate your account.<br/>
                To activate your user account please click the link below: <br/>
                <a target="_blank" href="\${link}">\${link}</a>
            </p>

            If you have already activated your account, please ignore this mail. <br/>
            <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
        </div>',
        't', 'FALSE', 'html', '1', 'Activate your user account', 'RegisterAppUserActionService','FALSE',
        null, null, 'f', null, 0, 'FALSE');
    """

    String appUserRegistrationActionMailSfsa = """
    INSERT INTO app_mail ("id", "version", "company_id", "body", "is_active", "is_required_role_ids",
        "mime_type", "plugin_id", "subject", "transaction_code", "is_manual_send", "controller_name",
        "action_name", "is_required_recipients", "recipients", "updated_by", "has_send")
        VALUES (NEXTVAL('app_mail_id_seq'), 0, '101',
        '<div>
            <p>
                Dear Mr/Ms \${name}, <br/>
                Thank you for completing your registration. Please activate your account.<br/>
                To activate your user account please click the link below: <br/>
                <a target="_blank" href="\${link}">\${link}</a>
            </p>

            If you have already activated your account, please ignore this mail. <br/>
            <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
        </div>',
        't', 'FALSE', 'html', '1', 'Activate your user account', 'RegisterAppUserActionService','FALSE',
        null, null, 'f', null, 0, 'FALSE');
    """

    String appUserRegistrationActionMailSecl = """
    INSERT INTO app_mail ("id", "version", "company_id", "body", "is_active", "is_required_role_ids",
        "mime_type", "plugin_id", "subject", "transaction_code", "is_manual_send", "controller_name",
        "action_name", "is_required_recipients", "recipients", "updated_by", "has_send")
        VALUES (NEXTVAL('app_mail_id_seq'), 0, '102',
        '<div>
            <p>
                Dear Mr/Ms \${name}, <br/>
                Thank you for completing your registration. Please activate your account.<br/>
                To activate your user account please click the link below: <br/>
                <a target="_blank" href="\${link}">\${link}</a>
            </p>

            If you have already activated your account, please ignore this mail. <br/>
            <i>Note: This is an auto-generated email, which does not need reply.<br/></i>
        </div>',
        't', 'FALSE', 'html', '1', 'Activate your user account', 'RegisterAppUserActionService','FALSE',
        null, null, 'f', null, 0, 'FALSE');
    """

    String dropAppDbInstanceColumn = """
        ALTER TABLE app_db_instance DROP COLUMN local_data_bucket;
        ALTER TABLE app_db_instance DROP COLUMN remote_data_bucket;
        ALTER TABLE app_db_instance DROP COLUMN cdc_location;
    """

    String dropListRoleModel = """
        DROP VIEW IF EXISTS list_role_action_service_model;
    """
    String dropListSupplierModel = """
        DROP VIEW IF EXISTS list_supplier_action_service_model;
    """
    String updateSmsSql = """
        UPDATE app_sms SET body = 'This is a TEST SMS' WHERE transaction_code = 'SendSmsActionService';
    """

    String newSysConfigMisSql = """
        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(nextVal('sys_configuration_id_seq'), 1, 'If value = 1 then Application runs in Production mode and mail and sms is sendable, if value = 2 then
        Application runs in Development mode and mail and sms is not sendable. Default value is 1. If the value is not 1 or 2 then it will
        considered as 1', 'mis.application.deploymentMode', 1, 0, '1', 0, null);

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(nextVal('sys_configuration_id_seq'), 2, 'If value = 1 then Application runs in Production mode and mail and sms is sendable, if value = 2 then
        Application runs in Development mode and mail and sms is not sendable. Default value is 1. If the value is not 1 or 2 then it will
        considered as 1', 'mis.application.deploymentMode', 1, 0, '1', 0, null);

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(nextVal('sys_configuration_id_seq'), 3, 'If value = 1 then Application runs in Production mode and mail and sms is sendable, if value = 2 then
        Application runs in Development mode and mail and sms is not sendable. Default value is 1. If the value is not 1 or 2 then it will
        considered as 1', 'mis.application.deploymentMode', 1, 0, '1', 0, null);

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(nextVal('sys_configuration_id_seq'), 1, 'Determines phone number pattern for user', 'mis.application.phonePattern', 1, 0, '\\d{11}', 0, null);

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(nextVal('sys_configuration_id_seq'), 2, 'Determines phone number pattern for user', 'mis.application.phonePattern', 1, 0, '\\d{11}', 0, null);

        INSERT INTO sys_configuration(id, company_id, description, key, plugin_id, updated_by, value, version, message)
        VALUES(nextVal('sys_configuration_id_seq'), 3, 'Determines phone number pattern for user', 'mis.application.phonePattern', 1, 0, '\\d{11}', 0, null);
    """

    String themeMisSql = """
        INSERT INTO theme(id, version, company_id, key, updated_by, updated_on, value, description)
        VALUES(NEXTVAL('theme_id_seq'), 0, 1, 'app.noAccessPage', 0, null, '<div class="jumbotron">
            <div class="container">
                <div class="row">
                    <div class="col-md-10">
                        <h1>Sorry, this page is not available.</h1>

                        <p>Lets try one of the following remedies:</p>
                        <ul>
                            <li>If you typed the page address in the address bar, make sure that it is spelled correctly.</li>

                            <li>Click the <a
                                href="javascript:history.back(1)">back</a> button to go back to the previous page.
                            </li>
                            <li>Click <a href="/">here</a>  to go directly to the Application home page.
                            </li>
                            <li>If all else fails, contact with system administrator.</li>
                        </ul>
                    </div>
                    <div class="col-md-2">
                        <span class="fa fa-exclamation-triangle" style="font-size: 15em;color: #428BCA"></span>
                    </div>
                </div>
            </div>
        </div>', 'Template for No Access page');

        INSERT INTO theme(id, version, company_id, key, updated_by, updated_on, value, description)
        VALUES(NEXTVAL('theme_id_seq'), 0, 2, 'app.noAccessPage', 0, null, '<div class="jumbotron">
            <div class="container">
                <div class="row">
                    <div class="col-md-10">
                        <h1>Sorry, this page is not available.</h1>

                        <p>Lets try one of the following remedies:</p>
                        <ul>
                            <li>If you typed the page address in the address bar, make sure that it is spelled correctly.</li>

                            <li>Click the <a
                                href="javascript:history.back(1)">back</a> button to go back to the previous page.
                            </li>
                            <li>Click <a href="/">here</a>  to go directly to the Application home page.
                            </li>
                            <li>If all else fails, contact with system administrator.</li>
                        </ul>
                    </div>
                    <div class="col-md-2">
                        <span class="fa fa-exclamation-triangle" style="font-size: 15em;color: #428BCA"></span>
                    </div>
                </div>
            </div>
        </div>', 'Template for No Access page');

        INSERT INTO theme(id, version, company_id, key, updated_by, updated_on, value, description)
        VALUES(NEXTVAL('theme_id_seq'), 0, 3, 'app.noAccessPage', 0, null, '<div class="jumbotron">
            <div class="container">
                <div class="row">
                    <div class="col-md-10">
                        <h1>Sorry, this page is not available.</h1>

                        <p>Lets try one of the following remedies:</p>
                        <ul>
                            <li>If you typed the page address in the address bar, make sure that it is spelled correctly.</li>

                            <li>Click the <a
                                href="javascript:history.back(1)">back</a> button to go back to the previous page.
                            </li>
                            <li>Click <a href="/">here</a>  to go directly to the Application home page.
                            </li>
                            <li>If all else fails, contact with system administrator.</li>
                        </ul>
                    </div>
                    <div class="col-md-2">
                        <span class="fa fa-exclamation-triangle" style="font-size: 15em;color: #428BCA"></span>
                    </div>
                </div>
            </div>
        </div>', 'Template for No Access page');
    """

    String reqMapForSchemaInfoSfsl = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/schemaInformation/listSchemaInfo', 'ROLE_-2,ROLE_-12_100,ROLE_-206_100',
        'List Schema Information', 1, 'APP-385', FALSE, FALSE);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-206, 'APP-385', 9);
    """

    String reqMapForSchemaInfoSfsa = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/schemaInformation/listSchemaInfo', 'ROLE_-2,ROLE_-12_101,ROLE_-206_101',
        'List Schema Information', 1, 'APP-385', FALSE, FALSE);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-206, 'APP-385', 9);
    """

    String reqMapForSchemaInfoSecl = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/schemaInformation/listSchemaInfo', 'ROLE_-2,ROLE_-12_102,ROLE_-206_102,ROLE_-302_102',
        'List Schema Information', 1, 'APP-385', FALSE, FALSE);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-206, 'APP-385', 9);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-302, 'APP-385', 11);
    """

    String reqMapForTestDataSfsl = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/testData/list', 'ROLE_-2,ROLE_-12_100,ROLE_-206_100',
        'List Test Data', 1, 'APP-386', FALSE, FALSE);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-206, 'APP-386', 9);
    """

    String reqMapForTestDataSfsa = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/testData/list', 'ROLE_-2,ROLE_-12_101,ROLE_-206_101',
        'List Test Data', 1, 'APP-386', FALSE, FALSE);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-206, 'APP-386', 9);
    """

    String reqMapForTestDataSecl = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/testData/list', 'ROLE_-2,ROLE_-12_102,ROLE_-206_102,ROLE_-302_102',
        'List Test Data', 1, 'APP-386', FALSE, FALSE);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-206, 'APP-386', 9);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-302, 'APP-386', 11);
    """
}
