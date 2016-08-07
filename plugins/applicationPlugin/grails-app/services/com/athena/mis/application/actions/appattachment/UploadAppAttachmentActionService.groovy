package com.athena.mis.application.actions.appattachment

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppSysConfigCacheService
import com.athena.mis.application.entity.AppAttachment
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppSystemEntityCacheService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.commons.CommonsMultipartFile

class UploadAppAttachmentActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String MB = " MB"
    private static final String KB = " KB"
    private static final String BYTES = " bytes"
    private static final String APP_ATTACHMENT = "appAttachment"
    private static final String UNRECOGNIZED_FILE = "Unrecognized file."
    private static final String FILE_SIZE_MAX_OF = "Attachment size can be maximum of "

    AppAttachmentService appAttachmentService
    AppSystemEntityCacheService appSystemEntityCacheService
    AppSysConfigCacheService appSysConfigCacheService

    /**
     * 1. validate attachment
     * 2. build attachment object
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.contentObj) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // validate attachment
            String validationMsg = validateAttachment(params)
            if (validationMsg) {  //if validation failed then return with message
                Map executePreResult = new LinkedHashMap<>()
                return super.setError(executePreResult, validationMsg)
            }
            // build appAttachment object to create
            AppAttachment appAttachment
            if (params.id) {
                appAttachment = getAppAttachmentForUpdate(params)
            } else {
                appAttachment = getAppAttachment(params)
            }
            params.put(APP_ATTACHMENT, appAttachment)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive attachment object from executePreCondition method
     * 2. create new attachment or update existing
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map execute(Map result) {
        try {
            Map executeResult = new LinkedHashMap<>()
            AppAttachment appAttachment = (AppAttachment) result.get(APP_ATTACHMENT)
            if (result.id) {
                appAttachmentService.updateContent(appAttachment)
            } else {
                appAttachment.save(flush: true)
            }
            executeResult.put(ID, appAttachment.id)
            return super.setSuccess(executeResult, null)
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
     * Do nothing
     * @param result - resulting map from execute
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
     * validate content
     * 1. check extension
     * 2. check max size
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on validation check
     */
    public String validateAttachment(Map params) {
        CommonsMultipartFile contentFile = params.contentObj
        // check supported attachment
        String isSupportedFile = checkSupportedAttachment(contentFile)
        if (!isSupportedFile) {
            return UNRECOGNIZED_FILE
        }
        // check attachment file size
        String returnMsg = checkMaxSize(contentFile)
        if (returnMsg) return returnMsg

        return null
    }

    /**
     * Get image extension
     * @param imageFile - object of CommonsMultipartFile
     * @return - name string of image extension
     */
    private String checkSupportedAttachment(CommonsMultipartFile imageFile) {
        String extension = getContentExtension(imageFile)
        List sysEntityMimeType = appSystemEntityCacheService.listByIsActive(appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, super.getCompanyId())
        for (int i = 0; i < sysEntityMimeType.size(); i++) {
            SystemEntity systemEntity = (SystemEntity) sysEntityMimeType[i]
            if (systemEntity.key.equalsIgnoreCase(extension)) {
                return extension
            }
        }
        return null
    }

    /**
     * Get uploaded file extension
     * @param contentFile - object of CommonsMultipartFile
     * @return - file extension
     */
    private String getContentExtension(CommonsMultipartFile contentFile) {
        String attachmentName = contentFile.properties.originalFilename.toString()
        int i = attachmentName.lastIndexOf(SINGLE_DOT)
        String extension = attachmentName.substring(i + 1)
        return extension
    }

    /**
     * Get original attachment file name
     * @param contentFile - an object of CommonsMultipartFile
     * @return contentFileName - name of file
     */
    private String getAttachmentName(CommonsMultipartFile contentFile) {
        String contentFileName = contentFile.properties.originalFilename.toString()
        return contentFileName
    }

    /**
     * Check if uploaded file exceeds content pre-defined size
     * @param contentFile - an object of CommonsMultipartFile
     * @return - a message string of file size
     */
    private String checkMaxSize(CommonsMultipartFile contentFile) {
        SysConfiguration sysConfiguration = (SysConfiguration) appSysConfigCacheService.readByKeyAndCompanyId(appSysConfigCacheService.APPLICATION_ATTACHMENT_SIZE, super.getCompanyId())
        long contentFileSize = contentFile.size.longValue()
        String returnMsg = null
        long maxSize = Long.parseLong(sysConfiguration.value)       // max size 5 MB
        if (contentFileSize <= maxSize) return returnMsg
        long kbSize = 1024L
        long mbSize = 1048576L  // 1024 * 1024
        if (maxSize < kbSize) {
            returnMsg =  FILE_SIZE_MAX_OF + maxSize + BYTES
        } else if (maxSize < mbSize) {
            long inKb = (maxSize) / kbSize
            returnMsg = FILE_SIZE_MAX_OF + inKb + KB
        } else {
            long inMb = (maxSize) / mbSize
            returnMsg =  FILE_SIZE_MAX_OF + inMb + MB
        }
        return returnMsg
    }

    /**
     * get AppAttachment object
     * @param params - serialized parameters from UI
     * @return - object of AppAttachment
     */
    private AppAttachment getAppAttachment(Map params) {
        CommonsMultipartFile contentFile = params.contentObj
        AppAttachment appAttachment = new AppAttachment()
        appAttachment.content = contentFile.bytes
        appAttachment.fileName = getAttachmentName(contentFile)
        appAttachment.extension = getContentExtension(contentFile)
        appAttachment.createdOn = new Date()
        appAttachment.caption = EMPTY_SPACE
        appAttachment.companyId = super.getCompanyId()
        appAttachment.entityId = Long.parseLong(params.entityId.toString())
        appAttachment.entityTypeId = Long.parseLong(params.entityTypeId.toString())
        appAttachment.contentCategoryId = 0L
        appAttachment.contentTypeId = 0L
        appAttachment.isDraft = true
        return appAttachment
    }

    /**
     * get AppAttachment object for update
     * @param params - serialized parameters from UI
     * @return - object of AppAttachment
     */
    private AppAttachment getAppAttachmentForUpdate(Map params) {
        CommonsMultipartFile contentFile = params.contentObj
        long id = Long.parseLong(params.id.toString())
        AppAttachment appAttachment = appAttachmentService.read(id)
        appAttachment.content = contentFile.bytes
        appAttachment.fileName = getAttachmentName(contentFile)
        appAttachment.extension = getContentExtension(contentFile)
        appAttachment.isDraft = true
        return appAttachment
    }

}
