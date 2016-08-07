package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppUserService
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Change user password in DB
 *  For details go through Use-Case doc named 'ChangeUserPasswordActionService'
 */
class ChangeUserPasswordActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    AppUserService appUserService
    SpringSecurityService springSecurityService

    private static final String APP_USER = 'appUser'
    private static final String WRONG_PASSWORD_MSG = 'Invalid Password'
    private static final String INVALID_INPUT_MSG = 'Error occurred due to invalid input'
    private static final String SUCCESS_MSG = 'Password changed successfully'
    private static final String NOT_FOUND_MSG = 'User not found'

    /**
     * 1.get logged in user
     * 2. check if user exists or not
     * 3. check input validation
     * @param params - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        try {
            AppUser appUser = getAppUser()   // get logged in user
            // check if user exists or not
            if (!appUser) {
                return super.setError(params, NOT_FOUND_MSG)
            }
            String msg = checkInputValidation(params, appUser)
            if (msg) {
                return super.setError(params, msg)
            }
            params.put(APP_USER, appUser)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Save new password in DB
     * 1. get the appUser object from map
     * 2. update password
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            appUserService.updatePassword(appUser)  // save new password in DB
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
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
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SUCCESS_MSG)
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
     * 1. check required parameters
     * 2. match input of old password with AppUser.password
     * 3. assign new password to AppUser
     * 4. check object validation
     * @param params -serialized parameters from UI
     * @param appUser -object of AppUser
     * @return -a string containing error message or null value depending on validation check
     */
    private String checkInputValidation(Map params, AppUser appUser) {
        if ((!params.oldPassword) || (!params.newPassword)) {
            return ERROR_FOR_INVALID_INPUT
        }
        // check if input for old password is correct or not
        String oldPass = springSecurityService.encodePassword(params.oldPassword) // encode password
        if (!oldPass.equals(appUser.password)) {
            return WRONG_PASSWORD_MSG
        }
        appUser.password = springSecurityService.encodePassword(params.newPassword) // encode password
        appUser.validate()  // validate AppUser object
        if (appUser.hasErrors()) {
            return INVALID_INPUT_MSG
        }
        return null
    }
}
