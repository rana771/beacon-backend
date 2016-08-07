package com.athena.mis.application.actions.appemployee

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppEmployee
import com.athena.mis.application.model.ListAppEmployeeActionServiceModel
import com.athena.mis.application.service.AppEmployeeService
import com.athena.mis.application.service.ListAppEmployeeActionServiceModelService
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Update employee object and grid data
 *  For details go through Use-Case doc named 'UpdateEmployeeActionService'
 */
class UpdateAppEmployeeActionService extends BaseService implements ActionServiceIntf {

    AppEmployeeService appEmployeeService
    ListAppEmployeeActionServiceModelService listAppEmployeeActionServiceModelService

    private static final String SUCCESS_MESSAGE = "Employee has been updated successfully"
    private static final String OBJ_NOT_FOUND = "Selected employee not found"
    private static final String OBJ_MODIFIED = "Selected employee already modified/deleted by someone. Please refresh grid and try again"
    private static final String EMPLOYEE = "employee"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Get parameters from UI and build employee object for update
     * @param parameters - serialized params from ui
     * @return - map containing employee object built for update
     */
    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try{
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id || !parameterMap.version) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            long employeeId = Long.parseLong(parameterMap.id.toString())
            long employeeVersion = Long.parseLong(parameterMap.version.toString())

            AppEmployee oldEmployee = appEmployeeService.read(employeeId) // get employee object
            if (!oldEmployee) {
                return super.setError(parameters, OBJ_NOT_FOUND)
            }
            if(oldEmployee.version != employeeVersion) {
                return super.setError(parameters, OBJ_MODIFIED)
            }
            AppEmployee employee = buildEmployeeObject(parameterMap, oldEmployee)  // build employee object for update
            parameters.put(EMPLOYEE, employee)
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Update employee object in DB
     * This function is in transactional block and will roll back in case of any exception
     * @param previousResult
     * @return
     */
    @Transactional
    Map execute(Map previousResult) {
        try{
            AppEmployee employee = (AppEmployee) previousResult.get(EMPLOYEE)
            appEmployeeService.update(employee)
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    /**
     * read updated employee object
     * @param executeResult
     * @return executeResult
     */
    @Transactional(readOnly = true)
    Map buildSuccessResultForUI(Map executeResult) {
        try{
            AppEmployee employee = (AppEmployee) executeResult.get(EMPLOYEE)
            ListAppEmployeeActionServiceModel savedEmployee = listAppEmployeeActionServiceModelService.read(employee.id)
            executeResult.put(EMPLOYEE, savedEmployee)
            return super.setSuccess(executeResult, SUCCESS_MESSAGE)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * Build employee object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldEmployee -old employee object
     * @return -updated employee object
     */
    private AppEmployee buildEmployeeObject(GrailsParameterMap parameterMap, AppEmployee oldEmployee) {
        AppEmployee employee = new AppEmployee(parameterMap)
        Date dateOfBirth = DateUtility.parseMaskedDate(parameterMap.dateOfBirth.toString())
        Date dateOfJoin = DateUtility.parseMaskedDate(parameterMap.dateOfJoin.toString())
        oldEmployee.fullName = employee.fullName
        oldEmployee.nickName = employee.nickName
        oldEmployee.designationId = employee.designationId
        oldEmployee.mobileNo = employee.mobileNo
        oldEmployee.email = employee.email
        oldEmployee.dateOfBirth = dateOfBirth
        oldEmployee.dateOfJoin = dateOfJoin
        oldEmployee.address = employee.address
        oldEmployee.updatedBy = super.appUser.id
        oldEmployee.updatedOn = new Date()
        return oldEmployee
    }

}