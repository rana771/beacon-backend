package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.Role
import com.athena.mis.application.model.ListAppAnnouncementActionServiceModel
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.ListAppAnnouncementActionServiceModelService
import com.athena.mis.application.service.RoleService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new mail object and show in grid
 *  For details go through Use-Case doc named 'CreateAppAnnouncementActionService'
 */
class CreateAppAnnouncementActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_MAIL = "appMail"
    private static final String SUCCESS_MESSAGE = "Announcement has been saved successfully"
    private static final String TRANSACTION_CODE = "CreateAppAnnouncementActionService"
    private static final String MIME_TYPE = "html"

    RoleService roleService
    AppMailService appMailService
    ListAppAnnouncementActionServiceModelService listAppAnnouncementActionServiceModelService

    /**
     * 1. check input validation
     * 2. build mail object
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check input validation
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
            // build mail object
            AppMail mail = getAppMail(params)
            params.put(APP_MAIL, mail)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive mail object from executePreCondition method
     * 2. create new mail object
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppMail mail = (AppMail) result.get(APP_MAIL)
            // save new mail object in DB
            appMailService.create(mail)
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
     * Build mail object
     * @param params - serialized parameters from UI
     * @return - mail object
     */
    private AppMail getAppMail(Map params) {
        AppMail mail = new AppMail(params)
        mail.transactionCode
        mail.companyId = super.companyId
        mail.isActive = true
        mail.mimeType = MIME_TYPE
        mail.transactionCode = TRANSACTION_CODE
        mail.isRequiredRoleIds = true
        mail.isManualSend = true
        mail.isAnnouncement = true
        mail.createdBy = super.appUser.id
        mail.createdOn = new Date()
        return mail
    }

    /**
     * 1. check required parameters
     * @param params - serialized parameters from UI
     * @return - a string containing null value or error message depending on validation check
     */
    private String checkValidation(Map params) {
        // check required parameters
        if (!params.displayName || !params.subject || !params.body) {
            return ERROR_FOR_INVALID_INPUT
        }
        return null
    }
}
