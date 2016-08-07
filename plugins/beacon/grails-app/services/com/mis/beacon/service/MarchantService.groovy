package com.mis.beacon.service

import com.mis.beacon.Marchant
import com.athena.mis.BaseDomainService
import org.springframework.transaction.annotation.Transactional

class MarchantService extends BaseDomainService {

    @Override
    public void init() {
        domainClass = Marchant.class
    }


    /**
     * Get list of Marchant by list of ids
     * @param lstMarchantIds - list of AppGroup.id
     * @return - list of Marchant by ids
     */
    public List<Marchant> findAllByIdInList(List<Long> lstMarchantIds) {
        List<Marchant> lstMarchant = Marchant.findAllByIdInList(lstMarchantIds, [sort: Marchant.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstMarchant
    }

    @Override
    public List<Marchant> list() {
        return Marchant.listOrderByName();
    }

    private static final String UPDATE_CONTENT_COUNT_QUERY = """
        UPDATE marchant
        SET content_count = content_count + :contentCount,
        version = version + 1
        WHERE
        id=:id
    """

    // update content count for marchant during create, update and delete content
    public int updateContentCountForMarchant(long marchantId, int count) {
        Map queryParams = [
                contentCount: count,
                id          : marchantId
        ]
        int updateCount = executeUpdateSql(UPDATE_CONTENT_COUNT_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Content count updated for Marchant")
        }
        return updateCount
    }

    @Transactional
    public void delete(Long id) {
        try {
            String query = """
                    DELETE FROM marchant
                      WHERE id=${id}
                    """
            int deleteCount = executeUpdateSql(query)
            if (deleteCount <= 0) {
                throw new RuntimeException("Error occurred while deleting Marchant.")
            }
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index marchant_name_company_id_idx on marchant(lower(name),company_id);"
        executeSql(nameIndex)
        String codeIndex = "create unique index marchant_code_company_id_idx on marchant(lower(code),company_id);"
        executeSql(codeIndex)
    }

       private static final String INSERT_QUERY = """
                INSERT INTO marchant( id, version, name, code, description, content_count, start_date, end_date, company_id,
                created_by, created_on, updated_by, is_approve_in_from_supplier, is_approve_in_from_inventory,
                is_approve_inv_out, is_approve_consumption, is_approve_production)
                VALUES (:id, :version, :name, :code, :description, :contentCount, :startDate, :endDate, :companyId, :createdBy,
                :createdOn, :updatedBy, :isApproveInFromSupplier, :isApproveInFromInventory, :isApproveInvOut, :isApproveConsumption,
                :isApproveProduction);
        """

    @Override
    public boolean createTestData(long companyId, long userId) {
        Marchant marchant1 = new Marchant(name: "Dhaka Flyover", code: "DF", description: 'A Flyover all over the dhaka', contentCount: 0, startDate: new Date(), endDate: new Date() + 30, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        Marchant marchant2 = new Marchant(name: "School Construction", code: "SC", description: 'Construction of 4 storied school building', contentCount: 0, startDate: new Date() - 7, endDate: new Date() + 60, companyId: companyId, createdBy: userId, createdOn: new Date() - 7, updatedBy: 0)
        Marchant marchant3 = new Marchant(name: "Roads Construction", code: "RC", description: 'Construction of Dhaka Chittagong 6 Lane Highway', contentCount: 0, startDate: new Date() - 25, endDate: new Date() + 90, companyId: companyId, createdBy: userId, createdOn: new Date() - 25, updatedBy: 0)

        runSqlForCreateTestData(marchant1)
        runSqlForCreateTestData(marchant2)
        runSqlForCreateTestData(marchant3)
        return true
    }

    public void runSqlForCreateTestData(Marchant parameter) {

    }
}
