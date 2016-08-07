package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppNote
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.utility.DateUtility

class AppNoteService extends BaseDomainService {

    AppSystemEntityCacheService appSystemEntityCacheService
    TestDataModelService testDataModelService

    @Override
    public void init() {
        domainClass = AppNote.class
    }

    private static final String DELETE_BY_TYPE_QUERY = """
                    DELETE FROM app_note
                      WHERE entity_id=:entityId AND
                            entity_type_id=:entityTypeId
    """
    /**
     * Delete AppNote object from DB
     * @param entityId -id of entity (AppUser.id, Company.id etc.)
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, APPUSER, COMPANY etc.)
     * @return -boolean value true
     */
    public Boolean delete(long entityId, long entityTypeId) {

        Map queryParams = [
                entityId    : entityId,
                entityTypeId: entityTypeId
        ]
        int deleteCount = executeUpdateSql(DELETE_BY_TYPE_QUERY, queryParams)
        return Boolean.TRUE
    }

    private static final String DELETE_BY_ENTITY_AND_ENTITY_TYPE_QUERY = """
          DELETE FROM app_note
          WHERE entity_id=:entityId
          AND entity_type_id=:entityTypeId
          AND company_id =:companyId
    """

    public int deleteByEntityAndEntityType(long entityId, long entityTypeId, long companyId) {
        Map queryParams = [entityId: entityId, entityTypeId: entityTypeId, companyId: companyId]
        int deleteCount = executeUpdateSql(DELETE_BY_ENTITY_AND_ENTITY_TYPE_QUERY, queryParams)
        return deleteCount
    }

    /**
     * Get count of AppNote by entityTypeId and companyId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, TASK etc.)
     * @param companyId -id of company
     * @return -an integer containing the value of count
     */
    public int countByEntityTypeIdAndCompanyId(long entityTypeId, long companyId) {
        int count = AppNote.countByEntityTypeIdAndCompanyId(entityTypeId, companyId)
        return count
    }


    public int countByEntityTypeIdAndEntityId(long entityTypeId, long entityId) {
        int count = AppNote.countByEntityTypeIdAndEntityId(entityTypeId, entityId)
        return count
    }

    /**
     * Get list of AppNote by companyId, entityTypeId and entityId
     * @param companyId -id of company
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, TASK etc.)
     * @param entityId -id of entity (Customer.id, Task.id etc.)
     * @return -list of AppNote
     */
    public List<AppNote> findAllByCompanyIdAndEntityTypeIdAndEntityId(long companyId, long entityTypeId, long entityId) {
        List<AppNote> lstAppNote = AppNote.findAllByCompanyIdAndEntityTypeIdAndEntityId(companyId, entityTypeId, entityId, [readOnly: true])
        return lstAppNote
    }

    /**
     * Get list of AppNote by companyId, entityTypeId and entityId and sort by order
     * @param companyId -id of company
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, TASK etc.)
     * @param entityId -id of entity (Customer.id, Task.id etc.)
     * @return -list of AppNote
     */
    public List<AppNote> findAllByCompanyIdAndEntityTypeIdAndEntityIdOrdered(long companyId, long entityTypeId, long entityId, String order) {
        List<AppNote> lstAppNote = AppNote.findAllByCompanyIdAndEntityTypeIdAndEntityId(companyId, entityTypeId, entityId, [readOnly: true, sort: AppNote.DEFAULT_SORT_FIELD, order: order])
        return lstAppNote
    }

    /**
     * Get list of AppNote by companyId, entityTypeId and entityId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, TASK etc.)
     * @param entityId -id of entity (Customer.id, Task.id etc.)
     * @param companyId -id of company
     * @param baseService -object of BaseService
     * @return -list of AppNote
     */
    public List<AppNote> findAllByEntityTypeIdAndEntityIdAndCompanyId(long entityTypeId, long entityId, long companyId, BaseService baseService) {
        List<AppNote> lstAppNote = AppNote.findAllByEntityTypeIdAndEntityIdAndCompanyId(entityTypeId, entityId, companyId, [max: baseService.resultPerPage, offset: baseService.start, sort: baseService.sortColumn, order: baseService.sortOrder, readOnly: true])
        return lstAppNote
    }

    /**
     * Get count of AppNote by companyId, entityTypeId and entityId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, TASK etc.)
     * @param entityId -id of entity (Customer.id, Task.id etc.)
     * @param companyId -id of company
     * @return -an integer containing the value of count
     */
    public int countByEntityTypeIdAndEntityIdAndCompanyId(long entityTypeId, long entityId, long companyId) {
        int count = AppNote.countByEntityTypeIdAndEntityIdAndCompanyId(entityTypeId, entityId, companyId)
        return count
    }

    /**
     * Get list of AppNote by companyId, entityTypeId and entityId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, TASK etc.)
     * @param entityId -id of entity (Customer.id, Task.id etc.)
     * @param companyId -id of company
     * @param note -String value note (search key word)
     * @param baseService -object of BaseService
     * @return -list of AppNote
     */
    public List<AppNote> findAllByEntityTypeIdAndEntityIdAndCompanyIdAndNoteIlike(long entityTypeId, long entityId, long companyId, String note, BaseService baseService) {
        List<AppNote> lstAppNote = AppNote.findAllByEntityTypeIdAndEntityIdAndCompanyIdAndNoteIlike(entityTypeId, entityId, companyId, note, [max: baseService.resultPerPage, offset: baseService.start, sort: baseService.sortColumn, order: baseService.sortOrder, readOnly: true])
        return lstAppNote
    }

