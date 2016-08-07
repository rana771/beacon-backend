package com.mis.beacon.campaign

import com.mis.beacon.BeaconCampaign
import com.mis.beacon.Campaign
import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.mis.beacon.service.CampaignService
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional


class DeleteCampaignActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DELETE_CAMPAIGN_SUCCESS_MESSAGE = "Campaign has been deleted successfully"

    private static final String HAS_ASSOCIATION_MESSAGE_BEACON = " Beacon is associated with selected campaign"
    private static final String HAS_ASSOCIATION_MESSAGE_MARCHANT = " Marchant is associated with selected campaign"


    private static final String HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS = " voucher is associated with this campaign"
    private static final String CAMPAIGN = "campaign"

    AppSystemEntityCacheService appSystemEntityCacheService
    CampaignService campaignService

    /**
     * 1. Check Validation
     * 2. Association check for campaign with different domains
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
            //association check for campaign with different domains
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
     * Delete campaign object from DB
     * 1. get the campaign object from map
     * 2. delete from db
     * This function is in transactional boundary and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    @Transactional
    public Map execute(Map result) {
        try {
            Campaign campaign = (Campaign) result.get(CAMPAIGN)
            campaignService.delete(campaign.id)
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
        return super.setSuccess(result, DELETE_CAMPAIGN_SUCCESS_MESSAGE)
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


    private String checkValidation(Map params) {
        // check required parameter
        if (params.id == null) {
            return ERROR_FOR_INVALID_INPUT
        }
        long campaignId = Long.parseLong(params.id.toString())
        Campaign campaign = campaignService.read(campaignId)
        //check for campaign existence
        if (campaign == null) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        params.put(CAMPAIGN, campaign)
        return null
    }


    private String hasAssociation(Map params) {
        Campaign campaign = (Campaign) params.get(CAMPAIGN)
        long campaignId = campaign.id

        int count = 0
        String errMsg



        return null
    }


    private String checkAccountingAssociation(long campaignId) {
        int count
        if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
            count = countVoucherDetails(campaignId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_VOUCHER_DETAILS
            }
        }
        return null
    }


    private String checkBeaconAssociation(long campaignId) {
        int count
        if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
            count = countBeacon(campaignId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_BEACON
            }

        }
        return null
    }

    private String checkMarchantAssociation(long campaignId) {
        int count
        if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
            count = countMarchant(campaignId)
            if (count > 0) {
                return count.toString() + HAS_ASSOCIATION_MESSAGE_MARCHANT
            }

        }
        return null
    }

    private int countBeacon(long campaignId) {
        Map queryParams = [
                campaignId: campaignId

        ]
        List results = executeSelectSql(COUNT_QUERY, queryParams)
        int count = results[0].count
        return count
    }

    private int countMarchant(long campaignId) {
        Map queryParams = [
                campaignId: campaignId

        ]
        List results = executeSelectSql(MARCHANT_QUERY, queryParams)
        int count = results[0].count
        return count
    }

    /**
     * Get total user_entity(user_campaign) number of given campaign-id
     *
     * @param campaignId - campaign id
     * @param companyId -Company.id
     * @return - total user_entity(user_campaign) number
     */


    private static final String COUNT_QUERY = """
            SELECT COUNT(id) AS count
            FROM beacon
            WHERE beacon.campaign_id =:campaignId
    """

    private static final String MARCHANT_QUERY = """
            SELECT COUNT(id) AS count
            FROM marchant
            WHERE marchant.campaign_id =:campaignId
    """


}
