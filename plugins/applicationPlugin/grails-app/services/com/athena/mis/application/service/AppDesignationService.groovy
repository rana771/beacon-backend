package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppDesignation
import com.athena.mis.utility.DateUtility

class AppDesignationService extends BaseDomainService {

    TestDataModelService testDataModelService

    @Override
    public void init() {
        domainClass = AppDesignation.class
    }

    /**
     * Get sorted list of Designation by companyId
     * @return - list of Designation
     */
    @Override
    public List list() {
        long companyId = getCompanyId()
        return AppDesignation.findAllByCompanyId(companyId, [sort: AppDesignation.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
    }

    /**
     * Get list of Designation by companyId
     * @param companyId - Company.id
     * @return - list of Designation by companyId
     */
    public List<AppDesignation> findAllByCompanyId (long companyId) {
        List<AppDesignation> lstDesignation = AppDesignation.findAllByCompanyId(companyId, [readOnly: true])
        return lstDesignation
    }

    /**
     * Get list of Designation by companyId and Name
     * @param companyId - Company.id
     * @param name
     * @return - list of Designation by companyId and Name
     */
    public List<AppDesignation> findAllByCompanyIdAndName (long companyId, String name) {
        List<AppDesignation> lstDesignation = AppDesignation.findAllByCompanyIdAndName(companyId, name, [readOnly: true])
        return lstDesignation
    }

    /**
     * Get count of Designation by name and companyId
     * @param name - name of Designation
     * @param companyId - Company.id
     * @return - count of Designation by name and companyId
     */
    public int countByNameIlikeAndCompanyId(String name, long companyId) {
        int count = AppDesignation.countByNameIlikeAndCompanyId(name, companyId)
        return count
    }

    /**
     * Count test data
     * @param companyId
     * @return
     */
    public int countByCompanyIdAndIdLessThan(long companyId){
        return AppDesignation.countByCompanyIdAndIdLessThan(companyId, 0)
    }


    /**
     * Get count of Designation by name, companyId and id
     * @param name - name of Designation
     * @param companyId - Company.id
     * @param id - Designation.id
     * @return - count of Designation by name, companyId and id
     */
    public int countByNameIlikeAndCompanyIdAndIdNotEqual(String name, long companyId, long id) {
        int count = AppDesignation.countByNameIlikeAndCompanyIdAndIdNotEqual(name, companyId, id)
        return count
    }

    /**
     * Get count of Designation by short name and companyId
     * @param shortName - short name of Designation
     * @param companyId - Company.id
     * @return - count of Designation by short name and companyId
     */
    public int countByShortNameIlikeAndCompanyId(String shortName, long companyId) {
        int count = AppDesignation.countByShortNameIlikeAndCompanyId(shortName, companyId)
        return count
    }

    /**
     * Get count of Designation by short name, companyId and id
     * @param shortName - short name of Designation
     * @param companyId - Company.id
     * @param id - Designation.id
     * @return - count of Designation by short name, companyId and id
     */
    public int countByShortNameIlikeAndCompanyIdAndIdNotEqual(String shortName, long companyId, long id) {
        int count = AppDesignation.countByShortNameIlikeAndCompanyIdAndIdNotEqual(shortName, companyId, id)
        return count
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index app_designation_name_company_id_idx on app_designation(lower(name), company_id);"
        executeSql(nameIndex)
        String shortNameIndex = "create unique index app_designation_short_name_company_id_idx on app_designation(lower(short_name), company_id);"
        executeSql(shortNameIndex)
    }

    private static final String INSERT_QUERY =
            """
            INSERT INTO app_designation( id, version, name, short_name, company_id, created_by, created_on, updated_by)
            VALUES (:id,
                :version, :name, :shortName,
                :companyId, :createdBy, :createdOn, :updatedBy);
        """

    public int countByCompanyId(long companyId) {
        return AppDesignation.countByCompanyId(companyId)
    }

    @Override
    public boolean createTestData(long companyId , long userId){

        AppDesignation designation1 = new AppDesignation(name: 'General Manager', shortName: 'GM', companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        AppDesignation designation2 = new AppDesignation(name: 'Manager', shortName: 'M', companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        AppDesignation designation3 = new AppDesignation(name: 'Junior Software Engineer', shortName: 'JSE', companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)

        runSqlForCreateTestData(designation1)
        runSqlForCreateTestData(designation2)
        runSqlForCreateTestData(designation3)
        return true
    }

    public void runSqlForCreateTestData(AppDesignation designation){
        Map queryParams = [
                id          : testDataModelService.getNextIdForTestData(),
                version     : 0L,
                name        : designation.name,
                shortName   : designation.shortName,
                companyId   : designation.companyId,
                createdBy   : designation.createdBy,
                createdOn   : DateUtility.getSqlDateWithSeconds(designation.createdOn),
                updatedBy   : designation.updatedBy
        ]
        executeInsertSql(INSERT_QUERY, queryParams)
    }
}
