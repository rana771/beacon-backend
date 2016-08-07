package com.athena.mis.application.actions.appgroup

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppGroup
import com.athena.mis.application.service.AppGroupService
import com.athena.mis.ActionServiceIntf
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update new group(AppGroup) object and show in grid
 *  For details go through Use-Case doc named 'UpdateAppGroupActionService'
 */
class UpdateAppGroupActionService extends BaseService implements ActionServiceIntf {

    AppGroupService appGroupService

    private static final String GROUP_UPDATE_SUCCESS_MESSAGE = "Group has been updated successfully"
    private static final String APP_GROUP_OBJ = "appGroup"
    private static final String DUPLICATE_NAME_FAILURE_MSG = "Same group name already exists"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check required parameters
     * 2. pull AppGroup object
     * 3. check AppGroup existence
     * 4. ensure uniqueness of name
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.name || !params.id || !params.version) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long groupId = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())
            AppGroup oldGroup = appGroupService.read(groupId)
            // check if AppGroup object exist
            if ((!oldGroup) || (oldGroup.version != version)) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            // build AppGroup object for update
            AppGroup appGroup = buildAppGroupObject(params, oldGroup)
            // check for duplicate AppGroup name
            String msg = checkUniquenessOfName(appGroup)
            if (msg) {
                return super.setError(params, msg)
            }
            params.put(APP_GROUP_OBJ, appGroup)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the AppGroup object
     * 2. Update existing AppGroup in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppGroup appGroup = (AppGroup) result.get(APP_GROUP_OBJ)
            appGroupService.update(appGroup)
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
        return super.setSuccess(result, GROUP_UPDATE_SUCCESS_MESSAGE)
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
     * Get newly built appGroup object
     * @param parameterMap - serialize parameters from UI
     * @param oldGroup - previous state of group object
     * @return - newly built appGroup object
     */
    private AppGroup buildAppGroupObject(Map parameterMap, AppGroup oldGroup) {
        oldGroup.name = parameterMap.name
        oldGroup.updatedOn = new Date()
        oldGroup.updatedBy = super.getAppUser().id
        return oldGroup
    }

    /**
     * Check uniqueness of name
     * @param appGroup -object of AppGroup
     * @return -a string containing error message or null value depending on uniqueness of name
     */
    private String checkUniquenessOfName(AppGroup appGroup) {
        int count = appGroupService.countByNameIlikeAndIdNotEqualAndCompanyId(appGroup.name, appGroup.id, appGroup.companyId)
        if (count > 0) {
            return DUPLICATE_NAME_FAILURE_MSG
        }
        return null
    }
}