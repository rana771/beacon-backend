package com.athena.mis.integration.application

import com.athena.mis.PluginConnector

class ApplicationConnectorService extends PluginConnector {

    static transactional = false
    static lazyInit = false

    // registering plugin in servletContext
    @Override
    public boolean initialize() {
        setPlugin(PLUGIN_NAME, this)
        return true
    }

    @Override
    public String getName() {
        return PLUGIN_NAME
    }

    @Override
    public int getId() {
        return PLUGIN_ID
    }

    @Override
    String getPrefix() {
        return PLUGIN_PREFIX
    }
}
