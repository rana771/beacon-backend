package com.athena.mis.application.actions.apppage

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppPage
import com.athena.mis.application.service.AppPageService
import grails.transaction.Transactional
import org.apache.log4j.Logger

class CreateAppPageActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_PAGE = "appPage"
    private static final String SUCCESS_MESSAGE = "Page has been created successfully"
    private static final String BODY_MAX_LENGTH_MSG = "Body length should be maximum 2040 character."
    private static final String TITLE_UNIQUE_MSG = "Title should be unique."

    AppPageService appPageService

    Map executePreCondition(Map parameters) {
        try {
            String errMsg = checkValidation(parameters)
            if (errMsg) {
                return super.setError(parameters, errMsg)
            }
            AppPage page = buildObj(parameters)
            parameters.put(APP_PAGE, page)
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Transactional
    Map execute(Map previousResult) {
        try {
            AppPage page = (AppPage) previousResult.get(APP_PAGE)
            appPageService.create(page)
            previousResult.put(APP_PAGE, page)
            return previousResult
        } catch (Exception ex) {
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
        return null
    }

    private String checkRequiredParams(Map params) {
        if (!params.title || !params.body) {
            return ERROR_FOR_INVALID_INPUT
        }
        long entityTypeId = Long.parseLong(params.entityTypeId.toString())
        int duplicateCount = appPageService.countByTitleIlikeAndEntityTypeIdAndCompanyId(params.title, entityTypeId, super.getCompanyId())
        // check duplicate category name
        if (duplicateCount > 0) {
            return TITLE_UNIQUE_MSG
        }

        String pageBody = params.body.toString()
        if (pageBody.trim().length() > AppPage.BODY_MAX_LENGTH) {
            return BODY_MAX_LENGTH_MSG
        }
        return null
    }

    private AppPage buildObj(Map params){
        AppPage page = new AppPage(params)
        page.companyId = super.companyId
        page.createdBy = super.appUser.id
        page.createdOn = new Date()
        page.updatedBy = 0L
        return page
    }
}
