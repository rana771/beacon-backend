package com.athena.mis.application.actions.benchmarkStar

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.BenchmarkStar
import com.athena.mis.application.service.BenchmarkStarService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create benchmark & display in the grid
 *  For details go through Use-Case doc named 'CreateBenchmarkStarActionService'
 */
class CreateBenchmarkStarActionService extends BaseService implements ActionServiceIntf {

    BenchmarkStarService benchmarkStarService

    private static final String CREATE_SUCCESS_MSG = "Benchmark has been successfully saved"
    private static final String ALREADY_EXISTS_MSG = "This benchmark name already exists"
    private static final String BENCH_MARK = "benchmark"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check required parameters
     * 2. build benchmark object
     * 3. check for duplicate benchmark name
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.name) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // check for duplicate benchmark name
            int count = benchmarkStarService.countByNameIlikeAndCompanyId(params.name)
            if (count > 0) {
                return super.setError(params, ALREADY_EXISTS_MSG)
            }
            // build benchmark object
            BenchmarkStar benchmark = buildBenchmarkObject(params)
            params.put(BENCH_MARK, benchmark)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. receive benchmark object from executePreCondition method
     * 2. create new benchmark
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            BenchmarkStar benchmark = (BenchmarkStar) result.get(BENCH_MARK)
            benchmarkStarService.create(benchmark)  // save new benchmark object in DB
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
        return super.setSuccess(result, CREATE_SUCCESS_MSG)
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
     * Build benchmark object
     * @param parameterMap - serialized parameters from UI
     * @return - new benchmark object
     */
    private BenchmarkStar buildBenchmarkObject(Map parameterMap) {
        BenchmarkStar benchmark = new BenchmarkStar(parameterMap)
        benchmark.companyId = super.companyId
        return benchmark
    }
}
