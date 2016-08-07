package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.supplieritem.CreateSupplierItemActionService
import com.athena.mis.application.actions.supplieritem.GetItemListSupplierItemActionService
import com.athena.mis.application.actions.supplieritem.ListSupplierItemActionService
import com.athena.mis.application.actions.supplieritem.ShowSupplierItemActionService
import com.athena.mis.application.actions.supplieritem.UpdateSupplierItemActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SupplierItem
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.model.ListSupplierItemActionServiceModel
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.service.ItemTypeService
import com.athena.mis.application.service.ListSupplierItemActionServiceModelService
import com.athena.mis.application.service.SupplierItemService
import com.athena.mis.application.service.SupplierService
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.service.TestDataModelService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 11/29/2015.
 */

@TestFor(SupplierItemController)
@Mock([
        Supplier ,
        SupplierService,
        SupplierItem,
        SupplierItemService,
        Item,
        ItemService,
        ItemTypeService,
        TestDataModelService,
        SystemEntity,
        SystemEntityService,
        ShowSupplierItemActionService,
        GetItemListSupplierItemActionService,
        CreateSupplierItemActionService,
        UpdateSupplierItemActionService,
        ListSupplierItemActionService,
        ListSupplierItemActionServiceModelService,
        ListSupplierItemActionServiceModel,
        ShowSupplierItemActionService,
        BaseService,
        AppSessionService
])

class SupplierItemControllerSpec extends Specification{

    void setup() {
        AppUser appUser = new AppUser()
        appUser.id = 1

        //controller.showSupplierItemActionService.appSessionService.setAppUser(appUser)
        controller.createSupplierItemActionService.appSessionService.setAppUser(appUser)
        controller.updateSupplierItemActionService.appSessionService.setAppUser(appUser)
        //controller.deleteSupplierItemActionService.appSessionService.setAppUser(appUser)
        controller.listSupplierItemActionService.appSessionService.setAppUser(appUser)
        //controller.getItemListSupplierItemActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show Supplier Item' () {

        setup:

//        Supplier supp = new Supplier(
//                version: 1,
//                name: "Md. Masudul Islam",
//                address: "Test Address",
//                accountName: "Test Account Name",
//                bankName: "Test Bank Name",
//                bankAccount: "1",
//                itemCount: 1,
//                supplierTypeId: 1,
//                companyId:1,
//                createdBy: 1,
//                createdOn: new Date(),
//                updatedBy: 1,
//                updatedOn: new Date())
//
//        supp.id = 99
//        supp.save(flush: true, failOnError: true, validate: false)
//
//        Item item = new Item (
//                version: 1,
//                name: "Test Item",
//                code : "ITM",                         // Item Code
//                unit : "Piece",                        // Item Unit
//                itemTypeId : 1,                     // ItemType.id
//                categoryId : 1,                    // SystemEntity.id (Inventory, NonInventory, FixedAsset)
//                valuationTypeId : 1,                // SystemEntity.id (e.g : FIFO, LIFO etc)
//                isIndividualEntity : false,          // flag for individual entity (for item type Fixed Asset)
//                isFinishedProduct : false,           // is the item finished product (for item type Inventory)
//                companyId : 1,                     // Company.id
//                createdBy : 1,                      // AppUser.id
//                createdOn : new Date(),                     // Object creation DateTime
//                updatedBy : 1,                      // AppUser.id
//                updatedOn : new Date(),                      // Object Updated DateTime
//                isDirty : false
//        )
//
//        item.id = 90
//        item.save(flush: true, failOnError: true, validate: false)
//
//        SupplierItem suppItem = new SupplierItem(
//                version : 0,
//                supplierId : supp.id,
//                itemId : item.id,
//                companyId : 1,
//                createdBy : 1,
//                createdOn : new Date(),
//                updatedBy : 1,
//                updatedOn : new Date()
//        )
//
//        suppItem.id = 99
//        suppItem.save(flush: true, failOnError: true, validate: false)
//
//        controller.params.oId = supp.id

        when:
        request.method = "POST"
        controller.show()

        then:
        view == "/application/supplierItem/show"
    }

