package com.athena.mis.application.actions.appfaq

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppFaq
import com.athena.mis.application.service.AppFaqService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete AppFaq object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAppFaqActionService'
 */
class DeleteAppFaqActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_FAQ = "appFaq"
    private static final String DELETE_SUCCESS_MESSAGE = "Faq has been deleted successfully."
    private static final String ENTITY_NOT_FOUND = "Selected faq could not be found."

    AppFaqService appFaqService

    /**
     * 1. check required parameter
     * 2. pull AppFaq object from service
     * 3. check for AppFaq object existence
     * @param params - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(params.id.toString())
            AppFaq appFaq = appFaqService.read(id)
            // check existence of AppFaq object
            if (!appFaq) {
                return super.setError(params, ENTITY_NOT_FOUND)
            }
            params.put(APP_FAQ, appFaq)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }
    /**
     * Delete AppFaq object from DB
     * 1. get the AppFaq object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppFaq appFaq = (AppFaq) result.get(APP_FAQ)
            appFaqService.delete(appFaq)
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
        return super.setSuccess(result, DELETE_SUCCESS_MESSAGE)
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
