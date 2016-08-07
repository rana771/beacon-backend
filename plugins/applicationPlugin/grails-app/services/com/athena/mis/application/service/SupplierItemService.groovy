package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SupplierItem
import com.athena.mis.utility.DateUtility

/**
 *  Service class for basic supplier-item CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'SupplierItemService'
 */
class SupplierItemService extends BaseDomainService {

    TestDataModelService testDataModelService
    ItemService itemService
    SupplierService supplierService

    @Override
    public void init() {
        domainClass = SupplierItem.class
    }

    /**
     * @return -list of supplier-item objects
     */
    @Override
    public List list() {
        return SupplierItem.list(sort: SupplierItem.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true);
    }

    /**
     * Method to get the list of supplier item
     * @param supplierId - supplier id
     * @param companyId - company id
     * @return - a list of supplier item
     */
    public List<SupplierItem> findAllBySupplierIdAndCompanyId(long supplierId, long companyId) {
        List<SupplierItem> supplierItemList = SupplierItem.findAllBySupplierIdAndCompanyId(supplierId, companyId, [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        return supplierItemList
    }

    /**
     * Method to get supplier item list
     * @param supplierId - supplier id
     * @param companyId - company id
     * @param lstMatchedItemIds - list of matched item id
     * @return - a list of supplier item
     */
    public List<SupplierItem> findAllBySupplierIdAndCompanyIdAndItemIdInList(long supplierId, long companyId, List<Long> lstMatchedItemIds) {
        List<SupplierItem> supplierItemList = SupplierItem.findAllBySupplierIdAndCompanyIdAndItemIdInList(supplierId, companyId, lstMatchedItemIds, [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        return supplierItemList
    }
    /**
     * Method to get supplier item object
     * @param supplierId - supplier id
     * @param companyId - company id
     * @param itemId - item id
     * @return - supplier item object
     */
    public SupplierItem findBySupplierIdAndCompanyIdAndItemId(long supplierId, long companyId, long itemId) {
        SupplierItem supplierItem = SupplierItem.findBySupplierIdAndCompanyIdAndItemId(supplierId, companyId, itemId, [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        return supplierItem
    }

    /**
     * Method to get the count of supplier items
     * @param supplierId - supplier id
     * @param companyId - company id
     * @param lstMatchedItemIds - list of matched item ids
     * @return - an integer value of count
     */
    public int countBySupplierIdAndCompanyIdAndItemIdInList(long supplierId, long companyId, List<Long> lstMatchedItemIds) {
        int count = SupplierItem.countBySupplierIdAndCompanyIdAndItemIdInList(supplierId, companyId, lstMatchedItemIds)
        return count
    }
    /**
     * Method to get the count of supplier items
     * @param supplierId - supplier id
     * @param companyId - company id
     * @return - an integer number of supplier item count
     */
    public int countBySupplierIdAndCompanyId(long supplierId, long companyId) {
        int count = SupplierItem.countBySupplierIdAndCompanyId(supplierId, companyId)
        return count
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM supplier_item
                      WHERE id=:id   """
    /**
     * SQL to delete supplierItem object from database
     * @param id -SupplierItem.id
     * @return -boolean value
     */
    public int delete(long id) {
        int deleteCount = executeUpdateSql(DELETE_QUERY, [id: id])
        if (deleteCount <= 0) {
            throw new RuntimeException('error occurred at supplierItemService.delete')
        }
        return deleteCount
    }

    private static final String INSERT_QUERY = """
        INSERT INTO supplier_item( id, version,
            item_id, supplier_id,
            company_id, created_by, created_on, updated_by )
        VALUES (:id,  :version,
            :itemId, :supplierId,
            :companyId, :createdBy, :createdOn, :updatedBy);
    """

    @Override
    public boolean createTestData(long companyId, long userId) {
        Item item1 = itemService.findByNameAndCompanyId('Brick', companyId)
        Item item2 = itemService.findByNameAndCompanyId('Cement', companyId)
        Item item3 = itemService.findByNameAndCompanyId('Diesel', companyId)

        Supplier supplier = supplierService.findByNameAndCompanyId('Supplier 1', companyId)

        SupplierItem supplierItem1 = new SupplierItem(itemId: item1.id, supplierId: supplier.id, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        SupplierItem supplierItem2 = new SupplierItem(itemId: item2.id, supplierId: supplier.id, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        SupplierItem supplierItem3 = new SupplierItem(itemId: item3.id, supplierId: supplier.id, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)

        runSqlForCreateTestData(supplierItem1)
        runSqlForCreateTestData(supplierItem2)
        runSqlForCreateTestData(supplierItem3)
        return true
    }

    public void runSqlForCreateTestData(SupplierItem parameter) {
        Map queryParams = [
                id        : testDataModelService.getNextIdForTestData(),
                version   : 0L,
                itemId    : parameter.itemId,
                supplierId: parameter.supplierId,
                companyId : parameter.companyId,
                createdBy : parameter.createdBy,
                createdOn : DateUtility.getSqlDateWithSeconds(parameter.createdOn),
                updatedBy : parameter.updatedBy
        ]
        executeInsertSql(INSERT_QUERY, queryParams)
    }

    @Override
    public void createDefaultSchema() {
        String sqlIndex = "CREATE UNIQUE INDEX supplier_item_supplier_id_item_id_idx ON supplier_item(supplier_id,item_id);"
        executeSql(sqlIndex)
    }

}
