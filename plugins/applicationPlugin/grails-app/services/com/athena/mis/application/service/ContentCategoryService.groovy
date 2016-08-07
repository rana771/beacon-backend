package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger

/**
 * ContentCategoryService is used to handle only CRUD related object manipulation (e.g. list, read, create, delete etc.)
 */
class ContentCategoryService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    public static final String IMAGE_TYPE_PHOTO = "PHOTO"
    public static final String IMAGE_TYPE_SIGNATURE = "SIGNATURE"
    public static final String IMAGE_TYPE_LOGO = "LOGO"         // company logo with text
    public static final String IMAGE_TYPE_LOGO_SMALL = "LOGO_SMALL"
    // only company logo without text (used in exh invoice)
    public static final String IMAGE_TYPE_PHOTO_ID = "PHOTO_ID"         // e.g. Passport, Voter ID
    public static final String IMAGE_TYPE_SCREEN_SHOT = "SCREEN_SHOT"

    SystemEntityService systemEntityService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Override
    public void init() {
        domainClass = ContentCategory.class
    }

    /**
     * @return - list of ContentCategory
     */
    @Override
    public List list() {
        return ContentCategory.list(sort: ContentCategory.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true)
    }

    private static final String INSERT_QUERY = """
            INSERT INTO content_category(id, version, name, content_type_id, max_size, system_content_category,
                          width, height, extension, updated_by, updated_on, company_id ,is_reserved,
                          created_by, created_on)
            VALUES (:id,0,:name, :contentTypeId, :maxSize, :systemContentCategory,
                          :width,:height, :extension, :updatedBy, :updatedOn, :companyId, false,
                          :createdBy, :createdOn)
    """

    /**
     * Save ContentCategory object into DB
     * @param contentCategory -object of ContentCategory
     * @return -saved ContentCategory object
     */
    public ContentCategory create(ContentCategory contentCategory) {
        Map queryParams = [
                id                   : contentCategory.id,
                name                 : contentCategory.name,
                contentTypeId        : contentCategory.contentTypeId,
                systemContentCategory: contentCategory.systemContentCategory,
                maxSize              : contentCategory.maxSize,
                width                : contentCategory.width,
                height               : contentCategory.height,
                extension            : contentCategory.extension,
                createdBy            : contentCategory.createdBy,
                updatedBy            : contentCategory.updatedBy,
                createdOn            : DateUtility.getSqlDateWithSeconds(contentCategory.createdOn),
                updatedOn            : contentCategory.updatedOn,
                companyId            : contentCategory.companyId
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException("error occurred at contentCategoryService.create")
        }
        return contentCategory
    }

    private static final String SELECT_NEXT_VAL_CONTENT_CATEGORY = "SELECT nextval('content_category_id_seq') as id"

    /**
     * Get id from dedicated content category id sequence
     * @return - a long variable containing the value of id
     */
    public long getContentCategoryId() {
        List results = executeSelectSql(SELECT_NEXT_VAL_CONTENT_CATEGORY)
        long contentCategoryId = results[0].id
        return contentCategoryId
    }

    /**
     * Update ContentCategory object in DB
     * @param contentCategory -object of ContentCategory
     * @return -an integer containing the value of update count
     */
    public int update(ContentCategory contentCategory) {
        String query = """
                    UPDATE content_category SET
                          version=version+1,
                          name=:name,
                          content_type_id=:contentTypeId,
                          max_size=:maxSize,
                          width=:width,
                          height=:height,
                          extension=:extension,
                          updated_by=:updatedBy,
                          updated_on=:updatedOn
                        WHERE id=:id AND
                              version=:version
        """
        Map queryParams = [
                id           : contentCategory.id,
                version      : contentCategory.version,
                name         : contentCategory.name,
                contentTypeId: contentCategory.contentTypeId,
                maxSize      : contentCategory.maxSize,
                width        : contentCategory.width,
                height       : contentCategory.height,
                extension    : contentCategory.extension,
                updatedBy    : contentCategory.updatedBy,
                updatedOn    : DateUtility.getSqlDateWithSeconds(contentCategory.updatedOn)
        ]

        int updateCount = executeUpdateSql(query, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating content_category')
        }
        contentCategory.version = contentCategory.version + 1
        return updateCount
    }

    /**
     * Delete ContentCategory object from DB
     * @param id -id of ContentCategory object
     * @return -a boolean value(true/false) depending on method success
     */
    public Boolean delete(long id) {
        String query = """
                        DELETE FROM content_category
                          WHERE id=:id
        """
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(query, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("Error occurred while delete content_category information")
        }
        return Boolean.TRUE
    }

    private static final String DELETE_ALL = """
        DELETE FROM content_category WHERE company_id = :companyId
    """

    /**
     * Delete all contentCategory by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    public List<ContentCategory> findAllByCompanyId() {
        long companyId = super.getCompanyId()
        List<ContentCategory> lstContentCategory = ContentCategory.findAllByCompanyId(companyId, [readOnly: true])
        return lstContentCategory
    }

    /**
     * for customer registration where session is unavailable
     * @return
     */
    public List<ContentCategory> findAllByCompanyId(long companyId) {
        List<ContentCategory> lstContentCategory = ContentCategory.findAllByCompanyId(companyId, [readOnly: true])
        return lstContentCategory
    }

    public ContentCategory readBySystemContentCategory(String systemContentCategory) {
        long companyId = super.getCompanyId()
        ContentCategory contentCategory = ContentCategory.findBySystemContentCategoryIlikeAndCompanyId(systemContentCategory, companyId, [readOnly: true])
        return contentCategory
    }

    public ContentCategory readBySystemContentCategory(String systemContentCategory, long companyId) {
        ContentCategory contentCategory = ContentCategory.findBySystemContentCategoryIlikeAndCompanyId(systemContentCategory, companyId, [readOnly: true])
        return contentCategory
    }

    public int countByNameAndContentTypeId(String name, long contentTypeId) {
        long companyId = super.getCompanyId()
        int count = ContentCategory.countByNameIlikeAndContentTypeIdAndCompanyId(name, contentTypeId, companyId)
        return count
    }

    public List<ContentCategory> listByContentTypeId(long contentTypeId) {
        long companyId = super.getCompanyId()
        List<ContentCategory> lstContentCategory = ContentCategory.findAllByContentTypeIdAndIsReservedAndCompanyId(contentTypeId, false, companyId, [readOnly: true])
        return lstContentCategory
    }

    public int countByNameAndContentTypeIdAndIdNotEqual(String name, long contentTypeId, long id) {
        long companyId = super.getCompanyId()
        int count = ContentCategory.countByNameIlikeAndContentTypeIdAndIdNotEqualAndCompanyId(name, contentTypeId, id, companyId)
        return count
    }

    public int countByContentTypeId(long contentTypeId) {
        long companyId = super.getCompanyId()
        int count = ContentCategory.countByContentTypeIdAndCompanyId(contentTypeId, companyId)
        return count
    }

    /**
     * Create default data for ContentCategory
     */
    public boolean createDefaultData(long companyId, long systemUserId) {
        try {
            SystemEntity contentTypeImage = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_IMAGE, companyId)
            Date currDate = new Date()
            ContentCategory contentTypePhoto = new ContentCategory(version: 0, name: 'Photo', width: 379, height: 485, maxSize: 1048576, extension: null, updatedOn: null, updatedBy: 0, companyId: companyId, contentTypeId: contentTypeImage.id, systemContentCategory: IMAGE_TYPE_PHOTO, createdOn: currDate, createdBy: systemUserId, isReserved: true)
            contentTypePhoto.save()

            ContentCategory contentTypeSignature = new ContentCategory(version: 0, name: 'Signature', width: 200, height: 50, maxSize: 1048576, extension: null, updatedOn: null, updatedBy: 0, companyId: companyId, contentTypeId: contentTypeImage.id, systemContentCategory: IMAGE_TYPE_SIGNATURE, createdOn: currDate, createdBy: systemUserId, isReserved: true)
            contentTypeSignature.save()

            ContentCategory contentTypeCompanyLogo = new ContentCategory(version: 0, name: 'Company Logo', width: 0, height: 0, maxSize: 1048576, extension: null, updatedOn: null, updatedBy: 0, companyId: companyId, contentTypeId: contentTypeImage.id, systemContentCategory: IMAGE_TYPE_LOGO, createdOn: currDate, createdBy: systemUserId, isReserved: true)
            contentTypeCompanyLogo.save()

            ContentCategory contentTypeCompanyLogoSmall = new ContentCategory(version: 0, name: 'Company small logo', width: 0, height: 0, maxSize: 1048576, extension: null, updatedOn: null, updatedBy: 0, companyId: companyId, contentTypeId: contentTypeImage.id, systemContentCategory: IMAGE_TYPE_LOGO_SMALL, createdOn: currDate, createdBy: systemUserId, isReserved: true)
            contentTypeCompanyLogoSmall.save()

            ContentCategory contentTypePhotoId = new ContentCategory(version: 0, name: 'Photo ID', width: 500, height: 500, maxSize: 1048576, extension: null, updatedOn: null, updatedBy: 0, companyId: companyId, contentTypeId: contentTypeImage.id, systemContentCategory: IMAGE_TYPE_PHOTO_ID, createdOn: currDate, createdBy: systemUserId, isReserved: true)
            contentTypePhotoId.save()

            if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                ContentCategory contentTypeScreenShot = new ContentCategory(version: 0, name: 'Screen Shot', width: 0, height: 0, maxSize: 2097152, extension: null, updatedOn: null, updatedBy: 0, companyId: companyId, contentTypeId: contentTypeImage.id, systemContentCategory: IMAGE_TYPE_SCREEN_SHOT, createdOn: currDate, createdBy: systemUserId, isReserved: true)
                contentTypeScreenShot.save()
            }

            if (PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)) {
                contentTypeSignature.width = 150
                contentTypeSignature.height = 155
                contentTypeSignature.save()
            }
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index content_category_name_content_type_id_idx on content_category(lower(name), content_type_id);"
        executeSql(nameIndex)
        String systemIndex = "create unique index content_category_system_content_category_company_id_idx on content_category(lower(system_content_category),company_id);"
        executeSql(systemIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
