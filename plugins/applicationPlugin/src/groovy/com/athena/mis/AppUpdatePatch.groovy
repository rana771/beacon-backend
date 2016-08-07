package com.athena.mis

import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.apache.log4j.Logger

import javax.sql.DataSource

abstract class AppUpdatePatch {

    private Logger log = Logger.getLogger(getClass())

    DataSource dataSource
    public List<AppSql> lstAppSql = []

    public static final int RELEASE_SPAN = 90
    public static final String TYPE_SQL = "sql"
    public static final String TYPE_METHOD = "method"
    public static final String IS_ERROR = "isError"

    public abstract void init();

    public abstract Date getReleaseDate();

    public void execute(long companyId) {
        try {
            for (int i = 0; i < lstAppSql.size(); i++) {
                AppSql appSql = lstAppSql[i]
                if (appSql.isExecutionDone) continue
                if ((appSql.companyId == 0L) || (appSql.companyId == companyId)) {
                    if (appSql.type.equals(TYPE_METHOD)) {
                        "$appSql.methodName"()
                    } else {
                        executeSql(appSql.query)
                    }
                    appSql.isExecutionDone = true
                }
            }
        } catch (Exception e) {
            println 'UPDATE PATCH EXCEPTION: ' + e
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    public boolean executeSql(String query) {
        Sql sql = new Sql(dataSource)
        consolePrint(query)
        return sql.execute(query)
    }

    public List<GroovyRowResult> executeSelectSql(String query) {
        Sql sql = new Sql(dataSource)
        consolePrint(query)
        return sql.rows(query)
    }

    private static final String EXECUTING_SQL = " SQL : "

    protected void consolePrint(String content) {
        println "${EXECUTING_SQL}${content}"
    }
}
