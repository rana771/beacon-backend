package com.athena.mis.integration.budget

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SystemEntity

public abstract class BudgPluginConnector extends PluginConnector {

    public static final int PLUGIN_ID = 3
    public static final String PLUGIN_PREFIX = "BUDG"
    public static final String PLUGIN_NAME = "Budget";

    // Return the Budget Plugin version
    public abstract int getPluginVersion()

    public abstract long getSystemEntityTypeBudgetTaskStatus()

    public abstract void initByType(long systemEntityTypeId)

    public abstract List listByIsActive(long systemEntityTypeId, long companyId)

    public abstract SystemEntity readByReservedId(long reservedId, long typeId, long companyId)

    // Return the budget sprint Object by id
    public abstract Object readBudgSprint(long id)

    // Return the budget Object by id
    public abstract Object readBudget(long id)

    // Return the budgetDetails Object by id
    public abstract Object readBudgetDetails(long projectId, long itemId)

    // Return budget scope Object by id
    public abstract Object readBudgetScope(long id)

    // Search budget by budgetLineItem
    public abstract Object searchByBudgetItem(String budgetLineItem)

    // Read budget by budgetLineItem
    public abstract Object readByBudgetItem(String budgetLineItem)

    // Read budget by budgetId and itemId
    public abstract Object readBudgetDetailsByBudgetAndItem(long budgetId, long itemId)

    // update content count for budget during create, update and delete content for budget
    public abstract Object updateContentCountForBudget(long budgetId, int count)

    public abstract void bootStrap(Company company)

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