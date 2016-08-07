package com.athena.mis.application.dts.impl;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.athena.mis.DataImportDto;
import com.athena.mis.application.dts.DataAdapterService;
import com.athena.mis.application.entity.AppDbInstance;
import com.athena.mis.application.entity.TransactionLog;
import com.athena.mis.application.utility.TimeLogger;
import groovy.lang.Writable;
import groovy.text.SimpleTemplateEngine;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mirza-Ehsan on 11-Dec-2014.
 */
public class GreenPlumDataAdapterService extends DataAdapterService {

    private Logger log = Logger.getLogger(getClass());

    private boolean CONTINUE_EXECUTION = true;

    private static final String TABLE_NAME = "tableName";
    private static final String FILE_LOCATION = "file_location";
    private static final String IS_COPY = "isCopy";
    private static final String EXPORT = "export";
    private static final String DATA_IMPORT_DTO = "dataImportDto";
    private static final String BEGIN_TRANSACTION = "BEGIN TRANSACTION;";
    private static final String END_TRANSACTION_COMMIT = "END TRANSACTION;COMMIT;";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String USER = "?user=";
    private static final String PASSWORD = "&password=";

    private String JOB = null;

    private AppDbInstance appDbInstance;

    // initialize AppDbInstance object by DataAdapter
    public GreenPlumDataAdapterService(AppDbInstance instance) {
        super.appDbInstance = instance;
        appDbInstance = instance;
    }

    private String connString = null;

    @Override
    public String getConnectionString() {
        if (connString != null)
            return connString;
        connString = appDbInstance.getUrl() + COLON + appDbInstance.getPort() + SLASH + appDbInstance.getDbName() + USER + appDbInstance.getUserName() + PASSWORD + appDbInstance.getPassword();
        return connString;
    }

    @Override
    public void setJob(String jobClassName) {
        JOB = jobClassName;
    }

    @Override
    public String getJob() {
        return JOB;
    }

    @Override
    public void setContinueExecution(boolean isContinueExecution) {
        CONTINUE_EXECUTION = isContinueExecution;
    }

