package com.athena.mis.application.deployment

import com.athena.mis.AppSql
import com.athena.mis.AppUpdatePatch
import com.athena.mis.application.model.ListRoleActionServiceModel
import com.athena.mis.application.model.ListSupplierItemActionServiceModel
import com.athena.mis.application.model.ListUserRoleForCompanyUserActionServiceModel
import com.athena.mis.utility.DateUtility

/**
 * Script version: 1
 * Start Date: 22-Feb-15
 * Release Dates By Project:
 * MIS: 14-Mar-15
 * EXH: n/a
 */
class AppUpdatePatch3Service extends AppUpdatePatch {

    private static final String RELEASE_DATE = "31/02/2015" // DD/MM/YYYY

    public Date getReleaseDate() {
        return DateUtility.parseMaskedDate(RELEASE_DATE)
    }

    public void init() {
        if (lstAppSql.size() > 0) return

        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: deleteRequestMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: deleteRoleFeatureMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: insertRoleFeatureMappingSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: insertRequestMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: updateRequestMapUrlSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: dropViewSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListSupplierItemActionServiceModel.SQL_LIST_SUPPLIER_ITEM_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListUserRoleForCompanyUserActionServiceModel.SQL_USER_ROLE_FOR_COMPANY_USER_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListRoleActionServiceModel.SQL_LIST_ROLE_MODEL)
    }

    String deleteRequestMapSql = """
        DELETE FROM request_map WHERE transaction_code IN('APP-76','APP-218','APP-123','APP-40','APP-106','APP-101','APP-190','APP-281','APP-265','APP-296','APP-323','APP-289','APP-145','APP-231');
    """

    String deleteRoleFeatureMapSql = """
        DELETE FROM role_feature_mapping WHERE transaction_code IN('APP-76','APP-218','APP-123','APP-40','APP-106','APP-101','APP-190','APP-281','APP-265','APP-296','APP-323','APP-289','APP-145','APP-231');

        DELETE FROM role_feature_mapping WHERE transaction_code IN ('APP-56','APP-57','APP-58','APP-59','APP-60','APP-61') AND role_type_id IN (-4,-5);

        DELETE FROM role_feature_mapping WHERE transaction_code IN ('APP-144','APP-146','APP-147','APP-148','APP-149','APP-152');

        DELETE FROM role_feature_mapping WHERE transaction_code IN ('ACC-157','ACC-141','ACC-142','ACC-143','ACC-121',
        'ACC-122','ACC-124','ACC-125','ACC-126','ACC-127','ACC-151','ACC-131''ACC-132','ACC-133','ACC-134','ACC-135',
        'ACC-136','BUDG-38','BUDG-29','PROC-78','PROC-31','PROC-32','PROC-33','PROC-34','PROC-35','PROC-36','PROC-37',
        'PROC-38','PROC-39','PROC-40','PROC-41','PROC-42','PROC-50','PROC-51','PROC-52','INV-150','INV-144','INV-149',
        'FA-47','QS-41') AND role_type_id IN (-3);

        DELETE FROM role_feature_mapping WHERE transaction_code IN ('ACC-157','INV-150') AND role_type_id IN (-12);
    """

    String insertRoleFeatureMappingSql = """
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES (-3, 'APP-144', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES (-3, 'APP-146', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES (-3, 'APP-147', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES (-3, 'APP-148', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES (-3, 'APP-149', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES (-3, 'APP-152', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES (-3, 'APP-339', 1);
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES (-25, 'APP-339', 1);
    """

    String insertRequestMapSql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/userRole/showForCompanyUser', 'ROLE_-2,ROLE_RESELLER', 'Show user role mapping for company user', 1, 'APP-340', TRUE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/userRole/listForCompanyUser', 'ROLE_-2,ROLE_RESELLER', 'List user role mapping for company user', 1, 'APP-341', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/userRole/createForCompanyUser', 'ROLE_-2,ROLE_RESELLER', 'Create user role mapping for company user', 1, 'APP-342', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/userRole/updateForCompanyUser', 'ROLE_-2,ROLE_RESELLER', 'Update user role mapping for company user', 1, 'APP-343', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/userRole/dropDownRoleForCompanyUserReload', 'ROLE_-2,ROLE_RESELLER', 'Reload role drop down for company user', 1, 'APP-344', FALSE, FALSE);

        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/item/dropDownItemReload', 'ROLE_-2,ROLE_-3_1,ROLE_-3_2,ROLE_-3_3,ROLE_-25_1,ROLE_-25_2,ROLE_-25_3', 'Reload Item Drop Down', 1, 'APP-339', FALSE, FALSE);
    """

    String updateRequestMapUrlSql = """
        UPDATE request_map SET url = '/contentCategory/dropDownContentCategoryReload', feature_name = 'Reload Content Category Drop Down' WHERE transaction_code = 'APP-153';
        UPDATE request_map SET url = '/supplierItem/dropDownSupplierItemReload', feature_name = 'Reload Supplier Item Drop Down' WHERE transaction_code = 'APP-235';
        UPDATE request_map SET config_attribute = config_attribute || ',ROLE_RESELLER' WHERE transaction_code = 'APP-36';
    """

    String dropViewSql = """
        DROP VIEW IF EXISTS list_role_action_service_model;
        DROP VIEW IF EXISTS list_supplier_item_action_service_model;
        DROP VIEW IF EXISTS list_entity_content_action_service_model;
        DROP VIEW IF EXISTS list_user_role_for_company_user_action_service_model;
    """
}
