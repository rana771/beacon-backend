package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.contentcategory.CreateContentCategoryActionService
import com.athena.mis.application.actions.contentcategory.DeleteContentCategoryActionService
import com.athena.mis.application.actions.contentcategory.GetContentCategoryListByContentTypeIdActionService
import com.athena.mis.application.actions.contentcategory.ListContentCategoryActionService
import com.athena.mis.application.actions.contentcategory.UpdateContentCategoryActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.ContentCategory
import com.athena.mis.application.model.ListContentCategoryActionServiceModel
import com.athena.mis.application.service.ContentCategoryService
import com.athena.mis.application.service.ListContentCategoryActionServiceModelService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/17/2015.
 */

@TestFor(ContentCategoryController)

@Mock([
        ContentCategory,
        ContentCategoryService,
        CreateContentCategoryActionService,
        ListContentCategoryActionService,
        UpdateContentCategoryActionService,
        GetContentCategoryListByContentTypeIdActionService,
        ListContentCategoryActionServiceModelService,
        ListContentCategoryActionServiceModel,
        BaseService,
        AppSessionService
])

class ContentCategoryControllerSpec extends Specification{

    void setup(){
        AppUser appUser = new AppUser()
        appUser.id = 1


        controller.createContentCategoryActionService.appSessionService.setAppUser(appUser)
        controller.updateContentCategoryActionService.appSessionService.setAppUser(appUser)
//        controller.deleteContentCategoryActionService.appSessionService.setAppUser(appUser)
        controller.listContentCategoryActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show Content Category'() {

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/contentCategory/show'
    }


    def 'Create Content Category' () {
        setup:
        controller.params.id = 1
        controller.params.version = 0                       // entity version in the persistence layer
        controller.params.name = "Signature"                // unique for content type -- Content Category Name(Signature, Image, PhotoId, Drawing, Archive etc.)
        controller.params.contentTypeId = 1                 // SystemEntity.id (Document, Image)
        controller.params.systemContentCategory = "PHOTO"   // if not reserved then id; if reserved then specific data (PHOTO, LOGO etc)
        controller.params.maxSize = 1024*1024*1             // Maximum file size (e.g : 1024*1024*1, 1024*1024*5 etc.)
        controller.params.width  = 512                      // width of the Content(e.g : 512 etc.)
        controller.params.height  = 1024                    // height of the Content(e.g : 1024 etc.)
        controller.params.extension = "doc, excel, pdf"
        controller.params.createdOn  = new Date()           // Object created DateTime
        controller.params.createdBy = 1                     // AppUser.id
        controller.params.updatedOn                         // Object updated DateTime
        controller.params.updatedBy  = 1                    // AppUser.id
        controller.params.companyId  = 1                    // Company.id
        controller.params.isReserved  = true                // flag for reserved

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "Content category has been created successfully"
    }

    def 'Update Content Category' () {
        setup:

        ContentCategory cc = new ContentCategory(
                version : 0,                       // entity version in the persistence layer
                name : "Signature",                // unique for content type -- Content Category Name(Signature, Image, PhotoId, Drawing, Archive etc.)
                contentTypeId : 1,                 // SystemEntity.id (Document, Image)
                systemContentCategory : "PHOTO",   // if not reserved then id; if reserved then specific data (PHOTO, LOGO etc)
                maxSize : 1024*1024*1,             // Maximum file size (e.g : 1024*1024*1, 1024*1024*5 etc.)
                width  : 512,                      // width of the Content(e.g : 512 etc.)
                height  : 1024,                    // height of the Content(e.g : 1024 etc.)
                extension : "doc, excel, pdf",
                createdOn  : new Date(),           // Object created DateTime
                createdBy : 1,                     // AppUser.id
                updatedOn     : new Date(),                      // Object updated DateTime
                updatedBy  : 1 ,                   // AppUser.id
                companyId  : 1,                    // Company.id
                isReserved  : true
        )

        cc.id =1
        cc.save(flush: true)

        controller.params.id = 1
        controller.params.version = "0"                       // entity version in the persistence layer
        controller.params.name = "Signature"                // unique for content type -- Content Category Name(Signature, Image, PhotoId, Drawing, Archive etc.)
        controller.params.contentTypeId = 1                 // SystemEntity.id (Document, Image)
        controller.params.systemContentCategory = "PHOTO"   // if not reserved then id; if reserved then specific data (PHOTO, LOGO etc)
        controller.params.maxSize = 1024*1024*1             // Maximum file size (e.g : 1024*1024*1, 1024*1024*5 etc.)
        controller.params.width  = 512                      // width of the Content(e.g : 512 etc.)
        controller.params.height  = 1024                    // height of the Content(e.g : 1024 etc.)
        controller.params.extension = "doc, excel, pdf"
        controller.params.createdOn  = new Date()           // Object created DateTime
        controller.params.createdBy = 1                     // AppUser.id
        controller.params.updatedOn                         // Object updated DateTime
        controller.params.updatedBy  = 1                    // AppUser.id
        controller.params.companyId  = 1                    // Company.id
        controller.params.isReserved  = true

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Content category has been updated successfully"
        response.json.isError == false
    }

    def 'List Content Category' () {

        setup:

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

    def 'List ContentCategoryByContentTypeId'(){

        setup:

        ContentCategory cc = new ContentCategory(
                version : 0,                       // entity version in the persistence layer
                name : "Signature",                // unique for content type -- Content Category Name(Signature, Image, PhotoId, Drawing, Archive etc.)
                contentTypeId : 1,                 // SystemEntity.id (Document, Image)
                systemContentCategory : "PHOTO",   // if not reserved then id; if reserved then specific data (PHOTO, LOGO etc)
                maxSize : 1024*1024*1,             // Maximum file size (e.g : 1024*1024*1, 1024*1024*5 etc.)
                width  : 512,                      // width of the Content(e.g : 512 etc.)
                height  : 1024,                    // height of the Content(e.g : 1024 etc.)
                extension : "doc, excel, pdf",
                createdOn  : new Date(),           // Object created DateTime
                createdBy : 1,                     // AppUser.id
                updatedOn     : new Date(),                      // Object updated DateTime
                updatedBy  : 1 ,                   // AppUser.id
                companyId  : 1,                    // Company.id
                isReserved  : true
        )

        cc.id =1
        cc.save(flush: true)

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0
        controller.params.contentTypeId = cc.contentTypeId

        when:
        request.method = 'POST'
        controller.listContentCategoryByContentTypeId()

        then:
        response.json.isError == false
//        response.json.count >= 0
    }
}
