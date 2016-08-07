package com.athena.mis.application.deployment

import com.athena.mis.ActionServiceIntf
import com.athena.mis.AppUpdatePatch
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppVersion
import com.athena.mis.application.service.AppVersionService
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.GrailsApplication

class AppUpdatePatchExecutorService extends AppUpdatePatch implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    GrailsApplication grailsApplication
    AppVersionService appVersionService
    AppConfigurationService appConfigurationService

    private static final String SQL_LST_COMPANY_ID = "SELECT id FROM company"
    private static final String LST_APP_UPDATE_PATCH = "lstAppUpdatePatch"
    private static final String RELEASE_NO = "releaseNo"
    private static final String CONFIG_CURRENT_VERSION = "configCurrentVersion"

    /**
     * dummy implementation because of extending AppUpdatePatch
     * @return
     */
    public Date getReleaseDate() { return null }

    public void init() {
        Map result = new LinkedHashMap()
        executePreCondition(result)
        if (!result.isError) execute(result)
        if (!result.isError) executePostCondition(result)
    }

    /**
     * build list of app update patch and list of app version
     * @param result
     * @return
     */
    Map executePreCondition(Map result) {
        try {
            int configCurrentVersion = appConfigurationService.getAppPluginVersion()

            List<GroovyRowResult> lstCompanyIdResult = executeSelectSql(SQL_LST_COMPANY_ID)
            List<Long> lstCompanyId = lstCompanyIdResult.collect { it.id }
            List lstAppUpdatePatch = []

            String sqlVersion = """SELECT release_no FROM app_version where plugin_id = ${
                PluginConnector.PLUGIN_ID
            } AND is_current = true"""
            List<GroovyRowResult> lstVersion = executeSelectSql(sqlVersion)
            int releaseNo = lstVersion[0].release_no

            for (int i = 0; i < lstCompanyId.size(); i++) {
                long companyId = lstCompanyId[i]
                int dbCurrentVersion = releaseNo
                if (dbCurrentVersion == configCurrentVersion) {   // already sync
                    continue
                } else if (dbCurrentVersion > configCurrentVersion) { // Back-dated war deployed
                    String errorMsg = "Back-dated Plugin(Application) Deployed. Plugin ver.=${configCurrentVersion} & DB ver.=${dbCurrentVersion}"
                    log.error(errorMsg)
                    println(errorMsg)
                    result.put(IS_ERROR, Boolean.TRUE)
                    return result
                } else {
                    for (int j = dbCurrentVersion; j < configCurrentVersion; j++) {
                        AppUpdatePatch currPatch = (AppUpdatePatch) grailsApplication.getMainContext().getBean("appUpdatePatch${j + 1}Service")
                        lstAppUpdatePatch << [companyId: companyId, updatePatch: currPatch]
                    }
                }
            }
            result.put(IS_ERROR, Boolean.FALSE)
            result.put(RELEASE_NO, releaseNo)
            result.put(LST_APP_UPDATE_PATCH, lstAppUpdatePatch)
            result.put(CONFIG_CURRENT_VERSION, configCurrentVersion)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * execute all update patch built from pre-condition
     * @param result
     * @return
     */
    Map execute(Map result) {
        try {
            List lstAppUpdatePatch = (List) result.get(LST_APP_UPDATE_PATCH)
            lstAppUpdatePatch.each {
                AppUpdatePatch updatePatch = it.updatePatch
                long companyId = it.companyId
                updatePatch.init()
                updatePatch.execute(companyId)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. clear all update patch sql list
     * 2. update app version object is current = false
     * 3. create new app version object with latest release no
     * @param result
     * @return
     */
    Map executePostCondition(Map result) {
        try {
            List lstAppUpdatePatch = (List) result.get(LST_APP_UPDATE_PATCH)
            int configCurrentVersion = (int) result.get(CONFIG_CURRENT_VERSION)
            int releaseNo = (int) result.get(RELEASE_NO)

            lstAppUpdatePatch.each {
                AppUpdatePatch updatePatch = it.updatePatch
                updatePatch.lstAppSql.clear()
            }

            if (releaseNo < configCurrentVersion) {
                appVersionService.updateIsCurrentFalse(PluginConnector.PLUGIN_ID, releaseNo)
            }

            int size = configCurrentVersion - releaseNo
            int version = releaseNo + 1
            boolean isCurrent = false
            for (int i = 0; i < size; i++) {
                if (version == configCurrentVersion) {
                    isCurrent = true
                }
                AppVersion newAppVersion = buildNewAppVersion(version, isCurrent)
                appVersionService.create(newAppVersion)
                version++
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    //do nothing
    Map buildSuccessResultForUI(Map result) {
        return null
    }

    // do nothing
    Map buildFailureResultForUI(Map result) {
        return null
    }

    /**
     * build new app version object
     * @param version - version
     * @param isCurrent - true/false
     * @return - object of AppVersion
     */
    private AppVersion buildNewAppVersion(int version, boolean isCurrent) {
        AppUpdatePatch updatePatch = (AppUpdatePatch) grailsApplication.getMainContext().getBean("appUpdatePatch${version}Service")
        AppVersion appVersion = new AppVersion()
        appVersion.isCurrent = isCurrent
        appVersion.span = RELEASE_SPAN
        appVersion.pluginId = PluginConnector.PLUGIN_ID
        appVersion.releaseNo = version
        appVersion.releasedOn = updatePatch.getReleaseDate()
        appVersion.pluginName = PluginConnector.PLUGIN_NAME
        appVersion.pluginPrefix = PluginConnector.PLUGIN_PREFIX
        return appVersion
    }
}
