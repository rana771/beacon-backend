package com.athena.mis.application.actions.supplieritem

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.service.ItemService
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to get unassigned itemList to show on drop-down
 *  For details go through Use-Case doc named 'GetItemListSupplierItemActionService'
 */
class GetItemListSupplierItemActionService extends BaseService implements ActionServiceIntf {
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
            if (!params.supplierId || !params.itemTypeId) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * 1. get list of specific type itemList for drop-down
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            long supplierId = Long.parseLong(result.supplierId.toString())
            long itemTypeId = Long.parseLong(result.itemTypeId.toString())

            List<GroovyRowResult> itemList = listItemBySupplierId(supplierId, itemTypeId)
            result.put(ITEM_LIST, itemService.listForKendoDropdown(itemList, null, null))
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

    /**
     * get list of specific type itemList for drop-down
     *
     * @param supplierId -Supplier.id
     * @param itemTypeId -ItemType.id
     * @return -list of groovyRowResult
     */
    private List<GroovyRowResult> listItemBySupplierId(long supplierId, long itemTypeId) {
        String queryStr = """
                        SELECT id, name, unit, code FROM item
                        WHERE item_type_id = :itemTypeId AND
                              company_id = :companyId AND
                              id NOT IN (SELECT item_id FROM supplier_item
                                                WHERE supplier_id = :supplierId)
                        ORDER BY name
                        """
        Map queryParams = [
                itemTypeId: itemTypeId,
                companyId: super.getCompanyId(),
                supplierId: supplierId
        ]
        List<GroovyRowResult> lstItem = executeSelectSql(queryStr, queryParams)
        return lstItem
    }
}
