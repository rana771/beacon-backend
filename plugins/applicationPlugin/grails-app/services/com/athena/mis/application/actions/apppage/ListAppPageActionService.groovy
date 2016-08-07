package com.athena.mis.application.actions.apppage

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppPage
import org.apache.log4j.Logger

class ListAppPageActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    Map executePreCondition(Map parameters) {
        return parameters
    }

    Map execute(Map previousResult) {
        try {
            Map result = getSearchResult(previousResult, AppPage.class)
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
