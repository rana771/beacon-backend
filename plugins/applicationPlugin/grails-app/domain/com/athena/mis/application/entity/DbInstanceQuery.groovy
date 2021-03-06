package com.athena.mis.application.entity

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> Create DocOfflineDataFeedQuery for DocOfflineDataFeed search
 * DocOfflineDataFeedQuery has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Local Reference:</strong> Has-a relationship with other domains:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.AppDbInstance#id as dbInstanceId}</li>
 *     <li>{@link com.athena.mis.application.entity.Company#id as companyId}</li>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as createdBy}</li>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as updatedBy}</li>
 * </ul>
 *
 */
class DbInstanceQuery {
    long id                     // primary key (Auto generated by its own sequence)
    long version                // entity version in the persistence layer
    String name                 // Unique name within DocOfflineDataFeed
    String sqlQuery             // any sql e.g. (select * from table_name)
    long dbInstanceId           // DbInstance.id
    long companyId              // Company.id
    Date createdOn              // Object creation DateTime
    long createdBy              // AppUser.id
    Date updatedOn              // Object Updated DateTime
    long updatedBy              // AppUser.id
    int resultPerPage           // result per page
    int numberOfExecution       // count of execution
    String message              // message
    boolean isReserved          // if reserved query then true; otherwise false
    long queryTypeId            // SystemEntity.id (Diagnostic query etc.)

    static constraints = {
        name(nullable: false)
        sqlQuery(nullable: false, maxSize: 5000)
        updatedOn(nullable: true)
        resultPerPage(nullable: false)
        message(nullable: true, maxSize: 1000)
    }

    static mapping = {
        id generator: 'sequence', params: [sequence: 'db_instance_query_id_seq']
        dbInstanceId index: 'db_instance_query_db_instance_id_idx'
        createdBy index: 'db_instance_query_created_by_idx'
        updatedBy index: 'db_instance_query_updated_by_idx'
        companyId index: 'db_instance_query_company_id_idx'

        // unique index on "name" and "db_instance_id" using DbInstanceQueryService.createDefaultSchema()
        // <domain_name><property_name_1><property_name_2>idx
    }

    public String getSqlQuery() {
        return sqlQuery.length() > 5000 ? sqlQuery.substring(0, 4999) : sqlQuery
    }
}
