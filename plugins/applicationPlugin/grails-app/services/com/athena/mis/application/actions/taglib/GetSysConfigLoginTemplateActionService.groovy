package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.service.CompanyService
import org.apache.log4j.Logger

class GetSysConfigLoginTemplateActionService extends BaseService implements ActionServiceIntf {

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
     *
     * @param result - A map returned from precondition method
     * @return - A map consisting of desired html
     */
    public Map execute(Map result) {
        try {
            long companyId = Long.parseLong(result.companyId.toString())
            long deploymentMode = 1L
            SysConfiguration config = (SysConfiguration) appConfigurationService.appSysConfigCacheService.readByKeyAndCompanyId(appConfigurationService.appSysConfigCacheService.APPLICATION_DEPLOYMENT_MODE, companyId)
            if (config) {
                deploymentMode = Long.parseLong(config.value)
            }
            String html = buildHtml(deploymentMode)
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
     * @param deploymentMode
     * @return
     */
    private String buildHtml(long deploymentMode) {
        String html
        switch (deploymentMode) {
            case 1:
                html = buildHtmlForProductionMode()
                break
            case 2:
                html = buildHtmlForDevelopmentMode()
                break
            default:
                html = buildHtmlForProductionMode()
                break
        }
        return html
    }

    private String buildHtmlForProductionMode() {
        String html = """
            <div class="form-group">
                <label class="col-md-3 control-label" for="username">Login ID:</label>

                <div class="col-md-9">
                    <input type="text" class="form-control" id="username" name="j_username" tabindex="1"
                        autofocus placeholder="Login Id" value=""/>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label" for="password">Password:</label>

                <div class="col-md-9">
                    <input type="password" class="form-control" id="password" name="j_password"
                        tabindex="2" placeholder="Password" value=""/>
                </div>
            </div>

            <div class="form-group">
                <div class="col-md-3">&nbsp;</div>

                <div class="col-md-5">
                    <img src="" id="image" name="image"/>
                </div>

                <div class="col-md-4">
                    <a href="#" id="refreshCaptcha" title="Refresh security ID"
                        class="btn btn-default pull-right" tabindex="3">
                        <span class="glyphicon glyphicon-refresh"></span>
                    </a>
                </div>
            </div>

            <div class="form-group">
                <label class="control-label col-md-3" for="captcha">Security:</label>

                <div class="col-md-9">
                    <input type="text" class="form-control" id="captcha" name="captcha" tabindex="4"
                        value="" placeHolder="Type security ID here"/>
                </div>
            </div>

            <script type="text/javascript">
                \$(document).ready(function () {
                    var imgId = Math.floor(Math.random() * 9999999999);
                    \$("#image").attr("src", "/jcaptcha/jpeg/image?id=" + imgId);
                });
                \$("#refreshCaptcha").click(function () {
                    \$("#image").fadeOut(500, function () {
                        var captchaURL = \$("#image").attr("src");
                        captchaURL = captchaURL.replace(captchaURL.substring(captchaURL.indexOf("=") + 1, captchaURL.length), Math.floor(Math.random() * 9999999999));
                        \$("#image").attr("src", captchaURL);
                    });
                    \$("#image").fadeIn(300);
                });
            </script>
        """
        return html
    }

    private String buildHtmlForDevelopmentMode() {
        String html = """
            <div class="form-group">
                <label class="col-md-3 control-label" for="username">Login ID:</label>

                <div class="col-md-9">
                    <input type="text" class="form-control" id="username" name="j_username" tabindex="1"
                        autofocus placeholder="Login Id" value="appadmin@athena.com"/>
                </div>
            </div>

            <div class="form-group">
                <label class="col-md-3 control-label" for="password">Password:</label>

                <div class="col-md-9">
                    <input type="password" class="form-control" id="password" name="j_password"
                        tabindex="2" placeholder="Password" value="athena@123"/>
                </div>
            </div>
        """
        return html
    }
}
