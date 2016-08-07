package com.athena.mis.integration.ictpool

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.SystemEntity

public abstract class IctPoolPluginConnector extends PluginConnector {

    public static final int PLUGIN_ID = 17
    public static final String PLUGIN_PREFIX = "ICT"
    public static final String PLUGIN_NAME = "ICTPOOL";

    public abstract void bootStrap(Company company)


}