package com.athena.mis.application.actions.item

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Item
import com.athena.mis.application.service.ItemService
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete item (Category: Inventory)
 *  For details go through Use-Case doc named 'DeleteItemCategoryInventoryActionService'
 */
class DeleteItemCategoryInventoryActionService extends BaseService implements ActionServiceIntf {

    ItemService itemService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService

    private static final String ITEM_DELETE_SUCCESS_MSG = "Item has been successfully deleted"
    private static final String HAS_ASSOCIATION_SUPPLIER_ITEM = " supplier(s) associated with selected item"
    private static final String HAS_ASSOCIATION_BUDGET_DETAILS = " budget details(s) associated with selected item"
    private static final String HAS_ASSOCIATION_PURCHASE_REQUEST = " purchase request(s) associated with selected item"
    private static final String HAS_ASSOCIATION_INVENTORY_TRANSACTION = " inventory transaction(s) associated with selected item"
    private static final String HAS_ASSOCIATION_LC = " LC(s) are associated with selected item"
    private static final String HAS_ASSOCIATION_LEASE_ACCOUNT = " Lease Account(s) are associated with selected item"
    private static final String HAS_ASSOCIATION_VOUCHER = " voucher(s) are associated with selected item"
    private static final String ITEM = "item"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to delete item object
     *      1) Check existence of required parameter
     *      2) Check existence of item object
     *      3) check association with different domain(s) of installed plugin(s)
     * @param params -parameters from UI
     * @return -a map containing item object
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            if (!params.itemId) { // check required parameter
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long itemId = Long.parseLong(params.itemId.toString())
            Item item = itemService.read(itemId)
            if (!item) { //check existence of item object
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }

            //check association
            Map preResult = (Map) hasAssociation(item)
            Boolean hasAssociation = (Boolean) preResult.get(HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                String message = preResult.get(MESSAGE)
                return super.setError(params, message)
            }
            params.put(ITEM, item)
            return params
        } catch (Exception e) {
            log.error(e.getMessage())
            throw new RuntimeException(e)
        }
    }

    /**
     * delete item object from DB
     * @param result -received from pre execute method
     * @return - map
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Item item = (Item) result.get(ITEM)
            itemService.delete(item) //delete from DB
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * do nothing for post operation
     * @param result - received form execute method
     * @return - received map
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * set success message
     * @param result - received from post execute method
     * @return - map received form previous method
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, ITEM_DELETE_SUCCESS_MSG)
    }

    /**
     * do nothing in failure method
     * @param result -map returned from previous methods
     * @return -the same map
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * method to check if item has any association with different domain(s) of installed plugin(s)
     * @param item -Item object
     * @return -a map contains booleanValue(true/false) and association message
     *          based on existence of association
     */
    private LinkedHashMap hasAssociation(Item item) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(HAS_ASSOCIATION, Boolean.TRUE)
        long itemId = item.id
        long companyId = item.companyId
        int count = 0

        count = countSupplierItem(itemId)
        if (count > 0) {
            result.put(MESSAGE, count.toString() + HAS_ASSOCIATION_SUPPLIER_ITEM)
            return result
        }

        if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
            count = countBudgetDetails(itemId, companyId)
            if (count > 0) {
                result.put(MESSAGE, count.toString() + HAS_ASSOCIATION_BUDGET_DETAILS)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)) {
            count = countPurchaseRequestDetails(itemId, companyId)
            if (count > 0) {
                result.put(MESSAGE, count.toString() + HAS_ASSOCIATION_PURCHASE_REQUEST)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
            count = countInventoryTransactionDetails(itemId)
            if (count > 0) {
                result.put(MESSAGE, count.toString() + HAS_ASSOCIATION_INVENTORY_TRANSACTION)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
            count = countLc(itemId, companyId)
            if (count > 0) {
                result.put(MESSAGE, count.toString() + HAS_ASSOCIATION_LC)
                return result
            }

            count = countLeaseAccount(itemId, companyId)
            if (count > 0) {
                result.put(MESSAGE, count.toString() + HAS_ASSOCIATION_LEASE_ACCOUNT)
                return result
            }

            count = countVoucherDetails(itemId, companyId)
            if (count > 0) {
                result.put(MESSAGE, count.toString() + HAS_ASSOCIATION_VOUCHER)
                return result
            }
        }

        result.put(HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    /**
     * count number of item(s) mapped with supplier(s)
     * @param itemId -Item.id
     * @return -int value
     */
    private int countSupplierItem(long itemId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM supplier_item
            WHERE item_id = ${itemId}
        """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * count number of item(s) associated with budget_details
     * @param itemId -Item.id
     * @param companyId -Company.id
     * @return -int value
     */
    private int countBudgetDetails(long itemId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM budg_budget_details
            WHERE item_id = ${itemId} AND
                  company_id = ${companyId}
        """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * count number of item(s) associated with purchase_request_details
     * @param itemId -Item.id
     * @param companyId -Company.id
     * @return -int value
     */
    private int countPurchaseRequestDetails(long itemId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_request_details
            WHERE item_id = ${itemId} AND
                  company_id = ${companyId}
            """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * count number of item(s) associated with inventory_transaction_details
     * @param itemId -Item.id
     * @return -int value
     */
    public int countInventoryTransactionDetails(long itemId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction_details
            WHERE item_id = ${itemId} """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * count number of item(s) associated with acc_lc
     * @param itemId -Item.id
     * @param companyId -Company.id
     * @return -int value
     */
    public int countLc(long itemId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM acc_lc
            WHERE item_id = ${itemId} AND
                  company_id = ${companyId}
            """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * count number of item(s) associated with acc_lease_account
     * @param itemId -Item.id
     * @param companyId -Company.id
     * @return -int value
     */
    public int countLeaseAccount(long itemId, long companyId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM acc_lease_account
            WHERE item_id = ${itemId} AND
                  company_id = ${companyId}
            """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }

    /**
     * count number of item(s) associated with acc_voucher
     * @param itemId -Item.id
     * @param companyId -Company.id
     * @return -int value
     */
    public int countVoucherDetails(long itemId, long companyId) {
        String queryStr = """
            SELECT COUNT(voucher_details_id) AS count
            FROM vw_acc_voucher_with_details
            WHERE source_type_id = ${accAccountingImplService.getAccSourceTypeItem()} AND
                  source_id = ${itemId} AND
                  company_id = ${companyId}
            """

        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }
}
