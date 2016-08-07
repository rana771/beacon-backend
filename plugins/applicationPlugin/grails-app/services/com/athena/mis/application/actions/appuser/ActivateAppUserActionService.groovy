package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.CompanyService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

/**
 *  Show login UI panel after app user activation
 *  For details go through Use-Case doc named 'ActivateAppUserActionService'
 */
class ActivateAppUserActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static String APP_USER_ACTIVATION_SUCCESS_MESSAGE = "Your account has been activated. Please sign in."
    private static String APP_USER_NOT_FOUND = "User not found"
    private static String COMPANY_NOT_FOUND = "Company not found"
    private static String APP_USER_ALREADY_ACTIVATED = "Your account already activated"
    private static String APP_USER_INVALID_ACTIVATION_REQUEST = "Invalid activation request"
    private static String APP_USER = "appUser"

    AppUserService appUserService
    CompanyService companyService

    /**
     * get httpServletRequest obj from params
     * check activation link
     * get company id by url
     * read app user by activation link and company id
     * check whether appUser exists or not
     * check whether appUser already active or not
     * @param parameters
     * @return
     */
    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try {
            String activationLink = parameters.link
            GrailsParameterMap params = (GrailsParameterMap) parameters
            HttpServletRequest request = params.getRequest()
            if (!activationLink || activationLink.isEmpty()) {     // check required parameter
                return super.setError(parameters, APP_USER_INVALID_ACTIVATION_REQUEST)
            }
            Company company = companyService.read(request)
            if(company == null) {
                return super.setError(parameters, COMPANY_NOT_FOUND)
            }
            AppUser appUser = appUserService.findByActivationLinkAndCompanyId(activationLink, company.id)
            // get activation link from cache
            if (!appUser) {               // check appUser
                return super.setError(parameters, APP_USER_NOT_FOUND)
            }

            if (appUser.isActivatedByMail) {        // check already activated
                return super.setError(parameters, APP_USER_ALREADY_ACTIVATED)
            }
            parameters.put(APP_USER, appUser)
            return parameters
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * update appUser obj to DB
     * @param previousResult
     * @return
     */
    @Transactional
    Map execute(Map previousResult) {
        try {
            AppUser appUser = (AppUser) previousResult.get(APP_USER)
            appUser.enabled = true            // appUser activated/enabled
            appUser.isActivatedByMail = true    // set enabled flag
            appUserService.update(appUser)            // update appUser in DB
            previousResult.put(APP_USER, appUser)
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    Map buildSuccessResultForUI(Map executeResult) {
        return super.setSuccess(executeResult, APP_USER_ACTIVATION_SUCCESS_MESSAGE)
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }
}
