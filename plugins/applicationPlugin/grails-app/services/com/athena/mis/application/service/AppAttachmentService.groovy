package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppAttachment
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.DateUtility
import groovy.sql.GroovyRowResult

class AppAttachmentService extends BaseDomainService {

    ContentCategoryService contentCategoryService
    AppSystemEntityCacheService appSystemEntityCacheService
    TestDataModelService testDataModelService

    @Override
    public void init() {
        domainClass = AppAttachment.class
    }

    /**
     * Get AppAttachment object without content
     * @param id -id of AppAttachment
     * @return -object of AppAttachment
     */
    @Override
    public AppAttachment read(long id) {
        AppAttachment attachment = buildObject(id)
        return attachment
    }

    /**
     * Get AppAttachment object with content
     * @param id -id of AppAttachment
     * @return -object of AppAttachment
     */
    public AppAttachment readWithContent(long id) {
        return AppAttachment.read(id)
    }

    private static final String READ_QUERY = """
        SELECT id, version, entity_id, entity_type_id, content_type_id, created_on, created_by, company_id,
        extension, caption, content_category_id, file_name, expiration_date
        FROM app_attachment
        WHERE id=:id
    """

    /**
     * Build object of AppAttachment without content
     * @param id -id of AppAttachment
     * @return -object of AppAttachment
     */
    public AppAttachment buildObject(long id) {
        List<GroovyRowResult> lstAttachment = executeSelectSql(READ_QUERY, [id: id])
        if (!lstAttachment || (lstAttachment.size() <= 0)) {
            return null
        }
        GroovyRowResult eachRow = lstAttachment[0]
        AppAttachment attachment = new AppAttachment()
        attachment.id = eachRow.id
        attachment.version = eachRow.version
        attachment.entityId = eachRow.entity_id
        attachment.entityTypeId = eachRow.entity_type_id
        attachment.contentTypeId = eachRow.content_type_id
        attachment.extension = eachRow.extension
        attachment.caption = eachRow.caption
        attachment.createdOn = eachRow.created_on
        attachment.createdBy = eachRow.created_by
        attachment.companyId = eachRow.company_id
        attachment.contentCategoryId = eachRow.content_category_id
        attachment.fileName = eachRow.file_name
        attachment.expirationDate = eachRow.expiration_date
        return attachment
    }

    private static final String CREATE_WITHOUT_CONTENT_QUERY = """
        UPDATE app_attachment
        SET
            caption = :caption,
            content_category_id = :contentCategoryId,
            content_type_id = :contentTypeId,
            entity_type_id = :entityTypeId,
            entity_id = :entityId,
            expiration_date = :expirationDate,
            created_by = :createdBy,
            company_id = :companyId
        WHERE
            id = :id AND
            version = :version
    """

    /**
     * Create AppAttachment object in DB without content
     * @param appAttachment -object of AppAttachment
     * @return -updated object of AppAttachment
     */
    public boolean createWithoutContent(AppAttachment appAttachment) {

        Map queryParams = [
                id               : appAttachment.id,
                version          : appAttachment.version,
                caption          : appAttachment.caption,
                contentCategoryId: appAttachment.contentCategoryId,
                contentTypeId    : appAttachment.contentTypeId,
                entityTypeId     : appAttachment.entityTypeId,
                entityId         : appAttachment.entityId,
                createdBy        : appAttachment.createdBy,
                companyId        : appAttachment.companyId,
                expirationDate   : appAttachment.expirationDate ? DateUtility.getSqlDate(appAttachment.expirationDate) : null
        ]

        int updateCount = executeUpdateSql(CREATE_WITHOUT_CONTENT_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred at app_attachment create')
        }
        appAttachment.version = appAttachment.version + 1
        return true
    }

    private static final String UPDATE_CONTENT_QUERY = """
        UPDATE app_attachment
        SET
            extension=:extension,
            file_name=:fileName,
            content=:content
        WHERE
            id=:id AND
            version=:version
    """

