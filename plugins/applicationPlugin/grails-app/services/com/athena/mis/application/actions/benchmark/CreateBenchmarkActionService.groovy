package com.athena.mis.application.actions.benchmark

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Benchmark
import com.athena.mis.application.service.BenchmarkService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create benchmark & display in the grid
 *  For details go through Use-Case doc named 'CreateBenchmarkActionService'
 */
class CreateBenchmarkActionService extends BaseService implements ActionServiceIntf {

    BenchmarkService benchmarkService

    private static final String CREATE_SUCCESS_MSG = "Benchmark has been successfully saved"
    private static final String ALREADY_EXISTS_MSG = "This benchmark name already exists"
    private static final String BENCH_MARK = "benchmark"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. build benchmark object
     * @param params -object receive from UI
     * @return -Map containing all objects
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if(!params.name || !params.volumeTypeId){
              return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            int count = benchmarkService.countByNameIlikeAndCompanyId(params.name)
            if(count > 0){
                return super.setError(params, ALREADY_EXISTS_MSG)
            }
            Benchmark benchmark = buildBenchmarkObject(params)   // build benchmark object
            params.put(BENCH_MARK, benchmark)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Save benchmark object in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param result -map returned from executePreCondition method
     * @return -a map containing all objects
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Benchmark benchmark = (Benchmark) result.get(BENCH_MARK)
            benchmarkService.create(benchmark)  // save new benchmark object in DB
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
        return super.setSuccess(result, CREATE_SUCCESS_MSG)
    }
    /**
     * @param result - received from previous method
     * @return - same received param
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build benchmark object
     * @param parameterMap -serialized parameters from UI
     * @return -new benchmark object
     */
    private Benchmark buildBenchmarkObject(Map parameterMap) {
        Benchmark benchmark = new Benchmark(parameterMap)
        benchmark.companyId = super.companyId
        return benchmark
    }
}