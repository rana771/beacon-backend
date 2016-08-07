package com.athena.mis.application.actions.appgroup

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppGroup
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppGroupService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.AppUserEntityService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete user group(AppGroup) object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAppGroupActionService'
 */
class DeleteAppGroupActionService extends BaseService implements ActionServiceIntf {

    AppGroupService appGroupService
    AppUserEntityService appUserEntityService
    AppSystemEntityCacheService appSystemEntityCacheService

    private static final String GROUP_DELETE_SUCCESS_MSG = "Group has been successfully deleted"
    private static final String APP_GROUP_OBJ = "appGroup"
    private static
    final String HAS_ASSOCIATION_USER_GROUP_MAPPING = " User-Group-Mapping associated with selected Group"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check required parameter
     * 2. pull AppGroup object from service
     * 3. check for AppGroup object existence
     * 4. association check for AppGroup with different domains
     * @param params - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameter
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long groupId = Long.parseLong(params.id.toString())
            AppGroup appGroup = appGroupService.read(groupId)
            if (!appGroup) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            // check association with relevant domains
            String msg = hasAssociation(appGroup.id)
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
     * Delete AppGroup object from DB
     * 1. get the AppGroup object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppGroup appGroup = (AppGroup) result.get(APP_GROUP_OBJ)
            appGroupService.delete(appGroup)
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
        return super.setSuccess(result, GROUP_DELETE_SUCCESS_MSG)
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
     * Check association with relevant domains
     * @param groupId -AppGroup.id
     * @return -a string containing error message or null value depending on association check
     */
    private String hasAssociation(long groupId) {
        SystemEntity appUserEntityGroup = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_GROUP, appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, super.getCompanyId())
        int count = appUserEntityService.countByEntityIdAndEntityTypeId(groupId, appUserEntityGroup.id)
        if (count > 0) {
            return count + HAS_ASSOCIATION_USER_GROUP_MAPPING
        }
        return null
    }
}
