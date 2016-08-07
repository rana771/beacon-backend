package com.athena.mis.integration.arms

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity

public abstract class ArmsPluginConnector extends PluginConnector {

    public static final String PLUGIN_NAME = "ARMS"
    public static final int PLUGIN_ID = 11
    public static final String PLUGIN_PREFIX = "ARMS"

    public abstract int getPluginVersion()

    public abstract void initByType(long typeId)

    public abstract List<SystemEntity> listByIsActive(long typeId, long companyId)

    public abstract SystemEntity readByReservedId(long reservedId, long typeId, long companyId)

    public abstract void bootStrap(Company company)

    public abstract Object readByExchangeHouseId(long exhId)

    public abstract void initSession()

    public abstract Object getProcessTypeIssue()

    public abstract Object getProcessTypeForward()

    public abstract Object getProcessTypePurchase()

    public abstract Object getRmsProcessInstrumentMapping(long processType, long instrumentType)

    public abstract void initSysConfigCacheUtility()

    public abstract SysConfiguration readSysConfig(String key, long companyId)

    public abstract void loadTestData(long companyId, long systemUserId)

    public abstract void deleteTestData()

    public abstract long getSystemEntityInstrumentType()

    public abstract long getSystemEntityPaymentMethod()

    public abstract long getSystemEntityProcessType()

    public abstract long getSystemEntityTaskStatus()

    // render common modal html
    public abstract String renderModalHtml()

    // sys config list for grid
    public abstract Map sysConfigListForGrid(BaseService baseService, long companyId)

    // sys config search list for grid
    public abstract Map sysConfigSearchListForGrid(BaseService baseService, long companyId)
}
