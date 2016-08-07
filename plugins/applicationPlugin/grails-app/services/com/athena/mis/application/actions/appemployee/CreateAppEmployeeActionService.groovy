package com.athena.mis.application.actions.appemployee

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppEmployee
import com.athena.mis.application.model.ListAppEmployeeActionServiceModel
import com.athena.mis.application.service.AppEmployeeService
import com.athena.mis.application.service.ListAppEmployeeActionServiceModelService
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new employee object and show in grid
 *  For details go through Use-Case doc named 'CreateEmployeeActionService'
 */
class CreateAppEmployeeActionService extends BaseService implements ActionServiceIntf {

    AppEmployeeService appEmployeeService
    ListAppEmployeeActionServiceModelService listAppEmployeeActionServiceModelService

    private static final String SUCCESS_MSG = "Employee has been successfully saved"
    private static final String EMPLOYEE = "employee"

    private Logger log = Logger.getLogger(getClass())

    /**
     * build employee object
     * @param parameters - serialize parameter from ui
     * @return - a map containing employee object for execute
     */
    Map executePreCondition(Map parameters) {
        try{
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            AppEmployee employee = buildEmployeeObject(parameterMap)
            parameters.put(EMPLOYEE, employee)
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Save employee object in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param previousResult - map from executePreCondition containing employee object
     * @return a map containing saved employee object for execute
     */
    @Transactional
    Map execute(Map previousResult) {
        try{
            AppEmployee employee = (AppEmployee) previousResult.get(EMPLOYEE)
            appEmployeeService.create(employee)  // save new employee object in DB
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * do nothing
     * @param previousResult
     * @return previousResult
     */
    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    /**
     * read newly created employee object
     * @param executeResult
     * @return executeResult
     */
    @Transactional(readOnly = true)
    Map buildSuccessResultForUI(Map executeResult) {
        try{
            AppEmployee employee = (AppEmployee) executeResult.get(EMPLOYEE)
            ListAppEmployeeActionServiceModel savedEmployee = listAppEmployeeActionServiceModelService.read(employee.id)
            executeResult.put(EMPLOYEE, savedEmployee)
            return super.setSuccess(executeResult, SUCCESS_MSG)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * do nothing
     * @param executeResult
     * @return executeResult
     */
    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * Build employee object
     * @param parameterMap -serialized parameters from UI
     * @return -new employee object
     */
    private AppEmployee buildEmployeeObject(GrailsParameterMap parameterMap) {
        parameterMap.dateOfBirth = DateUtility.parseMaskedDate(parameterMap.dateOfBirth)
        parameterMap.dateOfJoin = DateUtility.parseMaskedDate(parameterMap.dateOfJoin)
        AppEmployee employee = new AppEmployee(parameterMap)
        AppUser appUser = super.appUser
        employee.companyId = appUser.companyId
        employee.createdOn = new Date()
        employee.createdBy = appUser.id
        employee.updatedOn = null
        employee.updatedBy = 0
        return employee
    }

}