package com.mis.beacon.beacon

import com.mis.beacon.Beacon
import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.utility.DateUtility
import com.mis.beacon.service.BeaconService
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


class UpdateBeaconActionService extends BaseService implements ActionServiceIntf {

     private Logger log = Logger.getLogger(getClass())

    private static final String BEACON = "beacon"
    private static final String PROJECT_CODE_ALREADY_EXISTS = "Same beacon code already exists"
    private static final String PROJECT_NAME_ALREADY_EXISTS = "Same beacon name already exists"
    private static final String PROJECT_UPDATE_SUCCESS_MESSAGE = "Beacon has been updated successfully"
    private static
    final String HAS_UNAPPROVED_CONSUMPTION = " Consumption(s) is unapproved of this beacon. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_PRODUCTION = " Production(s) is unapproved of this beacon. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_OUT = " Inventory Out(s) is unapproved of this beacon. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_IN_FROM_INVENTORY = " In from Inventory(s) is unapproved of this beacon. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_IN_FROM_SUPPLIER = " In from Supplier(s) is unapproved of this beacon. Approve all transactions to change auto approve property"

    BeaconService beaconService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService

    /**
     * 1. Check Validation
     * 2. Check un-approve transactions for auto approve
     * 3. Build beacon object for update
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


            getBeacon(params)

            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the beacon object from map
     * 2. Update existing beacon in DB
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Beacon beacon = (Beacon) result.get(BEACON)
            beaconService.update(beacon)
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
     * Build beacon object for update
     *
     * @param params - serialize parameters from UI
     * @return - beacon object
     */
    private Beacon getBeacon(Map params) {
        Beacon oldBeacon = (Beacon) params.get(BEACON)

        Beacon newBeacon = new Beacon(params)
        oldBeacon.name = newBeacon.name
        oldBeacon.minor = newBeacon.minor
        oldBeacon.major = newBeacon.major
        oldBeacon.uuid = newBeacon.uuid
        return oldBeacon
    }

    /**
     * 1. Check Beacon object existance
     * 2. Check for duplicate beacon code
     * 3. Check for duplicate beacon name
     * 4. Check parameters
     *
     * @param beacon - object of Beacon
     * @param params - a map from caller method
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params) {
        String errMsg
        //Check parameters
        if (!params.id){
            return ERROR_FOR_INVALID_INPUT
        }
        long beaconId = Long.parseLong(params.id.toString())
        Beacon beacon = beaconService.read(beaconId)

        //check Beacon object existance
//        errMsg = checkBeaconExistance(beacon, params)
//        if (errMsg != null) return errMsg

//        //check for duplicate beacon code
//        errMsg = checkBeaconCountByCode(beacon, params)
//        if (errMsg != null) return errMsg
//
//        //check for duplicate beacon name
//        errMsg = checkBeaconCountByName(beacon, params)
        if (errMsg != null) return errMsg
        params.put(BEACON, beacon)
        return null
    }

    /**
     * check Beacon object existance
     *
     * @param beacon - an object of Beacon
     * @param params - a map from caller method
     * @return - error message or null
     */

    private String checkBeaconExistance(Beacon beacon, Map params) {
        long version = Long.parseLong(params.version.toString())
        if (!beacon || beacon.version != version) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }
}
