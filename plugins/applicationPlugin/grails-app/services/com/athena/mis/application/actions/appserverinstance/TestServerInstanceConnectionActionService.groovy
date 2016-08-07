package com.athena.mis.application.actions.appserverinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppServerInstance
import com.athena.mis.application.model.ListAppServerInstanceActionServiceModel
import com.athena.mis.application.service.AppServerInstanceService
import com.athena.mis.application.service.ListAppServerInstanceActionServiceModelService
import com.athena.mis.application.utility.AppServerConnection
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class TestServerInstanceConnectionActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String SERVER_INSTANCE = "serverInstance"
    private static final String CREATE_SUCCESS_MESSAGE = "Server instance has been successfully tested"

    AppServerInstanceService appServerInstanceService
    ListAppServerInstanceActionServiceModelService listAppServerInstanceActionServiceModelService

    /**
     * check Validation
     *
     * @param params - Request parameters
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            //Check parameters
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long serverInstanceId = Long.parseLong(params.id)
            AppServerInstance serverInstance = appServerInstanceService.read(serverInstanceId)
            params.put(SERVER_INSTANCE, serverInstance)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Get AppServerInstance object from map
     * Update AppServerInstance object
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppServerInstance serverInstance = (AppServerInstance) result.get(SERVER_INSTANCE)
            String message = checkServerInstance(serverInstance)
            if (message) {
                serverInstance.isTested = false
                appServerInstanceService.update(serverInstance)
                return super.setError(result, message)
            }
            serverInstance.isTested = true
            appServerInstanceService.update(serverInstance)
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
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        AppServerInstance appServerInstance = (AppServerInstance) result.get(SERVER_INSTANCE)
        ListAppServerInstanceActionServiceModel actionServiceModel = listAppServerInstanceActionServiceModelService.read(appServerInstance.id)
        result.put(SERVER_INSTANCE, actionServiceModel)
        return super.setSuccess(result, CREATE_SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        AppServerInstance appServerInstance = (AppServerInstance) result.get(SERVER_INSTANCE)
        ListAppServerInstanceActionServiceModel actionServiceModel = listAppServerInstanceActionServiceModelService.read(appServerInstance.id)
        result.put(SERVER_INSTANCE, actionServiceModel)
        return result
    }

    private String checkServerInstance(AppServerInstance appServerInstance) {
        Map output = AppServerConnection.testConnection(appServerInstance, 'ls /tmp')
        boolean isError = Boolean.parseBoolean(output.isError.toString())
        if (isError == true) {
            return output.exception.toString()
        }
        return null
    }
}
