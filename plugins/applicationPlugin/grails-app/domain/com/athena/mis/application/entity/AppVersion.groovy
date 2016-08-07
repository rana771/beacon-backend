package com.athena.mis.application.entity

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> Application initiate version check using this domain
 * and save release version history for each installed plugin
 * </p>
 *
 */
class AppVersion {
    long id             // Object id (auto generated)
    int span            // release time span
    int pluginId        // Plugin id (For example: PluginConnector.APPLICATION_ID)
    int releaseNo       // release number
    Date releasedOn     // release date
    boolean isCurrent   // if current release
    String pluginName   // name of plugin
    String pluginPrefix // prefix of plugin

    static mapping = {
        version false
        releasedOn type: 'date'

        // unique index on "release_no" and "plugin_id" using AppVersionService.createDefaultSchema()
        // <domain_name><property_name_1><property_name_2>idx
    }
}
