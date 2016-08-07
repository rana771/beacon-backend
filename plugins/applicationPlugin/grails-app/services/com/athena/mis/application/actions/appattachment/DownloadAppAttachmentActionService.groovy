package com.athena.mis.application.actions.appattachment

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppAttachment
import com.athena.mis.application.service.AppAttachmentService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Class to download AppAttachment object
 * For details go through Use-Case doc named 'DownloadAppAttachmentActionService'
 */
class DownloadAppAttachmentActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String NOT_FOUND_MESSAGE = "Selected attachment not found"
    private static final String APP_ATTACHMENT_OBJECT = "appAttachment"

    AppAttachmentService appAttachmentService

    /**
     * 1. check existence of required parameters
     * 2. check existence of appAttachment(attached file)
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.appAttachmentId) {
                return setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // check existence of AppAttachment
            long appAttachmentId = Long.parseLong(params.appAttachmentId.toString())
            AppAttachment appAttachment = appAttachmentService.readWithContent(appAttachmentId)
            if (!appAttachment) {
                return setError(params, NOT_FOUND_MESSAGE)
            }
            params.put(APP_ATTACHMENT_OBJECT, appAttachment)
            return params

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive AppAttachment object from executePreCondition method
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map execute(Map result) {
        try {
            AppAttachment appAttachment = (AppAttachment) result.get(APP_ATTACHMENT_OBJECT)
            appAttachment.fileName = appAttachment.fileName.trim().replace(SINGLE_SPACE, UNDERSCORE)
            result.put(APP_ATTACHMENT_OBJECT, appAttachment)
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
     * Since there is no success message return the same map
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
