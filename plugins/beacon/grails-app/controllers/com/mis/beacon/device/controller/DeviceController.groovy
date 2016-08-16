package com.mis.beacon.device.controller

import com.athena.mis.application.controller.BaseController
import com.mis.beacon.device.device.CreateDeviceActionService
import com.mis.beacon.device.device.DeleteDeviceActionService
import com.mis.beacon.device.device.ListDeviceActionService
import com.mis.beacon.device.device.UpdateDeviceActionService

class DeviceController extends BaseController {

    CreateDeviceActionService createDeviceActionService
    UpdateDeviceActionService updateDeviceActionService
    ListDeviceActionService listDeviceActionService
    DeleteDeviceActionService deleteDeviceActionService

    static allowedMethods = [
            show: "POST", create: "POST", update: "POST", delete: "POST", list: "POST"
    ]

    /**
     * Show device UI
     */
    def show() {
        render(view: "/device/show", model: [oId: params.oId?params.oId:null])
    }

    /**
     * Create new device
     */
    def create() {
        renderOutput(createDeviceActionService, params)
    }

    /**
     * Update device
     */
    def update() {
        renderOutput(updateDeviceActionService, params)
    }

    /**
     * Delete device
     *
     */
    def delete() {
        renderOutput(deleteDeviceActionService, params)
    }

    /**
     * List & Search device
     */
    def list() {
        renderOutput(listDeviceActionService, params)
    }
}

