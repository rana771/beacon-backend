package com.athena.mis.application.actions.item

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Item
import com.athena.mis.application.service.ItemService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get all itemList filtered by specific itemType to show on drop-down
 *      -> used in AccLc & AccLeaseAccount CRUD
 *  For details go through Use-Case doc named 'GetItemListSupplierItemActionService'
 */
class GetItemListByItemTypeActionService extends BaseService implements ActionServiceIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String ITEM_LIST = "itemList"

    ItemService itemService

    /**
     * 1. check required parameters
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        try {
            //check required parameters
            if (!params.itemTypeId) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. get all itemList filtered by specific itemType to show on drop-down
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            long itemTypeId = Long.parseLong(result.itemTypeId.toString())
            List<Item> lstItem = itemService.listByTypeForDropDown(itemTypeId)
            result.put(ITEM_LIST, itemService.listForKendoDropdown(lstItem, null, null))
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     *
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * There is no Build success, so return the same map as received
     *
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
