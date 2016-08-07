package com.mis.beacon.campaign

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.mis.beacon.Beacon
import com.mis.beacon.BeaconCampaign
import com.mis.beacon.Campaign
import com.athena.mis.ActionServiceIntf
import com.athena.mis.utility.DateUtility
import com.mis.beacon.service.BeaconCampaignService
import com.mis.beacon.service.CampaignService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional


class CreateCampaignActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String CAMPAIGN = "campaign"
    private static final String BEACON_CAMPAIGN = "campaign_campaign"
    private static final String CAMPAIGN_CODE_ALREADY_EXISTS = "Same campaign code already exists"
    private static final String CAMPAIGN_NAME_ALREADY_EXISTS = "Same campaign name already exists"
    private static final String CAMPAIGN_SAVE_SUCCESS_MESSAGE = "Campaign has been saved successfully"

    CampaignService campaignService
//    BeaconCampaignService beaconCampaignService
    /**
     * 1. check Validation
     * 2. build campaign object
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
            // build campaign object
            Campaign campaign = getCampaign(params, user)
            List<BeaconCampaign> beaconCampaignList = getBeaconCampaign(params, campaign)
            params.put(CAMPAIGN, campaign)
            params.put(BEACON_CAMPAIGN, beaconCampaignList)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. receive campaign object from executePreCondition method
     * 2. create new campaign
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Campaign campaign = (Campaign) result.get(CAMPAIGN)
            List<BeaconCampaign> beaconCampaignList = (List<BeaconCampaign>)result.get(BEACON_CAMPAIGN)
            // save new campaign object in DB
            campaignService.create(campaign)
            beaconCampaignList.each {
               it.save()
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
        return super.setSuccess(result, CAMPAIGN_SAVE_SUCCESS_MESSAGE)
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
     * Build campaign object
     *
     * @param params - serialize parameters from UI
     * @param user - an object of AppUser
     * @return - campaign object
     */
    private Campaign getCampaign(Map params, AppUser user) {
        params.startTime = DateUtility.parseMaskedDate(params.startTime.toString())
        params.endTime = DateUtility.parseMaskedDate(params.endTime.toString())

        Campaign campaign = new Campaign(params)
        if (!(campaign.isScheduleAlways))
            campaign.isScheduleAlways = false;

        return campaign
    }


    private List<BeaconCampaign> getBeaconCampaign(Map params, Campaign campaign) {
        List<BeaconCampaign> beaconCampaignList = []
        if(params.beacons.toString().contains(",")) {
            params.beacons.each {
                BeaconCampaign beaconCampaign = new BeaconCampaign()
                beaconCampaign.campaign = campaign
                beaconCampaign.beacon = Beacon.read(Long.parseLong(it.toString()))
                beaconCampaignList.add(beaconCampaign)
            }
        }else {
            BeaconCampaign beaconCampaign = new BeaconCampaign()
            beaconCampaign.campaign = campaign
            beaconCampaign.beacon = Beacon.read(Long.parseLong(params.beacons.toString()))
            beaconCampaignList.add(beaconCampaign)
        }
        return beaconCampaignList
    }
    /**
     * 1. check for duplicate campaign name
     * 2. check for duplicate campaign code
     *
     * @param campaign -object of Campaign
     * @param user - an object of AppUser
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params, AppUser user) {
        String errMsg
        //Check parameters
        if (!params.name) {
            return ERROR_FOR_INVALID_INPUT
        }
        //check for duplicate campaign code
//        errMsg = checkCampaignCountByCode(params, user)
//        if (errMsg != null) return errMsg
//
//        //check for duplicate campaign name
//        errMsg = checkCampaignCountByName(params, user)
//        if (errMsg != null) return errMsg

        return null
    }

    /**
     * check for duplicate campaign code
     *
     * @param campaign - an object of Campaign
     * @param user - an object of AppUser
     * @return - error message or null
     */
    private String checkCampaignCountByCode(Map params, AppUser user) {
//        int count = campaignService.countByCodeIlikeAndCompanyId(params.code, user.companyId)
//        if (count > 0) {
//            return CAMPAIGN_CODE_ALREADY_EXISTS
//        }
        return null
    }

    /**
     * check for duplicate campaign name
     *
     * @param campaign - an object of Campaign
     * @param user - an object of AppUser
     * @return - error message or null
     */
    private String checkCampaignCountByName(Map params, AppUser user) {
//        int count = campaignService.countByNameIlikeAndCompanyId(params.name, user.companyId)
//        if (count > 0) {
//            return CAMPAIGN_NAME_ALREADY_EXISTS
//        }
        return null
    }

}

