package com.athena.mis.application.model


class ListAppEmployeeActionServiceModel {

    public static final String SQL_EMPLOYEE_MODEL = """
    DROP TABLE IF EXISTS list_app_employee_action_service_model;
    DROP VIEW IF EXISTS list_app_employee_action_service_model;
    CREATE OR REPLACE VIEW list_app_employee_action_service_model AS
    SELECT emp.id, emp.version, emp.company_id, emp.full_name,
    emp.nick_name, emp.mobile_no, emp.email, emp.address, emp.date_of_birth,
    emp.date_of_join, emp.designation_id, desig.name designation_name
    FROM app_employee emp
    LEFT JOIN app_designation desig ON desig.id = emp.designation_id
    """

    long id                 // AppEmployee.id
    long version            // AppEmployee.version
    long companyId          // AppEmployee.companyId
    String fullName         // AppEmployee.fullName
    String nickName         // AppEmployee.nickName
    String mobileNo         // AppEmployee.mobileNo
    String email            // AppEmployee.email
    String address          // AppEmployee.address
    Date dateOfBirth        // AppEmployee.dateOfBirth
    Date dateOfJoin         // AppEmployee.dateOfJoin
    long designationId      // AppEmployee.designationId
    String designationName  // AppDesignation.name matching AppEmployee.designationId

    static mapping = {
        cache usage: "read-only"
    }
}
