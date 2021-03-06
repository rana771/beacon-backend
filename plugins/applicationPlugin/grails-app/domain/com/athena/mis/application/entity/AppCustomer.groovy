package com.athena.mis.application.entity

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> Customer is used as a source in AccVoucher.
 * Customer has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Foreign Reference:</strong> Other domain, which has foreign key reference of Customer:</p>
 * <ul>
 *     <li>{@link com.athena.mis.accounting.entity.AccVoucherDetails#sourceId}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccCancelledVoucherDetails#sourceId}</li>
 *     // todo write all foreign key references for EXH, ARMS and other plugins
 * </ul>
 *
 * <p><strong>Local Reference:</strong> Has-a relationship with other domains:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as createdBy}</li>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as updatedBy}</li>
 *     <li>{@link com.athena.mis.application.entity.Company#id as companyId}</li>
 * </ul>
 *
 */
class AppCustomer {

    public static final String DEFAULT_SORT_FIELD = "fullName"
    // Delete query for deleting test data of customer domain
    public static final String DELETE_TEST_DATA_QUERY = """ DELETE FROM app_customer WHERE id < 0 and company_id = :companyId """

    long id                 // primary key (Auto generated by its own sequence)
    long version            // entity version in the persistence layer
    long companyId          // Company.id
    String fullName         // Full Name of Customer
    String nickName         // Nick Name of Customer
    String phoneNo          // Customer's phone no
    String email            // Customer's email address
    Date dateOfBirth        // Customer's birth date
    String address          // Customer's address
    long createdBy          // AppUser.id
    Date createdOn          // Object creation DateTime
    long updatedBy          // AppUser.id
    Date updatedOn          // Object Updated DateTime

    static mapping = {
        id generator: 'sequence', params: [sequence: 'app_customer_id_seq']
        dateOfBirth type: 'date'
        companyId index: 'app_customer_company_id_idx'
        createdBy index: 'app_customer_created_by_idx'
        updatedBy index: 'app_customer_updated_by_idx'
    }

    static constraints = {
        companyId(nullable: false)
        email(nullable: true)
        address(nullable: true)
        phoneNo(nullable: true)
        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
    }

    public String toString() {
        return this.fullName
    }

    public String getAddress() {
        return address && address.length() > 255 ? address.substring(0, 254) : address
    }
}
