package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.Benchmark
import com.athena.mis.application.entity.Sampling8F

class Sampling8FService extends BaseDomainService {

    @Override
    public void init() {
        domainClass = Sampling8F.class
    }

    @Override
    public void createDefaultSchema() {}

    private static final String field1 = "Benchmarking is the process of comparing ones business processes and performance''s metrics to industry bests or best practices from other companies. Dimensions typically measured are quality, time and cost. There are many practices of bench"
    private static final String field2 = "In the process of best practice benchmarking, management identifies the best firms in their industry, or in another industry where similar processes exist, and compares the results and processes of those studied to ones own results and proc"
    private static final String field3 = "In this way, they learn how well the targets perform and, more importantly, the business processes that explain why these firms are successful. Benchmarking is used to measure performance using a specific indicator (cost per unit of measure"
    private static final String field4 = "Companies processes, usually within a peer group defined for the purposes of comparison. This then allows organizations to develop plans on how to make improvement or adapt specific best practice, with the aim of increasing aspect of perfor"
    private static final String field5 = "Also referred to as best practice benchmarking ( or process benchmarking ), this process is used in management''s and particularly strategic managements, in which organizations evaluate various aspects of their processes in relation to best"

    private static final long field6 = -584652415L
    private static final double field7 = 7412562.369256d
    private static final float field8 = 326398.452f


    public String getSampling8FSql(Benchmark benchmark, long seed) {
        long limit = benchmark.recordPerBatch + seed
        StringBuffer sql = new StringBuffer("INSERT INTO sampling8f (field1, field2, field3, field4, field5, field6, field7, field8) VALUES ")
        long j = 0L
        for (long i = seed; i < limit; i++) {
            String count = i.toString()
            String s = "('${field1 + count}', '${field2 + count}', '${field3 + count}', '${field4 + count}', '${field5 + count}', '${field6 + j + seed}', '${field7 + i}', '${field8 + i}'),"
            sql.append(s)
            j++
        }
        return sql.substring(0, sql.length() - 1)
    }

    public void create(String sql) {
        List insertResult = executeInsertSql(sql)
        if (insertResult.size() <= 0) {
            throw new RuntimeException('Error occurred while insert sampling8f information')
        }
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
