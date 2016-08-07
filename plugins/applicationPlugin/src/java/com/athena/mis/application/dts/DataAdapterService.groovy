package com.athena.mis.application.dts

import com.athena.mis.application.entity.AppDbInstance
import com.athena.mis.application.entity.TransactionLog
import org.apache.log4j.Logger

import java.sql.*
import java.util.Date

/**
 * Created by rumana on 10/19/2015.
 */
public abstract class DataAdapterService {

    private Logger log = Logger.getLogger(getClass());

    protected static final String ERROR_IN_EXECUTE_SERVICE = "Error in DataAdapter: ";
    protected static final String CONNECTION_ERR_MSG = "Failed to connection. Reason: ";
    protected static final String IS_ERROR = "isError";
    protected static final String EXCEPTION = "exception";
    protected static final String MIN_VALUE = "min";
    protected static final String MAX_VALUE = "max";
    protected static final String SQL = "sql";
    protected static final String TEXT = "text";
    protected static final String CSV = ".csv";
    protected static final String CSV_FILE_BYTES = "csvFileBytes";
    protected static final String UNDERSCORE = "_";

    protected static final String SOURCE_DB_INSTANCE = "sourceInstance";
    protected static final String LIMIT = " LIMIT ";
    protected static final String SELECT_FROM = "SELECT * FROM ";
    protected static final String OFFSET = " OFFSET ";

    protected static final String LST_TABLE_NAME = "lstTableName";
    protected static final String LST_SCHEMA = "lstSchema";
    protected static final String RECORD_PER_BATCH = "recordPerBatch";
    protected static final String CSV_FILE_PATH = "csvFilePath";
    protected static final String COLUMN_SEPARATOR = "columnSeparator";
    protected static final String ESCAPE_CHARACTER = "escapeCharacter";
    protected static final String QUOTE_CHARACTER = "quoteCharacter";
    protected static final String MAX_RECORD_PER_FILE = "maxRecordPerFile";
    protected static final String ENTITY_TYPE_ID = "entityTypeId";
    protected static final String ENTITY_ID = "entityId";
    protected static final String ENTITY_NAME = "entityName";
    protected static final String ENTITY_DOMAIN_NAME = "entityDomainName";
    protected static final String ID = "id";
    protected static final String NAME = "name";
    protected static final String COUNT = "count";
    protected static final String SEMICOLON = ";";
    protected static final String IS_MAPPED = "isMapped";
    protected static final String MESSAGE = 'message';

    protected static final String REPEAT_COUNT = "repeatCount";

    protected static final String PARENTHESES_START = "(";
    protected static final String PARENTHESES_END = ")";
    protected static final String COMA = ",";
    protected static final String SINGLE_QUOTE = "'";

    protected static final String SINGLE_QUOTE_AND_COMA = "',";
    protected static final String PARENTHESES_END_AND_COMA = "),";
    protected static final String INSERT_INTO = "INSERT INTO ";
    protected static final String EMPTY_SPACE = "";
    protected static final String PARENTHESES_END_AND_VALUE = ") VALUES";
    protected static final String PARENTHESES_END_AND_FROM = ") FROM";
    protected static final String NULL = "null";
    protected static final String NEW_LINE = "\r\n";
    protected static final String TAB_STRING = "\\t";
    protected static final String TAB_CHAR = "\t";
    protected static final String RESULT_SET = "resultSet";
    protected static final String LST_RESULT = "lstResult";
    protected static final String ROW_COUNT = "rowCount";
    protected static final String TBL_NAME = "tblName";
    protected static final String OBJECT_TYPE = "objectType";
    protected static final String TABLE = "Table";
    protected static final String ALL_TABLE = "allTable";
    protected static final String SINGLE_SPACE = " ";
    protected static final String URL = "url";
    protected static final String COMPANY_ID = "companyId";
    protected static final String VENDOR = "vendor";
    protected static final String DB_NAME = "dbName";
    protected static final String QUERY_NAME = "queryName";
    protected static final String QUERY_STRING = "query";
    protected static final String NOTE = "note";


