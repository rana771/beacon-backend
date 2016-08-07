package com.athena.mis.application.actions.item

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.model.ListItemCategoryNonInvActionServiceModel
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.service.ListItemCategoryNonInvActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update item object (Category: Non-Inventory) and to show on grid list
 *  For details go through Use-Case doc named 'UpdateItemCategoryNonInvActionService'
 */
class UpdateItemCategoryNonInvActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ITEM = "item"
    private static final String ITEM_UPDATE_SUCCESS_MESSAGE = "Item has been updated successfully"
    private static final String ITEM_NAME_ALREADY_EXISTS = "Same item name already exists"
    private static final String OBJ_NOT_FOUND = "Selected item not found"

    ItemService itemService
    ListItemCategoryNonInvActionServiceModelService listItemCategoryNonInvActionServiceModelService

    /**
     * Check different criteria to update item object
     *      1) Check existence of selected item object
     *      2) Validate item object to update
     *      3) Check duplicate item name
     * @param params -parameter send from UI
     * @return -a map containing item object for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if ((!params.id) || (!params.version) || (!params.itemTypeId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())
            Item oldItem = itemService.read(id)
            //Check existence of selected item object
            if ((!oldItem) || (oldItem.version != version)) {
                return super.setError(params, OBJ_NOT_FOUND)
            }
            // Check duplicate name
            int duplicateName = itemService.countByCategoryIdAndNameAndIdNotEqual(oldItem.categoryId, params.name, oldItem.id)
            if (duplicateName > 0) {
                return super.setError(params, ITEM_NAME_ALREADY_EXISTS)
            }
            //Build object for update
            Item newItem = getItem(params, oldItem)
            params.put(ITEM, newItem)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * update item object in DB
     * @param parameters -N/A
     * @param obj -itemObject send from controller
     * @return -newly updated item object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Item item = (Item) result.get(ITEM)
            //update in DB
            itemService.update(item)
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
     * Wrap updated item object to show on grid
     * @param obj -updated item object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            Item item = (Item) result.get(ITEM)
            ListItemCategoryNonInvActionServiceModel itemModel = listItemCategoryNonInvActionServiceModelService.read(item.id)
            result.put(ITEM, itemModel)
            return super.setSuccess(result, ITEM_UPDATE_SUCCESS_MESSAGE)
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
     * build item object to update
     * @param params -GrailsParameterMap
     * @param oldItem -Item object
     * @return -item object
     */
    private Item getItem(Map params, Item oldItem) {
        Item item = new Item(params)
        oldItem.name = item.name
        oldItem.code = item.code
        oldItem.unit = item.unit
        oldItem.itemTypeId = item.itemTypeId
        oldItem.updatedBy = super.getAppUser().id
        oldItem.updatedOn = new Date()
        return oldItem
    }
}

