package com.athena.mis.application.actions.item

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.model.ListItemCategoryFixedAssetActionServiceModel
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.service.ItemTypeService
import com.athena.mis.application.service.ListItemCategoryFixedAssetActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create new item (Category: FixedAsset) and show on grid list
 *  For details go through Use-Case doc named 'CreateItemCategoryFixedAssetActionService'
 */
class CreateItemCategoryFixedAssetActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    ItemService itemService
    ItemTypeService itemTypeService
    AppSystemEntityCacheService appSystemEntityCacheService
    ListItemCategoryFixedAssetActionServiceModelService listItemCategoryFixedAssetActionServiceModelService

    private static final String ITEM_CREATE_SUCCESS_MSG = "Item has been successfully saved"
    private static final String ITEM_NAME_ALREADY_EXISTS = "Same item name already exists"
    private static final String ITEM = "item"

    /**
     * Check different criteria for creating new item
     * Check duplicate item name
     * @param params -parameter send from UI
     * @param obj -N/A
     * @return -a map containing item object for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            SystemEntity itemSysEntityObject = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_FIXED_ASSET, appSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY, companyId)
            //Check duplicate name
            int duplicateName = itemService.countByCategoryIdAndName(itemSysEntityObject.id, params.name.toString())
            if (duplicateName > 0) {
                return setError(params, ITEM_NAME_ALREADY_EXISTS)
            }

            Item item = buildItemObject(params, itemSysEntityObject)
            params.put(ITEM, item)
            return params

        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Save item object in DB
     * @param parameters -N/A
     * @param obj -itemObject send from previous method
     * @return -newly created item object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {

        try {
            Item item = (Item) result.get(ITEM)
            itemService.create(item)  //save in DB
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * do nothing at post condition
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Wrap newly created item to show on grid
     * @param obj -newly created item object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Map buildSuccessResultForUI(Map result) {
        try {
            Item item = (Item) result.get(ITEM)
            ListItemCategoryFixedAssetActionServiceModel itemModel = listItemCategoryFixedAssetActionServiceModelService.read(item.id)
            result.put(ITEM, itemModel)
            return setSuccess(result, ITEM_CREATE_SUCCESS_MSG)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * build item object to create
     * @param parameterMap -grailsParameterMap
     * @return -item object
     */
    private Item buildItemObject(Map params, SystemEntity sysCategoryEntity) {

        AppUser appUser = getAppUser()
        SystemEntity valuationTypeAvgObj = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_VALUATION_AVG, appSystemEntityCacheService.SYS_ENTITY_TYPE_VALUATION, appUser.companyId)
        Item item = new Item(params)
        item.companyId = appUser.companyId
        item.categoryId = sysCategoryEntity.id
        item.createdOn = new Date()
        item.createdBy = appUser.id
        item.updatedBy = 0
        item.isFinishedProduct = false
        item.valuationTypeId = valuationTypeAvgObj.id //valuationType = AVG (for both IndividualEntity OR Not)

        return item
    }
}
