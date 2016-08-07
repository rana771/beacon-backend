package com.athena.mis.application.actions.appserverdbinstancemapping

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.AppServerDbInstanceMapping
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.model.ListAppServerDbInstanceMappingActionServiceModel
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppServerDbInstanceMappingService
import com.athena.mis.application.service.ListAppServerDbInstanceMappingActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update new AppServerDbInstanceMapping object and show in grid
 *  For details go through Use-Case app named 'UpdateAppServerDbInstanceMappingActionService'
 */
class UpdateAppServerDbInstanceMappingActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_SERVER_DB_INSTANCE_MAPPING = 'appServerDbInstanceMapping'
    private static final String UPDATE_SUCCESS_MESSAGE = 'Server Db Instance Mapping has been successfully update'
    private static final String SERVER_DB_INSTANCE_MAPPING_ALREADY_EXIST = "Server Db Instance Mapping already exist within DB Vendor"
    private static final String OBJ_NOT_FOUND = "App DbInstance could not be found!"

    AppDbInstanceService appDbInstanceService
    AppServerDbInstanceMappingService appServerDbInstanceMappingService
    ListAppServerDbInstanceMappingActionServiceModelService listAppServerDbInstanceMappingActionServiceModelService

    /**
     * 1. check required parameters
     * 2. pull AppServerDbInstanceMapping object from service
     * 3. check validation
     * 4. get updated AppServerDbInstanceMapping for update
     *
     * @param params - serialize parameters form UI
     * @return - A map of containing AppServerDbInstanceMapping object or error message
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if ((!params.id) || (!params.version) || (!params.appServerInstanceId) || (!params.appDbInstanceId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long serverMappingId = Long.parseLong(params.id.toString())
            // pull AppServerDbInstanceMapping object from service
            AppServerDbInstanceMapping oldServerMapping = appServerDbInstanceMappingService.read(serverMappingId)

            long appDbInstanceId = Long.parseLong(params.appDbInstanceId.toString())
            AppDbInstance appDbInstance = appDbInstanceService.read(appDbInstanceId)

            // check validation
            String errorMsg = checkValidation(params, oldServerMapping, appDbInstance)
            if (errorMsg) {
                return super.setError(params, errorMsg)
            }
            // get updated AppServerDbInstanceMapping for update
            AppServerDbInstanceMapping serverMapping = getServerMapping(params, oldServerMapping, appDbInstance)
            params.put(APP_SERVER_DB_INSTANCE_MAPPING, serverMapping)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. get AppServerDbInstanceMapping object form map
     * 2. Update AppServerDbInstanceMapping object to DB
     * Ths method is in Transactional boundary so will rollback in case of any exception
     *
     * @param result - A map from preCondition
     * @return - A map of containing AppServerDbInstanceMapping object or error message
     */
    @Transactional
    public Map execute(Map result) {
        try {
            // get AppServerDbInstanceMapping object form map
            AppServerDbInstanceMapping serverMapping = (AppServerDbInstanceMapping) result.get(APP_SERVER_DB_INSTANCE_MAPPING)
            // update AppServerDbInstanceMapping into DB
            appServerDbInstanceMappingService.update(serverMapping)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
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
     * Show newly updated AppServerDbInstanceMapping to grid
     * put success message
     *
     * @param result - map from execute method
     * @return - A map containing all necessary object for show
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppServerDbInstanceMapping serverMapping = (AppServerDbInstanceMapping) result.get(APP_SERVER_DB_INSTANCE_MAPPING)
            ListAppServerDbInstanceMappingActionServiceModel serverMappingModel = listAppServerDbInstanceMappingActionServiceModelService.read(serverMapping.id)
            result.put(APP_SERVER_DB_INSTANCE_MAPPING, serverMappingModel)
            return super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     *
     *
     * @param params - serialize parameters form UI
     * @param oldServerMapping - an object of AppServerDbInstanceMapping
     * @return - error message or nothing
     */
    private String checkValidation(Map params, AppServerDbInstanceMapping oldServerMapping, AppDbInstance appDbInstance) {
        long version = Long.parseLong(params.version.toString())
        // check oldServerMapping object existence
        if (!oldServerMapping || (oldServerMapping.version != version)) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        long appServerInstanceId = Long.parseLong(params.appServerInstanceId.toString())
        // check duplicate AppServerDbInstanceMapping object by appServerInstance, dbVendor
        int duplicateCountByServerMapping = appServerDbInstanceMappingService.countByAppServerInstanceIdAndDbVendorIdAndIdNotEqual(appServerInstanceId, appDbInstance.vendorId, oldServerMapping.id)
        if (duplicateCountByServerMapping > 0) {
            return SERVER_DB_INSTANCE_MAPPING_ALREADY_EXIST
        }
        // check appDbInstance object existence
        if (!appDbInstance) {
            return OBJ_NOT_FOUND
        }
        return null
    }

    /**
     * Build AppServerDbInstanceMapping object for update
     *
     * @param params - a serialize map from UI
     * @param oldServerMapping - an object of AppServerDbInstanceMapping
     * @param appDbInstance - an object of AppDbInstance
     * @return AppServerDbInstanceMapping object for update
     */
    private AppServerDbInstanceMapping getServerMapping(Map params, AppServerDbInstanceMapping oldServerMapping, AppDbInstance appDbInstance) {
        AppUser appUser = super.getAppUser()
        AppServerDbInstanceMapping serverMapping = new AppServerDbInstanceMapping(params)
        oldServerMapping.appServerInstanceId = serverMapping.appServerInstanceId
        oldServerMapping.appDbInstanceId = serverMapping.appDbInstanceId
        oldServerMapping.dbVendorId = appDbInstance.vendorId
        oldServerMapping.updatedBy = appUser.id
        oldServerMapping.updatedOn = new Date()
        return oldServerMapping
    }
}
