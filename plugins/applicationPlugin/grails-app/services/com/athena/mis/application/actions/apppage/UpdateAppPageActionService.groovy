package com.athena.mis.application.actions.apppage

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppPage
import com.athena.mis.application.service.AppPageService
import grails.transaction.Transactional
import org.apache.log4j.Logger

class UpdateAppPageActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String APP_PAGE = "appPage"
    private static final String APP_PAGE_NOT_FOUND = "Page object not found"
    private static final String VERSION_MISMATCHED = "Version mismatched. Please refresh and try again"
    private static final String SUCCESS_MESSAGE = "Page has been updated successfully"
    private static final String BODY_MAX_LENGTH_MSG = "Body length should be maximum 2040 character."
    private static final String TITLE_UNIQUE_MSG = "Title should be unique."

    AppPageService appPageService

    @Transactional(readOnly = true)
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
            appPageService.update(page)
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
        errMsg = checkPageExistence(params)
        if (errMsg) return errMsg
        errMsg = checkVersion(params)
        if (errMsg) return errMsg

        return null
    }

    private String checkRequiredParams(Map params) {
        if (!params.id || !params.version) {
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
        long entityTypeId = Long.parseLong(params.entityTypeId.toString())

        int duplicateCount = appPageService.countByTitleIlikeAndEntityTypeIdAndCompanyIdAndIdNotEqual(params.title, entityTypeId, page.companyId, page.id)
        // check duplicate category name
        if (duplicateCount > 0) {
            return TITLE_UNIQUE_MSG
        }

        String aboutTheCourse = params.body.toString()
        if (aboutTheCourse.trim().length() > AppPage.BODY_MAX_LENGTH) {
            return BODY_MAX_LENGTH_MSG
        }
        params.put(APP_PAGE, page)
        return null
    }

    private String checkVersion(Map params) {
        AppPage page = (AppPage) params.get(APP_PAGE)
        long version = Long.parseLong(params.version.toString())
        if (page.version != version) {
            return VERSION_MISMATCHED
        }
        return null
    }

    private AppPage buildObj(Map params) {
        AppPage oldPage = (AppPage) params.get(APP_PAGE)
        AppPage newPage = new AppPage(params)
        oldPage.title = newPage.title
        oldPage.body = newPage.body
        oldPage.isCommentable = newPage.isCommentable
        oldPage.entityId = newPage.entityId
        oldPage.updatedBy = super.appUser.id
        oldPage.updatedOn = new Date()
        return oldPage
    }
}
