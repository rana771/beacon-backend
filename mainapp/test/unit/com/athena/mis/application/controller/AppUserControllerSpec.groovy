package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.appcompanyuser.CreateAppCompanyUserActionService
import com.athena.mis.application.actions.appcompanyuser.DeleteAppCompanyUserActionService
import com.athena.mis.application.actions.appcompanyuser.ListAppCompanyUserActionService
import com.athena.mis.application.actions.appcompanyuser.UpdateAppCompanyUserActionService
import com.athena.mis.application.actions.appuser.ActivateAppUserActionService
import com.athena.mis.application.actions.appuser.ChangeUserPasswordActionService
import com.athena.mis.application.actions.appuser.CheckUserPasswordActionService
import com.athena.mis.application.actions.appuser.CreateAppUserActionService
import com.athena.mis.application.actions.appuser.DeleteAppUserActionService
import com.athena.mis.application.actions.appuser.ForceLogoutOnlineUserActionService
import com.athena.mis.application.actions.appuser.ListAllAppUserActionService
import com.athena.mis.application.actions.appuser.ListAppUserActionService
import com.athena.mis.application.actions.appuser.ListOnlineUserActionService
import com.athena.mis.application.actions.appuser.ManageUserPasswordActionService
import com.athena.mis.application.actions.appuser.RegisterAppUserActionService
import com.athena.mis.application.actions.appuser.ResetExpiredPasswordActionService
import com.athena.mis.application.actions.appuser.ResetPasswordActionService
import com.athena.mis.application.actions.appuser.SendMailForPasswordResetActionService
import com.athena.mis.application.actions.appuser.ShowAppUserRegistrationActionService
import com.athena.mis.application.actions.appuser.ShowForResetPasswordActionService
import com.athena.mis.application.actions.appuser.UpdateAllAppUserActionService
import com.athena.mis.application.actions.appuser.UpdateAppUserActionService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.model.ListAllAppUserActionServiceModel
import com.athena.mis.application.model.ListAppCompanyUserActionServiceModel
import com.athena.mis.application.model.ListAppUserActionServiceModel
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.ApplicationSessionService
import com.athena.mis.application.service.CompanyService
import com.athena.mis.application.service.ContentCategoryService
import com.athena.mis.application.service.ListAllAppUserActionServiceModelService
import com.athena.mis.application.service.ListAppCompanyUserActionServiceModelService
import com.athena.mis.application.service.ListAppUserActionServiceModelService
import com.athena.mis.application.service.RoleService
import com.athena.mis.application.session.AppSessionService
import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/28/2015.
 */

@TestFor(AppUserController)

@Mock([
        SpringSecurityService,
        AppUser,
        AppUserService,
        Company,
        CompanyService,
        RoleService,
        CreateAppUserActionService,
        UpdateAppUserActionService,
        ListAppUserActionService,
        DeleteAppUserActionService,
        ManageUserPasswordActionService,
        ChangeUserPasswordActionService,
        CheckUserPasswordActionService,
        CreateAppCompanyUserActionService,
        DeleteAppCompanyUserActionService,
        ListAppCompanyUserActionService,
        ListAllAppUserActionServiceModelService,
        UpdateAppCompanyUserActionService,
        ForceLogoutOnlineUserActionService,
        ListOnlineUserActionService,
        SendMailForPasswordResetActionService,
        ResetPasswordActionService,
        ShowForResetPasswordActionService,
        ResetExpiredPasswordActionService,
        ListAllAppUserActionService,
        UpdateAllAppUserActionService,
        RegisterAppUserActionService,
        ActivateAppUserActionService,
        ShowAppUserRegistrationActionService,
        ListAllAppUserActionServiceModel,
        BaseService,
        AppSessionService,
        AppAttachmentService,
        ContentCategoryService,
        AppConfigurationService,
        ListAppUserActionServiceModelService,
        ListAppCompanyUserActionServiceModelService,
        ListAppCompanyUserActionServiceModel,
        ListAppUserActionServiceModel,
        AppSystemEntityCacheService,
        ApplicationSessionService
])

class AppUserControllerSpec extends Specification{

