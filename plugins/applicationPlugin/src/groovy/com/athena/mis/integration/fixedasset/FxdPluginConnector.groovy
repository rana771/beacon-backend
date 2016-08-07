package com.athena.mis.integration.fixedasset

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import groovy.sql.GroovyRowResult

public abstract class FxdPluginConnector extends PluginConnector {

    public static final int PLUGIN_ID = 7
    public static final String PLUGIN_PREFIX = "FXD"
    public static final String PLUGIN_NAME = "FixedAsset";

    // Return the Fixed Asset Plugin version
    public abstract int getPluginVersion()

    // Get FixedAssetDetails object by id
    public abstract Object getFixedAssetDetailsById(long id)

    // Get fixed asset details list by inventory id and item id
    public abstract List getFixedAssetListByInvIdAndItemId(long inventoryId, long itemId)

    // Get fixed asset details list by inventory id
    public abstract List<GroovyRowResult> getFixedAssetListByInvId(long inventoryId)

    public abstract void bootStrap(Company company)

    // sys config list for grid
    public abstract Map sysConfigListForGrid(BaseService baseService, long companyId)

    // sys config search list for grid
    public abstract Map sysConfigSearchListForGrid(BaseService baseService, long companyId)
}