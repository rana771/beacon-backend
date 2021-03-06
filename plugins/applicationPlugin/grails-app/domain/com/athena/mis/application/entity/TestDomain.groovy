package com.athena.mis.application.entity

class TestDomain {

    public static final String DEFAULT_SORT_FIELD = "name"
    // Delete query for deleting test data of designation domain
    public static final String DELETE_TEST_DATA_QUERY = """ DELETE FROM app_designation WHERE id < 0 and company_id = :companyId """

    long id                 // primary key (Auto generated by its own sequence)
    long version            // entity version in the persistence layer
    long companyId          // Company.id
    String name             // name of Designation
    String shortName        // shortName or Designation
    long createdBy          // AppUser.id
    Date createdOn          // Object creation DateTime
    long updatedBy          // AppUser.id
    Date updatedOn          // Object Updated DateTime

    static constraints = {
    }
}
