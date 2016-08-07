package com.athena.mis.application.actions.benchmark

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Benchmark
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.BenchmarkService
import com.athena.mis.application.service.TransactionLogService
import grails.transaction.Transactional
import org.apache.log4j.Logger

/**
 * Clear benchmark
 * For details go through Use-Case doc named 'ClearBenchmarkActionService'
 */
class ClearBenchmarkActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    BenchmarkService benchmarkService
    TransactionLogService transactionLogService
    AppSystemEntityCacheService appSystemEntityCacheService

    private static final String BENCH_MARK = "benchmark"
    private static final String CLEAN_UP_SUCCESS_MSG = "Benchmark has been cleaned successfully"
    private static final String IN_PROGRESS_MSG = "Benchmark is in progress"
    private static final String LOG_EMPTY_MSG = "Transaction log is empty"
    private static final String ENTITY_ID = 'entityId'
    private static final String ENTITY_TYPE_ID = 'entityTypeId'

    /**
     * 1. check required parameters
     * 2. get benchmark object by id from service
     * 3. check benchmark object existence
     * 4. check if benchmark is in progress
     * 5. check if benchmark has transaction log
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long benchmarkId = Long.parseLong(params.id)
            // check benchmark object existence
            Benchmark benchmark = benchmarkService.read(benchmarkId)
            if (!benchmark) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            // check if benchmark is in progress
            if (benchmark.startTime && !benchmark.endTime) {
                return super.setError(params, IN_PROGRESS_MSG)
            }
            // check if benchmark has transaction log
            SystemEntity entityType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_TRANSACTION_LOG_BENCHMARK, appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG, benchmark.companyId)
            int count = transactionLogService.countByCompanyIdAndEntityTypeIdAndEntityId(benchmark.companyId, entityType.id, benchmarkId)
            if (count == 0) {
                return super.setError(params, LOG_EMPTY_MSG)
            }
            params.put(BENCH_MARK, benchmark)
            params.put(ENTITY_ID, benchmarkId)
            params.put(ENTITY_TYPE_ID, entityType.id)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive benchMark object, entity and entity type from executePreCondition method
     * 2. clear corresponding logs
     * 3. update startTime and endTime of benchmark
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Benchmark benchMark = (Benchmark) result.get(BENCH_MARK)
            long entityTypeId = ((Long) result.get(ENTITY_TYPE_ID)).longValue()
            long entityId = ((Long) result.get(ENTITY_ID)).longValue()
            transactionLogService.deleteAllByCompanyAndEntityTypeAndEntity(super.getCompanyId(), entityTypeId, entityId)

            // Set Benchmark.startTime = null & Benchmark.endTime = null
            benchMark.startTime = null
            benchMark.endTime = null
            benchmarkService.update(benchMark)
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
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, CLEAN_UP_SUCCESS_MSG)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
