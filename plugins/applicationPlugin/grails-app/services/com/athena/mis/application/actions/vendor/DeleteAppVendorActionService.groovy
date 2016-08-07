package com.athena.mis.application.actions.vendor

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Vendor
import com.athena.mis.application.service.AppDbInstanceService
import com.athena.mis.application.service.VendorService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete vendor object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAppVendorActionService'
 */
class DeleteAppVendorActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String VENDOR = "vendor"
    private static final String DELETE_SUCCESS_MESSAGE = "Vendor has been deleted successfully"
    private static final String ASSOCIATION_MSG_WITH_VENDOR = " DB instance is associated with this Vendor."

    VendorService vendorService
    AppDbInstanceService appDbInstanceService

    /**
     * 1. pull vendor object
     * 2. check for vendor existence
     * 3. association check for vendor
     * @param params - serialize parameters from UI
     * @return - a map containing vendor object.
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            long vendorId = Long.parseLong(params.id.toString())
            Vendor vendor = (Vendor) vendorService.read(vendorId)
            if (vendor == null) {
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }

            String associationMsg = hasAssociation(vendor)
            // association check for appDbInstance with different domains
            if (associationMsg) {
                return super.setError(params, associationMsg)
            }
            params.put(VENDOR, vendor)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }
    /**
     * Delete vendor object from DB
     * 1. delete selected vendor
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameters from pre execute method
     * @return - map
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Vendor vendor = (Vendor) result.get(VENDOR)
            vendorService.delete(vendor)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }
    /**
     *
     * @param result - map received from execute method
     * @return - map
     */
    public Map executePostCondition(Map result) {
        return result
    }
    /**
     *
     * @param result - map received from executePost method
     * @return - map containing success message
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, DELETE_SUCCESS_MESSAGE)
    }
    /**
     *
     * @param result - map received from previous method
     * @return - map
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1. Check association with vendor
     * @param vendor - Vendor object
     * @return - specific association message
     */
    private String hasAssociation(Vendor vendor) {
        int countQuery = appDbInstanceService.countByVendorIdAndCompanyId(vendor.id, vendor.companyId)
        if (countQuery > 0) {
            return countQuery.toString() + ASSOCIATION_MSG_WITH_VENDOR
        }
        return null
    }
}
