package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.company.CreateCompanyForResellerActionService
import com.athena.mis.application.actions.company.ListCompanyActionService
import com.athena.mis.application.actions.company.ListCompanyForResellerActionService
import com.athena.mis.application.actions.company.UpdateCompanyActionService
import com.athena.mis.application.actions.company.UpdateCompanyForResellerActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.entity.AppCountry
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.model.ListCompanyActionServiceModel
import com.athena.mis.application.model.ListCompanyForResellerActionServiceModel
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.CompanyService
import com.athena.mis.application.service.ContentCategoryService
import com.athena.mis.application.service.AppCountryService
import com.athena.mis.application.service.CurrencyService
import com.athena.mis.application.service.ListCompanyActionServiceModelService
import com.athena.mis.application.service.ListCompanyForResellerActionServiceModelService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/3/2015.
 */

@TestFor(CompanyController)
@Mock([
        AppUser,
        Company,
        Currency,
        CompanyService,
        AppSystemEntityCacheService,
        UpdateCompanyActionService,
        UpdateCompanyForResellerActionService,
        ListCompanyActionService,
        AppAttachmentService,
        AppCountry,
        AppCountryService,
        ContentCategory,
        SystemEntity,
        CurrencyService,
        AppUserService,
        ListCompanyForResellerActionServiceModelService,
        ContentCategoryService,
        ListCompanyActionServiceModelService,
        ListCompanyActionServiceModel,
        ListCompanyForResellerActionService,
        ListCompanyForResellerActionServiceModel,
        BaseService,
        AppSessionService

])

class CompanyControllerSpec extends Specification{

    void setup () {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.updateCompanyActionService.appSessionService.setAppUser(appUser)
        controller.listCompanyActionService.appSessionService.setAppUser(appUser)
        //controller.DeleteCompanyActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show Company' () {

        when:
        request.method = "POST"
        controller.show()

        then:
        view == "/application/company/show"
    }

    def 'Show ForReseller'(){

        when:
        request.method = "POST"
        controller.showForReseller()

        then:
        view == "/application/company/showForReseller"
    }

    def 'Update Company' () {

        setup:

        Currency currency = new Currency(
                version : 0,
                name : "Test Currency",
                symbol : "TK",
                otherCode : "CODE",
                companyId : 1,
                createdBy : 1,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date()
        )

        currency.id = 100
        currency.save(flush: true, failOnError: true, validate: false)

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
                currencyId : currency.id,                     // Currency.id
                contactName : "Contact Name",       // name of contact person
                contactSurname : "Contact Surname", // surname of contact person
                contactPhone : "01812114599",       // phone of contact person
                isActive : true                     // if false then
        )
        company.id = 100
        company.save(flush: true, failOnError: true, validate: false)

        AppCountry appCountry = new AppCountry(
                version: 0,
                name: "Bangladesh",
                code: "BD",
                isdCode: "88",
                phoneNumberPattern: "Test Pattern",
                nationality: "Bangladeshi",
                currencyId: currency.id,
                companyId : company.id,
                createdBy: 1,
                createdOn: new Date(),
                updatedBy: 1,
                updatedOn: new Date()
        )

        appCountry.id = 100
        appCountry.save(flush: true, failOnError: true, validate: false)

        SystemEntity systemEntity = new SystemEntity()
        systemEntity.id = 1
        systemEntity.version = 0
        systemEntity.key = "Test Key"
        systemEntity.type = 1
        systemEntity.isActive = true
        systemEntity.pluginId = 1
        systemEntity.createdBy = 1
        systemEntity.createdOn = new Date()
        systemEntity.updatedOn = new Date()
        systemEntity.updatedBy = 1
        systemEntity.companyId = company.id;

        systemEntity.reservedId = controller.updateCompanyActionService.appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_COMPANY
        systemEntity.save()

        controller.updateCompanyActionService.appSystemEntityCacheService.sysEntityMap.put(controller.updateCompanyActionService.appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY,[systemEntity])

