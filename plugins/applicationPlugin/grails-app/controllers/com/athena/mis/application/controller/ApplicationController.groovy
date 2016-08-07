package com.athena.mis.application.controller

import com.athena.mis.PluginConnector

class ApplicationController {
    static allowedMethods = [renderApplicationMenu: "POST"]

    def springSecurityService

    def renderApplicationMenu() {
        if (!springSecurityService.isLoggedIn()) {
            render('false')
            return
        }
        render(contentType: "text/json") {
            lstTemplates = array {
                element([name: 'menu', content: g.render(plugin: 'applicationplugin', template: '/application/leftmenu')])
                element([name: 'dashBoard', content: g.render(plugin: 'applicationplugin', template: '/application/dashBoardApp')])
                element([name: 'copyright', content: PluginConnector.PLUGIN_ID])
            }
        }
    }
}
