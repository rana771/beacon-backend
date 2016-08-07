package com.athena.mis.application.model

class SchemaInformationModel {

    public static final String SQL_SCHEMA_INFO_MODEL = """
    DROP TABLE IF EXISTS schema_information_model;
    DROP VIEW IF EXISTS schema_information_model;
    CREATE OR REPLACE VIEW schema_information_model AS
    SELECT row_number() over() id, table_name, column_name, data_type, udt_name,  is_nullable, COALESCE(character_maximum_length,0) length
    FROM INFORMATION_SCHEMA.COLUMNS;
    """

    long id
    String tableName
    String columnName
    String dataType
    String udtName
    String isNullable
    int length

    static mapping = {
        version false
        cache usage: "read-only"
    }
}
