package com.athena.mis.application.actions.itemtype

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.model.ListItemTypeActionServiceModel
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.service.ItemTypeService
import com.athena.mis.application.service.ListItemTypeActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Update item type object and grid data
 *  For details go through Use-Case doc named 'UpdateItemTypeActionService'
 */
class UpdateItemTypeActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ITEM_TYPE = "itemType"
    private static final String ENTITY_NOT_FOUND = "Selected item type not found"
    private static final String NAME_ALREADY_EXISTS = "Same item type name already exists"
    private static final String UPDATE_SUCCESS_MESSAGE = "Item type has been updated successfully"
    private static final String CATEGORY_NOT_UPDATE_ABLE = " item(s) associated with selected item type, so category is not update-able"

    ItemTypeService itemTypeService
    ItemService itemService
    ListItemTypeActionServiceModelService listItemTypeActionServiceModelService

    /**
     * 1. Check existence of required parameters
     * 2. pull item type object from cache utility
     * 3. check item type existence
     * 4. Build object for update
     * 5. duplicate check for item type name
     * @param params -serialize parameters from UI
     * @param obj - N/A
     * @return - a map containing item type & isError(true/false) depending on method success & relevant message.
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            //Check existence of required parameters
            if ((!params.id) || (!params.name) || (!params.version) || (!params.categoryId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long id = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())
            ItemType oldItemType = itemTypeService.read(id)
            if ((!oldItemType) || (oldItemType.version != version)) {
                return super.setError(params, ENTITY_NOT_FOUND)
            }
            //Check duplicate name
            int duplicateName = itemTypeService.countByNameIlikeAndIdNotEqualAndCompanyId(params.name, oldItemType.id, oldItemType.companyId)
            if (duplicateName > 0) {
                return super.setError(params, NAME_ALREADY_EXISTS)
            }
            long categoryId = Long.parseLong(params.categoryId.toString())
            if (oldItemType.categoryId != categoryId) {
                int itemCount = itemService.countByItemTypeId(oldItemType.id)
                if (itemCount > 0) {
                    return super.setError(params, itemCount.toString() + CATEGORY_NOT_UPDATE_ABLE)
                }
            }
            //Build object for update
            ItemType itemType = getItemType(params, oldItemType)
            params.put(ITEM_TYPE, itemType)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }
    /**
     * 1. receive item type object from pre execute method
     * 2. Update item type
     * 3. update item type to corresponding cache utility & sort cache utility
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters - N/A
     * @param obj - receive project object from pre execute method
     * @return - a map containing item type object & isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            ItemType itemType = (ItemType) result.get(ITEM_TYPE)
            itemTypeService.update(itemType)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }
    /**
     * do nothing for post operation
     */
    public Map executePostCondition(Map result) {
        return result
    }
    /**
     * Wrap list of item type in grid entity
     * @param obj -item type object
     * @return -list of wrapped item type
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            ItemType itemType = (ItemType) result.get(ITEM_TYPE)
            ListItemTypeActionServiceModel itemTypeModel = listItemTypeActionServiceModelService.read(itemType.id)
            result.put(ITEM_TYPE, itemTypeModel)
            return super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }
    /**
     * Build failure result in case of any error
     * @param obj -N/A
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
    /**
     * Build item type object
     * @param parameterMap - serialize parameters from UI
     * @param oldItemType - previous state of item type object
     * @return - newly built item type object
     */
    private ItemType getItemType(Map params, ItemType oldItemType) {
        ItemType itemType = new ItemType(params)
        oldItemType.name = itemType.name
        oldItemType.categoryId = itemType.categoryId
        oldItemType.updatedBy = super.getAppUser().id
        oldItemType.updatedOn = new Date()
        return oldItemType
    }
}

