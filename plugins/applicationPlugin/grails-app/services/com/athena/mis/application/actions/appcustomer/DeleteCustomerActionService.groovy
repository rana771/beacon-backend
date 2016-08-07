package com.athena.mis.application.actions.appcustomer

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppCustomer
import com.athena.mis.application.service.AppCustomerService
import com.athena.mis.integration.accounting.AccPluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to delete customer object from DB as well as from cache
 *  For details go through Use-Case doc named 'DeleteCustomerActionService'
 */
class DeleteCustomerActionService extends BaseService implements ActionServiceIntf {

    AppCustomerService appCustomerService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService

    private static final String HAS_ASSOCIATION_MESSAGE = " voucher information is associated with this customer"
    private static final String DELETE_SUCCESS_MESSAGE = "Customer has been deleted successfully"
    private static final String CUSTOMER = "customer"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check different criteria to delete customer object
     *      1) Check existence of customer object
     *      2) Check association with AccVoucherDetails
     * @param parameters -parameters from UI
     * @return -a map containing customer object.
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            long customerId = Long.parseLong(params.id.toString())
            AppCustomer customer = (AppCustomer) appCustomerService.read(customerId)
            if (!customer) {//check existence on object
                return super.setError(params, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            //Check association
            Map associationResult = (Map) hasAssociation(customer)
            Boolean hasAssociation = (Boolean) associationResult.get(HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                String message = associationResult.get(MESSAGE)
                return super.setError(params, message)
            }
            params.put(CUSTOMER, customer)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Delete customer object from DB
     * 1. delete selected customer
     * This function is in transactional boundary and will roll back in case of any exception
     * @param result - parameters from pre execute method
     * @return - map
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppCustomer customer = (AppCustomer) result.get(CUSTOMER)
            appCustomerService.delete(customer)
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
     * check different association with customer while deleting customer
     * @param customer -Customer object
     * @return -a map contains boolean value(true/false) & association message
     */
    private LinkedHashMap hasAssociation(AppCustomer customer) {
        LinkedHashMap result = new LinkedHashMap()
        int customerId = customer.id
        int count = 0
        result.put(HAS_ASSOCIATION, Boolean.TRUE)

        if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
            count = countVoucherDetails(customerId)
            if (count > 0) {
                result.put(MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE)
                return result
            }
        }
        result.put(HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private int countVoucherDetails(int customerId) {
        String queryStr = """
            SELECT COUNT(id) AS count
            FROM acc_voucher_details
            WHERE source_type_id = ${accAccountingImplService.getAccSourceTypeCustomer()}
            AND source_id = ${customerId}
            """
        List results = executeSelectSql(queryStr)
        int count = results[0].count
        return count
    }
}
