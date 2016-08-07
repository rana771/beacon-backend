package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppAttachment
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.AppSystemEntityCacheService
import grails.transaction.Transactional
import org.apache.log4j.Logger

/**
 * delete mail
 * for details please go through use case named 'DeleteMailForAppMailActionService'
 */
class DeleteAppMailForMailActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String NOT_FOUND = "Mail object not found"
    private static final String APP_MAIL = "appMail"
    private static final String SUCCESS_MSG = "Mail has been deleted successfully"

    AppMailService appMailService
    AppAttachmentService appAttachmentService
    AppSystemEntityCacheService appSystemEntityCacheService

    /**
     * check necessary parameters
     * read mail by id
     * check existence
     * @param parameters - serialized parameters from UI
     * @return - same map as received
     */
    Map executePreCondition(Map parameters) {
        try {
            // check necessary parameters
            if (!parameters.id) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(parameters.id.toString())
            // read mail object by id
            AppMail mail = (AppMail) appMailService.read(id)
            // check existence
            String errMsg = checkExistence(mail)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            parameters.put(APP_MAIL, mail)
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * delete mail object from DB
     * @param previousResult - resulting map from pre condition
     * @return - same map as received
     */
    @Transactional
    Map execute(Map previousResult) {
        try {
            AppMail mail = (AppMail) previousResult.get(APP_MAIL)
            SystemEntity typeMail = (SystemEntity) appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_APP_MAIL, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, super.companyId)
            List<AppAttachment> appAttachment = appAttachmentService.findAllByEntityTypeIdAndEntityId(typeMail.id, mail.id)
            // delete mail from DB
            appMailService.delete(mail)
            appAttachment.each {
                appAttachmentService.delete(it)
            }
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no post condition
     * @param previousResult - resulting map from execute
     * @return - same map as received
     */
    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    /**
     * put success message into map
     * @param executeResult - resulting map from post condition
     * @return - same map as received
     */
    Map buildSuccessResultForUI(Map executeResult) {
        return super.setSuccess(executeResult, SUCCESS_MSG)
    }

    /**
     * @param executeResult resulting map from post condition
     * @return - same map as received
     */
    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * check whether mail exists or not
     * @param mail - AppMail object
     * @return - error message or null depending on validation
     */
    private String checkExistence(AppMail mail) {
        if (!mail) {
            return NOT_FOUND
        }
        return null
    }
}
