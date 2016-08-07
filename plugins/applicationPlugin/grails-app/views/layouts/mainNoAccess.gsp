<%@ page import="com.athena.mis.utility.UIConstants; grails.util.Environment;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="language" content="en"/>
    <title><g:layoutTitle default="MIS"/></title>
    <link rel="shortcut icon" href="${grailsApplication.config.theme.application}/images/favicon.ico"
          type="image/x-icon"/>
    <g:render template='/layouts/commonInclude'/>
    <script type="text/javascript">
        $(document).ready(function () {
            showLoadingSpinner(false);
        });
    </script>
</head>

<body>
<div class='pane_title'>
    <i id="spinner" class="fa fa-2x fa-refresh fa-spin" style="margin: 2px 4px;color:#9F9F9F"></i>

    <div id="dockMenuContainer" class="pull-right">
        <ul class="nav nav-pills">
            <li class="dropdown">
                <a data-toggle="dropdown">
                    <span class="fa fa-lg fa-user" style="color: #428BCA"></span>&nbsp;<span
                        class="fa fa-caret-down"></span>
                </a>
                <ul role="menu" class="dropdown-menu dropdown-menu-right" style="z-index: 9999">
                    <li style="text-align: center"><app:sessionUser property='username'></app:sessionUser></li>
                    <li class="divider"></li>
                    <li><a href="<g:createLink controller="logout"/>"><span
                            class="fa fa-sign-out"></span>&nbsp;Logout</a></li>
                </ul>
            </li>
        </ul>
    </div>
</div>
<g:render template='/login/noAccess'/>
</body>
</html>

