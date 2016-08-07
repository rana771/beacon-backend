package com.athena.mis.integration.qsmeasurement

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company

public abstract class QsPluginConnector extends PluginConnector {

    public static final int PLUGIN_ID = 6
    public static final String PLUGIN_PREFIX = "QS";
    public static final String PLUGIN_NAME = "QS";

    // Return the QS Plugin version
    public abstract int getPluginVersion()

    // Get count of QsMeasurement by budget id
    public abstract int countQSMeasurementByBudgetId(long budgetId)

    // Get sum of QsMeasurement by budget id
    public abstract double getQsSumOfBudget(long budgetId, boolean isGovt)

    // Get gross receivable amount of QsMeasurement(both internal and govt.) by project id
    public abstract Map getGrossReceivableQs(long projectId)

    public abstract void bootStrap(Company company)

    // sys config list for grid
    public abstract Map sysConfigListForGrid(BaseService baseService, long companyId)

    // sys config search list for grid
    public abstract Map sysConfigSearchListForGrid(BaseService baseService, long companyId)
}