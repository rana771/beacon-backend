class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?" {
            constraints {
                // apply constraints here
            }
        }
        "/showDoc/$id"(controller: 'docDocument',action: 'renderDocument')
        "/showDocViewer/$id"(controller: 'docDocument', action: 'renderDocumentViewer')
        "/"(view: "/index")
        "500"(view: '/app/404')  // Internal Server Error
        "505"(view: '/app/404')  // HTTP version not supported
        "400"(view: '/app/404')  // Bad request or syntax
        "404"(view: '/app/404')  // Page not found
        "405"(view: '/app/404')  // Method not allowed
        "408"(view: '/app/404')  // Request timeout
        "414"(view: '/app/404')  // Request-URI too long
        "403"(view: 'app/404')  // Access Forbidden (e.g. Attempts to access the WEB-INF or META-INF directories)
        "401" (controller: 'logout', action: 'index')
    }
}
