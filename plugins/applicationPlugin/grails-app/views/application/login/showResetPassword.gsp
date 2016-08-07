<%@ page import="com.athena.mis.PluginConnector" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="cache-control" content="public"/>
    <title>Change Your Password</title>
    <script type="text/javascript"
            src="${grailsApplication.config.theme.application}/js/jquery/jquery-1.8.1.min.js"></script>
    <script type="text/javascript"
            src="${grailsApplication.config.theme.application}/js/kendo/kendo.web.min.js"></script>
    <script type="text/javascript"
            src="${grailsApplication.config.theme.application}/js/bootstrap/js/bootstrap.min.js"></script>
    <link rel="shortcut icon" href="${grailsApplication.config.theme.application}/images/favicon.ico"
          type="image/x-icon"/>
    <link rel="stylesheet" href="${grailsApplication.config.theme.application}/css//bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${grailsApplication.config.theme.application}/css/kendo/kendo.common.min.css"/>
    <link rel="stylesheet" href="${grailsApplication.config.theme.application}/css/kendo/kendo.silver.min.css"/>
    <style type="text/css">
    .linkText a {
        -moz-border-radius: 4px;
        border-radius: 4px;
        padding: 2px 2px;
        font: 10px verdana, sans-serif;
        text-decoration: none;
        color: #3B5998;
    }

    .linkText a:hover {
        background: #5b74a8;
        color: #FFF;
    }
    </style>
</head>

<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-3">
        </div>

        <div class="col-md-5">
            <div class="container-fluid">
                <div class="alert alert-danger"
                     style="margin-top: 5px;margin-bottom: 0">Your password has expired on:: ${userInfoMap?.expireDate}</div>

                <div class="panel panel-default">
                    <div class="panel-heading">
                        <div class="panel-title">
                            <div style="font-size: 15px;font-weight: bold">Change Password</div>
                        </div>
                    </div>

                    <div class="panel-body">
                        <form onsubmit=""
                              action='${createLink(controller: 'appUser', action: 'resetExpiredPassword')}?companyId=${companyId}'
                              method='POST' id='loginForm' class="form-horizontal form-widgets" role="form"
                              autocomplete='on'>
                            <div class="form-group">
                                <label class="col-md-4 control-label" for="userName">User Name:</label>

                                <div class="col-md-8">
                                    <span>${userInfoMap?.userName}</span>
                                    <input type='hidden' name='userName' id='userName'
                                           value="${userInfoMap?.userName}"/>
                                    <input type='hidden' name='userId' id='userId' value="${userInfoMap?.userId}"/>
                                    <input type='hidden' name='expireDate' id='expireDate'
                                           value="${userInfoMap?.expireDate}"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-4 control-label" for="oldPassword">Old Password:</label>

                                <div class="col-md-8">
                                    <input type="password" class="k-textbox" id="oldPassword" name="oldPassword"
                                           tabindex="1" placeholder="Type old password"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-4 control-label" for="password">New Password:</label>

                                <div class="col-md-8">
                                    <input type="password" class="k-textbox" id="password" name="password"
                                           tabindex="2" placeholder="Type Password"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-4 control-label" for="password">Retype Password:</label>

                                <div class="col-md-8">
                                    <input type="password" class="k-textbox" id="retypePassword" name="retypePassword"
                                           tabindex="3" placeholder="Type the password again"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="col-md-4">&nbsp;</div>

                                <div class="col-md-3">
                                    <button id="create" name="create" type="submit" data-role="button"
                                            class="k-button k-button-icontext"
                                            role="button" tabindex="4"
                                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Change
                                    </button>
                                </div>

                                <div class="col-md-5">
                                    <a href="${createLink(controller: 'login', action: 'auth')}">Back to Sign In Page</a>
                                </div>
                            </div>

                            <div class="form-group" style="margin-bottom: 0px">
                                <g:if test="${flash.message && !flash.success}">
                                    <div class='alert alert-danger col-md-12' id="login_msg_">${flash.message}</div>
                                </g:if>
                                <g:if test="${flash.message && flash.success}">
                                    <div class='alert alert-success col-md-12' id="login_msg_">${flash.message}</div>
                                </g:if>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <img src="${grailsApplication.config.theme.application}<app:themeContent
                    plugin_id="${PluginConnector.PLUGIN_ID}"
                    name="app.login.imgTopRight" companyId="${companyId}">
            </app:themeContent>"
                 align="right"/>
        </div>
    </div>

    <div class="row">
        <div class="panel panel-default">
            <div class="panel-body">
                <app:themeContent plugin_id="${PluginConnector.PLUGIN_ID}" name="app.login.advertisingPhrase"
                                  companyId="${companyId}">
                </app:themeContent>
            </div>
        </div>
    </div>
</div>
</body>
</html>