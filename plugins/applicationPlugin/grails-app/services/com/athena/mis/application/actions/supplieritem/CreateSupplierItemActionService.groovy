package com.athena.mis.application.actions.supplieritem

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.*
import com.athena.mis.application.model.ListSupplierItemActionServiceModel
import com.athena.mis.application.service.ItemService
import com.athena.mis.application.service.ListSupplierItemActionServiceModelService
import com.athena.mis.application.service.SupplierItemService
import com.athena.mis.application.service.SupplierService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create supplier-item object and to show on grid list
 *  For details go through Use-Case doc named 'CreateSupplierItemActionService'
 */
class CreateSupplierItemActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ITEM = "item"
    private static final String SUPPLIER = "supplier"
    private static final String SUPPLIER_ITEM = "supplierItem"
    private static final String ITEM_NOT_FOUND = "Item not found"
    private static final String SUPPLIER_NOT_FOUND = "Supplier can not found!"
    private static final String SAVE_SUCCESS_MESSAGE = "Supplier-Item has been saved successfully"
    private static final String MATERIAL_ALREADY_EXISTS = "Supplier-Item with same material already exists"

    SupplierService supplierService
    SupplierItemService supplierItemService
    ItemService itemService
    ListSupplierItemActionServiceModelService listSupplierItemActionServiceModelService

    /**
     * Check different criteria for creating new supplier-item object
     *      1) Check existence of supplier & selected item object
     *      2) Check existence of same supplierItem object
     *      3) Validate supplierItem object
     * @param params -serialized parameters send from UI
     * @param obj -N/A
     * @return -a map containing supplierItem object for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            long supplierId = Long.parseLong(params.supplierId.toString())
            Supplier supplier = supplierService.read(supplierId)
            //check existence of supplier object
            if (!supplier) {
                return super.setError(params, SUPPLIER_NOT_FOUND)
            }
            long itemId = Long.parseLong(params.itemId.toString())
            Item item = itemService.read(itemId)
            //check existence of item object
            if (!item) {
                return super.setError(params, ITEM_NOT_FOUND)
            }
            SupplierItem supplierItem = getSupplierItem(params)
            //check existence of same supplierItem object
            SupplierItem existingSupplierItem = supplierItemService.findBySupplierIdAndCompanyIdAndItemId(supplierItem.supplierId, super.companyId, supplierItem.itemId)
            if (existingSupplierItem) {
                return super.setError(params, MATERIAL_ALREADY_EXISTS)
            }
            params.put(ITEM, item)
            params.put(SUPPLIER, supplier)
            params.put(SUPPLIER_ITEM, supplierItem)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Save supplierItem object in DB;also increase itemCount in Supplier domain
     * @param parameters -N/A
     * @param obj -supplierItem Map send from executePreCondition
     * @return -newly created supplierItem object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            SupplierItem supplierItem = (SupplierItem) result.get(SUPPLIER_ITEM)
            Supplier supplier = (Supplier) result.get(SUPPLIER)
            supplierItemService.create(supplierItem)//save in DB
            // update item count for supplier
            updateSupplierItemCount(supplier)
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
     * Wrap newly created supplierItem object to show on grid
     * @param obj -newly created supplierItem object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            SupplierItem supplierItem = (SupplierItem) result.get(SUPPLIER_ITEM)
            ListSupplierItemActionServiceModel supplierItemModel = listSupplierItemActionServiceModelService.read(supplierItem.id)
            result.put(SUPPLIER_ITEM, supplierItemModel)
            return super.setSuccess(result, SAVE_SUCCESS_MESSAGE)
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
     * Build SupplierItem object
     * @param params -serialized parameters from UI
     * @return -new SupplierItem object
     */
    private SupplierItem getSupplierItem(Map params) {
        SupplierItem supplierItem = new SupplierItem(params)
        AppUser appUser = super.getAppUser()
        supplierItem.companyId = appUser.companyId
        supplierItem.createdOn = new Date()
        supplierItem.createdBy = appUser.id
        return supplierItem
    }

    /**
     * Update item count for supplier
     * @param supplier -object of Supplier
     */
    private void updateSupplierItemCount(Supplier supplier) {
        supplier.itemCount = supplier.itemCount + 1
        supplierService.updateForSupplierItem(supplier)//update itemCount of supplier domain in DB
    }
}
