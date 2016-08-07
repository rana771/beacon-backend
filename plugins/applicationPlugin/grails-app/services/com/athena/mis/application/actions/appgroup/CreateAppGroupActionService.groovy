package com.athena.mis.application.actions.appgroup

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppGroup
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppGroupService
import com.athena.mis.ActionServiceIntf
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new group(AppGroup) object and show in grid
 *  For details go through Use-Case doc named 'CreateAppGroupActionService'
 */
class CreateAppGroupActionService extends BaseService implements ActionServiceIntf {

    AppGroupService appGroupService

    private static final String GROUP_CREATE_SUCCESS_MSG = "Group has been successfully saved"
    private static final String DUPLICATE_NAME_FAILURE_MSG = "Duplicate Name found"
    private static final String APP_GROUP = "appGroup"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check required parameters
     * 2. build AppGroup object with parameters
     * 3. ensure uniqueness of name
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.name) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // build AppGroup object with parameters
            AppGroup appGroup = buildAppGroupObject(params)
            // ensure uniqueness of name
            String msg = checkUniquenessOfName(appGroup)
            if (msg) {
                return super.setError(params, msg)
            }
            params.put(APP_GROUP, appGroup)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive AppGroup object from executePreCondition method
     * 2. create new AppGroup
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppGroup appGroup = (AppGroup) result.get(APP_GROUP)
            // save new project object in DB
            appGroupService.create(appGroup)
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
        return super.setSuccess(result, GROUP_CREATE_SUCCESS_MSG)
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
     * Build AppGroup object
     * @param parameterMap - serialized parameters from UI
     * @return - newly built AppGroup object
     */
    private AppGroup buildAppGroupObject(Map parameterMap) {
        AppUser user = super.getAppUser()
        AppGroup appGroup = new AppGroup(parameterMap)
        appGroup.createdOn = new Date()
        appGroup.createdBy = user.id
        appGroup.companyId = user.companyId
        return appGroup
    }

    /**
     * Check uniqueness of name
     * @param appGroup - object of AppGroup
     * @return - a string containing error message or null value depending on uniqueness of name
     */
    private String checkUniquenessOfName(AppGroup appGroup) {
        int count = getProjectCountByName(appGroup)
        if (count > 0) {
            return DUPLICATE_NAME_FAILURE_MSG
        }
        return null
    }

    /**
     * Get count of AppGroup by name
     * @param appGroup - object of AppGroup
     * @return - count of AppGroup by name
     */
    private int getProjectCountByName(AppGroup appGroup) {
        return appGroupService.countByNameIlikeAndCompanyId(appGroup.name, appGroup.companyId)
    }
}