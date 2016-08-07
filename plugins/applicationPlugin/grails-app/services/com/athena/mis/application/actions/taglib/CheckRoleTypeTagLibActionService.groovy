package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import grails.plugin.springsecurity.SpringSecurityService
import org.apache.log4j.Logger

class CheckRoleTypeTagLibActionService extends BaseService implements ActionServiceIntf {

    SpringSecurityService springSecurityService

    private static final String STR_ID = 'id'
    private static final String NOT_LOGGED = 'not LoggedIn'
    private static final String COMMA = ','

    private Logger log = Logger.getLogger(getClass())

    /**
     * Do nothing here
     * @param params - a map with some necessary parameter
     * @return - returned the same map
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * @param result - a map returned from precondition method
     * @return - a map consisting hasRole flag
     */
    public Map execute(Map result) {
        try {
            result.hasRole = Boolean.FALSE
            if (!springSecurityService.isLoggedIn()) {
                return setError(result, NOT_LOGGED)
            }

            String strIds = result.get(STR_ID)
            if ((!strIds) || (strIds.length() == 0)) {
                return result
            }
            List<String> lstIds = strIds.split(COMMA);
            for (int i = 0; i < lstIds.size(); i++) {
                long roleTypeId = Long.parseLong(lstIds[i].toString())
                boolean hasRole = appSessionService.hasRole(roleTypeId)
                if (hasRole) {
                    result.hasRole = new Boolean(hasRole)
                    break
                }
            }
            return result
        } catch (Exception e) {
            log.error(e.message)
            throw new RuntimeException(e)
        }
    }

    /**
     * Do nothing in post condition
     * @param result - A map returned by execute method
     * @return - returned the received map
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * do nothing for build success operation
     * @param result - A map returned by post condition method.
     * @return - returned the same received map containing isError = false
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Do nothing here
     * @param result - map returned from previous any of method
     * @return - a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

}
