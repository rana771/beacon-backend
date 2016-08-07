package com.mis.beacon.zone

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.mis.beacon.Zone
import com.athena.mis.ActionServiceIntf
import com.athena.mis.utility.DateUtility
import com.mis.beacon.service.ZoneService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional


class CreateZoneActionService  extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String ZONE = "zone"
    private static final String  ZONE_SAVE_SUCCESS_MESSAGE = "Zone has been saved successfully"

    ZoneService zoneService

    /**
     * 1. check Validation
     * 2. build zone object
     *
     * @param params - serialized parameters from UI
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            AppUser user = super.getAppUser();
            // check Validation
            String errMsg = checkValidation(params, user)
            if (errMsg) {
                return super.setError(params, errMsg)
            }
            // build zone object
            Zone zone = getZone(params, user)
            params.put(ZONE, zone)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive zone object from executePreCondition method
     * 2. create new zone
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Zone zone = (Zone) result.get(ZONE)
            // save new zone object in DB
            zoneService.create(zone)
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
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, ZONE_SAVE_SUCCESS_MESSAGE)
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
     * Build zone object
     *
     * @param params - serialize parameters from UI
     * @param user - an object of AppUser
     * @return - zone object
     */
    private Zone getZone(Map params, AppUser user) {
        Zone zone = new Zone(params)
        zone.createdOn = new Date()
        zone.createdBy = user.id
        zone.companyId = user.companyId
        zone.updatedBy = 0
        zone.updatedOn = null
        return zone
    }

    /**
     * 1. check for duplicate zone name
     * 2. check for duplicate zone code
     *
     * @param zone -object of Zone
     * @param user - an object of AppUser
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params, AppUser user) {
        String errMsg
            //write your validation message here

        return null
    }



}

