package com.athena.mis.application.actions.benchmark

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Benchmark
import com.athena.mis.application.service.BenchmarkService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * Update benchmark.
 * For details go through Use-Case doc named 'UpdateBenchmarkActionService'
 */
class UpdateBenchmarkActionService extends BaseService implements ActionServiceIntf {

    private static final String UPDATE_SUCCESS_MESSAGE = "Benchmark has been updated successfully"
    private static final String ALREADY_EXISTS_MSG = "This benchmark name already exists"
    private static final String BENCH_MARK = "benchmark"
    private static final String OBJ_NOT_FOUND = "Selected benchmark not found"

    private Logger log = Logger.getLogger(getClass())

    BenchmarkService benchmarkService

    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameters
            if (!params.id || !params.version) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long benchmarkId = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())
            Benchmark oldBenchmark = benchmarkService.read(benchmarkId)
            if (!oldBenchmark || oldBenchmark.version != version) {  //check existence of benchmark object
                return super.setError(params, OBJ_NOT_FOUND)
            }
            int count = benchmarkService.countByNameIlikeAndCompanyIdAndIdNotEqual(params.name, benchmarkId)
            if(count > 0){
                return super.setError(params, ALREADY_EXISTS_MSG)
            }
            Benchmark benchmark = buildBenchmarkObject(params, oldBenchmark)
            params.put(BENCH_MARK, benchmark)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Update benchmark object in DB
     * This function is in transactional block and will roll back in case of any exception
     * @param result -map returned from executePreCondition method
     * @return -a map containing all objects
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Benchmark benchmark = (Benchmark) result.get(BENCH_MARK)
            benchmarkService.update(benchmark)  // update benchmark object in DB
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
     * set update success message
     * @param result - received from post method
     * @return - same received param
     */
    public Map buildSuccessResultForUI(Map result) {
        super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
    }

    /**
     * @param result - received from previous method
     * @return - same received param
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build benchmark object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldBenchmark -old oldBenchmark object
     * @return -updated benchmark object
     */
    private Benchmark buildBenchmarkObject(Map parameterMap, Benchmark oldBenchmark) {
        Benchmark benchmark = new Benchmark(parameterMap)
        oldBenchmark.name = benchmark.name
        oldBenchmark.volumeTypeId = benchmark.volumeTypeId
        oldBenchmark.totalRecord = benchmark.totalRecord
        oldBenchmark.recordPerBatch = benchmark.recordPerBatch
        oldBenchmark.isSimulation = benchmark.isSimulation
        return oldBenchmark
    }
}
