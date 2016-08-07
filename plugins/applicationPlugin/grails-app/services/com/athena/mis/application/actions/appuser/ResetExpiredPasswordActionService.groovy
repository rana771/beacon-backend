package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.AppUserService
import com.athena.mis.utility.DateUtility
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 * Reset expired password of user and related properties
 * For details go through Use-Case doc named 'ResetExpiredPasswordActionService'
 */
class ResetExpiredPasswordActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_USER = "appUser"
    private static final String USER_NOT_FOUND_MSG = "User can not found"
    private static final String SAME_EXPIRED_MSG = "Previous and new password can not be same"
    private static final String INVALID_PASS_LENGTH = "Password must have at least 8 characters"
    private static final String PASSWORD_MISMATCH_MSG = "Password mismatched, please type again"
    private static final String SUCCESS_MESSAGE = "Password changed successfully, please login again"
    private static final String OLD_PASSWORD_MISMATCH_MSG = "Previous password mismatched, please type again"
    private static
    final String INVALID_COMBINATION = "Password should contain combination of letters, numbers & special character"
    private static final String PASSWORD_PATTERN = """^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\\W]))|((?=.*[a-z])(?=.*[\\d])(?=.*[\\W]))|((?=.*[A-Z])(?=.*[\\d])(?=.*[\\W]))).*\$"""

    SpringSecurityService springSecurityService
    AppUserService appUserService
    AppConfigurationService appConfigurationService

    /**
     * 1. get companyId from request
     * 2. check input validation
     *
     * @param parameters - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map parameters) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            // check required parameters
            if ((!params.userName) || (!params.userId) || (!params.oldPassword) || (!params.password) || (!params.retypePassword)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long companyId = Long.parseLong(params.companyId.toString())
            long userId = Long.parseLong(params.userId.toString())
            AppUser appUser = appUserService.findByIdAndCompanyId(userId, companyId)
            String checkValidationMsg = checkInputValidation(params, appUser)
            if (checkValidationMsg) {
                return super.setError(params, checkValidationMsg)
            }
            params.put(APP_USER, appUser)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. Set and update properties of AppUser for reset password
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            // set password reset properties
            setPasswordResetProperties(appUser, result)
            // update properties for reset password
            updatePasswordResetProperties(appUser)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     *
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. put success message
     *
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. check required parameters
     * 2. get AppUser object by id from cache utility
     * 3. check if user exists or not
     * 4. check if input old password matches with user password
     * 5. check if old and new passwords are same
     * 6. check password confirmation
     * 7. check password length
     * 8. check character combination of password
     *
     * @param params -serialized parameters from UI
     * @param companyId - Company.id
     * @return - a map containing AppUser object, isError (true/false) and relevant error message
     */
    private String checkInputValidation(GrailsParameterMap params, AppUser appUser) {
        // check if user exists or not
        if (!appUser) {
            return USER_NOT_FOUND_MSG
        }
        String oldPassword = params.oldPassword
        String encodedOldPassword = springSecurityService.encodePassword(params.oldPassword)
        String password = params.password
        String retypePassword = params.retypePassword
        // check old password confirmation
        if (!encodedOldPassword.equals(appUser.password)) {
            return OLD_PASSWORD_MISMATCH_MSG
        }
        // check old and new password
        if (oldPassword.equals(password)) {
            return SAME_EXPIRED_MSG
        }
        // check password confirmation
        if (!password.equals(retypePassword)) {
            return PASSWORD_MISMATCH_MSG
        }
        // check length of password
        if (password.length() < 8) {
            return INVALID_PASS_LENGTH
        }
        // check password combination
        if (!password.matches(PASSWORD_PATTERN)) {
            return INVALID_COMBINATION
        }
        return null
    }

    /**
     * Set properties of AppUser for reset password
     *
     * @param user -object of AppUser
     * @param params -serialized parameters from UI
     */
    private void setPasswordResetProperties(AppUser user, Map params) {
        user.password = springSecurityService.encodePassword(params.password)
        SysConfiguration sysConfig = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.DEFAULT_PASSWORD_EXPIRE_DURATION, user.companyId)
        if (sysConfig) {
            user.nextExpireDate = new Date() + Integer.parseInt(sysConfig.value)
        } else {
            user.nextExpireDate = new Date()
        }
    }

    private static final String UPDATE_USER = """
        UPDATE app_user
        SET
            password=:password,
            next_expire_date=:nextExpireDate,
            version=version+1
        WHERE
            id=:id AND
            version=:version
    """

    /**
     * Update AppUser properties for reset password
     *
     * @param user -object of AppUser
     * @return -an integer containing the value of update count
     */
    private int updatePasswordResetProperties(AppUser user) {
        Map queryParams = [
                password      : user.password,
                nextExpireDate: DateUtility.getSqlDateWithSeconds(user.nextExpireDate),
                id            : user.id,
                version       : user.version
        ]
        int updateCount = executeUpdateSql(UPDATE_USER, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while reset password')
        }
        user.version = user.version + 1
        return updateCount
    }
}
