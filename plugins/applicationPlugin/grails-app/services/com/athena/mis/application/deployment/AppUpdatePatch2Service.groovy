package com.athena.mis.application.deployment

import com.athena.mis.AppSql
import com.athena.mis.AppUpdatePatch
import com.athena.mis.application.model.ListUserRoleForCompanyUserActionServiceModel
import com.athena.mis.utility.DateUtility

class AppUpdatePatch2Service extends AppUpdatePatch {

    private static final String RELEASE_DATE = "31/01/2015" // DD/MM/YYYY

    public Date getReleaseDate() {
        return DateUtility.parseMaskedDate(RELEASE_DATE)
    }

    public void init() {
        if (lstAppSql.size() > 0) return

        //common
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 0L, query: ListUserRoleForCompanyUserActionServiceModel.SQL_USER_ROLE_FOR_COMPANY_USER_MODEL)

        // For Exh - UK
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: sqlRemoveDuplicateBankBranchSfsl)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: sqlDropCreateConstraintAndIndexSfsl)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: sqlDeleteVersionConfig)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 100L, query: insertEnforceVersionConfigSFSL)

        // For Exh - AU
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: sqlRemoveDuplicateBankBranchSfsa)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: sqlDropCreateConstraintAndIndexSfsa)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: sqlDeleteVersionConfig)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 101L, query: insertEnforceVersionConfigSFSA)

        // For Exh - SA
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: sqlRemoveDuplicateBankBranchSecl)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: sqlDropCreateConstraintAndIndexSecl)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: sqlDeleteVersionConfig)
        lstAppSql << new AppSql(type: TYPE_SQL, companyId: 102L, query: insertEnforceVersionConfigSECL)
    }

    String sqlDropCreateConstraintAndIndexSfsl = """
    ALTER TABLE app_bank DROP CONSTRAINT bank_code_country_id_key;
    ALTER TABLE app_bank DROP CONSTRAINT bank_name_country_id_key;
    ALTER TABLE district DROP CONSTRAINT district_name_country_id_key;
    ALTER TABLE app_country DROP CONSTRAINT country_code_company_id_key;
    ALTER TABLE app_country DROP CONSTRAINT country_name_company_id_key;
    ALTER TABLE currency DROP CONSTRAINT currency_symbol_company_id_key;
    ALTER TABLE currency DROP CONSTRAINT currency_symbol_key_company_id;
    ALTER TABLE app_db_instance DROP CONSTRAINT app_db_instance_company_id_name_key;
    ALTER TABLE app_group_entity DROP CONSTRAINT
    app_group_entity_company_id_entity_type_id_entity_id_group__key;
    ALTER TABLE app_user DROP CONSTRAINT unique_login_id;
    ALTER TABLE app_user_entity DROP CONSTRAINT
    app_user_entity_company_id_entity_type_id_entity_id_app_use_key;
    ALTER TABLE company DROP CONSTRAINT company_email;
    ALTER TABLE company DROP CONSTRAINT company_web_url;
    ALTER TABLE db_instance_query DROP CONSTRAINT
    db_instance_query_db_instance_id_name_key;
    ALTER TABLE request_map DROP CONSTRAINT request_map_transaction_code_key;
    ALTER TABLE request_map DROP CONSTRAINT request_map_url_key;
    ALTER TABLE role DROP CONSTRAINT role_authority_key;
    ALTER TABLE sys_configuration DROP CONSTRAINT sys_configuration_key_company_id;
    ALTER TABLE theme DROP CONSTRAINT theme_key_company_id_key;
    DROP INDEX item_name_idx;


    CREATE UNIQUE INDEX app_bank_name_country_id_idx ON app_bank(lower(name),country_id);
    CREATE UNIQUE INDEX app_bank_code_country_id_idx ON app_bank(lower(code),country_id);

    CREATE UNIQUE INDEX app_bank_branch_name_bank_id_district_id_idx ON
    app_bank_branch(lower(name),bank_id, district_id);

    CREATE UNIQUE INDEX app_bank_branch_code_bank_id_district_id_idx ON
    app_bank_branch(lower(code),bank_id, district_id);

    CREATE UNIQUE INDEX district_name_country_id_idx ON
    district(lower(name), country_id);

    CREATE UNIQUE INDEX app_country_name_company_id_idx ON
    app_country(lower(name), company_id);

    CREATE UNIQUE INDEX app_country_code_company_id_idx ON
    app_country(lower(code), company_id);

    CREATE UNIQUE INDEX currency_name_company_id_idx ON
    currency(lower(name), company_id);

    CREATE UNIQUE INDEX currency_symbol_company_id_idx ON
    currency(lower(symbol), company_id);


    CREATE UNIQUE INDEX app_db_instance_name_company_id_idx ON app_db_instance(lower(name),company_id);
    CREATE UNIQUE INDEX app_group_name_company_id_idx ON app_group(lower(name),company_id);
    CREATE UNIQUE INDEX app_group_entity_group_id_entt_id_entt_type_id_company_id_idx ON
    app_group_entity(group_id, entity_id, entity_type_id, company_id);

    CREATE UNIQUE INDEX app_schedule_name_company_id_idx ON app_schedule(lower(name),company_id);
    CREATE UNIQUE INDEX app_shell_script_name_company_id_idx ON app_shell_script(lower(name),company_id);

    CREATE UNIQUE INDEX app_user_login_id_company_id_idx ON app_user(lower(login_id),company_id);

    CREATE UNIQUE INDEX app_user_entity_app_user_id_entt_id_entt_type_id_company_id_idx ON
    app_user_entity(app_user_id, entity_id, entity_type_id, company_id);

    CREATE UNIQUE INDEX benchmark_name_company_id_idx ON benchmark(lower(name),company_id);

    CREATE UNIQUE INDEX benchmark_star_name_company_id_idx ON benchmark_star(lower(name),company_id);

    CREATE UNIQUE INDEX company_name_idx ON company(lower(name));
    CREATE UNIQUE INDEX company_code_idx ON company(lower(code));
    CREATE UNIQUE INDEX company_web_url_idx ON company(lower(web_url));
    CREATE UNIQUE INDEX company_email_idx ON company(lower(email));

    CREATE UNIQUE INDEX content_category_name_content_type_id_idx ON
    content_category(lower(name), content_type_id);
    CREATE UNIQUE INDEX content_category_system_content_category_company_id_idx ON
    content_category(lower(system_content_category),company_id);

    CREATE UNIQUE INDEX db_instance_query_name_db_instance_id_idx ON
    db_instance_query(lower(name), db_instance_id);

    CREATE UNIQUE INDEX designation_name_company_id_idx ON designation(lower(name), company_id);
    CREATE UNIQUE INDEX designation_short_name_company_id_idx ON designation(lower(short_name), company_id);

    CREATE UNIQUE INDEX item_name_category_id_idx ON item(lower(name), category_id);

    CREATE UNIQUE INDEX item_type_name_company_id_idx ON item_type(lower(name), company_id);

    CREATE UNIQUE INDEX project_name_company_id_idx ON project(lower(name),company_id);
    CREATE UNIQUE INDEX project_code_company_id_idx ON project(lower(code),company_id);

    CREATE UNIQUE INDEX request_map_transaction_code_idx ON request_map(lower(transaction_code));
    CREATE UNIQUE INDEX request_map_url_idx ON request_map(lower(url));

    CREATE UNIQUE INDEX role_authority_idx ON role(lower(authority));
    CREATE UNIQUE INDEX role_name_company_id_idx ON role(lower(name), company_id);

    CREATE UNIQUE INDEX supplier_name_company_id_idx ON supplier(lower(name), company_id);

    CREATE UNIQUE INDEX sys_configuration_key_company_id_idx ON sys_configuration(lower(key), company_id);


    CREATE UNIQUE INDEX system_entity_key_type_reserved_id_company_id_idx
    ON system_entity(lower(key),type,reserved_id,company_id);

    CREATE UNIQUE INDEX theme_key_company_id_idx ON theme(lower(key),company_id);

    CREATE UNIQUE INDEX vehicle_name_company_id_idx ON
    vehicle(lower(name),company_id);
    """

    String sqlDropCreateConstraintAndIndexSfsa = """
    ALTER TABLE app_bank DROP CONSTRAINT bank_code_country_id_key;
    ALTER TABLE app_bank DROP CONSTRAINT bank_name_country_id_key;
    ALTER TABLE district DROP CONSTRAINT district_name_country_id_key;
    ALTER TABLE app_country DROP CONSTRAINT country_code_company_id_key;
    ALTER TABLE app_country DROP CONSTRAINT country_name_company_id_key;
    ALTER TABLE currency DROP CONSTRAINT currency_symbol_company_id_key;
    ALTER TABLE currency DROP CONSTRAINT currency_symbol_key_company_id;
    ALTER TABLE app_db_instance DROP CONSTRAINT app_db_instance_company_id_name_key;
    ALTER TABLE app_group_entity DROP CONSTRAINT
    app_group_entity_company_id_entity_type_id_entity_id_group__key;
    ALTER TABLE app_user DROP CONSTRAINT unique_login_id;
    ALTER TABLE app_user_entity DROP CONSTRAINT
    app_user_entity_company_id_entity_type_id_entity_id_app_use_key;
    ALTER TABLE company DROP CONSTRAINT company_email;
    ALTER TABLE company DROP CONSTRAINT company_web_url;
    ALTER TABLE db_instance_query DROP CONSTRAINT
    db_instance_query_db_instance_id_name_key;
    ALTER TABLE request_map DROP CONSTRAINT request_map_transaction_code_key;
    ALTER TABLE request_map DROP CONSTRAINT request_map_url_key;
    ALTER TABLE role DROP CONSTRAINT role_authority_key;
    ALTER TABLE sys_configuration DROP CONSTRAINT sys_configuration_key_company_id;
    ALTER TABLE theme DROP CONSTRAINT theme_key_company_id_key;
    DROP INDEX item_name_idx;


    CREATE UNIQUE INDEX app_bank_name_country_id_idx ON app_bank(lower(name),country_id);
    CREATE UNIQUE INDEX app_bank_code_country_id_idx ON app_bank(lower(code),country_id);

    CREATE UNIQUE INDEX app_bank_branch_name_bank_id_district_id_idx ON
    app_bank_branch(lower(name),bank_id, district_id);

    CREATE UNIQUE INDEX app_bank_branch_code_bank_id_district_id_idx ON
    app_bank_branch(lower(code),bank_id, district_id);

    CREATE UNIQUE INDEX district_name_country_id_idx ON
    district(lower(name), country_id);

    CREATE UNIQUE INDEX app_country_name_company_id_idx ON
    app_country(lower(name), company_id);

    CREATE UNIQUE INDEX app_country_code_company_id_idx ON
    app_country(lower(code), company_id);

    CREATE UNIQUE INDEX currency_name_company_id_idx ON
    currency(lower(name), company_id);

    CREATE UNIQUE INDEX currency_symbol_company_id_idx ON
    currency(lower(symbol), company_id);


    CREATE UNIQUE INDEX app_db_instance_name_company_id_idx ON app_db_instance(lower(name),company_id);
    CREATE UNIQUE INDEX app_group_name_company_id_idx ON app_group(lower(name),company_id);
    CREATE UNIQUE INDEX app_group_entity_group_id_entt_id_entt_type_id_company_id_idx ON
    app_group_entity(group_id, entity_id, entity_type_id, company_id);

    CREATE UNIQUE INDEX app_schedule_name_company_id_idx ON app_schedule(lower(name),company_id);
    CREATE UNIQUE INDEX app_shell_script_name_company_id_idx ON app_shell_script(lower(name),company_id);

    CREATE UNIQUE INDEX app_user_login_id_company_id_idx ON app_user(lower(login_id),company_id);

    CREATE UNIQUE INDEX app_user_entity_app_user_id_entt_id_entt_type_id_company_id_idx ON
    app_user_entity(app_user_id, entity_id, entity_type_id, company_id);

    CREATE UNIQUE INDEX benchmark_name_company_id_idx ON benchmark(lower(name),company_id);

    CREATE UNIQUE INDEX benchmark_star_name_company_id_idx ON benchmark_star(lower(name),company_id);

    CREATE UNIQUE INDEX company_name_idx ON company(lower(name));
    CREATE UNIQUE INDEX company_code_idx ON company(lower(code));
    CREATE UNIQUE INDEX company_web_url_idx ON company(lower(web_url));
    CREATE UNIQUE INDEX company_email_idx ON company(lower(email));

    CREATE UNIQUE INDEX content_category_name_content_type_id_idx ON
    content_category(lower(name), content_type_id);
    CREATE UNIQUE INDEX content_category_system_content_category_company_id_idx ON
    content_category(lower(system_content_category),company_id);

    CREATE UNIQUE INDEX db_instance_query_name_db_instance_id_idx ON
    db_instance_query(lower(name), db_instance_id);

    CREATE UNIQUE INDEX designation_name_company_id_idx ON designation(lower(name), company_id);
    CREATE UNIQUE INDEX designation_short_name_company_id_idx ON designation(lower(short_name), company_id);

    CREATE UNIQUE INDEX item_name_category_id_idx ON item(lower(name), category_id);

    CREATE UNIQUE INDEX item_type_name_company_id_idx ON item_type(lower(name), company_id);

    CREATE UNIQUE INDEX project_name_company_id_idx ON project(lower(name),company_id);
    CREATE UNIQUE INDEX project_code_company_id_idx ON project(lower(code),company_id);

    CREATE UNIQUE INDEX request_map_transaction_code_idx ON request_map(lower(transaction_code));
    CREATE UNIQUE INDEX request_map_url_idx ON request_map(lower(url));

    CREATE UNIQUE INDEX role_authority_idx ON role(lower(authority));
    CREATE UNIQUE INDEX role_name_company_id_idx ON role(lower(name), company_id);

    CREATE UNIQUE INDEX supplier_name_company_id_idx ON supplier(lower(name), company_id);

    CREATE UNIQUE INDEX sys_configuration_key_company_id_idx ON sys_configuration(lower(key), company_id);


    CREATE UNIQUE INDEX system_entity_key_type_reserved_id_company_id_idx
    ON system_entity(lower(key),type,reserved_id,company_id);

    CREATE UNIQUE INDEX theme_key_company_id_idx ON theme(lower(key),company_id);

    CREATE UNIQUE INDEX vehicle_name_company_id_idx ON
    vehicle(lower(name),company_id);
    """

    String sqlDropCreateConstraintAndIndexSecl = """
    ALTER TABLE app_bank DROP CONSTRAINT bank_code_country_id_key;
    ALTER TABLE app_bank DROP CONSTRAINT bank_name_country_id_key;
    ALTER TABLE district DROP CONSTRAINT district_name_country_id_key;
    ALTER TABLE app_country DROP CONSTRAINT country_code_company_id_key;
    ALTER TABLE app_country DROP CONSTRAINT country_name_company_id_key;
    ALTER TABLE currency DROP CONSTRAINT currency_symbol_company_id_key;
    ALTER TABLE app_db_instance DROP CONSTRAINT app_db_instance_company_id_name_key;
    ALTER TABLE app_group_entity DROP CONSTRAINT unique_group_id;
    ALTER TABLE app_user DROP CONSTRAINT unique_login_id;
    ALTER TABLE app_user_entity DROP CONSTRAINT
    app_user_entity_company_id_entity_type_id_entity_id_app_use_key;
    ALTER TABLE company DROP CONSTRAINT company_email_key;
    ALTER TABLE company DROP CONSTRAINT company_web_url_key;
    ALTER TABLE content_category DROP CONSTRAINT
    content_category_company_id_system_content_category_key;
    ALTER TABLE db_instance_query DROP CONSTRAINT
    db_instance_query_db_instance_id_name_key;
    ALTER TABLE request_map DROP CONSTRAINT request_map_transaction_code_key;
    ALTER TABLE request_map DROP CONSTRAINT request_map_url_key;
    ALTER TABLE role DROP CONSTRAINT role_authority_key;
    ALTER TABLE sys_configuration DROP CONSTRAINT
    sys_configuration_company_id_key_key;
    ALTER TABLE theme DROP CONSTRAINT theme_company_id_key_key;
    DROP INDEX item_name_idx;


    CREATE UNIQUE INDEX app_bank_name_country_id_idx ON app_bank(lower(name),country_id);
    CREATE UNIQUE INDEX app_bank_code_country_id_idx ON app_bank(lower(code),country_id);

    CREATE UNIQUE INDEX app_bank_branch_name_bank_id_district_id_idx ON
    app_bank_branch(lower(name),bank_id, district_id);

    CREATE UNIQUE INDEX app_bank_branch_code_bank_id_district_id_idx ON
    app_bank_branch(lower(code),bank_id, district_id);

    CREATE UNIQUE INDEX district_name_country_id_idx ON
    district(lower(name), country_id);

    CREATE UNIQUE INDEX app_country_name_company_id_idx ON
    app_country(lower(name), company_id);

    CREATE UNIQUE INDEX app_country_code_company_id_idx ON
    app_country(lower(code), company_id);

    CREATE UNIQUE INDEX currency_name_company_id_idx ON
    currency(lower(name), company_id);

    CREATE UNIQUE INDEX currency_symbol_company_id_idx ON
    currency(lower(symbol), company_id);


    CREATE UNIQUE INDEX app_db_instance_name_company_id_idx ON app_db_instance(lower(name),company_id);
    CREATE UNIQUE INDEX app_group_name_company_id_idx ON app_group(lower(name),company_id);
    CREATE UNIQUE INDEX app_group_entity_group_id_entt_id_entt_type_id_company_id_idx ON
    app_group_entity(group_id, entity_id, entity_type_id, company_id);

    CREATE UNIQUE INDEX app_schedule_name_company_id_idx ON app_schedule(lower(name),company_id);
    CREATE UNIQUE INDEX app_shell_script_name_company_id_idx ON app_shell_script(lower(name),company_id);

    CREATE UNIQUE INDEX app_user_login_id_company_id_idx ON app_user(lower(login_id),company_id);

    CREATE UNIQUE INDEX app_user_entity_app_user_id_entt_id_entt_type_id_company_id_idx ON
    app_user_entity(app_user_id, entity_id, entity_type_id, company_id);

    CREATE UNIQUE INDEX benchmark_name_company_id_idx ON benchmark(lower(name),company_id);

    CREATE UNIQUE INDEX benchmark_star_name_company_id_idx ON benchmark_star(lower(name),company_id);

    CREATE UNIQUE INDEX company_name_idx ON company(lower(name));
    CREATE UNIQUE INDEX company_code_idx ON company(lower(code));
    CREATE UNIQUE INDEX company_web_url_idx ON company(lower(web_url));
    CREATE UNIQUE INDEX company_email_idx ON company(lower(email));

    CREATE UNIQUE INDEX content_category_name_content_type_id_idx ON
    content_category(lower(name), content_type_id);
    CREATE UNIQUE INDEX content_category_system_content_category_company_id_idx ON
    content_category(lower(system_content_category),company_id);

    CREATE UNIQUE INDEX db_instance_query_name_db_instance_id_idx ON
    db_instance_query(lower(name), db_instance_id);

    CREATE UNIQUE INDEX designation_name_company_id_idx ON designation(lower(name), company_id);
    CREATE UNIQUE INDEX designation_short_name_company_id_idx ON designation(lower(short_name), company_id);

    CREATE UNIQUE INDEX item_name_category_id_idx ON item(lower(name), category_id);

    CREATE UNIQUE INDEX item_type_name_company_id_idx ON item_type(lower(name), company_id);

    CREATE UNIQUE INDEX project_name_company_id_idx ON project(lower(name),company_id);
    CREATE UNIQUE INDEX project_code_company_id_idx ON project(lower(code),company_id);

    CREATE UNIQUE INDEX request_map_transaction_code_idx ON request_map(lower(transaction_code));
    CREATE UNIQUE INDEX request_map_url_idx ON request_map(lower(url));

    CREATE UNIQUE INDEX role_authority_idx ON role(lower(authority));
    CREATE UNIQUE INDEX role_name_company_id_idx ON role(lower(name), company_id);

    CREATE UNIQUE INDEX supplier_name_company_id_idx ON supplier(lower(name), company_id);

    CREATE UNIQUE INDEX sys_configuration_key_company_id_idx ON sys_configuration(lower(key), company_id);


    CREATE UNIQUE INDEX system_entity_key_type_reserved_id_company_id_idx
    ON system_entity(lower(key),type,reserved_id,company_id);

    CREATE UNIQUE INDEX theme_key_company_id_idx ON theme(lower(key),company_id);

    CREATE UNIQUE INDEX vehicle_name_company_id_idx ON
    vehicle(lower(name),company_id);
    """

    String sqlRemoveDuplicateBankBranchSfsl = """

        UPDATE exh_task SET outlet_branch_id = 65 WHERE outlet_branch_id = 627;
        DELETE FROM app_bank_branch WHERE id = 627;

        UPDATE exh_task SET outlet_branch_id = 610 WHERE outlet_branch_id = 12;
        DELETE FROM app_bank_branch WHERE id = 12;

        UPDATE exh_task SET outlet_branch_id = 50 WHERE outlet_branch_id = 618;
        DELETE FROM app_bank_branch WHERE id = 618;

        UPDATE exh_task SET outlet_branch_id = 66 WHERE outlet_branch_id = 628;
        DELETE FROM app_bank_branch WHERE id = 628;

        UPDATE exh_task SET outlet_branch_id = 54 WHERE outlet_branch_id = 620;
        DELETE FROM app_bank_branch WHERE id = 620;

        UPDATE exh_task SET outlet_branch_id = 80 WHERE outlet_branch_id = 635;
        DELETE FROM app_bank_branch WHERE id = 635;

        UPDATE exh_task SET outlet_branch_id = 43 WHERE outlet_branch_id = 616;
        DELETE FROM app_bank_branch WHERE id = 616;

        UPDATE exh_task SET outlet_branch_id = 250 WHERE outlet_branch_id = 251;
        DELETE FROM app_bank_branch WHERE id = 251;

        UPDATE exh_task SET outlet_branch_id = 48 WHERE outlet_branch_id = 617;
        DELETE FROM app_bank_branch WHERE id = 617;

        UPDATE exh_task SET outlet_branch_id = 84 WHERE outlet_branch_id = 636;
        DELETE FROM app_bank_branch WHERE id = 636;

        UPDATE exh_task SET outlet_branch_id = 57 WHERE outlet_branch_id = 622;
        DELETE FROM app_bank_branch WHERE id = 622;

        UPDATE exh_task SET outlet_branch_id = 55 WHERE outlet_branch_id = 621;
        DELETE FROM app_bank_branch WHERE id = 621;

        UPDATE exh_task SET outlet_branch_id = 58 WHERE outlet_branch_id = 623;
        DELETE FROM app_bank_branch WHERE id = 623;

        UPDATE exh_task SET outlet_branch_id = 109 WHERE outlet_branch_id = 154;
        DELETE FROM app_bank_branch WHERE id = 154;

        UPDATE exh_task SET outlet_branch_id = 34 WHERE outlet_branch_id = 614;
        DELETE FROM app_bank_branch WHERE id = 614;

        UPDATE exh_task SET outlet_branch_id = 612 WHERE outlet_branch_id = 24;
        DELETE FROM app_bank_branch WHERE id = 24;

        UPDATE exh_task SET outlet_branch_id = 78 WHERE outlet_branch_id = 632;
        DELETE FROM app_bank_branch WHERE id = 632;

        UPDATE exh_task SET outlet_branch_id = 171 WHERE outlet_branch_id = 107;
        DELETE FROM app_bank_branch WHERE id = 107;

    """

    String sqlRemoveDuplicateBankBranchSfsa = """
    UPDATE exh_task SET outlet_branch_id = 48 WHERE outlet_branch_id = 494;
    UPDATE exh_task SET outlet_branch_id = 402 WHERE outlet_branch_id = 410;
    UPDATE exh_task SET outlet_branch_id = 402 WHERE outlet_branch_id = 520;
    UPDATE exh_task SET outlet_branch_id = 40 WHERE outlet_branch_id = 77;

    DELETE FROM app_bank_branch WHERE id = 494;
    DELETE FROM app_bank_branch WHERE id = 410;
    DELETE FROM app_bank_branch WHERE id = 520;
    DELETE FROM app_bank_branch WHERE id = 77;
    """

    String sqlRemoveDuplicateBankBranchSecl = """
    UPDATE exh_task SET outlet_branch_id = 48 WHERE outlet_branch_id = 494;
    UPDATE exh_task SET outlet_branch_id = 402 WHERE outlet_branch_id = 410;
    UPDATE exh_task SET outlet_branch_id = 402 WHERE outlet_branch_id = 520;
    UPDATE exh_task SET outlet_branch_id = 40 WHERE outlet_branch_id = 77;

    DELETE FROM app_bank_branch WHERE id = 494;
    DELETE FROM app_bank_branch WHERE id = 410;
    DELETE FROM app_bank_branch WHERE id = 520;
    DELETE FROM app_bank_branch WHERE id = 77;
    """

    String sqlDeleteVersionConfig = "DELETE FROM sys_configuration WHERE key = 'mis.application.deployment.dbVersion';"

    String insertEnforceVersionConfigSFSL = """
        INSERT INTO sys_configuration(id,company_id,description,key,plugin_id,value,updated_by,message,version)
        VALUES(nextVal('sys_configuration_id_seq'),100,'If config is true , enforce to update application after current release span',
        'mis.application.enforceReleaseVersion',1,'true',0,'Application is out of date. Please consider updating to latest version',0);
    """

    String insertEnforceVersionConfigSFSA = """
        INSERT INTO sys_configuration(id,company_id,description,key,plugin_id,value,updated_by,message,version)
        VALUES(nextVal('sys_configuration_id_seq'),101,'If config is true , enforce to update application after current release span',
        'mis.application.enforceReleaseVersion',1,'true',0,'Application is out of date. Please consider updating to latest version',0);
    """

    String insertEnforceVersionConfigSECL = """
        INSERT INTO sys_configuration(id,company_id,description,key,plugin_id,value,updated_by,message,version)
        VALUES(nextVal('sys_configuration_id_seq'),102,'If config is true , enforce to update application after current release span',
        'mis.application.enforceReleaseVersion',1,'true',0,'Application is out of date. Please consider updating to latest version',0);
    """
}
