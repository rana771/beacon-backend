package com.athena.mis.application.entity

class Benchmark {

    public static final String DEFAULT_SORT_FIELD = "name"

    long id
    long version
    String name
    long totalRecord
    long recordPerBatch
    boolean isSimulation
    Date startTime
    Date endTime
    long volumeTypeId
    long companyId

    static mapping = {
        id generator: 'sequence', params: [sequence: 'benchmark_id_seq']

        // unique index on "name" using BenchmarkService.createDefaultSchema()
        // <domain_name><property_name_1>idx
    }

    static constraints = {
        startTime(nullable: true)
        endTime(nullable: true)
    }

    public long getDuration() {
        return (this.endTime.getTime() - this.startTime.getTime())
    }
}
