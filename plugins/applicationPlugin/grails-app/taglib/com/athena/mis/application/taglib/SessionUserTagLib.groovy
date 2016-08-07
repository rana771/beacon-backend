package com.athena.mis.application.taglib

import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.session.AppSessionService
import org.springframework.beans.factory.annotation.Autowired

/**
 * Retrieve different property values of session user
 */
class SessionUserTagLib {

    static namespace = "app"

    @Autowired
    AppSessionService appSessionService

    private static final String USER_NAME = "username"
    private static final String SHORT_USER_NAME = "shortUserName"
    private static final String PROPERTY = "property"
    private static final String ID = "id"
    private static final String EMPTY_SPACE = ""
    private static final String SINGLE_SPACE = " "

    /**
     * Shows logged-in user information within tag
     * attr takes the attribute 'property' (which exists in AppUser domain)
     * example: <app:sessionUser property="username"></app:sessionUser>
     *
     * @attr property REQUIRED -the property name of AppUse domain
     */
    def sessionUser = { attrs, body ->

        String property = attrs.remove(PROPERTY)

        AppUser appUser = appSessionService.getAppUser()
        if (!appUser) {
            out << EMPTY_SPACE
            return
        }
        String output = null
        switch (property) {
            case USER_NAME:
                output = appUser.username
                break
            case SHORT_USER_NAME:
                output = appUser.username.tokenize(SINGLE_SPACE).first()
                break
            case ID:
                output = appUser.id
                break
            default:
                output = EMPTY_SPACE   // default value
        }
        out << output
    }
}
