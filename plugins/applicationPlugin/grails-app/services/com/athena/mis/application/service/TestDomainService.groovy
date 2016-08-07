package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.TestDomain
import com.athena.mis.utility.DateUtility


class TestDomainService  extends BaseDomainService {

     TestDataModelService testDataModelService

            @Override
            public void init() {
                domainClass = TestDomain.class
            }


            /**
             * Pull testDomain object
             * @return - list of testDomain
             */
            @Override
            public List<TestDomain> list() {
              return TestDomain.findAllByCompanyId(companyId, [sort: TestDomain.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true]);
           }

    /**
    * Get list of TestDomain by list of ids
    * @param lstTestDomainIds - list of AppGroup.id
    * @return - list of TestDomain by ids
    */
    public List<TestDomain> findAllByIdInList(List<Long> lstTestDomainIds) {
        List<TestDomain> lstTestDomain = TestDomain.findAllByIdInList(lstTestDomainIds, [sort: TestDomain.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstTestDomain
    }

    public List<TestDomain> findAllByNameIlikeAndCompanyId(String query) {
        List<TestDomain> lstTestDomain = TestDomain.findAllByNameIlikeAndCompanyId(query, super.companyId, [readOnly: true])
        return lstTestDomain
    }

    /**
    * Get testDomain object by name and companyId
    * @param name
    * @param companyId
    * @return - TestDomain object
    */
    public TestDomain findByNameAndCompanyId(String name, long companyId) {
        TestDomain testDomain = TestDomain.findByNameAndCompanyId(name, companyId, [readOnly: true])
        return testDomain
    }


    @Override
    public void createDefaultSchema() {
            String nameIndex = "create unique index testDomain_name_company_id_idx on testDomain(lower(name),company_id);"
            executeSql(nameIndex)
            String codeIndex = "create unique index testDomain_code_company_id_idx on testDomain(lower(code),company_id);"
            executeSql(codeIndex)
        }

        public int countByCompanyId(long companyId) {
        return TestDomain.countByCompanyId(companyId)
    }

    /**
    * Count test data
    * @param companyId
    * @return
    */
    public int countByCompanyIdAndIdLessThan(long companyId){
        return TestDomain.countByCompanyIdAndIdLessThan(companyId, 0)
    }

        private static final String INSERT_QUERY = """
                INSERT INTO testDomain( id, version, name, code, description, content_count, start_date, end_date, company_id,
                created_by, created_on, updated_by, is_approve_in_from_supplier, is_approve_in_from_inventory,
                is_approve_inv_out, is_approve_consumption, is_approve_production)
                VALUES (:id, :version, :name, :code, :description, :contentCount, :startDate, :endDate, :companyId, :createdBy,
                :createdOn, :updatedBy, :isApproveInFromSupplier, :isApproveInFromInventory, :isApproveInvOut, :isApproveConsumption,
                :isApproveProduction);
        """

    @Override
    public boolean createTestData(long companyId, long userId) {
      //Write your default data insert statement here
        return true
    }

    public void runSqlForCreateTestData(TestDomain parameter) {
        Map queryParams = [
                    id                      : testDataModelService.getNextIdForTestData(),
                    version                 : 0L,
                    name                    : parameter.name,
                    code                    : parameter.code,
                    description             : parameter.description,
                    contentCount            : parameter.contentCount,
                    startDate               : DateUtility.getSqlDate(parameter.startDate),
                    endDate                 : DateUtility.getSqlDate(parameter.endDate),
                    companyId               : parameter.companyId,
                    createdBy               : parameter.createdBy,
                    createdOn               : DateUtility.getSqlDateWithSeconds(parameter.createdOn),
                    updatedBy               : parameter.updatedBy,
                    isApproveInFromSupplier : false,
                    isApproveInFromInventory: false,
                    isApproveInvOut         : false,
                    isApproveConsumption    : false,
                    isApproveProduction     : false
            ]
            executeInsertSql(INSERT_QUERY, queryParams)
    }
}
