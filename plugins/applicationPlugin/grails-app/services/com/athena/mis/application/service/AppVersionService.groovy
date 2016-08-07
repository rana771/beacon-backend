package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppVersion
import com.athena.mis.application.entity.SysConfiguration

class AppVersionService extends BaseDomainService {

    AppConfigurationService appConfigurationService

    private static final String CONFIG_NOT_FOUND = "System configuration for release version enforcement is not found"
    private static
    final String DEFAULT_ERR_MSG = "Application is out of date. Please consider updating to latest version"

    @Override
    public void init() {
        domainClass = AppVersion.class
    }

    public AppVersion create(AppVersion appVersion) {
        return appVersion.save()
    }

    public static final String SQL_UPDATE = """
        UPDATE app_version SET
        is_current = :isCurrent
        WHERE id = :id
    """

    public int update(AppVersion appVersion) {
        Map queryParams = [id: appVersion.id, isCurrent: appVersion.isCurrent]
        int updateCount = executeUpdateSql(SQL_UPDATE, queryParams)
        return updateCount
    }

    public static final String SQL_UPDATE_IS_CURRENT_FALSE = """
        UPDATE app_version SET
        is_current = false
        WHERE plugin_id = :pluginId
        AND release_no = :releaseNo
    """

    public int updateIsCurrentFalse(int pluginId, int releaseNo) {
        Map queryParams = [pluginId: pluginId, releaseNo: releaseNo]
        int updateCount = executeUpdateSql(SQL_UPDATE_IS_CURRENT_FALSE, queryParams)
        return updateCount
    }

    public AppVersion findByPluginIdAndIsCurrent(int pluginId, boolean isCurrent) {
        return AppVersion.findByPluginIdAndIsCurrent(pluginId, isCurrent, [readOnly: true])
    }

    public int countByPluginId(int pluginId) {
        return AppVersion.countByPluginId(pluginId, [readOnly: true])
    }

    public String checkVersionExpiration(int pluginId) {
        SysConfiguration versionConfig = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(
                appConfigurationService.appSysConfigCacheService.ENFORCE_RELEASE_VERSION, super.companyId)
        if (!versionConfig) return CONFIG_NOT_FOUND
        boolean enforceCheck = Boolean.parseBoolean(versionConfig.value)
        if (!enforceCheck) return null
        String errMsg = DEFAULT_ERR_MSG
        if (versionConfig.message && !versionConfig.message.equals(EMPTY_SPACE)) errMsg = versionConfig.message
        AppVersion appVersion = findByPluginIdAndIsCurrent(pluginId, Boolean.TRUE)
        if (!appVersion) return errMsg
        Date dateToday = new Date()
        Date versionExpiryDate = appVersion.releasedOn + appVersion.span
        if (dateToday > versionExpiryDate) {
            return errMsg
        }
        return null
    }

    public boolean registerPlugin(int pluginId, int configVersion, String pluginName, String pluginPrefix) {
        AppVersion appVersion = new AppVersion()
        appVersion.isCurrent = true
        appVersion.span = 90
        appVersion.pluginId = pluginId
        appVersion.releaseNo = configVersion
        appVersion.releasedOn = new Date()
        appVersion.pluginName = pluginName
        appVersion.pluginPrefix = pluginPrefix
        create(appVersion)
        return true
    }

    @Override
    public void createDefaultSchema() {
        String sqlIndex = "CREATE UNIQUE INDEX app_version_release_no_plugin_id_idx ON app_version(release_no,plugin_id);"
        executeSql(sqlIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
