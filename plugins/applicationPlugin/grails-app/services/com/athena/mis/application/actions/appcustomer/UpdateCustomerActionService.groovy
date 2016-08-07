package com.athena.mis.application.actions.appcustomer

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppCustomer
import com.athena.mis.application.service.AppCustomerService
import com.athena.mis.ActionServiceIntf
import com.athena.mis.utility.DateUtility
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update customer CRUD and to show on grid list
 *  For details go through Use-Case doc named 'UpdateCustomerActionService'
 */
class UpdateCustomerActionService extends BaseService implements ActionServiceIntf {

    AppCustomerService appCustomerService

    private static final String OBJ_CHANGED_MSG = "Selected customer has been changed, Refresh the page again"
    private static final String INVALID_INPUT_MSG = "Failed to update due to invalid input"
    private static final String CUSTOMER_UPDATE_SUCCESS_MESSAGE = "Customer has been updated successfully"
    private static final String CUSTOMER = "customer"

    private Logger log = Logger.getLogger(getClass())

    /**
     * 1. check input validation
     * 2. check object existence
     * 3. build object
     * @param parameters - serialized parameters from UI
     * @return -a map containing customer object
     */
    public Map executePreCondition(Map params) {
        try {
            //Check parameters
            if ((!params.id) || (!params.version) || (!params.fullName)) {
                return super.setError(params, INVALID_INPUT_MSG)
            }
            long id = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())

            //Check existing of Obj and version matching
            AppCustomer oldCustomer = (AppCustomer) appCustomerService.read(id)
            if ((!oldCustomer) || oldCustomer.version != version) {
                return super.setError(params, OBJ_CHANGED_MSG)
            }
            AppCustomer customer = buildObject(params, oldCustomer)
            params.put(CUSTOMER, customer)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Update customer object in DB
     * @param result -customerObject from executePreCondition method
     * @return -a map containing customer object
     */
    @Transactional
    public Map execute(Map result) {
        try {
            AppCustomer customer = (AppCustomer) result.get(CUSTOMER)
            appCustomerService.update(customer)
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
     * 1. set success messagge
     * @param result - map received from post method
     * @return - map
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, CUSTOMER_UPDATE_SUCCESS_MESSAGE)
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
     * @param oldCustomer -object of Customer
     * @return -new Customer object
     */
    private AppCustomer buildObject(Map parameterMap, AppCustomer oldCustomer) {
        parameterMap.dateOfBirth = DateUtility.parseMaskedDate(parameterMap.dateOfBirth.toString())
        AppCustomer customer = new AppCustomer(parameterMap)
        oldCustomer.fullName = customer.fullName
        oldCustomer.nickName = customer.nickName
        oldCustomer.phoneNo = customer.phoneNo
        oldCustomer.email = customer.email
        oldCustomer.address = customer.address
        oldCustomer.dateOfBirth = customer.dateOfBirth
        oldCustomer.updatedBy = super.appUser.id
        oldCustomer.updatedOn = new Date()
        return oldCustomer
    }
}
