package com.athena.mis.application.controller

import com.athena.mis.application.actions.dbinstance.*

class AppDbInstanceController extends BaseController {

    static allowedMethods = [
            show                           : "POST",
            create                         : "POST",
            update                         : "POST",
            delete                         : "POST",
            list                           : "POST",
            showResult                     : "POST",
            testDBConnection               : "POST",
            listDbTable                    : "POST",
            dropDownTableColumnReload      : "POST",
            dropDownTableReload            : "POST",
            showForDplDashboard            : "POST",
            listVendorForDplDashboard      : "POST",
            listDbForDplDashboard: "POST",
            testDbForDplDashboard: "POST",
            showForSqlConsole              : "POST",
            listResultForSqlConsole        : "POST",
            createDbInstanceForDplDashboard: "POST",
            updateDbInstanceForDplDashboard: "POST",
            connect                        : "POST",
            disconnect                     : "POST",
            reconnect                      : "POST"
    ]

    CreateAppDbInstanceActionService createAppDbInstanceActionService
    UpdateAppDbInstanceActionService updateAppDbInstanceActionService
    DeleteAppDbInstanceActionService deleteAppDbInstanceActionService
    ListAppDbInstanceActionService listAppDbInstanceActionService
    ShowResultForDbInstanceActionService showResultForDbInstanceActionService
    TestDbInstanceConnectionActionService testDbInstanceConnectionActionService
    ListTableForDbInstanceActionService listTableForDbInstanceActionService
    ListAppDbInstanceForDashboardActionService listAppDbInstanceForDashboardActionService
    ListVendorForDplDashboardActionService listVendorForDplDashboardActionService
    ListDbForDplDashboardActionService listDbForDplDashboardActionService
    CreateAppDbInstanceForDplDashboardActionService createAppDbInstanceForDplDashboardActionService
    UpdateAppDbInstanceForDplDashboardActionService updateAppDbInstanceForDplDashboardActionService
    TestDbConnectionForDashboardActionService testDbConnectionForDashboardActionService
    ListResultForDplSqlConsoleActionService listResultForDplSqlConsoleActionService
    ConnectAppDbInstanceActionService connectAppDbInstanceActionService
    DisconnectAppDbInstanceActionService disconnectAppDbInstanceActionService
    ReconnectAppDbInstanceActionService reconnectAppDbInstanceActionService

    /**
     * Show DB Instance List
     */
    def show() {
        render(view: '/application/appDbInstance/show', model: [oId: params.oId ? params.oId : null, url: params.url ? params.url : null])
    }

    /**
     * Create A New DB Instance
     */
    def create() {
        renderOutput(createAppDbInstanceActionService, params)
    }

    /**
     * Update a DB Instance
     */
    def update() {
        renderOutput(updateAppDbInstanceActionService, params)
    }

    /**
     * Delete a DB Instance
     */
    def delete() {
        renderOutput(deleteAppDbInstanceActionService, params)
    }

    /**
     * List DB Instance
     */
    def list() {
        renderOutput(listAppDbInstanceActionService, params)
    }

    /**
     * List DB Instance
     */
    def listForDashboard() {
        renderOutput(listAppDbInstanceForDashboardActionService, params)
    }

    /**
     * Show sql query result from grid
     */
    def showResult() {
        String view = '/application/appDbInstance/showQueryResult'
        renderView(showResultForDbInstanceActionService, params, view)
    }

    def testDBConnection() {
        renderOutput(testDbInstanceConnectionActionService, params)
    }

    def listDbTable() {
        renderOutput(listTableForDbInstanceActionService, params)
    }

    def dropDownTableColumnReload() {
        render app.dropDownTableColumn(params)
    }

    def dropDownTableReload() {
        render app.dropDownTable(params)
    }

    def dropDownDbInstanceReload() {
        render app.dropDownAppDbInstance(params)
    }

    def showForDplDashboard() {
        render(view: '/dataPipeLine/dplDashboard/show')
    }

    def listVendorForDplDashboard() {
        renderOutput(listVendorForDplDashboardActionService, params)
    }

    def listDbForDplDashboard() {
        renderOutput(listDbForDplDashboardActionService, params)
    }

    def createDbInstanceForDplDashboard() {
        renderOutput(createAppDbInstanceForDplDashboardActionService, params)
    }

    def updateDbInstanceForDplDashboard() {
        renderOutput(updateAppDbInstanceForDplDashboardActionService, params)
    }

    def testDbForDplDashboard() {
        renderOutput(testDbConnectionForDashboardActionService, params)
    }

    def showForSqlConsole() {
        render(view: '/dataPipeLine/dplSqlConsole/show', model: [oId: params.oId ? params.oId : null, url: params.url ? params.url : null])
    }

    def listResultForSqlConsole() {
        renderOutput(listResultForDplSqlConsoleActionService, params)
    }

    def connect() {
        renderOutput(connectAppDbInstanceActionService, params)
    }

    def disconnect() {
        renderOutput(disconnectAppDbInstanceActionService, params)
    }

    def reconnect() {
        renderOutput(reconnectAppDbInstanceActionService, params)
    }
}
