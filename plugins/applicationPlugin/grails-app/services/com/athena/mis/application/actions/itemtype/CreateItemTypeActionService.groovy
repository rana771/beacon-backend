package com.athena.mis.application.actions.itemtype

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.model.ListItemTypeActionServiceModel
import com.athena.mis.application.service.ItemTypeService
import com.athena.mis.application.service.ListItemTypeActionServiceModelService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new item type object and show in grid
 *  For details go through Use-Case doc named 'CreateItemTypeActionService'
 */
class CreateItemTypeActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ITEM_TYPE = "itemType"
    private static final String CREATE_SUCCESS_MSG = "Item type has been successfully saved"
    private static final String NAME_ALREADY_EXISTS = "Same item type name already exists"

    ItemTypeService itemTypeService
    ListItemTypeActionServiceModelService listItemTypeActionServiceModelService

    /**
     * 1. Check existence of required parameters
     * 2. Build ItemType Object
     * 3. duplicate check for item type name
     * @param parameters - serialize parameters from UI
     * @param obj - N/A
     * @return - a map containing item type object & isError(true/false) depending on method success
     *   & relevant message.
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            //Check existence of required parameters
            if (!params.name || !params.categoryId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            //Check duplicate name
            int duplicateName = itemTypeService.countByNameIlikeAndCompanyId(params.name, super.getCompanyId())
            if (duplicateName > 0) {
                return super.setError(params, NAME_ALREADY_EXISTS)
            }
            //Build ItemType Object
            ItemType itemType = buildItemTypeObject(params)
            params.put(ITEM_TYPE, itemType)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }
    /**
     * Save item type object in DB and update cache utility accordingly
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            ItemType itemType = (ItemType) result.get(ITEM_TYPE)
            itemTypeService.create(itemType)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }
    /**
     * Do nothing for post operation
     */
    public Map executePostCondition(Map result) {
        return result
    }
    /**
     * Show newly created item type object in grid
     * 1. receive item type from execute method
     * 2. pull item category from cache utility
     * @param obj - map from execute method
     * @return - a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            ItemType itemType = (ItemType) result.get(ITEM_TYPE)
            ListItemTypeActionServiceModel itemTypeModel = listItemTypeActionServiceModelService.read(itemType.id)
            result.put(ITEM_TYPE, itemTypeModel)
            return super.setSuccess(result, CREATE_SUCCESS_MSG)
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
     * Build item type object
     * @param parameterMap - serialize parameters from UI
     * @return - newly built item type object
     */
    private ItemType buildItemTypeObject(Map parameterMap) {
        ItemType itemType = new ItemType(parameterMap)
        AppUser appUser = super.getAppUser()
        itemType.createdBy = appUser.id
        itemType.createdOn = new Date()
        itemType.companyId = appUser.companyId
        itemType.updatedBy = 0L
        itemType.updatedOn = null
        return itemType
    }
}

