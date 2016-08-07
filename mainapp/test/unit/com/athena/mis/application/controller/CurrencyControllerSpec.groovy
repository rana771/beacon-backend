package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.currency.CreateCurrencyActionService
import com.athena.mis.application.actions.currency.ListCurrencyActionService
import com.athena.mis.application.actions.currency.UpdateCurrencyActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.service.CurrencyService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/1/2015.
 */

@TestFor(CurrencyController)
@Mock([Currency, CurrencyService, CreateCurrencyActionService, UpdateCurrencyActionService,
        ListCurrencyActionService, BaseService, AppSessionService])

class CurrencyControllerSpec extends Specification{

    void setup() {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.createCurrencyActionService.appSessionService.setAppUser(appUser)
        controller.updateCurrencyActionService.appSessionService.setAppUser(appUser)
        controller.listCurrencyActionService.appSessionService.setAppUser(appUser)
//        controller.deleteCurrencyActionService.appSessionService.setAppUser(appUser)
    }

    def 'Test Show Action' () {

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/currency/show'
    }

    def 'Test Create Action' () {

        setup:
        controller.params.id = "1"
        controller.params.version = "1"
        controller.params.name = "Taka"
        controller.params.symbol = "TK"
        controller.params.otherCode = "Other Code"
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
        response.json.message == "Currency has been successfully saved"
        response.json.isError == false

    }

    def 'Test Update Action' () {

        setup:
        Currency crn = new Currency(
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

        crn.id = 100
        crn.save(flush: true, failOnError: true, validate: false)

        controller.params.version = "0"
        controller.params.name = "Taka"
        controller.params.symbol = "TK"
        controller.params.otherCode = "Other Code"
        controller.params.companyId = "1"
        controller.params.createdBy = "1"
        controller.params.createdOn = new Date()
        controller.params.updatedBy = "1"
        controller.params.updatedOn = new Date()
        controller.params.id = crn.id

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == controller.updateCurrencyActionService.CURRENCY_UPDATE_SUCCESS_MESSAGE
        response.json.isError == false
    }

    def 'Test List Action' () {
        setup:
        Currency crn = new Currency(
                id : 1,
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

        crn.save(flush: true, failOnError: true, validate: false)

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
