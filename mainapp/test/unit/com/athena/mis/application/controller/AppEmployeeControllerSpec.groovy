package com.athena.mis.application.controller

/**
 * Created by Asif on 11/26/2015.
 */

import com.athena.mis.BaseService
import com.athena.mis.application.actions.appemployee.CreateAppEmployeeActionService
//import com.athena.mis.application.actions.employee.DeleteEmployeeActionService
import com.athena.mis.application.actions.appemployee.ListAppEmployeeActionService
import com.athena.mis.application.actions.appemployee.UpdateAppEmployeeActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppEmployee
import com.athena.mis.application.model.ListAppEmployeeActionServiceModel
import com.athena.mis.application.service.AppEmployeeService
import com.athena.mis.application.service.ListAppEmployeeActionServiceModelService
import com.athena.mis.application.session.AppSessionService

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by nahida on 25-Nov-2015.
 */

@TestFor(AppEmployeeController)

@Mock([
        AppEmployee,
        AppUser,
        CreateAppEmployeeActionService,
        ListAppEmployeeActionService,
        UpdateAppEmployeeActionService,
        AppEmployeeService,
        BaseService,
        ListAppEmployeeActionServiceModelService,
        ListAppEmployeeActionServiceModel,
        AppSessionService
])

class AppEmployeeControllerSpec extends Specification{

    void setup() {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.createAppEmployeeActionService.appSessionService.setAppUser(appUser)
        controller.updateAppEmployeeActionService.appSessionService.setAppUser(appUser)
        controller.listAppEmployeeActionService.appSessionService.setAppUser(appUser)
        //controller.deleteEmployeeActionService.appSessionService.setAppUser(appUser)
    }

    def 'test show action' () {

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/appEmployee/show'
    }

     def 'test create action' () {

         setup:
         controller.params.id = 1
         controller.params.address = "Test Address"
         controller.params.companyId = 1
         controller.params.dateOfBirth = "01/03/1978"
         controller.params.dateOfJoin = "01/03/1998"
         controller.params.designationId = 1
         controller.params.email = "info@athena.com.bd"
         controller.params.fullName = "Md. Masudul Islam"
         controller.params.mobileNo = "01711114599"
         controller.params.nickName = "Masud"

         when:
         request.method = 'POST'
         controller.create()

         then:
         response.redirectedUrl == null
         response.json.message == "Employee has been successfully saved"
         response.json.isError == false
     }

    def 'test update action' () {

        setup:
        AppEmployee emp = new AppEmployee(id: 1, version: 0, address: "Test Address", companyId:1, createdBy: 1, createdOn: new Date(),
        dateOfJoin: new Date(), designationId: 1, email: "info@athena.com.bd", fullName: "Md. Masudul Islam", mobileNo: "01711114599",
        nickName: "Masud", updatedBy: 1, updatedOn: new Date())
        emp.id=100
        emp.save(flush: true, failOnError: true, validate: false)

        controller.params.address = "Test Address after Insert"
        controller.params.companyId = 1
        controller.params.version="0"
        controller.params.dateOfBirth = "01/03/1979"
        controller.params.dateOfJoin = "11/11/2015"
        controller.params.designationId = 1
        controller.params.email = "info@athena.com.bd"
        controller.params.fullName = "Update Name after Insert"
        controller.params.mobileNo = "01716814851"
        controller.params.nickName = "Test Name"
        controller.params.id = emp.id

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Employee has been updated successfully"
        response.json.isError == false
    }

    def 'test list action' () {
        setup:
        AppEmployee emp = new AppEmployee(id: 1, version: 1, address: "Test Address", companyId:1, createdBy: 1, createdOn: new Date(),
                dateOfJoin: new Date(), designationId: 1, email: "info@athena.com.bd", fullName: "Md. Masudul Islam", mobileNo: "01711114599",
                nickName: "Masud", updatedBy: 1, updatedOn: new Date())

        emp.save()

        controller.params.page = 1
        controller.params.pageSize = 10
        controller.params."sort[0]['dir']" = 'asc'
        controller.params."sort[0]['field']" = 'fullName'
        controller.params.take = 10
        controller.params.skip = 0

        when:
        request.method = 'POST'
        controller.list()

        then:
        response.json.isError == false
        response.json.count >= 0
    }
}