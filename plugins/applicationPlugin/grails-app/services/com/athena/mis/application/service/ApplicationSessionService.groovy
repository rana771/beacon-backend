package com.athena.mis.application.service

import com.athena.mis.UserSession
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.session.AppSessionService
import net.sf.uadetector.ReadableUserAgent
import net.sf.uadetector.UserAgentStringParser
import net.sf.uadetector.service.UADetectorServiceFactory
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import javax.servlet.http.HttpSessionEvent
import javax.servlet.http.HttpSessionListener

class ApplicationSessionService implements HttpSessionListener {

    @Autowired
    AppSessionService appSessionService

    static transactional = false
    private Logger log = Logger.getLogger(getClass())

    private static final String USER_AGENT = "user-agent"
    private static final String PARENTHESIS_START = " ( "
    private static final String PARENTHESIS_END = " ) "
    public static final String SINGLE_DOT = "."

    @Override
    void sessionCreated(HttpSessionEvent httpSessionEvent) {
        // do nothing
    }

    @Override
    void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        sessionMap.remove(httpSessionEvent.session.id);
    }

    public boolean logOut() {
        if (appSessionService.getSession()) {
            appSessionService.invalidateSession()
        } else {
            log.error("Session not found")
        }
        return true
    }

    public boolean forceLogOut(List lstSessionIds) {
        for (int i = 0; i < lstSessionIds.size(); i++) {
            String sessionId = lstSessionIds[i]
            HttpSession httpSession = (HttpSession) sessionMap.remove(sessionId).session;
            httpSession.invalidate()
        }
        return true
    }

    private Map<String, UserSession> sessionMap = new HashMap<String, UserSession>()

    // build custom session object and put in session map
    public void init(AppUser user, HttpServletRequest request, HttpSession session) {
        // get client IP, OS etc.
        String strUserAgent = request.getHeader(USER_AGENT)
        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser()
        ReadableUserAgent agent = parser.parse(strUserAgent)
        String clientBrowser = agent.name + PARENTHESIS_START + agent.versionNumber.major + SINGLE_DOT + agent.versionNumber.minor + PARENTHESIS_END
        String clientIP = request.getRemoteAddr()
        String clientOS = agent.operatingSystem.name

        // build sessionUser with browser, IP, OS etc.
        UserSession userSession = new UserSession()
        userSession.clientBrowser = clientBrowser
        userSession.clientIP = clientIP
        userSession.clientOS = clientOS
        userSession.session = session
        userSession.appUser = user

        sessionMap.put(session.id, userSession)
    }

    public List<UserSession> list() {
        long companyId = appSessionService.getCompanyId()
        List<UserSession> lstSessionUser = []
        for (Map.Entry<String, UserSession> entry : sessionMap.entrySet()) {
            UserSession userSession = entry.getValue()
            AppUser user = userSession.appUser
            if (user.companyId == companyId) {
                lstSessionUser.add(userSession)
            }
        }
        return lstSessionUser
    }
}
