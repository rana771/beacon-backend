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
import org.springframework.web.multipart.commons.CommonsMultipartFile

import static grails.async.Promises.task
import static org.springframework.util.StringUtils.countOccurrencesOf

class CreateAndSendAppMailForMailActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_MAIL = "appMail"
    private static final String ATTACHMENT = "attachment"
    private static final String SUCCESS_MESSAGE = "Mail has been saved successfully"
    private static final String MIME_TYPE = "html"
    private static final String NOT_PRODUCTION_MODE = "Application has to be in production mode to send mail"
    private static final String TRANSACTION_CODE = "CreateMailForAppMailActionService"
    private static final String INVALID_MAIL = "Invalid Mail Address"
    private static final def EMAIL_PATTERN = /[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\.[a-zA-Z]{2,4}/ // isValidEmail

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
    AppSystemEntityCacheService appSystemEntityCacheService
    AppAttachmentService appAttachmentService
    AppConfigurationService appConfigurationService
    MailSenderService mailSenderService
    AppUserService appUserService
    AppMessageService appMessageService

    /**
     * check validation
     * read mail object
     * @param parameters - serialized parameters from UI
     * @return - same map as received
     */
    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try {
            // check validation
            String errMsg = checkValidation(parameters)
            if (errMsg) {
                parameters.put(ATTACHMENT, null)
                return super.setError(parameters, errMsg)
            }
            AppMail mail
            if (parameters.id) {
                mail = (AppMail) appMailService.read(Long.parseLong(parameters.id.toString()))
                mail = buildMailObjectForUpdate(parameters, mail)
            } else {
                mail = buildMailObjectForCreate(parameters)
            }
            parameters.put(APP_MAIL, mail)
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * save mail object to DB
     * create attachment if exists
     * @param previousResult - resulting map from pre condition
     * @return - same map as received
     */
    @Transactional
    Map execute(Map previousResult) {
        try {
            AppMail mail = (AppMail) previousResult.get(APP_MAIL)

            // update to DB
            if (previousResult.id) {
                appMailService.update(mail)
            } else {
                appMailService.create(mail)
            }

            SystemEntity typeMail = (SystemEntity) appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_APP_MAIL, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, super.companyId)
            AppAttachment appAttachment = appAttachmentService.findAttachmentByEntityTypeIdAndEntityId(typeMail.id, mail.id)
            if (appAttachment) {
                // send mail with attachment
                sendMail(mail, appAttachment)
            } else {
                // send mail
                sendMail(mail)
            }
            // create AppMessage
            createAppMessage(mail)
            previousResult.put(ATTACHMENT, null)
            previousResult.put(APP_MAIL, mail)
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no post condition
     * @param previousResult - resulting map from execute
     * @return - same map as received
     */
    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    /**
     * put success message into map
     * @param executeResult - resulting map from post condition
     * @return - same map as received
     */
    Map buildSuccessResultForUI(Map executeResult) {
        return super.setSuccess(executeResult, SUCCESS_MESSAGE)
    }

    /**
     * @param executeResult resulting map from post condition
     * @return - same map as received
     */
    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * build mail object for update
     * @param params - Parameter map
     * @param mail - AppMail object
     * @return - AppMail object
     */
    private AppMail buildMailObjectForUpdate(Map params, AppMail mail) {
        AppUser appUser = super.appUser
        AppMail newMail = new AppMail(params)
        mail.isActive = false
        mail.mimeType = MIME_TYPE
        mail.updatedBy = super.appUser.id
        mail.updatedOn = new Date()
        mail.hasSend = true
        mail.subject = newMail.subject
        mail.body = newMail.body
        mail.recipients = newMail.recipients
        mail.recipientsCc = newMail.recipientsCc
        mail.emailFrom = appUser.email
        return mail
    }

    /**
     * build AppMail object for create
     * @param params - Parameter map
     * @return - AppMail object
     */
    private AppMail buildMailObjectForCreate(Map params) {
        AppUser appUser = super.appUser
        AppMail mail = new AppMail(params)
        mail.isActive = false
        mail.companyId = super.companyId
        mail.mimeType = MIME_TYPE
        mail.updatedBy = super.appUser.id
        mail.updatedOn = new Date()
        mail.hasSend = true
        mail.isRequiredRecipients = true
        mail.transactionCode = TRANSACTION_CODE
        mail.isManualSend = true
        mail.isAnnouncement = false
        mail.emailFrom = appUser.email
        mail.createdBy = super.appUser.id
        mail.createdOn = new Date()
        mail.updatedBy = 0L
        return mail
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
     * 1. check required parameter
     * 2. check application deployment mode
     * 3. check valid mail
     * @param params - serialized parameters from UI
     * @return - error message or null value
     */
    private String checkValidation(Map params) {
        // check required parameter
        String errMsg = checkRequiredParameters(params)
        if (errMsg) {
            return errMsg
        }
        // check application deployment mode
        errMsg = checkDeploymentMode(params)
        if (errMsg) {
            return errMsg
        }
        // check valid mail
        errMsg = checkValidEmail(params)
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
        if (!params.recipients || !params.subject || !params.body) {
            return ERROR_FOR_INVALID_INPUT
        }
        return null
    }

    /**
     * Check application deployment mode
     * @param params - serialized parameters from UI
     * @return - error message or null value depending on application deployment mode
     */
    private String checkDeploymentMode(Map params) {
        int deploymentMode = 1
        SysConfiguration sysConfiguration = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.APPLICATION_DEPLOYMENT_MODE, super.companyId)
        if (sysConfiguration) {
            deploymentMode = Integer.parseInt(sysConfiguration.value)
        }
        if (deploymentMode != 1) {
            return NOT_PRODUCTION_MODE
        }
        return null
    }

    /**
     * Send mail
     * @param appMail - object of AppMail
     */
    private void sendMail(AppMail appMail) {
        // get list of user
        appMail.body = StringEscapeUtils.unescapeHtml(appMail.body);
        // send mail in another thread
        task { mailSenderService.sendMail(appMail) }
    }

    /**
     * Send mail
     * @param appMail - object of AppMail
     */
    private void sendMail(AppMail appMail, AppAttachment attachment) {
        // get list of user
        appMail.body = StringEscapeUtils.unescapeHtml(appMail.body);
        MailAttachment mailAttachment = new MailAttachment()
        mailAttachment.attachment = attachment.content
        mailAttachment.attachmentName = attachment.fileName
        mailAttachment.attachmentMimeType = getMimeType(attachment.extension)
        // send mail in another thread
        task { mailSenderService.sendMail(appMail, mailAttachment) }
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
