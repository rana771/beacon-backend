package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.DbInstanceQuery
import com.athena.mis.application.entity.SystemEntity
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger

class DbInstanceQueryService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    AppSystemEntityCacheService appSystemEntityCacheService
    AppDbInstanceService appDbInstanceService
    SystemEntityService systemEntityService

    @Override
    public void init() {
        domainClass = DbInstanceQuery.class
    }

    /**
     * Get count of DocOfflineDataFeedQuery by name
     *
     * @param name -name of DocOfflineDataFeedQuery
     * @return -an integer containing the value of count
     */
    public int countByNameIlikeAndDbInstanceId(String name, long dbInstanceId) {
        return DbInstanceQuery.countByNameIlikeAndDbInstanceId(name, dbInstanceId)
    }

    /**
     * Get count of DocOfflineDataFeedQuery by name
     *
     * @param name -name of DocOfflineDataFeedQuery
     * @return -an integer containing the value of count
     */
    public int countByNameIlikeAndDbInstanceIdAndIdNotEqual(String name, DbInstanceQuery dbInstanceQuery) {
        return DbInstanceQuery.countByNameIlikeAndDbInstanceIdAndIdNotEqual(name, dbInstanceQuery.dbInstanceId, dbInstanceQuery.id)
    }

    public int countByDbInstanceId(long dbInstanceId) {
        int count = DbInstanceQuery.countByDbInstanceId(dbInstanceId)
        return count
    }

    public List<GroovyRowResult> runQuery(DbInstanceQuery dbInstanceQuery) {
        List<GroovyRowResult> resultList = executeSelectSql(dbInstanceQuery.sqlQuery)
        return resultList
    }

    public List<DbInstanceQuery> findAllByDbInstanceIdAndQueryTypeId(long dbInstanceId, long queryTypeId) {
        return DbInstanceQuery.findAllByDbInstanceIdAndQueryTypeId(dbInstanceId, queryTypeId, [readOnly: true])
    }

    public List<DbInstanceQuery> findAllByQueryTypeIdAndDbInstanceIdNotEqualAndCompanyId(long queryTypeId, long dbInstanceId, long companyId) {
        return DbInstanceQuery.findAllByQueryTypeIdAndDbInstanceIdNotEqualAndCompanyId(queryTypeId, dbInstanceId, companyId, [readOnly: true])
    }

    private static final String SCHEMA_STATISTIC = "Table Schema Statistic"
    private static final String SCHEMA_STATISTIC_QUERY = """
        select "table", tbl_rows, diststyle, sortkey_num, encoded, skew_sortkey1, size, unsorted, sortkey1
        from svv_table_info
        order by 1;
    """
    private static final String MISSING_STATISTIC = "Identifying Tables with Missing Statistics"
    private static final String MISSING_STATISTIC_QUERY = """
        select substring(trim(plannode),1,100) as plannode, count(*)
        from stl_explain
        where plannode like '%missing statistics%'
        group by plannode
        order by 2 desc;
    """
    private static final String DATA_SKEW = "Identifying Tables with Data Skew or Unsorted Rows"
    private static final String DATA_SKEW_QUERY = """
        select trim(pgn.nspname) as schema,
        trim(a.name) as table, id as tableid,
        decode(pgc.reldiststyle,0, 'even',1,det.distkey ,8,'all') as distkey, dist_ratio.ratio::decimal(10,4) as skew,
        det.head_sort as "sortkey",
        det.n_sortkeys as "#sks", b.mbytes,
        decode(b.mbytes,0,0,((b.mbytes/part.total::decimal)*100)::decimal(5,2)) as pct_of_total,
        decode(det.max_enc,0,'n','y') as enc, a.rows,
        decode( det.n_sortkeys, 0, null, a.unsorted_rows ) as unsorted_rows ,
        decode( det.n_sortkeys, 0, null, decode( a.rows,0,0, (a.unsorted_rows::decimal(32)/a.rows)*100) )::decimal(5,2) as pct_unsorted
        from (select db_id, id, name, sum(rows) as rows,
        sum(rows)-sum(sorted_rows) as unsorted_rows
        from stv_tbl_perm a
        group by db_id, id, name) as a
        join pg_class as pgc on pgc.oid = a.id
        join pg_namespace as pgn on pgn.oid = pgc.relnamespace
        left outer join (select tbl, count(*) as mbytes
        from stv_blocklist group by tbl) b on a.id=b.tbl
        inner join (select attrelid,
        min(case attisdistkey when 't' then attname else null end) as "distkey",
        min(case attsortkeyord when 1 then attname  else null end ) as head_sort ,
        max(attsortkeyord) as n_sortkeys,
        max(attencodingtype) as max_enc
        from pg_attribute group by 1) as det
        on det.attrelid = a.id
        inner join ( select tbl, max(mbytes)::decimal(32)/min(mbytes) as ratio
        from (select tbl, trim(name) as name, slice, count(*) as mbytes
        from svv_diskusage group by tbl, name, slice )
        group by tbl, name ) as dist_ratio on a.id = dist_ratio.tbl
        join ( select sum(capacity) as  total
        from stv_partitions where part_begin=0 ) as part on 1=1
        where mbytes is not null
        order by  mbytes desc;
    """
    private static final String TUNING = "Identifying Queries That Are Top Candidates for Tuning"
    private static final String TUNING_QUERY = """
        select trim(database) as db, count(query) as n_qry,
        max(substring (qrytext,1,80)) as qrytext,
        min(run_minutes) as "min" ,
        max(run_minutes) as "max",
        avg(run_minutes) as "avg", sum(run_minutes) as total,
        max(query) as max_query_id,
        max(starttime)::date as last_run,
        sum(alerts) as alerts, aborted
        from (select userid, label, stl_query.query,
        trim(database) as database,
        trim(querytxt) as qrytext,
        md5(trim(querytxt)) as qry_md5,
        starttime, endtime,
        (datediff(seconds, starttime,endtime)::numeric(12,2))/60 as run_minutes,
        alrt.num_events as alerts, aborted
        from stl_query
        left outer join
        (select query, 1 as num_events from stl_alert_event_log group by query ) as alrt
        on alrt.query = stl_query.query
        where userid <> 1 and starttime >= dateadd(day, -7, current_date))
        group by database, label, qry_md5, aborted
        order by total desc limit 50;
    """
    private static final String REVIEW_QUEUE_WAIT_TIME = "Reviewing Queue Wait Times for Queries"
    private static final String REVIEW_QUEUE_WAIT_TIME_QUERY = """
        select trim(database) as DB , w.query,
        substring(q.querytxt, 1, 100) as querytxt,  w.queue_start_time,
        w.service_class as class, w.slot_count as slots,
        w.total_queue_time/1000000 as queue_seconds,
        w.total_exec_time/1000000 exec_seconds, (w.total_queue_time+w.total_Exec_time)/1000000 as total_seconds
        from stl_wlm_query w
        left join stl_query q on q.query = w.query and q.userid = w.userid
        where w.queue_start_Time >= dateadd(day, -7, current_Date)
        and w.total_queue_Time > 0  and w.userid >1
        and q.starttime >= dateadd(day, -7, current_Date)
        order by w.total_queue_time desc, w.queue_start_time desc limit 35;
    """

    private static final String SHOW_ALL_TABLE_WITHOUT_COUNT = "Show all table"

    private static final String SELECT_ALL_TABLE_WITHOUT_COUNT_QUERY = """
        select table_name from information_schema.tables
        where table_schema = 'public' and table_type = 'BASE TABLE' ORDER BY 1;
    """

    private static final String SHOW_ERROR_LOG = "Show error log"

    private static final String SHOW_ERROR_LOG_QUERY = """
        Select err_reason, raw_field_value, starttime, line_number, type, colname, raw_line, err_code, raw_line,
        filename from stl_load_errors  where filename like 's3://habi-data-feed/<OBJECT NAME>%'
    """

    private static final String DELETE_ALL = """
        DELETE FROM db_instance_query WHERE company_id = :companyId
    """

    /**
     * Delete all systemEntity by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    public boolean createDefaultData(long companyId, long systemUserId) {
        try {
            AppDbInstance dbInstance = appDbInstanceService.findByReservedVendorIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_VENDOR_AMAZON_REDSHIFT, companyId)
            SystemEntity queryType = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_QUERY_DIAGNOSTIC, companyId)
            if (dbInstance) {
                DbInstanceQuery schemaStatistic = new DbInstanceQuery(companyId: companyId, createdBy: systemUserId,
                        createdOn: new Date(), dbInstanceId: dbInstance.id, name: SCHEMA_STATISTIC, resultPerPage: 10,
                        sqlQuery: SCHEMA_STATISTIC_QUERY, updatedBy: 0, queryTypeId: queryType.id, isReserved: true)
                schemaStatistic.save()
                DbInstanceQuery missingStatistic = new DbInstanceQuery(companyId: companyId, createdBy: systemUserId,
                        createdOn: new Date(), dbInstanceId: dbInstance.id, name: MISSING_STATISTIC, resultPerPage: 10,
                        sqlQuery: MISSING_STATISTIC_QUERY, updatedBy: 0, queryTypeId: queryType.id, isReserved: true)
                missingStatistic.save()
                DbInstanceQuery dataSkew = new DbInstanceQuery(companyId: companyId, createdBy: systemUserId,
                        createdOn: new Date(), dbInstanceId: dbInstance.id, name: DATA_SKEW, resultPerPage: 10,
                        sqlQuery: DATA_SKEW_QUERY, updatedBy: 0, queryTypeId: queryType.id, isReserved: true)
                dataSkew.save()
                DbInstanceQuery tuning = new DbInstanceQuery(companyId: companyId, createdBy: systemUserId,
                        createdOn: new Date(), dbInstanceId: dbInstance.id, name: TUNING, resultPerPage: 10,
                        sqlQuery: TUNING_QUERY, updatedBy: 0, queryTypeId: queryType.id, isReserved: true)
                tuning.save()
                DbInstanceQuery reviewQueueWaitTime = new DbInstanceQuery(companyId: companyId, createdBy: systemUserId,
                        createdOn: new Date(), dbInstanceId: dbInstance.id, name: REVIEW_QUEUE_WAIT_TIME, resultPerPage: 10,
                        sqlQuery: REVIEW_QUEUE_WAIT_TIME_QUERY, updatedBy: 0, queryTypeId: queryType.id, isReserved: true)
                reviewQueueWaitTime.save()
                DbInstanceQuery showSchemaWithoutCount = new DbInstanceQuery(companyId: companyId, createdBy: systemUserId,
                        createdOn: new Date(), dbInstanceId: dbInstance.id, name: SHOW_ALL_TABLE_WITHOUT_COUNT, resultPerPage: 10,
                        sqlQuery: SELECT_ALL_TABLE_WITHOUT_COUNT_QUERY, updatedBy: 0, queryTypeId: queryType.id, isReserved: true)
                showSchemaWithoutCount.save()
                DbInstanceQuery showError = new DbInstanceQuery(companyId: companyId, createdBy: systemUserId,
                        createdOn: new Date(), dbInstanceId: dbInstance.id, name: SHOW_ERROR_LOG, resultPerPage: 10,
                        sqlQuery: SHOW_ERROR_LOG_QUERY, updatedBy: 0, queryTypeId: queryType.id, isReserved: true)
                showError.save()
            }
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index db_instance_query_name_db_instance_id_idx on db_instance_query(lower(name), db_instance_id);"
        executeSql(nameIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
