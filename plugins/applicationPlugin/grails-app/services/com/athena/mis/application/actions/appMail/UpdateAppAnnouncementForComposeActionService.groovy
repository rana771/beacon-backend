package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Role
import com.athena.mis.application.model.ListAppAnnouncementActionServiceModel
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.ListAppAnnouncementActionServiceModelService
import com.athena.mis.application.service.RoleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update mail object and show in grid
 *  For details go through Use-Case doc named 'UpdateAppAnnouncementForComposeActionService'
 */
class UpdateAppAnnouncementForComposeActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_MAIL = "appMail"
    private static final String SUCCESS_MESSAGE = "Announcement has been updated successfully."

    AppMailService appMailService
    RoleService roleService
    ListAppAnnouncementActionServiceModelService listAppAnnouncementActionServiceModelService

    /**
     * 1. check validation
     * 2. build mail object for update
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check validation
            String errMsg = checkValidation(params)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            // get role ids
            if (params.roleIds.equals(EMPTY_SPACE)) {
                List<Role> lstRole = roleService.listForDropDown()
                List<Long> lstRoleIds = lstRole*.id
                String roleIds = roleService.buildCommaSeparatedStringOfIds(lstRoleIds)
                params.roleIds = roleIds
            }
            // build mail object for update
            buildMailObject(params)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the mail object from map
     * 2. Update existing mail object in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppMail mail = (AppMail) result.get(APP_MAIL)
            appMailService.update(mail)
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
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    Map buildSuccessResultForUI(Map result) {
        try{
            AppMail mail = (AppMail) result.get(APP_MAIL)
            ListAppAnnouncementActionServiceModel savedMail = listAppAnnouncementActionServiceModelService.read(mail.id)
            result.put(APP_MAIL, savedMail)
            return super.setSuccess(result, SUCCESS_MESSAGE)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build mail object for update
     * @param params - serialize parameters from UI
     * @return - updated mail object
     */
    private AppMail buildMailObject(Map params) {
        AppMail oldMail = (AppMail) params.get(APP_MAIL)
        AppMail newMail = new AppMail(params)
        AppUser systemUser = super.getAppUser()
        oldMail.roleIds = newMail.roleIds
        oldMail.displayName = newMail.displayName
        oldMail.subject = newMail.subject
        oldMail.body = newMail.body
        oldMail.updatedOn = new Date()
        oldMail.updatedBy = systemUser.id
        return oldMail
    }

    /**
     * 1. check required parameters
     * 2. check mail object existence
     * @param params - serialized parameters from UI
     * @return - a string containing null value or error message depending on validation check
     */
    private String checkValidation(Map params) {
        String errMsg
        // check required parameters
        if (!params.id || !params.version || !params.displayName || !params.subject || !params.body) {
            return ERROR_FOR_INVALID_INPUT
        }
        long mailId = Long.parseLong(params.id.toString())
        AppMail mail = appMailService.read(mailId)
        // check mail object existence
        errMsg = checkMailObjectExistence(mail, params)
        if (errMsg) {
            return errMsg
        }
        params.put(APP_MAIL, mail)
        return null
    }

    /**
     * check mail object existence
     * @param mail - an object of AppMAil
     * @param params - serialized parameters from UI
     * @return - error message or null value
     */
    private String checkMailObjectExistence(AppMail mail, Map params) {
        long version = Long.parseLong(params.version.toString())
        if (!mail || mail.version != version) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }
}
