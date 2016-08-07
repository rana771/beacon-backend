package com.mis.beacon.beacon

import com.mis.beacon.Beacon
import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.mis.beacon.service.BeaconService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional



class DeleteBeaconActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DELETE_BEACON_SUCCESS_MESSAGE = "Beacon has been deleted successfully"
    private static final String HAS_ASSOCIATION_USER_BEACON = " user is associated with selected beacon"
    private static final String HAS_ASSOCIATION_ATTACHMENT_BEACON = " attachment is associated with selected beacon"
    private static final String HAS_ASSOCIATION_MESSAGE_BUDGET = " budget is associated with selected beacon"
    private static
    final String HAS_ASSOCIATION_MESSAGE_BUDGET_DETAILS = " budget details is associated with selected beacon"
    private static
    final String HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST = " purchase request is associated with selected beacon"
    private static
    final String HAS_ASSOCIATION_MESSAGE_PURCHASE_ORDER = " purchase order is associated with selected beacon"
    private static
    final String HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION = " store transaction is associated with selected beacon"
    private static final String HAS_ASSOCIATION_MESSAGE_INVENTORY = " store is associated with selected beacon"
    private static final String HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS = " voucher is associated with this beacon"
    private static final String BEACON = "beacon"

    AppSystemEntityCacheService appSystemEntityCacheService
    BeaconService beaconService

    /**
     * 1. Check Validation
     * 2. Association check for beacon with different domains
     *
     * @param parameters - serialize parameters from UI
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
            //association check for beacon with different domains
            String associationMsg = hasAssociation(params)
            if (associationMsg != null) {
                return super.setError(params, associationMsg)
            }
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Delete beacon object from DB
     * 1. get the beacon object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Beacon beacon = (Beacon) result.get(BEACON)
            beaconService.delete(beacon.id);
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new RuntimeException(ex);
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
        return super.setSuccess(result, DELETE_BEACON_SUCCESS_MESSAGE)
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     *
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * 1.check required parameter
     * 2.check for beacon existence
     *
     * @param params - a map from caller method
     * @return - error message or null
     */
    private String checkValidation(Map params) {
        // check required parameter
        if (params.id == null) {
            return ERROR_FOR_INVALID_INPUT
        }
        long beaconId = Long.parseLong(params.id.toString())
        Beacon beacon = beaconService.read(beaconId)
        //check for beacon existence
        if (beacon == null) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        params.put(BEACON, beacon)
        return null
    }

    /**
     * 1. check beacon content count
     * 2. check association with entity_id(beacon_id) of app_user_entity(user_beacon)
     * 3. check association with Budget plugin
     * 4. check association with Procurement plugin
     * 5. check association with Inventory plugin
     * 6. check association with Accounting plugin
     *
     * @param params - a map from caller method
     * @return - specific association message
     */
    private String hasAssociation(Map params) {
        Beacon beacon = (Beacon) params.get(BEACON)
        long beaconId = beacon.id

        int count = 0
        String errMsg;

        if (errMsg != null) return errMsg

        return null
    }

    /**
     * 1. check association with beacon_id of voucher_details
     *
     * @param beaconId - Beacon id
     * @return specific association message
     */

    private static final String SELECT_QUERY = """
            SELECT COUNT(id) AS count
            FROM app_user_entity
            WHERE entity_id =:beaconId AND
            entity_type_id =:entityTypeId
    """



    private static final String COUNT_QUERY = """
            SELECT COUNT(id) AS count
            FROM budg_budget
            WHERE beacon_id =:beaconId AND
            company_id =:companyId
    """


}
