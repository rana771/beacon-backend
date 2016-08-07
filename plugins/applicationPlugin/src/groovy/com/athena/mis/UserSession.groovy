package com.athena.mis

import com.athena.mis.application.entity.AppUser

import javax.servlet.http.HttpSession

/**
 * Created by rumana on 11/21/2015.
 */
class UserSession {

    String clientBrowser
    String clientIP
    String clientOS
    HttpSession session
    AppUser appUser
}
