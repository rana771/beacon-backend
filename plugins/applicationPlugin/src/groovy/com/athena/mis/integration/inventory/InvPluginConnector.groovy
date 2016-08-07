package com.athena.mis.integration.inventory

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity

public abstract class InvPluginConnector extends PluginConnector {

    public static final int PLUGIN_ID = 4
    public static final String PLUGIN_PREFIX = "INV";
    public static final String PLUGIN_NAME = "Inventory";

    // Return the Inventory Plugin version
    public abstract int getPluginVersion()

    public abstract long getSystemEntityTypeInventory()

    public abstract long getSystemEntityTypeProductionLine()

    public abstract long getSystemEntityTypeTransaction()

    public abstract long getSystemEntityTypeTransactionEntity()

    public abstract void initByType(long systemEntityTypeId)

    public abstract List listByIsActive(long systemEntityTypeId, long companyId)

    public abstract SystemEntity readByReservedId(long reservedId, long typeId, long companyId)

    //read inv Inventory Transaction Object by purchaseOrderId
    public abstract Object readInvTransactionByPurchaseOrderId(long purchaseOrderId)

    //read invInventory object by id
    public abstract Object readInventory(long id)

    //get inventory type siteId
    public abstract Long getInventoryTypeSiteId()

    //get consumeItem by budget and item id
    public abstract int getConsumeItemByBudgetAndItemId(long budgetId, long itemId)

    //get total approved consumption of the project
    public abstract String getTotalApprovedConsumptionByProjectId(long projectId)

    //get material stock balance of the project
    public abstract String getMaterialStockBalanceByProjectId(long projectId)

    //get fixed asset stock balance of the project
    public abstract String getFixedAssetStockBalanceByProjectId(long projectId)

    //Get Inventory Transaction Entity Type Supplier
    public abstract Long getTransactionEntityTypeIdSupplier()

    //Get Inventory Transaction Entity Type None
    public abstract Long getTransactionEntityTypeIdNone()

    //Get Inventory Transaction Entity Type Inventory
    public abstract Long getTransactionEntityTypeIdInventory()

    //get InvProductionItemTypeFinishedMaterialId
    public abstract Long getInvProductionItemTypeFinishedMaterialId()

    //init system config
    public abstract void initInvSysConfiguration()

    // get transaction type id In
    public abstract long getInvTransactionTypeIdIn()

    // get transaction type id consumption
    public abstract long getInvTransactionTypeIdConsumption()

    // get transaction type id production
    public abstract long getInvTransactionTypeIdProduction()

    // get transaction type id out
    public abstract long getInvTransactionTypeIdOut()


    public abstract void bootStrap(Company company)

    // init session value
    public abstract void initSession()

    //get list of inventory ids mapped with logged in user
    public abstract List<Long> getUserInventoryIds()

    //give user and inventory mapped inventory objects
    public abstract List getUserInventoriesByType(long typeId, String hintsText, Boolean showHints)

    // count inventory transaction by budget id
    public abstract int countInventoryTransactionByBudgetId(long budgetId)

    public abstract SysConfiguration readSysConfiguration(String key)

    // create test data
    public abstract void loadTestData(long companyId, long systemUserId)

    // delete test data
    public abstract void deleteTestData()

    // sys config list for grid
    public abstract Map sysConfigListForGrid(BaseService baseService, long companyId)

    // sys config search list for grid
    public abstract Map sysConfigSearchListForGrid(BaseService baseService, long companyId)
}