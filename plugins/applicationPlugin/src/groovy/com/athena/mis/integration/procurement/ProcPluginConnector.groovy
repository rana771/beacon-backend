package com.athena.mis.integration.procurement

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity
import groovy.sql.GroovyRowResult

public abstract class ProcPluginConnector extends PluginConnector {

    public static final int PLUGIN_ID = 5
    public static final String PLUGIN_PREFIX = "PROC";
    public static final String PLUGIN_NAME = "Procurement";

/*    // Return the PR Object by id
    Object readPR(long id)*/

    // Return the Procurement Plugin version
    public abstract int getPluginVersion()

    // Return the PO Object by id
    public abstract Object readPO(long id)

    // Return the PO Details Object by id
    public abstract Object readPODetails(long id)

    //return List of GroovyRowResult of PO by supplier and projects  (for Create in StoreIn from supplier)
    public abstract List listPOBySupplierIdAndProjectId(long supplierId, long projectId)

    //return List of GroovyRowResult of PO by supplier and projects   (for Edit in StoreIn from supplier)
    public abstract List listPOBySupplierIdAndProjectIdForEdit(long supplierId, long purchaseOrderId, long projectId)

    public abstract List<GroovyRowResult> listPOItemByPurchaseOrder(long purchaseOrderId)

    //update storeInQuantity in po details For Create StoreInDetails
    public abstract Object updateStoreInQuantityForPODetails(Object purchaseOrderDetailsInstance)

    //read PurchaseOrderDetails GroovyRowResult object by purchaseOrderId and materialId
    public abstract Object readPODetailsByPurchaseOrderAndItem(long purchaseOrderId, long itemId)

    //get po list of fixed asset
    public abstract List getPOListOfFixedAsset()

    //get fixed asset list of PO
    public abstract List<GroovyRowResult> getFixedAssetListByPOId(long poId)

    public abstract void bootStrap(Company company)

    //init SysConfiguration
    public abstract void initProcSysConfiguration()

    public abstract SysConfiguration readSysConfiguration(String key)

    // create test data
    public abstract void loadTestData(long companyId, long systemUserId)

    // delete test data
    public abstract void deleteTestData()

    public abstract List<SystemEntity> listByIsActive(long typeId, long companyId)

    // render common modal html
    public abstract String renderModalHtml()

    // sys config list for grid
    public abstract Map sysConfigListForGrid(BaseService baseService, long companyId)

    // sys config search list for grid
    public abstract Map sysConfigSearchListForGrid(BaseService baseService, long companyId)
}