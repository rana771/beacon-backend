package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.DateUtility

/**
 *  Service class for basic supplier CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'SupplierService'
 */
class SupplierService extends BaseDomainService {

    private static final String COMPANY_ID_COLUMN = "companyId";

    TestDataModelService testDataModelService
    SystemEntityService systemEntityService

    @Override
    public void init() {
        domainClass = Supplier.class
    }

    /**
     * @return -list of supplier object
     */
    public List<Supplier> list(BaseService baseService) {
        List<Supplier> lstSupplier = Supplier.findAllByCompanyId(companyId, [max: baseService.resultPerPage, offset: baseService.start, sort: baseService.sortColumn, order: baseService.sortOrder, readOnly: true]);
        return lstSupplier
    }

    @Override
    public List<Supplier> list() {
        List<Supplier> lstSupplier = Supplier.findAllByCompanyId(companyId, [sort: Supplier.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true]);
        return lstSupplier
    }

    public List listBySupplierTypeId(long supplierTypeId) {
        return Supplier.findAllByCompanyIdAndSupplierTypeId(companyId, supplierTypeId, [sort: Supplier.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true]);
    }

    public int count() {
        return Supplier.countByCompanyId(companyId);
    }

    /**
     * Count test data
     * @param companyId
     * @return
     */
    public int countByCompanyIdAndIdLessThan(long companyId){
        return Supplier.countByCompanyIdAndIdLessThan(companyId, 0)
    }

    public Map search(String queryType, String query, BaseService baseService) {

        List lst = Supplier.withCriteria {
            eq(COMPANY_ID_COLUMN, companyId)
            ilike(queryType, query)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(baseService.sortColumn, baseService.sortOrder)
            setReadOnly(true)
        } as List;

        // Count query for the above criteria using projection
        List counts = Supplier.withCriteria {
            eq(COMPANY_ID_COLUMN, companyId)
            ilike(queryType, query)
            projections { rowCount() };
        } as List;

        int total = counts[0] as int;
        return [list: lst, count: total];
    }

    /**
     * Method to get the count of suppliers
     * @param name - supplier name
     * @param companyId - company id
     * @return - an integer number of supplier count
     */
    public int countByNameIlikeAndCompanyId(String name, long companyId) {
        int supplierCount = Supplier.countByNameIlikeAndCompanyId(name, companyId)
        return supplierCount
    }

    /**
     * Method to get the count of suppliers
     * @param name - supplier name
     * @param companyId - company id
     * @param supplierId - supplier id
     * @return - an integer number of supplier count
     */
    public int countByNameIlikeAndCompanyIdAndIdNotEqual(String name, long companyId, long supplierId) {
        int count = Supplier.countByNameIlikeAndCompanyIdAndIdNotEqual(name, companyId, supplierId)
        return count
    }

    /**
     * Method to count suppliers
     * @param supplierTypeId - supplier type id
     * @param companyId - company id
     * @return - an integer number of supplier count
     */
    public int countBySupplierTypeIdAndCompanyId(long supplierTypeId, long companyId) {
        int count = Supplier.countBySupplierTypeIdAndCompanyId(supplierTypeId, companyId)
        return count
    }

    /**
     * Get list of Supplier by name and companyId
     * @param name - Supplier.name
     * @param companyId - Company.id
     * @return - list of Supplier
     */
    public List<Supplier> findAllByNameIlikeAndCompanyId(String name, long companyId) {
        List<Supplier> lstSupplier = Supplier.findAllByNameIlikeAndCompanyId(name, companyId, [readOnly: true])
        return lstSupplier
    }

    /**
     * find supplier by name and companyId
     * @param name
     * @param companyId
     * @return
     */
    public Supplier findByNameAndCompanyId(String name, long companyId) {
        Supplier supplier = Supplier.findByNameAndCompanyId(name, companyId, [readOnly: true])
        return supplier
    }

    private static final String QUERY = """
                    UPDATE supplier SET
                          version= :newVersion,
                         item_count=:itemCount
                      WHERE
                          id=:id AND
                          version=:version
                          """
    /**
     * Update item_count field when new item is added
     * @param supplier -Supplier object
     * @return -int value(updateCount)
     */
    public int updateForSupplierItem(Supplier supplier) {
        Map queryParams = [
                newVersion: supplier.version + 1,
                itemCount : supplier.itemCount,
                id        : supplier.id,
                version   : supplier.version
        ]
        int updateCount = executeUpdateSql(QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('error occurred at supplierService.updateForSupplierItem')
        }
        supplier.version = supplier.version + 1
        return updateCount;
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM supplier
                      WHERE
                          id=:id
                          """
    /**
     * SQL to delete supplier object from database
     * @param id -Supplier.id
     * @return -boolean value
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('error occurred at supplierService.delete')
        }
        return Boolean.TRUE;
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index supplier_name_company_id_idx on supplier(lower(name), company_id);"
        executeSql(nameIndex)
    }

    public int countByCompanyId(long companyId) {
        return Supplier.countByCompanyId(companyId)
    }

    private static final String INSERT_QUERY = """
        INSERT INTO supplier( id, version,
            name, account_name, address, item_count, supplier_type_id,
            company_id, created_by, created_on, updated_by )
        VALUES (:id,   :version,
            :name, :accountName, :address, :itemCount, :supplierTypeId,
            :companyId, :createdBy, :createdOn, :updatedBy);
    """

    @Override
    public boolean createTestData(long companyId, long userId) {
        SystemEntity supplierType = systemEntityService.findByKeyAndCompanyId('Material Provider', companyId)
        Supplier supplier1 = new Supplier(name: "Supplier 1", accountName: "Acc name", address: 'somewhere in dhaka', companyId: companyId, createdBy: userId, createdOn: new Date(), itemCount: 3, supplierTypeId: supplierType.id)
        Supplier supplier2 = new Supplier(name: "Supplier 2", accountName: "Acc name", address: 'somewhere in Sylhet', companyId: companyId, createdBy: userId, createdOn: new Date(), itemCount: 0, supplierTypeId: supplierType.id)

        runSqlForCreateTestData(supplier1)
        runSqlForCreateTestData(supplier2)
        return true
    }

    public void runSqlForCreateTestData(Supplier parameter) {
        Map queryParams = [
                id            : testDataModelService.getNextIdForTestData(),
                version       : 0L,
                name          : parameter.name,
                accountName   : parameter.accountName,
                address       : parameter.address,
                itemCount     : parameter.itemCount,
                supplierTypeId: parameter.supplierTypeId,
                companyId     : parameter.companyId,
                createdBy     : parameter.createdBy,
                createdOn     : DateUtility.getSqlDateWithSeconds(parameter.createdOn),
                updatedBy     : parameter.updatedBy
        ]
        executeInsertSql(INSERT_QUERY, queryParams)
    }
}