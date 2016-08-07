package com.athena.mis.application.actions.appuserentity

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppUserEntityActionServiceModel
import grails.transaction.Transactional
import org.apache.log4j.Logger

/**
 *  Get list of AppUserEntity objects to show on grid
 *  For details go through Use-Case doc named 'ListAppUserEntityActionService'
 */
class ListAppUserEntityActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    /**
     * No pre conditions required for searching project domains
     *
     * @param params - Request parameters
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * 1. check required parameters
     * 2. get list of AppUserEntity(user entity mapping) object(s) for grid by entity type id and entity id
     * @param parameters -parameters send from UI
     * @param obj -N/A
     * @return -a map containing list of necessary objects for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            long entityTypeId = Long.parseLong(result.entityTypeId.toString());
            long entityId = Long.parseLong(result.entityId.toString());
            Closure filterParam = {
                eq('entityTypeId', entityTypeId)
                eq('entityId', entityId)
            }
            Map resultMap = super.getSearchResult(result, ListAppUserEntityActionServiceModel.class, filterParam)
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
     *
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Since there is no success message return the same map
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
