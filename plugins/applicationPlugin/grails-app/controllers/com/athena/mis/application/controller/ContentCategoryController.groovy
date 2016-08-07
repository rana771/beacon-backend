package com.athena.mis.application.controller

import com.athena.mis.application.actions.contentcategory.*

class ContentCategoryController extends BaseController {

    static allowedMethods = [
            list: "POST", show: "POST", update: "POST",
            delete: "POST", listContentCategoryByContentTypeId: "POST"
    ]

    CreateContentCategoryActionService createContentCategoryActionService
    DeleteContentCategoryActionService deleteContentCategoryActionService
    ListContentCategoryActionService listContentCategoryActionService
    UpdateContentCategoryActionService updateContentCategoryActionService
    GetContentCategoryListByContentTypeIdActionService getContentCategoryListByContentTypeIdActionService

    /**
     * show ContentCategory list
     */
    def show() {
        render(view: "/application/contentCategory/show")
    }

    /**
     * list and search ContentCategory
     */
    def list() {
        renderOutput(listContentCategoryActionService, params)
    }

    /**
     * create ContentCategory
     */
    def create() {
        renderOutput(createContentCategoryActionService, params)
    }

    /**
     * update ContentCategory
     */
    def update() {
        renderOutput(updateContentCategoryActionService, params)
    }

    /**
     * delete ContentCategory
     */
    def delete() {
        renderOutput(deleteContentCategoryActionService, params)
    }

    /**
     * get list of ContentCategory by contentTypeId
     */
    def listContentCategoryByContentTypeId() {
        renderOutput(getContentCategoryListByContentTypeIdActionService, params)
    }

    def dropDownContentCategoryReload(){
        render app.dropDownContentCategory(params)
    }
}
