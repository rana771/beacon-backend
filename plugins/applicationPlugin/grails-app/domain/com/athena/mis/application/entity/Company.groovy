package com.athena.mis.application.entity

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> Application runs under a certain Company.
 * Company has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Foreign Reference:</strong> Other domain, which has foreign key reference of Company:</p>
 * <ul>
 *     <strong>Each domain has foreign key reference of Company as companyId EXCEPT followings</strong>
 *
 *     <strong>Application Plugin</strong>
 *     <li>{@link com.athena.mis.application.entity.RequestMap}</li>
 *     <li>{@link com.athena.mis.application.entity.ReservedSystemEntity}</li>
 *     <li>{@link com.athena.mis.application.entity.RoleFeatureMapping}</li>
 *     <li>{@link ReservedRole}</li>
 *     <li>{@link com.athena.mis.application.entity.SystemEntityType}</li>
 *     <li>{@link com.athena.mis.application.entity.UserRole}</li>
 *
 *     <strong>Budget Plugin</strong>
 *     <li>{@link com.athena.mis.budget.entity.BudgSprintBudget}</li>
 *
 *     <strong>Inventory Plugin</strong>
 *     <li>{@link com.athena.mis.inventory.entity.InvInventoryTransactionDetails}</li>
 *
 *     <strong>Accounting Plugin</strong>
 *     <li>{@link com.athena.mis.accounting.entity.AccCancelledVoucherDetails}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccIouPurpose}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccVoucherDetails}</li>
 *
 *     <strong>Fixed Asset Plugin</strong>
 *     <strong>QS Plugin</strong>
 *     <li>{@link com.athena.mis.qs.entity.QsMeasurement}</li>
 *     <strong>Project Track Plugin</strong>
 *     <strong>Document Plugin</strong>
 *
 *     <strong>ExchangeHouse Plugin</strong>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhAgentCurrencyPosting}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhCustomerBeneficiaryMapping}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhPaymentResponseNotification}</li>
 *
 *     <strong>SARB Plugin</strong>
 *
 *     <strong>ARMS Plugin</strong>
 *     <li>{@link com.athena.mis.arms.model.RmsTaskListSummaryModel}</li>
 *
 *     // todo write all foreign key references for all domain in all plugins
 * </ul>
 *
 * <p><strong>Local Reference:</strong> Has-a relationship with other domains:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as createdBy}</li>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as updatedBy}</li>
 *     <li>{@link AppCountry#id as countryId}</li>
 *     <li>{@link com.athena.mis.application.entity.Currency#id as currencyId}</li>
 * </ul>
 *
 */
class Company {

    public static final String DEFAULT_SORT_FIELD = "name"

    long id                 // primary key (Assigned)
    long version            // entity version in the persistence layer
    String name             // Company Name (unique)
    String title            // Company title
    long createdBy          // AppUser.id
    Date createdOn          // Object creation DateTime
    long updatedBy          // AppUser.id
    Date updatedOn          // Object updated DateTime
    String address1         // Company Address 1
    String address2         // Company Address 2
    String webUrl           // Company url (unique)
    String code             // Company code (unique)
    long countryId          // AppCountry.id
    long currencyId         // Currency.id
    String contactName      // name of contact person
    String contactSurname   // surname of contact person
    String contactPhone     // phone of contact person
    boolean isActive        // if false then user of respected company will not be able to login
    boolean isDefaultDataLoaded
    boolean isTestDataLoaded

    static mapping = {
        id generator: 'assigned'
        countryId index: 'company_country_id_idx'
        createdBy index: 'company_created_by_idx'
        updatedBy index: 'company_updated_by_idx'
        currencyId index: 'company_currency_id_idx'

        // unique index on "name" using CompanyService.createDefaultSchema()
        // unique index on "code" using CompanyService.createDefaultSchema()
        // unique index on "web_url" using CompanyService.createDefaultSchema()
        // <domain_name><property_name_1>idx
    }

    static constraints = {
        name(nullable: false)
        title(nullable: true)
        address1(nullable: false, maxSize: 255)
        address2(nullable: true, maxSize: 255)
        updatedOn(nullable: true)
        code(nullable: false)
        countryId(nullable: false)
        currencyId(nullable: false)
        webUrl(nullable: false)
    }

    public String getAddress1() {
        return address1.length() > 255 ? address1.substring(0, 254) : address1
    }

    public String getAddress2() {
        return address2 && address2.length() > 255 ? address2.substring(0, 254) : address2
    }
}
