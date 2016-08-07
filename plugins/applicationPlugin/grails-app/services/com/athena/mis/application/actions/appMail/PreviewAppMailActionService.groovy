package com.athena.mis.application.actions.appMail

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.AppUserService
import com.athena.mis.utility.DateUtility
import grails.transaction.Transactional
import org.apache.log4j.Logger

/**
 * Preview mail
 * for details please go through use case named 'PreviewAppMailActionService'
 */
class PreviewAppMailActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String HTML_MAIL = "html"

    AppMailService appMailService
    AppUserService appUserService

    /**
     * 1. check necessary parameters
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    Map executePreCondition(Map parameters) {
        try {
            // check necessary parameters
            if (!parameters.mailId) {
                return super.setError(parameters, ERROR_FOR_INVALID_INPUT)
            }
            return parameters
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. read mail by id
     * 2. build html template to view
     * This method is in transactional block and will roll back in case of any exception
     * @param previousResult - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    Map execute(Map previousResult) {
        try {
            long id = Long.parseLong(previousResult.mailId.toString())
            // read mail by id
            AppMail mail = (AppMail) appMailService.read(id)
            // build html template to view
            String html = buildHtml(mail)
            previousResult.put(HTML_MAIL, html)
            return previousResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param previousResult - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    Map executePostCondition(Map previousResult) {
        return previousResult
    }

    /**
     * @param executeResult - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    Map buildSuccessResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param executeResult - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    Map buildFailureResultForUI(Map executeResult) {
        return executeResult
    }

    /**
     * build html for preview
     * @param mail - AppMail onject
     * @return - String of html
     */
    private String buildHtml(AppMail mail) {
        String html = """
            <table class="table table-bordered">
                <tbody>
                    <tr>
                        <td class="active">To</td>
                        <td>${mail.recipients}</td>
                    </tr>
                    <tr>
                        <td class="active">Cc</td>
                        <td>${mail.recipientsCc ? mail.recipientsCc : ''}</td>
                    </tr>
                    <tr>
                        <td class="active">On</td>
                        <td>${DateUtility.getShortDateTimeFormatAsString(mail.updatedOn)}</td>
                    </tr>
                    <tr>
                        <td class="active">Subject</td>
                        <td>${mail.subject}</td>
                    </tr>
                    <tr>
                        <td class="active">Body</td>
                        <td>${mail.body}</td>
                    </tr>
                </tbody>
            </table>
        """
        return html
    }
}
