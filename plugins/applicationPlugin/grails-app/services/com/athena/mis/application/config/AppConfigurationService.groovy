package com.athena.mis.application.config

import org.codehaus.groovy.grails.commons.GrailsApplication

// application config service
class AppConfigurationService {

    GrailsApplication grailsApplication
    AppSysConfigCacheService appSysConfigCacheService

    private int appVersion = -1
    private String reportDirectory = null
    private String imageDir = null

    // get plugin version
    public int getAppPluginVersion() {
        if (appVersion > 0) return appVersion

        String version = grailsApplication.config.applicationConfig.version
        appVersion = Integer.parseInt(version)
        return appVersion
    }

    // get plugin report directory
    public String getAppReportDirectory() {
        if (reportDirectory) return reportDirectory

        String dir = grailsApplication.config.applicationConfig.directory
        File file = grailsApplication.parentContext.getResource(dir).file
        reportDirectory = file.absolutePath
        return reportDirectory
    }

    // get plugin report directory
    public String getAppImageDir() {
        if (imageDir) return imageDir
        String dir = grailsApplication.config.applicationConfig.images
        File file = grailsApplication.parentContext.getResource(dir).file
        imageDir = file.absolutePath
        return imageDir
    }
}
