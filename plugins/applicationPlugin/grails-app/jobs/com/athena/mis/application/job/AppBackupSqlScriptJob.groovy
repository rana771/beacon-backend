package com.athena.mis.application.job

import com.athena.mis.BaseJobExecutor
import com.athena.mis.application.actions.job.AppBackupSqlScriptJobActionService

/**
 * Created by Mirza-Ehsan on 02-Feb-2015.
 */
class AppBackupSqlScriptJob extends BaseJobExecutor {

    AppBackupSqlScriptJobActionService appBackupSqlScriptJobActionService

    def execute(context) {
        Long companyId = (Long)context.mergedJobDataMap.get('companyId')
        Map parameters = [companyId: companyId]
        executeJob(appBackupSqlScriptJobActionService, parameters)
    }
}
