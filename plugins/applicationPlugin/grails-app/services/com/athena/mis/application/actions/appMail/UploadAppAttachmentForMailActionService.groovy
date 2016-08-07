package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppAttachment
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.AppSystemEntityCacheService
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.web.multipart.commons.CommonsMultipartFile

/**
 * upload attachment for mail
 * for details please go through use case named 'UploadAppAttachmentForMailActionService'
 */
class UploadAppAttachmentForMailActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_MAIL = "appMail"
    private static final String ATTACHMENT = "attachment"
    private static final String INVALID_EXTENSION = "Selected file type is not supported in application"
    private static final String LIMIT_EXCEEDS = "File size exceeds its limit"
    private static final String TRANSACTION_CODE = "CreateMailForAppMailActionService"

    AppMailService appMailService
    AppAttachmentService appAttachmentService
    AppSystemEntityCacheService appSystemEntityCacheService
    AppConfigurationService appConfigurationService

    /**
     * 1. check Validation
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    Map executePreCondition(Map parameters) {
        try {
            // check validation
            String errMsg = checkValidation(parameters)
            if (errMsg) {
                parameters.put(ATTACHMENT, null)
                return super.setError(parameters, errMsg)
            }
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. read system entity for entity type mail
     * 2. create new mail or update existing
     * 3. create new attachment or update existing
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    Map execute(Map result) {
        try {
            SystemEntity typeMail = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_APP_MAIL, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, super.companyId)
            AppMail mail
            if (result.id) {
                mail = (AppMail) appMailService.read(Long.parseLong(result.id.toString()))
                AppAttachment appAttachment = getAppAttachmentForUpdate(result, typeMail)
                appAttachmentService.updateContent(appAttachment)
            } else {
                // build mail object
                mail = buildMailObject(result)
                appMailService.create(mail)
                AppAttachment appAttachment = getAppAttachment(result, mail, typeMail)
                appAttachment.save(flush: true)
            }
            result.put(ATTACHMENT, null)
            result.put(APP_MAIL, mail)
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
    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    /**
     * Do nothing
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    Map buildSuccessResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * 1. check required parameter
     * 2. check for valid extension
     * 3. check whether file size exceeds max limit
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on check validation
     */
    private String checkValidation(Map params) {
        // check required parameter
        String errMsg = checkRequiredParameters(params)
        if (errMsg) {
            return errMsg
        }
        // check valid extension
        errMsg = checkInvalidExtension(params)
        if (errMsg) {
            return errMsg
        }
        // check whether file size exceeds max limit
        errMsg = checkMaxFileSize(params)
        if (errMsg) {
            return errMsg
        }
        return null
    }

    /**
     * Check required parameter
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on check validation
     */
    private String checkRequiredParameters(Map params) {
        // check required parameter
        if (!params.attachment) {
            return ERROR_FOR_INVALID_INPUT
        }
        return null
    }

    /**
     * check for valid extension
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on check validation
     */
    private String checkInvalidExtension(Map params) {
        boolean isInvalid = true
        CommonsMultipartFile contentFile = params.attachment
        String originalContentFileName = contentFile.properties.originalFilename.toString()
        int i = originalContentFileName.lastIndexOf('.')
        String fileExtension = originalContentFileName.substring(i + 1)

        List<SystemEntity> mimeTypeList = appSystemEntityCacheService.list(appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, super.companyId)
        mimeTypeList.each {
            if (it.key.equalsIgnoreCase(fileExtension)) {
                isInvalid = false
            }
        }
        if (isInvalid) {
            return INVALID_EXTENSION
        }
        return null
    }

    /**
     * check whether file size exceeds max limit
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on check validation
     */
    private String checkMaxFileSize(Map params) {
        CommonsMultipartFile contentFile = params.attachment
        long fileSize = contentFile.size.longValue()
        SysConfiguration maxSizeConfig = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.APPLICATION_ATTACHMENT_SIZE, super.companyId)
        if (fileSize > Long.parseLong(maxSizeConfig.value)) {
            return LIMIT_EXCEEDS
        }
        return null
    }

    /**
     * build mail object
     * @param params - Parameter map
     * @return - AppMail object
     */
    private AppMail buildMailObject(Map params) {
        AppMail mail = new AppMail(params)
        mail.companyId = super.companyId
        mail.isActive = true
        mail.transactionCode = TRANSACTION_CODE
        mail.isRequiredRecipients = true
        mail.isManualSend = true
        mail.isAnnouncement = false
        mail.createdBy = super.appUser.id
        mail.createdOn = new Date()
        mail.updatedBy = 0L
        mail.hasSend = false
        mail.subject = EMPTY_SPACE
        mail.body = EMPTY_SPACE
        return mail
    }

    /**
     * get AppAttachment object
     * @param params - serialized parameters from UI
     * @param mail - AppMail object
     * @param typeMail - object of SystemEntity
     * @return - object of AppAttachment
     */
    private AppAttachment getAppAttachment(Map params, AppMail mail, SystemEntity typeMail) {
        CommonsMultipartFile contentFile = params.attachment
        AppAttachment appAttachment = new AppAttachment()
        appAttachment.content = contentFile.bytes

        //Set Extension of uploaded file
        String originalContentFileName = contentFile.properties.originalFilename.toString()
        appAttachment.fileName = originalContentFileName
        int i = originalContentFileName.lastIndexOf('.')
        String fileExtension = originalContentFileName.substring(i + 1)
        appAttachment.extension = fileExtension

        appAttachment.createdOn = new Date()
        appAttachment.caption = EMPTY_SPACE
        appAttachment.companyId = super.companyId
        appAttachment.entityId = mail.id
        appAttachment.entityTypeId = typeMail.id
        appAttachment.contentCategoryId = 0L
        appAttachment.contentTypeId = 0L
        return appAttachment
    }

    /**
     * get AppAttachment object for update
     * @param params - serialized parameters from UI
     * @param typeMail - object of SystemEntity
     * @return - object of AppAttachment
     */
    private AppAttachment getAppAttachmentForUpdate(Map params, SystemEntity typeMail) {
        CommonsMultipartFile contentFile = params.attachment
        long id = Long.parseLong(params.id.toString())
        AppAttachment appAttachment = appAttachmentService.findAttachmentByEntityTypeIdAndEntityId(typeMail.id, id)
        appAttachment.content = contentFile.bytes

        //Set Extension of uploaded file
        String originalContentFileName = contentFile.properties.originalFilename.toString()
        appAttachment.fileName = originalContentFileName

        int i = originalContentFileName.lastIndexOf('.')
        String fileExtension = originalContentFileName.substring(i + 1)
        appAttachment.extension = fileExtension
        return appAttachment
    }
}
