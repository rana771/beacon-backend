<%@ page import="com.athena.mis.PluginConnector" %>
<app:themeContent name="app.cssMainComponents" css="true" plugin_id="${PluginConnector.PLUGIN_ID}">
</app:themeContent>
<link rel="stylesheet" href="${grailsApplication.config.theme.application}/css//bootstrap/css/bootstrap.min.css"/>
<link rel="stylesheet" href="${grailsApplication.config.theme.application}/css/kendo/kendo.common.min.css"/>
<link rel="stylesheet" href="${grailsApplication.config.theme.application}/css/kendo/kendo.silver.min.css"/>
<link rel="shortcut icon" href="${resource(dir: 'images', file: 'favicon.ico')}" type="image/x-icon"/>

<div class="container-fluid">
    <div class="row">
        <div class="col-md-3">&nbsp;</div>

        <div class="col-md-6">
            <div class="panel panel-danger">
                <div class="panel-heading">
                    <div class="panel-title">
                        Unauthorized Access
                    </div>
                </div>

                <div class="panel-body">
                    <div>You are not allowed to access the required page or perform the action.</div>

                    <div>Please contact with administrator if you think you should be allowed to access it.</div>

                    <div>Click <a href="${createLink(uri: '/')}">here</a>  to go directly to the home page.</div>
                </div>
            </div>
        </div>

        <div class="col-md-3">&nbsp;</div>
    </div>

    <div class="row">
        <div class="panel panel-default">
            <div class="panel-body">
                <app:themeContent name="app.login.advertisingPhrase" plugin_id="${PluginConnector.PLUGIN_ID}">
                </app:themeContent>
            </div>
        </div>
    </div>

    <div class="row">
        <app:themeContent name="app.login.productCopyright" plugin_id="${PluginConnector.PLUGIN_ID}">
        </app:themeContent>
    </div>
</div>

