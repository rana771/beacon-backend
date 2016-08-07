import com.athena.mis.application.actions.logout.LogoutSuccessActionService
import com.athena.mis.application.controller.BaseController
import grails.plugin.springsecurity.SpringSecurityUtils

class LogoutController extends BaseController {

    LogoutSuccessActionService logoutSuccessActionService

    /**
     * Index action. Redirects to the Spring security logout uri.
     */
    def index() {
        getServiceResponse(logoutSuccessActionService, params)
        redirect uri: SpringSecurityUtils.securityConfig.logout.filterProcessesUrl // '/j_spring_security_logout'
    }
}
