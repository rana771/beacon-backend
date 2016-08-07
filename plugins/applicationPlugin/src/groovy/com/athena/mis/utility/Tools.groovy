package com.athena.mis.utility

import com.athena.mis.application.entity.AppUser
import org.codehaus.groovy.grails.commons.GrailsApplication

class Tools {
//    public static final int PLEASE_SELECT_VALUE = -1
//    public static final String PLZ_SELECT_VALUE = '-1'
//    public static final String PLEASE_SELECT_LEVEL = 'Please Select...'  // listForKendoDropdown
//    public static final String DASH = "_"
//    public static final String dd_MM_yyyy_SLASH = "dd/MM/yyyy"

//    public static final String PAGE = "page"
//    public static final String TOTAL = "total"
//    public static final String ROWS = "rows"
    public static final String ID = "id"    // listForKendoDropdown
//    public static final String COUNT = "count"
//    public static final String LIST = "list"
//    public static final String FROM = "From "
//    public static final String TO = " To "
//    public static final String IN = "IN"
//    public static final String PER = " per "
//    public static final String PNG_EXTENSION = ".png"
//    public static final def EMAIL_PATTERN = /[-0-9a-zA-Z.+_]+@[-0-9a-zA-Z.+_]+\.[a-zA-Z]{2,4}/ // isValidEmail
//    public static final String PLUS = "+"
//    public static final String MINUS = "-"
//    public static final String PERCENTAGE = "%" // eliminate uses from domains
//    public static final String PIPE = "|"
    public static final String COMA = "," // buildCommaSeparatedStringOfIds
//    public static final String SINGLE_QUOTE = "'"
//    public static final String GRATER_SIGN = ">"
//    public static final String EMPTY_SPACE_COMA = " , "
    public static final String EMPTY_SPACE = '' // uses in Tools only
    public static final String SINGLE_SPACE = ' ' // uses in Tools only
//    public static final String PARENTHESIS_START = " ( "
//    public static final String PARENTHESIS_END = " ) "
//    public static final String THIRD_BRACKET_START = "["
//    public static final String THIRD_BRACKET_END = "]"
//    public static final String SEMICOLON = ";"
    public static final String COLON = ":" // getFullUrl
//    public static final String SINGLE_DOT = "."
    public static final String THREE_DOTS = "..." // makeDetailsShort
//    public static final String QUESTION_SIGN = "?"
//    public static final String BR = "<br>"
    public static final String UNDERSCORE = "_" // getIdsFromParams
//    public static final String HYPHEN = "-"
//    public static final String SLASH = "/"
//    public static final String STR_ZERO = "0"
//    public static final String STR_ZERO_DECIMAL = "0.00"
//    public static final String NOT_APPLICABLE = "N/A"
//    public static final String NOT_GIVEN = "Not Given"
//    public static final String NONE = "None"
//    public static final String LABEL_NEW = "New"    // used in place of grid object id
//    public static final String HAS_ACCESS = "hasAccess"
//    public static final String IS_VALID = "isValid"
//    public static final String IS_ERROR = 'isError' // eliminate uses
//    public static final String IS_APP_EXCEPTION = 'isAppException'
//    public static final String MESSAGE = 'message' // AppJdbcConnection
//    public static final String DELETED = "deleted"
//    public static final String MAIL_SENDING_ERR_MSG = 'mailSendingErrMsg'
//    public static final String ENTITY = 'entity'
//    public static final String GRID_ENTITY = 'gridEntity'
//    public static final String VERSION = 'version'
//    public static final String HAS_ASSOCIATION = "hasAssociation"
//    public static final String COMPANY_LOGO = "companyLogo"
//    public static final String YES = "YES"
//    public static final String TRUE = 'true'
//    public static final String FALSE = 'false'
//    public static final String NO = "NO"
//    public static final String ALL = "ALL"
//    public static final String ERROR_FOR_INVALID_INPUT = "Error occurred for invalid inputs"
//    public static final String EXCEPTION = "exception";
    // Date range of searching in Filter panels
    private static String REPORT_DIRECTORY = null;
    private static String REPORT_DIRECTORY_ACC = null;
    private static String REPORT_DIRECTORY_BUDGET = null;
    private static String REPORT_DIRECTORY_FIXED_ASSET = null;
    private static String REPORT_DIRECTORY_PROJECT_TRACK = null;
    private static String REPORT_DIRECTORY_EXCHANGE_HOUSE = null;
    private static String REPORT_DIRECTORY_DOCUMENT = null;
    private static String REPORT_DIRECTORY_INV = null;
    private static String REPORT_DIRECTORY_PROC = null;
    private static String REPORT_DIRECTORY_QS = null;
    private static String REPORT_DIRECTORY_ARMS = null;
    private static String REPORT_DIRECTORY_SARB = null;

//    public static final String COMMON_REPORT_DIR = 'COMMON_REPORT_DIR'

//    public static
//    final String BACK_DATED_INVENTORY_TRANSACTION_DELETE_PROHIBITED = "Back dated inventory transaction cannot be deleted, Please contact with administrator.";
//    public static
//    final String BACK_DATED_INVENTORY_TRANSACTION_UPDATE_PROHIBITED = "Back dated inventory transaction cannot be edited, Please contact with administrator.";
//
//    private static final String REGEX_SYMBOL_START = "\\Q"
//    private static final String REGEX_SYMBOL_END = "\\E"
//    public static final String NEW_LINE = "\r\n"

