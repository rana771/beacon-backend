package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppUserService
import grails.transaction.Transactional
import org.apache.log4j.Logger

class GetSwitchUserTagLibActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String HTML = "html"

    AppUserService appUserService

    Map executePreCondition(Map parameters) {
        return parameters
    }

    @Transactional(readOnly = true)
    Map execute(Map previousResult) {
        try {
            AppUser appUser = super.appUser
            previousResult.put(HTML, EMPTY_SPACE)
            if (!checkSwitchable(appUser)) {
                return previousResult
            }
            List<AppUser> userList = appUserService.findAllByIsSwitchableAndEnabledAndCompanyIdAndIdNotEqual(true, true, super.companyId, appUser.id)
            String html = buildHtml(userList)
            previousResult.put(HTML, html)
            return previousResult
        } catch (Exception ex) {
            log.error(e.getMessage())
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

    private String buildHtml(List<AppUser> appUserList) {
        if (appUserList.size() == 0) {
            return EMPTY_SPACE
        }
        String html = "<li class='divider'></li><li class='dropdown-header'>Switch User As<li>"

        for (int i = 0; i < appUserList.size(); i++) {
            html += """<li id='switchUser'><a>${appUserList[i].username}<form action='/j_spring_security_switch_user' method='POST'>
                            <input type='hidden' name='j_username' value='${appUserList[i].loginId}'/>
                    </form></a></li>"""
        }

        String script = """
            <script type="text/javascript">
                \$(document).ready(function () {
                    \$('#switchUser > a').click(function () {
                        \$(this).children().submit();
                    });
                });
            </script>
        """
        return html + script
    }

    private boolean checkSwitchable(AppUser appUser) {
        if (appUser.isSwitchable) return true
        return false
    }
}
