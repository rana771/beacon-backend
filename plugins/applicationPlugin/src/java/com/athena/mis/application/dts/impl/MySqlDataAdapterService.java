package com.athena.mis.application.dts.impl;

import au.com.bytecode.opencsv.CSVWriter;
import com.athena.mis.DataExportDto;
import com.athena.mis.application.dts.DataAdapterService;
import com.athena.mis.application.entity.AppDbInstance;
import com.athena.mis.application.entity.TransactionLog;
import com.athena.mis.application.utility.TimeLogger;
import org.apache.log4j.Logger;

import javax.xml.soap.Text;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.Date;


/**
 * Created by Mirza-Ehsan on 06-Dec-2014.
 */
public class MySqlDataAdapterService extends DataAdapterService {

    private Logger log = Logger.getLogger(getClass());

    private boolean CONTINUE_EXECUTION = true;

    private static final String IS_MAPPED = "isMapped";
    private static final String START_TRANSACTION = "START TRANSACTION;";
    private static final String COMMIT = "COMMIT;";
    private static final String MY_SQL = "my_sql";
    private static final String EXPORT = "export";
    private static final String DATA_EXPORT_DTO = "dataExportDto";
    private static final String ANGLE_QUOTE = "`";
    private static final String COLON = ":";
    private static final String SLASH = "/";
    private static final String USER = "?user=";
    private static final String PASSWORD = "&password=";
    private static final String ADDITIONAL_PARAM = "&autoReconnect=true&useCursorFetch=true";

    private String JOB = null;

    private AppDbInstance appDbInstance;

    public MySqlDataAdapterService(AppDbInstance instance) {
        super.appDbInstance = instance;
        appDbInstance = instance;
    }

    private String connString = null;

