package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger

/**
 *  Common service-class for any kind of User-Entity mapping(e.g : UserProject, UserInventory mapping etc)
 *  For details go through Use-Case doc named 'AppUserEntityService'
 */
class AppUserEntityService extends BaseDomainService {
    private Logger log = Logger.getLogger(getClass())

    SystemEntityService systemEntityService
    AppUserService appUserService
    ProjectService projectService
    TestDataModelService testDataModelService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Override
    public void init() {
        domainClass = AppUserEntity.class
    }

    /**
     * Method to get the count of user group
     * @param entityId - entity id
     * @param entityTypeId - entity type id
     * @return - an integer value of count of user group
     */
    public int countByEntityIdAndEntityTypeId(long entityId, long entityTypeId) {
        int countUserGroup = (int) AppUserEntity.countByEntityIdAndEntityTypeId(entityId, entityTypeId)
        return countUserGroup
    }

    /**
     * Method to count entity type
     * @param systemEntityId - system entity id
     * @return - an integer value of count
     */
    public int countByEntityTypeId(long systemEntityId) {
        int count = AppUserEntity.countByEntityTypeId(systemEntityId)
        return count
    }

    /**
     * Method to find the existing user group mapping object
     * @param appUserId - app user id
     * @param entityTypeId - entity type id
     * @param entityId - entity id
     * @param companyId - company id
     * @return - the object of app user entity
     */
    public AppUserEntity findByAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(long appUserId, long entityTypeId, long entityId, long companyId) {
        AppUserEntity appUserEntity = AppUserEntity.findByAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(appUserId, entityTypeId, entityId, companyId, [readOnly: true])
        return appUserEntity
    }

    /**
     * Method to count existing user project mapping object
     * @param appUserId - app user id
     * @param entityTypeId - entity type id
     * @param entityId - entity id
     * @param companyId - company id
     * @return - the count value of app user entity
     */
    public int countByAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(long appUserId, long entityTypeId, long entityId, long companyId) {
        int appUserCount = AppUserEntity.countByAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(appUserId, entityTypeId, entityId, companyId, [readOnly: true])
        return appUserCount
    }

    /**
     * Method to find the existing user group mapping object
     * @param id - app user entity id
     * @param appUserId - app user id
     * @param entityTypeId - entity type id
     * @param entityId - entity id
     * @param companyId - company id
     * @return - the object of app user entity
     */
    public AppUserEntity findByIdNotEqualAndAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(long id, long appUserId, long entityTypeId, long entityId, long companyId) {
        AppUserEntity appUserEntity = AppUserEntity.findByIdNotEqualAndAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(id, appUserId, entityTypeId, entityId, companyId, [readOnly: true])
        return appUserEntity
    }

    /**
     * Method to find the existing user group mapping count
     * @param id - app user entity id
     * @param appUserId - app user id
     * @param entityTypeId - entity type id
     * @param entityId - entity id
     * @param companyId - company id
     * @return - the count value of app user entity
     */
    public int countByIdNotEqualAndAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(long id, long appUserId, long entityTypeId, long entityId, long companyId) {
        int appUserEntityCount = AppUserEntity.countByIdNotEqualAndAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(id, appUserId, entityTypeId, entityId, companyId, [readOnly: true])
        return appUserEntityCount
    }

    /**
     * Method to find existing user project object
     * @param existingUserId - user id
     * @param entityTypeId - entity type id
     * @param existingProjectId - project id
     * @return - the object of existing user project
     */
    public AppUserEntity findByAppUserIdAndEntityTypeIdAndEntityId(long existingUserId, long entityTypeId, long existingProjectId) {
        AppUserEntity existingUserProject = AppUserEntity.findByAppUserIdAndEntityTypeIdAndEntityId(existingUserId, entityTypeId, existingProjectId, [readOnly: true])
        return existingUserProject
    }

    /**
     * Method to find existing user agent object
     * @param appUserId - app user id
     * @param entityTypeId - entity type id
     * @return - the object of existing user agent
     */
    public AppUserEntity findByAppUserIdAndEntityTypeId(long appUserId, long entityTypeId) {
        AppUserEntity existingUserAgent = AppUserEntity.findByAppUserIdAndEntityTypeId(appUserId, entityTypeId, [readOnly: true])
        return existingUserAgent
    }

