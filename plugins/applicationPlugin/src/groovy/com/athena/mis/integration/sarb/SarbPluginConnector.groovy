package com.athena.mis.integration.sarb

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity

public abstract class SarbPluginConnector extends PluginConnector {

    public static final String PLUGIN_NAME = "SARB"
    public static final int PLUGIN_ID = 12
    public static final String PLUGIN_PREFIX = "SARB"

    public abstract int getPluginVersion()

    // init list after create, update and delete
    public abstract void initByType(long typeId)

    // get list of active SystemEntity object
    public abstract List<SystemEntity> listByIsActive(long typeId, long companyId)

    // read SystemEntity object by reserved id
    public abstract SystemEntity readByReservedId(long reservedId, long typeId, long companyId)

    // init bootstrap of sarb plugin
    public abstract void bootStrap(Company company)

    public abstract List provinceList(long companyId)

    public abstract boolean createSarbCustomerDetails(Object params, long customerId)

    public abstract boolean updateSarbCustomerDetails(Object params)

    public abstract boolean createSarbCustomerDetailsTrace(Object params)

    public abstract Object readSarbCustomerDetails(long customerId)

    public abstract String validatePhotoIdNo(String photoIdNo, String customerPhotoTypeCode, String countryCode)

    public abstract void initSarbSysConfiguration()

    public abstract String validateTaskDetails(String remittancePurposeCode, photoIdTypeCode)

    public abstract SysConfiguration readSysConfig(String key, long companyId)

    public abstract long getSystemEntityReviseTask()

    // sys config list for grid
    public abstract Map sysConfigListForGrid(BaseService baseService, long companyId)

    // sys config search list for grid
    public abstract Map sysConfigSearchListForGrid(BaseService baseService, long companyId)
}
