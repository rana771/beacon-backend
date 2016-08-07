package com.athena.mis.application.actions.benchmarkStar

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.BenchmarkStar
import com.athena.mis.application.service.BenchmarkStarService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Stop execution for BenchmarkStar
 *  For details go through Use-Case dpl named 'StopBenchmarkStarActionService'
 */
class StopBenchmarkStarActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    BenchmarkStarService benchmarkStarService

    /**
     * 1. check required parameter
     * 2. pull BenchmarkStar object from service
     * @param params - serialize params from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameter
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long benchmarkId = Long.parseLong(params.id.toString())
            //pull benchmarkStar object from service
            BenchmarkStar benchmarkStar = benchmarkStarService.read(benchmarkId)
            if (!benchmarkStar) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. Set CONTINUE_EXECUTION = false in respective DataAdapter
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map execute(Map previousResult) {
        try {
            // set CONTINUE_EXECUTION = false
            benchmarkStarService.CONTINUE_EXECUTION = false
            return previousResult
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
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
     * Since there is no success message return the same map
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
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
