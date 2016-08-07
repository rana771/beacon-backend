package com.athena.mis.application.controller

import com.athena.mis.application.actions.item.*

class ItemController extends BaseController {

    static allowedMethods = [
            listItemByItemTypeId  : "POST",
            dropDownItemReload    : "POST",
            createFixedAssetItem  : "POST", showFixedAssetItem: "POST",
            updateFixedAssetItem  : "POST", deleteFixedAssetItem: "POST",
            listFixedAssetItem    : "POST",
            showNonInventoryItem  : "POST", listNonInventoryItem: "POST",
            deleteNonInventoryItem: "POST", createNonInventoryItem: "POST",
            updateNonInventoryItem: "POST",
            showInventoryItem     : "POST", listInventoryItem: "POST",
            deleteInventoryItem   : "POST", createInventoryItem: "POST",
            updateInventoryItem   : "POST"
    ]

    //Fixed Asset Item
    CreateItemCategoryFixedAssetActionService createItemCategoryFixedAssetActionService
    UpdateItemCategoryFixedAssetActionService updateItemCategoryFixedAssetActionService
    DeleteItemCategoryFixedAssetActionService deleteItemCategoryFixedAssetActionService
    ListItemCategoryFixedAssetActionService listItemCategoryFixedAssetActionService

    GetItemListByItemTypeActionService getItemListByItemTypeActionService

    //Non Inventory Item
    ListItemCategoryNonInvActionService listItemCategoryNonInvActionService
    CreateItemCategoryNonInvActionService createItemCategoryNonInvActionService
    UpdateItemCategoryNonInvActionService updateItemCategoryNonInvActionService
    DeleteItemCategoryNonInvActionService deleteItemCategoryNonInvActionService

    // Inventory item
    ListItemCategoryInventoryActionService listItemCategoryInventoryActionService
    CreateItemCategoryInventoryActionService createItemCategoryInventoryActionService
    UpdateItemCategoryInventoryActionService updateItemCategoryInventoryActionService
    DeleteItemCategoryInventoryActionService deleteItemCategoryInventoryActionService

    //---------- Start : Fixed Asset Item ----------
    def showFixedAssetItem() {
        render(view: '/application/item/showFixedAsset')
    }

    def createFixedAssetItem() {
        renderOutput(createItemCategoryFixedAssetActionService, params)
    }

    def updateFixedAssetItem() {
        renderOutput(updateItemCategoryFixedAssetActionService, params)
    }

    def deleteFixedAssetItem() {
        renderOutput(deleteItemCategoryFixedAssetActionService, params)
    }

    def listFixedAssetItem() {
        renderOutput(listItemCategoryFixedAssetActionService, params)
    }
    //---------- End : Fixed Asset Item ----------

    //---------- Start : Non-Inventory Item ----------
    def showNonInventoryItem() {
        render(view: "/application/item/showNonInventory")
    }

    def createNonInventoryItem() {
        renderOutput(createItemCategoryNonInvActionService, params)
    }

    def updateNonInventoryItem() {
        renderOutput(updateItemCategoryNonInvActionService, params)
    }

    def deleteNonInventoryItem() {
        renderOutput(deleteItemCategoryNonInvActionService, params)
    }

    def listNonInventoryItem() {
        renderOutput(listItemCategoryNonInvActionService, params)
    }
    //---------- End : Non-Inventory Item ----------

    //---------- Start : Inventory Item ----------
    def showInventoryItem() {
        render(view: "/application/item/showInventory")
    }

    def createInventoryItem() {
        renderOutput(createItemCategoryInventoryActionService, params)
    }

    def updateInventoryItem() {
        renderOutput(updateItemCategoryInventoryActionService, params)
    }

    def deleteInventoryItem() {
        renderOutput(deleteItemCategoryInventoryActionService, params)
    }

    def listInventoryItem() {
        renderOutput(listItemCategoryInventoryActionService, params)
    }
    //---------- End : Inventory Item ----------

    // get item list by item type id(used in AccLc & AccLeaseAccount CRUD)
    def listItemByItemTypeId() {
        renderOutput(getItemListByItemTypeActionService, params)
    }

    def dropDownItemReload(){
        render app.dropDownItem(params)
    }
}
