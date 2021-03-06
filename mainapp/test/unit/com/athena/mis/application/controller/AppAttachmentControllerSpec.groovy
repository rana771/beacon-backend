package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.appattachment.CreateAppAttachmentActionService
import com.athena.mis.application.actions.appattachment.DeleteAppAttachmentActionService
import com.athena.mis.application.actions.appattachment.DownloadAppAttachmentActionService
import com.athena.mis.application.actions.appattachment.ListAppAttachmentActionService
import com.athena.mis.application.actions.appattachment.ShowAppAttachmentActionService
import com.athena.mis.application.actions.appattachment.UpdateAppAttachmentActionService
import com.athena.mis.application.entity.AppAttachment
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.SystemEntityType
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ContentCategoryService
import com.athena.mis.application.service.SystemEntityTypeService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/21/2015.
 */

@TestFor(AppAttachmentController)

@Mock([
        AppAttachment,
        AppAttachmentService,
        Project,
        SystemEntity,
        SystemEntityType,
        ContentCategoryService,
        ShowAppAttachmentActionService,
        AppSystemEntityCacheService,
        SystemEntityTypeService,
        CreateAppAttachmentActionService,
        DeleteAppAttachmentActionService,
        ListAppAttachmentActionService,
        UpdateAppAttachmentActionService,
        BaseService,
        AppSessionService
])

class AppAttachmentControllerSpec extends Specification{

    void setup(){
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.createAppAttachmentActionService.appSessionService.setAppUser(appUser)
        controller.updateAppAttachmentActionService.appSessionService.setAppUser(appUser)
        controller.showAppAttachmentActionService.appSessionService.setAppUser(appUser)
//        controller.deleteAppAttachmentActionService.appSessionService.setAppUser(appUser)
        controller.listAppAttachmentActionService.appSessionService.setAppUser(appUser)
//        controller.downloadAppAttachmentActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show AppAttachment' (){

        setup:

        SystemEntity systemEntity = new SystemEntity(
                version : 0,
                key : "Test Key",
                type : 1,
                reservedId : AppSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY,
                isActive : true,
                pluginId : 1,
                createdBy : 1,
                createdOn : new Date(),
                updatedOn : new Date(),
                updatedBy : 1,
                companyId : 1
        )
        systemEntity.id = 1
        systemEntity.save()

        controller.showAppAttachmentActionService.appSystemEntityCacheService.init()

        controller.params.leftMenu = "/#LeftMenu"
        controller.params.oId = 1
        controller.params.entityTypeId = 1

        when:
        request.method = "POST"
        controller.show()

        then:
        view == "/application/appAttachment/show"
    }

    def 'Create AppAttachment' () {

        setup:

        AppAttachment appAttachment = new AppAttachment(

                version : 0,                   // entity version in the persistence layer
                contentCategoryId : 1,         // ContentCategory.Id (Signature, Drawing, Logo, Archived file)
                contentTypeId : 1,             // SystemEntity.id (Document, Image)
                entityTypeId : 1,              // SystemEntity.id (Project, customers, suppliers, appUsers)
                entityId : 1,                  // Project.id, AppUser.id etc
                content : 1,                   // content
                caption : "Test Attachment",   // caption of the content
                extension : "jpeg",            // Extension of the content(e.g : doc, txt, exl, png, jpeg, gif etc)
                fileName : "Test file",        // file name of the content
                createdBy : 1,                 // AppUser.id
                createdOn : new Date(),        // Object creation DateTime
                updatedBy : 1,                 // AppUser.id
                updatedOn : new Date(),        // Object updated DateTime
                companyId : 1,                 // Company.id
                expirationDate : new Date()
        )

        appAttachment.id = 1
        appAttachment.save(flush: true, failOnError: true, validate: false)

        controller.params.id = 1                        // primary key (Auto generated by its own sequence)
        controller.params.version = "0"                   // entity version in the persistence layer
        controller.params.contentCategoryId = 1         // ContentCategory.Id (Signature, Drawing, Logo, Archived file)
        controller.params.contentTypeId = 1             // SystemEntity.id (Document, Image)
        controller.params.entityTypeId = 1              // SystemEntity.id (Project, customers, suppliers, appUsers)
        controller.params.entityId = 1                  // Project.id, AppUser.id etc
        controller.params.content = 1                   // content
        controller.params.caption = "Test Attachment"   // caption of the content
        controller.params.extension = "jpeg"            // Extension of the content(e.g : doc, txt, exl, png, jpeg, gif etc)
        controller.params.fileName = "Test file"        // file name of the content
        controller.params.createdBy = 1                 // AppUser.id
        controller.params.createdOn = new Date()        // Object creation DateTime
        controller.params.updatedBy = 1                 // AppUser.id
        controller.params.updatedOn = new Date()        // Object updated DateTime
        controller.params.companyId = 1                 // Company.id
        controller.params.attachmentId = appAttachment.id

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "Attachment has been successfully saved"
        response.json.isError == false
    }

