package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.CompanyService
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

import javax.servlet.http.HttpServletRequest

/**
 *  Show UI for user registration
 *  For details go through Use-Case doc named 'ShowAppUserRegistrationActionService'
 */
class ShowAppUserRegistrationActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String COMPANY = "company"

    CompanyService companyService

    /**
     * do nothing
     * @param parameters
     * @return
     */
    Map executePreCondition(Map parameters) {
        return parameters
    }

    /**
     * build map for user registration page
     * @param previousResult
     * @return
     */
    @Transactional(readOnly = true)
    Map execute(Map previousResult) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) previousResult
            HttpServletRequest request = params.getRequest()
            Company company = companyService.read(request)
            previousResult.put(COMPANY, company)
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * do nothing
     * @param previousResult
     * @return
     */
    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    /**
     * do nothing
     * @param executeResult
     * @return
     */
    Map buildSuccessResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * do nothing
     * @param executeResult
     * @return
     */
    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }
}
