package com.athena.mis.application.actions.report.benchmarkStar

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.entity.BenchmarkStar
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.BenchmarkStarService
import com.athena.mis.application.service.TransactionLogService
import com.athena.mis.utility.DateUtility
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.springframework.transaction.annotation.Transactional

class DownloadBenchmarkStarReportActionService extends BaseService implements ActionServiceIntf {

    JasperService jasperService
    BenchmarkStarService benchmarkStarService
    AppAttachmentService appAttachmentService
    TransactionLogService transactionLogService
    AppConfigurationService appConfigurationService
    AppSystemEntityCacheService appSystemEntityCacheService

    private Logger log = Logger.getLogger(getClass())

    private static final String BENCHMARK_OBJ = "benchmarkObj"
    private static final String BENCHMARK_ID = "benchmarkId"
    private static final String BENCHMARK_NAME = "benchmarkName"
    private static final String BENCHMARK_NOT_FOUND = "Benchmark not found"
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'benchmarkStar'
    private static final String OUTPUT_FILE_NAME = 'benchmark'
    private static final String REPORT_TITLE = 'Benchmark_Report'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE_PDF = 'benchmarkStar.jasper'
    private static final String REPORT = "report"
    private static final String PDF_EXTENSION = ".pdf"
    private static final String NOT_EXECUTED_MSG = "Benchmark is not executed yet"
    private static final String TOTAL_RECORD = "totalRecord"
    private static final String RECORD_PER_BATCH = "recordPerBatch"
    private static final String START_TIME = "startTime"
    private static final String END_TIME = "endTime"
    private static final String TOTAL_TIME = "totalTime"
    private static final String READ_TIME = "readTime"
    private static final String AVG_READ_TIME = "avgReadTime"
    private static final String PROCESSING_TIME = "processingTime"
    private static final String AVG_PROCESSING_TIME = "avgProcessingTime"
    private static final String WRITE_TIME = "writeTime"
    private static final String AVG_WRITE_TIME = "avgWriteTime"
    private static final String IS_SIMULATION = "isSimulation"
    private static final String YES = "Yes"
    private static final String NO = "No"
    private static final String TRANSACTION_LOG_TYPE = "transactionLogType"

    /**
     * 1. check required parameters
     * 2. check existence of Benchmark object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.benchmarkId) {
                return setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long benchmarkId = Long.parseLong(params.benchmarkId.toString())
            BenchmarkStar benchmark = benchmarkStarService.read(benchmarkId)
            // check existence of Benchmark object
            if (!benchmark) {
                return setError(params, BENCHMARK_NOT_FOUND)
            }
            SystemEntity entityType = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_TRANSACTION_LOG_BENCHMARK_STAR, appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG, benchmark.companyId)
            int count = transactionLogService.countByCompanyIdAndEntityTypeIdAndEntityId(benchmark.companyId, entityType.id, benchmark.id)
            if (count == 0) {
                return setError(params, NOT_EXECUTED_MSG)
            }
            params.put(BENCHMARK_OBJ, benchmark)
            params.put(TRANSACTION_LOG_TYPE, entityType)
            return params

        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Generate report
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing generated report and isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            BenchmarkStar benchmark = (BenchmarkStar) result.get(BENCHMARK_OBJ)
            SystemEntity transactionLogType = (SystemEntity) result.get(TRANSACTION_LOG_TYPE)
            Map report = getBenchmarkReport(benchmark, transactionLogType.id)
            result.put(REPORT, report)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Do nothing in post condition
     * @param result - A map returned by execute method
     * @return - returned the received map
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * do nothing for build success operation
     * @param result - A map returned by post condition method.
     * @return - returned the same received map containing isError = false
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Do nothing here
     * @param result - map returned from previous any of method
     * @return - a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Generate report
     * @param benchmark -object of Benchmark
     * @param entityTypeId -transaction log entity type id
     * @return -a map containing generated report
     */
    private Map getBenchmarkReport(BenchmarkStar benchmark, long entityTypeId) {
        String reportDir = appConfigurationService.getAppReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        GroovyRowResult result = getExecutionTime(benchmark.id, entityTypeId)

        String totalTime = DateUtility.evaluateTimeSpanInMinSec(Double.parseDouble(result.total_time.toString()))
        String processingTime = DateUtility.evaluateTimeSpanInMinSec(Double.parseDouble(result.processing_time.toString()))
        String readTime = DateUtility.evaluateTimeSpanInMinSec(Double.parseDouble(result.total_read_time.toString()))
        String writeTime = DateUtility.evaluateTimeSpanInMinSec(Double.parseDouble(result.total_write_time.toString()))

        String avgProcessingTime = DateUtility.evaluateTimeSpanInSec(Double.parseDouble(result.avg_processing_time.toString()))
        String avgReadTime = DateUtility.evaluateTimeSpanInSec(Double.parseDouble(result.avg_read_time.toString()))
        String avgWriteTime = DateUtility.evaluateTimeSpanInSec(Double.parseDouble(result.avg_write_time.toString()))

        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(COMMON_REPORT_DIR, appConfigurationService.getAppReportDirectory() + File.separator)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(BENCHMARK_ID, benchmark.id)
        reportParams.put(BENCHMARK_NAME, benchmark.name)
        reportParams.put(TOTAL_RECORD, benchmark.totalRecord * 3)
        reportParams.put(RECORD_PER_BATCH, benchmark.recordPerBatch * 3)
        reportParams.put(START_TIME, DateUtility.formatDateTimeLongAmPm(benchmark.startTime))
        reportParams.put(END_TIME, DateUtility.formatDateTimeLongAmPm(benchmark.endTime))
        reportParams.put(TOTAL_TIME, totalTime)
        reportParams.put(PROCESSING_TIME, processingTime)
        reportParams.put(AVG_PROCESSING_TIME, avgProcessingTime)
        reportParams.put(READ_TIME, readTime)
        reportParams.put(AVG_READ_TIME, avgReadTime)
        reportParams.put(WRITE_TIME, writeTime)
        reportParams.put(AVG_WRITE_TIME, avgWriteTime)
        reportParams.put(IS_SIMULATION, benchmark.isSimulation ? YES : NO)
        reportParams.put(TRANSACTION_LOG_TYPE, entityTypeId)
        reportParams.put(COMPANY_LOGO, appAttachmentService.getCompanyLogo())

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE_PDF, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

    private static final String QUERY_STR = """
        SELECT COALESCE((SUM(time_to_read) + SUM(time_to_write) + SUM(processing_time)),0) AS total_time,
        COALESCE(SUM(processing_time),0) AS processing_time, SUM(time_to_read) AS total_read_time,
        SUM(time_to_write) AS total_write_time, ROUND(AVG(processing_time),0) AS avg_processing_time,
        ROUND(AVG(time_to_read),0) AS avg_read_time, ROUND(AVG(time_to_write),0) AS avg_write_time
        FROM transaction_log
        WHERE entity_id =:benchmarkId
        AND entity_type_id = :transactionLogTypeId
    """

    private GroovyRowResult getExecutionTime(long benchmarkId, long entityTypeId) {
        Map queryParams = [
                benchmarkId: benchmarkId, transactionLogTypeId: entityTypeId
        ]
        List<GroovyRowResult> lstResult = executeSelectSql(QUERY_STR, queryParams)
        return lstResult[0]
    }
}
