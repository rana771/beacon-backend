package com.athena.mis.application.actions.report.userrole

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.config.AppConfigurationService
import com.athena.mis.application.service.AppAttachmentService
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.plugins.jasper.JasperExportFormat
import org.codehaus.groovy.grails.plugins.jasper.JasperReportDef
import org.codehaus.groovy.grails.plugins.jasper.JasperService

/**
 * Download user wise list of role report in pdf format
 * For details go through Use-Case doc named 'DownloadUserRoleReportActionService'
 */
class DownloadUserRoleReportActionService extends BaseService implements ActionServiceIntf {

    JasperService jasperService
    AppAttachmentService appAttachmentService
    AppConfigurationService appConfigurationService

    private Logger log = Logger.getLogger(getClass())

    private static final String REPORT_FILE_FORMAT = 'pdf'
    private static final String REPORT_NAME_FIELD = 'REPORT_NAME'
    private static final String REPORT_FOLDER = 'userRole'
    private static final String OUTPUT_FILE_NAME = 'user_role'
    private static final String REPORT_TITLE = 'user_role'
    private static final String REPORT_DIR = 'REPORT_DIR'
    private static final String JASPER_FILE_PDF = 'userRole.jasper'
    private static final String REPORT = "report"
    private static final String PDF_EXTENSION = ".pdf"
    private static final String COMPANY_ID = "companyId"

    /**
     * Do nothing for execute pre condition
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * Generate report
     * @param parameters -N/A
     * @param obj -N/A
     * @return -a map containing generated report and isError(true/false) depending on method success
     */
    public Map execute(Map result) {
        try {
            Map report = getUserRoleReport()
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
     * @return -a map containing generated report
     */
    private Map getUserRoleReport() {
        String reportDir = appConfigurationService.getAppReportDirectory() + File.separator + REPORT_FOLDER
        String outputFileName = OUTPUT_FILE_NAME + PDF_EXTENSION
        long companyId = getCompanyId()

        Map reportParams = new LinkedHashMap()
        reportParams.put(REPORT_DIR, reportDir)
        reportParams.put(COMMON_REPORT_DIR, appConfigurationService.getAppReportDirectory() + File.separator)
        reportParams.put(REPORT_NAME_FIELD, REPORT_TITLE)
        reportParams.put(COMPANY_LOGO, appAttachmentService.getCompanyLogo(companyId))
        reportParams.put(COMPANY_ID, companyId)

        JasperReportDef reportDef = new JasperReportDef(name: JASPER_FILE_PDF, fileFormat: JasperExportFormat.PDF_FORMAT,
                parameters: reportParams, folder: reportDir)

        ByteArrayOutputStream report = jasperService.generateReport(reportDef)
        return [report: report, reportFileName: outputFileName, format: REPORT_FILE_FORMAT]
    }
}
