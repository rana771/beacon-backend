package com.athena.mis.application.actions.benchmarkStar

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.BenchmarkStar
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.BenchmarkStarService
import com.athena.mis.utility.DateUtility
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger

/**
 *  Render to Benchmark report page with required values
 *  For details go through Use-Case doc named 'ShowBenchmarkStarReportActionService'
 */
class ShowBenchmarkStarReportActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    BenchmarkStarService benchmarkStarService
    AppMyFavouriteService appMyFavouriteService
    AppSystemEntityCacheService appSystemEntityCacheService

    private static final String BENCH_MARK = "benchmark"
    private static final String BENCH_MARK_DETAILS = "benchMarkDetails"
    private static final String X_STEP = "xStep"
    private static final String START_TIME = "startTime"
    private static final String END_TIME = "endTime"
    private static final String LEFT_MENU = "leftMenu"
    private static final String DURATION = "duration"
    private static final String NOT_FOUND = "BenchmarkStar not found"

    /**
     * @param params -object receive from UI
     * @return -Map containing all objects
     */
    public Map executePreCondition(Map params) {
        try {
            if (!params.oId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. build benchmark details object
     * @param result - received from pre execute method
     * @return - same received param
     */
    @Transactional
    public Map execute(Map result) {
        long benchmarkId = Long.parseLong(result.oId)
        BenchmarkStar benchmark = benchmarkStarService.read(benchmarkId)
        // check existence
        String errMsg = checkBenchMarkStarExistence(benchmark)
        if (errMsg) {
            appMyFavouriteService.setIsDirtyAndIsDefault(result)
            return super.setError(result, errMsg)
        }
        String startTime = DateUtility.formatDateTimeLongAmPm(benchmark.startTime)
        String endTime = DateUtility.formatDateTimeLongAmPm(benchmark.endTime)
        long duration = benchmark.getDuration()
        GroovyRowResult benchMarkDetails = getTimeForBenchMark(benchmarkId)
        long xStep = (long) benchMarkDetails.transaction_count
        if (xStep > 15L) {
            xStep = (long) Math.ceil(xStep / 15L)
        } else {
            xStep = 1L
        }
        benchMarkDetails.total_time = DateUtility.evaluateTimeSpanInMinSec(Double.parseDouble(benchMarkDetails.total_time.toString()))
        benchMarkDetails.total_read_time = DateUtility.evaluateTimeSpanInMinSec(Double.parseDouble(benchMarkDetails.total_read_time.toString()))
        benchMarkDetails.total_write_time = DateUtility.evaluateTimeSpanInMinSec(Double.parseDouble(benchMarkDetails.total_write_time.toString()))
        benchMarkDetails.total_processing_time = DateUtility.evaluateTimeSpanInMinSec(Double.parseDouble(benchMarkDetails.total_processing_time.toString()))

        benchMarkDetails.avg_read_time = DateUtility.evaluateTimeSpanInSec(Double.parseDouble(benchMarkDetails.avg_read_time.toString()))
        benchMarkDetails.avg_write_time = DateUtility.evaluateTimeSpanInSec(Double.parseDouble(benchMarkDetails.avg_write_time.toString()))
        benchMarkDetails.avg_processing_time = DateUtility.evaluateTimeSpanInSec(Double.parseDouble(benchMarkDetails.avg_processing_time.toString()))
        result.put(BENCH_MARK_DETAILS, benchMarkDetails)
        result.put(BENCH_MARK, benchmark)
        result.put(X_STEP, xStep)
        result.put(START_TIME, startTime)
        result.put(END_TIME, endTime)
        result.put(DURATION, duration)
        result.put(LEFT_MENU, result.leftMenu)
        return result
    }

    private static final String GET_SUM_QUERY = """
       SELECT
        string_agg(time_to_read::text, ',' ORDER BY sequence) read_time ,
        string_agg(time_to_write::text, ',' ORDER BY sequence) write_time,
        string_agg(processing_time::text, ',') processing_time,
        string_agg((b.record_per_batch*bt.sequence)::text, ',' ORDER BY sequence) span,
        SUM(time_to_read) AS total_read_time ,
        SUM(time_to_write) AS total_write_time ,
        SUM(processing_time) AS total_processing_time,
        (SUM(time_to_read)+ SUM(time_to_write)+ SUM(processing_time)) AS total_time,
        ROUND(AVG(time_to_read),0) AS avg_read_time,
        ROUND(AVG(time_to_write),0) AS avg_write_time,
        ROUND(AVG(processing_time),0) AS avg_processing_time,
        COUNT(bt.id) AS transaction_count
        FROM transaction_log bt
        LEFT JOIN benchmark b on b.id = bt.entity_id
        WHERE bt.entity_id=:benchmarkId
        AND bt.entity_type_id = :transactionLogTypeId
    """

    private GroovyRowResult getTimeForBenchMark(long benchmarkId) {
        SystemEntity transactionLogType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_TRANSACTION_LOG_BENCHMARK_STAR, appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG, super.companyId)
        Map queryParams = [benchmarkId: benchmarkId, transactionLogTypeId: transactionLogType.id]
        List<GroovyRowResult> total = executeSelectSql(GET_SUM_QUERY, queryParams)
        return total[0]
    }

    /**
     * @param result - received from execute method
     * @return - same received param
     */
    public Map executePostCondition(Map result) {
        return result
    }
    /**
     * @param result - received from post method
     * @return - same received param
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }
    /**
     * @param result - received from previous method
     * @return - same received param
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * check whether object exists or not
     * @param benchmark - BenchmarkStar object
     * @return - error message or null depending on validation
     */
    private String checkBenchMarkStarExistence(BenchmarkStar benchmark) {
        if (!benchmark) {
            return NOT_FOUND
        }
        return null
    }
}
