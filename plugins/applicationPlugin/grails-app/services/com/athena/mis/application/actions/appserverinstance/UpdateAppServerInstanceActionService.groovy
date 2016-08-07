package com.athena.mis.application.actions.appserverinstance

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppServerInstance
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.model.ListAppServerInstanceActionServiceModel
import com.athena.mis.application.service.AppServerInstanceService
import com.athena.mis.application.service.ListAppServerInstanceActionServiceModelService
import com.athena.mis.application.utility.AppServerConnection
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update new AppServerInstance object and show in grid
 *  For details go through Use-Case doc named 'UpdateAppServerInstanceActionService'
 */
class UpdateAppServerInstanceActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SERVER_INSTANCE = "serverInstance"
    private static final String UPDATE_SUCCESS_MESSAGE = "Server instance has been successfully updated"
    private static final String SEVER_NAME_MUST_BE_UNIQUE = "Server instance name must be unique!"

    AppServerInstanceService appServerInstanceService
    ListAppServerInstanceActionServiceModelService listAppServerInstanceActionServiceModelService

    /**
     * Get appUser object from Session
     * check Validation
     *
     * @param params - Request parameters
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            AppUser appUser = super.getAppUser()
            // check Validation
            String errMsg = checkValidation(params, appUser)
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
     * Update AppServerInstance object in DB
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppServerInstance appServerInstance = (AppServerInstance) result.get(SERVER_INSTANCE)
            appServerInstanceService.update(appServerInstance)
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
        return super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
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
     * Build AppServerInstance object
     *
     * @param params - serialize parameters from UI
     * @param user - an object of AppUser
     * @param oldServerInstance - an object of AppServerInstance
     * @return - AppServerInstance object
     */
    private AppServerInstance getServerInstance(Map params, AppUser user, AppServerInstance oldServerInstance) {
        AppServerInstance appServerInstance = new AppServerInstance(params)
        if (params.sshPassword) {
            oldServerInstance.sshPassword = appServerInstance.sshPassword
        }
        oldServerInstance.isTested = false
        oldServerInstance.name = appServerInstance.name
        oldServerInstance.sshHost = appServerInstance.sshHost
        oldServerInstance.sshPort = appServerInstance.sshPort
        oldServerInstance.sshUserName = appServerInstance.sshUserName
        oldServerInstance.osVendorId = appServerInstance.osVendorId
        oldServerInstance.updatedBy = user.id
        oldServerInstance.updatedOn = new Date()
        return oldServerInstance
    }

    /**
     * 1. check for AppServerInstance object existence
     * 2. check for AppServerInstance name
     *
     * @param params - a map from caller method
     * @param user - an object of AppUser
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params, AppUser user) {
        String errMsg
        //Check parameters
        if ((!params.id) || (!params.version) || (!params.name) || (!params.sshHost) ||
                (!params.sshPort) || (!params.sshUserName) || (!params.sshPassword) || (!params.osVendorId)) {
            return ERROR_FOR_INVALID_INPUT
        }
        long id = Long.parseLong(params.id.toString())
        AppServerInstance appServerInstance = appServerInstanceService.read(id)

        //check for AppServerInstance object existence
        errMsg = checkServerInstanceExistence(params, appServerInstance)
        if (errMsg != null) return errMsg

        //check for duplicate AppServerInstance name
        errMsg = checkServerInstanceCountByName(params, user, id)
        if (errMsg != null) return errMsg

        AppServerInstance server = getServerInstance(params, appUser, appServerInstance)
        params.put(SERVER_INSTANCE, server)
        return null
    }

    /**
     * check for AppServerInstance object existence
     *
     * @param params - a map from caller method
     * @param appServerInstance - an object of AppServerInstance
     * @return -  a string of error message or null
     */
    private String checkServerInstanceExistence(Map params, AppServerInstance appServerInstance) {
        long version = Long.parseLong(params.version.toString())
        if (!appServerInstance || appServerInstance.version != version) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

    /**
     * check for duplicate AppServerInstance name
     *
     * @param params - a map from caller method
     * @param user - an object of AppUser
     * @return - error message or null
     */
    private String checkServerInstanceCountByName(Map params, AppUser user, long id) {
        int count = appServerInstanceService.countByNameIlikeAndCompanyIdAndIdNotEqual(params.name, user.companyId, id)
        if (count > 0) {
            return SEVER_NAME_MUST_BE_UNIQUE
        }
        return null
    }
}