    /**
     * Get count of AppNote by companyId, entityTypeId and entityId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, TASK etc.)
     * @param entityId -id of entity (Customer.id, Task.id etc.)
     * @param companyId -id of company
     * @param note -String value note (search key word)
     * @return -an integer containing the value of count
     */
    public int countByEntityTypeIdAndEntityIdAndCompanyIdAndNoteIlike(long entityTypeId, long entityId, long companyId, String note) {
        int count = AppNote.countByEntityTypeIdAndEntityIdAndCompanyIdAndNoteIlike(entityTypeId, entityId, companyId, note)
        return count
    }

    /**
     * Save list of note obj to DB
     * @param noteList
     * @return boolean
     */
    public boolean create(List<AppNote> noteList) {
        if (noteList.size() == 0) return Boolean.FALSE
        for (AppNote note in noteList) {
            create(note)
        }
        return Boolean.TRUE
    }

    private static final String APP_NOTE_CREATE = """
        INSERT INTO app_note
        (
        id,
        version,
        company_id,
        created_by,
        created_on,
        entity_id,
        entity_note_type_id,
        entity_type_id,
        note,
        plugin_id,
        updated_by
        ) VALUES (
        :id,
        :version,
        :companyId,
        :createdBy,
        :createdOn,
        :entityId,
        :entityNoteTypeId,
        :entityTypeId,
        :note,
        :pluginId,
        :updatedBy
        )
    """

    public void createTestData(long companyId, long sysUserId, List<Long> customerIdList) {
        SystemEntity entityType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_NOTE_CUSTOMER, appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, companyId)
        SystemEntity appNoteType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_NOTE_SYSTEM_NOTE, appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE, companyId)
        Map queryParams = [
                id              : testDataModelService.getNextIdForTestData(),
                version         : 0L,
                companyId       : companyId,
                createdBy       : sysUserId,
                createdOn       : DateUtility.getSqlDateWithSeconds(new Date()),
                entityId        : customerIdList[0],
                entityNoteTypeId: appNoteType.id,
                entityTypeId    : entityType.id,
                note            : "Test data note for customer",
                pluginId        : ExchangeHousePluginConnector.PLUGIN_ID,
                updatedBy       : 0L
        ]

        executeInsertSql(APP_NOTE_CREATE, queryParams)

        queryParams = [
                id              : testDataModelService.getNextIdForTestData(),
                version         : 0L,
                companyId       : companyId,
                createdBy       : sysUserId,
                createdOn       : DateUtility.getSqlDateWithSeconds(new Date()),
                entityId        : customerIdList[1],
                entityNoteTypeId: appNoteType.id,
                entityTypeId    : entityType.id,
                note            : "Test data note for customer",
                pluginId        : ExchangeHousePluginConnector.PLUGIN_ID,
                updatedBy       : 0L
        ]

        executeInsertSql(APP_NOTE_CREATE, queryParams)

        queryParams = [
                id              : testDataModelService.getNextIdForTestData(),
                version         : 0L,
                companyId       : companyId,
                createdBy       : sysUserId,
                createdOn       : DateUtility.getSqlDateWithSeconds(new Date()),
                entityId        : customerIdList[2],
                entityNoteTypeId: appNoteType.id,
                entityTypeId    : entityType.id,
                note            : "Test data note for customer",
                pluginId        : ExchangeHousePluginConnector.PLUGIN_ID,
                updatedBy       : 0L
        ]

        executeInsertSql(APP_NOTE_CREATE, queryParams)

        queryParams = [
                id              : testDataModelService.getNextIdForTestData(),
                version         : 0L,
                companyId       : companyId,
                createdBy       : sysUserId,
                createdOn       : DateUtility.getSqlDateWithSeconds(new Date()),
                entityId        : customerIdList[3],
                entityNoteTypeId: appNoteType.id,
                entityTypeId    : entityType.id,
                note            : "Test data note for customer",
                pluginId        : ExchangeHousePluginConnector.PLUGIN_ID,
                updatedBy       : 0L
        ]

        executeInsertSql(APP_NOTE_CREATE, queryParams)
    }

    public void createTestDataForPT(long companyId, long sysUserId, long backlogId) {
        SystemEntity entityTypePtTask = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_NOTE_PT_TASK, appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, companyId)
        SystemEntity appNoteTypeNone = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_NOTE_NONE, appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE, companyId)
        Map queryParams = [
                id              : testDataModelService.getNextIdForTestData(),
                version         : 0L,
                companyId       : companyId,
                createdBy       : sysUserId,
                createdOn       : DateUtility.getSqlDateWithSeconds(new Date()),
                entityId        : backlogId,
                entityNoteTypeId: appNoteTypeNone.id,
                entityTypeId    : entityTypePtTask.id,
                note            : "Test data note for backlog",
                pluginId        : PtPluginConnector.PLUGIN_ID,
                updatedBy       : 0L
        ]

        executeInsertSql(APP_NOTE_CREATE, queryParams)
    }

    /**
     * Create app note by sql for test data
     * @param queryParams
     */
    public void createBySql(Map queryParams) {
        executeInsertSql(APP_NOTE_CREATE, queryParams)
    }

    @Override
    public void createDefaultSchema() {}

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }

}
