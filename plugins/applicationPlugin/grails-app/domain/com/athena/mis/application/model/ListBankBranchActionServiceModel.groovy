package com.athena.mis.application.model

class ListBankBranchActionServiceModel {
    public static final String SQL_LIST_BANK_BRANCH_MODEL = """
    DROP TABLE IF EXISTS list_bank_branch_action_service_model;
    DROP VIEW IF EXISTS list_bank_branch_action_service_model;
    CREATE OR REPLACE VIEW list_bank_branch_action_service_model AS
        SELECT
            br.id,
            br.version,
            br.name,
            br.code,
            br.address,
            br.is_sme_service_center,
            br.is_principle_branch,
            br.routing_no,
            br.bank_id,
            br.district_id,
            br.company_id,
            d.name district_name
        FROM app_bank_branch br
        LEFT JOIN district d ON br.district_id = d.id
    """

    long id                     // primary key (Auto generated by its own sequence)
    long version                 // entity version in the persistence layer
    String name                 // Unique name within a company
    String code                 // Unique code within a company
    String address              // address of BanBranch
    boolean isSmeServiceCenter  // flag for service center
    boolean isPrincipleBranch   // flag for principle branch
    String routingNo
    long bankId                 // Bank.id
    long districtId             // District.id
    long companyId              // Company.id
    String districtName         // District.name

    static mapping = {
        cache usage: "read-only"
    }
}
