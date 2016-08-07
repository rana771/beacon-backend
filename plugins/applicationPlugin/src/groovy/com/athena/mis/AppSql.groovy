package com.athena.mis


class AppSql {
    String type         // sql or method
    long companyId      // 0L if common, company.id otherwise
    String query        // sql to execute
    boolean isExecutionDone // one time execution is done
    String methodName       // method to execute
}
