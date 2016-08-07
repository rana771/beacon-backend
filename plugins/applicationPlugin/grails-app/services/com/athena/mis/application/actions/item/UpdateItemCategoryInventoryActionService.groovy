package com.athena.mis.application.actions.item

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Item
import com.athena.mis.application.model.ListItemCategoryInventoryActionServiceModel
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.service.ListItemCategoryInventoryActionServiceModelService
import com.athena.mis.integration.inventory.InvPluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update item object (Category: Inventory) and to show on grid list
 *  For details go through Use-Case doc named 'UpdateItemCategoryInventoryActionService'
 */
class UpdateItemCategoryInventoryActionService extends BaseService implements ActionServiceIntf{

    private Logger log = Logger.getLogger(getClass())

    private static final String ITEM = "item"
    private static final String ITEM_UPDATE_SUCCESS_MESSAGE = "Item has been updated successfully"
    private static final String ITEM_NAME_ALREADY_EXISTS = "Same item name already exists"
    private static final String HAS_ASSOCIATED_TRANSACTION = "This item has association with inventory transaction"
    private static
    final String HAS_ASSOCIATION_PRODUCTION_LINE_ITEM = "This Finished-Product item has association with production line item"

    ItemService itemService
    ListItemCategoryInventoryActionServiceModelService listItemCategoryInventoryActionServiceModelService

    @Autowired(required = false)
    InvPluginConnector invInventoryImplService

    /**
     * Check different criteria to update item object
     *      1) Check existence of selected item object
     *      2) Validate item object to update
     *      3) Check association
     *      4) Check duplicate item name
     * @param params -parameter send from UI
     * @param obj -N/A
     * @return -a map containing item object for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check required parameter
            if ((!params.id) || (!params.version)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long itemId = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())
            // get Item object form service
            Item oldItem = itemService.read(itemId)
            //Check existence of selected item object
            if ((!oldItem) || (oldItem.version != version)) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            // build Item object for update
            Item item = getItem(params, oldItem)
            //check association
            String msg = hasAssociation(oldItem, item)
            if (msg) {
                return super.setError(params, msg)
            }
            // check for duplicate item name
            int duplicateName = itemService.countByCategoryIdAndNameAndIdNotEqual(item.categoryId, item.name, item.id)
            if (duplicateName > 0) {
                return super.setError(params, ITEM_NAME_ALREADY_EXISTS)
            }
            params.put(ITEM, item)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
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
            itemService.update(item)//update in DB
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
            ListItemCategoryInventoryActionServiceModel itemModel = listItemCategoryInventoryActionServiceModelService.read(item.id)
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
        long valuationTypeId = Long.parseLong(params.valuationTypeId.toString())
        Item newItem = new Item(params)
        if (oldItem.valuationTypeId != valuationTypeId) {
            oldItem.isDirty = true
        }
        oldItem.name = newItem.name
        oldItem.code = newItem.code
        oldItem.unit = newItem.unit
        oldItem.isFinishedProduct = newItem.isFinishedProduct
        oldItem.itemTypeId = newItem.itemTypeId
        oldItem.valuationTypeId = newItem.valuationTypeId
        oldItem.updatedBy = super.getAppUser().id
        oldItem.updatedOn = new Date()
        return oldItem
    }

    /**
     * 1. check association with Inventory transaction details
     * 2. check association with Inventory production details
     * @param oldItem -old Item object
     * @param item -new Item object
     * @return -a string containing error message or null value depending on association check
     */
    private String hasAssociation(Item oldItem, Item item) {
        if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
            int itemCountInInventoryTransaction = countInventoryTransactionDetails(item.id)
            if (itemCountInInventoryTransaction > 0 && (item.valuationTypeId != oldItem.valuationTypeId)) {
                return HAS_ASSOCIATED_TRANSACTION
            }
            if (oldItem.isFinishedProduct) {
                int countInvProductionDetails = countByItemIdInInvProductionDetails(oldItem.id)
                if (countInvProductionDetails > 0) {
                    return HAS_ASSOCIATION_PRODUCTION_LINE_ITEM
                }
            }
        }
        return null
    }

    private static final String INV_INVENTORY_TRANSACTION_DETAILS = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction_details
            WHERE item_id = :itemId """
    /**
     * count number of item(s) associated with inventory_transaction_details
     * @param itemId -Item.id
     * @return -int value
     */
    private int countInventoryTransactionDetails(long itemId) {
        List results = executeSelectSql(INV_INVENTORY_TRANSACTION_DETAILS, [itemId: itemId])
        int count = results[0].count
        return count
    }

    private static final String INV_PRODUCTION_DETAILS = """
            SELECT COUNT(id) AS count
            FROM inv_production_details
            WHERE material_id = :itemId
            AND production_item_type_id = :productionItemTypeId
        """
    /**
     * count number of item(s) associated with production_details
     * @param itemId -Item.id
     * @return -int value
     */
    private int countByItemIdInInvProductionDetails(long itemId) {
        Map queryParams = [
                itemId: itemId,
                productionItemTypeId: invInventoryImplService.getInvProductionItemTypeFinishedMaterialId()
        ]
        List results = executeSelectSql(INV_PRODUCTION_DETAILS, queryParams)
        int count = results[0].count
        return count
    }
}
