package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.model.TestDataModel

class TestDataModelService extends BaseService {

    private static final String SELECT_TEST_DATA_MODEL_SERVICE = """
        SELECT nextval('test_data_id_seq');
    """

    private static final String SQL_SEQ_TEST_DATA = """
        CREATE SEQUENCE test_data_id_seq
        INCREMENT -1
        MINVALUE -9223372036854775807
        MAXVALUE -1
        START -1;
    """

    public void createDefaultSchema() {
        executeSql(TestDataModel.SQL_TEST_DATA_MODEL)
        executeSql(SQL_SEQ_TEST_DATA)
    }

    public Long getNextIdForTestData() {
        List result = executeSelectSql(SELECT_TEST_DATA_MODEL_SERVICE)
        Long customerID = (Long) result[0][0]
        return customerID
    }
}