    /**
     * Update AppAttachment object in DB with content
     * @param appAttachment -object of AppAttachment
     * @return -updated object of AppAttachment
     */
    public boolean updateContent(AppAttachment appAttachment) {

        Map queryParams = [
                id       : appAttachment.id,
                version  : appAttachment.version,
                content  : appAttachment.content,
                extension: appAttachment.extension,
                fileName : appAttachment.fileName
        ]

        int updateCount = executeUpdateSql(UPDATE_CONTENT_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred at app_attachment update')
        }
        return true
    }

    private static final String UPDATE_WITH_CONTENT_QUERY = """
        UPDATE app_attachment
        SET
            version=version+1,
            caption=:caption,
            content_category_id =:contentCategoryId,
            extension=:extension,
            file_name=:fileName,
            content=:content,
            updated_on=:updatedOn,
            updated_by=:updatedBy,
            expiration_date=:expirationDate
        WHERE
            id=:id AND
            version=:version
    """

    /**
     * Update AppAttachment object in DB with content
     * @param appAttachment -object of AppAttachment
     * @return -updated object of AppAttachment
     */
    public AppAttachment updateWithContent(AppAttachment appAttachment) {

        Map queryParams = [
                id               : appAttachment.id,
                version          : appAttachment.version,
                caption          : appAttachment.caption,
                contentCategoryId: appAttachment.contentCategoryId,
                content          : appAttachment.content,
                extension        : appAttachment.extension,
                fileName         : appAttachment.fileName,
                updatedOn        : DateUtility.getSqlDateWithSeconds(appAttachment.updatedOn),
                updatedBy        : appAttachment.updatedBy,
                expirationDate   : appAttachment.expirationDate ? DateUtility.getSqlDate(appAttachment.expirationDate) : null
        ]

        int updateCount = executeUpdateSql(UPDATE_WITH_CONTENT_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred at app_attachment update')
        }
        appAttachment.version = appAttachment.version + 1
        return appAttachment
    }

    private static final String UPDATE_WITHOUT_CONTENT_QUERY = """
        UPDATE app_attachment
        SET
            version=version+1,
            caption=:caption,
            content_category_id =:contentCategoryId,
            updated_on=:updatedOn,
            updated_by=:updatedBy,
            expiration_date=:expirationDate
        WHERE
            id=:id AND
            version=:version
    """

    /**
     * Update AppAttachment object in DB without content
     * @param appAttachment -object of AppAttachment
     * @return -updated object of AppAttachment
     */
    public AppAttachment updateWithoutContent(AppAttachment appAttachment) {

        Map queryParams = [
                id               : appAttachment.id,
                version          : appAttachment.version,
                caption          : appAttachment.caption,
                contentCategoryId: appAttachment.contentCategoryId,
                updatedOn        : DateUtility.getSqlDateWithSeconds(appAttachment.updatedOn),
                updatedBy        : appAttachment.updatedBy,
                expirationDate   : appAttachment.expirationDate ? DateUtility.getSqlDate(appAttachment.expirationDate) : null
        ]

        int updateCount = executeUpdateSql(UPDATE_WITHOUT_CONTENT_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred at app_attachment update')
        }
        appAttachment.version = appAttachment.version + 1
        return appAttachment
    }

    private static final String DELETE_QUERY = """
        DELETE FROM app_attachment
        WHERE
        id=:id
    """

