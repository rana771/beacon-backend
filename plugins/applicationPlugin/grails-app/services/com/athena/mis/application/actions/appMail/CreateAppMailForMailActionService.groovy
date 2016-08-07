package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.AppSystemEntityCacheService
import grails.transaction.Transactional
import org.apache.log4j.Logger

import static org.springframework.util.StringUtils.countOccurrencesOf

/**
 * create mail
 * for details please go through use case named 'CreateMailForAppMailActionService'
 */
class CreateAppMailForMailActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_MAIL = "appMail"
    private static final String ATTACHMENT = "attachment"
    private static final String SUCCESS_MESSAGE = "Mail has been saved successfully"
    private static final String MIME_TYPE = "html"
    private static final String INVALID_MAIL = "Invalid Mail Address"
    private static final String TRANSACTION_CODE = "CreateMailForAppMailActionService"
    private static final def EMAIL_PATTERN = /[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\.[a-zA-Z]{2,4}/ // isValidEmail

    AppMailService appMailService
    AppSystemEntityCacheService appSystemEntityCacheService
    AppAttachmentService appAttachmentService

    /**
     * check necessary parameters
     * check valid mail
     * build mail object
     * @param parameters - serialized parameters from UI
     * @return - same map as received
     */
    Map executePreCondition(Map parameters) {
        try {
            // check necessary parameters
            if (!parameters.recipients || !parameters.subject || !parameters.body) {
                parameters.put(ATTACHMENT, null)
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            // check valid mail
            String errMsg = checkValidEmail(parameters)
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
     * update mail object to DB
     * create attachment if exists
     * @param previousResult - resulting map from pre condition
     * @return - same map as received
     */
    @Transactional
    Map execute(Map previousResult) {
        try {
            AppMail mail = (AppMail) previousResult.get(APP_MAIL)
            // update new mail object in DB
            if (previousResult.id) {
                appMailService.update(mail)
            } else {
                appMailService.create(mail)
            }
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
     * build mail object
     * @param params - Parameter map
     * @return - AppMail object
     */
    private AppMail buildMailObjectForUpdate(Map params, AppMail oldMail) {
        AppUser appUser = super.appUser
        AppMail mail = new AppMail(params)
        oldMail.isActive = true
        oldMail.mimeType = MIME_TYPE
        oldMail.subject = mail.subject
        oldMail.body = mail.body
        oldMail.recipients = mail.recipients
        oldMail.recipientsCc = mail.recipientsCc
        oldMail.updatedBy = super.appUser.id
        oldMail.updatedOn = new Date()
        oldMail.emailFrom = appUser.email
        return oldMail
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
        mail.hasSend = false
        mail.isRequiredRecipients = true
        mail.transactionCode = TRANSACTION_CODE
        mail.isManualSend = true
        mail.isAnnouncement = false
        mail.createdBy = super.appUser.id
        mail.createdOn = new Date()
        mail.updatedBy = 0L
        mail.emailFrom = appUser.email
        return mail
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
