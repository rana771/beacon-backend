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
 *  Create new AppUserEntity object and show in grid
 *  For details go through Use-Case doc named 'CreateAppUserEntityActionService'
 */
class CreateAppUserEntityActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_USER_ENTITY = "appUserEntity"
    private static final String SAVE_SUCCESS_MESSAGE = "User entity mapping has been saved successfully"

    AppUserEntityService appUserEntityService
    ListAppUserEntityActionServiceModelService listAppUserEntityActionServiceModelService

    /**
     * 1. check required parameters
     * 2. build AppUserEntity object for create
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if ((!params.entityTypeId) || (!params.entityId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // build AppUserEntity object to create
            AppUserEntity appUserEntity = getAppUserEntity(params)
            params.put(APP_USER_ENTITY, appUserEntity)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Save AppUserEntity object (user entity mapping) in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppUserEntity appUserEntity = (AppUserEntity) result.get(APP_USER_ENTITY)
            // Save AppUserEntity object (user entity mapping) in DB
            appUserEntityService.create(appUserEntity)
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
     * Wrap newly created AppUserEntity object to show on grid
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing appUserEntityModel object and success message
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppUserEntity appUserEntity = (AppUserEntity) result.get(APP_USER_ENTITY)
            ListAppUserEntityActionServiceModel appUserEntityModel = listAppUserEntityActionServiceModelService.read(appUserEntity.id)
            result.put(APP_USER_ENTITY, appUserEntityModel)
            return setSuccess(result, SAVE_SUCCESS_MESSAGE)
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
     * build AppUserEntity object to save in DB
     * @param parameterMap -serialized parameters from UI
     * @return -AppUserEntity object
     */
    private AppUserEntity getAppUserEntity(Map params) {
        AppUserEntity appUserEntity = new AppUserEntity(params)
        appUserEntity.companyId = super.getCompanyId()
        return appUserEntity
    }
}
