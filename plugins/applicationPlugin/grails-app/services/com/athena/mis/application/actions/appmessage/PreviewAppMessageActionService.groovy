package com.athena.mis.application.actions.appmessage

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppAttachment
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.AppMessage
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.model.ListAppMessageActionServiceModel
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.AppMessageService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.ListAppMessageActionServiceModelService
import com.athena.mis.utility.DateUtility
import org.apache.commons.lang.StringEscapeUtils
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional


class PreviewAppMessageActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_MESSAGE = "appMessage"
    private static final String HTML_MAIL = "html"

    AppMessageService appMessageService
    AppUserService appUserService
    AppAttachmentService appAttachmentService
    AppSystemEntityCacheService appSystemEntityCacheService
    ListAppMessageActionServiceModelService listAppMessageActionServiceModelService
    /**
     * 1. check required inputs
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameter
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long appMessageId = Long.parseLong(params.id.toString())
            AppMessage appMessage = (AppMessage) appMessageService.read(appMessageId)
            if (!appMessage) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            params.put(APP_MESSAGE, appMessage)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. read AppMail object from DB
     * @params parameters - Serialize parameters from UI
     * @return - A map of Entity or error message
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppMessage appMessage = (AppMessage) result.get(APP_MESSAGE)
            if (!appMessage.isRead) {
                appMessage = getAppMessage(appMessage)
                appMessageService.update(appMessage)
            }
            SystemEntity typeMail = (SystemEntity) appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_APP_MAIL, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, super.companyId)
            AppAttachment appAttachment = appAttachmentService.findAttachmentByEntityTypeIdAndEntityId(typeMail.id, appMessage.appMailId)
            result.put(HTML_MAIL, buildHtml(appMessage, appAttachment))
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     *
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * For select there is no success message
     * read from model for grid refresh
     * since the input-parameter already have "isError:false", just return the same map
     *
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppMessage message = (AppMessage) result.get(APP_MESSAGE)
            ListAppMessageActionServiceModel model = listAppMessageActionServiceModelService.read(message.id)
            result.put(APP_MESSAGE, model)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param obj -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    private AppMessage getAppMessage(AppMessage appMessage) {
        appMessage.isRead = true
        return appMessage
    }

    /**
     * build html for preview
     * @param mail - AppMail onject
     * @return - String of html
     */
    private String buildHtml(AppMessage message, AppAttachment appAttachment) {
        AppUser to = (AppUser) appUserService.read(message.appUserId)
        AppUser sender = (AppUser) appUserService.read(message.senderId)
        String attachmentIcon = EMPTY_SPACE
        if (appAttachment) {
            attachmentIcon = "<a href='/appAttachment/downloadContent?appAttachmentId=${appAttachment.id}' class='pull-right' title='Download attachment content'><i class='fa fa-paperclip'></i></a>"
        }
        String html = """
            <table class="table table-bordered">
                <tbody>
                    <tr>
                        <td class="active">To</td>
                        <td>${to? to.email : ''}</td>
                    </tr>
                    <tr>
                        <td class="active">From</td>
                        <td>${sender? sender.email : ''}</td>
                    </tr>
                    <tr>
                        <td class="active">Date/Time</td>
                        <td>${DateUtility.getShortDateTimeFormatAsString(message.createdOn)}${attachmentIcon}</td>
                    </tr>
                    <tr>
                        <td class="active">Subject</td>
                        <td>${message.subject}</td>
                    </tr>
                    <tr>
                        <td class="active">Body</td>
                        <td>${message.body}</td>
                    </tr>
                </tbody>
            </table>
        """
        return html
    }
}
