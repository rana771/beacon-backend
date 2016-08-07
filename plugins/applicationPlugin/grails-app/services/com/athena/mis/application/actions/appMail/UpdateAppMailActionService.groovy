package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.Role
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.RoleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

class UpdateAppMailActionService extends BaseService implements ActionServiceIntf {

    AppMailService appMailService
    RoleService roleService

    private static final String APP_MAIL_UPDATE_SUCCESS_MESSAGE = "Mail has been updated successfully"
    private static final String OBJ_NOT_FOUND = "Selected mail not found"
    private static final String APP_MAIL_OBJ = "appMail"
    private static final String ROLE_NOT_FOUND = " Role not found"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check input validation
     * 2. check role ids
     * @param parameters -serialized parameters from UI
     * @return -a map containing necessary objects for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check input validation
            LinkedHashMap checkValidation = checkInputValidation(params)
            Boolean isError = checkValidation.get(IS_ERROR)
            if (isError.booleanValue()) {
                String message = checkValidation.get(MESSAGE)
                return super.setError(params, message)
            }
            AppMail appMail = (AppMail) checkValidation.get(APP_MAIL_OBJ)
            if (appMail.isRequiredRoleIds) {
                List<String> roleIds = appMail.roleIds.split(COMA)
                String msgReturn = checkRoleIds(roleIds)
                if (msgReturn) {
                    return super.setError(params, msgReturn)
                }
            }
            params.put(APP_MAIL_OBJ, appMail)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Update AppMail object in DB
     * @param result -AppMail object send from executePreCondition method
     * @return -updated AppMail object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppMail appMail = (AppMail) result.get(APP_MAIL_OBJ)
            appMailService.update(appMail)
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
     * set success message
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, APP_MAIL_UPDATE_SUCCESS_MESSAGE)
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. check required parameters
     * 2. get old AppMail object by id
     * 3. build new AppMail object for update
     * @param params -serialized parameters from UI
     * @return -a map containing AppMAil object, isError (true/false) and relevant error message
     */
    private LinkedHashMap checkInputValidation(Map params) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(IS_ERROR, Boolean.TRUE)
        if (!params.id || !params.version) {
            result.put(MESSAGE, ERROR_FOR_INVALID_INPUT)
            return result
        }
        long appMailId = Long.parseLong(params.id.toString())
        long version = Long.parseLong(params.version.toString())
        AppMail oldAppMail = appMailService.read(appMailId)
        // check whether selected  object exists or not
        if (!oldAppMail || oldAppMail.version != version) {
            result.put(MESSAGE, OBJ_NOT_FOUND)
            return result
        }
        AppMail appMail = buildAppMailObject(params, oldAppMail)
        result.put(APP_MAIL_OBJ, appMail)
        result.put(IS_ERROR, Boolean.FALSE)
        return result
    }

    /**
     * Build AppMail object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldAppMail -old appMail object
     * @return -updated AppMail object
     */
    private AppMail buildAppMailObject(Map parameterMap, AppMail oldAppMail) {
        AppMail appMail = new AppMail(parameterMap)
        oldAppMail.subject = appMail.subject
        oldAppMail.body = appMail.body
        if (oldAppMail.isRequiredRoleIds) {
            oldAppMail.roleIds = appMail.roleIds
        }
        if (oldAppMail.isRequiredRecipients) {
            oldAppMail.recipients = appMail.recipients
        }
        oldAppMail.recipientsCc = appMail.recipientsCc
        oldAppMail.displayName = appMail.displayName
        oldAppMail.isActive = appMail.isActive
        oldAppMail.updatedOn = new Date()
        oldAppMail.updatedBy = super.appUser.id
        return oldAppMail
    }

    /**
     * Check the existence of role ids from role type CacheUtility
     * @param roleIds - list of role ids
     * @return - a string of message
     */
    private String checkRoleIds(List<String> roleIds) {
        for (int i = 0; i < roleIds.size(); i++) {
            long roleId = 0L
            try {
                roleId = Long.parseLong(roleIds[i].trim())
            } catch (Exception e) {
                return roleIds[i] + ROLE_NOT_FOUND
            }
            Role role = (Role) roleService.read(roleId)
            if (!role) {
                return roleIds[i] + ROLE_NOT_FOUND
            }
        }
        return null
    }
}
