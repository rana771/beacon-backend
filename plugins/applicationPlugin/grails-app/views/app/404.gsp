<%@ page import="com.athena.mis.PluginConnector" %>
<html>
<head>
    <title>Page Not Found</title>
    <link rel="stylesheet" href="${grailsApplication.config.theme.application}/css//bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${grailsApplication.config.theme.application}/css/fontawesome/css/font-awesome.min.css"/>
    <link rel="shortcut icon" href="${grailsApplication.config.theme.application}/images/favicon.ico"
          type="image/x-icon"/>
</head>

<body>
<app:themeContent
        name="app.noAccessPage" plugin_id="${PluginConnector.PLUGIN_ID}">
</app:themeContent>
</body>
</html>