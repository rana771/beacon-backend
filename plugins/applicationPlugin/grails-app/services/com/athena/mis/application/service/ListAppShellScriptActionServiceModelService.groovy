package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppShellScriptActionServiceModel

class ListAppShellScriptActionServiceModelService extends BaseService {

    public ListAppShellScriptActionServiceModel read(long id) {
        return ListAppShellScriptActionServiceModel.read(id)
    }

    public void createDefaultSchema() {
        executeSql(ListAppShellScriptActionServiceModel.SQL_LIST_APP_SHELL_SCRIPT_MODEL)
    }
}
