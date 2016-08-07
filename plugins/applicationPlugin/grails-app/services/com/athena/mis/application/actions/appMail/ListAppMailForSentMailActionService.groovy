package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMail
import org.apache.log4j.Logger

/**
 * list sent mail
 * for details please go through use case named 'ListAppMailForSentMailActionService'
 */
class ListAppMailForSentMailActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    /**
     * @param parameters - serialized parameters from UI
     * @return - same map as received
     */
    Map executePreCondition(Map parameters) {
        return parameters
    }

    /**
     * get list of mail
     * @param previousResult - resulting map from pre condition
     * @return - same map as received
     */
    Map execute(Map previousResult) {
        try {
            long userId = super.appUser.id
            Closure additionalCondition = {
                eq('createdBy', userId)
                eq('isAnnouncement', false)
                eq('hasSend', true)
            }
            Map mailMap = getSearchResult(previousResult, AppMail.class, additionalCondition)
            previousResult.put(LIST, mailMap.list)
            previousResult.put(COUNT, mailMap.count)
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * @param previousResult - Map from execute
     * @return - same map as received
     */
    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    /**
     * @param executeResult - Map from post condition
     * @return - same map as received
     */
    Map buildSuccessResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * @param executeResult - Map from post condition
     * @return - same map as received
     */
    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }
}
