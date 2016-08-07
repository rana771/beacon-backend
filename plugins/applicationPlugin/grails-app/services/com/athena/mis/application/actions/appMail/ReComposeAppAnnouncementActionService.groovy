package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.service.AppMailService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new mail object and show in grid
 *  For details go through Use-Case doc named 'ReComposeAppAnnouncementActionService'
 */
class ReComposeAppAnnouncementActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_MAIL = "appMail"
    private static final String SUCCESS_MESSAGE = "Announcement has been re-composed successfully"

    AppMailService appMailService

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
            // build mail object for create
            AppMail appMail = buildMailObject(params)
            params.put(APP_MAIL, appMail)
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
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SUCCESS_MESSAGE)
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
     * 1. check required parameters
     * 2. check mail object existence
     * @param params - serialized parameters from UI
     * @return - a string containing null value or error message depending on validation check
     */
    private String checkValidation(Map params) {
        String errMsg
        // check required parameters
        if (!params.id) {
            return ERROR_FOR_INVALID_INPUT
        }
        long mailId = Long.parseLong(params.id.toString())
        AppMail mail = appMailService.read(mailId)
        // check mail object existence
        errMsg = checkMailObjectExistence(mail)
        if (errMsg) {
            return errMsg
        }
        params.put(APP_MAIL, mail)
        return null
    }

    /**
     * check mail object existence
     * @param mail - an object of AppMAil
     * @return - error message or null value
     */
    private String checkMailObjectExistence(AppMail mail) {
        if (!mail) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

    /**
     * Build mail object for create
     * @param params - serialize parameters from UI
     * @return - new mail object
     */
    private AppMail buildMailObject(Map params) {
        AppMail oldMail = (AppMail) params.get(APP_MAIL)
        AppMail newMail = new AppMail(params)
        newMail.roleIds = oldMail.roleIds
        newMail.subject = oldMail.subject
        newMail.body = oldMail.body
        newMail.mimeType = oldMail.mimeType
        newMail.transactionCode = oldMail.transactionCode
        newMail.companyId = oldMail.companyId
        newMail.isActive = true
        newMail.isRequiredRoleIds = oldMail.isRequiredRoleIds
        newMail.displayName = oldMail.displayName
        newMail.isManualSend = true
        newMail.createdOn = oldMail.createdOn
        newMail.isAnnouncement = true
        newMail.createdBy = oldMail.createdBy
        return newMail
    }
}
