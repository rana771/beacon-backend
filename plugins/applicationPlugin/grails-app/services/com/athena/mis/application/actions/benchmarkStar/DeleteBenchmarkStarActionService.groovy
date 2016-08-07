package com.athena.mis.application.actions.benchmarkStar

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.BenchmarkStar
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.BenchmarkStarService
import com.athena.mis.application.service.TransactionLogService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete benchmark object from DB
 *  For details go through Use-Case doc named 'DeleteBenchmarkStarActionService'
 */
class DeleteBenchmarkStarActionService extends BaseService implements ActionServiceIntf {

    BenchmarkStarService benchmarkStarService
    TransactionLogService transactionLogService
    AppSystemEntityCacheService appSystemEntityCacheService

    private static final String DELETE_SUCCESS_MSG = "Benchmark has been deleted successfully"
    private static final String ASSOCIATION_MSG = "Benchmark can't be deleted due to existence of its log"
    private static final String IN_PROGRESS = "This benchmark is in progress"
    private static final String BENCHMARK_NOT_FOUND = "Selected benchmark not found"
    private static final String BENCH_MARK = "benchmark"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check required parameter
     * 2. pull benchmark object from service
     * 3. check for benchmark object existence
     * 4. check if benchmark is in progress
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long benchmarkId = Long.parseLong(params.id.toString())
            BenchmarkStar benchmark = benchmarkStarService.read(benchmarkId)
            // check existence of Benchmark object
            if (!benchmark) {
                return super.setError(params, BENCHMARK_NOT_FOUND)
            }
            // check if benchmark is in progress
            if (benchmark.startTime && !benchmark.endTime) {
                return super.setError(params, IN_PROGRESS)
            }
            SystemEntity entityType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_TRANSACTION_LOG_BENCHMARK_STAR, appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG, benchmark.companyId)
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
     * 1. get the benchmark object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            BenchmarkStar benchmark = (BenchmarkStar) result.get(BENCH_MARK)
            benchmarkStarService.delete(benchmark)
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
        return super.setSuccess(result, DELETE_SUCCESS_MSG)
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
