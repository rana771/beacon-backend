package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppEmployee
import com.athena.mis.utility.DateUtility

/**
 * AppEmployeeService is used to handle only CRUD related object manipulation (e.g. list, read, create, delete etc.)
 */
class AppEmployeeService extends BaseDomainService {

    AppDesignationService appDesignationService
    TestDataModelService testDataModelService

    @Override
    public void init() {
        domainClass = AppEmployee.class
    }

    /**
     * Get sorted list of Employee by companyId
     * @return - list of employee
     */
    @Override
    public List list() {
        long companyId = getCompanyId()
        return AppEmployee.findAllByCompanyId(companyId, [sort: AppEmployee.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
    }

    /**
     * Get list of Employee by companyId
     * @param companyId - Company.id
     * @return - list of Employee
     */
    public List<AppEmployee> findAllByCompanyId(long companyId) {
        List<AppEmployee> lstEmployee = AppEmployee.findAllByCompanyId(companyId, [readOnly: true])
        return lstEmployee
    }

    public AppEmployee findByNameAndCompanyId(String name, long companyId) {
        AppEmployee employee = AppEmployee.findByFullNameAndCompanyId(name, companyId, [readOnly: true])
        return employee
    }

    /**
     * Get custom list of Employee with id and name by companyId for drop down
     * @return - custom list of Employee
     */
    public List listByCompanyForDropDown() {
        List<AppEmployee> lstEmployee = list()
        Map customEmployee
        List result = []
        for (int i = 0; i < lstEmployee.size(); i++) {
            customEmployee = [id: lstEmployee[i].id,
                    name: lstEmployee[i].fullName + PARENTHESIS_START + lstEmployee[i].id + PARENTHESIS_END]
            result << customEmployee
        }
        return result
    }

    /**
     * Get custom list of Employee with id and name by designationId for drop down
     * @param designationId - AppDesignation.id
     * @return - custom list of Employee
     */
    public List listByDesignationForDropDown(long designationId) {
        List<AppEmployee> lstEmployee = list()
        Map customEmployee
        List result = []
        for (int i = 0; i < lstEmployee.size(); i++) {
            if (lstEmployee[i].designationId == designationId) {
                customEmployee = [id: lstEmployee[i].id,
                        name: lstEmployee[i].fullName + PARENTHESIS_START + lstEmployee[i].id + PARENTHESIS_END]
                result << customEmployee
            }
        }
        return result
    }

    /**
     * Get Employee list by DesignationId
     * @param designationId - AppDesignation.id
     * @return - list of Employee
     */
    public List<AppEmployee> findAllByDesignationId(long designationId) {
        List<AppEmployee> lstEmployee = AppEmployee.findAllByDesignationId(designationId, [readOnly: true])
        return lstEmployee
    }

    private static final String INSERT_QUERY = """
            INSERT INTO app_employee( id, version,
                full_name, nick_name, date_of_join, designation_id,
                company_id, created_by, created_on, updated_by )
            VALUES (:id,    :version,
                :fullName, :nickName, :dateOfJoin, :designationId,
                :companyId, :createdBy, :createdOn, :updatedBy);
        """

    @Override
    public boolean createTestData(long companyId , long userId){

        List designation = appDesignationService.findAllByCompanyIdAndName(companyId, 'Junior Software Engineer')
        AppEmployee employee1 = new AppEmployee(fullName: 'Mr. Imtiaz Khan', nickName: 'Mr. Khan', dateOfJoin: new Date(), designationId: designation[0].id, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        AppEmployee employee2 = new AppEmployee(fullName: 'Md. Atiqur Rahman', nickName: 'Mr. Atiq', dateOfJoin: new Date(), designationId: designation[0].id, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)

        runSqlForCreateTestData(employee1)
        runSqlForCreateTestData(employee2)
        return true
    }

    public void runSqlForCreateTestData(AppEmployee employee){

        Map queryParams = [
                id              : testDataModelService.getNextIdForTestData(),
                version         : 0L,
                fullName        : employee.fullName,
                nickName        : employee.nickName,
                dateOfJoin      : DateUtility.getSqlDateWithSeconds(employee.createdOn),
                designationId   : employee.designationId,
                companyId       : employee.companyId,
                createdBy       : employee.createdBy,
                createdOn       : DateUtility.getSqlDateWithSeconds(employee.createdOn),
                updatedBy       : employee.updatedBy
        ]
        executeInsertSql(INSERT_QUERY, queryParams)
    }

    @Override
    public void createDefaultSchema() {
        // @todo create unique index
    }

}
