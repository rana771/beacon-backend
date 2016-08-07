package com.athena.mis.application.controller

/**
 * Created by Asif on 11/26/2015.
 */

import com.athena.mis.BaseService
import com.athena.mis.application.actions.vehicle.CreateVehicleActionService
import com.athena.mis.application.actions.vehicle.ListVehicleActionService
import com.athena.mis.application.actions.vehicle.UpdateVehicleActionService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Vehicle
import com.athena.mis.application.service.VehicleService
import com.athena.mis.application.session.AppSessionService
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * Created by nahida on 24-Nov-2015.
 */

@TestFor(VehicleController)
@Mock([
        Vehicle,
        AppUser,
        CreateVehicleActionService,
        UpdateVehicleActionService,
        ListVehicleActionService,
        VehicleService,
        BaseService,
        AppSessionService
])

class VehicleControllerSpec extends Specification{

    void setup(){

        AppUser appUser = new AppUser();
        appUser.id = 1;

        controller.createVehicleActionService.appSessionService.setAppUser(appUser)
        controller.updateVehicleActionService.appSessionService.setAppUser(appUser)
        controller.listVehicleActionService.appSessionService.setAppUser(appUser)

    }
    def "Test Show Action"() {

        setup:
        //controller.params.oId = oId

        when:
        request.method = "POST"
        controller.show()

        then:
        model.oId == null
        //model.oId

        //where:
        //oId = "ATHENA_ID"
    }

    def 'test create action'() {
        setup:
        controller.params.name = "TestVehicle"
        controller.params.description = "N/A"
        controller.params.companyId = 1

        when:
        request.method = 'POST'
        controller.create()

        then:
        response.redirectUrl == null
        response.json.message == "Vehicle has been saved successfully"
        response.json.isError == false
    }

    def 'test update action'() {
        setup:
        Vehicle vehicle = new Vehicle(version: 1, companyId: 1, createdBy: 1, createdOn: new Date(),
                description: 'N/A', name: "Insert for Update", updatedBy: 1, updatedOn: new Date() )
        vehicle.id = 2

        controller.params.id = vehicle.id
        controller.params.version = "0"
        controller.params.companyId = 1
        controller.params.description = "N/A"
        controller.params.name = "Updated After Insert"

        when:
        vehicle.save(flush: true, failOnError: true, validate: false)

        request.method = 'POST'
        controller.update()

        then:
        response.redirectUrl == null
        response.json.message == "Vehicle has been updated successfully"
        response.json.isError == false
    }

    def 'test list action'() {

        setup:
        Vehicle vehicle = new Vehicle(id: 1,  version: 1, companyId: 1, createdBy: 1, createdOn: new Date(),
                description: 'N/A', name: "Insert for Update", updatedBy: 1, updatedOn: new Date() )
        //vehicle.id = 1

        vehicle.save()

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
        response.json.count == 0
    }
}// endClass