    def 'Create Supplier Item' () {

        setup:

        Supplier supp = new Supplier(
                version: 1,
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

        Item item = new Item (
                version: 1,
                name: "Test Item",
                code : "ITM",                         // Item Code
                unit : "Piece",                        // Item Unit
                itemTypeId : 1,                     // ItemType.id
                categoryId : 1,                    // SystemEntity.id (Inventory, NonInventory, FixedAsset)
                valuationTypeId : 1,                // SystemEntity.id (e.g : FIFO, LIFO etc)
                isIndividualEntity : false,          // flag for individual entity (for item type Fixed Asset)
                isFinishedProduct : false,           // is the item finished product (for item type Inventory)
                companyId : 1,                     // Company.id
                createdBy : 1,                      // AppUser.id
                createdOn : new Date(),                     // Object creation DateTime
                updatedBy : 1,                      // AppUser.id
                updatedOn : new Date(),                      // Object Updated DateTime
                isDirty : false
        )

        item.id = 90
        item.save(flush: true, failOnError: true, validate: false)

        controller.params.id = "1"
        controller.params.itemId = item.id
        controller.params.supplierId = supp.id
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
        response.json.message == "Supplier-Item has been saved successfully"
        response.json.isError == false
    }

    def 'Update Supplier Item' (){

        setup:

        Supplier supp = new Supplier(
                version: 1,
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

        SupplierItem supItem = new SupplierItem(
                itemId : 1,
                version : 1,
                supplierId : 1,
                companyId : 1,
                createdBy : 1,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date()
        )

        Item item = new Item (
                version: 1,
                name: "Test Item",
                code : "ITM",                         // Item Code
                unit : "Piece",                        // Item Unit
                itemTypeId : 1,                     // ItemType.id
                categoryId : 1,                    // SystemEntity.id (Inventory, NonInventory, FixedAsset)
                valuationTypeId : 1,                // SystemEntity.id (e.g : FIFO, LIFO etc)
                isIndividualEntity : false,          // flag for individual entity (for item type Fixed Asset)
                isFinishedProduct : false,           // is the item finished product (for item type Inventory)
                companyId : 1,                     // Company.id
                createdBy : 1,                      // AppUser.id
                createdOn : new Date(),                     // Object creation DateTime
                updatedBy : 1,                      // AppUser.id
                updatedOn : new Date(),                      // Object Updated DateTime
                isDirty : false
        )

        item.id = 90
        item.save(flush: true, failOnError: true, validate: false)


        supItem.id = 1
        supItem.save(flush: true, failOnError: true, validate: false)

        controller.params.id = supItem.id
        controller.params.version = "1"
        controller.params.itemId = item.id
        controller.params.supplierId = supp.id
        controller.params.companyId = "1"
        controller.params.createdBy = "1"
        controller.params.createdOn = new Date()
        controller.params.updatedBy = "1"
        controller.params.updatedOn = new Date()

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Supplier's Item has been updated successfully"
        response.json.isError == false
    }

    def 'List Supplier Item ' (){
        setup:

        SupplierItem supItem = new SupplierItem(
                itemId : 1,
                supplierId : 1,
                companyId : 1,
                createdBy : 1,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date()
        )

        supItem.save(flush: true, failOnError: true, validate: false)

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0
        controller.params.supplierId = 1

        when:
        request.method = 'POST'
        controller.list()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'Get ItemListForSupplierItem'(){

        setup:

        Item item = new Item (
                version: 1,
                name: "Test Item",
                code : "ITM",                         // Item Code
                unit : "Piece",                        // Item Unit
                itemTypeId : 1,                     // ItemType.id
                categoryId : 1,                    // SystemEntity.id (Inventory, NonInventory, FixedAsset)
                valuationTypeId : 1,                // SystemEntity.id (e.g : FIFO, LIFO etc)
                isIndividualEntity : false,          // flag for individual entity (for item type Fixed Asset)
                isFinishedProduct : false,           // is the item finished product (for item type Inventory)
                companyId : 1,                     // Company.id
                createdBy : 1,                      // AppUser.id
                createdOn : new Date(),                     // Object creation DateTime
                updatedBy : 1,                      // AppUser.id
                updatedOn : new Date(),                      // Object Updated DateTime
                isDirty : false
        )

        item.id = 90
        item.save(flush: true, failOnError: true, validate: false)

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0
        controller.params.supplierId = 1
        controller.params.itemTypeId = 1

        when:
        request.method = 'POST'
        controller.getItemListForSupplierItem()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}
