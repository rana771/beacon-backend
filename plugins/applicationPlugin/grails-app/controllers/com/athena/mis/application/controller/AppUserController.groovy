package com.athena.mis.application.controller

import com.athena.mis.application.actions.appcompanyuser.CreateAppCompanyUserActionService
import com.athena.mis.application.actions.appcompanyuser.DeleteAppCompanyUserActionService
import com.athena.mis.application.actions.appcompanyuser.ListAppCompanyUserActionService
import com.athena.mis.application.actions.appcompanyuser.UpdateAppCompanyUserActionService
import com.athena.mis.application.actions.appuser.*
import grails.plugin.springsecurity.SpringSecurityService

class AppUserController extends BaseController {

    static allowedMethods = [
            show: "POST", create: "POST", update: "POST",
            delete: "POST", list: "POST", managePassword: "POST", changePassword: "POST", checkPassword: "POST",
            showForCompanyUser: "POST", createForCompanyUser: "POST", updateForCompanyUser: "POST",
            deleteForCompanyUser: "POST", listForCompanyUser: "POST",
            showOnlineUser: "POST", listOnlineUser: "POST", forceLogoutOnlineUser: "POST", showProfile:"POST",
            sendPasswordResetLink: "POST", showResetPassword: "GET", resetPassword: "POST", resetExpiredPassword: "POST",
            showAllUser: "POST", listAllUser: "POST", updateAllUser: "POST"
    ]

    CreateAppUserActionService createAppUserActionService
    UpdateAppUserActionService updateAppUserActionService
    UpdateAppUserProfileActionService updateAppUserProfileActionService
    ListAppUserActionService listAppUserActionService
    DeleteAppUserActionService deleteAppUserActionService
    ManageUserPasswordActionService manageUserPasswordActionService
    ChangeUserPasswordActionService changeUserPasswordActionService
    CheckUserPasswordActionService checkUserPasswordActionService
    SpringSecurityService springSecurityService

    // action services for company user
    CreateAppCompanyUserActionService createAppCompanyUserActionService
    DeleteAppCompanyUserActionService deleteAppCompanyUserActionService
    ListAppCompanyUserActionService listAppCompanyUserActionService
    UpdateAppCompanyUserActionService updateAppCompanyUserActionService

    ForceLogoutOnlineUserActionService forceLogoutOnlineUserActionService
    ListOnlineUserActionService listOnlineUserActionService

    // actions for password reset
    SendMailForPasswordResetActionService sendMailForPasswordResetActionService
    ShowForResetPasswordActionService showForResetPasswordActionService
    ResetPasswordActionService resetPasswordActionService
    ResetExpiredPasswordActionService resetExpiredPasswordActionService

    ListAllAppUserActionService listAllAppUserActionService
    UpdateAllAppUserActionService updateAllAppUserActionService

    RegisterAppUserActionService registerAppUserActionService
    ActivateAppUserActionService activateAppUserActionService
    ShowAppUserRegistrationActionService showAppUserRegistrationActionService
    ShowAppUserForEditActionService showAppUserForEditActionService
    RenderProfileImageActionService renderProfileImageActionService
    UploadAppUserProfileImageActionService uploadAppUserProfileImageActionService
    UploadAppUserDocumentActionService uploadAppUserDocumentActionService

    /**
     * show AppUser list
     */
    def show() {
        render(view: "/application/appUser/show")
    }

    def showProfile(){
        String view = "/application/appUser/showProfile"
        renderView(showAppUserForEditActionService, params, view)
    }

    def uploadProfileImage() {
        renderOutput(uploadAppUserProfileImageActionService, params)
    }

    def uploadDocument() {
        renderOutput(uploadAppUserDocumentActionService, params)
    }

    def renderProfileImage() {
        Map result = renderProfileImageActionService.execute(params)
        response.outputStream << result.stream
    }

    /**
     * create AppUser
     */
    def create() {
        renderOutput(createAppUserActionService, params)
    }

    /**
     * update AppUser
     */
    def update() {
        renderOutput(updateAppUserActionService, params)
    }

    /**
     * delete AppUser
     */
    def delete() {
        renderOutput(deleteAppUserActionService, params)
    }

    /**
     * list and search AppUser
     */
    def list() {
        renderOutput(listAppUserActionService, params)
    }

    /**
     * show UI for change password
     */
    def managePassword() {
        String view = "/application/appUser/managePassword"
        renderView(manageUserPasswordActionService, params, view)
    }

    def updateProfile(){
        renderOutput(updateAppUserProfileActionService, params)
    }

    /**
     * change user password
     */
    def changePassword() {
        renderOutput(changeUserPasswordActionService, params)
    }

    /**
     * check user password to reset request map
     */
    def checkPassword() {
        renderOutput(checkUserPasswordActionService, params)
    }

    /**
     * show company user(AppUser) list
     */
    def showForCompanyUser() {
        render(view: "/application/appCompanyUser/show")
    }

