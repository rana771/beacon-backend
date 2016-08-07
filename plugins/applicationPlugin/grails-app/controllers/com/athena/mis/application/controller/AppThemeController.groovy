package com.athena.mis.application.controller

import com.athena.mis.application.actions.apptheme.ListAppThemeActionService
import com.athena.mis.application.actions.apptheme.ShowAppThemeActionService
import com.athena.mis.application.actions.apptheme.UpdateAppThemeActionService

class AppThemeController extends BaseController {

    static allowedMethods = [showTheme: "POST", updateTheme: "POST", listTheme: "POST"]

    ShowAppThemeActionService showAppThemeActionService
    UpdateAppThemeActionService updateAppThemeActionService
    ListAppThemeActionService listAppThemeActionService

    /**
     * Show theme
     */
    def showTheme() {
        String view = '/application/appTheme/show'
        renderView(showAppThemeActionService, params, view)
    }

    /**
     * Update theme
     */
    def updateTheme() {
        renderOutput(updateAppThemeActionService, params)
    }

    /**
     * Search and List theme
     */
    def listTheme() {
        renderOutput(listAppThemeActionService, params)
    }

    /**
     * reload theme
     */
    def reloadTheme() {
        render app.themeContent(params)
    }
}
