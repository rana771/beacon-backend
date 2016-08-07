package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppBank
import com.athena.mis.application.service.AppBankService
import org.apache.log4j.Logger

/**
 * render hidden input html of system bank id
 * for details go through use case named "GetSystemBankTagLibActionService"
 */
class GetSystemBankTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())
    private static final String NAME = "name"

    AppBankService appBankService

    /**
     * check if required attr exists
     * @param params - serialized parameter from UI
     * @return - a map containing necessary information to build html
     */
    public Map executePreCondition(Map params) {
        try {
            if (!params.name) {
                return setError(params, ERROR_FOR_INVALID_INPUT)
            }
            String strName = params.name
            params.put(NAME, strName)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(e)
        }
    }


    /**
     * get system bank and build html
     * @param result - a map returned by precondition method
     * @return - a map containing desired html
     */
    public Map execute(Map result) {
        try {
            String strName = (String) result.get(NAME)
            AppBank systemBank = appBankService.getSystemBank(super.companyId)
            String html = buildBankHtml(systemBank, strName)
            result.html = html
            return result
        } catch (Exception e) {
            log.error(e.message)
            throw new RuntimeException(e)
        }
    }

    /**
     * Do nothing in post condition
     * @param result - A map returned by execute method
     * @return - returned the received map
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * do nothing for build success operation
     * @param result - A map returned by post condition method.
     * @return - returned the same received map containing isError = false
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Do nothing here
     * @param result - map returned from previous any of method
     * @return - a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * build html
     */
    private String buildBankHtml(AppBank bank, String strName) {
        String html = """
        <input type="hidden" name="${strName}" id="${strName}" value="${bank ? bank.id : EMPTY_SPACE}"/>
        """
        return html
    }
}
