package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.CompanyService
import org.apache.log4j.Logger

/**
 * Determine if New User Registration link should visible
 **/
class GetSysConfigUserRegistrationActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    CompanyService companyService
    AppConfigurationService appConfigurationService

    /**
     * Do nothing in precondition method
     * @param params - parameter from UI
     * @return - return the same map
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * Returns value if system configuration exists
     * 1. check if exh module installed
     * 2. check through interface, if New User Registration is enabled in Exh plugin
     * @param result - A map returned from precondition method
     * @return - A map consisting of desired html
     */
    public Map execute(Map result) {
        try {
            Boolean isRegistrationEnabled = Boolean.FALSE
            long companyId = Long.parseLong(result.companyId.toString())
            SysConfiguration config = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.ENABLE_NEW_USER_REGISTRATION, companyId)
            if (config && config.value.equalsIgnoreCase(TRUE)) {
                isRegistrationEnabled = Boolean.TRUE
            }
            String html = buildNewUserRegistrationLink(isRegistrationEnabled, config)
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
     * build link for new user registration
     * @param isRegistrationEnabled
     * @param config
     * @return
     */
    private String buildNewUserRegistrationLink(boolean isRegistrationEnabled, SysConfiguration config) {
        if (!isRegistrationEnabled) return EMPTY_SPACE
        String html = """
            <a href="/appUser/showRegistration" style="font-size: 12px" tabindex="9">${
            config.message ? config.message : 'New User? Click Here'
        }</a>
        """
        return html
    }
}
