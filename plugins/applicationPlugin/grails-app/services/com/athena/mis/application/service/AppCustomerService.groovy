package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppCustomer
import com.athena.mis.utility.DateUtility

/**
 *  Service class for basic customer CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'CustomerService'
 */
class AppCustomerService extends BaseDomainService {

    TestDataModelService testDataModelService

    @Override
    public void init() {
        domainClass = AppCustomer.class
    }

    /**
     * @return -list of customer
     */
    @Override
    public List<AppCustomer> list() {
        return AppCustomer.findAllByCompanyId(companyId, [sort: AppCustomer.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true]);
    }

    // used to populate drop-down with full name
    public List listByCompanyForDropDown() {
        List<AppCustomer> lstCustomer = this.list()
        Map customCustomer
        List result = []
        for (int i = 0; i < lstCustomer.size(); i++) {
            customCustomer = [id  : lstCustomer[i].id,
                              name: lstCustomer[i].fullName + PARENTHESIS_START + lstCustomer[i].id + PARENTHESIS_END]
            result << customCustomer
        }
        return result
    }

    /**
     * Count test data
     * @param companyId
     * @return
     */
    public int countByCompanyIdAndIdLessThan(long companyId){
        return AppCustomer.countByCompanyIdAndIdLessThan(companyId, 0)
    }

    public int countByCompanyId(long companyId) {
        return AppCustomer.countByCompanyId(companyId)
    }

    private static final String INSERT_QUERY = """
            INSERT INTO app_customer(
                id, version, full_name, nick_name, company_id, date_of_birth, created_by, created_on, updated_by)
            VALUES (:id,  :version, :fullName, :nickName, :companyId, :dateOfBirth,
                :createdBy, :createdOn, :updatedBy);
    """

    @Override
    public boolean createTestData(long companyId, long userId) {
        AppCustomer customer1 = new AppCustomer(fullName: 'Customer One', nickName: 'CusOne', dateOfBirth: DateUtility.parseMaskedDate("01/01/1980"), companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0L)
        AppCustomer customer2 = new AppCustomer(fullName: 'Customer Two', nickName: 'CusTwo', dateOfBirth: DateUtility.parseMaskedDate("01/01/1981"), companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0L)

        runSqlForCreateTestData(customer1)
        runSqlForCreateTestData(customer2)
        return true
    }

    public void runSqlForCreateTestData(AppCustomer customer) {
        Map queryParams = [
                id         : testDataModelService.getNextIdForTestData(),
                version    : 0L,
                fullName   : customer.fullName,
                nickName   : customer.nickName,
                address    : customer.address,
                companyId  : customer.companyId,
                createdBy  : customer.createdBy,
                createdOn  : DateUtility.getSqlDateWithSeconds(customer.createdOn),
                dateOfBirth: DateUtility.getSqlDate(customer.dateOfBirth),
                updatedBy  : customer.updatedBy
        ]
        executeInsertSql(INSERT_QUERY, queryParams)
    }

    @Override
    public void createDefaultSchema() {
        // @todo create unique index
    }

}
