package com.athena.mis

import com.athena.mis.application.entity.Benchmark
import com.athena.mis.application.entity.BenchmarkStar

public interface SamplingIntf {

    public static final String STR240_1 = "Benchmarking is the process of comparing ones business processes and performance''s metrics to industry bests or best practices from other companies. Dimensions typically measured are quality, time and cost. There are many practices of bench"
    public static final String STR240_2 = "In the process of best practice benchmarking, management identifies the best firms in their industry, or in another industry where similar processes exist, and compares the results and processes of those studied to ones own results and proc"
    public static final String STR240_3 = "In this way, they learn how well the targets perform and, more importantly, the business processes that explain why these firms are successful. Benchmarking is used to measure performance using a specific indicator (cost per unit of measure"
    public static final String STR240_4 = "Companies processes, usually within a peer group defined for the purposes of comparison. This then allows organizations to develop plans on how to make improvement or adapt specific best practice, with the aim of increasing aspect of perfor"
    public static final String STR240_5 = "Also referred to as best practice benchmarking ( or process benchmarking ), this process is used in management''s and particularly strategic managements, in which organizations evaluate various aspects of their processes in relation to best"
    public static final String STR240_6 = "There is no single benchmarking process that has been universally adopted. The wide appeal and acceptance of benchmarking has led to the emergence of benchmarking methodologies. One seminal book is Benchmarking  for Competitive  Advantage ("
    public static final String STR240_7 = "The first book on benchmarking, written and published by Kaiser Associates, is a practical guide and offers  seven-step approach. Robert Camp (wrote one of the earliest books on benchmarking in 1989) developed a 12-stage approach to benchma"
    public static final String STR240_8 = "Because benchmarking can be applied to any business process or function, a range of research techniques may be required.They include informal conversations with customers,employees, or simply reviewing cycle times or other performance indic"
    public static final String STR240_9 = "The cost of benchmarking can substantially be reduced through utilizing the many internet resources that have sprung up over the last few years. These aim to capture benchmarks and best practices from organizations,business sectors and coun"
    public static final String STR240_10 = "Benchmarking can be internal ( comparing performance between different groups or teams within an organization ) or external (comparing performance with companies in a specific industry or across industries). There are three types of benchma"

    public static final long LONG_1 = -584652415L
    public static final long LONG_2 = 2369256323L

    public static final float FLT_1 = 326398.452f
    public static final float FLT_2 = -63256.2563f

    public static final double DOUBLE_1 = 7412562.369256d
    public static final double DOUBLE_2 = -3674123698.2125626d

    public abstract String getSql(Benchmark benchmark, long seed)
    public abstract Map getSql(BenchmarkStar benchmark, long seed)
    public abstract String getSql(BenchmarkStar benchmark, long seed, List<Long> lstSmallIds, List<Long> lstMediumIds)
}
