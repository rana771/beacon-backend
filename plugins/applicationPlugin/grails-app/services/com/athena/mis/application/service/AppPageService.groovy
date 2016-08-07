package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppPage
import org.apache.log4j.Logger

class AppPageService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    @Override
    void createDefaultSchema() {

    }

    @Override
    void init() {
        this.domainClass = AppPage.class
    }

    public int countByCompanyIdAndEntityTypeIdAndEntityId(long companyId, long entityTypeId, long entityId) {
        int count = AppPage.countByCompanyIdAndEntityTypeIdAndEntityId(companyId, entityTypeId, entityId)
        return count
    }

    public int countByTitleIlikeAndEntityTypeIdAndCompanyId(String title, long entityTypeId, long companyId) {
        int count = AppPage.countByTitleIlikeAndEntityTypeIdAndCompanyId(title, entityTypeId, companyId)
        return count
    }

    public int countByTitleIlikeAndEntityTypeIdAndCompanyIdAndIdNotEqual(String title, long entityTypeId, long companyId, pageId) {
        int count = AppPage.countByTitleIlikeAndEntityTypeIdAndCompanyIdAndIdNotEqual(title, entityTypeId, companyId, pageId)
        return count
    }

    public int countByEntityTypeIdAndEntityIdAndCompanyId(long entityTypeId, long entityId, long companyId) {
        int count = AppPage.countByEntityTypeIdAndEntityIdAndCompanyId(entityTypeId, entityId, companyId)
        return count
    }

    public int countByTitleIlikeAndEntityTypeIdAndEntityIdAndCompanyId(String title, long entityTypeId, long entityId, long companyId) {
        int count = AppPage.countByTitleIlikeAndEntityTypeIdAndEntityIdAndCompanyId(title, entityTypeId, entityId, companyId)
        return count
    }

    public int countByTitleIlikeAndEntityTypeIdAndEntityIdAndCompanyIdAndIdNotEqual(String title, long entityTypeId, long entityId, long companyId, long id) {
        int count = AppPage.countByTitleIlikeAndEntityTypeIdAndEntityIdAndCompanyIdAndIdNotEqual(title, entityTypeId, entityId, companyId, id)
        return count
    }

    public List<AppPage> findAllByEntityIdAndEntityTypeId(long entityId, long entityTypeId) {
        List<AppPage> pageList = AppPage.findAllByEntityIdAndEntityTypeId(entityId, entityTypeId, [readOnly: true])
        return pageList
    }

    private static final String DELETE_BY_TYPE_QUERY = """
          DELETE FROM app_page
          WHERE entity_id=:entityId
          AND entity_type_id=:entityTypeId
          AND company_id =:companyId
    """

    public int deleteByEntityAndEntityType(long entityId, long entityTypeId, long companyId) {
        Map queryParams = [entityId: entityId, entityTypeId: entityTypeId, companyId: companyId]
        int deleteCount = executeUpdateSql(DELETE_BY_TYPE_QUERY, queryParams)
        return deleteCount
    }

    @Override
    boolean createTestData(long companyId, long systemUserId) {
        return false
    }

    public boolean createDefaultData(long companyId, long sysUserId) {
        try {
            new AppPage(title: "About Us", body: "This is About Us page", companyId: companyId,
                    createdOn: new Date(), createdBy: sysUserId, updatedBy: 0L).save(flush: true)
            new AppPage(title: "Contact Us", body: "This is Contact Us page", companyId: companyId,
                    createdOn: new Date(), createdBy: sysUserId, updatedBy: 0L).save(flush: true)
            new AppPage(title: "User Manual", body: "This is User Manual page", companyId: companyId,
                    createdOn: new Date(), createdBy: sysUserId, updatedBy: 0L).save(flush: true)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }
}
