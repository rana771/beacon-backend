package com.athena.mis.application.job

import com.athena.mis.BaseJobExecutor
import com.athena.mis.application.actions.job.AppMaintenanceSqlScriptJobActionService

/**
 * Created by Mirza-Ehsan on 02-Feb-2015.
 */
class AppMaintenanceSqlScriptJob extends BaseJobExecutor {
    AppMaintenanceSqlScriptJobActionService appMaintenanceSqlScriptJobActionService

    def execute(context) {
        Long companyId = (Long)context.mergedJobDataMap.get('companyId')
        Map parameters = [companyId: companyId]
        executeJob(appMaintenanceSqlScriptJobActionService, parameters)
    }
}
