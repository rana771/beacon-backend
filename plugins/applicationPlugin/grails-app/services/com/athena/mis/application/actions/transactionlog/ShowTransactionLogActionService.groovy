package com.athena.mis.application.actions.transactionlog

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Benchmark
import com.athena.mis.application.entity.BenchmarkStar
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.BenchmarkService
import com.athena.mis.application.service.BenchmarkStarService
import grails.transaction.Transactional
import org.apache.log4j.Logger

/**
 *  Show UI for dplDataExport CRUD
 *  For details go through Use-Case doc named 'ShowTransactionLogActionService'
 */
class ShowTransactionLogActionService extends BaseService implements ActionServiceIntf {

    BenchmarkService benchmarkService
    BenchmarkStarService benchmarkStarService
    AppMyFavouriteService appMyFavouriteService
    AppSystemEntityCacheService appSystemEntityCacheService

    private Logger log = Logger.getLogger(getClass())

    private static final String ENTITY_ID = 'entityId'
    private static final String ENTITY_TYPE_ID = 'entityTypeId'
    private static final String TABLE_NAME = 'tableName'
    private static final String DATA_EXPORT_NAME = 'dataExportName'
    private static final String ENTITY_TYPE_NAME = 'entityTypeName'
    private static final String BENCHMARK = 'Benchmark'
    private static final String BENCHMARK_NOT_FOUND = 'Benchmark not found'
    private static final String BENCHMARK_STAR = 'Benchmark Star'
    private static final String BENCHMARK_STAR_NOT_FOUND = 'Benchmark Star not found'
    private static final String MENU_ID = 'menuId'
    private static final String LEFT_MENU = 'leftMenu'

    /**
     * 1. check required inputs
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        String entityIdStr = params.oId ? params.oId : params.pId
        // check required parameter
        if (!entityIdStr || !params.entityTypeId) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        params.put(ENTITY_ID, entityIdStr)
        return params
    }

    /**
     * 1. read docAnswer from service
     * 2. Check dplDataExport object existence
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     * */

    @Transactional
    public Map execute(Map result) {
        try {
            String entityIdStr = result.get(ENTITY_ID)
            String errMsg
            long entityId = Long.parseLong(entityIdStr)
            long entityTypeId = Long.parseLong(result.entityTypeId.toString())

            if (result.tableName) {
                result.put(TABLE_NAME, result.tableName)
            }

            if (result.dataExportName) {
                result.put(DATA_EXPORT_NAME, result.dataExportName)
            }

            SystemEntity transactionLogType = appSystemEntityCacheService.read(entityTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG, super.getCompanyId())
            if (transactionLogType) {
                result.put(ENTITY_TYPE_NAME, transactionLogType.key)
            }
            if (transactionLogType.key == BENCHMARK) {
                Benchmark benchmark = benchmarkService.read(entityId)
                errMsg = checkBenchMarkExistence(benchmark)
                if (errMsg) {
                    appMyFavouriteService.setIsDirtyAndIsDefault(result)
                    return super.setError(result, errMsg)
                }
                result.put(DATA_EXPORT_NAME, benchmark.name)
            } else if (transactionLogType.key == BENCHMARK_STAR) {
                BenchmarkStar benchmarkStar = benchmarkStarService.read(entityId)
                errMsg = checkBenchMarkStarExistence(benchmarkStar)
                if (errMsg) {
                    appMyFavouriteService.setIsDirtyAndIsDefault(result)
                    return super.setError(result, errMsg)
                }
                result.put(DATA_EXPORT_NAME, benchmarkStar.name)
            }
            Map loadLeftMenu = buildLeftMenu(result, transactionLogType.key)

            result.put(ENTITY_TYPE_ID, entityTypeId)
            result.put(ENTITY_ID, entityId)
            result.put(MENU_ID, loadLeftMenu.menuId)
            result.put(LEFT_MENU, loadLeftMenu.leftMenu)
            return result
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
     * For select there is no success message
     * since the input-parameter already have "isError:false", just return the same map
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param obj -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
    /**
     *
     * @param result
     * @param entityTypeName
     * @return
     */
    private Map buildLeftMenu(Map result, String entityTypeName) {
        String leftMenu = EMPTY_SPACE
        String menuId = EMPTY_SPACE

        if (result.isFileExport == 'true') {
            menuId = "MENU_ID_DATA_PIPE_LINE"
            leftMenu = "dplDataExport/show"
        } else if (result.isFileExport == 'false') {
            menuId = "MENU_ID_DATA_PIPE_LINE"
            leftMenu = "dplDataExport/show"
        } else if (result.isFileImport) {
            menuId = "MENU_ID_DATA_PIPE_LINE"
            leftMenu = "dplDataImport/show"
        }

        if (entityTypeName == BENCHMARK) {
            menuId = "MENU_ID_APPLICATION"
            leftMenu = "benchmark/show"
        } else if (entityTypeName == BENCHMARK_STAR) {
            menuId = "MENU_ID_APPLICATION"
            leftMenu = "benchmarkStar/show"
        }
        return [menuId: menuId, leftMenu: leftMenu]
    }

    /**
     * check whether benchmark object exists or not
     * @param benchmark - Benchmark object
     * @return - error message or null depending on validation
     */
    private String checkBenchMarkExistence(Benchmark benchmark) {
        if (!benchmark) {
            return BENCHMARK_NOT_FOUND
        }
        return null
    }

    /**
     * check whether object exists or not
     * @param benchmark - BenchmarkStar object
     * @return - error message or null depending on validation
     */
    private String checkBenchMarkStarExistence(BenchmarkStar benchmark) {
        if (!benchmark) {
            return BENCHMARK_STAR_NOT_FOUND
        }
        return null
    }
}
