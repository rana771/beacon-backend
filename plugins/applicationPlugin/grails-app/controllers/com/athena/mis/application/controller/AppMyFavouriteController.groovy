package com.athena.mis.application.controller

import com.athena.mis.application.actions.appmyfavourite.*

class AppMyFavouriteController extends BaseController {

    static allowedMethods = [
            create: "POST", list: "POST", delete: "POST", setAsDefault: "POST", setAsDirty: "POST", select: "POST"
    ]

    CreateAppMyFavouriteActionService createAppMyFavouriteActionService
    ListAppMyFavouriteActionService listAppMyFavouriteActionService
    DeleteAppMyFavouriteActionService deleteAppMyFavouriteActionService
    SetDefaultAppMyFavouriteActionService setDefaultAppMyFavouriteActionService
    SelectAppMyFavouriteActionService selectAppMyFavouriteActionService
    SetDirtyAppMyFavouriteActionService setDirtyAppMyFavouriteActionService

    def create() {
        renderOutput(createAppMyFavouriteActionService, params)
    }

    def list() {
        renderOutput(listAppMyFavouriteActionService, params)
    }

    def delete() {
        renderOutput(deleteAppMyFavouriteActionService, params)
    }

    def setAsDefault() {
        renderOutput(setDefaultAppMyFavouriteActionService, params)
    }

    def select() {
        renderOutput(selectAppMyFavouriteActionService, params)
    }

    def setAsDirty() {
        renderOutput(setDirtyAppMyFavouriteActionService, params)
    }
}
