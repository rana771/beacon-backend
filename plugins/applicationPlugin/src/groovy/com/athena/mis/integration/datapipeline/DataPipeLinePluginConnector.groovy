package com.athena.mis.integration.datapipeline

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SystemEntity

public abstract class DataPipeLinePluginConnector extends PluginConnector {

    public static final int PLUGIN_ID = 14
    public static final String PLUGIN_PREFIX = "DPL"
    public static final String PLUGIN_NAME = "DataPipeLine"

    public abstract void bootStrap(Company company)

    // get object of sysConfiguration
    public abstract Object readSysConfig(String key, long companyId)

    public abstract int getPluginVersion()

    public abstract long getSystemEntityTypeDataFeed()

    public abstract long getSystemEntityTypeDataFeedCsv()

    public abstract long getSystemEntityTypeDataFeedText()

    public abstract long getSystemEntityTypeDataFeedStatus()

    public abstract void initByType(long systemEntityTypeId)

    public abstract List listByIsActive(long systemEntityTypeId, long companyId)

    public abstract SystemEntity readByReservedId(long reservedId, long typeId, long companyId)

    public abstract void initDataPipeLineSysConfigCacheService(String key)

    public abstract int countDplDataExportByDbInstanceId(long appDbInstanceId)

    public abstract int countDplDataExportByTargetDbInstanceId(long appDbInstanceId)

    public abstract int countDplDataImportByDbInstance(long appDbInstanceId)

    public abstract int countDplCdcMySqlBySourceDbInstanceId(long appDbInstanceId)

    public abstract int countDplCdcMySqlByTargetDbInstanceId(long appDbInstanceId)

    public abstract List findAllByDplCdcId(long cdcId)

    public abstract List findAllByDplDataExportId(long dplDataExportId)

    public abstract List findAllByDplDataExportIdAndTableNameIlike(long dplDataExportId, String tblName)

    public abstract List findAllByDplDataImportId(long dplDataImportId)
    // create test data
    public abstract void loadTestData(long companyId)

    public abstract List listTableNameForCdc(long dplCdcId)

    public abstract Object readDplDataExport(long id)

    public abstract Object readDplDataImport(long id)

    // render common modal html
    public abstract String renderModalHtml()

    // sys config list for grid
    public abstract Map sysConfigListForGrid(BaseService baseService, long companyId)

    // sys config search list for grid
    public abstract Map sysConfigSearchListForGrid(BaseService baseService, long companyId)
}
