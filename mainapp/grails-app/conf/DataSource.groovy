dataSource {
    driverClassName = "org.postgresql.Driver"
    dialect = org.hibernate.dialect.PostgreSQL9Dialect
    pooled = true
}

hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'

    // newly added.
    format_sql = false
    use_sql_comments = false
}

// environment specific settings
environments {
    development {
        dataSource {
//            url = "jdbc:postgresql://192.168.1.155:5432/doc_qutub"
//            url = "jdbc:postgresql://192.168.1.155:5432/pt_qutub"
            url = "jdbc:postgresql://localhost:5432/beacon"
            username = "postgres"
            password = "123456"
//            password = "postgres"
            dbCreate = "update"
            logSql = true
            properties {
                jmxEnabled = true
                maxActive = 5
                maxIdle = 2
                minIdle = 2
                initialSize = 1
                minEvictableIdleTimeMillis=60000
                timeBetweenEvictionRunsMillis=60000
                maxWait = 10000
                maxAge = 10 * 60000
                numTestsPerEvictionRun=3
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=false
                ignoreExceptionOnPreLoad = true
                validationQuery="SELECT 1"
                validationQueryTimeout = 3
                jdbcInterceptors = "ConnectionState;StatementCache(max=200)"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED // safe default
                // controls for leaked connections
                abandonWhenPercentageFull = 100 // settings are active only when pool is full
                removeAbandonedTimeout = 120000
                removeAbandoned = true
                // use JMX console to change this setting at runtime
                logAbandoned = false // causes stacktrace recording overhead, use only for debugging
            }
        }
    }

    production {
        dataSource {
//            url = "jdbc:postgresql://192.168.1.155:5432/mis2"    //@todo: CHECK APP_CONFIG DATABASE
//            url = "jdbc:postgresql://192.168.1.155:5432/nahida_valuation"
            url = "jdbc:postgresql://192.168.1.155:5432/beacon"
            username = "postgres"
            password = ""
            dbCreate = "update"
            logSql = false
            properties {
                jmxEnabled = true
                maxActive = 10
                maxIdle = 5
                minIdle = 3
                initialSize = 3
                minEvictableIdleTimeMillis=60000
                timeBetweenEvictionRunsMillis=60000
                maxWait = 10000
                maxAge = 10 * 60000
                numTestsPerEvictionRun=3
                testOnBorrow=true
                testWhileIdle=true
                testOnReturn=false
                ignoreExceptionOnPreLoad = true
                validationQuery="SELECT 1"
                validationQueryTimeout = 3
                jdbcInterceptors = "ConnectionState;StatementCache(max=200)"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED // safe default
                // controls for leaked connections
                abandonWhenPercentageFull = 100 // settings are active only when pool is full
                removeAbandonedTimeout = 120000
                removeAbandoned = true
                // use JMX console to change this setting at runtime
                logAbandoned = false // causes stacktrace recording overhead, use only for debugging
            }
        }
    }
}
