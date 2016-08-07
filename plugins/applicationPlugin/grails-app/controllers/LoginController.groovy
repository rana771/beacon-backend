import com.athena.mis.application.actions.login.*
import com.athena.mis.application.controller.BaseController
import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityService
import grails.plugin.springsecurity.SpringSecurityUtils
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.security.authentication.AuthenticationTrustResolver
import org.springframework.security.core.context.SecurityContextHolder

class LoginController extends BaseController {

    static allowedMethods = [
            auth      : "GET"
    ]

    AuthActionService authActionService
    CheckLoginActionService checkLoginActionService
    AuthFailActionService authFailActionService
    LoginSuccessActionService loginSuccessActionService
    SpringSecurityService springSecurityService
    AuthenticationTrustResolver authenticationTrustResolver
    SignUpActionService signUpActionService
    SwitchUserLoginActionService switchUserLoginActionService

    private static final String SESSION = 'session'
    private static final String REQUEST = 'request'
    private static final String RESPONSE = 'response'

    def noAccess() {
        render(template: '/login/noAccess')
    }

    /**
     * Show the login page.
     */
    def auth = {
        params.put(REQUEST, request)
        Map result = getServiceResponse(authActionService, params)
        Boolean isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            redirect uri: result.config.successHandler.defaultTargetUrl
            return
        }
        Boolean isPortalView = (Boolean) result.isPortalView
        if (isPortalView.booleanValue()) {
            render(view: result.view)
        } else {
            render view: result.view, model: [postUrl            : result.postUrl,
                                              rememberMeParameter: result.config.rememberMe.parameter, companyId: result.companyId]
        }
    }

    def loginSuccess() {
        params.put(REQUEST, request)
        params.put(RESPONSE, response)
        params.put(SESSION, session)
        Map result = getServiceResponse(loginSuccessActionService, params)
        Boolean isExpired = (Boolean) result.isExpired
        if (isExpired.booleanValue()) {
            render view: result.view, model: [userInfoMap: result.userInfoMap, companyId: result.companyId]
            return
        }
        Boolean isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            flash.message = result.message
            render view: result.view, model: [postUrl: result.postUrl, rememberMeParameter: result.config.rememberMe.parameter, companyId: result.companyId, message: result.message]
            return
        }
        redirect(uri: result.defaultUrl)
    }

    /**
     * Show denied page.
     */
    def denied() {
        if (springSecurityService.isLoggedIn() &&
                authenticationTrustResolver.isRememberMe(SecurityContextHolder.context?.authentication)) {
            // have cookie but the page is guarded with IS_AUTHENTICATED_FULLY
            redirect action: 'full', params: params
        }
    }

    /**
     * Login page for users with a remember-me cookie but accessing a IS_AUTHENTICATED_FULLY page.
     */
    def full() {
        def config = SpringSecurityUtils.securityConfig
        render view: '/application/login/auth', params: params,
                model: [hasCookie: authenticationTrustResolver.isRememberMe(SecurityContextHolder.context?.authentication),
                        postUrl  : "${request.contextPath}${config.apf.filterProcessesUrl}"]
    }

    /**
     * Callback after a failed login. Redirects to the auth page with a warning message.
     */
    def authfail() {
        GrailsParameterMap parameters = new GrailsParameterMap(request)
        parameters.put(SESSION, session)
        parameters.put(REQUEST, request)
        Map result = getServiceResponse(authFailActionService, parameters)
        if (springSecurityService.isAjax(request)) {
            render([error: result.message] as JSON)
        } else {
            flash.message = result.message
            Boolean isPortalView = (Boolean) result.isPortalView
            if (isPortalView.booleanValue()) {
                render view: result.view, model: [message: result.message]
            } else {
                redirect action: auth, params: params
            }
        }
    }

    /**
     * The Ajax success redirect url.
     */
    def ajaxSuccess() {
        render([success: true, username: springSecurityService.authentication.name] as JSON)
    }

    /**
     * The Ajax denied redirect url.
     */
    def ajaxDenied() {
        render([error: 'access denied'] as JSON)
    }

    def checklogin() {
        Map preResult = (LinkedHashMap) checkLoginActionService.executePreCondition(params)
        Boolean isError = (Boolean) preResult.isError
        if (isError.booleanValue()) {
            redirect uri: preResult.config.successHandler.defaultTargetUrl
            return
        }

        preResult.put(REQUEST, request)
        Map executeResult = (LinkedHashMap) checkLoginActionService.execute(preResult)
        isError = (Boolean) executeResult.isError
        if (isError.booleanValue()) {
            flash.message = executeResult.message
            render view: executeResult.view, model: [postUrl            : executeResult.postUrl,
                                                     rememberMeParameter: executeResult.config.rememberMe.parameter, companyId: executeResult.companyId]
            return
        }
        def model = [j_username: params.j_username, j_password: params.j_password]
        redirect(uri: "/j_spring_security_check", params: model)
    }

    def signUp() {
        renderOutput(signUpActionService, params)
    }

    def switchUser() {
        params.put(REQUEST, request)
        params.put(SESSION, session)
        Map result = getServiceResponse(switchUserLoginActionService, params)
        redirect(uri: result.defaultUrl)
    }
}

