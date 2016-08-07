package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.appcountry.CreateAppCountryActionService
import com.athena.mis.application.actions.appcountry.DeleteAppCountryActionService
import com.athena.mis.application.actions.appcountry.ListAppCountryActionService
import com.athena.mis.application.actions.appcountry.UpdateAppCountryActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.entity.AppCountry
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.service.CompanyService
import com.athena.mis.application.service.AppCountryService
import com.athena.mis.application.service.CurrencyService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.codehaus.groovy.grails.commons.GrailsApplication
import spock.lang.Specification

/**
 * Created by Asif on 11/29/2015.
 */

@TestFor(AppCountryController)

@Mock([
        AppCountry,
        AppCountryService,
        Company,
        CompanyService,
        Currency,
        CurrencyService,
        CreateAppCountryActionService,
        UpdateAppCountryActionService,
        ListAppCountryActionService,
        BaseService,
        AppSessionService
])

class AppCountryControllerSpec extends Specification{

    void setup() {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.createAppCountryActionService.appSessionService.setAppUser(appUser)
        controller.updateAppCountryActionService.appSessionService.setAppUser(appUser)
        controller.listAppCountryActionService.appSessionService.setAppUser(appUser)
        //controller.deleteEmployeeActionService.appSessionService.setAppUser(appUser)
    }

    def 'Test Show Action' () {

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/appCountry/show'
    }

    def 'Test Create Action' () {

        setup:
        controller.params.id = "1"
        controller.params.version = "0"
        controller.params.name = "Test Country"
        controller.params.code = "Test Code"
        controller.params.isdCode = "88"
        controller.params.phoneNumberPattern = "Sample Pattern"
        controller.params.nationality = "Bangladeshi"
        controller.params.currencyId = "1"
        controller.params.companyId = "1"
        controller.params.createdBy = "1"
        controller.params.createdOn = new Date()
        controller.params.updatedBy = "1"
        controller.params.updatedOn = new Date()

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == controller.createAppCountryActionService.COUNTRY_CREATE_SUCCESS_MESSAGE
        response.json.isError == false
    }

    def 'Test Update Action' () {

        setup:
        AppCountry cnty = new AppCountry(
                version: 0,
                name: "Bangladesh",
                code: "BD",
                isdCode: "88",
                phoneNumberPattern: "Test Pattern",
                nationality: "Bangladeshi",
                currencyId: 1,
                companyId : 100,
                createdBy: 1,
                createdOn: new Date(),
                updatedBy: 1,
                updatedOn: new Date()
        )

        cnty.id=100
        cnty.save(flush: true, failOnError: true, validate: false)

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
        company.id = 100
        company.save(flush: true, failOnError: true, validate: false)

        controller.params.version = "0"
        controller.params.name = "Test Country"
        controller.params.code = "Test Code"
        controller.params.isdCode = "88"
        controller.params.phoneNumberPattern = "Sample Pattern"
        controller.params.nationality = "Bangladeshi"
        controller.params.currencyId = "1"
        controller.params.companyId = company.id
        controller.params.createdBy = "1"
        controller.params.createdOn = new Date()
        controller.params.updatedBy = "1"
        controller.params.updatedOn = new Date()
        controller.params.id = cnty.id

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == " Country has been updated successfully"
        response.json.isError == false
    }

    def 'Test List Action' () {
        AppCountry cnty = new AppCountry(id: 1, version: 0, name: "Bangladesh", code: "BD", isdCode: "88",
                phoneNumberPattern: "Test Pattern", nationality: "Bangladeshi", currencyId: 1,
                companyId:1, createdBy: 1, createdOn: new Date(), updatedBy: 1, updatedOn: new Date())

        cnty.save(flush: true, failOnError: true, validate: false)

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
}
