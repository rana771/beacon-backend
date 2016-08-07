package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.UserSession
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.ApplicationSessionService
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

import javax.servlet.http.HttpSession

/**
 *  Show list of online users in grid
 *  For details go through Use-Case doc named 'ListOnlineUserActionService'
 */
class ListOnlineUserActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SESSION_USER_MAP = "sessionUserMap"
    private static final String DATE_FORMAT = "dd-MMM-yy [hh:mm:ss a]"
    private static final String AGO = ' ago'

    ApplicationSessionService applicationSessionService

    /**
     * No pre conditions required for searching project domains
     *
     * @param params - Request parameters
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * Get list of online user for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            initListing(result)
            Map sessionUserMap = list() // get list of online user
            result.put(SESSION_USER_MAP, sessionUserMap)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     *
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Wrap appUser list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Map buildSuccessResultForUI(Map result) {
        try {
            Map sessionUserMap = (Map) result.get(SESSION_USER_MAP)
            List lstSessionInfo = sessionUserMap.sessionInfo
            List listSessionUser = wrapSessionInfo(lstSessionInfo)   // wrap user list in grid entity
            Map successMap = new LinkedHashMap()
            successMap.put(COUNT, sessionUserMap.count)
            successMap.put(LIST, listSessionUser)
            return successMap
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Wrap list of appUser in grid entity
     * @param lstSessionInfo -list of online user
     * @param start -starting index of the page
     * @return -list of wrapped appUser
     */
    private List wrapSessionInfo(List<UserSession> lstSessionInfo) {
        List lstWrappedInfo = []
        for (int i = 0; i < lstSessionInfo.size(); i++) {
            UserSession userSession = lstSessionInfo[i]
            HttpSession currSession = userSession.session
            AppUser appUser = userSession.appUser // get appUser
            String lastActivity = DateUtility.getDifference(new Date(currSession.lastAccessedTime), new Date()) + AGO
            String loginTime = new Date(currSession.creationTime).format(DATE_FORMAT)
            lstWrappedInfo << [
                    id             : currSession.id,
                    userId         : appUser.id,
                    userName       : userSession.appUser.username,
                    loginId        : appUser.loginId,
                    loginTime      : loginTime,
                    ipAddress      : userSession.clientIP,
                    browser        : userSession.clientBrowser,
                    operatingSystem: userSession.clientOS,
                    lastActivity   : lastActivity
            ]
        }
        return lstWrappedInfo
    }

    /**
     * Get list and count of online user
     * @return -a map containing list and count of online user
     */
    private Map list() {
        List<UserSession> result = []
        List<UserSession> lstSessionInfo = applicationSessionService.list()
        int max = (start + resultPerPage) > lstSessionInfo.size() ? lstSessionInfo.size() : (start + resultPerPage)
        for (int i = start; i < max; i++) {
            result << lstSessionInfo[i]
        }
        return [sessionInfo: result, count: lstSessionInfo.size()]
    }
}
