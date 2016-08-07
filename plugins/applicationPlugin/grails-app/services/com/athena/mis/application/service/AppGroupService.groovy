package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppGroup
import com.athena.mis.utility.DateUtility

/**
 * AppGroupService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class AppGroupService extends BaseDomainService {

    static transactional = false

    AppUserService appUserService
    TestDataModelService testDataModelService

    @Override
    public void init() {
        domainClass = AppGroup.class
    }

    /**
     * Get sorted list of AppGroup by companyId
     * @return - list of AppGroup
     */
    @Override
    public List list() {
        long companyId = getCompanyId()
        return AppGroup.findAllByCompanyId(companyId, [sort: AppGroup.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
    }

    /**
     * Get list of AppGroup by companyId
     * @param companyId - Company.id
     * @return - list of AppGroup by companyId
     */
    public List<AppGroup> findAllByCompanyId(long companyId) {
        List<AppGroup> lstAppGroup = AppGroup.findAllByCompanyId(companyId, [readOnly: true])
        return lstAppGroup
    }

    /**
     * Get list of AppGroup by list of ids
     * @param lstGroupIds - list of AppGroup.id
     * @return - list of AppGroup by ids
     */
    public List<AppGroup> findAllByIdInList(List<Long> lstGroupIds) {
        List<AppGroup> lstAppGroup = AppGroup.findAllByIdInList(lstGroupIds, [readOnly: true])
        return lstAppGroup
    }

    /**
     * Get count of AppGroup by name and companyId
     * @param name - name of AppGroup
     * @param companyId - Company.id
     * @return - count of AppGroup by name and companyId
     */
    public int countByNameIlikeAndCompanyId(String name, long companyId) {
        int count = AppGroup.countByNameIlikeAndCompanyId(name, companyId)
        return count
    }

    /**
     * Get count of AppGroup by name, id and companyId
     * @param name - name of AppGroup
     * @param id - AppGroup.id
     * @param companyId - Company.id
     * @return - count of AppGroup by name, id and companyId
     */
    public int countByNameIlikeAndIdNotEqualAndCompanyId(String name, long id, long companyId) {
        int count = AppGroup.countByNameIlikeAndIdNotEqualAndCompanyId(name, id, companyId)
        return count
    }

    public int countByCompanyId(long companyId) {
        return AppGroup.countByCompanyId(companyId)
    }

    /**
     * Count test data
     * @param companyId
     * @return
     */
    public int countByCompanyIdAndIdLessThan(long companyId) {
        return AppGroup.countByCompanyIdAndIdLessThan(companyId, 0)
    }

    @Override
    public void createDefaultSchema() {
        String sqlIndex = "create unique index app_group_name_company_id_idx on app_group(lower(name),company_id);"
        executeSql(sqlIndex)
    }

    private static final String INSERT_QUERY = """
        INSERT INTO app_group( id, version,
            name,
            company_id, created_by, created_on, updated_by )
        VALUES (:id,  :version,
            :name,
            :companyId, :createdBy, :createdOn, :updatedBy);
    """

    @Override
    public boolean createTestData(long companyId, long userId) {

        AppGroup appGroup = new AppGroup(name: 'Admin Group', companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        runSqlForCreateTestData(appGroup)
        return true
    }

    public void runSqlForCreateTestData(AppGroup parameter) {
        Map queryParams = [
                id       : testDataModelService.getNextIdForTestData(),
                version  : 0L,
                name     : parameter.name,
                companyId: parameter.companyId,
                createdBy: parameter.createdBy,
                createdOn: DateUtility.getSqlDateWithSeconds(parameter.createdOn),
                updatedBy: parameter.updatedBy
        ]
        executeInsertSql(INSERT_QUERY, queryParams)
    }
}
