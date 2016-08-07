package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppSysConfigCacheService
import com.athena.mis.application.entity.AppAttachment
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppSystemEntityCacheService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.commons.CommonsMultipartFile

class UploadAppUserDocumentActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String MB = " MB"
    private static final String KB = " KB"
    private static final String BYTES = " bytes"
    private static final String USER_DOCUMENT = "userDocument"
    private static final String UNRECOGNIZED_FILE = "Unrecognized file."
    private static final String FILE_SIZE_MAX_OF = "Attachment size can be maximum of "
    private static final String NEW_DOCUMENT = "newDocument"
    private static final String OLD_DOCUMENT = "oldDocument"
    private static final String UPLOAD_SUCCESS_MSG = "Resume has been uploaded successfully."

    AppSystemEntityCacheService appSystemEntityCacheService
    AppSysConfigCacheService appSysConfigCacheService
    AppAttachmentService appAttachmentService


    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            AppUser sessionUser = super.getAppUser()
            // check if user exists or not
            if (!sessionUser) {
                return super.setError(params, "User could not be found.")
            }

            CommonsMultipartFile userDocument = params.userDocument ? params.userDocument : null
            AppAttachment appAttachment
            if (userDocument && (!userDocument.isEmpty())) {
                // validate attachment
                String validationMsg = validateAttachment(userDocument)
                if (validationMsg) {
                    Map executePreResult = new LinkedHashMap<>()
                    return super.setError(executePreResult, validationMsg)
                }

                SystemEntity userDocumentType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_USER_DOCUMENT, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, sessionUser.companyId)
                appAttachment = appAttachmentService.findAttachmentByEntityTypeIdAndEntityId(userDocumentType.id, appUser.id)
                if (appAttachment) {
                    appAttachment.content = userDocument.bytes
                    appAttachment.fileName = getAttachmentName(userDocument)
                    appAttachment.extension = getContentExtension(userDocument)
                    appAttachment.updatedBy = sessionUser.id
                    appAttachment.updatedOn = new Date()
                    params.put(OLD_DOCUMENT, appAttachment)
                } else {
                    appAttachment = getAppAttachment(params, userDocumentType)
                    params.put(NEW_DOCUMENT, appAttachment)
                }
            }
            params.put(USER_DOCUMENT, userDocument)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Transactional
    public Map execute(Map result) {
        try {
            CommonsMultipartFile userDocument = (CommonsMultipartFile) result.get(USER_DOCUMENT)
            if (userDocument && (!userDocument.isEmpty())) {
                AppAttachment oldDocument = (AppAttachment) result.get(OLD_DOCUMENT)
                if (oldDocument) {
                    appAttachmentService.updateContent(oldDocument)
                } else {
                    AppAttachment newDocument = (AppAttachment) result.get(NEW_DOCUMENT)
                    appAttachmentService.create(newDocument)
                }
            }
            Map executeResult = new LinkedHashMap<>()
            return super.setSuccess(executeResult, UPLOAD_SUCCESS_MSG)
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

    private AppAttachment getAppAttachment(Map params, SystemEntity userDocumentType) {
        CommonsMultipartFile contentFile = params.userDocument
        AppAttachment appAttachment = new AppAttachment()
        appAttachment.content = contentFile.bytes
        appAttachment.fileName = getAttachmentName(contentFile)
        appAttachment.extension = getContentExtension(contentFile)
        appAttachment.createdOn = new Date()
        appAttachment.caption = EMPTY_SPACE
        appAttachment.companyId = super.getCompanyId()
        appAttachment.createdBy = super.getAppUser().id
        appAttachment.entityId = super.getAppUser().id
        appAttachment.entityTypeId = userDocumentType.id
        appAttachment.contentCategoryId = 0L
        appAttachment.contentTypeId = 0L
        appAttachment.isDraft = false
        return appAttachment
    }

    public String validateAttachment(CommonsMultipartFile userDocument) {
        // check supported attachment
        String isSupportedFile = checkSupportedAttachment(userDocument)
        if (!isSupportedFile) {
            return UNRECOGNIZED_FILE
        }
        // check attachment file size
        String returnMsg = checkMaxSize(userDocument)
        if (returnMsg) return returnMsg

        return null
    }

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

    private String checkMaxSize(CommonsMultipartFile contentFile) {
        SysConfiguration sysConfiguration = (SysConfiguration) appSysConfigCacheService.readByKeyAndCompanyId(appSysConfigCacheService.APPLICATION_ATTACHMENT_SIZE, super.getCompanyId())
        long contentFileSize = contentFile.size.longValue()
        String returnMsg = null
        long maxSize = Long.parseLong(sysConfiguration.value)       // max size 5 MB
        if (contentFileSize <= maxSize) return returnMsg
        long kbSize = 1024L
        long mbSize = 1048576L  // 1024 * 1024
        if (maxSize < kbSize) {
            returnMsg = FILE_SIZE_MAX_OF + maxSize + BYTES
        } else if (maxSize < mbSize) {
            long inKb = (maxSize) / kbSize
            returnMsg = FILE_SIZE_MAX_OF + inKb + KB
        } else {
            long inMb = (maxSize) / mbSize
            returnMsg = FILE_SIZE_MAX_OF + inMb + MB
        }
        return returnMsg
    }
}
