package com.athena.mis.application.entity

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> One or more Company perform under a certain Country.
 * Country has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Foreign Reference:</strong> Other domain, which has foreign key reference of Country:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.Company#countryId}</li>
 *     <li>{@link com.athena.mis.application.entity.District#countryId}</li>
 *     <li>{@link AppBank#countryId}</li>
 *     <li>{@link com.athena.mis.arms.entity.RmsExchangeHouse#countryId}</li>
 *     <li>{@link com.athena.mis.arms.entity.RmsTask#countryId}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhAgent#countryId}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhBeneficiary#countryId}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhCustomer#countryId}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhCustomerTrace#countryId}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTask#receivingCountryId}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTaskTrace#receivingCountryId}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhBeneficiary#phoneCountryId}</li>
 * </ul>
 *
 * <p><strong>Local Reference:</strong> Has-a relationship with other domains:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as createdBy}</li>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as updatedBy}</li>
 *     <li>{@link com.athena.mis.application.entity.Company#id as companyId}</li>
 *     <li>{@link com.athena.mis.application.entity.Currency#id as currencyId}</li>
 * </ul>
 *
 */
class AppCountry {

    public static final String DEFAULT_SORT_FIELD = "name"

    long id                     // primary key (Auto generated by its own sequence)
    long version                 // entity version in the persistence layer
    String name                 // Unique name within a company
    String code                 // Unique code within a company
    String isdCode              // isd code
    String phoneNumberPattern   // mobile or phone pattern ie ^(\+088[\-\s]?)\d{11}$
    String nationality          // Unique nationality within a company
    long currencyId             // Currency.id
    long companyId              // Company.id
    long createdBy              // AppUser.id
    Date createdOn              // Object creation DateTime
    long updatedBy              // AppUser.id
    Date updatedOn              // Object Updated DateTime

    static constraints = {
        name (nullable: false)
        code(nullable: false)
        isdCode(nullable: false)
        phoneNumberPattern(nullable: false)
        nationality(nullable: false)
        currencyId(nullable: false)
        companyId(nullable: false)
        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
    }

    static mapping = {
        id generator: 'sequence', params: [sequence: 'app_country_id_seq']
        companyId index: 'app_country_company_id_idx'
        currencyId index: 'app_country_currency_id_idx'
        createdBy index: 'app_country_created_by_idx'
        updatedBy index: 'app_country_updated_by_idx'

        // unique index on "name" using CountryService.createDefaultSchema()
        // unique index on "code" using CountryService.createDefaultSchema()
        // <domain_name><property_name_1>idx
    }
}
