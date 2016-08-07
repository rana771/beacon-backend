package com.athena.mis.application.actions.appnote

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppNoteActionServiceModel
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of AppNote for grid
 *  For details go through Use-Case doc named 'ListAppNoteActionService'
 */
class ListAppNoteActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing for pre condition
     */
    public Map executePreCondition(Map parameters) {
        return parameters
    }

    /**
     * Get list of AppNote objects
     * @param result -parameters from UI
     * @return -a map contains list of AppNote objects  and count
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            // check required parameters
            if (!result.entityTypeId || !result.entityId) {
                result.put(LIST, [])
                result.put(COUNT, 0)
                return result
            }
            long entityTypeId = Long.parseLong(result.entityTypeId)
            long entityId = Long.parseLong(result.entityId)
            Closure additionalFilter={
                eq('entityTypeId',entityTypeId)
                eq('entityId',entityId)
            }
            Map searchResult= super.getSearchResult(result,ListAppNoteActionServiceModel.class,additionalFilter)
            result.put(LIST, searchResult.list)
            result.put(COUNT, searchResult.count)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Do nothing for post condition
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Do nothing for post condition
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Do nothing for post condition
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
