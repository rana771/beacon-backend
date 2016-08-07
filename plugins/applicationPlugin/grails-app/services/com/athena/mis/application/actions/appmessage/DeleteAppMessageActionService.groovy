package com.athena.mis.application.actions.appmessage

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMessage
import com.athena.mis.application.service.AppMessageService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete user AppMessage object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAppMessageActionService'
 */
class DeleteAppMessageActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_MESSAGE = "appMessage"
    private static final String DELETE_SUCCESS_MSG = "Message has been successfully deleted."

    AppMessageService appMessageService

    /**
     * 1. check required parameter
     * 2. pull AppMessage object from service
     * 3. check for AppMessage object existence
     *
     * @param params - serialize parameters from UI
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
            // pull AppMessage object from service
            AppMessage appMessage = appMessageService.read(appMessageId)
            // check for AppMessage object existence
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
     * Delete AppMessage object from DB
     * 1. get the AppMessage object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppMessage appMessage = (AppMessage) result.get(APP_MESSAGE)
            appMessageService.delete(appMessage)
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
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DELETE_SUCCESS_MSG)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
