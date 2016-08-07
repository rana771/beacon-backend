<%@ page import="com.athena.mis.PluginConnector; com.athena.mis.application.service.AppThemeService" %>
<script type="text/javascript"
        src="${grailsApplication.config.theme.application}/js/jquery/jquery-2.1.4.min.js"></script>
<script type="text/javascript"
        src="${grailsApplication.config.theme.application}/js/jquery/jquery.simple.timer.js"></script>
<script type="text/javascript" src="${grailsApplication.config.theme.application}/js/dateutil.js"></script>
<script type="text/javascript"
        src="${grailsApplication.config.theme.application}/js/jquery/iframe-post-form.js"></script>
<script type="text/javascript"
        src="${grailsApplication.config.theme.application}/js/jquery/featureList-1.0.0.js"></script>
<script type="text/javascript" src="${grailsApplication.config.theme.application}/js/kendo/kendo.all.min.js"></script>
<script type="text/javascript"
        src="${grailsApplication.config.theme.application}/js/bootstrap/js/bootstrap.min.js"></script>
<script type="text/javascript"
        src="${grailsApplication.config.theme.application}/js/bootstrap/js/bootstrap-slider.js"></script>
<script type="text/javascript" src="${grailsApplication.config.theme.application}/js/application.js"></script>
<script type="text/javascript" src="https://maps.googleapis.com/maps/api/js?sensor=false"></script>

<link rel="stylesheet" href="${grailsApplication.config.theme.application}/css/jquery/featurelist.css"/>

<app:themeContent
        name="${AppThemeService.KEY_KENDO_THEME}" plugin_id="${PluginConnector.PLUGIN_ID}">
</app:themeContent>
<link rel="stylesheet" href="${grailsApplication.config.theme.application}/css/kendo/kendo.common.min.css"/>
<link rel="stylesheet" href="${grailsApplication.config.theme.application}/css/kendo/kendo.dataviz.min.css"/>
<link rel="stylesheet" href="${grailsApplication.config.theme.application}/css/fontawesome/css/font-awesome.min.css"/>
<link rel="stylesheet" href="${grailsApplication.config.theme.application}/css/bootstrap/css/bootstrap.min.css"/>
<link rel="stylesheet" href="${grailsApplication.config.theme.application}/css/bootstrap/css/bootstrap-slider.css"/>
<link rel="stylesheet" href="${grailsApplication.config.theme.application}/css/main-image.css"/>
<g:render template='/layouts/commonTemplates'/>
<app:themeContent
        name="${AppThemeService.KEY_CSS_MAIN_COMPONENTS}" css="true" plugin_id="${PluginConnector.PLUGIN_ID}">
</app:themeContent>
<app:themeContent
        name="${AppThemeService.KEY_CSS_KENDO_CUSTOM}" css="true" plugin_id="${PluginConnector.PLUGIN_ID}">
</app:themeContent>
<app:themeContent
        name="${AppThemeService.KEY_CSS_BOOTSTRAP_CUSTOM}" css="true" plugin_id="${PluginConnector.PLUGIN_ID}">
</app:themeContent>
