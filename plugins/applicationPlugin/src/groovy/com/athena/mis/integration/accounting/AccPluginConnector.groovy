package com.athena.mis.integration.accounting

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity

public abstract class AccPluginConnector extends PluginConnector {

    public static final int PLUGIN_ID = 2
    public static final String PLUGIN_PREFIX = "ACC"
    public static final String PLUGIN_NAME = "Accounting";

    // Return the Accounting Plugin version
    public abstract int getPluginVersion()

    public abstract long getSystemEntityTypeSource()

    public abstract long getSystemEntityTypeVoucher()

    public abstract long getSystemEntityTypeInstrument()

    public abstract void initByType(long systemEntityTypeId)

    public abstract List listByIsActive(long systemEntityTypeId, long companyId)

    public abstract SystemEntity readByReservedId(long reservedId, long typeId, long companyId)

    // Return the financial year Object by id
    public abstract Object readFinancialYear(long id)

    // get source type supplier id
    public abstract long getAccSourceTypeSupplier()

    // get source type customer id
    public abstract long getAccSourceTypeCustomer()

    // get source type customer id
    public abstract long getAccSourceTypeItem()

    // get source type customer id
    public abstract long getAccSourceTypeEmployee()

    //get voucher balance by purchase order id
    public abstract double getTotalAmountByPurchaseOrderId(long purchaseOrderId)

    //get current financial year
    public abstract Object getCurrentFinancialYear()

    // get instrument type po
    public abstract long getInstrumentTypePurchaseOrder()

    //init Account SysConfiguration
    public abstract void initAccSysConfiguration()

    // update content count for financial year during create, update and delete content
    public abstract Object updateContentCountForFinancialYear(long financialYearId, int count)

    // get Indent-List-By-ProjectId
    public abstract List getIntendListByProjectId(long projectId)

    public abstract void bootStrap(Company company)

    public abstract void createDefaultData(long companyId)

    public abstract void createDefaultDataForAccGroup(long companyId, long systemUserId)

    public abstract SysConfiguration readSysConfiguration(String key)

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