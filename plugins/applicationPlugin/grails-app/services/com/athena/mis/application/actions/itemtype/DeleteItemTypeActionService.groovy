package com.athena.mis.application.actions.itemtype

import com.athena.mis.BaseService
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.service.ItemTypeService
import com.athena.mis.ActionServiceIntf
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete item type object from DB and remove it from grid
 *  For details go through Use-Case doc named 'DeleteItemTypeActionService'
 */
class DeleteItemTypeActionService extends BaseService implements ActionServiceIntf {

    ItemTypeService itemTypeService
    ItemService itemService

    private static final String DELETE_SUCCESS_MSG = "Item type has been successfully deleted"
    private static final String OBJ_NOT_FOUND = "Selected item type not found"
    private static final String HAS_ASSOCIATION_ITEM = " item(s) are associated with selected item type"
    private static final String ITEM_TYPE = "itemType"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check input validation
     * 2. pull item type object
     * 3. check for item type existence
     * 4. check association with Item
     * @param parameters - serialize parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check existence of required parameters
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            // check object existence
            long itemTypeId = Long.parseLong(params.id.toString())
            ItemType itemType = itemTypeService.read(itemTypeId)
            if (!itemType) {
                return super.setError(params, OBJ_NOT_FOUND)
            }
            // check association with Item
            int countItem = itemService.countByItemTypeId(itemTypeId)
            if (countItem > 0) {
                String msg = countItem + HAS_ASSOCIATION_ITEM
                return super.setError(params, msg)
            }
            params.put(ITEM_TYPE, itemType)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * Delete item type object from DB
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            ItemType itemType = (ItemType) result.get(ITEM_TYPE)
            itemTypeService.delete(itemType)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DELETE_SUCCESS_MSG)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}


