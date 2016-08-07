package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.requestmap.ListAssignedRoleActionService
import com.athena.mis.application.actions.requestmap.ListAvailableRoleActionService
import com.athena.mis.application.actions.requestmap.ReloadRequestMapActionService
import com.athena.mis.application.actions.requestmap.ResetRequestMapByPluginIdActionService
import com.athena.mis.application.actions.requestmap.UpdateRequestMapActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.RequestMap
import com.athena.mis.application.entity.Role
import com.athena.mis.application.service.RequestMapService
import com.athena.mis.application.service.RoleService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by Asif on 12/28/2015.
 */

@TestFor(RequestMapController)

@Mock([
        RequestMap,
        RequestMapService,
        Role,
        RoleService,
        UpdateRequestMapActionService,
        ResetRequestMapByPluginIdActionService,
        ReloadRequestMapActionService,
        ListAvailableRoleActionService,
        ListAssignedRoleActionService,
        BaseService,
        AppSessionService
])

class RequestMapControllerSpec extends Specification{

    void setup(){
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.updateRequestMapActionService.appSessionService.setAppUser(appUser)
        controller.resetRequestMapByPluginIdActionService.appSessionService.setAppUser(appUser)
        controller.reloadRequestMapActionService.appSessionService.setAppUser(appUser)
        controller.listAvailableRoleActionService.appSessionService.setAppUser(appUser)
        controller.listAssignedRoleActionService.appSessionService.setAppUser(appUser)

    }

    def 'Show RequestMap' (){

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/requestMap/show'
    }

    def 'Update RequestMap'(){
        setup:

        Role role = new Role(
                version : 0,
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

        RequestMap rm = new RequestMap(
                version : 0,
                url : "/application/renderApplicationMenu",
                configAttribute : "ROLE_-2,ROLE_RESELLER,ROLE_-12_1,ROLE_-3_1",
                featureName : "Application Module",
                pluginId : 1,
                transactionCode : "APP-92",
                isViewable : false,
                isCommon : false
        )

        rm.id = 1
        rm.save(flush: true)

        controller.params.id = rm.id
        controller.params.version = 0
        controller.params.url = "/application/renderApplicationMenu"
        controller.params.configAttribute = "ROLE_-2,ROLE_RESELLER,ROLE_-12_1,ROLE_-3_1"
        controller.params.featureName = "Application Module"
        controller.params.pluginId = 1
        controller.params.transactionCode = "APP-92"
        controller.params.isViewable = false
        controller.params.isCommon = false
        controller.params.roleId = role.id
        controller.params.assignedFeatureIds = "1"

        when:
        request.method = 'POST'
        controller.update()
        then:
        response.redirectUrl == null
        response.json.message == "Request map has been updated successfully"
        response.json.isError == false
    }

    def 'List AvailableRole' () {
        setup:

        Role role = new Role(
                version : 0,
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

        RequestMap rm = new RequestMap(
                version : 0,
                url : "/application/renderApplicationMenu",
                configAttribute : "ROLE_-2,ROLE_RESELLER,ROLE_-12_1,ROLE_-3_1",
                featureName : "Application Module",
                pluginId : 1,
                transactionCode : "APP-92",
                isViewable : false,
                isCommon : false
        )

        rm.id = 1
        rm.save(flush: true)

        controller.params.roleId = role.id
        controller.params.pluginId = 1
        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'featureName'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listAvailableRole()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

    def 'List AssignedRole' () {
        setup:

        Role role = new Role(
                version : 0,
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

        controller.params.pluginId = 1
        controller.params.roleId =  role.id
        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'name'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.listAssignedRole()

        then:
        response.json.isError == false
        response.json.count >= 0
    }

//    def 'Reset Request Map'(){
//
//    }
//
//    def 'Reload Request Map'(){
//
//    }
}
