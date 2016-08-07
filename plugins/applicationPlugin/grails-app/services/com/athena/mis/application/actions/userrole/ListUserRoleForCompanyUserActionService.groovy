package com.athena.mis.application.actions.userrole

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.model.ListUserRoleForCompanyUserActionServiceModel
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Get list of User-Role mapping object(s) to show on grid for company user
 *  For details go through Use-Case doc named 'ListUserRoleForCompanyUserActionService'
 */
class ListUserRoleForCompanyUserActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    /**
     * No pre conditions required
     * @param params - Request parameters
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * 1. initialize params for pagination of list
     * 2. pull all UserRole list from database by user id
     * 3. pull filtered result from database (if given criteria)
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            long userId = Long.parseLong(result.userId.toString())
            Closure additionalFilter = {
                eq('userId', userId)
            }
            Map resultMap = super.getSearchResult(result, ListUserRoleForCompanyUserActionServiceModel.class, additionalFilter)
            result.put(LIST, resultMap.list)
            result.put(COUNT, resultMap.count)
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
     * Since there is no success message return the same map
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param obj - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