    //VAT and AIT for project status report
//    public static final float VAT = 0.05 // 5%
//    public static final float AIT = 0.055 // 5.5%

    //download file extension
//    public static final String PDF_EXTENSION = ".pdf"
//    public static final String XLS_EXTENSION = ".xls"
//    public static final String CSV_EXTENSION = ".csv"
//    public static final String TEXT_EXTENSION = ".txt"
//    public static final String XML_EXTENSION = ".xml"
//    public static final String FORMAT_TYPE_NAME_PDF = "pdf"
//    public static final String FORMAT_TYPE_NAME_XLS = "xls"
//    public static final String FORMAT_TYPE_NAME_CSV = "csv"
    public static final String SYS_CONFIG_NOT_FOUND_MESSAGE = "System config message not found for:"

//    public static final String SPAN_RED_COLOR_START = "<span style=color:red>"
//    public static final String SPAN_RED_COLOR_END = "</span>"

/*    public static final String escapeForRegularExpression(String str) {
        if (str == null) return null
        String newStr = REGEX_SYMBOL_START + str + REGEX_SYMBOL_END
        return newStr
    }*/

    private static GrailsApplication getGrailsApp() {
        def grailsApplication = new AppUser().domainClass.grailsApplication
        return grailsApplication
    }

    public static final String getAccountingReportDirectory() {
        if (!REPORT_DIRECTORY_ACC) {
            String pathDir = grailsApp.config.application.plugins.accounting.directory + "/reports/accounting";
            File reportFolder = grailsApp.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_ACC = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_ACC;
    }

    public static final String getBudgetReportDirectory() {
        if (!REPORT_DIRECTORY_BUDGET) {
            String pathDir = grailsApp.config.application.plugins.budget.directory + "/reports/budget";
            File reportFolder = grailsApp.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_BUDGET = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_BUDGET;
    }

    public static final String getInventoryReportDirectory() {
        if (!REPORT_DIRECTORY_INV) {
            String pathDir = grailsApp.config.application.plugins.inventory.directory + "/reports/inventory";
            File reportFolder = grailsApp.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_INV = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_INV;
    }

    public static final String getProcurementReportDirectory() {
        if (!REPORT_DIRECTORY_PROC) {
            String pathDir = grailsApp.config.application.plugins.procurement.directory + "/reports/procurement";
            File reportFolder = grailsApp.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_PROC = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_PROC;
    }

    public static final String getQsReportDirectory() {
        if (!REPORT_DIRECTORY_QS) {
            String pathDir = grailsApp.config.application.plugins.qs.directory + "/reports/qs";
            File reportFolder = grailsApp.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_QS = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_QS;
    }

    public static final String getFixedAssetReportDirectory() {
        if (!REPORT_DIRECTORY_FIXED_ASSET) {
            String pathDir = grailsApp.config.application.plugins.fixedasset.directory + "/reports/fixedasset";
            File reportFolder = grailsApp.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_FIXED_ASSET = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_FIXED_ASSET;
    }

    public static final String getExchangeHouseReportDirectory() {
        if (!REPORT_DIRECTORY_EXCHANGE_HOUSE) {
            String pathDir = grailsApp.config.application.plugins.exchangehouse.directory + "/reports/exchangehouse";
            File reportFolder = grailsApp.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_EXCHANGE_HOUSE = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_EXCHANGE_HOUSE;
    }

    public static final String getDocumentReportDirectory() {
        if (!REPORT_DIRECTORY_DOCUMENT) {
            String pathDir = grailsApp.config.application.plugins.document.directory + "/reports/document";
            File reportFolder = grailsApp.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_DOCUMENT = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_DOCUMENT;
    }

    public static final String getDataPipeLineReportDirectory() {
        if (!REPORT_DIRECTORY_DOCUMENT) {
            String pathDir = grailsApp.config.application.plugins.datapipeline.directory + "/reports/datapipeline";
            File reportFolder = grailsApp.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_DOCUMENT = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_DOCUMENT;
    }

    public static final String getCommonReportDirectory() {
        if (!REPORT_DIRECTORY) {
            String pathDir = grailsApp.config.application.plugins.applicationplugin.directory + "/reports";
            File reportFolder = grailsApp.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY;
    }

    public static final String getCommonFooterDirectory() {
        if (!REPORT_DIRECTORY) {
            String pathDir = grailsApp.config.application.plugins.applicationplugin.directory + "/reports";
            File reportFolder = grailsApp.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY;
    }

    public static final String getProjectTrackReportDirectory() {
        if (!REPORT_DIRECTORY_PROJECT_TRACK) {
            String pathDir = grailsApp.config.application.plugins.projecttrack.directory + "/reports/projectTrack";
            File reportFolder = grailsApp.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_PROJECT_TRACK = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_PROJECT_TRACK;
    }

    public static final String getArmsReportDirectory() {
        if (!REPORT_DIRECTORY_ARMS) {
            String pathDir = grailsApp.config.application.plugins.arms.directory + "/reports/rms";
            File reportFolder = grailsApp.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_ARMS = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_ARMS;
    }

    public static final String getSarbReportDirectory() {
        if (!REPORT_DIRECTORY_SARB) {
            String pathDir = grailsApp.config.application.plugins.sarb.directory + "/reports/sarb";
            File reportFolder = grailsApp.parentContext.getResource(pathDir).file;
            REPORT_DIRECTORY_SARB = reportFolder.absolutePath;
        }
        return REPORT_DIRECTORY_SARB;
    }

    /*public static final String buildCommaSeparatedStringOfIds(List ids) {
        String strIds = EMPTY_SPACE
        for (int i = 0; i < ids.size(); i++) {
            strIds = strIds + ids[i]
            if ((i + 1) < ids.size()) strIds = strIds + COMA
        }
        return strIds
    }*/

    // Build common source dropDown for UI only with Name and ID
    /*public static final List buildSourceDropDown(List lst, String key) {
        List lstDropDown = []
        if ((lst == null) || (lst.size() <= 0))
            return lstDropDown
        for (int i = 0; i < lst.size(); i++) {
            Object obj = lst[i]
            lstDropDown << [id: obj.id, name: obj.getAt(key)]
        }
        return lstDropDown
    }*/

    public static final List<Long> getIds(List lstObjects) {
        List<Long> lstIds = []
        if ((lstObjects == null) || (lstObjects.size() <= 0))
            return lstIds
        for (int i = 0; i < lstObjects.size(); i++) {
            lstIds << (Long) lstObjects[i].id
        }
        return lstIds
    }

    /**
     * Get List of long ids from UI parameter
     * @param params -GrailsParameterMap containing underscore separated string of ids
     * @param key -key to get ids from GrailsParameterMap
     * @return -list of long ids
     */
    /*public static final List<Long> getIdsFromParams(Map params, String key) {
        List<Long> lstIds = []
        String str = (String) params.get(key)
        List lstStringIds = str.split(UNDERSCORE)
        for (int i = 0; i < lstStringIds.size(); i++) {
            lstIds << Long.parseLong(lstStringIds[i].toString())
        }
        return lstIds
    }*/

    // Following method receive a list of objects and return List of corresponding long IDs
//    private static final String STR_KEY_ID = 'id'

    /*public static final List buildListOfIds(List lstMain) {
        List<Long> lstIds = []
        for (int i = 0; i < lstMain.size(); i++) {
            Long id = (Long) lstMain[i].properties.get(STR_KEY_ID)
            lstIds << id
        }
        return lstIds
    }*/

//    public static final int DEFAULT_LENGTH_DETAILS_OF_BUDGET = 29;
//    public static final int DEFAULT_LENGTH_DETAILS_OF_BUDGET_FOR_REPORT = 60;
//    public static final int DEFAULT_LENGTH_DETAILS_OF_SYS_CONFIG = 100;
//    public static final int DEFAULT_LENGTH_DETAILS_OF_AREA_DES = 100;
//    public static final int DEFAULT_LENGTH_DETAILS_OF_SMS_BODY = 35;
//    public static final int DEFAULT_LENGTH_DETAILS_OF_SMS_DES = 50;
//    public static final int DEFAULT_LENGTH_DETAILS_OF_INDENT = 90;


    /*public static final String makeDetailsShort(String details, int length) {
        if (!details) return EMPTY_SPACE
        if (details.length() > length) {
            details = details.substring(0, length)
            details = details + THREE_DOTS
        }
        return details
    }*/

//    private static final String AMOUNT_FORMAT = "৳ ##,##,##0.00"  // makeAmountWithThousandSeparator
//    private static final String AMOUNT_FORMAT_WITHOUT_CURRENCY = "##,##,##0.00" // formatAmountWithoutCurrency
//    public static final String AMOUNT_FORMAT_CSV = "#0.00"
//    private static final String QUANTITY_FORMAT = "##,##,##0.0000"

    // used to pull formatted amount/quantity from DB
//    public static final String DB_CURRENCY_FORMAT = "FM৳ 99,99,999,99,99,990.0099"
//    public static final String DB_CURRENCY_FORMAT_CSV = "FM99999999999990.00"
//    public static final String DB_QUANTITY_FORMAT = "FM99,99,999,99,99,990.0099"
//    public static final String DB_QUANTITY_FORMAT_CSV = "FM99999999999990.0099"

    /*public static final String makeAmountWithThousandSeparator(double amount) {
        DecimalFormat myFormatter = new DecimalFormat(AMOUNT_FORMAT);
        String output = myFormatter.format(amount);
        return output
    }*/

    /*public static final String formatAmountWithoutCurrency(double amount) {
        DecimalFormat myFormatter = new DecimalFormat(AMOUNT_FORMAT_WITHOUT_CURRENCY);
        String output = myFormatter.format(amount);
        return output
    }*/

    /*public static final String formatQuantity(double amount) {
        DecimalFormat myFormatter = new DecimalFormat(QUANTITY_FORMAT);
        String output = myFormatter.format(amount);
        return output
    }

    public static final String makeAmountWithThousandSeparator(BigDecimal amount) {
        DecimalFormat myFormatter = new DecimalFormat(AMOUNT_FORMAT);
        String output = myFormatter.format(amount);
        return output
    }*/

    //return get file download file extension
    /*public static final String getFileExtension(String formatType) {
        String outputFileExtension
        switch (formatType) {
            case FORMAT_TYPE_NAME_PDF: outputFileExtension = PDF_EXTENSION
                break
            case FORMAT_TYPE_NAME_XLS: outputFileExtension = XLS_EXTENSION
                break
            case FORMAT_TYPE_NAME_CSV: outputFileExtension = CSV_EXTENSION
                break
            default: outputFileExtension = PDF_EXTENSION
        }
        return outputFileExtension

    }
    //return get  download file type
    public static final JasperExportFormat getFileType(String formatType) {
        JasperExportFormat jasperExportFormat

        switch (formatType) {
            case FORMAT_TYPE_NAME_PDF: jasperExportFormat = JasperExportFormat.PDF_FORMAT
                break
            case FORMAT_TYPE_NAME_XLS: jasperExportFormat = JasperExportFormat.XLS_FORMAT
                break
            case FORMAT_TYPE_NAME_CSV: jasperExportFormat = JasperExportFormat.CSV_FORMAT
                break
            default: jasperExportFormat = JasperExportFormat.PDF_FORMAT
        }
        return jasperExportFormat
    }*/

    /*public static final String BOLD_TAG_START = "<b>"
    public static final String BOLD_TAG_END = "</b>"

    public static String makeBold(String str) {
        return BOLD_TAG_START + str + BOLD_TAG_END
    }*/

    /*// Method checks if rate/quantity changed in transaction details (requires for summary update decision)
    public static final boolean isRateOrQuantityChanged(def oldTransactionDetails, def newTransactionDetails) {
        if (oldTransactionDetails.rate != newTransactionDetails.rate) {
            return true
        }
        if (oldTransactionDetails.actualQuantity != newTransactionDetails.actualQuantity) {
            return true
        }
        return false
    }*/

    /*public static final long parseLongInput(String strValue) {
        try {
            long value = Long.parseLong(strValue)
            return value
        } catch (Exception ignored) {
            return 0L
        }
    }

    public static final double parseDoubleInput(String strValue) {
        try {
            double value = Double.parseDouble(strValue)
            return value
        } catch (ignored) {
            return 0.0d
        }
    }

    public static final int parseIntInput(String strValue) {
        try {
            int value = Integer.parseInt(strValue)
            return value
        } catch (ignored) {
            return 0
        }
    }*/

    /**
     * Following method adds the Unselected Object with proper key sets for kendo datasource
     * @param lst -the main list with objects
     * @param textMember - optional value, default is 'name'
     * @return - the main List with unselected value added in first index
     */
    /*private static final String NAME = 'name'

    public static final List listForKendoDropdown(List lst, String textMember, String unselectedText) {
        List lstReturn = []
        Map unseledtedVal = new LinkedHashMap()
        String txtMember = textMember ? textMember : NAME
        String unselectedTxt = unselectedText ? unselectedText : PLEASE_SELECT_LEVEL
        if (lst.size() == 0) {
            unseledtedVal.put(ID, EMPTY_SPACE)
            unseledtedVal.put(txtMember, unselectedTxt)
            lstReturn.add(0, unseledtedVal)
            return lstReturn
        }
        // List is not empty. iterate through each key (except id & textMember)
        // Put these keys (if any) inside unselectedVal (with empty string) for consistency
        Map<String, Object> firstObj
        Object tmp = lst[0]     // pick the first element, assuming all are same
        if (tmp instanceof Map) {
            firstObj = (Map<String, Object>) tmp // groovyRowResult
        } else {
            firstObj = (Map<String, Object>) tmp.properties   // Domains
        }

        for (String key : firstObj.keySet()) {
            unseledtedVal.put(key, EMPTY_SPACE)
        }
        unseledtedVal.put(ID, EMPTY_SPACE)
        unseledtedVal.put(txtMember, unselectedTxt)
        lstReturn.add(0, unseledtedVal)
        lstReturn = lstReturn.plus(1, lst)
        // append the original list (& return a new list in case cache utility object)
        return lstReturn
    }*/

    /*//Domain
    public static final String DOMAIN_EXCHANGEHOUSE = "Exchange House"
    public static final String DOMAIN_BANK_BRANCH = "Branch" // "BankBranch"
    public static final String DOMAIN_BENEFICIARY = "Beneficiary"
    public static final String DOMAIN_TASK = "Task"
    public static final String DOMAIN_TASK_TRACE = "Task Trace"
    public static final String DOMAIN_CUSTOMER = "Customer"
    public static final String DOMAIN_COUNTRY = "Country"
    public static final String DOMAIN_CURRENCY_CONVERSION = "Currency Conversion"
    public static final String DOMAIN_SARB_CURRENCY_CONVERSION = "Sarb Currency Conversion"
    public static final String DOMAIN_COMPANY = "Company"
    public static final String DOMAIN_AGENT = "Agent"
    public static final String DOMAIN_AGENT_CURRENCY_POSTING = "Agent Currency Posting"
    private static final String HAS = " has "
    private static final String ASSOCIATED = " associated "
    private static final String S_PLURAL = "(s)"*/
    // write a message to get associative information
    /*public static final String getMessageOfAssociation(String name, int count, String domainName) {
        return name + HAS + count + ASSOCIATED + domainName + S_PLURAL

    }*/

//    public static final char ACTION_CREATE = 'C';
//    public static final char ACTION_UPDATE = 'U';
//    public static final char ACTION_DELETE = 'D';
    // Customer Address Verification Status
//    public static final int CUSTOMER_ADDRESS_VERIFIED = 1
//    public static final int CUSTOMER_ADDRESS_NOT_VERIFIED = 0

//    public static final double TASK_REGULAR_FEE = 0.00
//    public static final double MOBILE_PAY_AMOUNT_LIMIT = 20000;
//    public static final int EXH_DEFAULT_DATE_RANGE = 7;
//    public static final int EXH_DEFAULT_DATE_RANGE_FOR_SEARCH_TASK_DETAILS = 30;


//    private static final String HTTP = "http"
//    private static final String HTTPS = "https"
//    private static final String COLON_WITH_SLASH = "://"
//    private static final String OPENING_WWW = '//www.'
//    private static final String DOUBLE_SLASH = '//'

    /*public static final String getFullUrl(HttpServletRequest request, boolean ignoreWWW) {

        boolean includePort = true
        String scheme       = request.getScheme()             // http
        String serverName   = request.getServerName()     // localhost
        int serverPort      = request.getServerPort()        // 8080
        String contextPath  = request.getContextPath()   // root
        boolean inHttp      = scheme.equalsIgnoreCase(HTTP)
        boolean inHttps     = scheme.equalsIgnoreCase(HTTPS)

        if ((inHttp && (serverPort == 80)) || (inHttps && (serverPort == 443))) {
            includePort = false;
        }
        String fullUrl = scheme + COLON_WITH_SLASH + serverName +
                (includePort ? (COLON + serverPort) : EMPTY_SPACE) + contextPath
        if (ignoreWWW) {
            fullUrl = fullUrl.replace(OPENING_WWW, DOUBLE_SLASH)
        }
        return fullUrl
    }*/

    /*public static final boolean isValidEmail(String email) {
        if (!email) {
            return false
        }
        return email.matches(EMAIL_PATTERN)
    }*/

    /**
     * retrieve system configuration message
     * @param sysConfiguration
     * @return
     */
    /*public static final String getSysConfigMessage(SysConfiguration sysConfiguration) {
        if (sysConfiguration && sysConfiguration.message) {
            return sysConfiguration.message
        }
        return SYS_CONFIG_NOT_FOUND_MESSAGE + SINGLE_SPACE + (sysConfiguration ? sysConfiguration.key : EMPTY_SPACE)
    }*/

    /*
    * Intersects two given collections of Domains based on id property
    */

    /*public static List intersectById(List left, List right) {
        List result = []
        if (left.isEmpty() || right.isEmpty()) return result

        List big = (left.size() >= right.size()) ? left : right
        List small = (right.size() <= left.size()) ? right : left
        List<Long> bigIds = big*.id
        result = small.findAll { bigIds.contains(it.id) }
        return result
    }*/

    /*public static String roundDouble(double number, int precision) {
        String format = (precision > 0) ? "0." : "0"
        try {
            for (int i = 0; i < precision; i++) format += "0"
            DecimalFormat df = new DecimalFormat(format)
            String abc = df.format(number)
            return abc
        } catch (ignored) {
            return number.toString()
        }
    }*/
}