    void setup(){
        AppUser appUser = new AppUser()
        appUser.id = 1
        def mockSecurityService = mockFor(SpringSecurityService)
        controller.createAppCompanyUserActionService.springSecurityService=mockSecurityService.createMock()
        controller.createAppUserActionService.appSessionService.setAppUser(appUser)
        controller.updateAppUserActionService.appSessionService.setAppUser(appUser)
        controller.listAppUserActionService.appSessionService.setAppUser(appUser)
        controller.deleteAppUserActionService.appSessionService.setAppUser(appUser)
        controller.manageUserPasswordActionService.appSessionService.setAppUser(appUser)
        controller.changeUserPasswordActionService.appSessionService.setAppUser(appUser)
        controller.checkUserPasswordActionService.appSessionService.setAppUser(appUser)
//        controller.springSecurityService.appSessionService.setAppUser(appUser)

        // action services for company user
        controller.createAppCompanyUserActionService.appSessionService.setAppUser(appUser)
        controller.deleteAppCompanyUserActionService.appSessionService.setAppUser(appUser)
        controller.listAppCompanyUserActionService.appSessionService.setAppUser(appUser)
        controller.updateAppCompanyUserActionService.appSessionService.setAppUser(appUser)

        controller.forceLogoutOnlineUserActionService.appSessionService.setAppUser(appUser)
        controller.listOnlineUserActionService.appSessionService.setAppUser(appUser)

        // actions for password reset
        controller.sendMailForPasswordResetActionService.appSessionService.setAppUser(appUser)
        controller.showForResetPasswordActionService.appSessionService.setAppUser(appUser)
        controller.resetPasswordActionService.appSessionService.setAppUser(appUser)
        controller.resetExpiredPasswordActionService.appSessionService.setAppUser(appUser)

        controller.listAllAppUserActionService.appSessionService.setAppUser(appUser)
        controller.updateAllAppUserActionService.appSessionService.setAppUser(appUser)

        controller.registerAppUserActionService.appSessionService.setAppUser(appUser)
        controller.activateAppUserActionService.appSessionService.setAppUser(appUser)
        controller.showAppUserRegistrationActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show AppUser' (){
        when:
        request.method = "POST"
        controller.show()

        then:
        view == "/application/appUser/show"
    }

    def 'Show ForCompanyUser' (){
        when:
        request.method = "POST"
        controller.showForCompanyUser()

        then:
        view == "/application/appCompanyUser/show"
    }

    def 'Show ResetPassword' (){

        setup:
        controller.params.companyId = "1"

        when:
//        request.method = "POST"
        controller.showResetPassword()

        then:
        model.companyId == "1"
    }

    def 'Show AllUser' (){
        when:
        request.method = "POST"
        controller.showAllUser()

        then:
        view == "/application/appUser/showForReseller"
    }

    def 'Show Registration' () {
        setup:

        Company company = new Company(
                version : 1,
                name : "Test Company",
                title : "Company Title",            // Company title
                createdBy : 1,                      // AppUser.id
                createdOn : new Date(),             // Object creation DateTime
                updatedBy : 1,                      // AppUser.id
                updatedOn : new Date(),             // Object updated DateTime
                address1  : "Address1",             // Company Address 1
                address2  : "Address2",             // Company Address 2
                webUrl    : "www.athena.com.bd",    // Company url (unique)
                code      : "TC",                   // Company code (unique)
                countryId : 100,                      // Country.id
                currencyId : 100,                     // Currency.id
                contactName : "Contact Name",       // name of contact person
                contactSurname : "Contact Surname", // surname of contact person
                contactPhone : "01812114599",       // phone of contact person
                isActive : true                     // if false then
        )
        company.id = 100
        company.save(flush: true, failOnError: true, validate: false)

        when:
        request.method = "POST"
        controller.showRegistration()

        then:
        view == "/application/appUser/showForRegistration"
    }

    def 'Show OnlineUser' () {
        when:
        request.method = "POST"
        controller.showOnlineUser()

        then:
        view == "/application/appUser/showOnlineUser"
    }

