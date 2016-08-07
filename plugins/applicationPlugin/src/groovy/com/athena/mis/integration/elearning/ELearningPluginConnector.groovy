package com.athena.mis.integration.elearning

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity

public abstract class ELearningPluginConnector extends PluginConnector {

    public static final int PLUGIN_ID = 15
    public static final String PLUGIN_PREFIX = "EL"
    public static final String PLUGIN_NAME = "ELearning"

    public abstract void bootStrap(Company company)

    // get object of sysConfiguration
    public abstract SysConfiguration readSysConfig(String key, long companyId)

    public abstract int getPluginVersion()

    public abstract void initByType(long typeId)

    public abstract List<SystemEntity> listByIsActive(long typeId, long companyId)

    public abstract SystemEntity readByReservedId(long reservedId, long typeId, long companyId)

    // create test data
    public abstract void loadTestData(long companyId, long systemUserId)

    // delete test data
    public abstract void deleteTestData()

    public abstract long getSystemEntityTypeLanguage()

    public abstract long getSystemEntityTypeAssignment()

    public abstract int countElCourseByDocCategoryId(long docCategoryId, long companyId)

    // count exam question mapping
    public abstract int countByQuestionIdAndCompanyId(long questionId, long companyId)

    public abstract int countByQuizIdAndCompanyId(long questionId, long companyId)

    public abstract int countElLessonBySubCategoryId(long subCategoryId, long companyId)

    // sys config list for grid
    public abstract Map sysConfigListForGrid(BaseService baseService, long companyId)

    // sys config search list for grid
    public abstract Map sysConfigSearchListForGrid(BaseService baseService, long companyId)

    public abstract void initElearningSysConfigCacheService(String key)
}
