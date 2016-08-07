package com.athena.mis.integration.beacon

import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Company

public abstract class BeaconPluginConnector extends PluginConnector {

    public static final int PLUGIN_ID = 18
    public static final String PLUGIN_PREFIX = "BCON"
    public static final String PLUGIN_NAME = "BEACON";

    public abstract void bootStrap(Company company)


}