    def 'Create AppUser'(){

        setup:
        AppUser appUser = new AppUser(
                version : 0,
                loginId : "masud@athena.com.bd",
                username : "Abdul Karim Mondol",
                password : "123456",
                enabled  : true,
                hasSignature : false,
                companyId : 1,
                employeeId : 1,
                accountExpired : false,
                accountLocked  : false,
                passwordExpired : false,
                isCompanyUser   :  false,
                nextExpireDate  : new Date(),
                activationLink  : "Test Link",
                isActivatedByMail : false,
                passwordResetLink  : "Test Link",
                isPowerUser  : true,
                isSystemUser : false,
                isDisablePasswordExpiration : false,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date(),
                isReseller : false
        )
        appUser.id = 1
        appUser.save(flush: true, failOnError: true, validate: false)
        controller.params.version = 0
        controller.params.loginId = "masud@athena.com.bd"
        controller.params.username = "Abdul Karim Mondol"
        controller.params.password = "123456"
        controller.params.enabled  = true
        controller.params.hasSignature = false
        controller.params.companyId = 1
        controller.params.employeeId = 1
        controller.params.accountExpired = false
        controller.params.accountLocked  = false
        controller.params.passwordExpired = false
        controller.params.isCompanyUser   =  false
        controller.params.nextExpireDate  = new Date()
        controller.params.activationLink  = "Test Link"
        controller.params.isActivatedByMail = false
        controller.params.passwordResetLink  = "Test Link"
        controller.params.isPowerUser  = true
        controller.params.isSystemUser = false
        controller.params.isDisablePasswordExpiration = false
        controller.params.createdOn = new Date()
        controller.params.updatedBy = 1
        controller.params.updatedOn = new Date()
        controller.params.isReseller = false


        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "User has been saved successfully"
        response.json.isError == false
    }

    def 'Create ForCompanyUser'(){
        setup:

        AppUser appUser = new AppUser(
                version : 0,
                loginId : "masud@athena.com.bd",
                username : "Abdul Karim Mondol",
                password : "123456",
                enabled  : true,
                hasSignature : false,
                companyId : 1,
                employeeId : 1,
                accountExpired : false,
                accountLocked  : false,
                passwordExpired : false,
                isCompanyUser   :  false,
                nextExpireDate  : new Date(),
                activationLink  : "Test Link",
                isActivatedByMail : false,
                passwordResetLink  : "Test Link",
                isPowerUser  : true,
                isSystemUser : false,
                isDisablePasswordExpiration : false,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date(),
                isReseller : false
        )

        appUser.id = 1
        appUser.save(flush: true, failOnError: true, validate: false)

        controller.params.version = 0
        controller.params.loginId = "masud@athena.com.bd"
        controller.params.username = "Abdul Karim Mondol"
        controller.params.password = "123456"
        controller.params.enabled  = true
        controller.params.hasSignature = false
        controller.params.companyId = 1
        controller.params.employeeId = 1
        controller.params.accountExpired = false
        controller.params.accountLocked  = false
        controller.params.passwordExpired = false
        controller.params.isCompanyUser   =  false
        controller.params.nextExpireDate  = new Date()
        controller.params.activationLink  = "Test Link"
        controller.params.isActivatedByMail = false
        controller.params.passwordResetLink  = "Test Link"
        controller.params.isPowerUser  = true
        controller.params.isSystemUser = false
        controller.params.isDisablePasswordExpiration = false
        controller.params.createdOn = new Date()
        controller.params.updatedBy = 1
        controller.params.updatedOn = new Date()
        controller.params.isReseller = false

        when:
        request.method = 'POST'
        controller.createForCompanyUser()

        then:
        response.redirectedUrl == null
        response.json.message == "Company user has been saved successfully.Now active your Company for Log-In"
        response.json.isError == false
    }

