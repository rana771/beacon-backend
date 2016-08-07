package com.athena.mis.application.actions.appuserentity

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.model.ListAppUserEntityActionServiceModel
import com.athena.mis.application.service.AppUserEntityService
import com.athena.mis.application.service.ListAppUserEntityActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update AppUserEntity object and grid data
 *  For details go through Use-Case doc named 'UpdateAppUserEntityActionService'
 */
class UpdateAppUserEntityActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_USER_ENTITY = "appUserEntity"
    private static final String UPDATE_SUCCESS_MESSAGE = "User entity mapping has been updated successfully"

    AppUserEntityService appUserEntityService
    ListAppUserEntityActionServiceModelService listAppUserEntityActionServiceModelService

    /**
     * 1. check required parameters
     * 2. check existence of AppUserEntity object
     * 3. build AppUserEntity object for update
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long appUserEntityId = Long.parseLong(params.id)
            AppUserEntity oldAppUserEntity = appUserEntityService.read(appUserEntityId)
            if (!oldAppUserEntity) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            // build AppUserEntity object to update
            AppUserEntity appUserEntity = getAppUserEntity(params, oldAppUserEntity)
            params.put(APP_USER_ENTITY, appUserEntity)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Update AppUserEntity object (user entity mapping) in DB
     * Update relevant cache utility
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppUserEntity appUserEntity = (AppUserEntity) result.get(APP_USER_ENTITY)
            // Update AppUserEntity object (user entity mapping) in DB
            appUserEntityService.update(appUserEntity)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     *
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. put success message
     * 2. get updated appUserEntityModel object
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing appUserEntityModel object and success message
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppUserEntity appUserEntity = (AppUserEntity) result.get(APP_USER_ENTITY)
            ListAppUserEntityActionServiceModel appUserEntityModel = listAppUserEntityActionServiceModelService.read(appUserEntity.id)
            result.put(APP_USER_ENTITY, appUserEntityModel)
            return super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * build AppUserEntity object to update in DB
     * @param params -serialized parameters from UI
     * @param oldAppUserEntity -object of AppUserEntity
     * @return -AppUserEntity object
     */
    private AppUserEntity getAppUserEntity(Map params, AppUserEntity oldAppUserEntity) {
        AppUserEntity appUserEntity = new AppUserEntity(params)
        oldAppUserEntity.appUserId = appUserEntity.appUserId
        return oldAppUserEntity
    }
}
