package com.athena.mis.application.controller

import com.athena.mis.application.actions.vehicle.CreateVehicleActionService
import com.athena.mis.application.actions.vehicle.DeleteVehicleActionService
import com.athena.mis.application.actions.vehicle.ListVehicleActionService
import com.athena.mis.application.actions.vehicle.UpdateVehicleActionService

class VehicleController extends BaseController {
    static allowedMethods = [show: "POST", create: "POST", update: "POST", delete: "POST"];

    CreateVehicleActionService createVehicleActionService
    UpdateVehicleActionService updateVehicleActionService
    DeleteVehicleActionService deleteVehicleActionService
    ListVehicleActionService listVehicleActionService

    /**
     *  Show vehicle
     */
    def show() {

        render(view: '/application/vehicle/show')
    }

    /**
     *  Create vehicle
     */
    def create() {
        renderOutput(createVehicleActionService, params)
    }

    /**
     *  Update vehicle
     */
    def update() {
        renderOutput(updateVehicleActionService, params)
    }

    /**
     *  Delete vehicle
     */
    def delete() {
        renderOutput(deleteVehicleActionService, params)
    }

    /**
     *  List & Search vehicle
     */
    def list() {
        renderOutput(listVehicleActionService, params)
    }
}
