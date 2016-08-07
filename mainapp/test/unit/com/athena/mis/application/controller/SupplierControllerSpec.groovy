package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.supplier.CreateSupplierActionService
import com.athena.mis.application.actions.supplier.GetSupplierListActionService
import com.athena.mis.application.actions.supplier.ListSupplierActionService
import com.athena.mis.application.actions.supplier.UpdateSupplierActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.model.ListSupplierActionServiceModel
import com.athena.mis.application.service.ListSupplierActionServiceModelService
import com.athena.mis.application.service.SupplierService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 11/29/2015.
 */

@TestFor(SupplierController)
@Mock([
        Supplier,
        SupplierService,
        AppUser,
        CreateSupplierActionService,
        ListSupplierActionService,
        UpdateSupplierActionService,
        GetSupplierListActionService,
        ListSupplierActionServiceModelService,
        ListSupplierActionServiceModel,
        BaseService,
        AppSessionService

])

class SupplierControllerSpec extends Specification{

     void setup() {

        AppUser appUser = new AppUser();
        appUser.id = 1;

        controller.createSupplierActionService.appSessionService.setAppUser(appUser)
        controller.updateSupplierActionService.appSessionService.setAppUser(appUser)
        controller.listSupplierActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show Supplier'() {

        setup:
        controller.params.oId = "TEST_ID"

        when:
        request.method = "POST"
        controller.show()

        then:
        model.oId == "TEST_ID"
        view == "/application/supplier/show"

    }

    def 'Create Supplier' () {

        setup:
        controller.params.id = "1"
        controller.params.name = "Md. Masudul Islam"
        controller.params.accountName = "Test Account Name"
        controller.params.address = "Test Address"
        controller.params.companyId = "1"
        controller.params.supplierTypeId = "1"
        controller.params.itemCount = "1"
        controller.params.createdBy = "1"
        controller.params.createdOn = new Date()
        controller.params.updatedBy = "1"
        controller.params.updatedOn = new Date()

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "Supplier has been saved successfully"
        response.json.isError == false
    }

    def 'Update Supplier' () {

        setup:
        Supplier supp = new Supplier(

                version: 0,
                name: "Md. Masudul Islam",
                address: "Test Address",
                accountName: "Test Account Name",
                bankName: "Test Bank Name",
                bankAccount: "1",
                itemCount: 1,
                supplierTypeId: 1,
                companyId:1,
                createdBy: 1,
                createdOn: new Date(),
                updatedBy: 1,
                updatedOn: new Date())

        supp.id = 99
        supp.save(flush: true, failOnError: true, validate: false)

        controller.params.version ="0"
        controller.params.name = "Update Name after Insert"
        controller.params.address = "Test Address after Insert"
        controller.params.accountName = "Test Account Name"
        controller.params.bankName = "Test Bank Name"
        controller.params.bankAccount = "1"
        controller.params.itemCount = "1"
        controller.params.supplierTypeId = "1"
        controller.params.companyId = "1"
        controller.params.createdBy = supp.id
        controller.params.createdOn = new Date()
        controller.params.updatedBy = supp.id
        controller.params.updatedOn = new Date()
        controller.params.id = supp.id

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Supplier has been updated successfully"
        response.json.isError == false
    }

    def 'List Supplier' () {

        setup:

        Supplier supp = new Supplier(
                id: 1,
                version: 0,
                name: "Md. Masudul Islam",
                address: "Test Address",
                accountName: "Test Account Name",
                bankName: "Test Bank Name",
                bankAccount: "1",
                itemCount: 1,
                supplierTypeId: 1,
                companyId:1,
                createdBy: 1,
                createdOn: new Date(),
                updatedBy: 1,
                updatedOn: new Date())

        supp.save()

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

    def 'List AllSupplier'(){

        setup:

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listAllSupplier()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}
