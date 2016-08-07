<html>
<head>
    <title>Page Not Found</title>
    <link rel="stylesheet" href="${grailsApplication.config.theme.application}/css//bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${grailsApplication.config.theme.application}/css/fontawesome/css/font-awesome.min.css"/>
    <link rel="shortcut icon" href="${grailsApplication.config.theme.application}/images/favicon.ico"
          type="image/x-icon"/>
</head>

<body>
<div class="jumbotron">
    <div class="container">
        <div class="row">
            <div class="col-md-10">
                <h1>Sorry, this page is not available.</h1>

                <p>Let's try one of the following remedies:</p>
                <ul>
                    <li>If you typed the page address in the address bar, make sure that it is spelled correctly.</li>

                    <li>Click the <a
                            href="javascript:history.back(1)">back</a> button to go back to the previous page.
                    </li>
                    <li>Click <a href="<g:resource
                            dir="/"/>">here</a>  to go directly to the Application home page.
                    </li>
                    <li>If all else fails, contact with system administrator.</li>
                </ul>
            </div>
            <div class="col-md-2">
                <span class="fa fa-exclamation-triangle" style="font-size: 15em;color: #428BCA"></span>
            </div>
        </div>
    </div>
</div>
</body>
</html>