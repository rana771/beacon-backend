package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.MailAttachment
import com.athena.mis.MailSenderService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.*
import com.athena.mis.application.service.*
import grails.transaction.Transactional
import org.apache.commons.lang.StringEscapeUtils
import org.apache.log4j.Logger

import static grails.async.Promises.task
import static org.springframework.util.StringUtils.countOccurrencesOf

class SendAppMailForMailActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SUCCESS_MSG = "Mail has been sent successfully"
    private static final String APP_MAIL = "appMail"
    private static final String INCOMPLETE_MAIL = "Incomplete mail can not be sent"
    private static final String NOT_PRODUCTION_MODE = "Application has to be in production mode to send mail"

    // extension
    private static final String PNG_EXTENSION = "png"
    private static final String JPG_EXTENSION = "jpg"
    private static final String BMP_EXTENSION = "bmp"
    private static final String JPEG_EXTENSION = "jpeg"
    private static final String GIF_EXTENSION = "gif"
    private static final String PDF_EXTENSION = "pdf"
    private static final String TEXT_EXTENSION = "txt"
    private static final String CSV_EXTENSION = "csv"
    private static final String ZIP_EXTENSION = "zip"

    // mime type
    private static final String MIME_TYPE_PDF = "application/pdf"
    private static final String MIME_TYPE_TEXT = "text/plain"
    private static final String MIME_TYPE_CSV = "text/csv"
    private static final String MIME_TYPE_ZIP = "application/zip"
    private static final String MIME_TYPE_PNG = "image/png"
    private static final String MIME_TYPE_JPG = "image/jpeg"
    private static final String MIME_TYPE_BMP = "image/bmp"
    private static final String MIME_TYPE_JPEG = "image/jpeg"
    private static final String MIME_TYPE_GIF = "image/gif"

    AppMailService appMailService
    AppUserService appUserService
    MailSenderService mailSenderService
    AppConfigurationService appConfigurationService
    AppMessageService appMessageService
    AppSystemEntityCacheService appSystemEntityCacheService
    AppAttachmentService appAttachmentService

    /**
     * 1. check validation
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try {
            // check validation
            String errMsg = checkValidation(parameters)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. send mail
     * 2. update mail object
     * 3. create AppMessage
     * This method is in transactional block and will roll back in case of any exception
     * @param previousResult - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    Map execute(Map previousResult) {
        try {
            AppMail appMail = (AppMail) previousResult.get(APP_MAIL)
            // send mail
            sendMail(appMail)
            // update mail
            updateMail(appMail)
            // create AppMessage
            createAppMessage(appMail)
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param previousResult - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    /**
     * set success message
     * @param executeResult - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    Map buildSuccessResultForUI(Map executeResult) {
        return super.setSuccess(executeResult, SUCCESS_MSG)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param executeResult - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * 1. check required parameter
     * 2. check mail object existence
     * 3. check application deployment mode
     * 4. check incomplete mail
     * @param params - serialized parameters from UI
     * @return - error message or null value
     */
    private String checkValidation(Map params) {
        // check required parameter
        String errMsg = checkRequiredParameters(params)
        if (errMsg) {
            return errMsg
        }
        // check mail object existence
        errMsg = checkMailObjectExistence(params)
        if (errMsg) {
            return errMsg
        }
        // check application deployment mode
        errMsg = checkDeploymentMode(params)
        if (errMsg) {
            return errMsg
        }
        // check incomplete mail
        errMsg = checkIncompleteMail(params)
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
        if (!params.id) {
            return ERROR_FOR_INVALID_INPUT
        }
        return null
    }

    /**
     * Check mail object existence
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on object existence
     */
    private String checkMailObjectExistence(Map params) {
        long mailId = Long.parseLong(params.id.toString())
        AppMail mail = (AppMail) appMailService.read(mailId)
        // check mail object existence
        if (!mail) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        params.put(APP_MAIL, mail)
        return null
    }

    /**
     * Check application deployment mode
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on application deployment mode
     */
    private String checkDeploymentMode(Map params) {
        AppMail appMail = (AppMail) params.get(APP_MAIL)
        int deploymentMode = 1
        SysConfiguration sysConfiguration = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.APPLICATION_DEPLOYMENT_MODE, appMail.companyId)
        if (sysConfiguration) {
            deploymentMode = Integer.parseInt(sysConfiguration.value)
        }
        if (deploymentMode != 1) {
            return NOT_PRODUCTION_MODE
        }
        return null
    }

    /**
     * check incomplete mail
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on application deployment mode
     */
    private String checkIncompleteMail(Map params) {
        AppMail appMail = (AppMail) params.get(APP_MAIL)
        if (!appMail.recipients || !appMail.subject || !appMail.body) {
            return INCOMPLETE_MAIL
        }
        return null
    }

    /**
     * Update mail object
     * @param appMail - object of AppMail
     */
    private void updateMail(AppMail appMail) {
        // build object for update
        appMail = buildObjectForUpdate(appMail)
        // update mail object
        appMailService.update(appMail)
    }

    /**
     * Build object for update
     * @param appMail - object of AppMail
     * @return - updated object of AppMail
     */
    private AppMail buildObjectForUpdate(AppMail appMail) {
        appMail.hasSend = true
        appMail.version = appMail.version + 1
        appMail.updatedBy = getAppUser().id
        appMail.updatedOn = new Date()
        appMail.isActive = false
        return appMail
    }

    /**
     * Send mail
     * @param appMail - object of AppMail
     */
    private void sendMail(AppMail appMail) {
        // get list of user
        appMail.body = StringEscapeUtils.unescapeHtml(appMail.body);
        AppAttachment attachment = getAttachment(appMail)
        if (attachment) {
            MailAttachment mailAttachment = new MailAttachment()
            mailAttachment.attachment = attachment.content
            mailAttachment.attachmentName = attachment.fileName
            mailAttachment.attachmentMimeType = getMimeType(attachment.extension)
            // send mail in another thread
            task { mailSenderService.sendMail(appMail, mailAttachment) }
        } else {
            task { mailSenderService.sendMail(appMail) }
        }
    }

    private AppAttachment getAttachment(AppMail mail) {
        SystemEntity typeMail = (SystemEntity) appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_APP_MAIL, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, super.companyId)
        AppAttachment appAttachment = appAttachmentService.findAttachmentByEntityTypeIdAndEntityId(typeMail.id, mail.id)
        return appAttachment
    }

    /**
     * get user ids
     * build AppMessage object
     * save to database
     * @param mail
     */
    private void createAppMessage(AppMail mail) {
        List<Long> userIds = getAppMessageUserId(mail)
        userIds.each {
            AppMessage message = buildMessageObject(mail, it)
            appMessageService.create(message)
        }
    }

    /**
     * build AppMessage
     * @param mail - AppMail object
     * @param userId - AppUser.id
     * @return - AppMessage object
     */
    private AppMessage buildMessageObject(AppMail mail, long userId) {
        AppUser user = super.appUser
        AppMessage message = new AppMessage()
        message.appMailId = mail.id
        message.appUserId = userId
        message.isRead = false
        message.createdOn = new Date()
        message.companyId = mail.companyId
        message.subject = mail.subject
        message.senderName = user.username
        message.senderId = user.id
        message.body = mail.body
        return message
    }

    /**
     * get list of user ids
     * @param mail - AppMail object
     * @return - List of ids
     */
    private List<Long> getAppMessageUserId(AppMail mail) {
        List<Long> userIds = []
        String recipients = mail.recipients + (mail.recipientsCc ? (COMA + mail.recipientsCc) : EMPTY_SPACE)
        List<String> loginIdList = buildListOfString(recipients)
        List<AppUser> userList = appUserService.findAllByLoginIdInList(loginIdList)
        userList.each { userIds << it.id }
        return userIds
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

    private String getMimeType(String extension) {
        String mimeType = EMPTY_SPACE
        switch (extension) {
            case PDF_EXTENSION:
                mimeType = MIME_TYPE_PDF
                break
            case TEXT_EXTENSION:
                mimeType = MIME_TYPE_TEXT
                break
            case CSV_EXTENSION:
                mimeType = MIME_TYPE_CSV
                break
            case ZIP_EXTENSION:
                mimeType = MIME_TYPE_ZIP
                break
            case PNG_EXTENSION:
                mimeType = MIME_TYPE_PNG
                break
            case JPG_EXTENSION:
                mimeType = MIME_TYPE_JPG
                break
            case BMP_EXTENSION:
                mimeType = MIME_TYPE_BMP
                break
            case JPEG_EXTENSION:
                mimeType = MIME_TYPE_JPEG
                break
            case GIF_EXTENSION:
                mimeType = MIME_TYPE_GIF
                break
            default:
                break
        }
        return mimeType
    }
}
