package com.athena.mis.application.model

class ListCompanyActionServiceModel {
    public static final String SQL_LIST_COMPANY_MODEL = """
    DROP TABLE IF EXISTS list_company_action_service_model;
    DROP VIEW IF EXISTS list_company_action_service_model;
    CREATE OR REPLACE VIEW list_company_action_service_model AS
        SELECT
            com.id,
            com.version,
            com.name,
            com.title,
            com.code,
            com.web_url,
            com.address1,
            com.address2,
            com.country_id,
            com.contact_name,
            com.contact_surname,
            com.contact_phone,
            cou.name as country_name
        FROM company com
        LEFT JOIN app_country cou ON cou.id = com.country_id
    """
    long id                 // primary key (Assigned)
    long version             // entity version in the persistence layer
    String name             // Company Name (unique)
    String title             // Company Name (unique)
    String code             // Company code (unique)
    String webUrl           // Company url (unique)
    String address1         // Company Address 1
    String address2         // Company Address 2
    long countryId          // AppCountry.id
    String contactName      // name of contact person
    String contactSurname   // surname of contact person
    String contactPhone     // phone of contact person
    String countryName      // AppCountry.name

    static mapping = {
        cache usage: "read-only"
    }
}
