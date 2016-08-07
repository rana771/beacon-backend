package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppAttachment
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppUserDetails
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.AppUserDetailsService
import com.athena.mis.application.service.AppUserService
import org.apache.log4j.Logger

/**
 *  Show UI for changing password
 *  For details go through Use-Case doc named 'ShowAppUserForEditActionService'
 */
class ShowAppUserForEditActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String NOT_FOUND_MSG = 'User not found'
    private static final String APP_USER = "appUser"
    private static final String GENDER_MALE_ID = "genderMaleId"
    private static final String GENDER_FE_MALE_ID = "genderFeMaleId"
    private static final String APP_USER_DETAILS = "appUserDetails"
    private static final String APP_ATTACHMENT_ID = "appAttachmentId"

    AppUserService appUserService
    AppAttachmentService appAttachmentService
    AppSystemEntityCacheService appSystemEntityCacheService
    AppUserDetailsService appUserDetailsService

    /**
     * No pre conditions required
     * @param params - parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * Get logged in user and check existence of user
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map execute(Map result) {
        try {
            AppUser sessionUser = super.getAppUser()  // get logged in user
            // check if user exists or not
            if (!sessionUser) {
                return super.setError(result, NOT_FOUND_MSG)
            }
            AppUser appUser = appUserService.read(sessionUser.id)
            SystemEntity userDocumentType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_USER_DOCUMENT, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, sessionUser.companyId)
            AppAttachment appAttachment = appAttachmentService.findAttachmentByEntityTypeIdAndEntityId(userDocumentType.id, appUser.id)
            result.put(APP_ATTACHMENT_ID, 0)
            if (appAttachment) {
                result.put(APP_ATTACHMENT_ID, appAttachment.id)
            }
            SystemEntity genderMaleType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_GENDER_MALE, appSystemEntityCacheService.SYS_ENTITY_TYPE_GENDER, super.companyId)
            SystemEntity genderFemaleType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_GENDER_FEMALE, appSystemEntityCacheService.SYS_ENTITY_TYPE_GENDER, super.companyId)
            AppUserDetails appUserDetails = appUserDetailsService.readByUser(appUser.id)
            result.put(APP_USER, appUser)
            result.put(APP_USER_DETAILS, appUserDetails)
            result.put(GENDER_MALE_ID, genderMaleType.id)
            result.put(GENDER_FE_MALE_ID, genderFemaleType.id)
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
     * There is no success message
     * since the input-parameter already have "isError:false", just return the same map
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
