package com.athena.mis.application.dts.impl;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.athena.mis.DataImportDto;
import com.athena.mis.application.dts.DataAdapterService;
import com.athena.mis.application.entity.AppDbInstance;
import com.athena.mis.application.entity.TransactionLog;
import com.athena.mis.application.utility.TimeLogger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Rezaul on 16-Jun-2015.
 */
public class AmazonRedshiftDataAdapterService extends DataAdapterService {

    private Logger log = Logger.getLogger(getClass());

    private boolean CONTINUE_EXECUTION = true;

    private static final String COMMIT = "COMMIT;";
    private static final String DATA_IMPORT_DTO = "dataImportDto";
    private static final String BEGIN_TRANSACTION = "BEGIN TRANSACTION;";
    private static final String FILE_SEPARATOR = "/";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String USER = "?user=";
    private static final String PASSWORD = "&password=";

    private String JOB = null;

    private AppDbInstance appDbInstance;

    // initialize AppDbInstance object by DataAdapter
    public AmazonRedshiftDataAdapterService(AppDbInstance instance) {
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

    // get a map object of "SELECT_TABLES" operation
    public Map listTable(Map params) {
        Map result = getInitResult();
        try {
            createConnection();
            if (params == null || params.get(ALL_TABLE) != null) {
                if (params == null) {
                    result = selectTablesWithCount();
                } else {
                    String tableName = null;
                    if (params.get(TBL_NAME) != null) {
                        Object table = params.get(TBL_NAME);
                        tableName = table.toString();
                    }
                    result = selectAllTable(tableName);
                }
            } else if (params.get(OBJECT_TYPE) != null) {
                if (params.get(OBJECT_TYPE).equals(TABLE)) {
                    result = selectTablesWithCount();
                } else {
                    result = selectViewsWithCount();
                }
            } else {
                Object tbl = params.get(TBL_NAME);
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
     * Load Data from CSV to Amazon Redshift DB
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
    public void loadFromDataFeed(Map params) {
        String entityDomainName = EMPTY_SPACE;
        long entityId = 0L;
        try {
            createConnection();
            CONTINUE_EXECUTION = true;

            String exceptionMsg = null;
            long timeToRead = 0;
            long timeToWrite;
            long processingTime = 0;
            boolean isException = false;

            DataImportDto dataImportDto = (DataImportDto) params.get(DATA_IMPORT_DTO);
            List<String> lstTableName = (List<String>) dataImportDto.getLstTableName();
            entityId = dataImportDto.getId();

            //Initialize read, write and processing time logger
            TimeLogger writeLogger = new TimeLogger();

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
                String copyWithCol = getInsertSql(dataImportDto, trgTblName, fileDir, fileNo);
                System.out.println("copyWithCol >> " + copyWithCol);
                if (copyWithCol == null) continue;

                for (; fileNo <= fileCount; fileNo++) {
                    if (!CONTINUE_EXECUTION) {
                        break;
                    }
                    //Get file and check file existence
                    String filePath = fileDir + File.separator + fileNo + UNDERSCORE + dataImportDto.getMaxRecordPerFile() + dataImportDto.getFileExt();
                    File file = new File(filePath);
                    if (!file.exists()) {
                        continue;
                    }
                    String copyScriptSql = getCopyScript(dataImportDto, copyWithCol, trgTblName, fileNo);
                    System.out.println("copyScriptSql >> " + copyScriptSql);

                    writeLogger.start();
                    //Execute insert sql
                    Map insertData = execute(copyScriptSql);
                    timeToWrite = writeLogger.calculate();

                    Boolean isError = (Boolean) insertData.get(IS_ERROR);
                    if (isError.booleanValue() == true) {
                        exceptionMsg = insertData.get(EXCEPTION).toString();
                        log.error(ERROR_IN_EXECUTE_SERVICE + exceptionMsg);

                        isException = true;
                        break;
                    }
                    long totalRow = fileNo * dataImportDto.getMaxRecordPerFile();
                    // get row count
                    FileReader fr = new FileReader(file);
                    LineNumberReader lnr = new LineNumberReader(fr);
                    int lineNumber = 0;

                    while (lnr.readLine() != null) {
                        lineNumber++;
                    }
                    lnr.close();
                    fr.close();
                    sqlCount = sqlCount + (lineNumber - 1);
                    //Save transaction log per file
                    TransactionLog trLog = getTransactionLogForImport(dataImportDto, exceptionMsg, timeToRead, timeToWrite, processingTime, lstTableName.size(), i, trgTblName, fileCount, fileNo, file, totalRow, sqlCount);
                    saveTransactionLog(trLog);

                }
                if (!CONTINUE_EXECUTION || isException) break;
            }

            //If execution stop by user or any exception occur the execute this method for update parent object
            stopExecutionAndSendMail(dataImportDto, isException, exceptionMsg);
        } catch (Throwable ex) {
            //ex.printStackTrace();
            log.error(ex.getMessage());
            updateParentObject(entityDomainName, entityId, new Date(), false, ex.getMessage());
        }
    }

    // get copy command script
    private String getCopyScript(DataImportDto dataImportDto, String copyWithCol, String trgTblName, int fileNo) {
        String copySql = null;
        if (dataImportDto.getDataFeedTypeId() == dataImportDto.getDataFeedTypeCsvId()) {
            copySql = copyWithCol + " '" + dataImportDto.getDplAwsBaseUrl() + dataImportDto.getExportedObjName() + FILE_SEPARATOR
                    + trgTblName + FILE_SEPARATOR + fileNo + UNDERSCORE + dataImportDto.getMaxRecordPerFile() + dataImportDto.getFileExt()
                    + "' credentials 'aws_access_key_id=" + dataImportDto.getDplAwsAccessKeyId() + ";aws_secret_access_key=" + dataImportDto.getDplAwsSecretAccessKey() + "' "
                    + "csv quote as '" + dataImportDto.getQuoteCharacter() + "' delimiter '" + dataImportDto.getColumnSeparator() + "' NULL as 'null' IGNOREHEADER 1;";
        } else if (dataImportDto.getDataFeedTypeId() == dataImportDto.getDataFeedTypeTxtId()) {
            copySql = copyWithCol + " '" + dataImportDto.getDplAwsBaseUrl() + dataImportDto.getExportedObjName() + FILE_SEPARATOR
                    + trgTblName + FILE_SEPARATOR + fileNo + UNDERSCORE + dataImportDto.getMaxRecordPerFile() + dataImportDto.getFileExt()
                    + "' credentials 'aws_access_key_id=" + dataImportDto.getDplAwsAccessKeyId() + ";aws_secret_access_key=" + dataImportDto.getDplAwsSecretAccessKey() + "' "
                    + "delimiter '" + dataImportDto.getColumnSeparator() + "' NULL as 'null' IGNOREHEADER 1;";
        }

        return copySql;
    }

    // If execution stop by user or any exception occur the execute this method for update parent object
    public void stopExecutionAndSendMail(DataImportDto dataImportDto, boolean isException, String exceptionMsg) {
        Date endTime;
        if (isException) {
            endTime = new Date();
            updateParentObject(dataImportDto.getEntityDomainName(), dataImportDto.getId(), endTime, false, exceptionMsg);
        } else if (CONTINUE_EXECUTION) {
            //If execution successfully done then execute this block for update parent object
            endTime = new Date();
            updateParentObject(dataImportDto.getEntityDomainName(), dataImportDto.getId(), endTime, true, null);
            sendExportImportMail(dataImportDto.getId(), dataImportDto.getUrl(), dataImportDto.getCompanyId(), dataImportDto.getEntityTypeId());
        }
    }

    private String getTransactionLogCount(DataImportDto dataImportDto, String trgTblName) {
        String transactionLogCount = "SELECT count(*) FROM transaction_log  WHERE company_id  = " + dataImportDto.getCompanyId()
                + " AND entity_type_id = " + dataImportDto.getEntityTypeId() + " AND entity_id = " + dataImportDto.getId()
                + " AND table_name like '" + trgTblName + "';";
        return transactionLogCount;
    }

    private static final String COPY = "COPY ";
    private static final char COMMA = ',';

    //Get InsertSql with column header
    private String getInsertSql(DataImportDto dataImportDto, String trgTblName, String fileDir, int fileNo) throws IOException {
        CSVReader csvReaderFirst = null;
        String columnSeparator = dataImportDto.getColumnSeparator();
        String quoteCharacter = dataImportDto.getQuoteCharacter();
        String schemaName = dataImportDto.getSchemaName();
        String escapeChar = dataImportDto.getEscapeCharacter();

        String fileNameFirst = fileDir + File.separator + fileNo + UNDERSCORE + dataImportDto.getMaxRecordPerFile() + dataImportDto.getFileExt();
        File fileFirst = new File(fileNameFirst);
        if (!fileFirst.exists()) return null;

        String columns = null;
        InputStreamReader readerFirst = new InputStreamReader(new FileInputStream(fileFirst));
        if (dataImportDto.getDataFeedTypeId() == dataImportDto.getDataFeedTypeCsvId()) {
            csvReaderFirst = new CSVReader(readerFirst, columnSeparator.charAt(0), quoteCharacter.charAt(0), escapeChar.charAt(0), 0, true, false);
            String[] firstLine = csvReaderFirst.readNext();
            columns = StringUtils.join(firstLine, columnSeparator.charAt(0));
        } else if (dataImportDto.getDataFeedTypeId() == dataImportDto.getDataFeedTypeTxtId()) {
            csvReaderFirst = new CSVReader(readerFirst, columnSeparator.charAt(0));
            String[] firstLine = csvReaderFirst.readNext();
            columns = StringUtils.join(firstLine, COMMA);
        }

        if (schemaName != null) {
            return COPY + schemaName + "." + '"' + trgTblName + '"' + PARENTHESES_START + columns + PARENTHESES_END_AND_FROM;
        }
        return COPY + trgTblName + PARENTHESES_START + columns + PARENTHESES_END_AND_FROM;
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

    private static final String SELECT_ALL_TABLE = "select \"table\" table_name, tbl_rows table_rows, size disk_usage\n" +
            "            FROM svv_table_info";
    private static final String SELECT_ALL_TABLE_WITHOUT_COUNT = "select table_name from information_schema.tables\n" +
            "where table_schema = 'public' and table_type = 'BASE TABLE'";
    private static final String ORDER_BY = " ORDER BY 1;";
    private static final String WHERE_TABLE = " WHERE \"table\" IN ";
    private static final String WHERE_TABLE_NOT_IN = " WHERE \"table\" NOT IN ";
    private static final String WHERE_TABLE_NOT_IN_FOR_ALL_TABLE = " AND table_name NOT IN ";
    private static final String WHERE_TABLE_IN_FOR_ALL_TABLE = " AND table_name IN ";

    // get select all tables
    private Map selectTablesWithCount() {
        Map result = getInitResult();
        try {
            createConnection();
            Statement statement = connection.createStatement();
            String sql = SELECT_ALL_TABLE + ORDER_BY;
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

    private Map selectAllTable(String tableName) {
        Map result = getInitResult();
        try {
            createConnection();
            String sql = SELECT_ALL_TABLE_WITHOUT_COUNT + WHERE_TABLE_IN_FOR_ALL_TABLE + tableName + ORDER_BY;
            if (params.get(IS_MAPPED) != null) {
                if ((Boolean) params.get(IS_MAPPED) && (tableName.isEmpty())) {
                    sql = SELECT_ALL_TABLE_WITHOUT_COUNT + ORDER_BY;
                } else if ((Boolean) params.get(IS_MAPPED) && (!tableName.isEmpty())) {
                    sql = SELECT_ALL_TABLE_WITHOUT_COUNT + WHERE_TABLE_NOT_IN_FOR_ALL_TABLE + PARENTHESES_START + tableName + PARENTHESES_END + ORDER_BY;
                }
            }
            Statement statement = connection.createStatement();
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

    // get list of all table view
    private Map selectViewsWithCount() {
        Map result = getInitResult();
        try {
            createConnection();
            Statement statement = connection.createStatement();
            String sql = SELECT_ALL_TABLE + ORDER_BY;
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

    private Map selectTablesWithCount(String tblName) {
        Map result = getInitResult();
        try {
            createConnection();
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
        } catch (Exception e) {
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
        return BEGIN_TRANSACTION + script + COMMIT;
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
            rollback();
            return result;
        }
    }

    public void writeDataFeed(Map params) {
    }
}
