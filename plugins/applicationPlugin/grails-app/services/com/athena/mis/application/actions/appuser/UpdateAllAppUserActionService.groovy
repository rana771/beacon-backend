package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.model.ListAllAppUserActionServiceModel
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.ListAllAppUserActionServiceModelService
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 *  Update appUser object and grid data
 *  For details go through Use-Case doc named 'UpdateAllAppUserActionService'
 */
class UpdateAllAppUserActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_USER = "appUser"
    private static final String APP_USER_UPDATE_SUCCESS_MESSAGE = "User has been updated successfully"
    private static final String APP_USER_NOT_FOUND = "User not found or might has been changed"
    private static final String LOGIN_ID_EXISTS_MSG = "Same login id already exists"
    private static final String NOT_AVAILABLE = "Given login id is not available, try another one"
    private static final String EMAIL_EXIST_MSG = "Email ID already exist."
    private static
    final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})\$";

    AppUserService appUserService
    SpringSecurityService springSecurityService
    ListAllAppUserActionServiceModelService listAllAppUserActionServiceModelService

    /**
     * Check pre conditions before updating AppUser
     *      get appUser object and check uniqueness of login id
     * Get existing AppUser from service by appUser.id
     * @param params -serialized params from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if ((!params.id) || (!params.version)) {
                return setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())
            // get existing appUser from cache utility by appUser.id
            AppUser existingAppUser = appUserService.read(id)
            // check if appUser exists or not
            if ((!existingAppUser) || (existingAppUser.version != version)) {
                return super.setError(params, APP_USER_NOT_FOUND)
            }
            // build appUser object
            AppUser sessionUser = super.getAppUser()   // get logged in user
            AppUser appUser = buildAppUser(params, existingAppUser, sessionUser)
            int count = appUserService.countByLoginIdIlikeAndCompanyIdAndIdNotEqual(appUser.loginId, appUser.companyId, appUser.id)
            if (count > 0) {
                return super.setError(params, LOGIN_ID_EXISTS_MSG)
            }
            count = appUserService.countByLoginIdIlikeAndIdNotEqual(appUser.loginId, appUser.id)
            if (count > 0) {
                return super.setError(params, NOT_AVAILABLE)
            }
            if(params.email){
                int countEmail = appUserService.countByEmailIlikeAndCompanyIdAndIdNotEqual(params.email, existingAppUser.companyId, existingAppUser.id)
                if (countEmail > 0) {
                    return super.setError(params, EMAIL_EXIST_MSG)
                }
            }
            params.put(APP_USER, appUser)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Update AppUser object in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            appUserService.update(appUser)   // update company user(appUser) object in DB
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. get newly created user(appUser) object in grid
     * 2. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            ListAllAppUserActionServiceModel appUserModel = listAllAppUserActionServiceModelService.read(appUser.id)
            result.put(APP_USER, appUserModel)
            return super.setSuccess(result, APP_USER_UPDATE_SUCCESS_MESSAGE)
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build AppUser object with parameters
     * @param params -serialized params from UI
     * @param existingAppUser -old object of AppUser
     * @param sessionUser -logged in user
     * @return -object of AppUser
     */
    private AppUser buildAppUser(Map params, AppUser existingAppUser, AppUser sessionUser) {
        AppUser appUser = new AppUser(params)
        if (params.password) {
            existingAppUser.password = springSecurityService.encodePassword(params.password)
        }

        if(isValidEmailAddress(params.loginId) && (!params.email)){
            int countEmail = appUserService.countByEmailIlikeAndCompanyId(appUser.loginId, existingAppUser.companyId)
            if (countEmail == 0) {
                appUser.email = params.loginId
            }
        }

        existingAppUser.isDisablePasswordExpiration = appUser.isDisablePasswordExpiration
        existingAppUser.enabled = appUser.enabled
        existingAppUser.accountLocked = appUser.accountLocked
        existingAppUser.accountExpired = appUser.accountExpired
        existingAppUser.loginId = appUser.loginId
        existingAppUser.username = appUser.username
        existingAppUser.cellNumber = appUser.cellNumber
        existingAppUser.ipAddress = appUser.ipAddress
        existingAppUser.employeeId = appUser.employeeId
        existingAppUser.hasSignature = appUser.hasSignature
        existingAppUser.updatedBy = sessionUser.id
        existingAppUser.isSwitchable = appUser.isSwitchable
        existingAppUser.isReserved = appUser.isReserved
        existingAppUser.updatedOn = new Date()
        existingAppUser.isPowerUser = appUser.isPowerUser
        existingAppUser.isConfigManager = appUser.isConfigManager
        return existingAppUser
    }

    private static boolean isValidEmailAddress(String email) {
        boolean result = false;
        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            result = true;
        }
        return result;
    }
}
