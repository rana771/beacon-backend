package com.athena.mis.application.model

class TestDataModel {

    public static final String SQL_TEST_DATA_MODEL = """
    DROP TABLE IF EXISTS vw_test_data_model;
    DROP VIEW IF EXISTS vw_test_data_model;
    CREATE OR REPLACE VIEW vw_test_data_model AS
    SELECT row_number() OVER () id, info.table_name, pg_relation_size(table_name) disk_usage, stat.reltuples row_count
      FROM information_schema.tables info
      LEFT JOIN pg_class stat ON stat.relname = info.table_name
    WHERE info.table_schema = 'public'
      AND info.table_type = 'BASE TABLE';
    """

    long id
    long diskUsage
    long rowCount
    String tableName

    static mapping = {
        version false
        table "vw_test_data_model"
        cache usage: "read-only"
    }
}
