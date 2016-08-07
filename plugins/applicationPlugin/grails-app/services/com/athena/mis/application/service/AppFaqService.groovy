package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppFaq
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.DateUtility

class AppFaqService extends BaseDomainService {

    TestDataModelService testDataModelService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Override
    public void init() {
        domainClass = AppFaq.class
    }

    public int countByEntityTypeIdAndEntityIdAndCompanyId(long entityTypeId, long entityId, long companyId) {
        int count = AppFaq.countByEntityTypeIdAndEntityIdAndCompanyId(entityTypeId, entityId, companyId)
        return count
    }

    private static final String DELETE_BY_ENTITY_AND_ENTITY_TYPE_QUERY = """
          DELETE FROM app_faq
          WHERE entity_id=:entityId
          AND entity_type_id=:entityTypeId
          AND company_id =:companyId
    """

    public int deleteByEntityAndEntityType(long entityId, long entityTypeId, long companyId) {
        Map queryParams = [entityId: entityId, entityTypeId: entityTypeId, companyId:companyId]
        int deleteCount = executeUpdateSql(DELETE_BY_ENTITY_AND_ENTITY_TYPE_QUERY, queryParams)
        return deleteCount
    }

    @Override
    public void createDefaultSchema() {}

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }

    // elearning faq test data
    public boolean createTestData(long companyId, long systemUserId, long courseId, long lessonId, long pluginId) {
        SystemEntity faqEntity = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_FAQ_DOC_SUB_CATEGORY, appSystemEntityCacheService.SYS_ENTITY_TYPE_FAQ, companyId)

        AppFaq appFaqOne = new AppFaq(
                version: 0L,
                entityTypeId: faqEntity.id,
                entityId: lessonId,
                question: "What is Web-based training?",
                answer: """ Web-based training is an affordable substitute for, or complement to, traditional CD-ROM, video or classroom-based training.
                            Any computer with Internet connectivity can access online courses delivered through a Web browser.
                            For effective learning to occur, courses must be designed specifically for online delivery.
                            That means incorporating interactive exercises that engage students and enhance the learning process.
                """,
                pluginId: pluginId,
                companyId: companyId,
                createdBy: systemUserId,
                createdOn: new Date(),
                updatedBy: 0L,
                updatedOn: null
        )
        runSqlForCreateTestData(appFaqOne)

        AppFaq appFaqTwo = new AppFaq(
                version: 0L,
                entityTypeId: faqEntity.id,
                entityId: lessonId,
                question: "How long will it take to complete the course?",
                answer: """The course content is available to you 24/7, system will notify course duration by email/sms.""",
                pluginId: pluginId,
                companyId: companyId,
                createdBy: systemUserId,
                createdOn: new Date(),
                updatedBy: 0L,
                updatedOn: null
        )
        runSqlForCreateTestData(appFaqTwo)

        AppFaq appFaqThree = new AppFaq(
                version: 0L,
                entityTypeId: faqEntity.id,
                entityId: lessonId,
                question: "Is there an online assessment or exam?",
                answer: """In order to receive your certification, you will need to successfully receive 80% or above on the multiple choice assessment.""",
                pluginId: pluginId,
                companyId: companyId,
                createdBy: systemUserId,
                createdOn: new Date(),
                updatedBy: 0L,
                updatedOn: null
        )
        runSqlForCreateTestData(appFaqThree)

        AppFaq appFaqFour = new AppFaq(
                version: 0L,
                entityTypeId: faqEntity.id,
                entityId: lessonId,
                question: "How do I receive my training Certificate?",
                answer: """ Once you have successfully passed the assessment you can download your training certificate instantly! This option is available 24/7.
                            You can email your certificate and print it off straight away.
                            We can provide email copies for your certificate when required at a later date.""",
                pluginId: pluginId,
                companyId: companyId,
                createdBy: systemUserId,
                createdOn: new Date(),
                updatedBy: 0L,
                updatedOn: null
        )
        runSqlForCreateTestData(appFaqFour)
        return true
    }


    private static final String TEST_DATA_INSERT_QUERY = """
        INSERT INTO app_faq(
            id,
            version,
            entity_type_id,
            entity_id,
            question,
            answer,
            plugin_id,
            company_id,
            created_by,
            created_on,
            updated_by,
            updated_on
            )VALUES (
            :id,
            :version,
            :entityTypeId,
            :entityId,
            :question,
            :answer,
            :pluginId,
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
    private void runSqlForCreateTestData(AppFaq appFaq) {
        Map queryParams = [
                id          : testDataModelService.getNextIdForTestData(),
                version     : appFaq.version,
                entityTypeId: appFaq.entityTypeId,
                entityId    : appFaq.entityId,
                question    : appFaq.question,
                answer      : appFaq.answer,
                pluginId    : appFaq.pluginId,
                companyId   : appFaq.companyId,
                createdBy   : appFaq.createdBy,
                createdOn   : DateUtility.getSqlDateWithSeconds(appFaq.createdOn),
                updatedBy   : appFaq.updatedBy,
                updatedOn   : appFaq.updatedOn

        ]
        executeInsertSql(TEST_DATA_INSERT_QUERY, queryParams)
    }

}
