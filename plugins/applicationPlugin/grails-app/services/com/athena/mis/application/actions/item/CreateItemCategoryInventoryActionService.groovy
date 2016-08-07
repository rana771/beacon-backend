package com.athena.mis.application.actions.item

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.model.ListItemCategoryInventoryActionServiceModel
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.service.ListItemCategoryInventoryActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create new item (Category: Inventory) and show on grid list
 *  For details go through Use-Case doc named 'CreateItemCategoryInventoryActionService'
 */
class CreateItemCategoryInventoryActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ITEM = "item"
    private static final String ITEM_CREATE_SUCCESS_MSG = "Item has been successfully saved"
    private static final String ITEM_NAME_ALREADY_EXISTS = "Same item name already exists"

    ItemService itemService
    AppSystemEntityCacheService appSystemEntityCacheService
    ListItemCategoryInventoryActionServiceModelService listItemCategoryInventoryActionServiceModelService

    /**
     * Check different criteria for creating new item
     * Check duplicate item name
     * @param params -parameter send from UI
     * @return -a map containing item object for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            Item item = getItem(params)
            // check for duplicate item name
            int duplicateName = itemService.countByCategoryIdAndName(item.categoryId, item.name)
            if (duplicateName > 0) {
                return super.setError(params, ITEM_NAME_ALREADY_EXISTS)
            }
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
            itemService.create(item)//save in DB
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
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            Item item = (Item) result.get(ITEM)
            ListItemCategoryInventoryActionServiceModel itemModel = listItemCategoryInventoryActionServiceModelService.read(item.id)
            result.put(ITEM, itemModel)
            return super.setSuccess(result, ITEM_CREATE_SUCCESS_MSG)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Build failure result in case of any error
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
    private Item getItem(Map params) {
        AppUser appUser = super.getAppUser()
        SystemEntity inventorySysEntityObject = appSystemEntityCacheService.readByReservedId(appSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_INVENTORY, appSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY, appUser.companyId)
        Item item = new Item(params)
        item.companyId = appUser.companyId
        item.categoryId = inventorySysEntityObject.id
        item.createdOn = new Date()
        item.createdBy = appUser.id
        return item
    }
}