    def 'Update AppAttachment' () {

        setup:

        AppAttachment appAttachment = new AppAttachment(

                version : 0,                   // entity version in the persistence layer
                contentCategoryId : 1,         // ContentCategory.Id (Signature, Drawing, Logo, Archived file)
                contentTypeId : 1,             // SystemEntity.id (Document, Image)
                entityTypeId : 1,              // SystemEntity.id (Project, customers, suppliers, appUsers)
                entityId : 1,                  // Project.id, AppUser.id etc
                content : 1,                   // content
                caption : "Test Attachment",   // caption of the content
                extension : "jpeg",            // Extension of the content(e.g : doc, txt, exl, png, jpeg, gif etc)
                fileName : "Test file",        // file name of the content
                createdBy : 1,                 // AppUser.id
                createdOn : new Date(),        // Object creation DateTime
                updatedBy : 1,                 // AppUser.id
                updatedOn : new Date(),        // Object updated DateTime
                companyId : 1,                 // Company.id
                expirationDate : new Date()
        )

        appAttachment.id = 1
        appAttachment.save(flush: true, failOnError: true, validate: false)

        controller.params.id = "1"                        // primary key (Auto generated by its own sequence)
        controller.params.version = "0"                   // entity version in the persistence layer
        controller.params.contentCategoryId = 1         // ContentCategory.Id (Signature, Drawing, Logo, Archived file)
        controller.params.contentTypeId = 1             // SystemEntity.id (Document, Image)
        controller.params.entityTypeId = 1              // SystemEntity.id (Project, customers, suppliers, appUsers)
        controller.params.entityId = 1                  // Project.id, AppUser.id etc
        controller.params.content = 1                   // content
        controller.params.caption = "Test Attachment"   // caption of the content
        controller.params.extension = "jpeg"            // Extension of the content(e.g : doc, txt, exl, png, jpeg, gif etc)
        controller.params.fileName = "Test file"        // file name of the content
        controller.params.createdBy = 1                 // AppUser.id
        controller.params.createdOn = new Date()        // Object creation DateTime
        controller.params.updatedBy = 1                 // AppUser.id
        controller.params.updatedOn = new Date()        // Object updated DateTime
        controller.params.companyId = 1                 // Company.id
        controller.params.expirationDate = new Date()
        controller.params.attachmentId = 1

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Attachment has been updated successfully"
        response.json.isError == false
    }

    def 'List Action' () {
        setup:

       Project project = new Project(
                code: "Update Test",
                name: "Insert for Update",
                version: 0,
                description: 'N/A',
                createdOn: new Date(),
                createdBy: 1,
                updatedOn: new Date(),
                updatedBy: 1, companyId: 1,
                contentCount: 1,
                startDate: new Date(),
                endDate: new Date(),
                isApproveInFromSupplier: false,
                isApproveInFromInventory: false,
                isApproveInvOut: false,
                isApproveConsumption: false,
                isApproveProduction: false
       )
        project.id = 1
        project.save(flush: true, failOnError: true, validate: false)

        AppAttachment appAttachment = new AppAttachment(
                id : 1,                     // primary key (Auto generated by its own sequence)
                version : 0,                // entity version in the persistence layer
                contentCategoryId : 1,      // ContentCategory.Id (Signature, Drawing, Logo, Archived file)
                contentTypeId : 1,          // SystemEntity.id (Document, Image)
                entityId : 1,               // Project.id, AppUser.id etc
                content : 1,                // content
                caption : "Test Caption",   // caption of the content
                extension : "doc",          // Extension of the content(e.g : doc, txt, exl, png, jpeg, gif etc)
                fileName : "Test File",     // file name of the content
                createdBy : 1,              // AppUser.id
                createdOn : new Date(),     // Object creation DateTime
                updatedBy : 1,              // AppUser.id
                updatedOn : new Date(),     // Object updated DateTime
                companyId : 1,              // Company.id
                expirationDate : new Date() // expire date of content
        )
        appAttachment.entityTypeId = project.id
        appAttachment.save(flush: true, failOnError: true, validate: false)

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'caption'
        controller.params.take = 10
        controller.params.skip = 0
        controller.params.entityTypeId = appAttachment.entityTypeId
        controller.params.entityId = appAttachment.entityId

        when:
        request.method = 'POST'
        controller.list()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}
