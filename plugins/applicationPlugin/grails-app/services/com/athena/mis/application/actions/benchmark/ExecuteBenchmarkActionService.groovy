package com.athena.mis.application.actions.benchmark

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Benchmark
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.TransactionLog
import com.athena.mis.application.service.*
import com.athena.mis.application.utility.TimeLogger
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

import static grails.async.Promises.task

/**
 *  Execute benchmark
 *  For details go through Use-Case doc named 'ExecuteBenchmarkActionService'
 */
class ExecuteBenchmarkActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())


    private static final String SEQUENCE = "sequence"
    private static final String BENCH_MARK = "benchmark"
    private static final String SAMPLING8F = "sampling8f"
    private static final String SAMPLING25F = "sampling25f"
    private static final String SAMPLING100F = "sampling100f"
    private static final String TOTAL_RECORD = "totalRecord"
    private static final String SUCCESS_MSG = "Benchmark has been started successfully."
    private static final String BENCHMARK_IN_PROGRESS_MSG = "Benchmark already is in progress."

    BenchmarkService benchmarkService
    Sampling8FService sampling8FService
    Sampling25FService sampling25FService
    Sampling100FService sampling100FService
    TransactionLogService transactionLogService
    SystemEntityService systemEntityService
    AppSystemEntityCacheService appSystemEntityCacheService

    /**
     * 1. check required parameters
     * 2. get Benchmark object
     * 3. check validation
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long benchmarkId = Long.parseLong(params.id)
            // get object of Benchmark
            Benchmark benchmark = benchmarkService.read(benchmarkId)
            // check validation
            String errMsg = checkValidation(benchmark)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            params.put(BENCH_MARK, benchmark)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive benchmark object from map
     * 2. update benchmark object into db with startTime and endTime
     * 3. start execution process using thread
     *
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map execute(Map result) {
        try {
            // receive benchmark object from map
            Benchmark benchmark = (Benchmark) result.get(BENCH_MARK)
            benchmark.startTime = new Date()
            benchmark.endTime = null
            // update benchmark object into db with startTime and endTime
            benchmarkService.update(benchmark)
            // start execution process using thread
            task { insertBenchmark(benchmark) }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. put success message
     *
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, SUCCESS_MSG)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. check existence of Benchmark object
     * 2. check if benchmark is in progress
     *
     * @param benchmark - an object of Benchmark
     * @return - error message or null value depending on validation check
     */
    private String checkValidation(Benchmark benchmark) {
        // check existence of Benchmark object
        String errMsg = isBenchMarkExist(benchmark)
        if (errMsg != null) return errMsg

        // check if benchmark is in progress
        errMsg = isBenchMarkInProgress(benchmark)
        if (errMsg != null) return errMsg

        return null
    }

    /**
     * 1. check existence of Benchmark object
     *
     * @param benchmark - an object of Benchmark
     * @return error message when error occurred or null
     */
    private String isBenchMarkExist(Benchmark benchmark) {
        // check existence of Benchmark object
        if (!benchmark) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

    /**
     * 1. check if benchmark is in progress
     *
     * @param benchmark - an object of Benchmark
     * @return error message when error occurred or null
     */
    private String isBenchMarkInProgress(Benchmark benchmark) {
        // check if benchmark is in progress
        if (benchmark.startTime && !benchmark.endTime) {
            return BENCHMARK_IN_PROGRESS_MSG
        }
        return null
    }

    /**
     * Start execution process using thread
     * 1. set continue execution true as default.
     * 2. get previous execution count form transaction log
     * 3. get sql to be executed by benchmark type
     * 4. get transaction log object for update
     * 5. Start: Batch DB insert
     * 6. update benchmark object after successfully execution with finish time
     *
     * @param benchmark - an object of Benchmark
     */
    private void insertBenchmark(Benchmark benchmark) {
        Date finishTime
        try {
            // set continue execution true as default.
            benchmarkService.CONTINUE_EXECUTION = true
            SystemEntity entityTypeBenchmark = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_TRANSACTION_LOG_BENCHMARK, benchmark.companyId)
            SystemEntity benchmarkType = systemEntityService.read(benchmark.volumeTypeId)
            // get previous execution count form transaction log
            Map result = transactionLogService.getTotalRecordToBeExecuted(benchmark, entityTypeBenchmark.id)
            long totalRecord = (Long) result.get(TOTAL_RECORD)
            long sequence = (Long) result.get(SEQUENCE)
            // sampling domain name by reserved id
            String samplingDomainName = getEntityName(benchmarkType.reservedId)

            long executeTime = 0
            long processingTime = 0
            sequence++

            TimeLogger writeLogger = new TimeLogger()
            TimeLogger processingLogger = new TimeLogger()

            long loop = totalRecord / benchmark.recordPerBatch
            long processStart = System.currentTimeMillis()

            for (long i = 1; i <= loop; i++) {
                // Start: object creation
                processingLogger.start()
                // get sql to be executed by benchmark type
                String sql = getSqlByBenchMarkType(benchmarkType, benchmark, processStart, i)
                // get transaction log object for update
                TransactionLog transactionLog = getTransactionLog(benchmark, sequence, samplingDomainName, entityTypeBenchmark.id, processingTime, executeTime)
                processingTime = processingLogger.calculate()
                // End: object creation

                // Start: Batch DB insert
                executeSampling(writeLogger, benchmark, benchmarkType, sql, transactionLog, processingTime, executeTime)
                sequence++

                if (!benchmarkService.CONTINUE_EXECUTION) break
            }
            finishTime = new Date()
            // update benchmark object after successfully execution with finish time
            benchmarkService.updateBenchMarkObject(benchmark.id, finishTime)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            finishTime = new Date()
            // update benchmark object after any error execution with finish time
            benchmarkService.updateBenchMarkObject(benchmark.id, finishTime)
        }
    }

    /**
     * 1. get sampling domain name by volume
     *
     * @param reservedId - an id of SystemEntity
     * @return samplingDomainName - a name of sampling domain
     */
    private String getEntityName(long reservedId) {
        String samplingDomainName = EMPTY_SPACE
        switch (reservedId) {
            case appSystemEntityCacheService.SYS_ENTITY_BENCHMARK_SMALL:
                samplingDomainName = SAMPLING8F
                break
            case appSystemEntityCacheService.SYS_ENTITY_BENCHMARK_MEDIUM:
                samplingDomainName = SAMPLING25F
                break
            case appSystemEntityCacheService.SYS_ENTITY_BENCHMARK_LARGE:
                samplingDomainName = SAMPLING100F
                break
            default: EMPTY_SPACE
        }
        return samplingDomainName
    }

    /**
     * 1. insert sql to sampling domain by corresponding service
     *
     * @param writeLogger - an object of  TimeLogger to calculate time
     * @param benchmark - an object of Benchmark
     * @param benchmarkType - an object of SystemEntity
     * @param sql - a string of sql to be executed to corresponding service
     * @param trLog - an object of TransactionLog
     * @param processingTime - a long value for processing time
     * @param executeTime - a long value to writing time
     * This function is in transactional boundary and will roll back in case of any exception
     */
    @Transactional
    private void executeSampling(TimeLogger writeLogger, Benchmark benchmark, SystemEntity benchmarkType, String sql, TransactionLog trLog, long processingTime, long executeTime) {
        writeLogger.start()
        if (!benchmark.isSimulation) {
            switch (benchmarkType.reservedId) {
                case appSystemEntityCacheService.SYS_ENTITY_BENCHMARK_SMALL:
                    // create sampling8f data by sql
                    sampling8FService.create(sql)
                    break
                case appSystemEntityCacheService.SYS_ENTITY_BENCHMARK_MEDIUM:
                    // create sampling25f data by sql
                    sampling25FService.create(sql)
                    break
                case appSystemEntityCacheService.SYS_ENTITY_BENCHMARK_LARGE:
                    // create sampling100f data by sql
                    sampling100FService.create(sql)
                    break
            }
        }
        executeTime = writeLogger.calculate()
        // End: Batch DB insert >> exception through

        trLog.timeToWrite = executeTime
        trLog.processingTime = processingTime
        // create transaction log to db
        transactionLogService.create(trLog)
    }

    /**
     * 1. get sql for execution by recored per batch
     *
     * @param benchmarkType - an object of SystemEntity
     * @param benchmark - an object of Benchmark
     * @param processStart
     * @param i
     * @return sql - an string sql for execution
     */
    private String getSqlByBenchMarkType(SystemEntity benchmarkType, Benchmark benchmark, long processStart, long i) {
        String sql = null
        switch (benchmarkType.reservedId) {
            case appSystemEntityCacheService.SYS_ENTITY_BENCHMARK_SMALL:
                sql = sampling8FService.getSampling8FSql(benchmark, (processStart + benchmark.recordPerBatch * i))
                break
            case appSystemEntityCacheService.SYS_ENTITY_BENCHMARK_MEDIUM:
                sql = sampling25FService.getSampling25FSql(benchmark, (processStart + benchmark.recordPerBatch * i))
                break
            case appSystemEntityCacheService.SYS_ENTITY_BENCHMARK_LARGE:
                sql = sampling100FService.getSampling100FSql(benchmark, (processStart + benchmark.recordPerBatch * i))
                break
        }
        return sql
    }

    /**
     * 1. get TransactionLog object for update
     *
     * @param benchmark - an object of Benchmark
     * @param sequence
     * @param samplingDomainName - a domain of sampling
     * @param entityTypeId
     * @param processingTime - a long value for processing time
     * @param executeTime - a long value to writing time
     * @return transactionLog - an updated TransactionLog object
     */
    private TransactionLog getTransactionLog(Benchmark benchmark, long sequence, String samplingDomainName, long entityTypeId, long processingTime, long executeTime) {
        TransactionLog transactionLog = new TransactionLog();

        transactionLog.sequence = sequence
        transactionLog.entityTypeId = entityTypeId
        transactionLog.entityId = benchmark.id
        transactionLog.totalRecord = sequence * benchmark.recordPerBatch
        transactionLog.recordPerBatch = benchmark.recordPerBatch
        transactionLog.timeToRead = 0L
        transactionLog.timeToWrite = executeTime
        transactionLog.processingTime = processingTime
        transactionLog.startTime = 0L
        transactionLog.endTime = 0L
        transactionLog.tableName = samplingDomainName
        transactionLog.companyId = benchmark.companyId
        transactionLog.createdOn = new Date()
        return transactionLog
    }

}
