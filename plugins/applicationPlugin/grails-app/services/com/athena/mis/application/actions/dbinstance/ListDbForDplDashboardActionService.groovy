package com.athena.mis.application.actions.dbinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppDbInstance
import grails.transaction.Transactional
import org.apache.log4j.Logger

/**
 * List source databases for DPL dashboard under settings
 * for details please go through use case named 'ListDbForDplDashboardActionService'
 */
class ListDbForDplDashboardActionService extends BaseService implements ActionServiceIntf {
    private final Logger log = Logger.getLogger(getClass())

    /**
     * check necessary parameters
     * @param parameters
     * @return Map
     */
    Map executePreCondition(Map parameters) {
        try {
            if (!parameters.vendorId) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * list for kendo grid
     * @param previousResult
     * @return Map
     */
    @Transactional(readOnly = true)
    Map execute(Map previousResult) {
        try {
            long vendorId = Long.parseLong(previousResult.vendorId.toString())
            Closure filterCriteria = {
                eq('vendorId', vendorId)
            }
            Map result = getSearchResult(previousResult, AppDbInstance.class, filterCriteria)
            previousResult.put(LIST, result.list)
            previousResult.put(COUNT, result.count)
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    Map buildSuccessResultForUI(Map executeResult) {
        return executeResult
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }
}
