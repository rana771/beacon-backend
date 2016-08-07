package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.appbankbranch.CreateAppBankBranchActionService
import com.athena.mis.application.actions.appbankbranch.ListAppBankBranchActionService
import com.athena.mis.application.actions.appbankbranch.ListExhDistributionPointActionService
import com.athena.mis.application.actions.appbankbranch.ShowAppBankBranchActionService
import com.athena.mis.application.actions.appbankbranch.UpdateAppBankBranchActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppBank
import com.athena.mis.application.entity.AppBankBranch
import com.athena.mis.application.entity.Company
import com.athena.mis.application.model.ListBankBranchActionServiceModel
import com.athena.mis.application.service.AppBankBranchService
import com.athena.mis.application.service.AppBankService
import com.athena.mis.application.service.CompanyService
import com.athena.mis.application.service.DistrictService
import com.athena.mis.application.service.ListBankBranchActionServiceModelService
import com.athena.mis.application.session.AppSessionService
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 11/30/2015.
 */

@TestFor(AppBankBranchController)

@Mock([
        AppBank,
        AppBankBranch,
        AppBankBranchService,
        ListBankBranchActionServiceModelService,
        ListBankBranchActionServiceModel,
        ShowAppBankBranchActionService,
        AppBankService,
        DistrictService,
        Company,
        CompanyService,
        CreateAppBankBranchActionService,
        UpdateAppBankBranchActionService,
        ListAppBankBranchActionService,
        BaseService,
        AppSessionService
])

class AppBankBranchControllerSpec extends Specification {

    void setup() {
        AppUser appUser = new AppUser()
        appUser.id = 1
        appUser.companyId = 1

//        controller.showBankBranchActionService.appSessionService.setAppUser(appUser)
        controller.createAppBankBranchActionService.appSessionService.setAppUser(appUser)
        controller.updateAppBankBranchActionService.appSessionService.setAppUser(appUser)
//        controller.deleteBankBranchActionService.appSessionService.setAppUser(appUser)
        controller.listAppBankBranchActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show AppBankBranch'() {

        setup:
        AppBank bank = new AppBank(
                version: 0,
                name: "Test Bank",
                code: "BK",
                companyId: 1,
                isSystemBank: false,
                countryId: 1,
                createdBy: 1,
                createdOn: new Date(),
                updatedBy: 1,
                updatedOn: new Date()
        )

        bank.id = 1
        bank.save(flush: true)

        controller.params.oId = bank.id

        when:
        request.method = "POST"
        controller.show()

        then:
        view == "/application/appBankBranch/show"
        model.bankName == "Test Bank"
    }

    def 'Create AppBankBranch'() {

        setup:

        AppBank bank = new AppBank(
                version: 0,
                name: "Test Bank",
                code: "BK",
                companyId: 1,
                isSystemBank: false,
                countryId: 1,
                createdBy: 1,
                createdOn: new Date(),
                updatedBy: 1,
                updatedOn: new Date()
        )

        bank.id = 1
        bank.save(flush: true)

        controller.params.id = "1"
        controller.params.version = "0"
        controller.params.code = "BNK"
        controller.params.name = "IFIC BANK"
        controller.params.address = "Dhanmondi"
        controller.params.bankId = bank.id
        controller.params.districtId = "1"
        controller.params.isSmeServiceCenter = false
        controller.params.isPrincipleBranch = false
        controller.params.companyId = 1
        controller.params.isGlobal = false
        controller.params.createdBy = "1"
        controller.params.createdOn = new Date()
        controller.params.updatedBy = "1"
        controller.params.updatedOn = new Date()
        controller.params.routingNo = "1"

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "Bank branch has been successfully saved"
        response.json.isError == false
    }

    def 'Update AppBankBranch'() {

        setup:
        AppBankBranch bankBranch = new AppBankBranch(
                version: 0,
                code: "BK",
                name: "Test Baranch",
                address: "Test Address",
                bankId: 1,
                districtId: 1,
                isSmeServiceCenter: false,
                isPrincipleBranch: false,
                companyId: 1,
                isGlobal: false,
                createdBy: 1,
                createdOn: new Date(),
                updatedBy: 1,
                updatedOn: new Date(),
                routingNo: 1
        )

        bankBranch.id = 100
        bankBranch.save(flush: true, failOnError: true, validate: false)

        controller.params.version = "0"
        controller.params.code = "BK"
        controller.params.name = "Test Baranch"
        controller.params.address = "Test Address"
        controller.params.bankId = 1
        controller.params.districtId = 1
        controller.params.isSmeServiceCenter = false
        controller.params.isPrincipleBranch = false
        controller.params.companyId = 1
        controller.params.isGlobal = false
        controller.params.createdBy = "1"
        controller.params.createdOn = new Date()
        controller.params.updatedBy = 1
        controller.params.updatedOn = new Date()
        controller.params.routingNo = 1
        controller.params.id = bankBranch.id

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Bank branch has been updated successfully"
        response.json.isError == false
    }

    def 'List AppBankBranch'() {
        setup:
        AppBankBranch bankBranch = new AppBankBranch(
                version: 0,
                code: "BK",
                name: "Test Baranch",
                address: "Test Address",
                bankId: 1,
                districtId: 1,
                isSmeServiceCenter: false,
                isPrincipleBranch: false,
                companyId: 1,
                isGlobal: false,
                createdBy: 1,
                createdOn: new Date(),
                updatedBy: 1,
                updatedOn: new Date(),
                routingNo: 1
        )
        bankBranch.bankId = 1
        bankBranch.save(flush: true, failOnError: true, validate: false)

        controller.params.bankId = bankBranch.bankId
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

//    def 'List DistributionPoint'(){
//
//        setup:
//
//        AppBank bank = new AppBank(
//                version: 0,
//                name: "Test Bank",
//                code: "BK",
//                companyId: 1,
//                isSystemBank: true,
//                countryId: 1,
//                createdBy: 1,
//                createdOn: new Date(),
//                updatedBy: 1,
//                updatedOn: new Date()
//        )
//
//        bank.id = 1
//        bank.save(flush: true)
//
//        controller.params.page = 1
//        controller.params.pageSize = 10
//        controller.params."sort[0]['dir']" = 'asc'
//        controller.params."sort[0]['field']" = 'name'
//        controller.params.take = 10
//        controller.params.skip = 0
//
//        when:
//        request.method = 'POST'
//        controller.listDistributionPoint()
//
//        then:
//        response.json.isError == false
//        response.json.count >= 0
//    }
}
