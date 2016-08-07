package com.athena.mis.application.actions.benchmark

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Benchmark
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * List of Benchmark.
 * For details go through Use-Case doc named 'ListBenchmarkActionService'
 */
class ListBenchmarkActionService extends BaseService implements ActionServiceIntf{

    private Logger log = Logger.getLogger(getClass())
    /**
     * @param params - serialized parameters from UI
     * @return - same received param
     */
    public Map executePreCondition(Map params) {
        return params
    }
    /**
     * Get Benchmark list and count for grid
     * @param result - received from pre operation method
     * @return -a map containing list & count
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            Map resultMap = super.getSearchResult(result, Benchmark.class)
            result.put(LIST, resultMap.list)
            result.put(COUNT, resultMap.count)
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
     * @param result -received from post method
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
}
