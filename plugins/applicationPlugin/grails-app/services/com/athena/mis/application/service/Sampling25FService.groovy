package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.Benchmark
import com.athena.mis.application.entity.Sampling25F

class Sampling25FService extends BaseDomainService {

    @Override
    public void init() {
        domainClass = Sampling25F.class
    }

    @Override
    public void createDefaultSchema() {}

    private static
    final String field1 = "Benchmarking is the process of comparing ones business processes and performance''s metrics to industry bests or best practices from other companies. Dimensions typically measured are quality, time and cost. There are many practices of bench"
    private static
    final String field11 = "Benchmarking is the process of comparing ones business processes and performance''s metrics to industry bests or best practices from other companies. Dimensions typically measured are quality, time and cost. There are many practices of bench"
    private static
    final String field2 = "In the process of best practice benchmarking, management identifies the best firms in their industry, or in another industry where similar processes exist, and compares the results and processes of those studied to ones own results and proc"
    private static
    final String field12 = "In the process of best practice benchmarking, management identifies the best firms in their industry, or in another industry where similar processes exist, and compares the results and processes of those studied to ones own results and proc"
    private static
    final String field3 = "In this way, they learn how well the targets perform and, more importantly, the business processes that explain why these firms are successful. Benchmarking is used to measure performance using a specific indicator (cost per unit of measure"
    private static
    final String field13 = "In this way, they learn how well the targets perform and, more importantly, the business processes that explain why these firms are successful. Benchmarking is used to measure performance using a specific indicator (cost per unit of measure"
    private static
    final String field4 = "Companies processes, usually within a peer group defined for the purposes of comparison. This then allows organizations to develop plans on how to make improvement or adapt specific best practice, with the aim of increasing aspect of perfor"
    private static
    final String field14 = "Companies processes, usually within a peer group defined for the purposes of comparison. This then allows organizations to develop plans on how to make improvement or adapt specific best practice, with the aim of increasing aspect of perfor"
    private static
    final String field5 = "Also referred to as best practice benchmarking ( or process benchmarking ), this process is used in management''s and particularly strategic managements, in which organizations evaluate various aspects of their processes in relation to best"
    private static
    final String field15 = "Also referred to as best practice benchmarking ( or process benchmarking ), this process is used in management''s and particularly strategic managements, in which organizations evaluate various aspects of their processes in relation to best"
    private static
    final String field6 = "There is no single benchmarking process that has been universally adopted. The wide appeal and acceptance of benchmarking has led to the emergence of benchmarking methodologies. One seminal book is Benchmarking  for Competitive  Advantage ("
    private static
    final String field7 = "The first book on benchmarking, written and published by Kaiser Associates, is a practical guide and offers  seven-step approach. Robert Camp (wrote one of the earliest books on benchmarking in 1989) developed a 12-stage approach to benchma"
    private static
    final String field8 = "Because benchmarking can be applied to any business process or function, a range of research techniques may be required.They include informal conversations with customers,employees, or simply reviewing cycle times or other performance indic"
    private static
    final String field9 = "The cost of benchmarking can substantially be reduced through utilizing the many internet resources that have sprung up over the last few years. These aim to capture benchmarks and best practices from organizations,business sectors and coun"
    private static
    final String field10 = "Benchmarking can be internal ( comparing performance between different groups or teams within an organization ) or external (comparing performance with companies in a specific industry or across industries). There are three types of benchma"

    private static final long field16 = -584652415L
    private static final long field18 = -584652415L
    private static final long field20 = -584652415L
    private static final long field17 = 2369256323L
    private static final long field19 = 2369256323L

    private static final float field23 = 326398.452f
    private static final float field24 = -63256.2563f

    private static final double field21 = 7412562.369256d
    private static final double field22 = -3674123698.2125626d

    boolean field25

    public String getSampling25FSql(Benchmark benchmark, long seed) {
        long limit = benchmark.recordPerBatch + seed
        StringBuffer sql = new StringBuffer("""INSERT INTO sampling25f (field1, field2, field3, field4, field5, field6, field7, field8,
                field9, field10, field11, field12, field13, field14, field15, field16, field17, field18, field19, field20, field21, field22,
                field23, field24, field25) VALUES """)
        long j = 0L
        for (long i = seed; i < limit; i++) {
            String count = i.toString()
            String s = """('${field1 + count}', '${field2 + count}', '${field3 + count}', '${field4 + count}', '${
                field5 + count
            }','${field6 + count}', '${field7 + count}', '${field8 + count}', '${field9 + count}', '${field10 + count}',
                    '${field11 + count}', '${field12 + count}', '${field13 + count}', '${field14 + count}', '${
                field15 + count
            }','${field16 + j + seed}', '${field17 + i}', '${field18 + i}', '${field19 + i}', '${field20 + i}','${
                field21 + i
            }', '${field22 + i}', '${field23 + i}', '${field24 + i}', ${field25}),"""
            sql.append(s)
            j++
        }
        return sql.substring(0, sql.length() - 1)
    }

    public void create(String sql) {
        List insertResult = executeInsertSql(sql)
        if (insertResult.size() <= 0) {
            throw new RuntimeException('Error occurred while insert sampling25 information')
        }
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
