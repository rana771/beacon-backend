package com.athena.mis.application.actions.benchmarkStar

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.service.AppDbInstanceService
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Truncate Sampling Star domains
 *  For details go through Use-Case doc named 'TruncateSamplingStarActionService'
 */
class TruncateSamplingStarActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SUCCESS_MSG = "SamplingStar domains has been truncated successfully"
    private static final String TRUNCATE_SQL = "TRUNCATE sampling100star,sampling25star,sampling8star;"
    private static final String BENCHMARK_IN_PROGRESS_MSG = " benchmark(s) is in progress"

    AppDbInstanceService appDbInstanceService
    DataAdapterFactoryService dataAdapterFactoryService

    /**
     * 1. check required inputs
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        try {
            int count = countCurrentExecutingBenchmark()
            if (count > 0) {
                String msg = count + BENCHMARK_IN_PROGRESS_MSG
                return super.setError(params, msg)
            }
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. truncate sampling domain
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            executeSql(TRUNCATE_SQL)
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

    private static final String QUERY_STR = """
        SELECT COUNT(id) AS count
        FROM benchmark_star
        WHERE start_time IS NOT NULL
        AND end_time IS NULL
    """

    private int countCurrentExecutingBenchmark() {
        List<GroovyRowResult> lstResult = executeSelectSql(QUERY_STR)
        int count = (int) lstResult[0].count
        return count
    }
}
