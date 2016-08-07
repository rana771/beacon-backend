package com.mis.beacon.zone

import com.mis.beacon.Zone
import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.utility.DateUtility
import com.mis.beacon.service.ZoneService
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


class UpdateZoneActionService extends BaseService implements ActionServiceIntf {

     private Logger log = Logger.getLogger(getClass())

    private static final String ZONE = "zone"
    private static final String ZONE_UPDATE_SUCCESS_MESSAGE = "Zone has been updated successfully"

    ZoneService zoneService
    @Autowired(required = false)

    /**
     * 1. Check Validation
     * 2. Check un-approve transactions for auto approve
     * 3. Build zone object for update
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

            // check your custom valiation

            // build zone object for update
            getZone(params)

            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the zone object from map
     * 2. Update existing zone in DB
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Zone zone = (Zone) result.get(ZONE)
            zoneService.update(zone)
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
        return super.setSuccess(result, ZONE_UPDATE_SUCCESS_MESSAGE)
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
     * Build zone object for update
     *
     * @param params - serialize parameters from UI
     * @return - zone object
     */
    private Zone getZone(Map params) {
        Zone oldZone = (Zone) params.get(ZONE)
        Zone newZone = new Zone(params)
        oldZone.name = newZone.name
        oldZone.code = newZone.code
        AppUser systemUser = super.getAppUser()
        oldZone.updatedOn = new Date()
        oldZone.updatedBy = systemUser.id

        // write approval flag holds previous state if user is not config manager

        return oldZone
    }

    /**
     * 1. Check Zone object existance
     * 2. Check for duplicate zone code
     * 3. Check for duplicate zone name
     * 4. Check parameters
     *
     * @param zone - object of Zone
     * @param params - a map from caller method
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params) {
        String errMsg
        //Check parameters

        long zoneId = Long.parseLong(params.id.toString())
        Zone zone = zoneService.read(zoneId)

        //check Zone object existance
        errMsg = checkZoneExistance(zone, params)
        if (errMsg != null) return errMsg

        // Check your custom validation here

        params.put(ZONE, zone)
        return null
    }

    /**
     * check Zone object existance
     *
     * @param zone - an object of Zone
     * @param params - a map from caller method
     * @return - error message or null
     */

    private String checkZoneExistance(Zone zone, Map params) {
        long version = Long.parseLong(params.version.toString())
        if (!zone || zone.version != version) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

}
