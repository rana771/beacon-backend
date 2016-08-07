package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.Project
import com.athena.mis.utility.DateUtility

/**
 * ProjectService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class ProjectService extends BaseDomainService {

    TestDataModelService testDataModelService

    @Override
    public void init() {
        domainClass = Project.class
    }
    /**
     * Pull project object
     * @return - list of project
     */
    @Override
    public List<Project> list() {
        return Project.findAllByCompanyId(companyId, [sort: Project.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true]);
    }

    /**
     * Get list of Project by list of ids
     * @param lstProjectIds - list of AppGroup.id
     * @return - list of Project by ids
     */
    public List<Project> findAllByIdInList(List<Long> lstProjectIds) {
        List<Project> lstProject = Project.findAllByIdInList(lstProjectIds, [sort: Project.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstProject
    }

    public List<Project> findAllByNameIlikeAndCompanyId(String query) {
        List<Project> lstProject = Project.findAllByNameIlikeAndCompanyId(query, super.companyId, [readOnly: true])
        return lstProject
    }

    /**
     * Get project object by name and companyId
     * @param name
     * @param companyId
     * @return - Project object
     */
    public Project findByNameAndCompanyId(String name, long companyId) {
        Project project = Project.findByNameAndCompanyId(name, companyId, [readOnly: true])
        return project
    }

    public int countByCodeIlikeAndCompanyId(Project project) {
        int count = Project.countByCodeIlikeAndCompanyId(project.code, project.companyId)
        return count
    }

    public int countByNameIlikeAndCompanyId(Project project) {
        int count = Project.countByNameIlikeAndCompanyId(project.code, project.companyId)
        return count
    }

    public int countByCodeIlikeAndCompanyId(String code, long companyId) {
        int count = Project.countByCodeIlikeAndCompanyId(code, companyId)
        return count
    }

    public int countByNameIlikeAndCompanyId(String name, long companyId) {
        int count = Project.countByNameIlikeAndCompanyId(name, companyId)
        return count
    }

    public int countByCodeIlikeAndCompanyIdAndIdNotEqual(Project project) {
        int count = Project.countByCodeIlikeAndCompanyIdAndIdNotEqual(project.code, project.companyId, project.id)
        return count
    }

    public int countByNameIlikeAndCompanyIdAndIdNotEqual(Project project) {
        int count = Project.countByNameIlikeAndCompanyIdAndIdNotEqual(project.code, project.companyId, project.id)
        return count
    }

    public int countByCodeIlikeAndCompanyIdAndIdNotEqual(String code, long companyId, long id) {
        return Project.countByCodeIlikeAndCompanyIdAndIdNotEqual(code, companyId, id)
    }

    public int countByNameIlikeAndCompanyIdAndIdNotEqual(String name, long companyId, long id) {
        return Project.countByNameIlikeAndCompanyIdAndIdNotEqual(name, companyId, id)
    }


    private static final String UPDATE_CONTENT_COUNT_QUERY = """
        UPDATE project
        SET content_count = content_count + :contentCount,
            version = version + 1
        WHERE
            id=:id
    """

    // update content count for project during create, update and delete content
    public int updateContentCountForProject(long projectId, int count) {
        Map queryParams = [
                contentCount: count,
                id          : projectId
        ]
        int updateCount = executeUpdateSql(UPDATE_CONTENT_COUNT_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Content count updated for Project")
        }
        return updateCount
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index project_name_company_id_idx on project(lower(name),company_id);"
        executeSql(nameIndex)
        String codeIndex = "create unique index project_code_company_id_idx on project(lower(code),company_id);"
        executeSql(codeIndex)
    }

    public int countByCompanyId(long companyId) {
        return Project.countByCompanyId(companyId)
    }

    /**
     * Count test data
     * @param companyId
     * @return
     */
    public int countByCompanyIdAndIdLessThan(long companyId){
        return Project.countByCompanyIdAndIdLessThan(companyId, 0)
    }

    private static final String INSERT_QUERY = """
        INSERT INTO project( id, version, name, code, description, content_count, start_date, end_date, company_id,
            created_by, created_on, updated_by, is_approve_in_from_supplier, is_approve_in_from_inventory,
            is_approve_inv_out, is_approve_consumption, is_approve_production)
        VALUES (:id, :version, :name, :code, :description, :contentCount, :startDate, :endDate, :companyId, :createdBy,
        :createdOn, :updatedBy, :isApproveInFromSupplier, :isApproveInFromInventory, :isApproveInvOut, :isApproveConsumption,
        :isApproveProduction);
    """

    @Override
    public boolean createTestData(long companyId, long userId) {
        Project project1 = new Project(name: "Dhaka Flyover", code: "DF", description: 'A Flyover all over the dhaka', contentCount: 0, startDate: new Date(), endDate: new Date() + 30, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        Project project2 = new Project(name: "School Construction", code: "SC", description: 'Construction of 4 storied school building', contentCount: 0, startDate: new Date() - 7, endDate: new Date() + 60, companyId: companyId, createdBy: userId, createdOn: new Date() - 7, updatedBy: 0)
        Project project3 = new Project(name: "Roads Construction", code: "RC", description: 'Construction of Dhaka Chittagong 6 Lane Highway', contentCount: 0, startDate: new Date() - 25, endDate: new Date() + 90, companyId: companyId, createdBy: userId, createdOn: new Date() - 25, updatedBy: 0)

        runSqlForCreateTestData(project1)
        runSqlForCreateTestData(project2)
        runSqlForCreateTestData(project3)
        return true
    }

    public void runSqlForCreateTestData(Project parameter) {
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
