package com.mis.beacon.marchant

import com.mis.beacon.Marchant
import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.utility.DateUtility
import com.mis.beacon.service.MarchantService
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


class UpdateMarchantActionService extends BaseService implements ActionServiceIntf {

     private Logger log = Logger.getLogger(getClass())

    private static final String MARCHANT = "marchant"
    private static final String PROJECT_CODE_ALREADY_EXISTS = "Same marchant code already exists"
    private static final String PROJECT_NAME_ALREADY_EXISTS = "Same marchant name already exists"
    private static final String PROJECT_UPDATE_SUCCESS_MESSAGE = "Marchant has been updated successfully"
    private static
    final String HAS_UNAPPROVED_CONSUMPTION = " Consumption(s) is unapproved of this marchant. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_PRODUCTION = " Production(s) is unapproved of this marchant. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_OUT = " Inventory Out(s) is unapproved of this marchant. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_IN_FROM_INVENTORY = " In from Inventory(s) is unapproved of this marchant. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_IN_FROM_SUPPLIER = " In from Supplier(s) is unapproved of this marchant. Approve all transactions to change auto approve property"

    MarchantService marchantService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService

    /**
     * 1. Check Validation
     * 2. Check un-approve transactions for auto approve
     * 3. Build marchant object for update
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            //Check Validation
            String errMsg = checkValidation(params)
            if (errMsg) {
                return super.setError(params, errMsg)
            }

            // check un-approve transactions for auto approve
            if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
                String msg = checkUnApproveTransactionsForAutoApprove(params)
                if (msg) {
                    return super.setError(params, msg)
                }
            }
            // build marchant object for update
            getMarchant(params)

            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the marchant object from map
     * 2. Update existing marchant in DB
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Marchant marchant = (Marchant) result.get(MARCHANT)
            marchantService.update(marchant)
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
     * 1. put success message
     *
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, PROJECT_UPDATE_SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build marchant object for update
     *
     * @param params - serialize parameters from UI
     * @return - marchant object
     */
    private Marchant getMarchant(Map params) {
        Marchant oldMarchant = (Marchant) params.get(MARCHANT)

        Marchant newMarchant = new Marchant(params)
        oldMarchant.name = newMarchant.name
        oldMarchant.description = newMarchant.description
        oldMarchant.companyAddress = newMarchant.companyAddress
        oldMarchant.companyPhone = newMarchant.companyPhone
        oldMarchant.email = newMarchant.email
        oldMarchant.apiKey = newMarchant.apiKey

        return oldMarchant
    }

    /**
     * 1. Check Marchant object existance
     * 2. Check for duplicate marchant code
     * 3. Check for duplicate marchant name
     * 4. Check parameters
     *
     * @param marchant - object of Marchant
     * @param params - a map from caller method
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params) {
        String errMsg
        //Check parameters
        if (!params.id)  {
            return ERROR_FOR_INVALID_INPUT
        }
        long marchantId = Long.parseLong(params.id.toString())
        Marchant marchant = marchantService.read(marchantId)


        if (errMsg != null) return errMsg
        params.put(MARCHANT, marchant)
        return null
    }

    /**
     * check Marchant object existance
     *
     * @param marchant - an object of Marchant
     * @param params - a map from caller method
     * @return - error message or null
     */