    /**
     * Method to find existing user agent object
     * @param userId - user id
     * @param entityTypeId - entity type id
     * @param existingAppUserEntityId - existing app user entity id
     * @return - the object of existing user agent
     */
    public AppUserEntity findByAppUserIdAndEntityTypeIdAndIdNotEqual(long userId, long entityTypeId, long existingAppUserEntityId) {
        AppUserEntity existingUserAgent = AppUserEntity.findByAppUserIdAndEntityTypeIdAndIdNotEqual(userId, entityTypeId, existingAppUserEntityId, [readOnly: true])
        return existingUserAgent
    }

    /**
     * Method to find all app user list
     * @param appUserId - app user id
     * @return - list of app user
     */
    public List<AppUserEntity> findAllByAppUserId(long appUserId) {
        List<AppUserEntity> lstAppUserMapping = AppUserEntity.findAllByAppUserId(appUserId, [readOnly: true])
        return lstAppUserMapping
    }

    /**
     * find appUserEntity by entityId and entityTypeId
     * @param entityId
     * @param entityTypeId
     * @return - object of appUserEntity
     */
    public AppUserEntity findByEntityIdAndEntityTypeId(long entityId, long entityTypeId) {
        AppUserEntity appUserEntity = AppUserEntity.findByEntityIdAndEntityTypeId(entityId, entityTypeId, [readOnly: true])
        return appUserEntity
    }

    public List<Long> findAllByEntityIdAndEntityTypeId(long entityId, long entityTypeId) {
        List<Long> appUserIds = AppUserEntity.findAllByEntityIdAndEntityTypeId(entityId, entityTypeId, [readOnly: true]).appUserId.unique()
        return appUserIds
    }

    public List<AppUserEntity> findAllByAppUserIdAndEntityTypeId(long appUserId, long reservedId) {
        long companyId = getCompanyId()
        SystemEntity appUserEntityType = appSystemEntityCacheService.readByReservedId(reservedId, appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, companyId)
        List<AppUserEntity> lstAppUserMapping = AppUserEntity.findAllByAppUserIdAndEntityTypeId(appUserId, appUserEntityType.id, [readOnly: true])
        return lstAppUserMapping
    }

    /**
     * Method to get list of AppUserEntity object by entityTypeId
     * @param entityTypeId -SystemEntity.id (e.g. Project,Inventory,Customer etc)
     * @return -list of AppUserEntity object(s)
     */
    public List listByType(long entityTypeId) {
        List<AppUserEntity> lst = lstAppUserEntity(entityTypeId)
        return lst
    }

    /**
     * Common method to update all kind of AppUserEntity object(s) (e.g : UserProject, UserInventory mapping etc)
     * @param appUserEntity -AppUserEntity object
     * @return -updateCount(if updateCount<=0 then throw exception to rollback whole DB transaction)
     */
    public int update(AppUserEntity appUserEntity) {
        String queryStr = """
                    UPDATE app_user_entity
                    SET
                          app_user_id=:userId
                    WHERE
                          id=:id
                    """
        Map queryParams = [
                id    : appUserEntity.id,
                userId: appUserEntity.appUserId
        ]

        int updateCount = executeUpdateSql(queryStr, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update user-entity mapping information')
        }
        return updateCount
    }

