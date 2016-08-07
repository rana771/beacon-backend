package com.athena.mis.application.actions.appserverdbinstancemapping

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.AppServerDbInstanceMapping
import com.athena.mis.application.model.ListAppServerDbInstanceMappingActionServiceModel
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppServerDbInstanceMappingService
import com.athena.mis.application.service.ListAppServerDbInstanceMappingActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new ServerDbInstanceMapping object and show in grid
 *  For details go through Use-Case app named 'CreateAppServerDbInstanceMappingActionService'
 */
class CreateAppServerDbInstanceMappingActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_SERVER_DB_INSTANCE_MAPPING = 'appServerDbInstanceMapping'
    private static final String CREATE_SUCCESS_MESSAGE = 'Server Db Instance Mapping has been successfully saved'
    private static final String SERVER_DB_INSTANCE_MAPPING_ALREADY_EXIST = "Server Db Instance Mapping already exist within DB Vendor"
    private static final String OBJ_NOT_FOUND = "App DbInstance could not be found!"

    AppDbInstanceService appDbInstanceService
    AppServerDbInstanceMappingService appServerDbInstanceMappingService
    ListAppServerDbInstanceMappingActionServiceModelService listAppServerDbInstanceMappingActionServiceModelService

    /**
     * 1. check required parameters
     * 2. check Validation
     * 3. build AppServerDbInstanceMapping object
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {

            //Check parameters
            if ((!params.appServerInstanceId) || (!params.appDbInstanceId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long appDbInstanceId = Long.parseLong(params.appDbInstanceId.toString())
            AppDbInstance appDbInstance = appDbInstanceService.read(appDbInstanceId)
            // check validation
            String errorMsg = checkValidation(params, appDbInstance)
            if (errorMsg) {
                return super.setError(params, errorMsg)
            }
            // get ServerDbInstanceMapping
            AppServerDbInstanceMapping serverMapping = getServerMapping(params, appDbInstance)
            params.put(APP_SERVER_DB_INSTANCE_MAPPING, serverMapping)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. receive AppServerDbInstanceMapping object from executePreCondition method
     * 2. create new AppServerDbInstanceMapping
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppServerDbInstanceMapping serverMapping = (AppServerDbInstanceMapping) result.get(APP_SERVER_DB_INSTANCE_MAPPING)
            // save AppServerDbInstanceMapping object into DB
            appServerDbInstanceMappingService.create(serverMapping)
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
     *
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            AppServerDbInstanceMapping serverMapping = (AppServerDbInstanceMapping) result.get(APP_SERVER_DB_INSTANCE_MAPPING)
            ListAppServerDbInstanceMappingActionServiceModel serverMappingModel = listAppServerDbInstanceMappingActionServiceModelService.read(serverMapping.id)
            result.put(APP_SERVER_DB_INSTANCE_MAPPING, serverMappingModel)
            return super.setSuccess(result, CREATE_SUCCESS_MESSAGE)
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
     * Build AppServerDbInstanceMapping object
     *
     * @param params - serialize parameters form UI
     * @param appDbInstance - an object of AppDbInstance
     * @return new docDbInstance object
     */
    private AppServerDbInstanceMapping getServerMapping(Map params, AppDbInstance appDbInstance) {
        AppServerDbInstanceMapping appMapping = new AppServerDbInstanceMapping(params)
        appMapping.dbVendorId = appDbInstance.vendorId
        appMapping.companyId = appUser.companyId
        appMapping.createdBy = appUser.id
        appMapping.createdOn = new Date()
        appMapping.updatedBy = 0L
        appMapping.updatedOn = null
        return appMapping
    }

    /**
     * 1. check appDbInstance object
     * 2. check for duplicate server mapping
     *
     * @param params - serialize parameters form UI
     * @param appDbInstance - an object of AppDbInstance
     * @return - error message or nothing
     */
    private String checkValidation(Map params, AppDbInstance appDbInstance) {
        // check appDbInstance object existence
        if (!appDbInstance) {
            return OBJ_NOT_FOUND
        }
        long appServerInstanceId = Long.parseLong(params.appServerInstanceId.toString())
        // check duplicate AppServerDbInstanceMapping object by appServerInstance, dbVendor
        int duplicateCountByServerMapping = appServerDbInstanceMappingService.countByAppServerInstanceIdAndDbVendorId(appServerInstanceId, appDbInstance.vendorId)
        if (duplicateCountByServerMapping > 0) {
            return SERVER_DB_INSTANCE_MAPPING_ALREADY_EXIST
        }
        return null
    }
}
