package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.Benchmark
import com.athena.mis.utility.DateUtility

class BenchmarkService extends BaseDomainService {

    static transactional = true

    public static boolean CONTINUE_EXECUTION = true

    @Override
    public void init() {
        domainClass = Benchmark.class
    }

    @Override
    public List<Benchmark> list() {
        long companyId = getCompanyId()
        List<Benchmark> lstBenchmark = Benchmark.findAllByCompanyId(companyId, [sort: Benchmark.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstBenchmark
    }

    public int countByNameIlikeAndCompanyId(String name) {
        return Benchmark.countByNameIlikeAndCompanyId(name, companyId)
    }

    public int countByNameIlikeAndCompanyIdAndIdNotEqual(String name, long id) {
        return Benchmark.countByNameIlikeAndCompanyIdAndIdNotEqual(name, companyId, id)
    }

    public void updateBenchMarkObject(long id, Date endTime) {
        Map params = [endTime: DateUtility.getSqlDateWithSeconds(endTime)]
        String updateQuery = """ UPDATE benchmark SET end_time = :endTime where id = ${id} """
        int updateCount = executeUpdateSql(updateQuery, params)
        if (updateCount <= 0) {
            throw new RuntimeException("Benchmark could not updated")
        }
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index benchmark_name_company_id_idx on benchmark(lower(name),company_id);"
        executeSql(nameIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