    public void createTestDataForExh(long companyId, long agentId, long customerId) {
        AppUser exhAgent = appUserService.findByLoginId('agent@athena.com')
        AppUser exhCustomer = appUserService.findByLoginId('customer@athena.com')

        SystemEntity agent = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_AGENT, companyId)
        SystemEntity customer = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_CUSTOMER, companyId)

        new AppUserEntity(appUserId: exhAgent.id, entityId: agentId, entityTypeId: agent.id, companyId: companyId).save()
        new AppUserEntity(appUserId: exhCustomer.id, entityId: customerId, entityTypeId: customer.id, companyId: companyId).save()
    }

    public boolean createDefaultDataForUserArms(long companyId) {
        try {
            AppUser branchUser = appUserService.findByLoginId('branch_banani@athena.com')
            AppUser otherBankUser = appUserService.findByLoginId('other_gulshan@athena.com')
            AppUser exhUser = appUserService.findByLoginId('exh@athena.com')

            SystemEntity bankBranch = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_BANK_BRANCH, companyId)
            SystemEntity exchangeHouse = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_EXCHANGE_HOUSE, companyId)

            new AppUserEntity(appUserId: branchUser.id, entityId: 2, entityTypeId: bankBranch.id, companyId: companyId).save()
            new AppUserEntity(appUserId: otherBankUser.id, entityId: 3, entityTypeId: bankBranch.id, companyId: companyId).save()
            new AppUserEntity(appUserId: exhUser.id, entityId: 1, entityTypeId: exchangeHouse.id, companyId: companyId).save()

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private static final String SELECT_QUERY = """
                SELECT * FROM app_user_entity
                WHERE entity_type_id
                IN(SELECT id from system_entity  WHERE reserved_id =:entityTypeId)
    """

    /**
     * Get List of application user entity
     * @param entityTypeId - entity type id from caller method
     * @return - a list of app user entity
     */
    private List<AppUserEntity> lstAppUserEntity(long entityTypeId) {
        Map queryParams = [
                entityTypeId: entityTypeId
        ]
        List<GroovyRowResult> listAppUserEntity = executeSelectSql(SELECT_QUERY, queryParams)
        List<AppUserEntity> lstReturn = []
        for (int i = 0; i < listAppUserEntity.size(); i++) {
            GroovyRowResult eachRow = listAppUserEntity[i]
            AppUserEntity appUserEntity = new AppUserEntity()
            appUserEntity.id = eachRow.id
            appUserEntity.appUserId = eachRow.app_user_id
            appUserEntity.entityId = eachRow.entity_id
            appUserEntity.entityTypeId = eachRow.entity_type_id
            appUserEntity.companyId = eachRow.company_id

            lstReturn << appUserEntity
        }
        return lstReturn
    }

    @Override
    public void createDefaultSchema() {
        String userIndex = "create unique index app_user_entity_app_user_id_entt_id_entt_type_id_company_id_idx on app_user_entity(app_user_id, entity_id, entity_type_id, company_id);"
        executeSql(userIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }

    private static final String INSERT_QUERY = """
        INSERT INTO app_user_entity (id, app_user_id, entity_id, entity_type_id, company_id)
        VALUES (:id, :appUserId, :entityId, :entityTypeId, :companyId)
    """

    /**
     * create test data for budget
     * @param companyId - id of company
     */
    public void createTestDataForBudget(long companyId) {
        AppUser director = appUserService.findByLoginId('director@athena.com')
        AppUser projectDirector = appUserService.findByLoginId('pd@athena.com')
        Project project = projectService.findByNameAndCompanyId('Dhaka Flyover', companyId)
        SystemEntity entityProject = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_PROJECT, companyId)
        AppUserEntity dirProjectMap = new AppUserEntity(appUserId: director.id, entityId: project.id, entityTypeId: entityProject.id, companyId: companyId)
        AppUserEntity pdProjectMap = new AppUserEntity(appUserId: projectDirector.id, entityId: project.id, entityTypeId: entityProject.id, companyId: companyId)
        insertTestData(dirProjectMap)
        insertTestData(pdProjectMap)
    }

    /**
     * create test data for Accounting
     * @param companyId - id of company
     */
    public void createTestDataForAccounting(long companyId) {
        AppUser accountant = appUserService.findByLoginId('accountant@athena.com')
        AppUser cfo = appUserService.findByLoginId('cfo@athena.com')
        Project project = projectService.findByNameAndCompanyId('Dhaka Flyover', companyId)
        SystemEntity entityProject = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_PROJECT, companyId)
        AppUserEntity accountantProjectMap = new AppUserEntity(appUserId: accountant.id, entityId: project.id, entityTypeId: entityProject.id, companyId: companyId)
        AppUserEntity cfoProjectMap = new AppUserEntity(appUserId: cfo.id, entityId: project.id, entityTypeId: entityProject.id, companyId: companyId)
        insertTestData(accountantProjectMap)
        insertTestData(cfoProjectMap)
    }

    /**
     * create test data for Project Track
     * @param companyId - id of company
     */
    public void createTestDataForPt(long companyId) {
        AppUser admin = appUserService.findByLoginId('ptadmin@athena.com')
        AppUser engineer = appUserService.findByLoginId('engineer@athena.com')
        AppUser sqa = appUserService.findByLoginId('sqa@athena.com')
        String query = "SELECT id FROM pt_project WHERE name ilike 'Project Track' AND company_id = ${companyId}"
        List<GroovyRowResult> project = executeSelectSql(query)
        long projectId = (long) project[0].id
        SystemEntity entityProject = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_PT_PROJECT, companyId)
        AppUserEntity adminProjectMap = new AppUserEntity(appUserId: admin.id, entityId: projectId, entityTypeId: entityProject.id, companyId: companyId)
        AppUserEntity engineerProjectMap = new AppUserEntity(appUserId: engineer.id, entityId: projectId, entityTypeId: entityProject.id, companyId: companyId)
        AppUserEntity sqaProjectMap = new AppUserEntity(appUserId: sqa.id, entityId: projectId, entityTypeId: entityProject.id, companyId: companyId)
        insertTestData(adminProjectMap)
        insertTestData(engineerProjectMap)
        insertTestData(sqaProjectMap)
    }

    /**
     * create test data for inventory
     * @param companyId - id of company
     */
    public void createTestDataForInventory(long companyId, List<Long> lstInventoryIds) {
        AppUser auditor = appUserService.findByLoginId('auditor@athena.com')
        AppUser projectManager = appUserService.findByLoginId('pm@athena.com')
        Project project = projectService.findByNameAndCompanyId('Dhaka Flyover', companyId)
        SystemEntity entityProject = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_PROJECT, companyId)
        AppUserEntity dirProjectMap = new AppUserEntity(appUserId: auditor.id, entityId: project.id, entityTypeId: entityProject.id, companyId: companyId)
        AppUserEntity pdProjectMap = new AppUserEntity(appUserId: projectManager.id, entityId: project.id, entityTypeId: entityProject.id, companyId: companyId)
        insertTestData(dirProjectMap)
        insertTestData(pdProjectMap)

        SystemEntity entityInventory = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_INVENTORY, companyId)
        for (int i = 0; i < lstInventoryIds.size(); i++) {
            AppUserEntity inventoryAuditor = new AppUserEntity(appUserId: auditor.id, entityId: lstInventoryIds[i], entityTypeId: entityInventory.id, companyId: companyId)
            AppUserEntity inventoryPm = new AppUserEntity(appUserId: projectManager.id, entityId: lstInventoryIds[i], entityTypeId: entityInventory.id, companyId: companyId)
            insertTestData(inventoryAuditor)
            insertTestData(inventoryPm)
        }
    }

    /**
     * insert test data
     * @param userEntity - object of AppUserEntity
     */
    private void insertTestData(AppUserEntity userEntity) {
        Map queryParams = [
                id          : testDataModelService.getNextIdForTestData(),
                appUserId   : userEntity.appUserId,
                entityId    : userEntity.entityId,
                entityTypeId: userEntity.entityTypeId,
                companyId   : userEntity.companyId
        ]
        executeInsertSql(INSERT_QUERY, queryParams)
    }

    /**
     * SQL to delete test data from database
     * @param userIds - list of user ids
     * @param entityTypeId - entityTypeId e.g- PROJECT,INVENTORY,PT PROJECT
     */
    public void deleteTestData(List<Long> userIds, long entityTypeId) {
        if (userIds.size() > 0) {
            String lstUserIds = super.buildCommaSeparatedStringOfIds(userIds)
            String deleteSql = """ DELETE FROM app_user_entity WHERE app_user_id IN (${lstUserIds})
                                                                AND entity_type_id = ${entityTypeId} """
            executeUpdateSql(deleteSql)
        }
    }

    /**
     * SQL to delete test data from database
     * @param userIds - list of user ids
     * @param entityTypeId - entityTypeId e.g- PROJECT,INVENTORY,PT PROJECT
     */
    public void deleteTestDataForPtAdmin(long entityTypeId) {
        AppUser admin = appUserService.findByLoginId('ptadmin@athena.com')
        String deleteSql = """ DELETE FROM app_user_entity WHERE app_user_id IN (${admin.id})
                                                                AND entity_type_id = ${entityTypeId} """
        executeUpdateSql(deleteSql)
    }
}
