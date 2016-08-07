package com.athena.mis.integration.document

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity

public abstract class DocumentPluginConnector extends PluginConnector {

    public static final int PLUGIN_ID = 13
    public static final String PLUGIN_PREFIX = "DOC"
    public static final String PLUGIN_NAME = "Document"

    public abstract void bootStrap(Company company)

    // get object of sysConfiguration
    public abstract SysConfiguration readSysConfig(String key, long companyId)

    public abstract int getPluginVersion()

    public abstract void initDocumentSysConfigCacheService(String key)

    public abstract long getSystemEntityTypeAnswer()

    public abstract long getSystemEntityTypeDifficultyLevel()

    public abstract long getSystemEntityTypeDocumentStatus()

    public abstract long getSystemEntityTypeDocumentIndex()

    public abstract long getSystemEntityTypeDocumentCategory()

    public abstract void initByType(long systemEntityTypeId)

    public abstract List listByIsActive(long systemEntityTypeId, long companyId)

    public abstract SystemEntity readByReservedId(long reservedId, long typeId, long companyId)

    // read object of DocDocument
    public abstract Object readDocDocumentVersion(long id)

    // read object of DocSubCategory
    public abstract Object readDocSubCategory(long entityId)

    public abstract int countDataIndexByDbInstanceId(long appDbInstanceId)

    // create test data
    public abstract void loadTestData(long companyId, long systemUserId)

    // delete test data
    public abstract void deleteTestData()

    // render common modal html
    public abstract String renderModalHtml()

    // sys config list for grid
    public abstract Map sysConfigListForGrid(BaseService baseService, long companyId)

    // sys config search list for grid
    public abstract Map sysConfigSearchListForGrid(BaseService baseService, long companyId)
}
