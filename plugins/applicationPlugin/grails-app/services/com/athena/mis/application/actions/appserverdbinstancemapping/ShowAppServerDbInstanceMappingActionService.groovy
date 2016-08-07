package com.athena.mis.application.actions.appserverdbinstancemapping

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppServerInstance
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.AppServerInstanceService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for App Server Db Instance Mapping
 *  For details go through Use-Case app named 'ShowAppServerDbInstanceMappingActionService'
 */
class ShowAppServerDbInstanceMappingActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_SERVER_INSTANCE_ID = "appServerInstanceId"
    private static final String APP_SERVER_INSTANCE_NAME = "appServerInstanceName"
    private static final String NOT_FOUND = "Server Instance could not be found"

    AppServerInstanceService appServerInstanceService
    AppMyFavouriteService appMyFavouriteService

    /**
     * 1. check required parameter
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        // check required parameter
        if ((!params.oId)) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        return params
    }

    /**
     * 1. read AppServerInstance object from service
     * 2. check AppServerInstance object existence
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */

    @Transactional
    public Map execute(Map result) {
        try {
            long appServerInstanceId = Long.parseLong(result.oId.toString())
            // pull AppServerInstance object form service
            AppServerInstance serverInstance = appServerInstanceService.read(appServerInstanceId)
            String errMsg = isAppServerInstanceExist(serverInstance)
            // check AppServerInstance object existence
            if (errMsg) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, errMsg)
            }
            result.put(APP_SERVER_INSTANCE_ID, appServerInstanceId)
            result.put(APP_SERVER_INSTANCE_NAME, serverInstance.name)
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
     * For show there is no success message
     * since the input-parameter already have "isError:false", just return the same map
     *
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
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
     * 1. check AppServerInstance instance existence
     *
     * @param serverInstance - an object of AppServerInstance
     * @return error message when error occurred or null
     */
    private String isAppServerInstanceExist(AppServerInstance serverInstance) {
        // check AppServerInstance object existence
        if (!serverInstance) {
            return NOT_FOUND
        }
        return null
    }
}
