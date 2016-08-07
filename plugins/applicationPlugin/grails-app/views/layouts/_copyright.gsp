<%@ page import="com.athena.mis.PluginConnector" %>
<div class="row grad-v-white-gray" id="copyRightBottom" style="position: absolute;width: 100%; bottom: 0;">
    <div class="col-md-9"
         style="padding: 20px 0 0 23px; font: bold 12px Arial, Helvetica, sans-serif;color: #3b5998;">
        <app:themeContent id="copyrightDiv" url="${createLink(controller: 'appTheme', action: 'reloadTheme')}"
                          plugin_id="${PluginConnector.PLUGIN_ID}" name="app.leftMenu.companyCopyright">
        </app:themeContent>
    </div>

    <div class="col-md-3 no-padding-margin">
        <a title="<app:themeContent name="app.companyName" plugin_id="${PluginConnector.PLUGIN_ID}">
        </app:themeContent>"
           href="<app:themeContent name="app.companyWebsite" plugin_id="${PluginConnector.PLUGIN_ID}">
           </app:themeContent>"
           target="_blank">
            <img src="${grailsApplication.config.theme.application}<app:themeContent
                    name="app.leftMenu.CompanyLogo" plugin_id="${PluginConnector.PLUGIN_ID}">
            </app:themeContent>">
        </a>
    </div>
</div>