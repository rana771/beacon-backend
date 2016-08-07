package com.mis.beacon.controller

import com.athena.mis.integration.beacon.BeaconPluginConnector


class VsBeaconController {

    static allowedMethods = [renderBeaconMenu: "POST"]

    def renderBeaconMenu() {
        render(contentType: "text/json") {
            lstTemplates = array {
                element([name: 'menu', content: g.render(plugin: 'beacon', template: 'leftMenu')])
                element([name: 'dashBoard', content: g.render(plugin: 'beacon', template: 'dashBoardBeacon')])
                element([name: 'copyright', content: BeaconPluginConnector.PLUGIN_ID])
            }
        }
    }
}
