package com.athena.mis.application.actions.appmessage

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppMessageActionServiceModel
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of all/filtered message in grid
 *  For details go through Use-Case doc named 'ListAppMessageActionService'
 */
class ListAppMessageActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_USER_ID = "appUserId"

    /**
     * @param params -serialized parameters from UI
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * 1. initialize params for pagination of list
     *
     * 2. pull all message list from database (if no criteria)
     *
     * 3. pull filtered result from database (if given criteria)
     *
     * @param params -serialized parameters from UI
     * @return -a map containing necessary objects
     */
    @Transactional(readOnly = true)
    public Map execute(Map params) {
        try {
            Closure additionalCondition = {
                eq(APP_USER_ID, super.getAppUser().id)
            }
            Map mailMap = getSearchResult(params, ListAppMessageActionServiceModel.class, additionalCondition)
            params.put(LIST, mailMap.list)
            params.put(COUNT, mailMap.count)
            return params
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
     * Since there is no success message return the same map
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
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