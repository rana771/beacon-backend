<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <meta name="language" content="en"/>
    <title><g:layoutTitle default="MIS"/></title>
    <link rel="shortcut icon" href="${grailsApplication.config.theme.application}/images/favicon.ico"
          type="image/x-icon"/>
    <g:render template='/layouts/commonInclude'/>
    <g:render template="/layouts/scriptMain"/>
    <g:layoutHead/>
</head>

<body class="graphic-container">
<g:render template='/layouts/commonModals'/>
<div class="row" style="height: 100%;margin-right: 0" id="containerParent">
    <div class="col-md-2" style="height: 100%;padding-right: 0;position: relative;" id="containerLeftMenu">
        <ul id="leftMenuKendo" style="border:none;">

        </ul>
        <g:render template='/layouts/copyright'/>
    </div>
    <div class="col-md-10" style="height: 100%" id="mainRightContainer">
        <div class="row" id="topMenuDiv">
            <g:render template='/layouts/dockMenu'/>
        </div>

        <div class="row">
            <div id='contentHolder' style='padding:5px 7px 0px 5px; overflow: auto;height: 100%'>
                <g:layoutBody/>
            </div>
        </div>
    </div>
</div>
</body>
</html>