    /**
     * Delete AppAttachment object from DB
     * @param id -id of AppAttachment object
     * @return -an integer containing the value of count
     */
    public int delete(long id) {
        Map queryParams = [
                id: id
        ]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('Failed to delete attachment')
        }
        return deleteCount
    }

    private static final String DELETE_BY_TYPE_QUERY = """
        DELETE FROM app_attachment
        WHERE entity_id=:entityId AND
        entity_type_id=:entityTypeId
    """

    /**
     * Delete AppAttachment object from DB
     * @param entityId -id of entity (AppUser.id, Company.id etc.)
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, APPUSER, COMPANY etc.)
     * @return -boolean value true
     */
    public Boolean delete(long entityId, long entityTypeId) {

        Map queryParams = [
                entityId    : entityId,
                entityTypeId: entityTypeId
        ]
        executeUpdateSql(DELETE_BY_TYPE_QUERY, queryParams)

        return Boolean.TRUE
    }


    private static final String QUERY_STR = """
        SELECT id, version, entity_id, entity_type_id, content_category_id, content_type_id,caption,
        company_id,created_by,created_on
        FROM app_attachment
        WHERE entity_id = :entityId
        AND entity_type_id = :entityTypeId
        AND content_type_id = :contentTypeId
    """

    public GroovyRowResult getAppAttachmentWithoutContent(long entityTypeId, long entityId, long contentTypeId) {
        Map queryParams = [
                entityTypeId : entityTypeId,
                entityId     : entityId,
                contentTypeId: contentTypeId
        ]
        List<GroovyRowResult> lstEntity = executeSelectSql(QUERY_STR, queryParams)
        return lstEntity[0]
    }

    /**
     * Get object of AppAttachment by entityId, entityTypeId and contentCategoryId
     * @param entityId -id of entity (AppUser.id, Company.id etc.)
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, APPUSER, COMPANY etc.)
     * @param contentCategoryId -id of ContentCategory
     * @return -object of AppAttachment
     */
    public AppAttachment findByEntityIdAndEntityTypeIdAndContentCategoryId(long entityId, long entityTypeId, long contentCategoryId) {
        AppAttachment appAttachment = AppAttachment.findByEntityIdAndEntityTypeIdAndContentCategoryId(entityId, entityTypeId, contentCategoryId, [readOnly: true])
        return appAttachment
    }

    public AppAttachment findByEntityTypeIdAndEntityIdAndCompanyId(long entityTypeId, long entityId, long companyId) {
        return AppAttachment.findByEntityTypeIdAndEntityIdAndCompanyId(entityTypeId, entityId, companyId, [readOnly: true])
    }

    public InputStream readContentByTypeAndEntity(long entityTypeId, long entityId, long companyId) {
        AppAttachment appAttachment = AppAttachment.findByEntityTypeIdAndEntityIdAndCompanyId(entityTypeId, entityId, companyId, [readOnly: true])
        if (!appAttachment) return null
        byte[] courseImage = appAttachment.content
        return new ByteArrayInputStream(courseImage)
    }

    /**
     * Get object of AppAttachment by entityTypeId, entityId and contentTypeId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, APPUSER, COMPANY etc.)
     * @param entityId -id of entity (AppUser.id, Company.id etc.)
     * @param contentTypeId -id of content type (SystemEntity.id --> DOCUMENT, IMAGE)
     * @return -object of AppAttachment
     */
    public AppAttachment findByEntityTypeIdAndEntityIdAndContentTypeId(long entityTypeId, long entityId, long contentTypeId) {
        AppAttachment appAttachment = AppAttachment.findByEntityTypeIdAndEntityIdAndContentTypeId(entityTypeId, entityId, contentTypeId, [readOnly: true])
        return appAttachment
    }
    /**
     * Get object of AppAttachment by entityTypeId, entityId and contentTypeId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, APPUSER, COMPANY etc.)
     * @param entityId -id of entity (AppUser.id, Company.id etc.)
     * @return -object of AppAttachment
     */
    public List<AppAttachment> findByEntityTypeIdAndEntityId(long entityTypeId, long entityId) {
        List<AppAttachment> lstAppAttachment = AppAttachment.findAllByEntityTypeIdAndEntityId(entityTypeId, entityId, [readOnly: true])
        return lstAppAttachment
    }

    //@todo rename methodName
    /**
     * Get object of AppAttachment by entityTypeId, entityId and contentTypeId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, APPUSER, COMPANY etc.)
     * @param entityId -id of entity (AppUser.id, Company.id etc.)
     * @return -object of AppAttachment
     */
    public AppAttachment findAttachmentByEntityTypeIdAndEntityId(long entityTypeId, long entityId) {
        return AppAttachment.findByEntityTypeIdAndEntityId(entityTypeId, entityId, [readOnly: true])
    }
    /**
     * Get object of AppAttachment by entityTypeId, entityId and contentTypeId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, APPUSER, COMPANY etc.)
     * @param entityId -id of entity (AppUser.id, Company.id etc.)
     * @param contentTypeId -id of content type (SystemEntity.id --> DOCUMENT, IMAGE)
     * @param contentCategoryId -id of content category (SCREEN SHOT, LOGO)
     * @return -object of AppAttachment
     */
    public AppAttachment findByEntityTypeIdAndEntityIdAndContentTypeIdAndContentCategoryId(long entityTypeId, long entityId, long contentTypeId, long contentCategoryId) {
        AppAttachment appAttachment = AppAttachment.findByEntityTypeIdAndEntityIdAndContentTypeIdAndContentCategoryId(entityTypeId, entityId, contentTypeId, contentCategoryId, [readOnly: true])
        return appAttachment
    }

    /**
     * Get count of AppAttachment by contentCategoryId
     * @param contentCategoryId -id of ContentCategory
     * @return -an integer containing the value of count
     */
    public int countByContentCategoryId(long contentCategoryId) {
        int count = AppAttachment.countByContentCategoryId(contentCategoryId)
        return count
    }

    /**
     * Get count of AppAttachment by entityTypeId and companyId
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, APPUSER, COMPANY etc.)
     * @param companyId -id of company
     * @return
     */
    public int countByEntityTypeIdAndCompanyId(long entityTypeId, long companyId) {
        int count = AppAttachment.countByEntityTypeIdAndCompanyId(entityTypeId, companyId)
        return count
    }

    public int countByEntityTypeIdAndEntityId(long entityTypeId, long entityId) {
        int count = AppAttachment.countByEntityTypeIdAndEntityId(entityTypeId, entityId)
        return count
    }

    public AppAttachment findByFileNameAndCompanyId(String fileName, long companyId) {
        return AppAttachment.findByFileNameAndCompanyId(fileName, companyId, [readOnly: true])
    }

    /**
     * Get company logo by company
     * @return
     */
    public InputStream getCompanyLogo() {
        long companyId = getCompanyId()
        return getCompanyLogo(companyId)
    }

    /**
     * get company logo (used in use-case without session)
     * @param companyId
     * @return
     */
    public InputStream getCompanyLogo(long companyId) {
        // pull system entity type(Company) object
        SystemEntity contentEntityTypeCompany = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_COMPANY, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, companyId)
        long entityTypeId = contentEntityTypeCompany.id
        ContentCategory contentCategory = contentCategoryService.readBySystemContentCategory(contentCategoryService.IMAGE_TYPE_LOGO, companyId)
        AppAttachment appAttachment = findByEntityTypeIdAndEntityIdAndContentTypeIdAndContentCategoryId(entityTypeId, companyId, contentCategory.contentTypeId, contentCategory.id)
        if (!appAttachment) return null
        byte[] companyLogo = appAttachment.content
        return new ByteArrayInputStream(companyLogo)
    }

    /**
     * get secondary company logo
     * @return - company logo
     */
    public InputStream getSecondaryCompanyLogo() {
        long companyId = getCompanyId()
        // pull system entity type(Company) object
        SystemEntity contentEntityTypeCompany = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_COMPANY, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, companyId)
        long entityTypeId = contentEntityTypeCompany.id
        ContentCategory contentCategory = contentCategoryService.readBySystemContentCategory(contentCategoryService.IMAGE_TYPE_LOGO_SMALL)
        AppAttachment appAttachment = findByEntityTypeIdAndEntityIdAndContentTypeIdAndContentCategoryId(entityTypeId, companyId, contentCategory.contentTypeId, contentCategory.id)
        if (!appAttachment) return null
        byte[] companyLogo = appAttachment.content
        return new ByteArrayInputStream(companyLogo)
    }

    /**
     * find list of content
     * @param entityTypeId -id of entity type (SystemEntity.id --> CUSTOMER, APPUSER, COMPANY etc.)
     * @param entityId -id of entity (AppUser.id, Company.id etc.)
     * @return
     */
    public List<AppAttachment> findAllByEntityTypeIdAndEntityId(long entityTypeId, long entityId) {
        return AppAttachment.findAllByEntityTypeIdAndEntityId(entityTypeId, entityId, [readOnly: true])
    }

    @Override
    public void createDefaultSchema() {}

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }

    public boolean createTestDataForEl(long companyId, long sysUserId, long assignmentOneId, long assignmentTwoId) {
        SystemEntity typeAssignment = (SystemEntity) appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_EL_ASSIGNMENT, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, companyId)

        AppAttachment appAttachmentOne = new AppAttachment(version: 0L, contentCategoryId: 0L, contentTypeId: 0L, entityTypeId: typeAssignment.id, entityId: assignmentOneId,
        content: "Write ten sentences on Bangladesh 71".bytes, caption: EMPTY_SPACE, extension: 'text', fileName: "assignment_one.txt", isDraft: false, companyId: companyId, createdBy: sysUserId,
        createdOn: new Date(), updatedBy: 0L)

        runSqlForCreateTestData(appAttachmentOne)

        AppAttachment appAttachmentTwo = new AppAttachment(version: 0L, contentCategoryId: 0L, contentTypeId: 0L, entityTypeId: typeAssignment.id, entityId: assignmentTwoId,
                content: "Write ten advantages of e-Learning solution on top of typical education system".bytes, caption: EMPTY_SPACE, extension: 'text', fileName: "assignment_two.txt", isDraft: false, companyId: companyId, createdBy: sysUserId,
                createdOn: new Date(), updatedBy: 0L)

        runSqlForCreateTestData(appAttachmentTwo)
        return true
    }

    private static final String TEST_DATA_INSERT_QUERY = """
        INSERT INTO app_attachment(
            id,
            version,
            content_category_id,
            content_type_id,
            entity_type_id,
            entity_id,
            content,
            caption,
            extension,
            file_name,
            expiration_date,
            is_draft,
            company_id,
            created_by,
            created_on,
            updated_by,
            updated_on
            )VALUES (
            :id,
            :version,
            :contentCategoryId,
            :contentTypeId,
            :entityTypeId,
            :entityId,
            :content,
            :caption,
            :extension,
            :fileName,
            :expirationDate,
            :isDraft,
            :companyId,
            :createdBy,
            :createdOn,
            :updatedBy,
            :updatedOn
            )
    """

    /**
     * Run SQL with mapping values
     * @param parameter - ElCourse object
     */
    private void runSqlForCreateTestData(AppAttachment appAttachment) {
        Map queryParams = [
                id               : testDataModelService.getNextIdForTestData(),
                version          : appAttachment.version,
                contentCategoryId: appAttachment.contentCategoryId,
                contentTypeId    : appAttachment.contentTypeId,
                entityTypeId     : appAttachment.entityTypeId,
                entityId         : appAttachment.entityId,
                content          : appAttachment.content,
                caption          : appAttachment.caption,
                extension        : appAttachment.extension,
                fileName         : appAttachment.fileName,
                expirationDate   : appAttachment.expirationDate,
                isDraft          : appAttachment.isDraft,
                companyId        : appAttachment.companyId,
                createdBy        : appAttachment.createdBy,
                createdOn        : DateUtility.getSqlDateWithSeconds(appAttachment.createdOn),
                updatedBy        : appAttachment.updatedBy,
                updatedOn        : appAttachment.updatedOn

        ]
        executeInsertSql(TEST_DATA_INSERT_QUERY, queryParams)
    }
}