    public void loadFromDataFeed(Map params) {
        try {
            createConnection();
            Boolean isCopy = (Boolean) params.get(IS_COPY);
            // isCopy = true, then load with copy command else
            if (isCopy) {
                // load csv to db with COPY command
                loadFromDataFeedUsingCopyCommand(params);
            } else {
                // load csv to db with INSERT command
                loadFromDataFeedWithBulkInsert(params);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // get a map object of "SELECT_TABLES" operation
    public Map listTable(Map params) {
        Map result = getInitResult();
        try {
            createConnection();

            if (params == null) {
                // get list tables
                result = selectTablesWithCount();
            } else if (params.get(OBJECT_TYPE) != null) {
                if (params.get(OBJECT_TYPE).equals(TABLE)) {
                    // get select all tables
                    result = selectTablesWithCount();
                } else {
                    // get list view with count
                    result = selectViewsWithCount();
                }
            } else {
                Object tbl = params.get(TBL_NAME);
                // get list of specific table
                result = selectTablesWithCount(tbl.toString());
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            result.put(IS_ERROR, Boolean.TRUE);
            result.put(EXCEPTION, e.getMessage());
            return result;
        }
    }

    /**
     * Load Data from CSV to Geenplum DB with COPY command
     * <p/>
     * Get required data from Map
     * Initialize TimeLogger for calculate read, write and processing time
     * <p/>
     * Get targeted folder by table name
     * Check transaction log for next executable file
     * Get data from file and build insert sql
     * After build sql for each file insert data to DB
     * Save transaction log after successful insertion for each file
     * <p/>
     * If any exception occur or user manually stopped execution then execution break from there
     * Also update parent object with end time
     * So that after restart the process system can start from that file by checking transaction log
     * <p/>
     * After Successful execution update parent object by isCompleteOperation(true) and end time
     *
     * @param params - required parameters for transfer data to CSV
     */
    private void loadFromDataFeedUsingCopyCommand(Map params) {
        String entityDomainName = EMPTY_SPACE;
        long entityId = 0L;
        try {
            CONTINUE_EXECUTION = true;

            String exceptionMsg = null;
            long timeToRead = 0;
            long timeToWrite;
            long processingTime = 0;
            boolean isException = false;

            DataImportDto dataImportDto = (DataImportDto) params.get(DATA_IMPORT_DTO);
            List<String> lstTableName = (List<String>) dataImportDto.getLstTableName();
            entityId = dataImportDto.getId();
            entityDomainName = dataImportDto.getEntityDomainName();
            // Initialize read, write and processing time logger
            TimeLogger writeLogger = new TimeLogger();

            // load process step 1 start with each table
            for (int i = 0; i < lstTableName.size(); i++) {
                String trgTblName = lstTableName.get(i);
                long sqlCount = 0;

                // Get targeted file and check existence
                String fileDir = dataImportDto.getDataFeedPath() + File.separator + trgTblName;
                File folder = new File(fileDir);
                if (!folder.exists()) continue;

                int fileCount = folder.listFiles().length;
                //Check if file count zero the continue loop
                if (fileCount <= 0) {
                    continue;
                }
                // get transaction log count
                String transactionLogCount = getTransactionLogCount(dataImportDto, trgTblName);

                //Count executed file from Transaction log
                Map executeCount = executeCountNative(transactionLogCount);
                Boolean isErrorCount = Boolean.parseBoolean(executeCount.get(IS_ERROR).toString());
                if (isErrorCount) {
                    isException = true;
                    log.error(ERROR_IN_EXECUTE_SERVICE + executeCount.get(EXCEPTION).toString());
                    break;
                }

                int totalExecutedFile = Integer.parseInt(executeCount.get(COUNT).toString());
                int fileNo = 1;

                //Set fileNo if any file executed
                if (totalExecutedFile > 0) {
                    fileNo = totalExecutedFile + 1;
                    sqlCount = totalExecutedFile * dataImportDto.getMaxRecordPerFile();
                }

                //Get inset sql with column for specific table
                String fileNameFirst = fileDir + File.separator + fileNo + UNDERSCORE + dataImportDto.getMaxRecordPerFile() + dataImportDto.getFileExt();
                File fileFirst = new File(fileNameFirst);
                if (!fileFirst.exists()) continue;

                // load process step 2 start with each file
                for (; fileNo <= fileCount; fileNo++) {

                    if (!CONTINUE_EXECUTION) {
                        break;
                    }

                    //Get file and check file existence
                    String fileName = fileDir + File.separator + fileNo + UNDERSCORE + dataImportDto.getMaxRecordPerFile() + dataImportDto.getFileExt();
                    File file = new File(fileName);
                    if (!file.exists()) {
                        continue;
                    }
                    String copyScriptSql = getCopyScript(dataImportDto, trgTblName, fileNo);
                    System.out.println("copyScriptSql >> " + copyScriptSql);
                    writeLogger.start();

                    //Execute insert sql
                    Map insertData = execute(copyScriptSql);
                    timeToWrite = writeLogger.calculate();

                    Boolean isError = (Boolean) insertData.get(IS_ERROR);
                    if (isError.booleanValue() == true) {
                        exceptionMsg = getExceptionMsg(insertData);
                        isException = true;
                        break;
                    }
                    // create transaction log
                    sqlCount = createTransactionLog(dataImportDto, exceptionMsg, timeToRead, timeToWrite, processingTime, lstTableName, i, trgTblName, fileCount, fileNo, file, sqlCount);
                }
                if (!CONTINUE_EXECUTION || isException) break;
            }
            //If execution stop by user or any exception occur the execute this method for update parent object
            stopExecutionAndSendMail(isException, exceptionMsg, entityDomainName, entityId, dataImportDto.getUrl(), dataImportDto.getCompanyId(), dataImportDto.getEntityTypeId());
        } catch (Throwable ex) {
            //ex.printStackTrace();
            log.error(ex.getMessage());
            updateParentObject(entityDomainName, entityId, new Date(), false, ex.getMessage());
        }
    }

    // evaluate copy command script with param
    private String getCopyScript(DataImportDto dataImportDto, String trgTblName, int fileNo) throws IOException {
        String fileLocation = dataImportDto.getGreenPlumMountLoc() + dataImportDto.getVendorDir() + File.separator + EXPORT + File.separator + dataImportDto.getExportedObjName() + File.separator + trgTblName + File.separator + fileNo + UNDERSCORE + dataImportDto.getMaxRecordPerFile() + dataImportDto.getFileExt();

        SimpleTemplateEngine engine = new SimpleTemplateEngine();
        Map parameters = new HashMap();
        parameters.put(TABLE_NAME, trgTblName);
        parameters.put(FILE_LOCATION, fileLocation);
        try {
            Writable template = engine.createTemplate(dataImportDto.getCopyScript()).make(parameters);
            String script = template.toString();
            return script;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }


    // If execution stop by user or any exception occur the execute this method for update parent object
    public void stopExecutionAndSendMail(boolean isException, String exceptionMsg, String entityDomainName, long entityId, String url, long companyId, long entityTypeId) {
        Date endTime;
        if (isException) {
            endTime = new Date();
            updateParentObject(entityDomainName, entityId, endTime, false, exceptionMsg);
        } else {
            //If execution successfully done then execute this block for update parent object
            endTime = new Date();
            updateParentObject(entityDomainName, entityId, endTime, true, null);
            // send mail per object
            sendExportImportMail(entityId, url, companyId, entityTypeId);
        }
    }


    private String getTransactionLogCount(DataImportDto dataImportDto, String trgTblName) {
        String transactionLogCount = "SELECT count(*) FROM transaction_log  WHERE company_id  = " + dataImportDto.getCompanyId()
                + " AND entity_type_id = " + dataImportDto.getEntityTypeId() + " AND entity_id = " + dataImportDto.getId()
                + " AND table_name like '" + trgTblName + "';";
        return transactionLogCount;
    }

    private long createTransactionLog(DataImportDto dataImportDto, String exceptionMsg, long timeToRead, long timeToWrite, long processingTime, List<String> lstTableName, int i, String trgTblName, int fileCount, int fileNo, File file, long sqlCount) throws IOException {
        long totalRow = fileNo * dataImportDto.getMaxRecordPerFile();
        // get row count
        FileReader fr = new FileReader(file);
        LineNumberReader lnr = new LineNumberReader(fr);
        int lineNumber = 0;

        while (lnr.readLine() != null) {
            lineNumber++;
        }
        lnr.close();
        sqlCount = sqlCount + (lineNumber - 1);
        // build transaction log per file
        TransactionLog trLog = getTransactionLogForImport(dataImportDto, exceptionMsg, timeToRead, timeToWrite, processingTime, lstTableName.size(), i, trgTblName, fileCount, fileNo, file, totalRow, sqlCount);
        // Save transaction log per file
        saveTransactionLog(trLog);
        return sqlCount;
    }

    // get exception message when sql execute
    private String getExceptionMsg(Map insertData) {
        String eMessage = insertData.get(EXCEPTION).toString();
        log.error(ERROR_IN_EXECUTE_SERVICE + eMessage);
        return eMessage;
    }

    /**
     * Load Data from CSV to Geenplum DB with INSERT command
     * <p/>
     * Get required data from Map
     * Initialize TimeLogger for calculate read, write and processing time
     * <p/>
     * Get targeted folder by table name
     * Check transaction log for next executable file
     * Get data from file and build insert sql
     * After build sql for each file insert data to DB
     * Save transaction log after successful insertion for each file
     * <p/>
     * If any exception occur or user manually stopped execution then execution break from there
     * Also update parent object with end time
     * So that after restart the process system can start from that file by checking transaction log
     * <p/>
     * After Successful execution update parent object by isCompleteOperation(true) and end time
     *
     * @param params - required parameters for transfer data to CSV
     */
    private void loadFromDataFeedWithBulkInsert(Map params) {
        String entityDomainName = EMPTY_SPACE;
        long entityId = 0L;
        try {
            CONTINUE_EXECUTION = true;

            Date finishTime;
            String exceptionMsg = null;
            long timeToRead, timeToWrite, processingTime;
            boolean isException = false;

            DataImportDto dataImportDto = (DataImportDto) params.get(DATA_IMPORT_DTO);
            List<String> lstTableName = (List<String>) dataImportDto.getLstTableName();
            entityId = dataImportDto.getId();
            entityDomainName = dataImportDto.getEntityDomainName();

            //Initialize read, write and processing time logger
            TimeLogger readLogger = new TimeLogger(true);   // for line by line read
            TimeLogger processingLogger = new TimeLogger(true);
            TimeLogger writeLogger = new TimeLogger();

            StringBuilder sbValues = new StringBuilder();   // common stringBuilder instance
            // load process step 1 start with each table
            for (int i = 0; i < lstTableName.size(); i++) {

                String trgTblName = lstTableName.get(i);
                long sqlCount = 0;

                //Get targeted file and check existence
                String fileDir = dataImportDto.getDataFeedPath() + File.separator + trgTblName;
                File folder = new File(fileDir);
                if (!folder.exists()) continue;

                int fileCount = folder.listFiles().length;
                //Check if file count zero the continue loop
                if (fileCount <= 0) {
                    continue;
                }

                String sql = getTransactionLogCount(dataImportDto, trgTblName);

                //Count executed file from Transaction log
                Map executeCount = executeCountNative(sql);
                Boolean isErrorCount = Boolean.parseBoolean(executeCount.get(IS_ERROR).toString());
                if (isErrorCount) {
                    isException = true;
                    log.error(ERROR_IN_EXECUTE_SERVICE + executeCount.get(EXCEPTION).toString());
                    break;
                }

                int totalExecutedFile = Integer.parseInt(executeCount.get(COUNT).toString());
                int fileNo = 1;

                //Set fileNo if any file executed
                if (totalExecutedFile > 0) {
                    fileNo = totalExecutedFile + 1;
                    sqlCount = totalExecutedFile * dataImportDto.getMaxRecordPerFile();
                }

                //Get inset sql with column for specific table
                String insertWithCol = getInsertSql(dataImportDto, trgTblName, fileDir, fileNo);
                if (insertWithCol == null) continue;

                // load process step 1 start with each file
                for (; fileNo <= fileCount; fileNo++) {

                    if (!CONTINUE_EXECUTION) {
                        break;
                    }
                    //Get file and check file existence
                    String fileName = fileDir + File.separator + fileNo + UNDERSCORE + dataImportDto.getMaxRecordPerFile() + dataImportDto.getFileExt();
                    File file = new File(fileName);
                    if (!file.exists()) {
                        continue;
                    }

                    //Initialize InputStreamReader and CSVReader for file
                    InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
                    char columnSepChar = dataImportDto.getColumnSeparator().charAt(0);
                    if (dataImportDto.getColumnSeparator().equals(TAB_STRING)) {
                        columnSepChar = '\t';
                    }

                    CSVReader csvReader = null;
                    if (dataImportDto.getDataFeedTypeId() == dataImportDto.getDataFeedTypeCsvId()) {
                        csvReader = new CSVReader(reader, columnSepChar, dataImportDto.getQuoteCharacter().charAt(0), dataImportDto.getEscapeCharacter().charAt(0), 0, true, false);
                    } else if (dataImportDto.getDataFeedTypeId() == dataImportDto.getDataFeedTypeTxtId()) {
                        csvReader = new CSVReader(reader, columnSepChar);
                    }

                    int recordBatchCount = 1;
                    long totalRow = fileNo * dataImportDto.getMaxRecordPerFile();

                    sbValues.setLength(0);  // empty the stringBuilder
                    String insertQuery;

                    csvReader.readNext(); // remove 1st line;
                    while (true) {

                        readLogger.start();
                        //read next line from csv
                        String[] nextLineAsTokens = csvReader.readNext();
                        readLogger.logInterval();

                        if ((nextLineAsTokens == null) || !CONTINUE_EXECUTION) break;

                        processingLogger.start();
                        //Append values from csvReader to StringBuilder
                        sqlCount = appendSql(nextLineAsTokens, sbValues, sqlCount);
                        processingLogger.logInterval();

                        recordBatchCount++;
                    }

                    //Calculate read and precessing time
                    timeToRead = readLogger.calculate();
                    processingTime = processingLogger.calculate();

                    //If any data exist and continue execution the execute this block
                    if (recordBatchCount > 1 && CONTINUE_EXECUTION) {

                        //Build insert query with column and values
                        insertQuery = insertWithCol + sbValues.substring(0, sbValues.length() - 1) + SEMICOLON;
                        writeLogger.start();
                        //Execute insert sql
                        Map insertData = execute(insertQuery);
                        timeToWrite = writeLogger.calculate();

                        Boolean isError = (Boolean) insertData.get(IS_ERROR);
                        if (isError.booleanValue() == true) {
                            exceptionMsg = getExceptionMsg(insertData);
                            csvReader.close();
                            isException = true;
                            break;
                        }

                        //Save transaction log per file
                        TransactionLog trLog = getTransactionLogForImport(dataImportDto, exceptionMsg, timeToRead, timeToWrite, processingTime, lstTableName.size(), i, trgTblName, fileCount, fileNo, file, totalRow, sqlCount);
                        saveTransactionLog(trLog);
                    }
                    csvReader.close();
                    reader.close();
                }
                if (!CONTINUE_EXECUTION || isException) break;
            }

            if (isException) {
                finishTime = new Date();
                updateParentObject(entityDomainName, dataImportDto.getId(), finishTime, false, exceptionMsg);
            } else if (CONTINUE_EXECUTION) {
                //If execution successfully done then execute this block for update parent object
                finishTime = new Date();
                updateParentObject(entityDomainName, dataImportDto.getId(), finishTime, true, null);
                sendExportImportMail(dataImportDto.getId(), dataImportDto.getUrl(), dataImportDto.getCompanyId(), dataImportDto.getEntityTypeId());
            }
        } catch (Throwable ex) {
            //ex.printStackTrace();
            log.error(ex.getMessage());
            updateParentObject(entityDomainName, entityId, new Date(), false, ex.getMessage());
        }
    }

    //Append data to StringBuilder
    private long appendSql(String[] row, StringBuilder sbValues, long sqlCount) {
        String sql = PARENTHESES_START;
        for (int column = 0; column < row.length; column++) {
            if (row[column].equalsIgnoreCase(EMPTY_SPACE) || row[column].equalsIgnoreCase(NULL)) {
                sql = sql + NULL + COMA;
            } else {
                row[column] = row[column].replace(SINGLE_QUOTE, (SINGLE_QUOTE + SINGLE_QUOTE));
                sql = sql + SINGLE_QUOTE + row[column] + SINGLE_QUOTE_AND_COMA;
            }

        }
        sql = sql.substring(0, sql.length() - 1) + PARENTHESES_END_AND_COMA;
        sbValues.append(sql);
        sqlCount++;
        return sqlCount;
    }

    //Get InsertSql with column header
    private String getInsertSql(DataImportDto dataImportDto, String trgTblName, String fileDir, int fileNo) throws IOException {
        CSVReader csvReaderFirst = null;
        Long maxRecordPerFile = dataImportDto.getMaxRecordPerFile();
        String columnSeparator = dataImportDto.getColumnSeparator();
        String quoteCharacter = dataImportDto.getQuoteCharacter();
        String schemaName = dataImportDto.getSchemaName();
        String fileExt = dataImportDto.getFileExt();
        String escapeChar = dataImportDto.getEscapeCharacter();

        String fileNameFirst = fileDir + File.separator + fileNo + UNDERSCORE + maxRecordPerFile + fileExt;
        File fileFirst = new File(fileNameFirst);
        if (!fileFirst.exists()) return null;

        InputStreamReader readerFirst = new InputStreamReader(new FileInputStream(fileFirst));
        char columnSepChar = columnSeparator.charAt(0);
        if (columnSeparator.equals(TAB_STRING)) {
            columnSepChar = '\t';
        }

        if (dataImportDto.getDataFeedTypeId() == dataImportDto.getDataFeedTypeCsvId()) {
            csvReaderFirst = new CSVReader(readerFirst, columnSepChar, quoteCharacter.charAt(0), escapeChar.charAt(0), 0, true, false);
        } else if (dataImportDto.getDataFeedTypeId() == dataImportDto.getDataFeedTypeTxtId()) {
            csvReaderFirst = new CSVReader(readerFirst, columnSepChar);
        }
        String[] firstLine = csvReaderFirst.readNext();
        String columns = StringUtils.join(firstLine, COMA);

        csvReaderFirst.close();
        readerFirst.close();
        if (schemaName != null) {
            return INSERT_INTO + schemaName + "." + '"' + trgTblName + '"' + PARENTHESES_START + columns + PARENTHESES_END_AND_VALUE;
        }
        return INSERT_INTO + trgTblName + PARENTHESES_START + columns + PARENTHESES_END_AND_VALUE;
    }

    // Build TransactionLog object with necessary parameters
    private TransactionLog getTransactionLogForImport(DataImportDto dataImportDto, String exception, long timeToRead, long executeTime,
                                                      long processingTime, int lstTableCount, int i, String sourceTblName, int fileCount, int fileNo, File file, long totalRow, long sqlCount) {
        String comment = null;
        TransactionLog transferLog = new TransactionLog();

        if (!CONTINUE_EXECUTION) {
            comment = DATA_IMPORT_MANUALLY_STOPPED_BY_USER;
        } else if (totalRow >= dataImportDto.getMaxRecordPerFile()) {
            comment = DATA_IMPORT_FOR_FILE + file.getName() + IS_DONE;
            if ((fileNo == fileCount) && (totalRow >= dataImportDto.getMaxRecordPerFile())) {
                comment = comment + NEW_LINE + DATA_IMPORT_FOR_FOR_TABLE + sourceTblName + IS_DONE;
                if ((i + 1) == lstTableCount) {
                    comment = comment + NEW_LINE + DATA_IMPORT_FOR_ALL_TABLE_S_IS_DONE;
                }
            }
        }

        transferLog.setSequence(0L);
        transferLog.setEntityTypeId(dataImportDto.getEntityTypeId());
        transferLog.setEntityId(dataImportDto.getId());
        transferLog.setTotalRecord(sqlCount);
        transferLog.setRecordPerBatch(0);
        transferLog.setTimeToRead(timeToRead);
        transferLog.setTimeToWrite(executeTime);
        transferLog.setProcessingTime(processingTime);
        transferLog.setStartTime(0L);
        transferLog.setEndTime(0L);
        transferLog.setTableName(sourceTblName);
        transferLog.setException(exception);
        transferLog.setComment(comment);
        transferLog.setCompanyId(dataImportDto.getCompanyId());
        transferLog.setCreatedOn(new Date());

        return transferLog;
    }

    private static final String SELECT_ALL_TABLE = "SELECT sotdtablename as table_name,sotdsize as disk_usage, 0 AS table_rows\n" +
            "FROM gp_toolkit.gp_size_of_table_disk\n" +
            "WHERE sotdschemaname = 'public'";
    private static final String ORDER_BY = " ORDER BY sotdtablename;";
    private static final String WHERE_TABLE = " AND sotdtablename IN ";
    private static final String WHERE_TABLE_NOT_IN = " AND sotdtablename NOT IN ";

    // get select all tables
    private Map selectTablesWithCount() {
        Map result = getInitResult();
        try {
            Statement statement = connection.createStatement();
            String sql = SELECT_ALL_TABLE + ORDER_BY;
            ResultSet rs = statement.executeQuery(sql);
            List lstResult = convertResultSetToArrayList(rs);
            statement.close();
            result.put(LST_RESULT, lstResult);
            return result;
        } catch (SQLException e) {
            log.error(e.getMessage());
            result.put(IS_ERROR, Boolean.TRUE);
            result.put(EXCEPTION, e.getMessage());
            rollback();
            return result;
        }
    }

    // get list of all table view
    private Map selectViewsWithCount() {
        Map result = getInitResult();
        try {
            Statement statement = connection.createStatement();
            String sql = SELECT_ALL_TABLE + ORDER_BY;
            ResultSet rs = statement.executeQuery(sql);
            List lstResult = convertResultSetToArrayList(rs);
            statement.close();
            result.put(LST_RESULT, lstResult);
            return result;
        } catch (SQLException e) {
            log.error(e.getMessage());
            result.put(IS_ERROR, Boolean.TRUE);
            result.put(EXCEPTION, e.getMessage());
            rollback();
            return result;
        }
    }

    // get select specific tables
    private Map selectTablesWithCount(String tblName) {
        Map result = getInitResult();
        try {
            String sql = SELECT_ALL_TABLE + WHERE_TABLE + tblName + ORDER_BY;
            if ((Boolean) params.get(IS_MAPPED) && (tblName.isEmpty())) {
                sql = SELECT_ALL_TABLE + ORDER_BY;
            } else if ((Boolean) params.get(IS_MAPPED) && (!tblName.isEmpty())) {
                sql = SELECT_ALL_TABLE + WHERE_TABLE_NOT_IN + PARENTHESES_START + tblName + PARENTHESES_END + ORDER_BY;
            }
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            List lstResult = convertResultSetToArrayList(rs);
            statement.close();
            result.put(LST_RESULT, lstResult);
            return result;
        } catch (SQLException e) {
            log.error(e.getMessage());
            result.put(IS_ERROR, Boolean.TRUE);
            result.put(EXCEPTION, e.getMessage());
            rollback();
            return result;
        }
    }

    private static final String COLUMN_DETAILS = "SELECT column_name, data_type, is_nullable,\n" +
            "            COALESCE(character_maximum_length,0) length, 0 column_key\n" +
            "            FROM INFORMATION_SCHEMA.COLUMNS\n" +
            "            WHERE table_name = '";

    // get list of table column details with tblName
    public Map selectColumnDetails(String tblName) {
        Map result = getInitResult();
        try {
            createConnection();
            Statement statement = connection.createStatement();
            String sql = COLUMN_DETAILS + tblName + SINGLE_QUOTE;
            ResultSet rs = statement.executeQuery(sql);
            List lstResult = convertResultSetToArrayList(rs);
            statement.close();
            result.put(LST_RESULT, lstResult);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            result.put(IS_ERROR, Boolean.TRUE);
            result.put(EXCEPTION, e.getMessage());
            rollback();
            return result;
        }
    }

    public String wrapWithTransactionBlock(String script) {
        return BEGIN_TRANSACTION + script + END_TRANSACTION_COMMIT;
    }

    public Map convertSqlToCsvStream(Map params) {
        Map result = getInitResult();
        try {
            createConnection();
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(params.get(SQL).toString());
            File tmpFile = File.createTempFile(CSV_FILE_BYTES, CSV);
            CSVWriter writer = new CSVWriter(new FileWriter(tmpFile), ',');
            if (params.get(VENDOR) != null) {
                String vendor[] = {VENDOR, params.get(VENDOR).toString()};
                String dbName[] = {DB_NAME, params.get(DB_NAME).toString()};
                String queryName[] = {QUERY_NAME, params.get(QUERY_NAME).toString()};
                String query[] = {QUERY_STRING, params.get(SQL).toString()};
                String note[] = {NOTE, params.get(NOTE).toString()};
                writer.writeNext(vendor);
                writer.writeNext(dbName);
                writer.writeNext(queryName);
                writer.writeNext(query);
                writer.writeNext(note);
            }
            writer.writeAll(rs, true);
            writer.flush();
            writer.close();
            // read file to byte
            byte[] byteArr = new byte[(int) tmpFile.length()];
            InputStream is = new FileInputStream(tmpFile);
            is.read(byteArr);
            is.close();
            tmpFile.delete();
            statement.close();
            result.put(CSV_FILE_BYTES, byteArr);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            result.put(IS_ERROR, Boolean.TRUE);
            result.put(EXCEPTION, e.getMessage());
            return result;
        }
    }

    public void writeDataFeed(Map params) {
    }
}
