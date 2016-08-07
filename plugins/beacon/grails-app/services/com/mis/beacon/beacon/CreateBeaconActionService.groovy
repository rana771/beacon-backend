package com.mis.beacon.beacon

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.mis.beacon.Beacon
import com.athena.mis.ActionServiceIntf
import com.athena.mis.utility.DateUtility
import com.mis.beacon.BeaconTag
import com.mis.beacon.Marchant
import com.mis.beacon.Zone
import com.mis.beacon.service.BeaconService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional


class CreateBeaconActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String BEACON = "beacon"
    private static final String BEACON_TAGS = "beacon_tags"
    private static final String BEACON_CODE_ALREADY_EXISTS = "Same beacon code already exists"
    private static final String BEACON_NAME_ALREADY_EXISTS = "Same beacon name already exists"
    private static final String BEACON_SAVE_SUCCESS_MESSAGE = "Beacon has been saved successfully"

    BeaconService beaconService

    /**
     * 1. check Validation
     * 2. build beacon object
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
            // build beacon object
            Beacon beacon = getBeacon(params, user)
            List<BeaconTag> beaconTagList = getBeaconTag(params.tag, beacon)
            params.put(BEACON, beacon)
            params.put(BEACON_TAGS, beaconTagList)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive beacon object from executePreCondition method
     * 2. create new beacon
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Beacon beacon = (Beacon) result.get(BEACON)
            // save new beacon object in DB
            beaconService.create(beacon)
            List<BeaconTag> beaconTagList = result.get(BEACON_TAGS)
            beaconTagList.each {
                it.save();
            }
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
        return super.setSuccess(result, BEACON_SAVE_SUCCESS_MESSAGE)
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
     * Build beacon object
     *
     * @param params - serialize parameters from UI
     * @param user - an object of AppUser
     * @return - beacon object
     */
    private Beacon getBeacon(Map params, AppUser user) {
        Beacon beacon = new Beacon(params)
        AppUser appUser = super.getAppUser();
        Marchant marchant = Marchant.findByAppUser(appUser);
        // Zone zone=Zone.read(Long.parseLong(params.zone.id.toString()))
        //beacon.zone=zone
        beacon.marchant = marchant;
        return beacon
    }

    /**
     * Build beacon object
     *
     * @param params - serialize parameters from UI
     * @param user - an object of AppUser
     * @return - beacon object
     */
    private List<BeaconTag> getBeaconTag(String tag, Beacon beacon) {
        String[] tags = tag.split(",")
        List<BeaconTag> beaconTagList = []
        for (int i = 0; i < tags.length; i++) {
            BeaconTag beaconTag = new BeaconTag()
            beaconTag.beacon = beacon;
            beaconTag.tag = tags[i];
            beaconTagList.add(beaconTag);
        }
        return beaconTagList
    }

    /**
     * 1. check for duplicate beacon name
     * 2. check for duplicate beacon code
     *
     * @param beacon -object of Beacon
     * @param user - an object of AppUser
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params, AppUser user) {
        String errMsg
        //Check parameters
        if (!params.name) {
            return ERROR_FOR_INVALID_INPUT
        }
        //check for duplicate beacon code
//        errMsg = checkBeaconCountByCode(params, user)
//        if (errMsg != null) return errMsg

//        check for duplicate beacon name
        errMsg = checkBeaconCountByUUID(params, user)
        if (errMsg != null) return errMsg

        return null
    }

    /**
     * check for duplicate beacon code
     *
     * @param beacon - an object of Beacon
     * @param user - an object of AppUser
     * @return - error message or null
     */
    private String checkBeaconCountByCode(Map params, AppUser user) {
//        int count = beaconService.countByCodeIlikeAndCompanyId(params.code, user.companyId)
//        if (count > 0) {
//            return BEACON_CODE_ALREADY_EXISTS
//        }
        return null
    }

    /**
     * check for duplicate beacon name
     *
     * @param beacon - an object of Beacon
     * @param user - an object of AppUser
     * @return - error message or null
     */
    private String checkBeaconCountByUUID(Map params, AppUser user) {
        int count = beaconService.countByUUIDAndAppUser(params.uuid.toString())
        if (count > 0) {
            return BEACON_NAME_ALREADY_EXISTS
        }
        return null
    }

}

