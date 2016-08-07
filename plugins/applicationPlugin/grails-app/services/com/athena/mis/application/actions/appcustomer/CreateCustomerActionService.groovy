package com.athena.mis.application.actions.appcustomer

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppCustomer
import com.athena.mis.application.service.AppCustomerService
import com.athena.mis.ActionServiceIntf
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create customer and to show on grid list
 *  For details go through Use-Case doc named 'CreateCustomerActionService'
 */
class CreateCustomerActionService extends BaseService implements ActionServiceIntf {

    AppCustomerService appCustomerService

    private static final String CUSTOMER_CREATE_SUCCESS_MSG = "Customer has been successfully saved"
    private static final String INVALID_INPUT_MSG = "Failed to create due to invalid input"
    private static final String CUSTOMER = "customer"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check input validity
     * @param params - serialized parameters from UI
     * @return - a map containing customer object
     */
    public Map executePreCondition(Map params) {
        try {
            //Check parameters
            if (!params.fullName) {
                return super.setError(params, INVALID_INPUT_MSG)
            }
            AppCustomer customer = buildCustomerObject(params)
            params.put(CUSTOMER, customer)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive customer object from pre execute method
     * 2. create new customer
     * This method is in transactional block and will roll back in case of any exception
     * @param result - map received from pre execute method
     * @return - map.
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppCustomer customer = (AppCustomer) result.get(CUSTOMER)
            appCustomerService.create(customer)
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
        return  super.setSuccess(result, CUSTOMER_CREATE_SUCCESS_MSG)
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
     * Build Customer object
     * @param parameterMap -serialized parameters from UI
     * @return -new Customer object
     */
    private AppCustomer buildCustomerObject(Map parameterMap) {
        parameterMap.dateOfBirth = DateUtility.parseMaskedDate(parameterMap.dateOfBirth.toString())
        AppCustomer customer = new AppCustomer(parameterMap)
        customer.companyId = companyId
        customer.dateOfBirth = customer.dateOfBirth
        customer.createdBy = appUser.id
        customer.createdOn = new Date()
        return customer
    }
}