    private String checkMarchantExistance(Marchant marchant, Map params) {
        long version = Long.parseLong(params.version.toString())
        if (!marchant || marchant.version != version) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

    /**
     * check for duplicate marchant code
     *
     * @param marchant - an object of Marchant
     * @param params - a map from caller method
     * @return - error message or null
     */
    private String checkMarchantCountByCode(Marchant marchant, Map params) {
        int count = marchantService.countByCodeIlikeAndCompanyIdAndIdNotEqual(params.code, marchant.companyId, marchant.id)
        if (count > 0) {
            return PROJECT_CODE_ALREADY_EXISTS
        }
        return null
    }

    /**
     * check for duplicate marchant name
     *
     * @param marchant - an object of Marchant
     * @param params - a map from caller method
     * @return - error message or null
     */
    private String checkMarchantCountByName(Marchant marchant, Map params) {
        int count = marchantService.countByNameIlikeAndCompanyIdAndIdNotEqual(params.name, marchant.companyId, marchant.id)
        if (count > 0) {
            return PROJECT_NAME_ALREADY_EXISTS
        }
        return null
    }

    private static final String STR_QUERY = """
        SELECT COUNT(iitd.id) AS count
        FROM inv_inventory_transaction_details iitd
        LEFT JOIN inv_inventory_transaction iit ON iit.id = iitd.inventory_transaction_id
        WHERE approved_by = 0
        AND iit.transaction_type_id = :transactionTypeId
        AND iit.transaction_entity_type_id = :transactionEntityTypeId
        AND iit.marchant_id = :marchantId
    """

    /**
     * Check un-approve transactions for auto approve
     *
     * @param params -serialized parameters from UI
     * @param marchant -object of Marchant
     * @return -a string containing relevant message or null value depending on count of un-approved transactions
     */
    private String checkUnApproveTransactionsForAutoApprove(Map params) {
        Marchant marchant = (Marchant) params.get(MARCHANT)
        Marchant newMarchant = new Marchant(params)

        String msg = null
        long transactionTypeId
        long transactionEntityTypeId
        int count
        if (!marchant.isApproveConsumption && newMarchant.isApproveConsumption) {
            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdConsumption()
            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdNone()
            Map queryParams = [
                    transactionTypeId: transactionTypeId,
                    transactionEntityTypeId: transactionEntityTypeId,
                    marchantId: marchant.id
            ]
            List result = executeSelectSql(STR_QUERY, queryParams)
            count = result[0].count
            if (count > 0) {
                return count + HAS_UNAPPROVED_CONSUMPTION
            }
        }
        if (!marchant.isApproveProduction && newMarchant.isApproveProduction) {
            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdProduction()
            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdNone()
            Map queryParams = [
                    transactionTypeId: transactionTypeId,
                    transactionEntityTypeId: transactionEntityTypeId,
                    marchantId: marchant.id
            ]
            List result = executeSelectSql(STR_QUERY, queryParams)
            count = result[0].count
            if (count > 0) {
                return count + HAS_UNAPPROVED_PRODUCTION
            }
        }
        if (!marchant.isApproveInvOut && newMarchant.isApproveInvOut) {
            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdOut()
            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdInventory()
            Map queryParams = [
                    transactionTypeId: transactionTypeId,
                    transactionEntityTypeId: transactionEntityTypeId,
                    marchantId: marchant.id
            ]
            List result = executeSelectSql(STR_QUERY, queryParams)
            count = result[0].count
            if (count > 0) {
                return count + HAS_UNAPPROVED_OUT
            }
        }
        if (!marchant.isApproveInFromInventory && newMarchant.isApproveInFromInventory) {
            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdIn()
            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdInventory()
            Map queryParams = [
                    transactionTypeId: transactionTypeId,
                    transactionEntityTypeId: transactionEntityTypeId,
                    marchantId: marchant.id
            ]
            List result = executeSelectSql(STR_QUERY, queryParams)
            count = result[0].count
            if (count > 0) {
                return count + HAS_UNAPPROVED_IN_FROM_INVENTORY
            }
        }
        if (!marchant.isApproveInFromSupplier && newMarchant.isApproveInFromSupplier) {
            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdIn()
            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdSupplier()
            Map queryParams = [
                    transactionTypeId: transactionTypeId,
                    transactionEntityTypeId: transactionEntityTypeId,
                    marchantId: marchant.id
            ]
            List result = executeSelectSql(STR_QUERY, queryParams)
            count = result[0].count
            if (count > 0) {
                return count + HAS_UNAPPROVED_IN_FROM_SUPPLIER
            }
        }
        return msg
    }
}
