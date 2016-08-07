package com.athena.mis.application.actions.appmessage

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMessage
import com.athena.mis.application.model.ListAppMessageActionServiceModel
import com.athena.mis.application.service.AppMessageService
import com.athena.mis.application.service.ListAppMessageActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Mark as unread of Message
 * For details go through Use-Case doc named 'MarkAsUnReadAppMessageActionService'
 */
class MarkAsUnReadAppMessageActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_MESSAGE = "appMessage"
    private static final String SUCCESS_MSG = "Successfully marked as un-read."
    private static final String MARKED_AS_READ_MSG = "Already marked as un-read."

    AppMessageService appMessageService
    ListAppMessageActionServiceModelService listAppMessageActionServiceModelService


    /**
     * 1. pull AppMessage object from service
     * 2. check appMessage object existence
     * 3. check message already read or not
     * 4. build appMessage for update as unread
     *
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if ((!params.id)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long appMessageId = Long.parseLong(params.id.toString())

            // read appMessage object from service
            AppMessage appMessage = appMessageService.read(appMessageId)
            // check appMessage object existence
            if (!appMessage) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            // check message already read or not
            if (!appMessage.isRead) {
                return super.setError(params, MARKED_AS_READ_MSG)
            }
            // build appMessage for update as unread
            appMessage = getAppMessage(appMessage)
            params.put(APP_MESSAGE, appMessage)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Update AppMessage as un-read
     * 1. get the appMessage object from map
     * This function is in transactional boundary and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            // get the appMessage object from map
            AppMessage appMessage = (AppMessage) result.get(APP_MESSAGE)
            // Update AppMessage as un-read
            appMessageService.update(appMessage)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
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
     * 1. put success message
     *
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        try {
            // get the appMessage object from map
            AppMessage appMessage = (AppMessage) result.get(APP_MESSAGE)
            ListAppMessageActionServiceModel model = listAppMessageActionServiceModelService.read(appMessage.id)
            result.put(APP_MESSAGE, model)
            return super.setSuccess(result, SUCCESS_MSG)
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /*
    * build appMessage for update as unread
    * */
    private AppMessage getAppMessage(AppMessage appMessage) {
        appMessage.isRead = false
        return appMessage
    }
}
