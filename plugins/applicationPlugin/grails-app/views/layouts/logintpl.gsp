<%@ page import="com.athena.mis.application.service.AppThemeService; com.athena.mis.PluginConnector; grails.util.Environment;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns="http://www.w3.org/1999/html">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="cache-control" content="public"/>
    <link rel="shortcut icon" href="${grailsApplication.config.theme.application}/images/favicon.ico"
          type="image/x-icon"/>
    <title>Application Login</title>
    <script type="text/javascript"
            src="${grailsApplication.config.theme.application}/js/jquery/jquery-2.1.4.min.js"></script>
    <script type="text/javascript"
            src="${grailsApplication.config.theme.application}/js/bootstrap/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="${grailsApplication.config.theme.application}/css//bootstrap/css/bootstrap.min.css"/>
    <app:themeContent
            name="${AppThemeService.KEY_CSS_BOOTSTRAP_CUSTOM}" css="true"
            plugin_id="${PluginConnector.PLUGIN_ID}">
    </app:themeContent>
</head>

<body class="container-fluid">
<div class="row">
    <div class="col-md-4">
        <div class="panel panel-default">
            <div class="panel-heading">
                <span class="pull-left"><i class="panel-icon fa fa-sign-in"></i></span>

                <div class="panel-title">
                    <div class="row">
                        <div class="col-md-7" style="font-weight: bold">Sign In</div>

                        <div class="col-md-5" style="padding-right: 1px">
                            <app:isUserRegistrationEnabled name="ExchangeHouse" companyId="${companyId}">
                            </app:isUserRegistrationEnabled>
                        </div>
                    </div>
                </div>
            </div>

            <div class="panel-body">
                <form action='${createLink(controller: 'login', action: 'checklogin')}?companyId=${companyId}'
                      autocomplete='on'
                      method="post" class="form-horizontal form-widgets" role="form" id='loginForm'>

                    <app:checkDeploymentModeForLoginForm
                            companyId="${companyId}">
                    </app:checkDeploymentModeForLoginForm>

                    <div class="form-group">
                        <div class="col-md-3">&nbsp;</div>

                        <div class="col-md-5">
                            <button id="create" name="create" type="submit" class="btn btn-default" tabindex="5">
                                <span class="glyphicon glyphicon-play"></span> Login
                            </button>
                        </div>

                        <div class="col-md-4" style="padding-right: 1px">
                            <a href="javascript:void(0);" tabindex="6"
                               onclick="javascript:$('#forgotPassPanel').toggle(400);"><small>Forgot Password?</small>
                            </a>
                        </div>
                    </div>
                </form>

                <form onsubmit=""
                      action='${createLink(controller: 'appUser', action: 'sendPasswordResetLink')}?companyId=${companyId}'
                      method='POST' class="form-horizontal form-widgets" role="form" style="padding-bottom: 4px">
                    <div class="form-group" id="forgotPassPanel" style="display: none;">
                        <div class="col-md-3">&nbsp;</div>

                        <div class="col-md-5">
                            <input type="text" class="form-control" id="loginId" name="loginId" tabindex="7"
                                   placeholder="Enter Login ID"/>
                        </div>

                        <div class="col-md-4">
                            <button type="submit" class="btn btn-default" tabindex="8">
                                <span class="glyphicon glyphicon-play"></span>Send Mail
                            </button>
                        </div>
                    </div>
                </form>

                <div>
                    <g:if test="${flash.message && !flash.success}">
                        <div class='alert alert-danger col-md-12' id="login_msg_"
                             style="margin-bottom: 0">${flash.message}</div>
                    </g:if>
                    <g:if test="${flash.message && flash.success}">
                        <div class='alert alert-success col-md-12' id="login_msg_"
                             style="margin-bottom: 0">${flash.message}</div>
                    </g:if>
                </div>
            </div>
        </div>
    </div>

    <div class="col-md-8" style="padding-right: 1px">
        <img class="img-responsive" src="${grailsApplication.config.theme.application}<app:themeContent
                name="app.login.imgTopRight" companyId="${companyId}" plugin_id="${PluginConnector.PLUGIN_ID}">
        </app:themeContent>"
             align="right"/>
    </div>
</div>

%{--<div class="row">--}%
    %{--<div class="panel panel-default">--}%
        %{--<div class="panel-body">--}%
            %{--<app:themeContent--}%
                    %{--name="app.login.pageCaution" companyId="${companyId}" plugin_id="${PluginConnector.PLUGIN_ID}">--}%
            %{--</app:themeContent>--}%
        %{--</div>--}%
    %{--</div>--}%
%{--</div>--}%

<div class="row">
    <app:themeContent
            name="app.login.businessSupport" companyId="${companyId}" plugin_id="${PluginConnector.PLUGIN_ID}">
    </app:themeContent>
</div>

%{--<div class="row">--}%
    %{--<div class="panel panel-default">--}%
        %{--<div class="panel-body">--}%
            %{--<app:themeContent--}%
                    %{--name="app.login.advertisingPhrase" companyId="${companyId}"--}%
                    %{--plugin_id="${PluginConnector.PLUGIN_ID}">--}%
            %{--</app:themeContent>--}%
        %{--</div>--}%
    %{--</div>--}%
%{--</div>--}%

<div class="row">
    <div class="col-md-12">
        <app:themeContent
                name="app.login.productCopyright" companyId="${companyId}" plugin_id="${PluginConnector.PLUGIN_ID}">
        </app:themeContent>
    </div>
</div>

<div class="row">
    <div class="col-md-6">
        <app:checkVersion pluginId="${PluginConnector.PLUGIN_ID}" companyId="${companyId}">
        </app:checkVersion>
    </div>
</div>
</body>
</html>
