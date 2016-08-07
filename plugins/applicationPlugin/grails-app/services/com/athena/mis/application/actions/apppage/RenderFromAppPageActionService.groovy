package com.athena.mis.application.actions.apppage

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppPage
import com.athena.mis.application.service.AppPageService
import grails.transaction.Transactional
import org.apache.commons.lang.StringEscapeUtils
import org.apache.log4j.Logger

class RenderFromAppPageActionService extends BaseService implements ActionServiceIntf {

    private final Logger log = Logger.getLogger(getClass())

    AppPageService appPageService

    Map executePreCondition(Map parameters) {
        return parameters
    }

    @Transactional(readOnly = true)
    Map execute(Map previousResult) {
        try {
            if (!previousResult.id) {
                return super.setError(previousResult, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(previousResult.id.toString())
            AppPage page = (AppPage) appPageService.read(id)
            if(!page) {
                return super.setError(previousResult, "Page not found")
            }

            page.body = StringEscapeUtils.unescapeHtml(page.body)
            previousResult.put("page", page);
            return previousResult
        }catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    Map buildSuccessResultForUI(Map executeResult) {
        return executeResult
    }

    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }
}
