package com.athena.mis.application.actions.benchmark

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppSystemEntityCacheService
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Truncate Sampling domains
 *  For details go through Use-Case doc named 'TruncateSamplingActionService'
 */
class TruncateSamplingActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String RESERVED_ID = "reservedId"
    private static final String SAMPLING_DOMAIN_SMALL = "TRUNCATE sampling8f"
    private static final String SAMPLING_DOMAIN_MEDIUM = "TRUNCATE sampling25f"
    private static final String SAMPLING_DOMAIN_LARGE = "TRUNCATE sampling100f"
    private static final String SUCCESS_MSG = "Selected sampling domain has been truncated successfully"
    private static final String BENCHMARK_IN_PROGRESS_MSG = " benchmark(s) is in progress with volume type "

    AppSystemEntityCacheService appSystemEntityCacheService

    /**
     * 1. check required inputs
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        try {
            // check required parameter
            if (!params.reservedId || !params.dbInstanceId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long reservedId = Long.parseLong(params.reservedId.toString())
            long companyId = super.getCompanyId()
            SystemEntity benchmarkType = (SystemEntity) appSystemEntityCacheService.readByReservedId(reservedId, appSystemEntityCacheService.SYS_ENTITY_TYPE_BENCHMARK, companyId)
            if (!benchmarkType) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            int count = countCurrentExecutingBenchmark(benchmarkType.id)
            if (count > 0) {
                String msg = count + BENCHMARK_IN_PROGRESS_MSG + benchmarkType.key
                return super.setError(params, msg)
            }
            params.put(RESERVED_ID, new Long(reservedId))
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
            Long reservedId = (Long) result.get(RESERVED_ID)
            switch (reservedId.longValue()) {
                case appSystemEntityCacheService.SYS_ENTITY_BENCHMARK_SMALL:
                    executeSql(SAMPLING_DOMAIN_SMALL)
                    break
                case appSystemEntityCacheService.SYS_ENTITY_BENCHMARK_MEDIUM:
                    executeSql(SAMPLING_DOMAIN_MEDIUM)
                    break
                case appSystemEntityCacheService.SYS_ENTITY_BENCHMARK_LARGE:
                    executeSql(SAMPLING_DOMAIN_LARGE)
                    break
                default:
                    return super.setError(result, ERROR_FOR_INVALID_INPUT)
            }
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
        FROM benchmark
        WHERE start_time IS NOT NULL
        AND end_time IS NULL
        AND volume_type_id = :volumeTypeId
    """

    private int countCurrentExecutingBenchmark(long volumeTypeId) {
        Map queryParams = [
                volumeTypeId: volumeTypeId
        ]
        List<GroovyRowResult> lstResult = executeSelectSql(QUERY_STR, queryParams)
        int count = (int) lstResult[0].count
        return count
    }
}
