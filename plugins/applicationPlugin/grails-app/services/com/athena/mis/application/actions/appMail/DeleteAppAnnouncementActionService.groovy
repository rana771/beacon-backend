package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.service.AppMailService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete mail object from DB
 *  For details go through Use-Case doc named 'DeleteAppAnnouncementActionService'
 */
class DeleteAppAnnouncementActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SUCCESS_MESSAGE = "Announcement has been deleted successfully."
    private static final String APP_MAIL = "appMail"

    AppMailService appMailService

    /**
     * 1. check validation
     * @param params - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check validation
            String errMsg = checkValidation(params)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Delete mail object from DB
     * 1. get the mail object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppMail mail = (AppMail) result.get(APP_MAIL)
            appMailService.delete(mail)
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
        return super.setSuccess(result, SUCCESS_MESSAGE)
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
     * 1. check required parameter
     * 2. check mail object existence
     * @param params - serialized parameters from UI
     * @return - error message or null value
     */
    private String checkValidation(Map params) {
        // check required parameter
        if (!params.id) {
            return ERROR_FOR_INVALID_INPUT
        }
        long mailId = Long.parseLong(params.id.toString())
        AppMail mail = appMailService.read(mailId)
        // check mail object existence
        if (!mail) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        params.put(APP_MAIL, mail)
        return null
    }
}
