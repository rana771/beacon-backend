package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.CheckRoleTagLibActionService
import com.athena.mis.application.actions.taglib.CheckRoleTypeTagLibActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.session.AppSessionService
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.web.access.GrailsWebInvocationPrivilegeEvaluator
import org.springframework.beans.factory.annotation.Autowired

/**
 * AccessControlTagLib methods check the user accessibility for given url(s)
 */

class AccessControlTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    SpringSecurityService springSecurityService
    CheckRoleTypeTagLibActionService checkRoleTypeTagLibActionService
    CheckRoleTagLibActionService checkRoleTagLibActionService
    @Autowired
    GrailsWebInvocationPrivilegeEvaluator grailsWebInvocationPrivilegeEvaluator
    @Autowired
    AppSessionService appSessionService

    private static final String METHOD_GET = 'GET'
    private static final String STR_URLS = 'urls'
    private static final String COMMA = ','
    private static final String EMPTY_SPACE = ''
    private static final String SPACE_CHARACTER = "\\s"

    /**
     * Renders the body if any of the the specified urls are granted
     * parameter attr takes the attribute 'urls' as comma separated urls
     * example: <app:ifAnyUrl urls="/controllerName1/action1,/controllerName2/action2"></app:ifAnyUrl>
     *
     * @attr urls REQUIRED the comma separated urls
     */
    def ifAnyUrl = { attrs, body ->

        if (!springSecurityService.isLoggedIn()) {
            out << EMPTY_SPACE
            return
        }
        String strUrls = attrs.remove(STR_URLS)
        strUrls = strUrls.replaceAll(SPACE_CHARACTER, EMPTY_SPACE)

        if (strUrls.size() <= 0) {
            out << EMPTY_SPACE
            return
        }

        List<String> lstUrls = strUrls.split(COMMA);
        for (int i = 0; i < lstUrls.size(); i++) {
            String url = lstUrls[i]
            if (url.length() == 0) {
                continue
            }
            if (hasAccess(url)) {
                out << body()
                break
            }
        }
    }

    /**
     * Renders the body if all of the the specified urls are granted
     * parameter  attr takes the attribute 'urls' as comma separated urls
     * example: <app:ifAllUrl urls="/controllerName1/action1,/controllerName2/action2"></app:ifAllUrl>
     *
     * @attr urls REQUIRED the comma separated urls
     */
    def ifAllUrl = { attrs, body ->
        if (!springSecurityService.isLoggedIn()) {
            out << EMPTY_SPACE
            return
        }
        String strUrls = attrs.remove(STR_URLS)
        strUrls = strUrls.replaceAll(SPACE_CHARACTER, EMPTY_SPACE)

        if (strUrls.size() <= 0) {
            out << EMPTY_SPACE
            return
        }
        boolean allGranted = true
        List<String> lstUrls = strUrls.split(COMMA);
        for (int i = 0; i < lstUrls.size(); i++) {
            String url = lstUrls[i]
            if (url.length() == 0) {
                out << EMPTY_SPACE
                return
            }
            if (!hasAccess(url)) {
                allGranted = false
                break
            }
        }
        if (allGranted) {
            out << body()
        }
    }

    /**
     * Renders the body if logged-in user has role of given type
     * parameter  attr takes the attribute 'id' as ReservedRole.id or comma separated id
     * example: <app:hasRoleType id="${ReservedRoleService.ROLE_TYPE_APP_DEVELOPMENT},${ReservedRoleService.ROLE_TYPE_APP_ADMIN}"></app:hasRoleType>
     * @attr id REQUIRED the id of ReservedRole domain
     */

    def hasRoleType = { attrs, body ->
        attrs.body = body
        super.executeTag(checkRoleTypeTagLibActionService, attrs)
        Boolean hasRoleType = (boolean) attrs.hasRole
        if (hasRoleType.booleanValue()) {
            out << body()
        } else {
            out << EMPTY_SPACE
        }
    }

    /**
     * Renders the body if logged-in user has given role
     * parameter  attr takes the attribute 'key' as sysConfig.key
     * example: <app:hasRole key="${AppSysConfigurationCacheUtility.APP_SYS_CONFIG}"></app:hasRole>
     *
     * @attr key REQUIRED the key of sysConfig
     * @attr pluginId REQUIRED the id of plugin
     */

    def hasRole = { attrs, body ->
        attrs.body = body
        super.executeTag(checkRoleTagLibActionService, attrs)
        Boolean hasRole = (boolean) attrs.hasRole
        if (hasRole.booleanValue()) {
            out << body()
        } else {
            out << EMPTY_SPACE
        }
    }

    /**
     * Renders the body depending on the property isPowerUser and/or isConfigManger of logged-in user
     * example: <app:checkSystemUser isPowerUser="true" isConfigManager="false"></app:checkSystemUser>
     *
     * @attr isPowerUser -true/false
     * @attr isConfigManager -true/false
     */
    def checkSystemUser = { attrs, body ->
        AppUser user = appSessionService.getAppUser()
        if (attrs.isPowerUser) {
            boolean isPowerUser = Boolean.parseBoolean(attrs.isPowerUser)
            if (user.isPowerUser != isPowerUser) {
                out << EMPTY_SPACE
                return
            }
        }
        if (attrs.isConfigManager) {
            boolean isConfigManager = Boolean.parseBoolean(attrs.isConfigManager)
            if (user.isConfigManager != isConfigManager) {
                out << EMPTY_SPACE
                return
            }
        }
        out << body()
    }

    /**
     * Check url access for a given authentication
     * @param url - the url to check access
     * @return - true(if has access), false(otherwise)
     */
    private boolean hasAccess(String url) {
        def auth = springSecurityService.authentication
        String method = METHOD_GET
        return grailsWebInvocationPrivilegeEvaluator.isAllowed(request.contextPath, url, method, auth)
    }

}
