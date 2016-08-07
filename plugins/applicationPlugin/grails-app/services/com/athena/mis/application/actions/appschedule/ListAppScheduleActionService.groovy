package com.athena.mis.application.actions.appschedule

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppSchedule
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class ListAppScheduleActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    /**
     * There is no preCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * get list and count of AppSchedule by companyId and pluginId
     * @param params -serialized parameters from UI
     * @return -a map containing necessary objects
     */
    @Transactional(readOnly = true)
    public Map execute(Map params) {
        try {
            String plugin = params.pluginId
            long pluginId = Long.parseLong(plugin)
            Closure additionalCondition = {
                eq('pluginId', pluginId)
            }
            Map appSchedule = getSearchResult(params, AppSchedule.class, additionalCondition)
            params.put(LIST, appSchedule.list)
            params.put(COUNT, appSchedule.count)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * set success message
     * @param result -map returned from execute method
     * @return -a map containing success message
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Build failure result in case of any error
     * @param result -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
