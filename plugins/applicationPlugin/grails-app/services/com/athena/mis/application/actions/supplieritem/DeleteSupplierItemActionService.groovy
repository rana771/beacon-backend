package com.athena.mis.application.actions.supplieritem

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.entity.SupplierItem
import com.athena.mis.application.service.SupplierItemService
import com.athena.mis.application.service.SupplierService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete supplierItem object from DB as well as from cache
 *  For details go through Use-Case doc named 'DeleteSupplierItemActionService'
 */
class DeleteSupplierItemActionService extends BaseService implements ActionServiceIntf {

    SupplierService supplierService
    SupplierItemService supplierItemService

    private static final String DELETE_SUCCESS_MESSAGE = "Supplier item has been deleted successfully"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String SUPPLIER_ITEM_NOT_FOUND = "Supplier item not found"
    private static final String SUPPLIER_ITEM_OBJ = "supplierItem"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to delete supplier-item object
     *      1) Check existence of required parameter
     *      2) Check existence of supplierItem object
     * @param params -parameters from UI
     * @return -a map containing supplierItem object for execute method
     */
    public Map executePreCondition(Map params) {
        try {
            if ((!params.id)) { //check existence of parameter
                return super.setError(params, INPUT_VALIDATION_FAIL_ERROR)
            }

            long supplierItemId = Long.parseLong(params.id.toString())
            SupplierItem supplierItem = supplierItemService.read(supplierItemId)
            if (!supplierItem) { //check existence of supplierItem object
                return super.setError(params, SUPPLIER_ITEM_NOT_FOUND)
            }

            params.put(SUPPLIER_ITEM_OBJ, supplierItem)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * delete supplierItem object from DB; Also decrease itemCount from Supplier domain
     * @param result -supplierItem Object send from executePreCondition
     * @return - map containing all necessary objects
     */
    @Transactional
    public Map execute(Map result) {
        try {
            SupplierItem supplierItem = (SupplierItem) result.get(SUPPLIER_ITEM_OBJ)
            supplierItemService.delete(supplierItem) // delete from DB
            // update item count for supplier
            updateSupplierItemCount(supplierItem.supplierId)
            return result
        }  catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * do nothing for post operation
     * @return - the same received map
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * set success message
     * @param result - map received from post operation method
     * @return -same received map
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DELETE_SUCCESS_MESSAGE)
    }

    /**
     * do nothing for post operation
     * @return - the same received map
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Update item count for supplier
     * @param supplierId -Supplier.id
     */
    private void updateSupplierItemCount(long supplierId) {
        Supplier supplier = supplierService.read(supplierId)
        supplier.itemCount = supplier.itemCount - 1
        supplierService.updateForSupplierItem(supplier)//decrease itemCount in DB
    }
}
