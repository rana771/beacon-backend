package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.Benchmark
import com.athena.mis.application.entity.BenchmarkStar
import com.athena.mis.application.entity.TransactionLog
import com.athena.mis.utility.DateUtility

class TransactionLogService extends BaseDomainService {

    @Override
    public void init() {
        domainClass = TransactionLog.class
    }

    public int countByCompanyIdAndEntityTypeIdAndEntityId(long companyId, long entityTypeId, long entityId) {
        int resultCount = TransactionLog.countByCompanyIdAndEntityTypeIdAndEntityId(companyId, entityTypeId, entityId)
        return resultCount
    }

    public int countByEntityTypeIdAndEntityId(long entityTypeId, long entityId) {
        int resultCount = TransactionLog.countByEntityTypeIdAndEntityId(entityTypeId, entityId)
        return resultCount
    }

    public int countByEntityTypeIdAndEntityIdAndTableName(long entityTypeId, long entityId, String tblName) {
        int resultCount = TransactionLog.countByEntityTypeIdAndEntityIdAndTableName(entityTypeId, entityId, tblName)
        return resultCount
    }

    public int countByEntityTypeIdAndEntityIdAndTableNameIlike(long entityTypeId, long entityId, String tblName) {
        int resultCount = TransactionLog.countByEntityTypeIdAndEntityIdAndTableNameIlike(entityTypeId, entityId, tblName)
        return resultCount
    }

    public List<TransactionLog> findAllByEntityTypeIdAndEntityId(long entityTypeId, long entityId) {
        List<TransactionLog> listTable = TransactionLog.findAllByEntityTypeIdAndEntityId(entityTypeId, entityId, [readOnly: true])
        return listTable
    }

    private static final String DELETE_ALL_LOG_QUERY = """
        DELETE FROM transaction_log
        WHERE company_id=:companyId
        AND entity_type_id =:entityTypeId
        AND entity_id = :entityId
    """

    public boolean deleteAllByCompanyAndEntityTypeAndEntity(long companyId, long entityTypeId, long entityId) {
        Map queryParams = [companyId: companyId, entityTypeId: entityTypeId, entityId: entityId]
        return executeSql(DELETE_ALL_LOG_QUERY, queryParams)
    }

    private static final String DELETE_ALL_LOG_QUERY_BY_TABLE_NAME = """
        DELETE FROM transaction_log
        WHERE entity_type_id =:entityTypeId
        AND entity_id = :entityId
        AND table_name = :tableName
    """

    public boolean deleteAllByEntityTypeAndEntityAndTable(long entityTypeId, long entityId, String tblName) {
        Map queryParams = [entityTypeId: entityTypeId, entityId: entityId, tableName: tblName]
        return executeSql(DELETE_ALL_LOG_QUERY_BY_TABLE_NAME, queryParams)
    }

    public void createLog(TransactionLog transactionLog) {
        transactionLog.save()
    }


    public void create(TransactionLog log) {
        Map params = [
                comment       : log.comment,
                companyId     : log.companyId,
                createdOn     : DateUtility.getSqlDateWithSeconds(log.createdOn),
                endTime       : log.endTime,
                entityId      : log.entityId,
                entityTypeId  : log.entityTypeId,
                exception     : log.exception,
                processingTime: log.processingTime,
                recordPerBatch: log.recordPerBatch,
                sequence      : log.sequence,
                startTime     : log.startTime,
                tableName     : log.tableName,
                timeToRead    : log.timeToRead,
                timeToWrite   : log.timeToWrite,
                totalRecord   : log.totalRecord
        ]
        String query = """
                    INSERT INTO transaction_log(
                            id, comment, company_id, created_on, end_time, entity_id, entity_type_id,
                            exception, processing_time, record_per_batch, sequence, start_time,
                            table_name, time_to_read, time_to_write, total_record)
                    VALUES (nextVal('transaction_log_id_seq'), :comment, :companyId, :createdOn, :endTime, :entityId,
                            :entityTypeId, :exception, :processingTime, :recordPerBatch, :sequence, :startTime,
                            :tableName, :timeToRead, :timeToWrite, :totalRecord);
                """
        List insertResult = executeInsertSql(query, params)

        if (insertResult.size() <= 0) {
            throw new RuntimeException('Error occurred while insert transaction log information')
        }

    }

    public Map getTotalRecordToBeExecuted(Benchmark benchmark, long entityTypeId) {
        List<Long> result = TransactionLog.withCriteria {
            eq("companyId", benchmark.companyId)
            eq("entityId", benchmark.id)
            eq("entityTypeId", entityTypeId)
            projections {
                max("totalRecord")
                max("sequence")
            }
        }
        long totalRecord = 0
        long sequence = 0
        if (result[0][0]) {
            totalRecord = result[0][0] as long
            sequence = result[0][1] as long
        }
        return [totalRecord: benchmark.totalRecord - totalRecord, sequence: sequence]
    }

    public Map getTotalRecordToBeExecuted(BenchmarkStar benchmarkStar, long entityTypeId) {
        List<Long> result = TransactionLog.withCriteria {
            eq("companyId", benchmarkStar.companyId)
            eq("entityId", benchmarkStar.id)
            eq("entityTypeId", entityTypeId)
            projections {
                max("totalRecord")
                max("sequence")
            }
        }
        long totalRecord = 0
        long sequence = 0
        if (result[0][0]) {
            totalRecord = result[0][0] as long
            sequence = result[0][1] as long
        }
        return [totalRecord: benchmarkStar.totalRecord - totalRecord, sequence: sequence]
    }

    @Override
    public void createDefaultSchema() {}

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }

}
