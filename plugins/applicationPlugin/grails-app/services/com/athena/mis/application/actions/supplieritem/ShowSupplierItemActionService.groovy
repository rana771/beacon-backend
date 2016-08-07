package com.athena.mis.application.actions.supplieritem

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.SupplierService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for supplier-item CRUD and list of supplierItem for grid
 *  For details go through Use-Case doc named 'ShowSupplierItemActionService'
 */
class ShowSupplierItemActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SUPPLIER = "supplier"
    private static final String SUPPLIER_NOT_FOUND = "Supplier can not found!"

    SupplierService supplierService
    AppMyFavouriteService appMyFavouriteService

    /**
     * check different criteria to show supplier-item page
     *          1) check existence of required parameter
     *          2) check existence of SUPPLIER object
     * @param parameters -parameter from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Map executePreCondition(Map params) {
        //check existence of required parameter
        if (!params.oId) {
            return super.setError(params, ERROR_FOR_INVALID_INPUT)
        }
        return params
    }

    /**
     * get list of supplierItem Objects
     * @param parameters -parameters from UI
     * @param obj -a map contains supplier object from preCondition
     * @return -a map contains supplierItem List and count
     */
    @Transactional
    public Map execute(Map result) {
        try {
            long supplierId = Long.parseLong(result.oId.toString())
            Supplier supplier = supplierService.read(supplierId)
            //check existence of supplier object
            if (!supplier) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, SUPPLIER_NOT_FOUND)
            }
            result.put(SUPPLIER, supplier)
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
     * wrap supplierItemObjectList to show on grid
     * @param obj -a map contains supplierItemObjectList and count
     * @return -wrapped supplierObjectList to show on grid
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }
}
