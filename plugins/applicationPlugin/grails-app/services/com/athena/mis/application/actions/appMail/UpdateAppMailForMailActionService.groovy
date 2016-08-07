package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppAttachment
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.AppSystemEntityCacheService
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.web.multipart.commons.CommonsMultipartFile

import static org.springframework.util.StringUtils.countOccurrencesOf

/**
 * Update mail
 * For details please go through use case named 'UpdateMailForAppMailActionService'
 */
class UpdateAppMailForMailActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_MAIL = "appMail"
    private static final String ATTACHMENT = "attachment"
    private static final String APP_MAIL_NOT_FOUND = "mail not found"
    private static final String VERSION_MISMATCHED = "Version mismatched. Please refresh and try again."
    private static final String SUCCESS_MESSAGE = "Mail has been updated successfully"
    private static final String INVALID_MAIL = "Invalid Mail Address"
    private static final def EMAIL_PATTERN = /[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\.[a-zA-Z]{2,4}/ // isValidEmail

    AppMailService appMailService
    AppAttachmentService appAttachmentService
    AppSystemEntityCacheService appSystemEntityCacheService

    /**
     * check necessary parameters
     * read mail by id
     * check validation
     * build object for update
     * @param parameters - serialized parameters from UI
     * @return - same map as received
     */
    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try {
            // check necessary parameters
            if (!parameters.id || !parameters.version) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(parameters.id.toString())
            long version = Long.parseLong(parameters.version.toString())
            // read mail by id
            AppMail mail = (AppMail) appMailService.read(id)
            // check validation
            String errMsg = checkValidation(mail, version, parameters)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            // build object for update
            mail = buildObject(mail, parameters)
            parameters.put(APP_MAIL, mail)
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * save updated object to database
     * create attachment
     * @param previousResult - resulting map from pre condition
     * @return - same map as received
     */
    @Transactional
    Map execute(Map previousResult) {
        try {
            AppMail mail = (AppMail) previousResult.get(APP_MAIL)
            // update mail
            appMailService.update(mail)
            CommonsMultipartFile attachment = previousResult.attachment ? previousResult.attachment : null
            if (attachment && (!attachment.isEmpty())) {
                AppAttachment appAttachment = buildAttachmentObject(attachment, mail.id)
                appAttachmentService.create(appAttachment)
            }
            previousResult.put(ATTACHMENT, null)
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no post condition
     * @param previousResult - Map from execute
     * @return - same map as received
     */
    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    /**
     * put success message into map
     * @param executeResult - Map from post condition
     * @return - same map as received
     */
    Map buildSuccessResultForUI(Map executeResult) {
        return super.setSuccess(executeResult, SUCCESS_MESSAGE)
    }

    /**
     * @param executeResult - Map from post condition
     * @return - same map as received
     */
    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * 1. check whether mail object exists or not
     * 2. check object version
     * 3. check valid mail
     * @param mail - AppMail object
     * @param version - version from UI
     * @return - error message or null depending on validation
     */
    private String checkValidation(AppMail mail, long version, Map params) {
        String errMsg = checkExistence(mail)
        if (errMsg) return errMsg
        errMsg = checkVersion(mail, version)
        if (errMsg) return errMsg
        errMsg = checkValidEmail(params)
        if (errMsg) return errMsg
        return null
    }

    /**
     * check whether mail object exists or not
     * @param mail - AppMail object
     * @return - error message or null depending on validation
     */
    private String checkExistence(AppMail mail) {
        if (!mail) {
            return APP_MAIL_NOT_FOUND
        }
        return null
    }

    /**
     * check object version
     * @param mail - AppMail object
     * @param version - version from UI
     * @return - error message or null depending on validation
     */
    private String checkVersion(AppMail mail, long version) {
        if (mail.version != version) {
            return VERSION_MISMATCHED
        }
        return null
    }

    /**
     * build object for update
     * @param oldMail - AppMail object
     * @param params - parameter map from UI
     * @return - Appmail object
     */
    private AppMail buildObject(AppMail oldMail, Map params) {
        AppMail newMail = new AppMail(params)
        oldMail.recipients = newMail.recipients
        oldMail.recipientsCc = newMail.recipientsCc
        oldMail.subject = newMail.subject
        oldMail.body = newMail.body
        oldMail.updatedBy = super.appUser.id
        oldMail.updatedOn = new Date()
        return oldMail
    }

    /**
     * build Attachment object
     * @param attachment - CommonsMultipartFile instance
     * @param mailId - AppMail.id
     * @return - AppAttachment object
     */
    private AppAttachment buildAttachmentObject(CommonsMultipartFile attachment, long mailId) {
        SystemEntity typeMail = (SystemEntity) appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_APP_MAIL, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, super.companyId)
        AppAttachment appAttachment = new AppAttachment()
        appAttachment.contentCategoryId = 0L
        appAttachment.contentTypeId = 0L
        appAttachment.entityTypeId = typeMail.id
        appAttachment.entityId = mailId
        appAttachment.content = attachment.bytes
        appAttachment.extension = getContentExtension(attachment)
        appAttachment.createdBy = super.appUser.id
        appAttachment.createdOn = new Date()
        appAttachment.updatedBy = 0L
        appAttachment.companyId = super.companyId
        appAttachment.caption = EMPTY_SPACE
        return appAttachment
    }

    /**
     * get extension of file
     * @param contentFile - CommonsMultipartFile instance
     * @return - String
     */
    private String getContentExtension(CommonsMultipartFile contentFile) {
        String contentFileName = contentFile.properties.originalFilename.toString()
        int i = contentFileName.lastIndexOf(SINGLE_DOT)
        String uploadedFileExtension = contentFileName.substring(i + 1)
        return uploadedFileExtension
    }

    /**
     * check whether email is valid or not
     * @param params - Map with UI parameters
     * @return - error message or null depending on validation
     */
    private String checkValidEmail(Map params) {
        boolean isInvalid = false
        String recipients = params.recipients
        String recipientsCc = params.recipientsCc
        List<String> loginIdList = buildListOfString(recipients.replaceAll(SINGLE_SPACE, EMPTY_SPACE))
        loginIdList.each {
            if (!isValidEmail(it)) {
                isInvalid = true
            }
        }
        if (!recipientsCc.isEmpty()) {
            List<String> loginIdListCc = buildListOfString(recipientsCc.replaceAll(SINGLE_SPACE, EMPTY_SPACE))
            loginIdListCc.each {
                if (!isValidEmail(it)) {
                    isInvalid = true
                }
            }
        }
        if (isInvalid) return INVALID_MAIL
        return null
    }

    /**
     * check whether email is valid or not
     * @param email - String mail address
     * @return - (true/false) depending on validation
     */
    public boolean isValidEmail(String email) {
        if (!email) {
            return false
        }
        return email.matches(EMAIL_PATTERN)
    }

    /**
     * build List of String from coma Separated recipients
     * @param recipients - String recipients
     * @return - List of recipients
     */
    private List<String> buildListOfString(String recipients) {
        List<String> loginIdList = []
        int recipientCount = countOccurrencesOf(recipients, COMA)
        for (int i = 0; i < recipientCount; i++) {
            String subString = recipients.substring(0, recipients.indexOf(COMA))
            loginIdList << subString
            recipients = recipients.replace(subString + COMA, EMPTY_SPACE)
        }
        loginIdList << recipients
        return loginIdList
    }
}
