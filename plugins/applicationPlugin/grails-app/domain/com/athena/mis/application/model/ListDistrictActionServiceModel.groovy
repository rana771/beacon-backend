package com.athena.mis.application.model

class ListDistrictActionServiceModel {
    public static final String SQL_LIST_DISTRICT_MODEL = """
    DROP TABLE IF EXISTS list_district_action_service_model;
    DROP VIEW IF EXISTS list_district_action_service_model;
    CREATE OR REPLACE VIEW list_district_action_service_model AS
        SELECT
            d.id,
            d.version,
            d.name,
            d.company_id,
            d.country_id,
            c.name country_name
            FROM district d
            LEFT JOIN app_country c ON d.country_id = c.id
    """

    long id             // primary key (Auto generated by its own sequence)
    long version        // entity version in the persistence layer
    String name         // Unique name within a company
    long companyId      // Company.id
    long countryId      // AppCountry.id
    String countryName  // AppCountry.name

    static mapping = {
        cache usage: "read-only"
    }
}
