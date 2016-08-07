package com.athena.mis.application.actions.benchmarkStar

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.BenchmarkStar
import com.athena.mis.application.service.BenchmarkStarService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update benchmark object and show in grid
 *  For details go through Use-Case doc named 'UpdateBenchmarkStarActionService'
 */
class UpdateBenchmarkStarActionService extends BaseService implements ActionServiceIntf {

    private static final String UPDATE_SUCCESS_MESSAGE = "Benchmark has been updated successfully"
    private static final String ALREADY_EXISTS_MSG = "This benchmark name already exists"
    private static final String BENCH_MARK = "benchmark"
    private static final String OBJ_NOT_FOUND = "Selected benchmark is not found"

    private Logger log = Logger.getLogger(getClass())

    BenchmarkStarService benchmarkStarService

    /**
     * 1. check required parameters
     * 2. pull benchmark object
     * 3. check benchmark object existence
     * 4. duplicate check for benchmark name
     * 5. build benchmark object for update
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.id || !params.version) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long benchmarkId = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())
            BenchmarkStar oldBenchmark = benchmarkStarService.read(benchmarkId)
            // check existence of benchmark object
            if (!oldBenchmark || oldBenchmark.version != version) {
                return super.setError(params, OBJ_NOT_FOUND)
            }
            int count = benchmarkStarService.countByNameIlikeAndCompanyIdAndIdNotEqual(params.name, benchmarkId)
            if (count > 0) {
                return super.setError(params, ALREADY_EXISTS_MSG)
            }
            BenchmarkStar benchmark = buildBenchmarkObject(params, oldBenchmark)
            params.put(BENCH_MARK, benchmark)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. get the benchmark object
     * 2. Update existing benchmark in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            BenchmarkStar benchmark = (BenchmarkStar) result.get(BENCH_MARK)
            benchmarkStarService.update(benchmark)  // update benchmark object in DB
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
        super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
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
     * Build benchmark object for update
     * @param parameterMap - serialized parameters from UI
     * @param oldBenchmark - old oldBenchmark object
     * @return - updated benchmark object
     */
    private BenchmarkStar buildBenchmarkObject(Map parameterMap, BenchmarkStar oldBenchmark) {
        BenchmarkStar benchmark = new BenchmarkStar(parameterMap)
        oldBenchmark.name = benchmark.name
        oldBenchmark.totalRecord = benchmark.totalRecord
        oldBenchmark.recordPerBatch = benchmark.recordPerBatch
        oldBenchmark.isSimulation = benchmark.isSimulation
        return oldBenchmark
    }
}
