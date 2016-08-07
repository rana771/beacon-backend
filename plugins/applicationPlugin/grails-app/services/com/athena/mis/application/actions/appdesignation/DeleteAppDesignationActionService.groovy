package com.athena.mis.application.actions.appdesignation

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppDesignation
import com.athena.mis.application.service.AppDesignationService
import com.athena.mis.ActionServiceIntf
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete AppDesignation object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteDesignationActionService'
 */
class DeleteAppDesignationActionService extends BaseService implements ActionServiceIntf {

    AppDesignationService appDesignationService

    private static final String HAS_ASSOCIATION_EMPLOYEE = " employee is associated with selected designation"
    private static final String DELETE_SUCCESS_MSG = "Designation has been successfully deleted"
    private static final String OBJ_NOT_FOUND_MSG = "Selected designation not found, Refresh the page"
    private static final String DESIGNATION = "designation"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. Check input validity
     * 2. pull AppDesignation object by AppDesignation id
     * 3. check AppDesignation existence
     * 4. Check employee-AppDesignation association
     * @param params - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long designationId = Long.parseLong(params.id.toString())
            AppDesignation designation = appDesignationService.read(designationId)
            // Check existence of object
            if (!designation) {
                return super.setError(params, OBJ_NOT_FOUND_MSG)
            }
            // Check association
            String msg = hasAssociation(designation)
            if (msg) {
                return super.setError(params, msg)
            }
            params.put(DESIGNATION, designation)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Delete AppDesignation object from DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppDesignation designation = (AppDesignation) result.get(DESIGNATION)
            appDesignationService.delete(designation)
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
        return super.setSuccess(result, DELETE_SUCCESS_MSG)
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
     * 1. Check AppDesignation-employee association
     * @param AppDesignation -AppDesignation object
     * @return -a map containing hasAssociation(true/false) and relevant message
     */
    private String hasAssociation(AppDesignation designation) {
        long designationId = designation.id
        long companyId = designation.companyId
        int count

        count = countEmployee(designationId, companyId)
        if (count > 0) {
            return HAS_ASSOCIATION_EMPLOYEE
        }
        return null
    }

    private static final String SELECT_QUERY = """
        SELECT COUNT(id) AS count
        FROM app_employee
        WHERE designation_id = :designationId
        AND company_id = :companyId
    """

    /**
     * Get total employee number of a specific AppDesignation
     * @param designationId - AppDesignation id
     * @param companyId - company id
     * @return - int value of total number of employees of a specific AppDesignation
     */
    private int countEmployee(long designationId, long companyId) {
        Map queryParams = [
                designationId: designationId,
                companyId: companyId
        ]
        List results = executeSelectSql(SELECT_QUERY, queryParams)
        int count = results[0].count
        return count
    }
}

