package com.athena.mis.application.actions.benchmarkStar

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.BenchmarkStar
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.entity.TransactionLog
import com.athena.mis.application.service.*
import com.athena.mis.application.utility.TimeLogger
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

import static grails.async.Promises.task

/**
 *  Execute benchmarkStar object
 *  For details go through Use-Case doc named 'ExecuteBenchmarkStarActionService'
 */
class ExecuteBenchmarkStarActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String COMA = ", "
    private static final String STR_SQL = "strSql"
    private static final String LST_IDS = "lstIds"
    private static final String SEQUENCE = "sequence"
    private static final String BENCH_MARK = "benchmark"
    private static final String TOTAL_RECORD = "totalRecord"
    private static final String SAMPLING_8 = "sampling8star"
    private static final String SAMPLING_25 = "sampling25star"
    private static final String SAMPLING_100 = "sampling100star"
    private static final String SUCCESS_MSG = "Benchmark has been started successfully."
    private static final String BENCHMARK_IN_PROGRESS_MSG = "Benchmark already is in progress."

    BenchmarkStarService benchmarkStarService
    Sampling8StarService sampling8StarService
    Sampling25StarService sampling25StarService
    Sampling100StarService sampling100StarService
    TransactionLogService transactionLogService
    SystemEntityService systemEntityService
    AppSystemEntityCacheService appSystemEntityCacheService

    /**
     * 1. check required parameters
     * 2. get object of BenchmarkStar from service
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
            long benchmarkId = Long.parseLong(params.id.toString())
            // get object of BenchmarkStar from service
            BenchmarkStar benchmarkStar = benchmarkStarService.read(benchmarkId)
            // check validation
            String errMsg = checkValidation(benchmarkStar)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            params.put(BENCH_MARK, benchmarkStar)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive benchmarkStar object from map
     * 2. update benchmark object into db with startTime and endTime
     * 3. start execution process using thread
     *
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map execute(Map result) {
        try {
            // receive benchmarkStar object from map
            BenchmarkStar benchmarkStar = (BenchmarkStar) result.get(BENCH_MARK)
            benchmarkStar.startTime = new Date()
            benchmarkStar.endTime = null
            // update benchmarkStar object into db with startTime and endTime
            benchmarkStarService.update(benchmarkStar)
            // start execution process using thread
            task { insertBenchmarkStar(benchmarkStar) }
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
     * 1. check existence of BenchmarkStar object
     * 2. check if BenchmarkStar is in progress
     *
     * @param benchmarkStar - an object of BenchmarkStar
     * @return - error message or null value depending on validation check
     */
    private String checkValidation(BenchmarkStar benchmarkStar) {
        // check existence of BenchmarkStar object
        String errMsg = isBenchmarkStarExits(benchmarkStar)
        if (errMsg != null) return errMsg

        // check if BenchmarkStar is in progress
        errMsg = isBenchMarkInProgress(benchmarkStar)
        if (errMsg != null) return errMsg

        return null
    }

    /**
     * 1. check existence of BenchmarkStar object
     *
     * @param benchmarkStar - an object of BenchmarkStar
     * @return error message when error occurred or null
     */
    private String isBenchmarkStarExits(BenchmarkStar benchmarkStar) {
        // check existence of BenchmarkStar object
        if (!benchmarkStar) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

    /**
     * 1. check if benchmarkStar is in progress
     *
     * @param benchmarkStar - an object of Benchmark
     * @return error message when error occurred or null
     */
    private String isBenchMarkInProgress(BenchmarkStar benchmarkStar) {
        // check if benchmarkStar is in progress
        if (benchmarkStar.startTime && !benchmarkStar.endTime) {
            return BENCHMARK_IN_PROGRESS_MSG
        }
        return null
    }

    /**
     * Start execution process using thread
     * 1. set continue execution true as default.
     * 2. get previous execution count form transaction log
     * 3. get sampling8Star, sampling25Star and sampling100Star sql from service to be executed
     * 4. get transaction log object for update
     * 5. Start: Batch DB insert
     * 6. update benchmarkStar object after successfully execution with finish time
     *
     * @param benchmarkStar - an object of BenchmarkStar
     */
    public void insertBenchmarkStar(BenchmarkStar benchmarkStar) {
        Date finishTime
        try {
            // set continue execution true as default.
            benchmarkStarService.CONTINUE_EXECUTION = true
            // check if Benchmark already has transaction data
            SystemEntity entityType = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_TRANSACTION_LOG_BENCHMARK_STAR, benchmarkStar.companyId)
            // get previous execution count form transaction log
            Map result = transactionLogService.getTotalRecordToBeExecuted(benchmarkStar, entityType.id)
            Long totalRecord = (Long) result.get(TOTAL_RECORD)
            Long sequence = (Long) result.get(SEQUENCE)
            String samplingDomainName = SAMPLING_8 + COMA + SAMPLING_25 + COMA + SAMPLING_100

            long executeTime = 0
            long processingTime = 0
            sequence++

            TimeLogger writeLogger = new TimeLogger()
            TimeLogger processingLogger = new TimeLogger()

            long loop = totalRecord / benchmarkStar.recordPerBatch
            long processStart = System.currentTimeMillis()

            for (long i = 1; i <= loop; i++) {
                // Start: object creation
                processingLogger.start()
                // get sampling8Star sql from service to be executed
                Map mapSampling8 = sampling8StarService.getSampling8StarSql(benchmarkStar, (processStart + benchmarkStar.recordPerBatch * i))
                String sql8 = (String) mapSampling8.get(STR_SQL)
                List<Long> lstSmallIds = (List<Long>) mapSampling8.get(LST_IDS)
                // get sampling25Star sql from service to be executed
                Map mapSampling25 = sampling25StarService.getSampling25StarSql(benchmarkStar, (processStart + benchmarkStar.recordPerBatch * i))
                String sql25 = (String) mapSampling25.get(STR_SQL)
                List<Long> lstMediumIds = (List<Long>) mapSampling25.get(LST_IDS)
                // get sampling100Star sql from service to be executed
                String sql100 = sampling100StarService.getSampling100StarSql(benchmarkStar, (processStart + benchmarkStar.recordPerBatch * i), lstSmallIds, lstMediumIds)
                // get transaction log object for update
                TransactionLog trLog = getTransactionLog(benchmarkStar, sequence, samplingDomainName, entityType.id, (benchmarkStar.recordPerBatch * 3), processingTime, executeTime)
                processingTime = processingLogger.calculate()
                // End: object creation

                // Start: Batch DB insert
                executeSamplingStar(writeLogger, benchmarkStar, sql8, sql25, sql100, trLog, processingTime, executeTime)
                sequence++

                if (!benchmarkStarService.CONTINUE_EXECUTION) break
            }
            finishTime = new Date()
            // update benchmarkStar object after successfully execution with finish time
            benchmarkStarService.updateBenchmarkStarObject(benchmarkStar.id, finishTime)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            finishTime = new Date()
            benchmarkStarService.updateBenchmarkStarObject(benchmarkStar.id, finishTime)
        }
    }

    /**
     * 1. insert sql to sampling star domain by corresponding service
     *
     * @param writeLogger - an object of  TimeLogger to calculate time
     * @param benchmarkStar - an object of BenchmarkStar
     * @param sql8 - a string of sql to be executed to corresponding service
     * @param sql25 - a string of sql to be executed to corresponding service
     * @param sql100 - a string of sql to be executed to corresponding service
     * @param trLog - an object of TransactionLog
     * @param processingTime - a long value for processing time
     * @param executeTime - a long value to writing time
     * This function is in transactional boundary and will roll back in case of any exception
     */
    @Transactional
    private void executeSamplingStar(TimeLogger writeLogger, BenchmarkStar benchmarkStar, String sql8, String sql25, String sql100, TransactionLog trLog, long processingTime, long executeTime) {
        writeLogger.start()
        if (!benchmarkStar.isSimulation) {
            // create sampling8Star data by sql
            sampling8StarService.create(sql8)
            // create sampling25Star data by sql
            sampling25StarService.create(sql25)
            // create sampling100Star data by sql
            sampling100StarService.create(sql100)
        }
        executeTime = writeLogger.calculate()
        // End: Batch DB insert

        trLog.timeToWrite = executeTime
        trLog.processingTime = processingTime
        // create transaction log to db
        transactionLogService.create(trLog)
    }

    /**
     * 1. get TransactionLog object for update
     *
     * @param benchmarkStar - an object of BenchmarkStar
     * @param sequence
     * @param samplingDomainName - a domain of sampling
     * @param entityTypeId
     * @param processingTime - a long value for processing time
     * @param executeTime - a long value to writing time
     * @return transactionLog - an updated TransactionLog object
     */
    private TransactionLog getTransactionLog(BenchmarkStar benchmarkStar, long sequence, String samplingDomainName, long entityTypeId, long recordPerBatch, long processingTime,
                                             long executeTime) {
        TransactionLog transferLog = new TransactionLog()

        transferLog.sequence = sequence
        transferLog.entityTypeId = entityTypeId
        transferLog.entityId = benchmarkStar.id
        transferLog.totalRecord = sequence * recordPerBatch
        transferLog.recordPerBatch = benchmarkStar.recordPerBatch
        transferLog.timeToRead = 0L
        transferLog.timeToWrite = executeTime
        transferLog.processingTime = processingTime
        transferLog.startTime = 0L
        transferLog.endTime = 0L
        transferLog.tableName = samplingDomainName
        transferLog.companyId = benchmarkStar.companyId
        transferLog.createdOn = new Date()
        return transferLog
    }

}
