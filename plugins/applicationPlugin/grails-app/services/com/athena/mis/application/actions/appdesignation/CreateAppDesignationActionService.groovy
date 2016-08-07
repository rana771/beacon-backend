package com.athena.mis.application.actions.appdesignation

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppDesignation
import com.athena.mis.application.service.AppDesignationService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new AppDesignation object and show in grid
 *  For details go through Use-Case doc named 'CreateDesignationActionService'
 */
class CreateAppDesignationActionService extends BaseService implements ActionServiceIntf {

    AppDesignationService appDesignationService

    private static final String CREATE_SUCCESS_MSG = "Designation has been successfully saved"
    private static final String DESIGNATION = "designation"
    private static final String NAME_EXISTS = "Same designation name already exists"
    private static final String SHORT_NAME_EXISTS = "Same short name already exists"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. Check input validation
     * 2. Build AppDesignation object
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            AppUser appUser = super.getAppUser()
            // check input validation
            String msg = checkInputValidation(params, appUser.companyId)
            if (msg) {
                return super.setError(params, msg)
            }
            // build Designation object
            AppDesignation designation = buildDesignationObject(params, appUser)
            params.put(DESIGNATION, designation)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. receive Designation object from executePreCondition method
     * 2. create new Designation
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppDesignation designation = (AppDesignation) result.get(DESIGNATION)
            appDesignationService.create(designation)
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
        return super.setSuccess(result, CREATE_SUCCESS_MSG)
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
     * 1. check required parameters
     * 2. ensure uniqueness of Designation name
     * 3. ensure uniqueness of Designation short name
     * @param params - serialized parameters from UI
     * @param companyId - Company.id
     * @return - a string containing error message or null value depending on input validation
     */
    private String checkInputValidation(Map params, long companyId) {
        // Check required parameters
        if ((!params.name) || (!params.shortName)) {
            return ERROR_FOR_INVALID_INPUT
        }
        int count
        // Check existing of same AppDesignation name
        String name = params.name.toString()
        count = appDesignationService.countByNameIlikeAndCompanyId(name, companyId)
        if (count > 0) {
            return NAME_EXISTS
        }
        // Check existing of same AppDesignation shortName
        String shortName = params.shortName.toString()
        count = appDesignationService.countByShortNameIlikeAndCompanyId(shortName, companyId)
        if (count > 0) {
            return SHORT_NAME_EXISTS
        }
        return null
    }

    /**
     * Build AppDesignation object
     * @param parameterMap - serialized parameters from UI
     * @param systemUser - AppUser object of logged in user
     * @return - new AppDesignation object
     */
    private AppDesignation buildDesignationObject(Map parameterMap, AppUser systemUser) {
        AppDesignation designation = new AppDesignation(parameterMap)
        designation.companyId = systemUser.companyId
        designation.createdOn = new Date()
        designation.createdBy = systemUser.id
        return designation
    }
}