    @Override
    public String getConnectionString() {
        if (connString != null)
            return connString;
        connString = appDbInstance.getUrl() + COLON + appDbInstance.getPort() + SLASH + appDbInstance.getDbName() + USER + appDbInstance.getUserName() + PASSWORD + appDbInstance.getPassword() + ADDITIONAL_PARAM;
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

    @Override
    public Map executeSelect(String sql, boolean convertToList) {
        Map result = getInitResult();
        try {
            createConnection();
            Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);
            statement.setFetchSize(Integer.MIN_VALUE);
            ResultSet rs = statement.executeQuery(sql);
            if (!convertToList) {
                result.put(RESULT_SET, rs);
            } else {
                List lstResult = convertResultSetToArrayList(rs);
                result.put(LST_RESULT, lstResult);
                rs.close();
                statement.close();
            }
            return result;
        } catch (Exception e) {
            result.put(IS_ERROR, Boolean.TRUE);
            result.put(EXCEPTION, e.getMessage());
            rollback();
            return result;
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
     * Write data from mysql DB to CSV
     * <p/>
     * Get required data from Map
     * Initialize timer logger for calculate read, write and processing time
     * <p/>
     * Create new directory for each table(if not available) and file
     * If previous file exist then start from next file
     * Get data from db by predefined batch and write them to file
     * Close file when it fulfilled record per file condition and create new one
     * Create transaction log after successful write in a file
     * Write file and directory(for tables) until all table data copied to CSV
     * <p/>
     * If any exception occur or user manually stopped execution then execution break from there and
     * delete current file from system also update parent object with end time
     * So that after restart the process system can start from that file
     * <p/>
     * After Successful execution update parent object by isCompleteOperation(true) and end time
     *
     * @param params - required parameters for transfer data to CSV
     */
    /*public void writeDataFeed(Map params) {
        String entityDomainName = EMPTY_SPACE;
        long entityId = 0L;
        try {
            createConnection();
            CONTINUE_EXECUTION = true;

            String exceptionMsg = null;
            long processingTime;
            long timeToRead;
            long executeTime;
            boolean isException = false;
            DataExportDto dataExportDto = (DataExportDto) params.get(DATA_EXPORT_DTO);
            List<String> lstTableName = (List<String>) dataExportDto.getLstTableName();
            List<String> lstSchema = (List<String>) dataExportDto.getLstSchema();
            entityId = dataExportDto.getId();

            //Initialize TimeLogger for calculate read, write and precessing time
            TimeLogger readLogger = new TimeLogger(true);
            TimeLogger writeLogger = new TimeLogger(true);
            TimeLogger processingLogger = new TimeLogger(true);

            for (int i = 0; i < lstTableName.size(); i++) {
                String sourceTblName = lstTableName.get(i);

                //Create Directory for each table
                String fileDir = createDirectory(dataExportDto, sourceTblName);

                long logCount = 0;
                long totalRecord = 0;
                Map logInfoResult = getLogInfo(dataExportDto.getId(), dataExportDto.getEntityTypeId(), sourceTblName);
                if (logInfoResult != null) {
                    logCount = Long.parseLong(logInfoResult.get("logCount").toString());
                    totalRecord = Long.parseLong(logInfoResult.get("totalRecord").toString());
                    if (totalRecord == 0) {
                        totalRecord = dataExportDto.getMaxRecordPerFile();
                    }
                }

                int fileNo = 1;
                long loopStartNo = 0;

                //Check required file existence
                for (; fileNo <= logCount; fileNo++) {
                    String fileName = fileDir + File.separator + fileNo + UNDERSCORE + dataExportDto.getMaxRecordPerFile() + dataExportDto.getFileExt();
                    File file = new File(fileName);
                    if (!file.exists()) {
                        break;
                    }
                }
                if (fileNo > 1) {
                    //Get loopStartNo if previous file exists
                    loopStartNo = (fileNo - 1) * (dataExportDto.getMaxRecordPerFile() / dataExportDto.getRecordPerBatch());
                    SQL_COUNT = (fileNo - 1) * dataExportDto.getMaxRecordPerFile();
                    if (SQL_COUNT > totalRecord) {
                        SQL_COUNT = totalRecord;
                    }
                }

                //Create new File
                File file = createNewFile(fileDir, fileNo, dataExportDto.getMaxRecordPerFile(), dataExportDto.getFileExt());
                //Initialize FileWriter and BufferedWriter
                FileWriter fileWriter = new FileWriter(file, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                long fileRowCount = 1;
                long totalRow = 0;
                int loopNo = 0;
                boolean isNewFile = true;

                for (; ; ) {
                    String fileContent = null;
                    if (!CONTINUE_EXECUTION) {
                        bufferedWriter.close();
                        file.delete();
                        break;
                    }

                    totalRow = fileRowCount * dataExportDto.getRecordPerBatch();

                    //Get select sql
                    String schema = lstSchema.get(i);
                    String trgSql = SELECT_FROM + "`" + sourceTblName+ "`" + LIMIT + dataExportDto.getRecordPerBatch() + OFFSET + (loopStartNo * dataExportDto.getRecordPerBatch()) + SEMICOLON;
                    if (!schema.equals(EMPTY_SPACE)) {
                        trgSql = SELECT_FROM + "`" + sourceTblName + "` ORDER BY " + schema + LIMIT + dataExportDto.getRecordPerBatch() + OFFSET + (loopStartNo * dataExportDto.getRecordPerBatch()) + SEMICOLON;
                    }

                    //Fetch data from DB for a batch
                    readLogger.start();
                    Map selectResult = executeSelect(trgSql, false);
                    readLogger.logInterval();

                    Boolean isError = (Boolean) selectResult.get(IS_ERROR);
                    if (isError.booleanValue() == true) {
                        exceptionMsg = selectResult.get(EXCEPTION).toString();
                        log.error(ERROR_IN_EXECUTE_SERVICE + exceptionMsg);

                        bufferedWriter.close();
                        file.delete();
                        isException = true;
                        break;
                    }

                    //Start processing time and convert result set for write to csv
                    processingLogger.start();
                    ResultSet resultSet = (ResultSet) selectResult.get(RESULT_SET);
                    if (dataExportDto.getDataFeedTypeId() == dataExportDto.getDataFeedTypeCsvId()) {
                        fileContent = convertResultSetToDateFeed(resultSet, dataExportDto.getColumnSeparator(), dataExportDto.getEscapeCharacter(), dataExportDto.getQuoteCharacter());
                    } else if (dataExportDto.getDataFeedTypeId() == dataExportDto.getDataFeedTypeTxtId()) {
                        fileContent = convertResultSetToDateFeed(resultSet, dataExportDto.getColumnSeparator());
                    }
                    processingLogger.logInterval();

                    //If new file and no data exists then execute this block
                    if (fileContent.equalsIgnoreCase(EMPTY_SPACE) && loopNo == 0) {
                        bufferedWriter.close();
                        file.delete();
                        break;
                    }

                    TransactionLog trLog = null;
                    //If no data exist in batch then execute this block
                    if (fileContent.equalsIgnoreCase(EMPTY_SPACE)) {

                        //If there are no data in further then execute this block
                        writeDataAndCloseBuffer(bufferedWriter, fileContent);

                        //Calculate the read, write and processing time
                        timeToRead = readLogger.calculate();
                        processingTime = processingLogger.calculate();
                        executeTime = writeLogger.calculate();

                        // build transaction log for last transaction
                        trLog = getTransactionLog(dataExportDto, fileContent, sourceTblName, i, lstTableName, timeToRead, processingTime, executeTime, exceptionMsg, file, totalRow);
                        // save Transaction log for last transaction
                        saveTransactionLog(trLog);
                        break;

                    } else if (totalRow >= dataExportDto.getMaxRecordPerFile()) {

                        //If file is new the execute this block for write column header
                        if (isNewFile) {
                            String colNames = getColumnNamesSql(resultSet, dataExportDto.getColumnSeparator(), dataExportDto.getQuoteCharacter());
                            bufferedWriter.write(colNames);
                            isNewFile = false;
                        }

                        //If max record per file then execute this block
                        writeDataAndCloseBuffer(bufferedWriter, fileContent);

                        //Calculate the read, write and processing time
                        timeToRead = readLogger.calculate();
                        processingTime = processingLogger.calculate();
                        executeTime = writeLogger.calculate();

                        // build transaction log for last transaction
                        trLog = getTransactionLog(dataExportDto, fileContent, sourceTblName, i, lstTableName,
                                timeToRead, processingTime, executeTime, exceptionMsg,
                                file, totalRow);
                        // save Transaction log for last transaction
                        saveTransactionLog(trLog);

                        fileRowCount = 1;
                        totalRow = 0;
                        loopNo = 0;
                        fileNo++;

                        //Create new file after one file write successfully
                        file = createNewFile(fileDir, fileNo, dataExportDto.getMaxRecordPerFile(), dataExportDto.getFileExt());
                        fileWriter = new FileWriter(file, true);
                        bufferedWriter = new BufferedWriter(fileWriter);

                        //Get column name for write in file as header
                        String colNames = getColumnNamesSql(resultSet, dataExportDto.getColumnSeparator(), dataExportDto.getQuoteCharacter());
                        bufferedWriter.write(colNames);
                    } else {
                        writeLogger.start();

                        //If file is new the execute this block for write column header
                        if (isNewFile) {
                            String colNames = getColumnNamesSql(resultSet, dataExportDto.getColumnSeparator(), dataExportDto.getQuoteCharacter());
                            bufferedWriter.write(colNames);
                            isNewFile = false;
                        }

                        //If data can be continue to write in this file then execute this block
                        bufferedWriter.write(fileContent);
                        fileRowCount++;
                        loopNo++;
                        writeLogger.logInterval();
                    }
                    loopStartNo++;
                    Statement s = resultSet.getStatement();
                    resultSet.close();
                    s.close();
                }
                SQL_COUNT = 0;
                if (!CONTINUE_EXECUTION || isException) break;
            }

            // If execution stop by user or any exception occur the execute this method for update parent object
            stopExecutionAndSendMail(dataExportDto, isException, exceptionMsg);

        } catch (Throwable ex) {
            log.error(ex.getMessage());
            updateParentObject(entityDomainName, entityId, new Date(), false, ex.getMessage());
        }
    }*/
    public void writeDataFeed(Map params) {
        String entityDomainName = EMPTY_SPACE;
        long entityId = 0L;
        try {
            createConnection();
            CONTINUE_EXECUTION = true;

            String exceptionMsg = null;
            boolean isException = false;
            DataExportDto dataExportDto = (DataExportDto) params.get(DATA_EXPORT_DTO);
            List<String> lstTableName = (List<String>) dataExportDto.getLstTableName();
            List<Integer> lstLogCount = (List<Integer>) dataExportDto.getLstLogCount();
            List<List> lstSchema = (List<List>) dataExportDto.getLstSchema();
            List<List> lstFieldValue = (List<List>) dataExportDto.getLstFieldValue();
            entityId = dataExportDto.getId();

            for (int i = 0; i < lstTableName.size(); i++) {
                String sourceTblName = lstTableName.get(i);
                int logCount = lstLogCount.get(i);
                long sqlCount = 0;

                List<String> lstSchemaForSourceTable = (List<String>) lstSchema.get(i);
                List<String> lstFieldValueForSourceTable = (List<String>) lstFieldValue.get(i);
                String schema = getSchema(lstSchemaForSourceTable);
                String fieldValue = getFieldValue(lstSchemaForSourceTable, lstFieldValueForSourceTable);

                //Create Directory for each table
                String fileDir = createDirectory(dataExportDto, sourceTblName);

                String trgSql = SELECT_FROM + ANGLE_QUOTE + sourceTblName + ANGLE_QUOTE + SEMICOLON;
                if (!schema.equals(EMPTY_SPACE)) {
                    trgSql = SELECT_FROM + ANGLE_QUOTE + sourceTblName + ANGLE_QUOTE + " order by " + schema + SEMICOLON;
                }
                if (logCount > 0 && !schema.equals(EMPTY_SPACE) && !fieldValue.equals(EMPTY_SPACE)) {
                    trgSql = SELECT_FROM + ANGLE_QUOTE + sourceTblName + ANGLE_QUOTE + " where " + fieldValue + " order by " + schema + SEMICOLON;
                }
                Map selectResult = executeSelect(trgSql, false);

                Boolean isError = (Boolean) selectResult.get(IS_ERROR);
                if (isError) {
                    exceptionMsg = selectResult.get(EXCEPTION).toString();
                    log.error(ERROR_IN_EXECUTE_SERVICE + exceptionMsg);
                    isException = true;
                    break;
                }

                //Start processing time and convert result set for write to csv
                ResultSet resultSet = (ResultSet) selectResult.get(RESULT_SET);
                exceptionMsg = convertResultSetToDateFeed(resultSet, dataExportDto, fileDir, sourceTblName, lstTableName, sqlCount, lstSchemaForSourceTable);

                Statement s = resultSet.getStatement();
                resultSet.close();
                s.close();

                if (exceptionMsg != null) {
                    isException = true;
                    break;
                }
                if (!CONTINUE_EXECUTION) break;
            }

            // If execution stop by user or any exception occur the execute this method for update parent object
            stopExecutionAndSendMail(dataExportDto, isException, exceptionMsg);
        } catch (Throwable ex) {
            log.error(ex.getMessage());
            updateParentObject(entityDomainName, entityId, new Date(), false, ex.getMessage());
        }
    }

    private String getSchema(List<String> lstSchema) {
        String schema = EMPTY_SPACE;
        for (int i = 0; i < lstSchema.size(); i++) {
            if (i == lstSchema.size() - 1) {
                schema = schema + lstSchema.get(i);
            } else {
                schema = schema + lstSchema.get(i) + COMA;
            }
        }
        return schema;
    }

    private String getFieldValue(List<String> lstSchema, List<String> lstFieldValue) {
        String fieldValue = EMPTY_SPACE;
        for (int i = 0; i < lstFieldValue.size(); i++) {
            String value = lstFieldValue.get(i);
            String schema = lstSchema.get(i);
            if (value != null) {
                if (i == lstFieldValue.size() - 1) {
                    fieldValue = fieldValue + schema + ">'" + value + "'";
                } else {
                    fieldValue = fieldValue + schema + ">'" + value + "' and ";
                }
            }
        }
        return fieldValue;
    }

    private Map getLogInfo(long entityId, long entityTypeId, String tableName) {
        String sql = "SELECT COUNT(id) AS log_count, coalesce(MAX(total_record), 0) AS max_record FROM transaction_log WHERE " + "entity_id=" + entityId
                + " AND entity_type_id=" + entityTypeId + " AND table_name='" + tableName + "';";
        try {
            Statement statement = conNative.createStatement();
            ResultSet rs = statement.executeQuery(sql);
            long logCount = 0;
            long totalRecord = 0;
            while (rs.next()) {
                logCount = Long.parseLong(rs.getObject(1).toString());
                totalRecord = Long.parseLong(rs.getObject(2).toString());
            }
            statement.close();
            Map<String, Long> data = new HashMap<String, Long>();
            data.put("logCount", logCount);
            data.put("totalRecord", totalRecord);
            return data;
        } catch (SQLException e) {
            log.error(e.getMessage());
            return null;
        }
    }

    // If execution stop by user or any exception occur the execute this method for update parent object
    public void stopExecutionAndSendMail(DataExportDto dataExportDto, boolean isException, String exceptionMsg) {
        Date endTime;
        if (isException) {
            endTime = new Date();
            updateParentObject(dataExportDto.getEntityDomainName(), dataExportDto.getId(), endTime, false, exceptionMsg);
        } else if (CONTINUE_EXECUTION) {
            //If execution successfully done then execute this block for update parent object
            endTime = new Date();
            updateParentObject(dataExportDto.getEntityDomainName(), dataExportDto.getId(), endTime, true, null);
            sendExportImportMail(dataExportDto.getId(), dataExportDto.getUrl(), dataExportDto.getCompanyId(), dataExportDto.getEntityTypeId());
        }
    }

    /*private String convertResultSetToDateFeed(ResultSet rs, String columnSeparator, String escapeChar, String quoteCharacter) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount();
        StringBuffer sb = new StringBuffer(EMPTY_SPACE);
//        if (columnSeparator.equals(TAB_STRING)) {
//            columnSeparator = TAB_CHAR;
//        }
        while (rs.next()) {
            StringBuffer data = new StringBuffer(EMPTY_SPACE);
            for (int i = 1; i <= colCount; ++i) {
                Object tmp = rs.getObject(i);
                if (tmp == null) {
                    data.append(NULL).append(columnSeparator);
                } else if (tmp instanceof Boolean) {
                    int tmpBool = (((Boolean) tmp).booleanValue() == true) || (tmp.toString().equalsIgnoreCase(DIGIT_1)) ? 1 : 0;
                    data.append(quoteCharacter).append(tmpBool).append(quoteCharacter).append(columnSeparator);
                } else if (tmp instanceof byte[]) {
                    data.append(NULL).append(columnSeparator);
                } else if (tmp instanceof Blob) {
                    data.append(NULL).append(columnSeparator);
                } else if (tmp instanceof String) {
                    String tmpStr = ((String) tmp).replace(quoteCharacter, (escapeChar + quoteCharacter));
                    data.append(quoteCharacter).append(tmpStr).append(quoteCharacter).append(columnSeparator);
                } else {
                    data.append(quoteCharacter).append(tmp.toString()).append(quoteCharacter).append(columnSeparator);
                }
            }
            data.deleteCharAt(data.length() - columnSeparator.length()).append(NEW_LINE);
            sb.append(data);
            SQL_COUNT++;
        }
        return sb.toString();
    }

    private String convertResultSetToDateFeed(ResultSet rs, String columnSeparator) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount();
        StringBuffer sb = new StringBuffer(EMPTY_SPACE);
//        if (columnSeparator.equals(TAB_STRING)) {
//            columnSeparator = TAB_CHAR;
//        }
        while (rs.next()) {
            StringBuffer data = new StringBuffer(EMPTY_SPACE);
            for (int i = 1; i <= colCount; ++i) {
                Object tmp = rs.getObject(i);
                if (tmp == null) {
                    data.append(NULL).append(columnSeparator);
                } else if (tmp instanceof Boolean) {
                    int tmpBool = (((Boolean) tmp).booleanValue() == true) || (tmp.toString().equalsIgnoreCase(DIGIT_1)) ? 1 : 0;
                    data.append(tmpBool).append(columnSeparator);
                } else if (tmp instanceof byte[]) {
                    data.append(NULL).append(columnSeparator);
                } else if (tmp instanceof Blob) {
                    data.append(NULL).append(columnSeparator);
                } else if (tmp instanceof String) {
                    data.append(tmp).append(columnSeparator);
                } else {
                    data.append(tmp).append(columnSeparator);
                }
            }
            data.deleteCharAt(data.length() - columnSeparator.length()).append(NEW_LINE);
            sb.append(data);
            SQL_COUNT++;
        }
        return sb.toString();
    }*/

    private String convertResultSetToDateFeed(ResultSet rs, DataExportDto dataExportDto, String fileDir, String sourceTblName, List<String> lstTableName, long sqlCount, List<String> lstSchema) throws SQLException {
        try {
            String columnSeparator = dataExportDto.getColumnSeparator();
            String escapeCharForCsv = dataExportDto.getEscapeCharacter() + dataExportDto.getQuoteCharacter();
            String quoteCharacter = dataExportDto.getQuoteCharacter();
            long maxRecordPerFile = dataExportDto.getMaxRecordPerFile();

            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();
            StringBuffer sb = new StringBuffer(EMPTY_SPACE);

            long totalRowCount = 0;
            int fileNo = 1;
            String data = EMPTY_SPACE;

            long processingTime;
            // initialize TimeLogger for calculate precessing time
            TimeLogger processingLogger = new TimeLogger(true);
            processingLogger.start();
            String colNames = getColumnNamesSql(rs, columnSeparator, quoteCharacter);
            List<String> lstValue = new ArrayList<String>();

            boolean isWrite = false;
            while (rs.next()) {
                if (!CONTINUE_EXECUTION) {
                    isWrite = false;
                    break;
                }
                isWrite = true;

                if (lstSchema.size() > 0) {
                    lstValue.clear();
                    for (int i = 0; i < lstSchema.size(); i++) {
                        lstValue.add(rs.getString(lstSchema.get(i)));
                    }
                }

                // convert row to csv or text
                if (dataExportDto.getDataFeedTypeId() == dataExportDto.getDataFeedTypeCsvId()) {
                    data = convertRowToCsv(rs, colCount, columnSeparator, escapeCharForCsv, quoteCharacter);
                } else if (dataExportDto.getDataFeedTypeId() == dataExportDto.getDataFeedTypeTxtId()) {
                    data = convertRowToText(rs, colCount, columnSeparator);
                }
                sb.append(data);
                sqlCount++;
                totalRowCount++;

                if (totalRowCount == maxRecordPerFile) {
                    processingLogger.logInterval();
                    processingTime = processingLogger.calculate();
                    // write to file and save log
                    writeToFileAndSaveLog(fileDir, fileNo, dataExportDto, colNames, processingTime, sourceTblName, lstTableName, sb.toString(), sqlCount);
                    // save field value
                    saveFieldValue(lstSchema, lstValue, sourceTblName, dataExportDto.getId());

                    processingLogger.start();
                    fileNo++;
                    totalRowCount = 0;
                    isWrite = false;
                    sb = new StringBuffer(EMPTY_SPACE);
                }
            }

            if (isWrite) {
                processingTime = processingLogger.calculate();
                // write to file and save log
                writeToFileAndSaveLog(fileDir, fileNo, dataExportDto, colNames, processingTime, sourceTblName, lstTableName, sb.toString(), sqlCount);
                // save field value
                saveFieldValue(lstSchema, lstValue, sourceTblName, dataExportDto.getId());
            }
            return null;
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String convertRowToCsv(ResultSet rs, int colCount, String columnSeparator, String escapeChar, String quoteCharacter) throws SQLException {
        StringBuffer data = new StringBuffer(EMPTY_SPACE);
        for (int i = 1; i <= colCount; ++i) {
            Object tmp = rs.getObject(i);
            if (tmp == null) {
                data.append(NULL).append(columnSeparator);
            } else if (tmp instanceof Boolean) {
                int tmpBool = (((Boolean) tmp).booleanValue() == true) ? 1 : 0;
                data.append(quoteCharacter).append(tmpBool).append(quoteCharacter).append(columnSeparator);
            } else if (tmp instanceof String) {
                String tmpStr = ((String) tmp).replace(quoteCharacter, escapeChar);
                data.append(quoteCharacter).append(tmpStr).append(quoteCharacter).append(columnSeparator);
            } else if (tmp instanceof byte[]) {
                data.append(NULL).append(columnSeparator);
            } else {
                data.append(quoteCharacter).append(tmp.toString()).append(quoteCharacter).append(columnSeparator);
            }
        }
        data.deleteCharAt(data.length() - columnSeparator.length()).append(NEW_LINE);
        return data.toString();
    }

    private String convertRowToText(ResultSet rs, int colCount, String columnSeparator) throws SQLException {
        StringBuffer data = new StringBuffer(EMPTY_SPACE);
        for (int i = 1; i <= colCount; ++i) {
            Object tmp = rs.getObject(i);
            if (tmp == null) {
                data.append(NULL).append(columnSeparator);
            } else if (tmp instanceof Boolean) {
                int tmpBool = (((Boolean) tmp).booleanValue() == true) ? 1 : 0;
                data.append(tmpBool).append(columnSeparator);
            } else if (tmp instanceof String) {
                data.append(tmp).append(columnSeparator);
            } else if (tmp instanceof byte[]) {
                data.append(NULL).append(columnSeparator);
            } else {
                data.append(tmp).append(columnSeparator);
            }
        }
        data.deleteCharAt(data.length() - columnSeparator.length()).append(NEW_LINE);
        return data.toString();
    }

    private void writeToFileAndSaveLog(String fileDir, int fileNo, DataExportDto dataExportDto, String colNames, long processingTime, String sourceTblName, List<String> lstTableName, String sb, long sqlCount) throws IOException {
        String fileText = dataExportDto.getFileExt();
        long maxRecordPerFile = dataExportDto.getMaxRecordPerFile();
        long executeTime;
        long readTime;

        File file = createNewFile(fileDir, fileNo, maxRecordPerFile, fileText);
        //Initialize FileWriter and BufferedWriter
        FileWriter fileWriter = new FileWriter(file, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        // write to file
        TimeLogger writeLogger = new TimeLogger(true);
        writeLogger.start();
        bufferedWriter.write(colNames);
        writeDataAndCloseBuffer(bufferedWriter, sb);
        writeLogger.logInterval();

        executeTime = writeLogger.calculate();
        readTime = (processingTime * 25) / 100;
        processingTime = processingTime - readTime;
        // build transaction log
        TransactionLog trLog = getTransactionLog(dataExportDto, sb, sourceTblName, fileNo, lstTableName, readTime, processingTime, executeTime, null, file, maxRecordPerFile, sqlCount);
        // save Transaction log
        saveTransactionLog(trLog);
    }

    //Build transaction log object by requires parameters
    private TransactionLog getTransactionLog(DataExportDto dataExportDto, String fileContent, String sourceTblName, int i, List<String> lstTable, long fetchTime, long processingTime, long executeTime, String exception, File file, long totalRow, long sqlCount) {
        String comment = null;
        TransactionLog transferLog = new TransactionLog();

        if (!CONTINUE_EXECUTION) {
            comment = DATA_EXPORT_STOPPED;
        } else if ((fileContent.equalsIgnoreCase(EMPTY_SPACE)) && ((i + 1) == lstTable.size())) {
            comment = DATA_EXPORT_FOR_FILE + file.getName() + IS_DONE + NEW_LINE + DATA_EXPORT_FOR_TABLE + sourceTblName + IS_DONE_FOR_ALL_TABLE;
        } else if (fileContent.equalsIgnoreCase(EMPTY_SPACE)) {
            comment = DATA_EXPORT_FOR_FILE + file.getName() + IS_DONE + NEW_LINE + DATA_EXPORT_FOR_TABLE + sourceTblName + IS_DONE;
        } else if (totalRow >= dataExportDto.getMaxRecordPerFile()) {
            comment = DATA_EXPORT_FOR_FILE + file.getName() + IS_DONE;
        }

        transferLog.setSequence(0L);
        transferLog.setEntityTypeId(dataExportDto.getEntityTypeId());
        transferLog.setEntityId(dataExportDto.getId());
        transferLog.setTotalRecord(sqlCount);
        transferLog.setRecordPerBatch(dataExportDto.getRecordPerBatch());
        transferLog.setTimeToRead(fetchTime);
        transferLog.setTimeToWrite(executeTime);
        transferLog.setProcessingTime(processingTime);
        transferLog.setStartTime(0L);
        transferLog.setEndTime(0L);
        transferLog.setTableName(sourceTblName);
        transferLog.setException(exception);
        transferLog.setComment(comment);
        transferLog.setCompanyId(dataExportDto.getCompanyId());
        transferLog.setCreatedOn(new Date());

        return transferLog;
    }

    //Create new directory for each table
    private String createDirectory(DataExportDto dataExportDto, String tableName) {
        String fileDir = dataExportDto.getDataFeedPath() + File.separator + MY_SQL + File.separator + EXPORT + File.separator + dataExportDto.getName() + File.separator + tableName.toLowerCase();
        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return fileDir;
    }

    //Create new file by specified file no
    private File createNewFile(String fileDir, int fileNo, long maxRecord, String fileExt) {
        String fileName = fileDir + File.separator + fileNo + UNDERSCORE + maxRecord + fileExt;
        File file = new File(fileName);

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return file;
    }

    private void saveFieldValue(List<String> lstSchema, List<String> lstValue, String tableName, long dataExportId) throws Exception {
        if (lstSchema.size() == 0)
            return;
        try {
            String updateQuery = "update dpl_data_export_source_schema set value = '";
            String whereQuery = "' where primary_key = '";
            String andQuery = "' and table_name = '" + tableName + "' and dpl_data_export_id = " + dataExportId;
            String strQuery = EMPTY_SPACE;
            for (int i = 0; i < lstSchema.size(); i++) {
                strQuery = strQuery + updateQuery + lstValue.get(i) + whereQuery + lstSchema.get(i) + andQuery + NEW_LINE;
            }
            updateFieldValue(strQuery);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new Exception(e);
        }
    }

    private static final String WHERE_TABLE = " AND table_name IN ";
    private static final String WHERE_TABLE_NOT_IN = " AND table_name NOT IN ";
    private static final String SELECT_ALL_TABLE = "SELECT table_name, table_rows, (data_length+index_length) disk_usage FROM INFORMATION_SCHEMA.TABLES WHERE table_rows IS NOT NULL AND table_schema = ";
    private static final String SELECT_ALL_VIEW = "SELECT table_name, 0 table_rows, 0 disk_usage FROM INFORMATION_SCHEMA.VIEWS WHERE table_schema = ";

    // get select all tables
    private Map selectTablesWithCount() {
        Map result = getInitResult();
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(SELECT_ALL_TABLE + SINGLE_QUOTE + appDbInstance.getDbName() + SINGLE_QUOTE);
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
            String sql = SELECT_ALL_VIEW + SINGLE_QUOTE + appDbInstance.getDbName() + SINGLE_QUOTE;
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
            String sql = SELECT_ALL_TABLE + SINGLE_QUOTE + appDbInstance.getDbName() + SINGLE_QUOTE + WHERE_TABLE + tblName;
            if ((Boolean) params.get(IS_MAPPED) && (tblName.isEmpty())) {
                sql = SELECT_ALL_TABLE + SINGLE_QUOTE + appDbInstance.getDbName() + SINGLE_QUOTE;
            } else if ((Boolean) params.get(IS_MAPPED) && (!tblName.isEmpty())) {
                sql = SELECT_ALL_TABLE + SINGLE_QUOTE + appDbInstance.getDbName() + SINGLE_QUOTE + WHERE_TABLE_NOT_IN + PARENTHESES_START + tblName + PARENTHESES_END;
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

    private static final String COLUMN_DETAILS = "SELECT DISTINCT(COLUMN_NAME) column_name, DATA_TYPE data_type, IS_NULLABLE is_nullable,\n" +
            "            COALESCE(CHARACTER_MAXIMUM_LENGTH,0) length, column_key\n" +
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
        return START_TRANSACTION + script + COMMIT;
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

    public void loadFromDataFeed(Map params) {
    }
}
