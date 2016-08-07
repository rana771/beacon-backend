package com.athena.mis.application.actions.benchmark

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Benchmark
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.BenchmarkService
import com.athena.mis.application.service.TransactionLogService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete benchmark & remove from the grid
 *  For details go through Use-Case doc named 'DeleteBenchmarkActionService'
 */
class DeleteBenchmarkActionService extends BaseService implements ActionServiceIntf {

    BenchmarkService benchmarkService
    TransactionLogService transactionLogService
    AppSystemEntityCacheService appSystemEntityCacheService

    private static final String DELETE_SUCCESS_MSG = "Benchmark has been deleted successfully"
    private static final String ASSOCIATION_MSG = "Benchmark can't be deleted due to existence of its log"
    private static final String IN_PROGRESS = "This benchmark is in progress"
    private static final String BENCHMARK_NOT_FOUND = "Selected benchmark not found"
    private static final String BENCH_MARK = "benchmark"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. pull benchmark object
     * 2. check pre conditions
     * @param params -object receive from UI
     * @return -Map containing all objects
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long benchmarkId = Long.parseLong(params.id.toString())
            Benchmark benchmark = benchmarkService.read(benchmarkId)
            // check existence of Benchmark object
            if (!benchmark) {
                return super.setError(params, BENCHMARK_NOT_FOUND)
            }
            if (benchmark.startTime && !benchmark.endTime) {
                return super.setError(params, IN_PROGRESS)
            }
            SystemEntity entityType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_TRANSACTION_LOG_BENCHMARK, appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG, benchmark.companyId)
            int count = transactionLogService.countByCompanyIdAndEntityTypeIdAndEntityId(benchmark.companyId, entityType.id, benchmark.id)
            if (count > 0) {
                return super.setError(params, ASSOCIATION_MSG)
            }
            params.put(BENCH_MARK, benchmark)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Delete benchmark object from DB
     * This method is in transactional block and will roll back in case of any exception
     * @param result -map returned from executePreCondition method
     * @return -a map containing all objects
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Benchmark benchmark = (Benchmark) result.get(BENCH_MARK)
            benchmarkService.delete(benchmark)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * @param result - received from execute method
     * @return - same received param
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * set success message
     * @param result - received from post method
     * @return - same received param
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DELETE_SUCCESS_MSG)
    }

    /**
     * @param result - received from previous method
     * @return - same received param
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
