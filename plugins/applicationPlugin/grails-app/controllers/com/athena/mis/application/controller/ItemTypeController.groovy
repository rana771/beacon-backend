package com.athena.mis.application.controller

import com.athena.mis.application.actions.itemtype.*

class ItemTypeController extends BaseController {

    static allowedMethods = [
            create: "POST", show: "POST", update: "POST", delete: "POST", list: "POST"
    ]

    CreateItemTypeActionService createItemTypeActionService
    UpdateItemTypeActionService updateItemTypeActionService
    ListItemTypeActionService listItemTypeActionService
    DeleteItemTypeActionService deleteItemTypeActionService

    /**
     * Show item type
     */
    def show() {
        render(view: "/application/itemType/show")
    }

    /**
     * List & Search item type
     */
    def list() {
        renderOutput(listItemTypeActionService, params)
    }

    /**
     * Create item type
     */
    def create() {
        renderOutput(createItemTypeActionService, params)
    }

    /**
     * Update item type
     */
    def update() {
        renderOutput(updateItemTypeActionService, params)
    }

    /**
     * Delete item type
     */
    def delete() {
        renderOutput(deleteItemTypeActionService, params)
    }
}
