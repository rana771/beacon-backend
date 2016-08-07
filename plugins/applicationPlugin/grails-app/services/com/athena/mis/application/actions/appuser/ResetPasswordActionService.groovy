package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.CompanyService
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpServletRequest

/**
 * Reset password of user and related properties
 * For details go through Use-Case doc named 'ResetPasswordActionService'
 */
class ResetPasswordActionService extends BaseService implements ActionServiceIntf{

    private Logger log = Logger.getLogger(getClass())

    private static final String USER_NOT_FOUND_MSG = 'User not found or password already been reset'
    private static final String SUCCESS_MESSAGE = 'Password has been reset successfully'
    private static final String APP_USER = 'appUser'
    private static final String TIME_EXPIRED_MSG = 'Time to reset password has expired'
    private static final String CODE_MISMATCH_MSG = 'Security code mismatched, please check mail for security code'
    private static final String PASSWORD_MISMATCH_MSG = 'Password mismatched, please type again'
    private static final String INVALID_PASS_LENGTH = 'Password must have at least 8 characters'
    private static
    final String INVALID_COMBINATION = 'Password should contain combination of letters, numbers & special character'
    private static final String PASSWORD_PATTERN = """^.*(?=.{8,})(((?=.*[a-z])(?=.*[A-Z])(?=.*[\\d]))|((?=.*[a-z])(?=.*[A-Z])(?=.*[\\W]))|((?=.*[a-z])(?=.*[\\d])(?=.*[\\W]))|((?=.*[A-Z])(?=.*[\\d])(?=.*[\\W]))).*\$"""

    AppUserService appUserService
    CompanyService companyService
    SpringSecurityService springSecurityService


    /**
     * 1. get companyId from request
     * 2. check input validation
     * @param params -serialized parameters from UI
     * @param obj -HttpServletRequest request
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map parameters) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long companyId = Long.parseLong(params.companyId.toString())
            // check required parameters
            if ((!params.link) || (!params.code) || (!params.password) || (!params.retypePassword)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            String link = params.link
            AppUser appUser = appUserService.findByPasswordResetLinkAndCompanyId(link, companyId)
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
     * Set and update properties of AppUser for reset password
     * @param parameters -serialized parameters from UI
     * @param obj -map returned from executePreCondition method
     * @return -a map contains isError(true/false) depending on method success and related message
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            cleanPasswordResetProperties(appUser, result)    // set password reset link, code and validity null
            updatePasswordResetProperties(appUser) // update properties for reset password
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
        return super.setSuccess(result, SUCCESS_MESSAGE)
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. check required parameters
     * 2. get AppUser object by link and companyId
     * 3. check if user exists or not
     * 4. check security code
     * 5. check time validation for reset password
     * 6. check password confirmation
     * 7. check password length
     * 8. check character combination of password
     * @param params -serialized parameters from UI
     * @return -a map containing AppUser object, isError (true/false) and relevant error message
     */
    private String checkInputValidation(GrailsParameterMap params, AppUser appUser) {
        // check if user exists or not
        if (!appUser) {
            return USER_NOT_FOUND_MSG
        }
        // check security code
        String code = params.code
        if (!code.equals(appUser.passwordResetCode)) {
            return CODE_MISMATCH_MSG
        }
        // check time validation for reset password
        Date currentDate = new Date()
        if (currentDate.compareTo(appUser.passwordResetValidity) > 0) {
            return TIME_EXPIRED_MSG
        }
        String password = params.password
        String retypePassword = params.retypePassword
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
     * @param user -object of AppUser
     * @param params -serialized parameters from UI
     */
    private void cleanPasswordResetProperties(AppUser user, Map result) {
        user.password = springSecurityService.encodePassword(result.password)
        user.passwordResetCode = null
        user.passwordResetValidity = null
        user.passwordResetLink = null
    }

    private static final String UPDATE_USER = """
        UPDATE app_user
        SET
            password=:password,
            password_reset_link=:passwordResetLink,
            password_reset_code=:passwordResetCode,
            password_reset_validity=:passwordResetValidity,
            version=version+1
        WHERE
            id=:id AND
            version=:version
    """

    /**
     * Update AppUser properties for reset password
     * @param user -object of AppUser
     * @return -an integer containing the value of update count
     */
    private int updatePasswordResetProperties(AppUser user) {
        Map queryParams = [
                password: user.password,
                passwordResetLink: user.passwordResetLink,
                passwordResetCode: user.passwordResetCode,
                passwordResetValidity: user.passwordResetValidity,
                id: user.id,
                version: user.version
        ]
        int updateCount = executeUpdateSql(UPDATE_USER, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while reset password')
        }
        user.version = user.version + 1
        return updateCount
    }
}