    protected static final String DATA_EXPORT_FOR_TABLE = "Data export for table ";
    protected static final String IS_DONE = " is done.";
    protected static final String IS_DONE_FOR_ALL_TABLE = " is done.\nData transfer for all table(s) is done.";
    protected static final String DATA_EXPORT_STOPPED = "Data export manually stopped by user.";
    protected static final String DATA_EXPORT_FOR_FILE = "Data export for file ";
    //Constant for Data import
    protected static final String DATA_IMPORT_MANUALLY_STOPPED_BY_USER = "Data import manually stopped by user.";
    protected static final String DATA_IMPORT_FOR_FILE = "Data import for file ";
    protected static final String DATA_IMPORT_FOR_FOR_TABLE = "Data import for for table ";
    protected static final String DATA_IMPORT_FOR_ALL_TABLE_S_IS_DONE = "Data import for all table(s) is done.";

    protected static final String INSERT = "insert";
    protected static final String UPDATE = "update";
    protected static final String DELETE = "delete";
    protected static final String ALTER = "alter";
    protected static final String REPLACE = "replace";

    protected static final String DIGIT_1 = "1";

    protected AppDbInstance nativeDbInstance = null;

    protected Map params = null;

    protected static Connection conNative;

    public abstract Map selectColumnDetails(String tblName);

    public AppDbInstance appDbInstance;
    public Connection connection;

    public void setParams(Map parameters) {
        this.params = parameters;
    }

    public abstract void setJob(String jobClassName);

    public abstract String getJob();

    public abstract void setContinueExecution(boolean isContinueExecution);

    public void setNativeDbInstance(AppDbInstance nativeInstance) {
        this.nativeDbInstance = nativeInstance;
    }

    protected Map<String, Object> getInitResult() {
        Map result = new HashMap<String, Object>();
        result.put(IS_ERROR, Boolean.FALSE);
        return result;
    }

    public abstract String getConnectionString();

    public Map connect() {
        Map result = getInitResult();
        try {
            if ((connection == null) || connection.isClosed()) {
                Class.forName(appDbInstance.getDriver());
                connection = DriverManager.getConnection(appDbInstance.getConnectionString());
                connection.setAutoCommit(false);
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            result.put(IS_ERROR, Boolean.TRUE);
            result.put(MESSAGE, CONNECTION_ERR_MSG + e.getMessage());
            return result;
        }
    }

    public Map disconnect() {
        Map result = getInitResult();
        try {
            if ((connection != null) && !connection.isClosed()) {
                connection.close();
            }
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            result.put(IS_ERROR, Boolean.TRUE);
            result.put(MESSAGE, e.getMessage());
            return result;
        }
    }

    public Map reconnect() {
        Map result = getInitResult();
        try {
            if ((connection != null) && !connection.isClosed()) {
                connection.close();
            }
            Class.forName(appDbInstance.getDriver());
            connection = DriverManager.getConnection(appDbInstance.getConnectionString());
            connection.setAutoCommit(false);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage());
            result.put(IS_ERROR, Boolean.TRUE);
            result.put(MESSAGE, CONNECTION_ERR_MSG + e.getMessage());
            return result;
        }
    }

    public boolean closeConnection() {
        try {
            if ((connection == null) || connection.isClosed()) return false;

            connection.commit();
            connection.close();
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            System.out.println(e.toString());
            return false;
        }
    }

    public Map testConnection() {
        Map result = getInitResult();
        result.put(IS_ERROR, Boolean.TRUE);
        try {
            Class.forName(appDbInstance.getDriver()).newInstance();
        } catch (ClassNotFoundException cnf) {
            log.error(cnf.getMessage());
            result.put(MESSAGE, CONNECTION_ERR_MSG + cnf.getMessage());
            return result;
        } catch (IllegalAccessException accessError) {
            log.error(accessError.getMessage());
            result.put(MESSAGE, CONNECTION_ERR_MSG + accessError.getMessage());
            return result;
        } catch (InstantiationException ie) {
            log.error(ie.getMessage());
            result.put(MESSAGE, CONNECTION_ERR_MSG + ie.getMessage());
            return result;
        }
        try {
            result = connect();
            return result;
        } catch (Exception se) {
            log.error(se.getMessage());
            result.put(IS_ERROR, Boolean.TRUE);
            result.put(MESSAGE, CONNECTION_ERR_MSG + se.getMessage());
            return result;
        }
    }

