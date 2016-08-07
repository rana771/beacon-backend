package com.athena.mis.application.model

class ListCompanyForResellerActionServiceModel {

    public static final String SQL_LIST_COMPANY_FOR_RESELLER_MODEL = """
        DROP TABLE IF EXISTS list_company_for_reseller_action_service_model;
        DROP VIEW IF EXISTS list_company_for_reseller_action_service_model;
        CREATE OR REPLACE VIEW list_company_for_reseller_action_service_model AS
            SELECT com.id,com.version,com.name,com.title, com.code, com.is_active,com.web_url,com.address1,com.address2,
                com.country_id,com.contact_name, com.contact_surname,
                com.contact_phone,cou.version AS country_version,cou.name as country_name,cou.code AS country_code,
                cou.isd_code ,cou.phone_number_pattern, cou.nationality, cur.id AS currency_id, cur.version AS currency_version,
                cur.name AS currency_name, cur.symbol, cur.other_code
            FROM company com
            LEFT JOIN app_country cou ON cou.id = com.country_id
            LEFT JOIN currency cur ON cur.id = com.currency_id
    """

    long id                 // primary key (Assigned)
    long version            // entity version in the persistence layer
    String name             // Company Name (unique)
    String title            // Company Title
    String code             // Company code (unique)
    String webUrl           // Company url (unique)
    boolean isActive        // Company.isActive
    String address1         // Company Address 1
    String address2         // Company Address 2
    long countryId          // AppCountry.id
    String contactName      // name of contact person
    String contactSurname   // surname of contact person
    String contactPhone     // phone of contact person
    long countryVersion     // AppCountry.version
    String countryName      // AppCountry.name (unique)
    String countryCode      // AppCountry.code (unique)
    String isdCode          // AppCountry.isdCode (unique)
    String phoneNumberPattern // AppCountry.phoneNumberPattern
    String nationality      // AppCountry.nationality
    long currencyId         // Currency.id
    long currencyVersion    // Currency.version
    String currencyName     // Currency.name (unique)
    String symbol           // Currency.symbol (unique)
    String otherCode        // Currency.otherCode

    static mapping = {
        cache usage: "read-only"
    }
}