    /**
     * create company user(AppUser)
     */
    def createForCompanyUser() {
        renderOutput(createAppCompanyUserActionService, params)
    }

    /**
     * update company user(AppUser)
     */
    def updateForCompanyUser() {
        renderOutput(updateAppCompanyUserActionService, params)
    }

    /**
     * delete company user(AppUser)
     */
    def deleteForCompanyUser() {
        renderOutput(deleteAppCompanyUserActionService, params)
    }

    /**
     * list and search company user(AppUser)
     */
    def listForCompanyUser() {
        renderOutput(listAppCompanyUserActionService, params)
    }

    /**
     * show UI to view list of online user
     */
    def showOnlineUser() {
        render(view: "/application/appUser/showOnlineUser")
    }

    /**
     * show list of online user
     */
    def listOnlineUser() {
        renderOutput(listOnlineUserActionService, params)
    }

    /**
     * log out online user
     */
    def forceLogoutOnlineUser() {
        renderOutput(forceLogoutOnlineUserActionService, params)
    }

    /**
     * update appUser(generate link etc.) and send mail for password reset
     */
    def sendPasswordResetLink() {
        Map result = getServiceResponse(sendMailForPasswordResetActionService, params)
        Boolean isError = result.isError
        if (isError.booleanValue()) {
            flash.success = false
            flash.message = result.message
            redirect(controller: 'login', action: 'auth')
            return
        }
        flash.success = true
        flash.message = result.message
        Boolean isPortalView = (Boolean) result.isPortalView
        if(isPortalView.booleanValue()) {
            render (view: result.view, model: [message: result.message])
        } else {
            redirect(controller: 'login', action: 'auth')
        }
    }

    /**
     * render the page to reset forgotten password
     */
    def showResetPassword() {
        String view = '/application/login/showForgotPassword'
        Map result = getServiceResponse(showForResetPasswordActionService, params)
        Boolean isError = result.isError
        if (isError.booleanValue()) {
            flash.success = false
            flash.message = result.message
            render view: view, model: [userInfoMap: result.userInfoMap, companyId: params.companyId]
            return
        }
        flash.message = null
        render view: view, model: [userInfoMap: result.userInfoMap, companyId: params.companyId]
    }

    /**
     * reset user password
     */
    def resetPassword() {
        String view = "/application/login/showForgotPassword"
        Map userInfoMap = [passwordResetLink: params.link, username: params.username]
        Map result = getServiceResponse(resetPasswordActionService, params)
        Boolean isError = result.isError
        if (isError.booleanValue()) {
            flash.success = false
            flash.message = result.message
            render view: view, model: [userInfoMap: userInfoMap, companyId: params.companyId]
            return
        }
        flash.success = true
        flash.message = result.message
        render view: view, model: [userInfoMap: userInfoMap, companyId: params.companyId]
    }

    /**
     * reset expired user password
     */
    def resetExpiredPassword() {
        Boolean isError
        String view = '/application/login/showResetPassword'
        Map userInfoMap = [userName: params.userName, userId: params.userId, expireDate: params.expireDate]
        Map result = getServiceResponse(resetExpiredPasswordActionService, params)
        isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            flash.success = false
            flash.message = result.message
            render(view: view, model: [userInfoMap: userInfoMap, companyId: params.companyId])
            return
        }
        flash.success = true
        flash.message = result.message
        render(view: '/application/login/auth', model: [userInfoMap: userInfoMap])
    }

    /**
     * show all user
     */
    def showAllUser() {
        String view = "/application/appUser/showForReseller"
        render(view: view)
    }

    /**
     * get list of all user
     */
    def listAllUser() {
        renderOutput(listAllAppUserActionService, params)
    }

    /**
     * update all user
     */
    def updateAllUser() {
        renderOutput(updateAllAppUserActionService, params)
    }

    def showRegistration() {
        renderView(showAppUserRegistrationActionService, params, "/application/appUser/showForRegistration")
    }


    def register() {
        String sessionId = session.id
        params.put("sessionId", sessionId)
        Map result = getServiceResponse(registerAppUserActionService, params)
        Boolean isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            render(view: '/application/appUser/showForRegistration', model: result)
            return
        }
        flash.message = result.message
        flash.success = true
        redirect(controller: 'login', action: 'auth')
    }

    /**
     * Customer activation
     */
    def activate() {
        Boolean isError
        LinkedHashMap result = (LinkedHashMap) getServiceResponse(activateAppUserActionService, params)
        isError = (Boolean) result.isError
        if (isError.booleanValue()) {
            flash.message = result.message
            flash.success = false
            redirect(controller: 'login', action: 'auth')
            return
        }
        flash.message = result.message
        flash.success = true
        redirect(controller: 'login', action: 'auth')
    }

    def switchToBangla() {
        session['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE'] = new Locale("bn")
        redirect(uri: "/")
    }

    def switchToEnglish() {
        session['org.springframework.web.servlet.i18n.SessionLocaleResolver.LOCALE'] = new Locale("en")
        redirect(url: "/")
    }
}
