package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMail
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class ListAppMailActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    /**
     * @param params -serialized parameters from UI
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * Check pluginId; if not defined then set pluginId = 1 (application pluginId)
     * get list and count of AppMail by companyId and pluginId
     * @param params -serialized parameters from UI
     * @return -a map containing necessary objects
     */
    @Transactional(readOnly = true)
    public Map execute(Map params) {
        try {
            String plugin = params.pluginId
            int pluginId = Integer.parseInt(plugin)
            Closure additionalCondition = {
                eq('pluginId', pluginId)
            }
            Map mailMap = getSearchResult(params, AppMail.class, additionalCondition)
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