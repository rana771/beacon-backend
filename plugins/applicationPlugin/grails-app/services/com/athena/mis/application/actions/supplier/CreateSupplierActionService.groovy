package com.athena.mis.application.actions.supplier

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.model.ListSupplierActionServiceModel
import com.athena.mis.application.service.ListSupplierActionServiceModelService
import com.athena.mis.application.service.SupplierService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create supplier object and to show on grid list
 *  For details go through Use-Case doc named 'CreateSupplierActionService'
 */
class CreateSupplierActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SUPPLIER = "supplier"
    private static final String SAVE_SUCCESS_MESSAGE = "Supplier has been saved successfully"
    private static final String NAME_ALREADY_EXISTS = "Same supplier name already exists"

    SupplierService supplierService
    ListSupplierActionServiceModelService listSupplierActionServiceModelService

    /**
     * Check different criteria for creating new supplier
     *      1) Validate supplier object
     *      2) Check duplicate supplier name
     * @param params -N/A
     * @param obj -Supplier object send from controller
     * @return -a map containing supplier object for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // Check parameters
            if ((!params.name) || (!params.supplierTypeId)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            int duplicateCount = supplierService.countByNameIlikeAndCompanyId(params.name, super.companyId)
            if (duplicateCount > 0) {
                return super.setError(params, NAME_ALREADY_EXISTS)
            }
            Supplier supplier = getSupplier(params)
            params.put(SUPPLIER, supplier)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Save supplier object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -supplierObject send from controller
     * @return -newly created supplier object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Supplier supplier = (Supplier) result.get(SUPPLIER)
            supplierService.create(supplier)//save in DB
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
     * Wrap newly created supplier to show on grid
     * @param obj -newly created supplier object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            Supplier supplier = (Supplier) result.get(SUPPLIER)
            ListSupplierActionServiceModel supplierModel = listSupplierActionServiceModelService.read(supplier.id)
            result.put(SUPPLIER, supplierModel)
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
     * Build Supplier object
     * @param parameterMap -serialized parameters from UI
     * @return -new Supplier object
     */
    private Supplier getSupplier(Map params) {
        Supplier supplier = new Supplier(params)
        AppUser appUser = super.getAppUser()
        supplier.itemCount = 0
        supplier.companyId = appUser.companyId
        supplier.createdOn = new Date()
        supplier.createdBy = appUser.id
        return supplier
    }
}
