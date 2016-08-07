package com.athena.mis.application.deployment

import com.athena.mis.AppSql
import com.athena.mis.AppUpdatePatch
import com.athena.mis.application.model.*
import com.athena.mis.utility.DateUtility

/**
 * Script version: 4
 * Start Date: 15-Mar-15
 * Release Dates By Project:
 * MIS: 16-April-15
 * EXH: n/a
 */
class AppUpdatePatch4Service extends AppUpdatePatch {

    private static final String RELEASE_DATE = "31/03/2015" // DD/MM/YYYY

    public Date getReleaseDate() {
        return DateUtility.parseMaskedDate(RELEASE_DATE)
    }

    public void init() {
        if (lstAppSql.size() > 0) return
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: dropViewSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: alterTableSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListAppUserActionServiceModel.SQL_LIST_APP_USER_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListCompanyActionServiceModel.SQL_LIST_COMPANY_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListCompanyForResellerActionServiceModel.SQL_LIST_COMPANY_FOR_RESELLER_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListRoleActionServiceModel.SQL_LIST_ROLE_MODEL)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListItemCategoryInventoryActionServiceModel.SQL_LIST_ITEM_CATEGORY_INVENTORY_MODEL)

        // for MIS
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: deleteTestPurposeUser)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: deleteReservedRoleSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: updateSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: requestMapSql)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 1L, query: roleFeatureMappingSql)
    }

    String dropViewSql = """
        DROP VIEW IF EXISTS list_app_user_action_service_model;
        DROP VIEW IF EXISTS list_company_action_service_model;
        DROP VIEW IF EXISTS list_company_for_reseller_action_service_model;
        DROP VIEW IF EXISTS list_role_action_service_model;
        DROP VIEW IF EXISTS list_item_category_inventory_action_service_model;
    """

    String alterTableSql = """
        ALTER TABLE item DROP COLUMN IF EXISTS is_dirty;
        ALTER TABLE item ADD COLUMN is_dirty boolean;
        UPDATE item SET is_dirty = false;
        ALTER TABLE item ALTER COLUMN is_dirty SET NOT NULL;
    """

    String deleteTestPurposeUser = """
        DELETE FROM user_role WHERE user_id IN (61,14,4,5,9);
        DELETE FROM app_user_entity where app_user_id IN (61,4,5);
        DELETE FROM app_user WHERE id IN (61,14,6,4,5,8,9);
    """

    String requestMapSql = """
        INSERT INTO request_map(id, version, url, config_attribute, feature_name, plugin_id, transaction_code, is_viewable, is_common)
        VALUES (NEXTVAL('request_map_id_seq'), 0, '/entityNote/listEntityNote', 'ROLE_-2,ROLE_-3_1,ROLE_-16_2,ROLE_-16_1,ROLE_-17_2,ROLE_-17_1,ROLE_-18_2,ROLE_-18_1',
        'List entity note for list view', 1, 'APP-345', FALSE, FALSE);
    """

    String roleFeatureMappingSql = """
        INSERT INTO role_feature_mapping (role_type_id, transaction_code, plugin_id) VALUES(-3, 'APP-345', 1);
    """

    String deleteReservedRoleSql = """
        UPDATE role SET role_type_id = 0 WHERE role_type_id IN (-4,-5,-6,-7,-8,-10,-11,-16,-17,-18);
        DELETE FROM role_type WHERE id IN (-4,-5,-6,-7,-8,-10,-11,-16,-17,-18);
    """

    String updateSql = """
        UPDATE request_map SET config_attribute = config_attribute || ',ROLE_-3_2,ROLE_-3_3,ROLE_-16_2,ROLE_-16_1'
        WHERE transaction_code = 'APP-328';
    """
}
