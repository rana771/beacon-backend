package com.athena.mis.application.actions.dbinstancequery

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.dts.DataAdapterFactoryService
import com.athena.mis.application.dts.DataAdapterService
import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.DbInstanceQuery
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppAttachmentService
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.DbInstanceQueryService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService
import org.springframework.transaction.annotation.Transactional

class DownloadDbInstanceQueryActionService extends BaseService implements ActionServiceIntf {

    AppSystemEntityCacheService appSystemEntityCacheService
    JasperService jasperService
    DbInstanceQueryService dbInstanceQueryService
    AppAttachmentService appAttachmentService
    AppDbInstanceService appDbInstanceService
    DataAdapterFactoryService dataAdapterFactoryService
    AppConfigurationService appConfigurationService

    private Logger log = Logger.getLogger(getClass())

    private static final String QUERY_OBJ = "queryObj"
    private static final String QUERY_NOT_FOUND = "Query not found"
    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String QUERY_ID = 'id'
    private static final String QUERY_NAME = 'name'
    private static final String QUERY_SQL = 'sqlQuery'
    private static final String REPORT_FOLDER = 'dbInstanceQuery'
    private static final String OUTPUT_FILE_NAME = 'dbInstanceQuery'
    private static final String REPORT_TITLE = 'DbInstanceQuery_Report'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String SUBREPORT_DIR = 'SUBREPORT_DIR'
    private static final String JASPER_FILE_PDF = 'dbInstanceQuery.jasper'
    private static final String REPORT = "report"
    private static final String PDF_EXTENSION = ".pdf"
    private static final String DB_VENDOR = "dbVendor"
    private static final String DATA_BASE_NAME = "dataBaseName"
    private static final String ENTITY_TYPE_ID = "entityTypeId"
    private static final String SELECT = "select"

    /**
     * 1. check required parameters
     * 2. check existence of dbInstanceQuery object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.queryId) {
                return setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long queryId = Long.parseLong(params.queryId.toString())
            DbInstanceQuery dbInstanceQuery = dbInstanceQueryService.read(queryId)
            if (!dbInstanceQuery) {
                return setError(params, QUERY_NOT_FOUND)
            }
            params.put(QUERY_OBJ, dbInstanceQuery)
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
            DbInstanceQuery dbInstanceQuery = (DbInstanceQuery) result.get(QUERY_OBJ)
            Map report = getQueryReport(dbInstanceQuery)
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
     * @param dbInstanceQuery -object of dbInstanceQuery
     * @return -a map containing generated report
     */
    private Map getQueryReport(DbInstanceQuery dbInstanceQuery) {
        SystemEntity note = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_NOTE_DB_QUERY, appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, dbInstanceQuery.companyId)
        AppDbInstance appDbInstance = appDbInstanceService.read(dbInstanceQuery.dbInstanceId)
        if (dbInstanceQuery.sqlQuery.toLowerCase().startsWith(SELECT)) {
            Map sqlResult = getQueryResult(appDbInstance, dbInstanceQuery)
            List lstQuery = (List) sqlResult.lstResult
        }

        SystemEntity vendor = appSystemEntityCacheService.readByReservedId(appDbInstance.reservedVendorId, appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, appDbInstance.companyId)
        String reportDir = appConfigurationService.getAppReportDirectory() + File.separator + REPORT_FOLDER
        String subReportDir = reportDir + File.separator
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION

        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(COMMON_REPORT_DIR, appConfigurationService.getAppReportDirectory() + File.separator)
        reportParams.put(SUBREPORT_DIR, subReportDir)
        reportParams.put(ENTITY_TYPE_ID, note.id)
        reportParams.put(DB_VENDOR, vendor.key)
        reportParams.put(DATA_BASE_NAME, appDbInstance.dbName)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(QUERY_ID, dbInstanceQuery.id)
        reportParams.put(QUERY_NAME, dbInstanceQuery.name)
        reportParams.put(QUERY_SQL, dbInstanceQuery.sqlQuery)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(COMPANY_LOGO, appAttachmentService.getCompanyLogo())

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE_PDF, fileFormat: JasperExportFormat.PDF_FORMAT,
                reportData: lstQuery, parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }

    private Map getQueryResult(AppDbInstance appDbInstance, DbInstanceQuery dbInstanceQuery) {
        DataAdapterService dataAdapter = dataAdapterFactoryService.createAdapter(appDbInstance)
        Map result = dataAdapter.executeSelect(dbInstanceQuery.sqlQuery, true)
        return result
    }
}
