package com.athena.mis.application.job

import com.athena.mis.BaseJobExecutor
import com.athena.mis.application.actions.job.AppBackupShellScriptJobActionService

/**
 * Created by Mirza-Ehsan on 02-Feb-2015.
 */
class AppBackupShellScriptJob extends BaseJobExecutor {
    AppBackupShellScriptJobActionService appBackupShellScriptJobActionService

    def execute(context) {
        Long companyId = (Long)context.mergedJobDataMap.get('companyId')
        Map parameters = [companyId: companyId]
        executeJob(appBackupShellScriptJobActionService, parameters)
    }
}
