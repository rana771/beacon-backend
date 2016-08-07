package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.BenchmarkStar
import com.athena.mis.utility.DateUtility

class BenchmarkStarService extends BaseDomainService {

    static transactional = true

    public static boolean CONTINUE_EXECUTION = true

    @Override
    public void init() {
        domainClass = BenchmarkStar.class
    }

    @Override
    public List<BenchmarkStar> list() {
        long companyId = getCompanyId()
        List<BenchmarkStar> lstBenchmark = BenchmarkStar.findAllByCompanyId(companyId, [sort: BenchmarkStar.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstBenchmark
    }

    public int countByNameIlikeAndCompanyId(String name) {
        return BenchmarkStar.countByNameIlikeAndCompanyId(name, companyId)
    }

    public int countByNameIlikeAndCompanyIdAndIdNotEqual(String name, long id) {
        return BenchmarkStar.countByNameIlikeAndCompanyIdAndIdNotEqual(name, companyId, id)
    }

    public int countByDbInstanceId(long dbInstanceId) {
        int count = BenchmarkStar.countByDbInstanceId(dbInstanceId)
        return count
    }

    public void updateBenchmarkStarObject(long id, Date endTime) {
        Map params = [endTime: DateUtility.getSqlDateWithSeconds(endTime)]
        String updateQuery = """ UPDATE benchmark_star SET end_time = :endTime where id = ${id} """
        executeUpdateSql(updateQuery, params)
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index benchmark_star_name_company_id_idx on benchmark_star(lower(name),company_id);"
        executeSql(nameIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
