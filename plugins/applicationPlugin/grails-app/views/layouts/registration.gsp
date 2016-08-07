<%@ page import="com.athena.mis.PluginConnector; com.athena.mis.application.service.AppThemeService;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="cache-control" content="public"/>
    <link rel="shortcut icon" href="${grailsApplication.config.theme.application}/images/favicon.ico"
          type="image/x-icon"/>

    <g:render template='/layouts/commonInclude'/>
    <app:themeContent name="${AppThemeService.KEY_CSS_MAIN_COMPONENTS}" css="true"
                      plugin_id="${PluginConnector.PLUGIN_ID}">
    </app:themeContent>
    <app:themeContent name="${AppThemeService.KEY_CSS_KENDO_CUSTOM}" css="true"
                      plugin_id="${PluginConnector.PLUGIN_ID}">
    </app:themeContent>
    <app:themeContent name="${AppThemeService.KEY_CSS_BOOTSTRAP_CUSTOM}" css="true"
                      plugin_id="${PluginConnector.PLUGIN_ID}">
    </app:themeContent>
    <g:layoutHead/>
</head>

<body>
<div class="row" style="margin: 0 5px; padding-top: 5px">
    <div class="col-md-12">
        <g:layoutBody/>
    </div>
</div>
</body>
</html>