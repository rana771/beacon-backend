package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.DateUtility

/**
 *  Service class for basic item CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'ItemService'
 */
class ItemService extends BaseDomainService {

    ItemTypeService itemTypeService
    TestDataModelService testDataModelService
    SystemEntityService systemEntityService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Override
    public void init() {
        domainClass = Item.class
    }

    /**
     * @return -list of item object
     */
    @Override
    public List<Item> list() {
        List<Item> itemList = []
        itemList = Item.findAllByCompanyId(companyId, [sort: Item.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return itemList
    }

    public List<Item> listByItemTypeId(long itemTypeId) {
        List<Item> lstItem = Item.findAllByItemTypeIdAndCompanyId(itemTypeId, companyId, [sort: Item.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstItem
    }

    public List<Item> listByCategoryId(long categoryId) {
        List<Item> lstItem = Item.findAllByCategoryIdAndCompanyId(categoryId, companyId, [sort: Item.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstItem
    }

    /**
     * Count test data
     * @param companyId
     * @return
     */
    public int countByCompanyIdAndIdLessThan(long companyId) {
        return Item.countByCompanyIdAndIdLessThan(companyId, 0)
    }

    private static final String DELETE_QUERY = """ DELETE FROM item WHERE id=:id """

    /**
     * SQL to delete item object from database
     * @param id -Item.id
     * @return -boolean value
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("Error occurred at itemService.delete")
        }
        return Boolean.TRUE
    }

    public int countByItemTypeId(long itemTypeId) {
        int count = Item.countByItemTypeIdAndCompanyId(itemTypeId, companyId)
        return count
    }

    public int countByValuationTypeId(long valuationTypeId) {
        int count = Item.countByValuationTypeIdAndCompanyId(valuationTypeId, companyId)
        return count
    }

    public int countByCategoryId(long categoryId) {
        int count = Item.countByCategoryIdAndCompanyId(categoryId, companyId)
        return count
    }

    public int countByCategoryIdAndName(long categoryId, String name) {
        int count = Item.countByCategoryIdAndNameAndCompanyId(categoryId, name, companyId)
        return count
    }

    public int countByCategoryIdAndNameAndIdNotEqual(long categoryId, String name, long id) {
        int count = Item.countByCategoryIdAndNameAndCompanyIdAndIdNotEqual(categoryId, name, companyId, id)
        return count
    }

    public List findAllByIdInList(List<Long> ids) {
        List lstItem = Item.findAllByIdInListAndCompanyId(ids, companyId, [readOnly: true])
        return lstItem
    }

    public List<Item> findAllByIsDirtyAndCompanyId(boolean isDirty, long companyId) {
        List<Item> lstItem = Item.findAllByIsDirtyAndCompanyId(isDirty, companyId, [readOnly: true])
        return lstItem
    }

    // used to populate drop-down with full name
    public List listByTypeForDropDown(long itemTypeId) {
        List lstSpecificItems = []
        List<Item> itemList = Item.findAllByItemTypeIdAndCompanyId(itemTypeId, companyId, [sort: Item.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        if (itemList.size() <= 0) return lstSpecificItems
        Map customItem
        for (int i = 0; i < itemList.size(); i++) {
            customItem = [id: itemList[i].id, name: itemList[i].name]
            lstSpecificItems << customItem
        }
        return lstSpecificItems
    }

    public List<Item> search(String queryType, String query) {
        List lst = Item.withCriteria {
            eq('companyId', companyId)
            ilike(queryType, query)
            maxResults(resultPerPage)
            firstResult(start)
            order(sortColumn, sortOrder)
            setReadOnly(true)
        } as List;
        return lst;
    }

    public Map search(String queryType, String query, BaseService baseService) {

        List lst = Item.withCriteria {
            eq('companyId', companyId)
            ilike(queryType, query)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(baseService.sortColumn, baseService.sortOrder)
            setReadOnly(true)
        } as List;

        // Count query for the above criteria using projection
        List counts = Item.withCriteria {
            eq('companyId', companyId)
            ilike(queryType, query)
            projections { rowCount() };
        } as List;

        int total = counts[0] as int;
        return [list: lst, count: total];
    }

    public Map searchCategoryWiseItemList(String queryType, String query, long categoryId, BaseService baseService) {
        SystemEntity Object = appSystemEntityCacheService.readByReservedId(categoryId, appSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY, companyId)

        List lst = Item.withCriteria {
            eq('companyId', companyId)
            eq('categoryId', Object.id)
            ilike(queryType, query)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(baseService.sortColumn, baseService.sortOrder)
            setReadOnly(true)
        } as List;

        // Count query for the above criteria using projection
        List counts = Item.withCriteria {
            eq('companyId', companyId)
            eq('categoryId', Object.id)
            ilike(queryType, query)
            projections { rowCount() };
        } as List;

        int total = counts[0] as int;
        return [list: lst, count: total];
    }

    public Map listItemByCategoryId(long categoryId, BaseService baseService) {

        List lst = Item.withCriteria {
            eq('companyId', companyId)
            eq('categoryId', categoryId)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            order(baseService.sortColumn, baseService.sortOrder)
            setReadOnly(true)
        } as List;

        // Count query for the above criteria using projection
        List counts = Item.withCriteria {
            eq('companyId', companyId)
            eq('categoryId', categoryId)
            projections { rowCount() };
        } as List;

        int total = counts[0] as int;
        return [list: lst, count: total];
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index item_name_category_id_idx on item(lower(name), category_id)"
        executeSql(nameIndex)
    }

    public Item findByNameAndCompanyId(String name, long companyId) {
        return Item.findByNameAndCompanyId(name, companyId, [readOnly: true])
    }

    public int countByCompanyId(long companyId) {
        return Item.countByCompanyId(companyId)
    }

    private static final String INSERT_QUERY = """
        INSERT INTO item(id, version,
            name, code, unit, category_id,
            item_type_id, valuation_type_id, is_individual_entity, is_finished_product,
            company_id, created_by, created_on, updated_by, is_dirty)
        VALUES (:id,  :version,
            :name, :code, :unit, :categoryId,  :itemTypeId,  :valuationTypeId,
            :isIndividualEntity,   :isFinishedProduct,
            :companyId, :createdBy, :createdOn, :updatedBy, :isDirty);
    """

    @Override
    public boolean createTestData(long companyId, long userId) {
        SystemEntity itemInvCategorySysEntityObject = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_INVENTORY, companyId)
        SystemEntity itemNonInvCategorySysEntityObject = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_NON_INVENTORY, companyId)

        SystemEntity valuationTypeFifoObj = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_VALUATION_FIFO, companyId)
        SystemEntity valuationTypeLifoObj = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_VALUATION_LIFO, companyId)

        ItemType itemType = itemTypeService.findByNameAndCompanyId('Material', companyId)
        ItemType itemTypeNon = itemTypeService.findByNameAndCompanyId('Work', companyId)

        Item item1 = new Item(name: 'Brick', code: 'BRK', unit: 'piece', itemTypeId: itemType.id, categoryId: itemInvCategorySysEntityObject.id, valuationTypeId: valuationTypeFifoObj.id, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        Item item2 = new Item(name: 'Cement', code: 'CMT', unit: 'Bag', itemTypeId: itemType.id, categoryId: itemInvCategorySysEntityObject.id, valuationTypeId: valuationTypeFifoObj.id, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        Item item3 = new Item(name: 'Diesel', code: 'DIS', unit: 'Liter', itemTypeId: itemType.id, categoryId: itemInvCategorySysEntityObject.id, valuationTypeId: valuationTypeLifoObj.id, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        Item item4 = new Item(name: 'Steel', code: 'STL', unit: 'KG', itemTypeId: itemType.id, categoryId: itemInvCategorySysEntityObject.id, valuationTypeId: valuationTypeLifoObj.id, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        Item item5 = new Item(name: 'Painting', code: 'PNT', unit: 'Hours', itemTypeId: itemTypeNon.id, categoryId: itemNonInvCategorySysEntityObject.id, valuationTypeId: valuationTypeFifoObj.id, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        Item item6 = new Item(name: 'ReadyMix', code: 'RM', unit: 'CU. M.', itemTypeId: itemType.id, categoryId: itemInvCategorySysEntityObject.id, valuationTypeId: valuationTypeFifoObj.id, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0, isFinishedProduct: true)

        runSqlForCreateTestData(item1)
        runSqlForCreateTestData(item2)
        runSqlForCreateTestData(item3)
        runSqlForCreateTestData(item4)
        runSqlForCreateTestData(item5)
        runSqlForCreateTestData(item6)
        return true
    }

    public void runSqlForCreateTestData(Item parameter) {
        Map queryParams = [
                id                : testDataModelService.getNextIdForTestData(),
                version           : 0L,
                name              : parameter.name,
                code              : parameter.code,
                unit              : parameter.unit,
                categoryId        : parameter.categoryId,
                itemTypeId        : parameter.itemTypeId,
                valuationTypeId   : parameter.valuationTypeId,
                isIndividualEntity: parameter.isIndividualEntity,
                isFinishedProduct : parameter.isFinishedProduct,
                companyId         : parameter.companyId,
                createdBy         : parameter.createdBy,
                createdOn         : DateUtility.getSqlDateWithSeconds(parameter.createdOn),
                updatedBy         : parameter.updatedBy,
                isDirty           : false
        ]
        executeInsertSql(INSERT_QUERY, queryParams)
    }
}
