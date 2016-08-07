package com.athena.mis.application.actions.appcountry

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppCountry
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of country for grid
 *  For details go through Use-Case doc named 'ListCountryActionService'
 */
class ListAppCountryActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    Map executePreCondition(Map parameters) {
        return parameters
    }

    @Transactional(readOnly = true)
    Map execute(Map previousResult) {
        try {
            Map resultMap = super.getSearchResult(previousResult, AppCountry.class)
            previousResult.put(LIST, resultMap.list)
            previousResult.put(COUNT, resultMap.count)
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