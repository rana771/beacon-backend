package com.athena.mis.application.config

import com.athena.mis.CacheUtility
import com.athena.mis.PluginConnector
import com.athena.mis.application.service.SysConfigurationService

class AppSysConfigCacheService extends CacheUtility {

    public static final String DEFAULT_PASSWORD_EXPIRE_DURATION = "mis.application.defaultPasswordExpireDuration"

    // check if application is in maintenance mode. true/ false
    public static final String IS_MAINTENANCE_MODE = "mis.application.isMaintenanceMode"

    // application native ip address
    public static final String NATIVE_IP_ADDRESS = "mis.application.nativeIpAddress"

    public static final String DOC_SUPPORTED_EXTENSIONS = 'mis.application.supportedExtensions'

    // this config enforce new release after a release span is ended
    public static final String ENFORCE_RELEASE_VERSION = "mis.application.enforceReleaseVersion"

    // check if app user registration is enabled. true = enabled, false = disabled
    public static final String ENABLE_NEW_USER_REGISTRATION = "mis.application.enableNewUserRegistration"

    // role id to map with app user at the time of registration
    public static final String ROLE_ID_FOR_APP_USER_REGISTRATION = "mis.application.roleIdForUserRegistration"

    // phone pattern
    public static final String APPLICATION_PHONE_PATTERN = "mis.application.phonePattern"

    // Application deployment mode
    public static final String APPLICATION_DEPLOYMENT_MODE = "mis.application.deploymentMode"

    // Application sms url
    public static final String APPLICATION_SMS_URL = "mis.application.smsUrl"

    // Application attachment size
    public static final String APPLICATION_ATTACHMENT_SIZE = "mis.application.attachmentSize"

    public static final String DEFAULT_PLUGIN = "mis.application.defaultPlugin"

    // Determine application menu show or not. default value true, means show
    public static final String APP_SHOW_APPLICATION_MENU = "mis.application.showApplicationMenu"

    SysConfigurationService sysConfigurationService

    public void init() {
        List list = sysConfigurationService.listByPlugin(PluginConnector.PLUGIN_ID)
        super.setListByCompanyId(list)
    }
}
