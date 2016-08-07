package com.athena.mis.application.controller

import com.athena.mis.application.actions.appschedule.ListAppScheduleActionService
import com.athena.mis.application.actions.appschedule.ShowAppScheduleActionService
import com.athena.mis.application.actions.appschedule.TestExecuteAppScheduleActionService
import com.athena.mis.application.actions.appschedule.UpdateAppScheduleActionService

class AppScheduleController extends BaseController {

    static allowedMethods = [show: "POST", update: "POST", list: "POST", testExecute: "POST"]

    ShowAppScheduleActionService showAppScheduleActionService
    UpdateAppScheduleActionService updateAppScheduleActionService
    ListAppScheduleActionService listAppScheduleActionService
    TestExecuteAppScheduleActionService testExecuteAppScheduleActionService

    /**
     * Show AppSchedule
     */
    def show() {
        String view = '/application/appSchedule/show'
        renderView(showAppScheduleActionService, params, view)
    }

    /**
     * Update AppSchedule
     */
    def update() {
        renderOutput(updateAppScheduleActionService, params)
    }

    /**
     * Get list of AppSchedule
     */
    def list() {
        renderOutput(listAppScheduleActionService, params)
    }
    /**
     * Test AppSchedule
     */
    def testExecute() {
        renderOutput(testExecuteAppScheduleActionService, params)
    }
}