        controller.params.version = "1"
        controller.params.name = "Test Company"
        controller.params.title = "Company Title"               // Company title
        controller.params.createdBy = 1                         // AppUser.id
        controller.params.createdOn = new Date()                // Object creation DateTime
        controller.params.updatedBy = 1                         // AppUser.id
        controller.params.updatedOn = new Date()                // Object updated DateTime
        controller.params.address1  = "Address1"                // Company Address 1
        controller.params.address2 = "Address2"                 // Company Address 2
        controller.params.webUrl = "www.athena.com.bd"          // Company url (unique)
        controller.params.code = "TC"                           // Company code (unique)
        controller.params.countryId = appCountry.id                        // Country.id
        controller.params.currencyId = currency.id                        // Currency.id
        controller.params.contactName = "Contact Name"          // name of contact person
        controller.params.contactSurname = "Contact Surname"    // surname of contact person
        controller.params.contactPhone = "01812114599"          // phone of contact person
        controller.params.isActive = true                       // if false then
        controller.params.id = company.id

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Company has been updated successfully"
        response.json.isError == false
    }

    def 'Update ForReseller'(){

        setup:

        AppUser appUser = new AppUser(
                version : 0,
                loginId : "masud@athena.com.bd",
                username : "Abdul Karim Mondol",
                password : "123456",
                enabled  : true,
                hasSignature : false,
                companyId : 1,
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

        Currency currency = new Currency(
                version : 0,
                name : "Test Currency",
                symbol : "TK",
                otherCode : "CODE",
                companyId : 1,
                createdBy : 1,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date()
        )

        currency.id = 100
        currency.save(flush: true, failOnError: true, validate: false)

        AppCountry appCountry = new AppCountry(
                version: 0,
                name: "Bangladesh",
                code: "BD",
                isdCode: "88",
                phoneNumberPattern: "Test Pattern",
                nationality: "Bangladeshi",
                currencyId: currency.id,
                companyId : 1,
                createdBy: 1,
                createdOn: new Date(),
                updatedBy: 1,
                updatedOn: new Date()
        )
        appCountry.id = 100
        appCountry.save(flush: true, failOnError: true, validate: false)

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
                currencyId : currency.id,                     // Currency.id
                contactName : "Contact Name",       // name of contact person
                contactSurname : "Contact Surname", // surname of contact person
                contactPhone : "01812114599",       // phone of contact person
                isActive : true                     // if false then
        )
        company.id = 100
        company.save(flush: true, failOnError: true, validate: false)

        controller.params.version = "1"
        controller.params.name = "Test Company"
        controller.params.title = "Company Title"               // Company title
        controller.params.createdBy = 1                         // AppUser.id
        controller.params.createdOn = new Date()                // Object creation DateTime
        controller.params.updatedBy = 1                         // AppUser.id
        controller.params.updatedOn = new Date()                // Object updated DateTime
        controller.params.address1  = "Address1"                // Company Address 1
        controller.params.address2 = "Address2"                 // Company Address 2
        controller.params.webUrl = "www.athena.com.bd"          // Company url (unique)
        controller.params.code = "TC"                           // Company code (unique)
        controller.params.countryId = appCountry.id.toString()                       // Country.id
        controller.params.currencyId = currency.id.toString()                       // Currency.id
        controller.params.contactName = "Contact Name"          // name of contact person
        controller.params.contactSurname = "Contact Surname"    // surname of contact person
        controller.params.contactPhone = "01812114599"          // phone of contact person
        controller.params.isActive = true                       // if false then
        controller.params.id = company.id.toString()

        when:
        request.method = 'POST'
        controller.updateForReseller()

        then:
        response.redirectUrl == null
        response.json.message == "Company has been updated successfully"
        response.json.isError == false
    }

    def 'List Company' () {
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
                countryId : 1,                      // Country.id
                currencyId : 1,                     // Currency.id
                contactName : "Contact Name",       // name of contact person
                contactSurname : "Contact Surname", // surname of contact person
                contactPhone : "01812114599",       // phone of contact person
                isActive : true                     // if false then
        )

        company.save(flush: true, failOnError: true, validate: false)

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

    def 'List ForResseler'(){

        setup:
        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listForReseller()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}
