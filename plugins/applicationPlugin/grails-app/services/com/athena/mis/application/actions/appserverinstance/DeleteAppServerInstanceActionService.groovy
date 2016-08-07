package com.athena.mis.application.actions.appserverinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppServerInstance
import com.athena.mis.application.service.AppServerDbInstanceMappingService
import com.athena.mis.application.service.AppServerInstanceService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete new AppServerInstance object and delete from grid
 *  For details go through Use-Case doc named 'DeleteAppServerInstanceActionService'
 */
class DeleteAppServerInstanceActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SERVER_INSTANCE = "serverInstance"
    private static final String DELETE_SUCCESS_MESSAGE = "Server instance has been successfully deleted"
    private static final String ASSOCIATION_WITH_SERVER_DB_MAPPING = " association with server DB Instance mapping"
    private static final String NATIVE_SERVER_INSTANCE = "Native server instance can not be deleted"

    AppServerInstanceService appServerInstanceService
    AppServerDbInstanceMappingService appServerDbInstanceMappingService

    /**
     * check Validation
     *
     * @param params - Request parameters
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check Validation
            String errMsg = checkValidation(params)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Get AppServerInstance object from map
     * Delete AppServerInstance object in DB
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppServerInstance appServerInstance = (AppServerInstance) result.get(SERVER_INSTANCE)
            appServerInstanceService.delete(appServerInstance)
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
     * Since there is no success message return the same map
     *
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DELETE_SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. check for AppServerInstance object existence
     * 2. check for AppServerDbInstanceMapping with AppServerInstance
     *
     * @param params - a map from caller method
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params) {
        String errMsg
        //Check parameters
        if (!params.id) {
            return ERROR_FOR_INVALID_INPUT
        }
        long id = Long.parseLong(params.id.toString())
        AppServerInstance appServerInstance = appServerInstanceService.read(id)

        //check for AppServerInstance object existence
        errMsg = checkServerInstanceExistence(appServerInstance)
        if (errMsg != null) return errMsg

        //check for AppServerDbInstanceMapping with AppServerInstance
        errMsg = checkServerDbInstanceCountById(appServerInstance)
        if (errMsg != null) return errMsg

        if (appServerInstance.isNative) {
            return NATIVE_SERVER_INSTANCE
        }
        params.put(SERVER_INSTANCE, appServerInstance)
        return null
    }

    /**
     * check for AppServerInstance object existence
     *
     * @param appServerInstance - an object of AppServerInstance
     * @return -  a string of error message or null
     */
    private String checkServerInstanceExistence(AppServerInstance appServerInstance) {
        if (!appServerInstance) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

    /**
     * check for AppServerDbInstanceMapping with AppServerInstance
     *
     * @param appServerInstance - an object of appServerInstance
     * @return - error message or null
     */
    private String checkServerDbInstanceCountById(AppServerInstance appServerInstance) {
        int count = appServerDbInstanceMappingService.countByAppServerInstanceId(appServerInstance.id)
        if (count > 0) {
            return count + ASSOCIATION_WITH_SERVER_DB_MAPPING
        }
        return null
    }
}