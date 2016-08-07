package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.Benchmark
import com.athena.mis.application.entity.Sampling100F

class Sampling100FService extends BaseDomainService {

    @Override
    public void init() {
        domainClass = Sampling100F.class
    }

    @Override
    public void createDefaultSchema() {}

    private static final String STR240_1 = "Benchmarking is the process of comparing ones business processes and performance''s metrics to industry bests or best practices from other companies. Dimensions typically measured are quality, time and cost. There are many practices of bench"
    private static final String STR240_2 = "In the process of best practice benchmarking, management identifies the best firms in their industry, or in another industry where similar processes exist, and compares the results and processes of those studied to ones own results and proc"
    private static final String STR240_3 = "In this way, they learn how well the targets perform and, more importantly, the business processes that explain why these firms are successful. Benchmarking is used to measure performance using a specific indicator (cost per unit of measure"
    private static final String STR240_4 = "Companies processes, usually within a peer group defined for the purposes of comparison. This then allows organizations to develop plans on how to make improvement or adapt specific best practice, with the aim of increasing aspect of perfor"
    private static final String STR240_5 = "Also referred to as best practice benchmarking ( or process benchmarking ), this process is used in management''s and particularly strategic managements, in which organizations evaluate various aspects of their processes in relation to best"
    private static final String STR240_6 = "There is no single benchmarking process that has been universally adopted. The wide appeal and acceptance of benchmarking has led to the emergence of benchmarking methodologies. One seminal book is Benchmarking  for Competitive  Advantage ("
    private static final String STR240_7 = "The first book on benchmarking, written and published by Kaiser Associates, is a practical guide and offers  seven-step approach. Robert Camp (wrote one of the earliest books on benchmarking in 1989) developed a 12-stage approach to benchma"
    private static final String STR240_8 = "Because benchmarking can be applied to any business process or function, a range of research techniques may be required.They include informal conversations with customers,employees, or simply reviewing cycle times or other performance indic"
    private static final String STR240_9 = "The cost of benchmarking can substantially be reduced through utilizing the many internet resources that have sprung up over the last few years. These aim to capture benchmarks and best practices from organizations,business sectors and coun"
    private static final String STR240_10 = "Benchmarking can be internal ( comparing performance between different groups or teams within an organization ) or external (comparing performance with companies in a specific industry or across industries). There are three types of benchma"

    private static final long LONG_1 = -584652415L
    private static final long LONG_2 = 2369256323L

    private static final float FLT_1 = 326398.452f
    private static final float FLT_2 = -63256.2563f

    private static final double DOUBLE_1 = 7412562.369256d
    private static final double DOUBLE_2 = -3674123698.2125626d

    private String field1 = STR240_1
    private String field2 = STR240_2
    private String field3 = STR240_3
    private String field4 = STR240_4
    private String field5 = STR240_5
    private String field6 = STR240_6
    private String field7 = STR240_7
    private String field8 = STR240_8
    private String field9 = STR240_9
    private String field10 = STR240_10
    private String field11 = STR240_1
    private String field12 = STR240_2
    private String field13 = STR240_3
    private String field14 = STR240_4
    private String field15 = STR240_5
    private String field16 = STR240_6
    private String field17 = STR240_7
    private String field18 = STR240_8
    private String field19 = STR240_9
    private String field20 = STR240_10
    private String field21 = STR240_1
    private String field22 = STR240_2
    private String field23 = STR240_3
    private String field24 = STR240_4
    private String field25 = STR240_5
    private String field26 = STR240_6
    private String field27 = STR240_7
    private String field28 = STR240_8
    private String field29 = STR240_9
    private String field30 = STR240_10
    private String field31 = STR240_1
    private String field32 = STR240_2
    private String field33 = STR240_3
    private String field34 = STR240_4
    private String field35 = STR240_5
    private String field36 = STR240_6
    private String field37 = STR240_7
    private String field38 = STR240_8
    private String field39 = STR240_9
    private String field40 = STR240_10
    private String field41 = STR240_1
    private String field42 = STR240_2
    private String field43 = STR240_3
    private String field44 = STR240_4
    private String field45 = STR240_5
    private String field46 = STR240_6
    private String field47 = STR240_7
    private String field48 = STR240_8
    private String field49 = STR240_9
    private String field50 = STR240_10
    private String field51 = STR240_1
    private String field52 = STR240_2
    private String field53 = STR240_3
    private String field54 = STR240_4
    private String field55 = STR240_5
    private String field56 = STR240_6
    private String field57 = STR240_7
    private String field58 = STR240_8
    private String field59 = STR240_9
    private String field60 = STR240_10
    private String field61 = STR240_1
    private String field62 = STR240_2
    private String field63 = STR240_3
    private long field64 = LONG_1
    private long field65 = LONG_2
    private long field66 = LONG_1
    private long field67 = LONG_2
    private long field68 = LONG_1
    private long field69 = LONG_2
    private long field70 = LONG_1
    private long field71 = LONG_2
    private long field72 = LONG_1
    private long field73 = LONG_2
    private long field74 = LONG_1
    private long field75 = LONG_2
    private long field76 = LONG_1
    private long field77 = LONG_2
    private long field78 = LONG_1
    private float field79 = FLT_1
    private float field80 = FLT_2
    private float field81 = FLT_1
    private float field82 = FLT_2
    private float field83 = FLT_1
    private float field84 = FLT_2
    private float field85 = FLT_1
    private double field86 = DOUBLE_1
    private double field87 = DOUBLE_2
    private double field88 = DOUBLE_1
    private double field89 = DOUBLE_2
    private double field90 = DOUBLE_1
    private double field91 = DOUBLE_2
    private double field92 = DOUBLE_1
    private double field93 = DOUBLE_2
    private double field94 = DOUBLE_1
    private double field95 = DOUBLE_2
    private double field96 = DOUBLE_1
    private boolean field97 = true
    private boolean field98
    private boolean field99 = true
    private boolean field100

