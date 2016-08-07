package com.athena.mis.application.actions.appdesignation

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppDesignation
import com.athena.mis.application.service.AppDesignationService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update new designation object and show in grid
 *  For details go through Use-Case doc named 'UpdateDesignationActionService'
 */
class UpdateAppDesignationActionService extends BaseService implements ActionServiceIntf {

    AppDesignationService appDesignationService

    private static final String UPDATE_SUCCESS_MESSAGE = "Designation has been updated successfully"
    private static
    final String OBJ_CHANGED_MSG = "Selected designation has been changed by other user, Refresh the page again"
    private static final String DESIGNATION = "designation"
    private static final String NAME_EXISTS = "Same designation name already exists"
    private static final String SHORT_NAME_EXISTS = "Same short name already exists"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. Check required parameters
     * 2. Check uniqueness of Designation name and short name
     * 3. Build designation object for update
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // Check required parameters
            if ((!params.id) || (!params.version) || (!params.name) || (!params.shortName)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())
            // Check existence of Obj and version matching
            AppDesignation oldDesignation = appDesignationService.read(id)
            if ((!oldDesignation) || (oldDesignation.version != version)) {
                return super.setError(params, OBJ_CHANGED_MSG)
            }
            // check uniqueness
            String msg = checkUniqueness(params, oldDesignation)
            if (msg) {
                return super.setError(params, msg)
            }
            // build Designation object for update
            AppDesignation designation = buildDesignationObject(params, oldDesignation)
            params.put(DESIGNATION, designation)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Update designation object in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppDesignation designation = (AppDesignation) result.get(DESIGNATION)
            appDesignationService.update(designation)
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
        return super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
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
     * 1. ensure uniqueness of Designation name
     * 2. ensure uniqueness of Designation short name
     * @param params -serialized parameters from UI
     * @param oldDesignation -object of Designation
     * @return -a string containing error message or null value depending on uniqueness check
     */
    private String checkUniqueness(Map params, AppDesignation oldDesignation) {
        long designationId = oldDesignation.id
        long companyId = oldDesignation.companyId
        int count
        // Check existence of same designation name
        String name = params.name.toString()
        count = appDesignationService.countByNameIlikeAndCompanyIdAndIdNotEqual(name, companyId, designationId)
        if (count > 0) {
            return NAME_EXISTS
        }
        // Check existence of same designation shortName
        String shortName = params.shortName.toString()
        count = appDesignationService.countByShortNameIlikeAndCompanyIdAndIdNotEqual(shortName, companyId, designationId)
        if (count > 0) {
            return SHORT_NAME_EXISTS
        }
        return null
    }

    /**
     * Build designation object
     * @param parameterMap -serialized parameters from UI
     * @param oldDesignation -object of Designation
     * @return -new designation object
     */
    private AppDesignation buildDesignationObject(Map parameterMap, AppDesignation oldDesignation) {
        oldDesignation.name = parameterMap.name.toString()
        oldDesignation.shortName = parameterMap.shortName.toString()
        oldDesignation.updatedBy = super.getAppUser().id
        oldDesignation.updatedOn = new Date()
        return oldDesignation
    }
}