package com.athena.mis.application.actions.appattachment

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppAttachment
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update AppAttachment object and show on grid list
 *  For details go through Use-Case doc named 'UpdateAppAttachmentActionService'
 */
class UpdateAppAttachmentActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String UPDATE_SUCCESS_MESSAGE = "Attachment has been updated successfully"
    private static final String NOT_FOUND_MESSAGE = "Selected attachment not found"
    private static final String APP_ATTACHMENT = "appAttachment"

    AppAttachmentService appAttachmentService

    /**
     * Check different criteria for updating AppAttachment object
     *      1) Check existence of required parameters
     *      2) Check existence of old AppAttachment object
     *      3) Validate AppAttachment object
     * @param params -parameter send from UI
     * @return -a map containing AppAttachment object for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check here for required params are present
            if (!params.id || !params.caption || !params.version || !params.entityTypeId || !params.entityId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())
            AppAttachment oldAppAttachment = appAttachmentService.read(id)
            //Check existence of old AppAttachment object
            if ((!oldAppAttachment) || (oldAppAttachment.version != version)) {
                return super.setError(params, NOT_FOUND_MESSAGE)
            }
            AppAttachment appAttachment = buildAppAttachmentObjectForUpdate(params, oldAppAttachment)
            params.put(APP_ATTACHMENT, appAttachment)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     *  1) if new attachment is given then update all fields of AppAttachment object
     *           otherwise update fields of AppAttachment object except content field
     *  2) if budget lineItem changed then update content_count field of Budget domain
     *
     * @param obj -AppAttachment Object send from executePreCondition
     * @return -newly created AppAttachment object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppAttachment appAttachment = (AppAttachment) result.get(APP_ATTACHMENT)
            //update fields of AppAttachment object except content
            appAttachmentService.updateWithoutContent(appAttachment)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * do nothing at post condition
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Wrap updated AppAttachment object to show on grid
     * @param obj -updated AppAttachment object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        Map failureResult = new LinkedHashMap<>()
        return super.setError(failureResult, result.get(MESSAGE).toString())
    }

    /**
     * build AppAttachment object to update in DB
     * @param params -GrailsParameterMap
     * @param oldAppAttachment -object of AppAttachment
     * @param sessionUser -object of AppUser (logged in user)
     * @return -AppAttachment object
     */
    private AppAttachment buildAppAttachmentObjectForUpdate(Map params, AppAttachment oldAppAttachment) {
        AppUser appUser = super.getAppUser()
        AppAttachment appAttachment = new AppAttachment(params)

        oldAppAttachment.contentCategoryId = appAttachment.contentCategoryId
        oldAppAttachment.caption = appAttachment.caption
        oldAppAttachment.updatedOn = new Date()
        oldAppAttachment.updatedBy = appUser.id
        oldAppAttachment.isDraft = false
        oldAppAttachment.expirationDate = params.expirationDate ? DateUtility.parseMaskedDate(params.expirationDate.toString()) : null
        return oldAppAttachment
    }
}
