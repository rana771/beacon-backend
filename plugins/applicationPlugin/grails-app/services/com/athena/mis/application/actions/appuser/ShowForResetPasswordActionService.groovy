package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.CompanyService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 * Show UI for reset password
 * For details go through Use-Case doc named 'ShowForResetPasswordActionService'
 */
class ShowForResetPasswordActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass());

    private static final String APP_USER = "appUser"
    private static final String USERNAME = "username"
    private static final String USER_INFO_MAP = "userInfoMap"
    private static final String PASSWORD_RESET_LINK = "passwordResetLink"
    private static final String TIME_EXPIRED_MSG = "Time to reset password has expired"
    private static final String USER_NOT_FOUND_MSG = "Invalid link or password already reset"

    AppUserService appUserService
    CompanyService companyService

    /**
     * 1. check input validation
     * 2. check time validation for reset password
     * @param params -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map parameters) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long companyId = Long.parseLong(params.companyId.toString())
            AppUser appUser = appUserService.findByPasswordResetLinkAndCompanyId(params.link, companyId)
            // check input validation
            String checkValidationMsg = checkInputValidation(params, appUser)
            if (checkValidationMsg) {
                return super.setError(params, checkValidationMsg)
            }
            // check time validation for reset password
            Date currentDate = new Date()
            if (currentDate.compareTo(appUser.passwordResetValidity) > 0) {
                return super.setError(params, TIME_EXPIRED_MSG)
            }
            params.put(APP_USER, appUser)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Build map with user info for show in UI
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            Map userInfoMap = buildUserInfoMap(appUser)    // build map with user info for show in UI
            result.put(USER_INFO_MAP, userInfoMap)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Do nothing for post operation
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Do nothing for build success result for UI
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true, relevant error message and user information
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. check required parameter
     * 2. get AppUser object by link and companyId
     * 4. check existence of user
     * @param params -serialized parameters from UI
     * @return -a map containing AppUser object, isError (true/false) and relevant message
     */
    private String checkInputValidation(GrailsParameterMap params, AppUser appUser) {
        // check required parameter
        if (!params.link) {
            return ERROR_FOR_INVALID_INPUT
        }
        // check if user exists or not
        if (!appUser) {
            return USER_NOT_FOUND_MSG
        }
        return null
    }

    /**
     * Build map with user information
     * @param user -object of AppUser
     * @return -a map containing user information
     */
    private Map buildUserInfoMap(AppUser user) {
        Map userInfoMap = new LinkedHashMap()
        userInfoMap.put(PASSWORD_RESET_LINK, user ? user.passwordResetLink : EMPTY_SPACE)
        userInfoMap.put(USERNAME, user ? user.username : EMPTY_SPACE)
        return userInfoMap
    }
}