    def 'Update AppUser'(){
        setup:

        AppUser appUser = new AppUser(
                version : 0,
                loginId : "masud@athena.com.bd",
                username : "Abdul Karim Mondol",
                password : "123456",
                enabled  : true,
                hasSignature : false,
                companyId : 1,
                employeeId : 1,
                accountExpired : false,
                accountLocked  : false,
                passwordExpired : false,
                isCompanyUser   :  false,
                nextExpireDate  : new Date(),
                activationLink  : "Test Link",
                isActivatedByMail : false,
                passwordResetLink  : "Test Link",
                isPowerUser  : true,
                isSystemUser : false,
                isDisablePasswordExpiration : false,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date(),
                isReseller : false
        )
        appUser.id = 1
        appUser.save(flush: true, failOnError: true, validate: false)

        controller.params.id = appUser.id
        controller.params.version = "0"
        controller.params.loginId = "masud@athena.com.bd"
        controller.params.username = "Abdul Karim Mondol"
        controller.params.password = "123456"
        controller.params.enabled  = true
        controller.params.hasSignature = false
        controller.params.companyId = 1
        controller.params.employeeId = 1
        controller.params.accountExpired = false
        controller.params.accountLocked  = false
        controller.params.passwordExpired = false
        controller.params.isCompanyUser   =  false
        controller.params.nextExpireDate  = new Date()
        controller.params.activationLink  = "Test Link"
        controller.params.isActivatedByMail = false
        controller.params.passwordResetLink  = "Test Link"
        controller.params.isPowerUser  = true
        controller.params.isSystemUser = false
        controller.params.isDisablePasswordExpiration = false
        controller.params.createdOn = new Date()
        controller.params.updatedBy = 1
        controller.params.updatedOn = new Date()
        controller.params.isReseller = false

        when:
        request.method = 'POST'
        controller.update()
        then:
        response.redirectUrl == null
        response.json.message == "User has been updated successfully"
        response.json.isError == false
    }

    def 'Update ForCompanyUser'(){
        setup:

        AppUser appUser = new AppUser(
                version : 0,
                loginId : "masud@athena.com.bd",
                username : "Abdul Karim Mondol",
                password : "123456",
                enabled  : true,
                hasSignature : false,
                companyId : 1,
                employeeId : 1,
                accountExpired : false,
                accountLocked  : false,
                passwordExpired : false,
                isCompanyUser   :  false,
                nextExpireDate  : new Date(),
                activationLink  : "Test Link",
                isActivatedByMail : false,
                passwordResetLink  : "Test Link",
                isPowerUser  : true,
                isSystemUser : false,
                isDisablePasswordExpiration : false,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date(),
                isReseller : false
        )
        appUser.id = 1
        appUser.save(flush: true, failOnError: true, validate: false)

        controller.params.compnayId = appUser.companyId
        controller.params.id = appUser.id.toString()
        controller.params.version = appUser.version.toString()
        controller.params.password = appUser.password
        controller.params.existingPass = 1

        when:
        request.method = 'POST'
        controller.updateForCompanyUser()
        then:
        response.redirectUrl == null
        response.json.message == "Company user has been updated successfully"
        response.json.isError == false
    }

    def 'Update AllUser'(){
        setup:

        AppUser appUser = new AppUser(
                version : 0,
                loginId : "masud@athena.com.bd",
                username : "Abdul Karim Mondol",
                password : "123456",
                enabled  : true,
                hasSignature : false,
                companyId : 1,
                employeeId : 1,
                accountExpired : false,
                accountLocked  : false,
                passwordExpired : false,
                isCompanyUser   :  false,
                nextExpireDate  : new Date(),
                activationLink  : "Test Link",
                isActivatedByMail : false,
                passwordResetLink  : "Test Link",
                isPowerUser  : true,
                isSystemUser : false,
                isDisablePasswordExpiration : false,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date(),
                isReseller : false
        )
        appUser.id = 1
        appUser.save(flush: true, failOnError: true, validate: false)

        controller.params.id = appUser.id.toString()
        controller.params.version = appUser.version.toString()

        when:
        request.method = 'POST'
        controller.updateAllUser()
        then:
        response.redirectUrl == null
        response.json.message == "User has been updated successfully"
        response.json.isError == false
    }

    def 'List AppUser' () {
        setup:

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.list()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'List ForCompanyUser' () {
        setup:

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listForCompanyUser()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'List OnlineUser' () {
        setup:

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listOnlineUser()

        then:
        response.json.count >= 0
    }

    def 'List AllUser' () {
        setup:

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listAllUser()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

//    def 'Manage Password'(){
//
//    }
//
//    def 'Change Password'(){
//
//    }
//
//    def 'Check Password'(){
//
//    }
//
//    def 'Reet Password'(){
//
//    }
//
//    def 'Reet Expired Password'(){
//
//    }
//
//    def 'Send Password Reset Link'(){
//
//    }
//
//    def 'Force Logout Online User'(){
//
//    }
//
//    def 'Register'(){
//
//    }
//
//    def 'Activate'(){
//
//    }
}
