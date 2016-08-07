package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.role.CreateRoleActionService
import com.athena.mis.application.actions.role.ListMyRoleActionService
import com.athena.mis.application.actions.role.ListRoleActionService
import com.athena.mis.application.actions.role.UpdateRoleActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Role
import com.athena.mis.application.model.ListMyRoleActionServiceModel
import com.athena.mis.application.model.ListRoleActionServiceModel
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.ListRoleActionServiceModelService
import com.athena.mis.application.service.RoleService
import com.athena.mis.application.service.TestDataModelService
import com.athena.mis.application.service.UserRoleService
import com.athena.mis.application.session.AppSessionService
import grails.plugin.springsecurity.SpringSecurityService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/2/2015.
 */

@TestFor(RoleController)
@Mock([
        Role,
        RoleService,
        UserRoleService,
        AppUserService,
        CreateRoleActionService,
        ListRoleActionService,
        UpdateRoleActionService,
        TestDataModelService,
        ListMyRoleActionService,
        ListRoleActionServiceModelService,
        ListMyRoleActionServiceModel,
        ListRoleActionServiceModel,
        SpringSecurityService,
        BaseService,
        AppSessionService])

class RoleControllerSpec extends Specification {

    void setup() {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.createRoleActionService.appSessionService.setAppUser(appUser)
        controller.updateRoleActionService.appSessionService.setAppUser(appUser)
        controller.listRoleActionService.appSessionService.setAppUser(appUser)
//        controller.deleteRoleActionService.appSessionService.setAppUser(appUser)
    }

    def 'Show Role' () {

        setup:
        controller.params.oId = "TEST_ID"

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/role/show'
        model.oId == "TEST_ID"
    }

    def 'Show MyRole'(){
        when:
        request.method = "POST"
        controller.showMyRole()

        then:
        view == '/application/role/showMyRole'
    }

    def 'Create Role'() {

        setup:
        controller.params.version = "0"
        controller.params.authority = "ROLE_5_101"
        controller.params.name = "Admin"
        controller.params.companyId = "1"
        controller.params.roleTypeId = "0"
        controller.params.createdBy = "1"
        controller.params.createdOn = new Date()
        controller.params.updatedBy = "1"
        controller.params.updatedOn = new Date()
        controller.params.pluginId = "1"
        controller.params.isReseller = false

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectedUrl == null
        response.json.message == "Role has been successfully saved"
        response.json.isError == false
    }

    def 'Update Role'() {

        setup:
        Role role = new Role(
                version : 1,
                authority : "ROLE_5_101",
                name : "Admin",
                companyId : 1,
                roleTypeId : 1,
                createdBy : 1,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date(),
                pluginId : 1,
                isReseller : false
        )

        role.id = 1
        role.save(flush: true)

        controller.params.version = "0"
        controller.params.authority = "ROLE_5_101"
        controller.params.name = "Admin"
        controller.params.companyId = "1"
        controller.params.roleTypeId = "1"
        controller.params.createdBy = "1"
        controller.params.createdOn = new Date()
        controller.params.updatedBy = "1"
        controller.params.updatedOn = new Date()
        controller.params.pluginId = "1"
        controller.params.isReseller = false
        controller.params.id = role.id

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Role has been successfully updated"
        response.json.isError == false
    }

    def 'List Role'() {
        setup:
        Role role = new Role(
                version : 1,
                authority : "ROLE_5_101",
                name : "Admin",
                companyId : 1,
                roleTypeId : 0,
                createdBy : 1,
                createdOn : new Date(),
                updatedBy : 1,
                updatedOn : new Date(),
                pluginId : 1,
                isReseller : false
        )

        role.save(flush: true)

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

    def 'List MyRole'(){

        setup:

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listMyRole()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

}