    public String getSampling100FSql(Benchmark benchmark, long seed) {
        long limit = benchmark.recordPerBatch + seed
        StringBuffer sql = new StringBuffer("""INSERT INTO sampling100f (field1, field2, field3, field4, field5, field6, field7, field8, field9, field10,
                field11, field12, field13, field14, field15, field16, field17, field18, field19, field20,
                field21, field22, field23, field24, field25, field26, field27, field28, field29, field30,
                field31, field32, field33, field34, field35, field36, field37, field38, field39, field40,
                field41, field42, field43, field44, field45, field46, field47, field48, field49, field50,
                field51, field52, field53, field54, field55, field56, field57, field58, field59, field60,
                field61, field62, field63, field64, field65, field66, field67, field68, field69, field70,
                field71, field72, field73, field74, field75, field76, field77, field78, field79, field80,
                field81, field82, field83, field84, field85, field86, field87, field88, field89, field90,
                field91, field92, field93, field94, field95, field96, field97, field98, field99, field100) VALUES """)
        long j = 0L
        for (long i = seed; i < limit; i++) {
            String count = i.toString()
            String s = """('${field1 + count}', '${field2 + count}', '${field3 + count}', '${field4 + count}', '${field5 + count}', '${field6 + count}', '${field7 + count}', '${field8 + count}', '${field9 + count}', '${field10 + count}',
                   '${field11 + count}', '${field12 + count}', '${field13 + count}', '${field14 + count}', '${field15 + count}', '${field16 + count}', '${field17 + count}', '${field18 + count}', '${field19 + count}', '${field20 + count}',
                   '${field21 + count}', '${field22 + count}', '${field23 + count}', '${field24 + count}', '${field25 + count}', '${field26 + count}', '${field27 + count}', '${field28 + count}', '${field29 + count}', '${field30 + count}',
                   '${field31 + count}', '${field32 + count}', '${field33 + count}', '${field34 + count}', '${field35 + count}', '${field36 + count}', '${field37 + count}', '${field38 + count}', '${field39 + count}', '${field40 + count}',
                   '${field41 + count}', '${field42 + count}', '${field43 + count}', '${field44 + count}', '${field45 + count}', '${field46 + count}', '${field47 + count}', '${field48 + count}', '${field49 + count}', '${field50 + count}',
                   '${field51 + count}', '${field52 + count}', '${field53 + count}', '${field54 + count}', '${field55 + count}', '${field56 + count}', '${field57 + count}', '${field58 + count}', '${field59 + count}', '${field60 + count}',
                   '${field61 + count}', '${field62 + count}', '${field63 + count}', '${field64 + j + seed}', '${field65 + i}', '${field66 + i}', '${field67 + i}', '${field68 + i}', '${field69 + i}', '${field70 + i}',
                   '${field71 + i}', '${field72 + i}', '${field73 + i}', '${field74 + i}', '${field75 + i}', '${field76 + i}', '${field77 + i}', '${field78 + i}', '${field79 + i}', '${field80 + i}',
                   '${field81 + i}', '${field82 + i}', '${field83 + i}', '${field84 + i}', '${field85 + i}', '${field86 + i}', '${field87 + i}', '${field88 + i}', '${field89 + i}', '${field90 + i}',
                   '${field91 + i}', '${field92 + i}', '${field93 + i}', '${field94 + i}', '${field95 + i}', '${field96 + i}', ${field97}, ${field98}, ${field99}, ${field100}),"""
            sql.append(s)
            j++
        }
        return sql.substring(0, sql.length() - 1)
    }

    public void create(String sql) {
        List insertResult = executeInsertSql(sql)
        if (insertResult.size() <= 0) {
            throw new RuntimeException('Error occurred while insert sampling100f information')
        }
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
