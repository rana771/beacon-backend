package com.athena.mis.application.actions.item

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.model.ListItemCategoryFixedAssetActionServiceModel
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.service.ItemTypeService
import com.athena.mis.application.service.ListItemCategoryFixedAssetActionServiceModelService
import com.athena.mis.integration.fixedasset.FxdPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update item object (Category: FixedAsset) and to show on grid list
 *  For details go through Use-Case doc named 'UpdateItemCategoryFixedAssetActionService'
 */
class UpdateItemCategoryFixedAssetActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    ItemService itemService
    ItemTypeService itemTypeService
    ListItemCategoryFixedAssetActionServiceModelService listItemCategoryFixedAssetActionServiceModelService

    private static final String ITEM_UPDATE_SUCCESS_MESSAGE = "Item has been updated successfully"
    private static final String ITEM_NAME_ALREADY_EXISTS = "Same item name already exists"
    private static final String HAS_ASSOCIATED_INV_TRANSACTION = "This item has association with inventory transaction"
    private static final String ITEM = "item"
    private static final String HAS_ASSOCIATION_FIXED_ASSET = " fixed asset details associated with selected item"

    /**
     * Check different criteria to update item object
     *      1) Check existence of selected item object
     *      2) Build item object to update
     *      3) Check duplicate item name
     *      4) Check association
     * @param params -parameter send from UI
     * @param obj -N/A
     * @return -a map containing item object for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {

            if ((!params.id)) {
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

            //Build object for update
            Item item = buildItemObject(params, oldItem)

            // check for duplicate item name
            int duplicateName = itemService.countByCategoryIdAndNameAndIdNotEqual(item.categoryId, item.name, item.id)
            if (duplicateName > 0) {
                return super.setError(params, ITEM_NAME_ALREADY_EXISTS)
            }

            //if isIndividualEntity property changed then check the association
            if (oldItem.isIndividualEntity != item.isIndividualEntity) {
                String msg = hasAssociation(item)
                if (msg) {
                    return setError(params, msg)
                }
            }
            params.put(ITEM, item)
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
            ListItemCategoryFixedAssetActionServiceModel itemModel = listItemCategoryFixedAssetActionServiceModelService.read(item.id)
            result.put(ITEM, itemModel)
            return setSuccess(result, ITEM_UPDATE_SUCCESS_MESSAGE)

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
     * build item object to update
     * @param parameterMap -GrailsParameterMap
     * @param oldItem -Item object
     * @return -item object
     */
    private Item buildItemObject(GrailsParameterMap parameterMap, Item oldItem) {

        AppUser appUser = getAppUser()
        Item item = new Item(parameterMap)

        oldItem.name = item.name
        oldItem.code = item.code
        oldItem.unit = item.unit
        oldItem.itemTypeId = item.itemTypeId
        oldItem.isIndividualEntity = item.isIndividualEntity
        oldItem.updatedBy = appUser.id
        oldItem.updatedOn = new Date()

        return oldItem
    }

    /**
     * 1. check association with Inventory transaction details
     * 1. check association with Fixed asset details
     * @param item -object of Item
     * @return -a string containing error message or null value depending on association check
     */
    private String hasAssociation(Item item) {
        if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
            int itemCountInInventoryTransaction = countInventoryTransactionDetails(item.id)
            if (itemCountInInventoryTransaction > 0) {
                return HAS_ASSOCIATED_INV_TRANSACTION
            }
        }
        if (PluginConnector.isPluginInstalled(FxdPluginConnector.PLUGIN_NAME)) {
            int countFAD = countFixedAssetDetails(item.id, item.companyId)
            if (countFAD > 0) {
                return countFAD + HAS_ASSOCIATION_FIXED_ASSET
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

    private static final String FXD_FIXED_ASSET_DETAILS = """
            SELECT COUNT(id) AS count
            FROM fxd_fixed_asset_details
            WHERE item_id = :itemId AND
                  company_id = :companyId
            """
    /**
     * count number of item(s) associated with fixed_asset_details
     * @param itemId -Item.id
     * @param companyId -Company.id
     * @return -int value
     */
    private int countFixedAssetDetails(long itemId, long companyId) {
        Map queryParams = [
                itemId   : itemId,
                companyId: companyId
        ]
        List results = executeSelectSql(FXD_FIXED_ASSET_DETAILS, queryParams)
        int count = results[0].count
        return count
    }
}
