package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.appbank.CreateAppBankActionService
import com.athena.mis.application.actions.appbank.ListAppBankActionService
import com.athena.mis.application.actions.appbank.UpdateAppBankActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppBank
import com.athena.mis.application.entity.Company
import com.athena.mis.application.model.ListBankActionServiceModel
import com.athena.mis.application.service.AppBankService
import com.athena.mis.application.service.CompanyService
import com.athena.mis.application.service.ListBankActionServiceModelService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 11/30/2015.
 */

@TestFor(AppBankController)
@Mock([
        AppBank,
        AppBankService,
        Company ,
        CompanyService,
        CreateAppBankActionService,
        ListBankActionServiceModelService,
        ListBankActionServiceModel,
        UpdateAppBankActionService,
        ListAppBankActionService,
        BaseService,
        AppSessionService
])

class AppBankControllerSpec extends Specification{

        void setup () {
                AppUser appUser = new AppUser()
                appUser.id = 1

                controller.createAppBankActionService.appSessionService.setAppUser(appUser)
                controller.updateAppBankActionService.appSessionService.setAppUser(appUser)
                controller.listAppBankActionService.appSessionService.setAppUser(appUser)
                //controller.deleteBankActionService.appSessionService.setAppUser(appUser)
        }

        def 'Test Show Action' () {
                setup:
                controller.params.oId = "ATHENA_ID"

                when:
                request.method = "POST"
                controller.show()

                then:
                model.oId == "ATHENA_ID"
                view == "/application/appBank/show"
        }


        def 'Test Create Action' () {

                setup:
                controller.params.id = "1"
                controller.params.version = "0"
                controller.params.name = "Test Bank"
                controller.params.code = "1"
                controller.params.companyId = "1"
                controller.params.isSystemBank = false
                controller.params.countryId = "1"
                controller.params.createdBy = "1"
                controller.params.createdOn = new Date()
                controller.params.updatedBy = "1"
                controller.params.updatedOn = new Date()

                when:
                request.method = 'POST'
                controller.create()

                then:
                response.redirectedUrl == null
                response.json.message == "Bank has been successfully saved"
                response.json.isError == false
        }

        def 'Test Update Action' () {

                setup:
                AppBank bnk = new AppBank(
                        version : 0,
                        name : "Test Bank",
                        code : "1",
                        companyId : 1,
                        isSystemBank : false,
                        countryId : 1,
                        createdBy : 1,
                        createdOn : new Date(),
                        updatedBy : 1,
                        updatedOn : new Date()
                )
                bnk.id = 100
                bnk.save(flush: true, failOnError: true, validate: false)

                controller.params.id = "100"
                controller.params.version = "0"
                controller.params.name = "Test Bank"
                controller.params.code = "1"
                controller.params.companyId = "1"
                controller.params.isSystemBank = false
                controller.params.countryId = "1"
                controller.params.createdBy = "1"
                controller.params.createdOn = new Date()
                controller.params.updatedBy = "1"
                controller.params.updatedOn = new Date()
                controller.params.id = bnk.id

                when:
                request.method = 'POST'
                controller.update()

                then:
                response.redirectUrl == null
                response.json.message == controller.updateAppBankActionService.BANK_UPDATE_SUCCESS_MESSAGE
                response.json.isError == false
        }

        def 'Test List Action' () {
                setup:
                AppBank bnk = new AppBank(
                        id : 100,
                        version : 0,
                        name : "Test Bank",
                        code : "1",
                        companyId : 1,
                        isSystemBank : false,
                        countryId : 1,
                        createdBy : 1,
                        createdOn : new Date(),
                        updatedBy : 1,
                        updatedOn : new Date()
                )

                bnk.save(flush: true, failOnError: true, validate: false)

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
