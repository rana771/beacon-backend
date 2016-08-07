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
 *  Class to update supplier object and to show on grid list
 *  For details go through Use-Case doc named 'UpdateSupplierActionService'
 */
class UpdateSupplierActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String SUPPLIER = "supplier"
    private static final String UPDATE_SUCCESS_MESSAGE = "Supplier has been updated successfully"
    private static final String SUPPLIER_NOT_FOUND = "Supplier not found"
    private static final String NAME_ALREADY_EXISTS = "Same supplier name already exists"

    SupplierService supplierService
    ListSupplierActionServiceModelService listSupplierActionServiceModelService

    /**
     * Check different criteria to update supplier object
     *      1) Check existence of selected supplier object
     *      2) Validate supplier object to update
     *      3) Check duplicate supplier name
     * @param params -N/A
     * @param obj -Supplier object send from controller
     * @return -a map containing supplier object for execute method
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            //Check parameters
            if ((!params.id) || (!params.version) || (!params.name)) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }

            long id = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())

            //Check existing of Obj and version matching
            Supplier oldSupplier = supplierService.read(id)
            if ((!oldSupplier) || (oldSupplier.version != version)) {
                return super.setError(params, SUPPLIER_NOT_FOUND)
            }
            // Check existing of same vehicle name
            String name = params.name.toString()
            int duplicateCount = supplierService.countByNameIlikeAndCompanyIdAndIdNotEqual(name,super.companyId, oldSupplier.id)
            if (duplicateCount > 0) {
                return super.setError(params, NAME_ALREADY_EXISTS)
            }
            Supplier supplier = getSupplier(params, oldSupplier)
            params.put(SUPPLIER, supplier)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * update supplier object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -supplierObject send from controller
     * @return -updated supplier object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Supplier supplier = (Supplier) result.get(SUPPLIER)
            supplierService.update(supplier)//update in DB
            return result
        }  catch (Exception ex) {
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
     * Wrap updated supplier object to show on grid
     * @param obj -updated supplier object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map buildSuccessResultForUI(Map result) {
        try {
            Supplier supplier = (Supplier) result.get(SUPPLIER)
            ListSupplierActionServiceModel supplierModel = listSupplierActionServiceModelService.read(supplier.id)
            result.put(SUPPLIER, supplierModel)
            return super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
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
     * @param oldSupplier -object of Supplier
     * @return -new Supplier object
     */
    private Supplier getSupplier(Map params, Supplier oldSupplier) {
        Supplier supplier = new Supplier(params)
        AppUser appUser = super.getAppUser()
        oldSupplier.name = supplier.name
        oldSupplier.address = supplier.address
        oldSupplier.accountName = supplier.accountName
        oldSupplier.bankAccount = supplier.bankAccount
        oldSupplier.bankName = supplier.bankName
        oldSupplier.supplierTypeId = supplier.supplierTypeId
        oldSupplier.updatedBy = appUser.id
        oldSupplier.updatedOn = new Date()
        return oldSupplier
    }
}
