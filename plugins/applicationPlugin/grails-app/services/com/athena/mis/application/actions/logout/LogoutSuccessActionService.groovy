package com.athena.mis.application.actions.logout

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.service.ApplicationSessionService
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class LogoutSuccessActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String FAILED_TO_LOGIN = "Failed to logout."

    @Autowired
    ApplicationSessionService applicationSessionService

    public Map executePreCondition(Map params) {
        return params
    }

    public Map execute(Map result) {
        try {
            applicationSessionService.logOut()       // remove user Session object from application scoped service
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(MESSAGE, FAILED_TO_LOGIN)
            return result
        }
    }


    Map executePostCondition(Map result) {
        return result
    }


    public Map buildSuccessResultForUI(Map result) {
        return result
    }


    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
