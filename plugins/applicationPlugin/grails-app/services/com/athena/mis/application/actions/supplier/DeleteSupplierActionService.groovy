package com.athena.mis.application.actions.supplier

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.service.SupplierService
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete supplier object from DB as well as from cache
 *  For details go through Use-Case doc named 'DeleteSupplierActionService'
 */
class DeleteSupplierActionService extends BaseService implements ActionServiceIntf {

    private static final String DELETE_SUCCESS_MESSAGE = "Supplier has been deleted successfully"
    private static final String SUPPLIER_HAS_ITEMS = "Supplier has association with one or more items"
    private static final String HAS_ASSOCIATION_PURCHASE_ORDER = " purchase order is associated with selected supplier"
    private static final String HAS_ASSOCIATION_INVENTORY_TRANSACTION = " inventory transaction is associated with selected supplier"
    private static final String HAS_ASSOCIATION_VOUCHER_DETAILS = " voucher information is associated with this supplier"
    private static final String HAS_ASSOCIATION_LC = " LC(s) is associated with this supplier"
    private static final String SUPPLIER = "supplier"

    private Logger log = Logger.getLogger(getClass())

    SupplierService supplierService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService

    /**
     * Check different criteria to delete supplier object
     *      1) Check existence of supplier object
     *      2) Check if supplier has any item(s)
     *      3) check association with different domain(s) of installed plugin(s)
     * @param params -parameters from UI
     * @return -a map contains isError(true/false) depending on method success
     */
    public Map executePreCondition(Map params) {
        try {
            long supplierId = Long.parseLong(params.id.toString())
            Supplier supplier = supplierService.read(supplierId)
            if (!supplier) { //check existence of supplier object
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }

            if (supplier.itemCount > 0) { //check if supplier has any associated item(s)
                return super.setError(params, SUPPLIER_HAS_ITEMS)
            }

            //check association
            Map associationResult = (Map) hasAssociation(supplier)
            Boolean hasAssociation = (Boolean) associationResult.get(HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {//if association found then return with message
                String message =  associationResult.get(MESSAGE)
                return super.setError(params, message)
            }
            params.put(SUPPLIER, supplier)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * delete supplier object from DB
     * @param result -received from pre execute method
     * @return -received map
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Supplier supplier = (Supplier) result.get(SUPPLIER)
            supplierService.delete(supplier) //delete from DB
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * do nothing for post operation
     * return the same map
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * set success message
     * @param result -received from post execute method
     * @return -the received map
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DELETE_SUCCESS_MESSAGE)
    }

    /**
     * do nothing for failure operation
     * return the same map
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * method to check if supplier has any association with different domain(s) of installed plugin(s)
     * @param supplier -Supplier object
     * @return -a map contains booleanValue(true/false) and association message
     *          based on existence of association
     */
    private LinkedHashMap hasAssociation(Supplier supplier) {
        LinkedHashMap result = new LinkedHashMap()
        long supplierId = supplier.id
        long companyId = supplier.companyId
        int count = 0
        result.put(HAS_ASSOCIATION, Boolean.TRUE)

        if (PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)) {
            count = countPurchaseOrder(supplierId, companyId)
            if (count.intValue() > 0) {
                result.put(MESSAGE, count.toString() + HAS_ASSOCIATION_PURCHASE_ORDER)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
            count = countInventoryTransaction(supplierId, companyId)
            if (count.intValue() > 0) {
                result.put(MESSAGE, count.toString() + HAS_ASSOCIATION_INVENTORY_TRANSACTION)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
            count = countVoucherDetails(supplierId)
            if (count.intValue() > 0) {
                result.put(MESSAGE, count.toString() + HAS_ASSOCIATION_VOUCHER_DETAILS)
                return result
            }

            count = countLc(supplierId, companyId)
            if (count.intValue() > 0) {
                result.put(MESSAGE, count.toString() + HAS_ASSOCIATION_LC)
                return result
            }
        }

        result.put(HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String QUERY_PROC_PURCHASE_ORDER = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_order
            WHERE supplier_id = :supplierId AND
                  company_id = :companyId
            """
    /**
     * count number of PO of a specific supplier
     * @param supplierId -Supplier.id
     * @param companyId -Company.id
     * @return -int value
     */
    private int countPurchaseOrder(long supplierId, long companyId) {
        Map queryParams = [
                supplierId: supplierId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_PROC_PURCHASE_ORDER, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_INV_INVENTORY_TRANSACTION = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction
            WHERE transaction_entity_type_id = :transactionEntityTypeId
              AND transaction_entity_id = :supplierId
              AND company_id = :companyId """
    /**
     * count number of In-From-Supplier transaction(InvInventoryTransaction) of a specific supplier
     * @param supplierId -Supplier.id
     * @param companyId -Company.id
     * @return -int value
     */
    private int countInventoryTransaction(long supplierId, long companyId) {
        Map queryParams = [
                transactionEntityTypeId: invInventoryImplService.getTransactionEntityTypeIdSupplier(),
                supplierId: supplierId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_INV_INVENTORY_TRANSACTION, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_ACC_VOUCHER_DETAILS = """
            SELECT COUNT(id) AS count
            FROM acc_voucher_details
            WHERE source_type_id = :sourceTypeId
            AND source_id = :supplierId
            """
    /**
     * count number of voucher(s) related with a specific supplier
     * @param supplierId -Supplier.id
     * @return -int value
     */
    private int countVoucherDetails(long supplierId) {
        Map queryParams = [
                sourceTypeId: accAccountingImplService.getAccSourceTypeSupplier(),
                supplierId: supplierId
        ]
        List results = executeSelectSql(QUERY_ACC_VOUCHER_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_ACC_LC = """
            SELECT COUNT(id) AS count
            FROM acc_lc
            WHERE supplier_id = :supplierId AND
                  company_id = :companyId
            """
    /**
     * count number of LC(s) related with a specific supplier
     * @param supplierId -Supplier.id
     * @param companyId -Company.id
     * @return -int value
     */
    private int countLc(long supplierId, long companyId) {
        Map queryParams = [
                supplierId: supplierId,
                companyId: companyId
        ]
        List results = executeSelectSql(QUERY_ACC_LC, queryParams)
        int count = results[0].count
        return count
    }
}
