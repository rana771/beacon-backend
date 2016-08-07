package com.athena.mis.application.actions.login

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.CompanyService
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.apache.log4j.Logger
import org.grails.plugin.jcaptcha.JcaptchaService

import javax.servlet.http.HttpServletRequest

class CheckLoginActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    JcaptchaService jcaptchaService
    CompanyService companyService
    AppConfigurationService appConfigurationService
    SpringSecurityService springSecurityService

    private static final String VIEW = "view"
    private static final String CONFIG = "config"
    private static final String POST_URL = "postUrl"
    private static final String COMPANY_ID = "companyId"
    private static final String VIEW_AUTH = "/application/login/auth"
    private static final String FAILED_TO_LOGIN = "Failed to login. Try again"
    private static final String CAPTCHA_ERR_MESSAGE = "Security ID wasn't matched."

    public Map executePreCondition(Map params) {
        try {
            params.put(IS_ERROR, Boolean.FALSE)
            def config = SpringSecurityUtils.securityConfig
            params.put(CONFIG, config)
            if (springSecurityService.isLoggedIn()) {
                params.put(IS_ERROR, Boolean.TRUE)
                return params
            }
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            params.put(IS_ERROR, Boolean.TRUE)
            params.put(MESSAGE, FAILED_TO_LOGIN)
            return params
        }
    }

    public Map execute(Map result) {
        try {
            HttpServletRequest request = (HttpServletRequest) result.request
            String postUrl = "${request.contextPath}${result.config.apf.filterProcessesUrl}"
            result.put(VIEW, VIEW_AUTH)
            result.put(POST_URL, postUrl)
            long companyId = Long.parseLong(result.companyId.toString())
            result.put(COMPANY_ID, companyId)
            String errMsg = checkJCaptcha(request, result)
            if (errMsg) {
                result.put(IS_ERROR, Boolean.TRUE)
                result.put(MESSAGE, result.get(MESSAGE))
                return result
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(IS_ERROR, Boolean.TRUE)
            result.put(MESSAGE, FAILED_TO_LOGIN)
            return result
        }
    }


    public Map executePostCondition(Map result) {
        return result
    }


    public Map buildSuccessResultForUI(Map result) {
        return result
    }


    public Map buildFailureResultForUI(Map result) {
        return result
    }

    private String checkJCaptcha(HttpServletRequest request, Map params) {
        try {
            def config = params.config
            long companyId = Long.parseLong(params.companyId.toString())
            params.put(COMPANY_ID, companyId)
            long deploymentMode = 1L
            SysConfiguration sysConfig = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.APPLICATION_DEPLOYMENT_MODE, companyId)
            if (config) {
                deploymentMode = Long.parseLong(sysConfig.value)
            }
            boolean matched = true      // default value for development mode
            if (deploymentMode != 2) {
                matched = jcaptchaService.validateResponse("image", request.session.id, params.captcha);
            }
            request.session.invalidate();
            // check JCaptcha
            if (!matched) {
                params.put(IS_ERROR, Boolean.TRUE)
                params.put(MESSAGE, CAPTCHA_ERR_MESSAGE)
                return CAPTCHA_ERR_MESSAGE
            }
            return null

        } catch (Exception ex) {
            log.error(ex.getMessage())
            params.put(IS_ERROR, Boolean.TRUE)
            params.put(MESSAGE, CAPTCHA_ERR_MESSAGE)
            return CAPTCHA_ERR_MESSAGE
        }
    }
}
