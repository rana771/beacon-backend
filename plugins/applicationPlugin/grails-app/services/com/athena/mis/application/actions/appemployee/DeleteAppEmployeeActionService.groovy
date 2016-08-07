package com.athena.mis.application.actions.appemployee

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppEmployee
import com.athena.mis.application.service.AppEmployeeService
import com.athena.mis.integration.accounting.AccPluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete employee object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteEmployeeActionService'
 */
class DeleteAppEmployeeActionService extends BaseService implements ActionServiceIntf {

    AppEmployeeService appEmployeeService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService

    private static
    final String HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS = " voucher information(s) associated with this employee"
    private static final String HAS_ASSOCIATION_MESSAGE_IOU_SLIP = " Iou Slip(s) associated with this employee"
    private static final String HAS_ASSOCIATION_MESSAGE_APP_USER = " user(s) associated with this employee"
    private static final String EMPLOYEE_DELETE_SUCCESS_MSG = "Employee has been successfully deleted"
    private static final String EMPLOYEE = "employee"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check required parameter
     * 2. pull Employee object from service
     * 3. check for Employee object existence
     * 4. association check for Employee with different domains
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long employeeId = Long.parseLong(params.id.toString())
            AppEmployee employee = appEmployeeService.read(employeeId)    // get employee object
            // check whether selected employee object exists or not
            if (!employee) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            // check association of employee with relevant domains
            String msg = hasAssociation(employee)
            if (msg) {
                return super.setError(params, msg)
            }
            params.put(EMPLOYEE, employee)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Delete Employee object from DB
     * 1. get the Employee object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppEmployee employee = (AppEmployee) result.get(EMPLOYEE)
            appEmployeeService.delete(employee)    // delete employee object from DB
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, EMPLOYEE_DELETE_SUCCESS_MSG)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Check association of employee with relevant domains
     * @param employee -employee object
     * @return -a string containing error message or null value depending on association check
     */
    private String hasAssociation(AppEmployee employee) {
        int employeeId = employee.id
        int count = 0
        // count appUser associated with the employee
        count = countAppUser(employeeId)
        if (count.intValue() > 0) {
            return count.toString() + HAS_ASSOCIATION_MESSAGE_APP_USER
        }
        if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
            // count accVoucherDetails associated with the employee
            count = countVoucherDetails(employeeId)
            if (count.intValue() > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS
            }
            // count accIouSlip associated with the employee
            count = countIouSlip(employeeId)
            if (count.intValue() > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_IOU_SLIP
            }
        }
        return null
    }

    private static final String QUERY_APP_USER = """
            SELECT COUNT(id) AS count
            FROM app_user
            WHERE employee_id = :employeeId
    """

    /**
     * Count appUser associated with the employee
     * @param employeeId -id of employee object
     * @return -an integer containing the value of count
     */
    private int countAppUser(int employeeId) {
        Map queryParams = [employeeId: employeeId]
        List results = executeSelectSql(QUERY_APP_USER, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_VOUCHER_DETAILS = """
            SELECT COUNT(id) AS count
            FROM acc_voucher_details
            WHERE source_type_id = :sourceTypeId
            AND source_id = :employeeId
    """

    /**
     * Count accVoucherDetails associated with the employee
     * @param employeeId -id of employee object
     * @return -an integer containing the value of count
     */
    private int countVoucherDetails(int employeeId) {
        Map queryParams = [
                employeeId: employeeId,
                sourceTypeId: accAccountingImplService.getAccSourceTypeEmployee()
        ]
        List results = executeSelectSql(QUERY_VOUCHER_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_IOU_SLIP = """
            SELECT COUNT(id) AS count
            FROM acc_iou_slip
            WHERE employee_id = :employeeId
    """

    /**
     * Count accIouSlip associated with the employee
     * @param employeeId -id of employee object
     * @return -an integer containing the value of count
     */
    private int countIouSlip(int employeeId) {
        Map queryParams = [employeeId: employeeId]
        List results = executeSelectSql(QUERY_IOU_SLIP, queryParams)
        int count = results[0].count
        return count
    }
}