    public Map execute(String sql) {
        Map result = getInitResult();
        try {
            createConnection();
            Statement statement = connection.createStatement();
            statement.execute(sql);
            statement.close();
            connection.commit();
            return result;
        } catch (Exception e) {
            result.put(IS_ERROR, Boolean.TRUE);
            result.put(EXCEPTION, e.getMessage());
            rollback();
            return result;
        }
    }

    public Map executeUpdate(String sql) {
        Map result = getInitResult();
        try {
            createConnection();
            Statement statement = connection.createStatement();
            int count = statement.executeUpdate(sql);
            statement.close();
            connection.commit();
            result.put(ROW_COUNT, count);
            return result;
        } catch (Exception e) {
            result.put(IS_ERROR, Boolean.TRUE);
            result.put(EXCEPTION, e.getMessage());
            rollback();
            return result;
        }
    }

    public Map executeSelect(String sql, boolean convertToList) {
        Map result = getInitResult();
        try {
            createConnection();
            Statement statement = connection.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY);

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

    public abstract Map listTable(Map params);

    public abstract Map convertSqlToCsvStream(Map params);

    public abstract String wrapWithTransactionBlock(String script);

    public abstract void writeDataFeed(Map params);

    public abstract void loadFromDataFeed(Map params);

    // rollback if error in sql
    protected void rollback() {
        try {
            if (connection != null || !connection.isClosed()) {
                connection.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void createConnection() throws Exception {
        try {
            getNativeConnection();

            if (connection == null) {
                Class.forName(appDbInstance.getDriver());
                connection = DriverManager.getConnection(appDbInstance.getConnectionString());
                connection.setAutoCommit(false);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            updateAppDbInstanceIsTestedFalse(appDbInstance);
            throw new Exception(e);
        }
    }

    private void getNativeConnection() throws ClassNotFoundException, SQLException {
        if (conNative == null && nativeDbInstance != null) {
            Class.forName(nativeDbInstance.getDriver());
            conNative = DriverManager.getConnection(nativeDbInstance.getConnectionString());
        }
    }

    protected List convertResultSetToArrayList(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int columns = md.getColumnCount();
        ArrayList<HashMap> list = new ArrayList<HashMap>();
        while (rs.next()) {
            HashMap<String, Object> row = new HashMap<String, Object>(columns);
            for (int i = 1; i <= columns; ++i) {
                row.put(md.getColumnName(i).toLowerCase(), rs.getObject(i));
            }
            list.add(row);
        }
        return list;
    }

    protected String getColumnNamesSql(ResultSet rs, String columnSeparator, String quoteCharacter) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount();
        if (columnSeparator.equals(TAB_STRING)) {
            columnSeparator = TAB_CHAR;
        }
        String colNames = EMPTY_SPACE;
        for (int i = 1; i <= colCount; ++i) {
            colNames = colNames + quoteCharacter + md.getColumnName(i) + quoteCharacter + columnSeparator;
        }
        return colNames.substring(0, colNames.length() - columnSeparator.length()) + NEW_LINE;
    }

    protected Map executeCountNative(String sql) {
        Map result = getInitResult();
        try {
            Statement statementNative = conNative.createStatement();
            ResultSet rs = statementNative.executeQuery(sql);
            rs.next();
            long count = rs.getLong(1);
            statementNative.close();
            result.put(COUNT, count);
            result.put(IS_ERROR, Boolean.FALSE);
            return result;
        } catch (SQLException e) {
            log.error(e.getMessage());
            result.put(IS_ERROR, Boolean.TRUE);
            result.put(EXCEPTION, e.getMessage());
            return result;
        }
    }

    protected void saveTransactionLog(TransactionLog tLog) {    //@todo: parameterize
        try {
            String comment = (tLog.getComment() != null) ? SINGLE_QUOTE + tLog.getComment() + SINGLE_QUOTE : null;
            String exception = (tLog.getException() != null) ? SINGLE_QUOTE + tLog.getException() + SINGLE_QUOTE : null;
            String sql = "INSERT INTO transaction_log(id,sequence,entity_type_id,entity_id,total_record,record_per_batch,time_to_read,time_to_write," +
                    "processing_time,start_time,end_time,table_name,exception,comment,company_id,created_on) VALUES(nextval('transaction_log_id_seq')" +
                    COMA + tLog.getSequence() + COMA + tLog.getEntityTypeId() + COMA + tLog.getEntityId() +
                    COMA + tLog.getTotalRecord() + COMA + tLog.getRecordPerBatch() + COMA + tLog.getTimeToRead() + COMA + tLog.getTimeToWrite() +
                    COMA + tLog.getProcessingTime() + COMA + tLog.getStartTime() + COMA + tLog.getEndTime() + ",'" + tLog.getTableName() + SINGLE_QUOTE_AND_COMA +
                    exception + COMA + comment + COMA + tLog.getCompanyId() + COMA + "?);";
            PreparedStatement psTransactionLog = conNative.prepareStatement(sql);
            psTransactionLog.setTimestamp(1, new Timestamp(tLog.getCreatedOn().getTime()));
            psTransactionLog.execute();
        } catch (Exception e) {
            log.error(e.getMessage());
            System.out.println(e.toString());
        }
    }

    protected void updateParentObject(String entityDomainName, long entityId, Date endTime, boolean isOperationCompleted, String exception) {
        try {
            String sql = "UPDATE " + entityDomainName + " SET start_time =" + (endTime != null ? "start_time" : "null") + ", end_time=? , is_operation_completed = ? , exception = ? WHERE id=" + entityId;
            //@todo: format date
            PreparedStatement psEntityDomain = conNative.prepareStatement(sql);
            if (endTime != null) {
                psEntityDomain.setTimestamp(1, new Timestamp(endTime.getTime()));
            } else {
                psEntityDomain.setTimestamp(1, null);
            }
            psEntityDomain.setBoolean(2, isOperationCompleted);
            psEntityDomain.setString(3, exception);
            psEntityDomain.execute();
        } catch (Exception e) {
            log.error(e.getMessage());
            System.out.println(e.toString());
        }
    }

    protected void updateFieldValue(String strQuery) {
        try {
            PreparedStatement statement = conNative.prepareStatement(strQuery);
            statement.execute();
        } catch (Exception e) {
            log.error(e.getMessage());
            System.out.println(e.toString());
        }
    }

    protected void writeDataAndCloseBuffer(BufferedWriter bufferedWriter, String fileContent) throws IOException {
        bufferedWriter.write(fileContent);
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    protected void sendMail(long logId, String url, boolean isExecuted, long companyId) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            String params = "logId=" + logId + "&isExecuted=" + isExecuted + "&companyId=" + companyId;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(params);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            InputStream inputStream;
            if ((responseCode < 200) || (responseCode > 202)) {
                inputStream = con.getErrorStream();
                String output = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
                log.error(output);
            }
            con.disconnect();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    protected void sendExportImportMail(long id, String url, long companyId, long entityTypeId) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            String params = "id=" + id + "&companyId=" + companyId + "&entityTypeId=" + entityTypeId;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(params);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            InputStream inputStream;
            if ((responseCode < 200) || (responseCode > 202)) {
                inputStream = con.getErrorStream();
                String output = new Scanner(inputStream, "UTF-8").useDelimiter("\\A").next();
                log.error(output);
            }
            con.disconnect();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    // update AppDbInstance isTested false if create connection fail.
    protected void updateAppDbInstanceIsTestedFalse(AppDbInstance appDbInstance) {
        try {
            AppDbInstance nativeDb = AppDbInstance.findByIsNativeAndCompanyId(true, appDbInstance.companyId, [readOnly: true])
            setNativeDbInstance(nativeDb);
            getNativeConnection();
            String sql = "UPDATE app_db_instance SET is_tested = FALSE WHERE id=" + appDbInstance.id;
            Statement statementNative = conNative.createStatement();
            statementNative.execute(sql);
            statementNative.close()
        } catch (Exception e) {
            log.error(e.getMessage());
            System.out.println(e.toString());
        }
    }
}
