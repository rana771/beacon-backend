package com.athena.mis.application.actions.appattachment

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppAttachment
import com.athena.mis.application.service.AppAttachmentService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class DownloadAppImageAttachmentActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String NOT_FOUND_MESSAGE = "Selected attachment not found"
    private static final String APP_ATTACHMENT_OBJECT = "appAttachment"
    private static final String STREAM = "stream"
    private static final String PNG_EXTENSION = "png"
    private static final String JPG_EXTENSION = "jpg"
    private static final String BMP_EXTENSION = "bmp"
    private static final String JPEG_EXTENSION = "jpeg"
    private static final String GIF_EXTENSION = "gif"

    AppAttachmentService appAttachmentService
    AppConfigurationService appConfigurationService

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
                params.put(STREAM, getNotFoundImage())
                return setError(params, NOT_FOUND_MESSAGE)
            }
            if (!checkFileExtension(appAttachment.extension)){
                params.put(STREAM, getNotFoundImage())
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

    /**
     * Get image for placeholder
     */
    private byte[] getNotFoundImage() {
        String imgLoc = appConfigurationService.getAppImageDir() + "/user-no-image.jpg"
        File imgFile = new File(imgLoc)
        return imgFile.bytes
    }

    private boolean checkFileExtension(String extension){
        boolean isImage = false
        switch (extension){
            case PNG_EXTENSION:
                isImage = true
                break
            case JPG_EXTENSION:
                isImage = true
                break
            case JPEG_EXTENSION:
                isImage = true
                break
            case BMP_EXTENSION:
                isImage = true
                break
            case GIF_EXTENSION:
                isImage = true
        }
        return isImage
    }
}
