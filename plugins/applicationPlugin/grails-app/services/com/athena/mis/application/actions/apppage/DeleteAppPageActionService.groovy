package com.athena.mis.application.actions.apppage

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppPage
import com.athena.mis.application.service.AppNoteService
import com.athena.mis.application.service.AppPageService
import com.athena.mis.integration.elearning.ELearningPluginConnector
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class DeleteAppPageActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_PAGE = "appPage"
    private static final String APP_PAGE_NOT_FOUND = "Page object not found"
    private static final String SUCCESS_MESSAGE = "Page has been deleted successfully"

    AppPageService appPageService
    AppNoteService appNoteService

    @Autowired(required = false)
    ELearningPluginConnector elearningImplService

    @Transactional(readOnly = true)
    Map executePreCondition(Map parameters) {
        try {
            String errMsg = checkValidation(parameters)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Transactional
    Map execute(Map result) {
        try {
            AppPage page = (AppPage) result.get(APP_PAGE)
            appPageService.delete(page)

            // delete all note by typeid
            if(elearningImplService){
                long entityId = Long.parseLong(result.entityId.toString())
                long entityTypeId = Long.parseLong(result.entityTypeId.toString())
                appNoteService.delete(entityId, entityTypeId)
            }
            return result
        }catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    Map buildSuccessResultForUI(Map executeResult) {
        return super.setSuccess(executeResult, SUCCESS_MESSAGE)
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    private String checkValidation(Map params) {
        String errMsg = checkRequiredParams(params)
        if (errMsg) return errMsg
        errMsg = checkPageExistence(params)
        if (errMsg) return errMsg
        return null
    }

    private String checkRequiredParams(Map params) {
        if (!params.id) {
            return ERROR_FOR_INVALID_INPUT
        }
        return null
    }

    private String checkPageExistence(Map params) {
        long id = Long.parseLong(params.id.toString())
        AppPage page = (AppPage) appPageService.read(id)
        if (!page) {
            return APP_PAGE_NOT_FOUND
        }
        params.put(APP_PAGE, page)
        return null
    }
}
