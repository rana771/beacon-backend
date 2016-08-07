package com.athena.mis.application.actions.appserverdbinstancemapping

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppServerDbInstanceMapping
import com.athena.mis.application.service.AppServerDbInstanceMappingService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete AppServerDbInstanceMapping object from DB and remove it from grid
 *  For details go through Use-Case app named 'DeleteAppServerDbInstanceMappingActionService'
 */
class DeleteAppServerDbInstanceMappingActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_SERVER_DB_INSTANCE_MAPPING = 'AppServerDbInstanceMapping'
    private static final String DELETE_SUCCESS_MSG = 'Server Db Instance Mapping has been successfully deleted!'

    AppServerDbInstanceMappingService appServerDbInstanceMappingService

    /**
     * 1. check required parameter
     * 2. pull AppServerDbInstanceMapping object from service
     * 3. check for AppServerDbInstanceMapping object existence
     *
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
            long serverMappingId = Long.parseLong(params.id.toString())
            // pull AppServerDbInstanceMapping object from service
            AppServerDbInstanceMapping serverMapping = appServerDbInstanceMappingService.read(serverMappingId)

            // check for AppServerDbInstanceMapping object existence
            if (serverMapping == null) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            params.put(APP_SERVER_DB_INSTANCE_MAPPING, serverMapping)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Delete AppServerDbInstanceMapping object from DB
     * 1. get the AppServerDbInstanceMapping object from map
     * 2. delete AppServerDbInstanceMapping object from db
     * This function is in transactional boundary and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            // get AppServerDbInstanceMapping object form map
            AppServerDbInstanceMapping serverMapping = (AppServerDbInstanceMapping) result.get(APP_SERVER_DB_INSTANCE_MAPPING)
            // delete AppServerDbInstanceMapping object from db
            appServerDbInstanceMappingService.delete(serverMapping)
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
     * 1. put success message
     *
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DELETE_SUCCESS_MSG)
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
}
