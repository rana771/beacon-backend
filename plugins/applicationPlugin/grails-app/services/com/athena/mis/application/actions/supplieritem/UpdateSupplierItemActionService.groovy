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
 *  Class to update supplier-item object and to show on grid list
 *  For details go through Use-Case doc named 'UpdateSupplierItemActionService'
 */
class UpdateSupplierItemActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ITEM = "item"
    private static final String ITEM_NOT_FOUND = "Item can not found!"
    private static final String SUPPLIER_ITEM = "supplierItem"
    private static final String SUPPLIER_NOT_FOUND = "Supplier can not found!"
    private static final String OBJECT_NOT_FOUND = "Supplier Item can not found!"
    private static final String UPDATE_SUCCESS_MESSAGE = "Supplier's Item has been updated successfully"

    SupplierService supplierService
    SupplierItemService supplierItemService
    ItemService itemService
    ListSupplierItemActionServiceModelService listSupplierItemActionServiceModelService

    /**
     * Check different criteria for updating supplier-item object
     *      1) Check existence of required parameters
     *      2) Check existence of supplier & selected item object
     *      3) Check existence of old supplierItem object
     *      4) Validate supplierItem object
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // check here for required params are present
            if ((!params.id) || (!params.version) || (!params.supplierId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long supplierId = Long.parseLong(params.supplierId.toString())
            // read Supplier object from service
            Supplier supplier = supplierService.read(supplierId)
            //check existence of supplier object
            if (!supplier) {
                return super.setError(params, SUPPLIER_NOT_FOUND)
            }
            long itemId = Long.parseLong(params.itemId.toString())
            // read Item object from service
            Item item = itemService.read(itemId)
            //check existence of item object
            if (!item) {
                return super.setError(params, ITEM_NOT_FOUND)
            }

            long id = Long.parseLong(params.id.toString())
            // read SupplierItem object from service
            SupplierItem oldSupplierItem = supplierItemService.read(id)
            //check existence of item object
            if (!oldSupplierItem) {
                return super.setError(params, OBJECT_NOT_FOUND)
            }
            // get new supplierItem object
            SupplierItem supplierItem = getSupplierItem(params, oldSupplierItem)
            if (oldSupplierItem && (oldSupplierItem.version != supplierItem.version)) {
                return super.setError(params, OBJECT_NOT_FOUND)
            }
            params.put(SUPPLIER_ITEM, supplierItem)
            params.put(ITEM, item)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the supplierItem object from map
     * 2. update supplierItem object in DB
     * This method is in transactional block and will roll back in case of any exception
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            SupplierItem supplierItem = (SupplierItem) result.get(SUPPLIER_ITEM)
            //update in DB
            supplierItemService.update(supplierItem)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
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
     * Wrap updated supplierItem object to show on grid
     * @param obj -updated supplierItem object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            SupplierItem supplierItem = (SupplierItem) result.get(SUPPLIER_ITEM)
            ListSupplierItemActionServiceModel supplierItemModel = listSupplierItemActionServiceModelService.read(supplierItem.id)
            result.put(SUPPLIER_ITEM, supplierItemModel)
            return super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build SupplierItem object
     * @param params -serialized parameters from UI
     * @param oldSupplierItem -object of SupplierItem
     * @return -new SupplierItem object
     */
    private SupplierItem getSupplierItem(Map params, SupplierItem oldSupplierItem) {
        SupplierItem supplierItem = new SupplierItem(params)
        AppUser appUser = super.getAppUser()
        oldSupplierItem.itemId = supplierItem.itemId
        oldSupplierItem.updatedBy = appUser.id
        oldSupplierItem.updatedOn = new Date()
        return oldSupplierItem
    }
}
