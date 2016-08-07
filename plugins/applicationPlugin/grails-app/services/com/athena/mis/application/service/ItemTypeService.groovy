package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.SystemEntity
import org.apache.log4j.Logger

/**
 * ItemTypeService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class ItemTypeService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    SystemEntityService systemEntityService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Override
    public void init() {
        domainClass = ItemType.class
    }

    /**
     * Pull itemType object
     * @return - list of itemType
     */
    @Override
    public List list() {
        long companyId = super.getCompanyId()
        return ItemType.findAllByCompanyId(companyId, [sort: ItemType.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
    }

    /**
     * Get list of ItemType by companyId
     * @param companyId - Company.id
     * @return - list of ItemType
     */
    public List<ItemType> findAllByCompanyId(long companyId) {
        List<ItemType> lstItemType = ItemType.findAllByCompanyId(companyId, [readOnly: true])
        return lstItemType
    }

    /**
     * Get count of ItemType by name and companyId
     * @param name - name of ItemType
     * @param companyId - Company.id
     * @return - count of ItemType by name and companyId
     */
    public int countByNameIlikeAndCompanyId(String name, long companyId) {
        int count = ItemType.countByNameIlikeAndCompanyId(name, companyId)
        return count
    }

    /**
     * Get count of ItemType by name, id and companyId
     * @param name - name of ItemType
     * @param id - ItemType.id
     * @param companyId - Company.id
     * @return - count of ItemType by name, id and companyId
     */
    public int countByNameIlikeAndIdNotEqualAndCompanyId(String name, long id, long companyId) {
        int count = ItemType.countByNameIlikeAndIdNotEqualAndCompanyId(name, id, companyId)
        return count
    }

    /**
     * Get ItemType list by categoryId
     * @param categoryId - SystemEntity.id
     * @return - list of ItemType
     */
    public List listByCategoryId(long categoryId) {
        List lstItemType = []
        List lstTemp = list()
        if (lstTemp.size() <= 0)
            return lstItemType
        for (int i = 0; i < lstTemp.size(); i++) {
            ItemType itemType = (ItemType) lstTemp[i]
            if (itemType.categoryId == categoryId) {
                lstItemType << itemType
            }
        }
        return lstItemType
    }

    /**
     * Get list of ItemType ids by categoryId
     * @param categoryId - SystemEntity.id
     * @return - list of ItemType ids
     */
    public List<Long> listIdsByCategoryId(long categoryId) {
        List<Long> lstItemTypeIds = []
        List lstTemp = list()
        if (lstTemp.size() <= 0)
            return lstItemTypeIds
        for (int i = 0; i < lstTemp.size(); i++) {
            ItemType itemType = (ItemType) lstTemp[i]
            if (itemType.categoryId == categoryId) {
                lstItemTypeIds << itemType.id
            }
        }
        return lstItemTypeIds
    }

    /**
     * Get list of all ids
     * @return - list of ids
     */
    public List<Long> getAllItemTypeIds() {
        List<Long> lstItemTypeIds = []
        List lstTemp = list()
        if (lstTemp.size() <= 0)
            return lstItemTypeIds
        for (int i = 0; i < lstTemp.size(); i++) {
            lstItemTypeIds << lstTemp[i].id
        }
        return lstItemTypeIds
    }

    /**
     * Get list of ItemType by name
     * @param name - name of ItemType
     * @param companyId - Company.id
     * @return - list of ItemType
     */
    public List<ItemType> findAllByNameIlikeAndCompanyId(String name, long companyId) {
        name = PERCENTAGE + name + PERCENTAGE
        List<ItemType> lstItemType = ItemType.findAllByNameIlikeAndCompanyId(name, companyId, [readOnly: true])
        return lstItemType
    }

    /**
     * Get object of ItemType by name
     * @param name
     * @param companyId
     * @return
     */
    public ItemType findByNameAndCompanyId(String name, long companyId) {
        ItemType itemType = ItemType.findByNameAndCompanyId(name, companyId, [readOnly: true])
        return itemType
    }

    private static final String DELETE_ALL = """
        DELETE FROM item_type WHERE company_id = :companyId
    """

    /**
     * Delete all ItemType by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    public boolean createDefaultData(long companyId, long sysUserId) {
        try {
            SystemEntity categoryInventory = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_INVENTORY, companyId)
            SystemEntity categoryNonInventory = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_NON_INVENTORY, companyId)
            SystemEntity categoryFxdAsset = systemEntityService.findByReservedIdAndCompanyId(appSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_FIXED_ASSET, companyId)

            new ItemType(version: 0, categoryId: categoryInventory.id, companyId: companyId, createdBy: sysUserId, createdOn: new Date(), name: 'Material', updatedBy: 0, updatedOn: null).save()
            new ItemType(version: 0, categoryId: categoryNonInventory.id, companyId: companyId, createdBy: sysUserId, createdOn: new Date(), name: 'Work', updatedBy: 0, updatedOn: null).save()
            new ItemType(version: 0, categoryId: categoryFxdAsset.id, companyId: companyId, createdBy: sysUserId, createdOn: new Date(), name: 'Fixed Asset', updatedBy: 0, updatedOn: null).save()
            new ItemType(version: 0, categoryId: categoryNonInventory.id, companyId: companyId, createdBy: sysUserId, createdOn: new Date(), name: 'Overhead', updatedBy: 0, updatedOn: null).save()
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index item_type_name_company_id_idx on item_type(lower(name), company_id);"
        executeSql(nameIndex)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }
}
