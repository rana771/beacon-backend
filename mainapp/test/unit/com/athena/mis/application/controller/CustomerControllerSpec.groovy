package com.athena.mis.application.controller

import com.athena.mis.BaseService
import com.athena.mis.application.actions.appcustomer.CreateCustomerActionService
import com.athena.mis.application.actions.appcustomer.ListCustomerActionService
import com.athena.mis.application.actions.appcustomer.UpdateCustomerActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppCustomer
import com.athena.mis.application.service.AppCustomerService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification
/**
 * Created by Asif on 11/29/2015.
 */

@TestFor(AppCustomerController)
@Mock([AppCustomer, AppCustomerService, AppUser, CreateCustomerActionService,
        ListCustomerActionService, UpdateCustomerActionService, BaseService, AppSessionService] )

class CustomerControllerSpec extends Specification{

    void setup () {
        AppUser appUser = new AppUser()
        appUser.id = 1

        controller.createCustomerActionService.appSessionService.setAppUser(appUser)
        controller.updateCustomerActionService.appSessionService.setAppUser(appUser)
        controller.listCustomerActionService.appSessionService.setAppUser(appUser)
    }

    def 'Test Show Action' () {

        when:
        request.method = "POST"
        controller.show()

        then:
        view == '/application/appCustomer/show'
    }

    def 'Test Create Action' () {

        setup:
        controller.params.companyId = 1
        controller.params.fullName = "Abbas Uddin"
        controller.params.nickName = "Abbas"
        controller.params.dateOfBirth = "10/10/2015"
        controller.params.createdBy =  1
        controller.params.createdOn = new Date()
        controller.params.updatedBy =  1

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectUrl == null
        response.json.message == "Customer has been successfully saved"
        response.json.isError == false
    }

    def 'Test Update Action' () {

        setup:
        AppCustomer cust = new AppCustomer(id : 1, version: 0, companyId: 1, fullName: "TestName", nickName: "NickName",
                phoneNo:"01711114599", email:"info@athena.com.bd", dateOfBirth:new Date(), address: "Test Address",
                createdBy: 1, createdOn: new Date(), updatedBy: 1, updatedOn: new Date())

        cust.id = 100
        cust.save()

        controller.params.companyId = "1"
        controller.params.version = "0"
        controller.params.fullName = "Abbas Uddin"
        controller.params.nickName = "Abbas"
        controller.params.dateOfBirth = "10/10/2015"
        controller.params.createdBy =  cust.id
        controller.params.createdOn = new Date()
        controller.params.updatedBy =  cust.id
        controller.params.id = cust.id

        when:
        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Customer has been updated successfully"
        response.json.isError == false
    }

    def 'Test List Action' () {
        setup:
        AppCustomer cust = new AppCustomer(id: 1, version: 1, companyId: 1, fullName: "TestName", nickName: "NickName",
                phoneNo:"01711114599", email:"info@athena.com.bd", dateOfBirth:new Date(), address: "Test Address",
        createdBy: 1, createdOn: new Date(), updatedBy: 1, updatedOn: new Date())

        cust.save()

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
        response.json.count >=0
    }
}
