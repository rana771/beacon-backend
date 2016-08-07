package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.CheckSysConfigTagLibActionService
import com.athena.mis.application.actions.taglib.GetSysConfigLoginTemplateActionService
import com.athena.mis.application.actions.taglib.GetSysConfigUserRegistrationActionService
import com.athena.mis.application.actions.taglib.ShowSysConfigTagLibActionService

class SystemConfigurationTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    ShowSysConfigTagLibActionService showSysConfigTagLibActionService
    CheckSysConfigTagLibActionService checkSysConfigTagLibActionService
    GetSysConfigUserRegistrationActionService getSysConfigUserRegistrationActionService
    GetSysConfigLoginTemplateActionService getSysConfigLoginTemplateActionService

    /**
     * Renders the value if system configuration exists
     * example: <app:showSysConfig key="key" pluginId="1"></app:showSysConfig>
     *
     * @attr key REQUIRED - key of system configuration
     * @attr pluginId REQUIRED - plugin id
     * @attr companyId - company id
     */
    def showSysConfig = { attrs, body ->
        attrs.body = body
        super.executeTag(showSysConfigTagLibActionService, attrs)
        out << (String) attrs.html
    }

    /**
     * Renders the internal content (link to user registration page)
     * if Exh Module installed & if new user registration is enabled in Exh sysConfig
     */
    def isUserRegistrationEnabled = { attrs, body ->
        attrs.body = body
        attrs.request = request
        super.executeTag(getSysConfigUserRegistrationActionService, attrs)
        out << (String) attrs.html
    }

    /**
     * Renders the body if system configuration exists
     * example: <app:checkSysConfig key="key" value="value" pluginId="1"></app:checkSysConfig>
     *
     * @attr key REQUIRED - key of system configuration
     * @attr value REQUIRED - value of system configuration
     * @attr pluginId REQUIRED - plugin id of sysConfiguration
     */
    def checkSysConfig = { attrs, body ->
        attrs.body = body
        attrs.request = request
        Map resultMap = (Map) checkSysConfigTagLibActionService.execute(attrs)
        Boolean hasSysConfig = (boolean) resultMap.hasSysConf
        if (hasSysConfig.booleanValue()) {
            out << body()
        } else {
            out << EMPTY_SPACE
        }
    }

    /**
     * Renders the body of login form
     */
    def checkDeploymentModeForLoginForm = { attrs, body ->
        attrs.body = body
        attrs.request = request
        super.executeTag(getSysConfigLoginTemplateActionService, attrs)
        out << (String) attrs.html
    }
}
