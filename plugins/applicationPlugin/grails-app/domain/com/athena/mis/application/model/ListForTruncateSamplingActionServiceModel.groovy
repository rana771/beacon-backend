package com.athena.mis.application.model

class ListForTruncateSamplingActionServiceModel {
    public static final String SQL_LIST_FOR_TRUNCATE_SAMPLING = """
    DROP TABLE IF EXISTS list_for_truncate_sampling_action_service_model;
    DROP VIEW IF EXISTS list_for_truncate_sampling_action_service_model;
    CREATE OR REPLACE VIEW list_for_truncate_sampling_action_service_model AS
            SELECT 1137 as id, 'Small' as volume_name, COUNT(field1) as total_count
                FROM sampling8f
            UNION
            SELECT 1138 as id,'Medium' as volume_name, COUNT(field1) as total_count
                FROM sampling25f
            UNION
            SELECT 1139 as id,'Large' as volume_name, COUNT(field1) as total_count
                FROM sampling100f
            ORDER BY id asc;
    """

    long id
    String volumeName
    long totalCount

    static mapping = {
        version false
        cache usage: "read-only"
    }
}
