package com.athena.mis.application.actions.appuserentity

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.service.AppUserEntityService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete AppUserEntity object from db
 *  For details go through Use-Case doc named 'DeleteAppUserEntityActionService'
 */
class DeleteAppUserEntityActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    AppUserEntityService appUserEntityService

    private static final String NOT_FOUND_MESSAGE = "Selected app user entity object not found"
    private static final String SUCCESS_MESSAGE = "User entity mapping has been deleted successfully"
    private static final String APP_USER_ENTITY = "appUserEntity"

    /**
     * 1. check required parameter
     * 2. pull appUserEntity object from service
     * 3. check for appUserEntity object existence
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check existence of required parameter
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long appUserEntityId = Long.parseLong(params.id.toString())
            AppUserEntity appUserEntity = appUserEntityService.read(appUserEntityId)
            // check existence of AppUserEntity object
            if (!appUserEntity) {
                return super.setError(params, NOT_FOUND_MESSAGE)
            }
            params.put(APP_USER_ENTITY, appUserEntity)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Delete appUserEntity object from DB
     * 1. get the appUserEntity object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppUserEntity appUserEntity = (AppUserEntity) result.get(APP_USER_ENTITY)
            appUserEntityService.delete(appUserEntity) //delete object from DB
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
        return super.setSuccess(result, SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
