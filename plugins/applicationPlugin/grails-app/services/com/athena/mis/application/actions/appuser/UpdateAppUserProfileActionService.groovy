package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppUserDetails
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.*
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class UpdateAppUserProfileActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String MALE = "male"
    private static final String FEMALE = "female"
    private static final String USER_ID = "userId"
    private static final String APP_USER = "appUser"
    private static final String USER_NAME = "userName"
    private static final String NOT_FOUND_MSG = 'User could not be found.'
    private static final String EMAIL_EXIST_MSG = "Email ID already exist."
    private static final String ABOUT_ME_MAX_LENGTH_MSG = "About me length should be maximum 5000 character."
    private static final String APP_USER_UPDATE_SUCCESS_MESSAGE = "User profile has been updated successfully"

    AppUserService appUserService
    AppUserDetailsService appUserDetailsService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            AppUser sessionUser = super.getAppUser()
            // check if user exists or not
            if (!sessionUser) {
                return super.setError(params, NOT_FOUND_MSG)
            }

            if (params.email) {
                int countEmail = appUserService.countByEmailIlikeAndCompanyIdAndIdNotEqual(params.email, sessionUser.companyId, sessionUser.id)
                if (countEmail > 0) {
                    return super.setError(params, EMAIL_EXIST_MSG)
                }
            }

            String aboutMe = params.aboutMe.toString()
            if (aboutMe.trim().length() > AppUserDetails.ABOUT_ME_MAX_LENGTH) {
                return super.setError(params, ABOUT_ME_MAX_LENGTH_MSG)
            }
            AppUser appUser = appUserService.read(sessionUser.id)
            AppUser updatedUser = getAppUser(params, appUser, sessionUser)
            params.put(APP_USER, updatedUser)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    @Transactional
    public Map execute(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            appUserService.updateProfile(appUser)   // update company user(appUser) object in DB
            super.appUser.username = appUser.username
            super.appUser.email = appUser.email
            super.appUser.cellNumber = appUser.cellNumber
            int countUser = appUserDetailsService.countByUserId(appUser.id)
            result.put(USER_ID, appUser.id)
            if (countUser > 0) {
                AppUserDetails oldElUserDetails = appUserDetailsService.readByUser(appUser.id)
                AppUserDetails appUserDetails = getAppUserDetails(result, oldElUserDetails)
                appUserDetailsService.update(appUserDetails)
            } else {
                if (result.dateOfBirth || result.address || result.aboutMe || result.facebookUrl || result.webUrl || result.nationalId) {
                    AppUserDetails appUserDetails = getAppUserDetails(result)
                    appUserDetailsService.create(appUserDetails)
                }
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
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

    public Map buildSuccessResultForUI(Map result) {
        try {
            AppUser appUser = (AppUser) result.get(APP_USER)
            result.put(USER_NAME, appUser.username)
            result.put(MESSAGE, APP_USER_UPDATE_SUCCESS_MESSAGE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    public Map buildFailureResultForUI(Map result) {
        return result
    }

    private AppUser getAppUser(Map params, AppUser appUser, AppUser sessionUser) {
        appUser.username = params.username ? params.username : appUser.username
        appUser.email = params.email ? params.email : appUser.email
        appUser.cellNumber = params.cellNumber ? params.cellNumber : appUser.cellNumber
        appUser.genderId = Long.parseLong(params.genderId.toString())
        appUser.updatedBy = sessionUser.id
        appUser.updatedOn = new Date()
        return appUser
    }


    private AppUserDetails getAppUserDetails(Map params) {
        params.dateOfBirth = DateUtility.parseMaskedDate(params.dateOfBirth.toString())
        AppUser appUser = appSessionService.getAppUser()
        AppUserDetails elUserDetails = new AppUserDetails(params)
        elUserDetails.companyId = appUser.companyId
        elUserDetails.createdBy = appUser.id
        elUserDetails.createdOn = new Date()
        elUserDetails.updatedBy = 0L
        elUserDetails.updatedOn = null
        return elUserDetails
    }

    private AppUserDetails getAppUserDetails(Map params, AppUserDetails oldElUserDetails) {
        params.dateOfBirth = DateUtility.parseMaskedDate(params.dateOfBirth.toString())
        AppUser appUser = appSessionService.getAppUser()
        AppUserDetails elUserDetails = new AppUserDetails(params)
        oldElUserDetails.dateOfBirth = elUserDetails.dateOfBirth
        oldElUserDetails.address = elUserDetails.address
        oldElUserDetails.aboutMe = elUserDetails.aboutMe
        oldElUserDetails.facebookUrl = elUserDetails.facebookUrl
        oldElUserDetails.webUrl = elUserDetails.webUrl
        oldElUserDetails.nationalId = elUserDetails.nationalId
        oldElUserDetails.updatedBy = appUser.id
        oldElUserDetails.updatedOn = new Date()
        return oldElUserDetails
    }
}
