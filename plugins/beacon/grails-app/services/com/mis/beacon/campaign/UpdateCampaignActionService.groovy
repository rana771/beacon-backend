package com.mis.beacon.campaign

import com.mis.beacon.Beacon
import com.mis.beacon.BeaconCampaign
import com.mis.beacon.Campaign
import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.utility.DateUtility
import com.mis.beacon.service.CampaignService
import groovy.sql.Sql
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import javax.sql.DataSource


class UpdateCampaignActionService extends BaseService implements ActionServiceIntf {

     private Logger log = Logger.getLogger(getClass())

    private static final String CAMPAIGN = "campaign"
    private static final String BEACON_CAMPAIGN = "beaconcampaign"
    private static final String PROJECT_CODE_ALREADY_EXISTS = "Same campaign code already exists"
    private static final String PROJECT_NAME_ALREADY_EXISTS = "Same campaign name already exists"
    private static final String PROJECT_UPDATE_SUCCESS_MESSAGE = "Campaign has been updated successfully"
    private static
    final String HAS_UNAPPROVED_CONSUMPTION = " Consumption(s) is unapproved of this campaign. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_PRODUCTION = " Production(s) is unapproved of this campaign. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_OUT = " Inventory Out(s) is unapproved of this campaign. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_IN_FROM_INVENTORY = " In from Inventory(s) is unapproved of this campaign. Approve all transactions to change auto approve property"
    private static
    final String HAS_UNAPPROVED_IN_FROM_SUPPLIER = " In from Supplier(s) is unapproved of this campaign. Approve all transactions to change auto approve property"

    CampaignService campaignService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService

    /**
     * 1. Check Validation
     * 2. Check un-approve transactions for auto approve
     * 3. Build campaign object for update
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
//            if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
//                String msg = checkUnApproveTransactionsForAutoApprove(params)
//                if (msg) {
//                    return super.setError(params, msg)
//                }
//            }
            // build campaign object for update
            Campaign campaign=  getCampaign(params)
            List<BeaconCampaign> beaconCampaignList=  getBeaconCampaignList(params,campaign)
            params.put(BEACON_CAMPAIGN, beaconCampaignList)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * 1. get the campaign object from map
     * 2. Update existing campaign in DB
     * This method is in transactional block and will roll back in case of any exception
     *
     * @param result - parameter from pre-condition
     * @return - same map of input-parameter containing isError(true/false)
     */
    Sql sql
    DataSource dataSource

    @Transactional
    public Map execute(Map result) {
        try {
            sql=new Sql(dataSource)
            Campaign campaign = (Campaign) result.get(CAMPAIGN)
            campaignService.update(campaign)
            List<BeaconCampaign>beaconCampaignList=(List<BeaconCampaign>)result.get(BEACON_CAMPAIGN)
            String strSql="""
                        DELETE from beacon_campaign WHERE campaign_id=${campaign.id}
                        """
            sql.executeUpdate(strSql)
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
     * Build campaign object for update
     *
     * @param params - serialize parameters from UI
     * @return - campaign object
     */
    private Campaign getCampaign(Map params) {
        Campaign oldCampaign = (Campaign) params.get(CAMPAIGN)
        params.startTime = DateUtility.parseMaskedDate(params.startTime.toString())
        params.endTime = DateUtility.parseMaskedDate(params.endTime.toString())

        Campaign newCampaign = new Campaign(params)
        if(!(newCampaign.isScheduleAlways))
            newCampaign.isScheduleAlways=false;

        oldCampaign.name = newCampaign.name
        oldCampaign.subject = newCampaign.subject
        oldCampaign.template = newCampaign.template
        oldCampaign.startTime = newCampaign.startTime
        oldCampaign.endTime = newCampaign.endTime
        oldCampaign.bonusRewardPoint = newCampaign.bonusRewardPoint
        oldCampaign.ticker = newCampaign.ticker
        oldCampaign.title = newCampaign.title
        oldCampaign.message = newCampaign.message
        oldCampaign.isScheduleAlways = newCampaign.isScheduleAlways
        return oldCampaign
    }


    private List<BeaconCampaign> getBeaconCampaignList(Map params,Campaign campaign) {
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
     * 1. Check Campaign object existance
     * 2. Check for duplicate campaign code
     * 3. Check for duplicate campaign name
     * 4. Check parameters
     *
     * @param campaign - object of Campaign
     * @param params - a map from caller method
     * @return -a string containing null value or error message depending on duplicate check
     */
    private String checkValidation(Map params) {
        String errMsg
        //Check parameters
        if ((!params.id)) {
            return ERROR_FOR_INVALID_INPUT
        }
        long campaignId = Long.parseLong(params.id.toString())
        Campaign campaign = campaignService.read(campaignId)

        //check Campaign object existance
//        errMsg = checkCampaignExistance(campaign, params)
//        if (errMsg != null) return errMsg
//
//        //check for duplicate campaign code
//        errMsg = checkCampaignCountByCode(campaign, params)
//        if (errMsg != null) return errMsg
//
//        //check for duplicate campaign name
//        errMsg = checkCampaignCountByName(campaign, params)
        if (errMsg != null) return errMsg
        params.put(CAMPAIGN, campaign)
        return null
    }

    /**
     * check Campaign object existance
     *
     * @param campaign - an object of Campaign
     * @param params - a map from caller method
     * @return - error message or null
     */

    private String checkCampaignExistance(Campaign campaign, Map params) {
        long version = Long.parseLong(params.version.toString())
        if (!campaign || campaign.version != version) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        return null
    }

    /**
     * check for duplicate campaign code
     *
     * @param campaign - an object of Campaign
     * @param params - a map from caller method
     * @return - error message or null
     */
//    private String checkCampaignCountByCode(Campaign campaign, Map params) {
//        int count = campaignService.countByCodeIlikeAndCompanyIdAndIdNotEqual(params.code, campaign.companyId, campaign.id)
//        if (count > 0) {
//            return PROJECT_CODE_ALREADY_EXISTS
//        }
//        return null
//    }

    /**
     * check for duplicate campaign name
     *
     * @param campaign - an object of Campaign
     * @param params - a map from caller method
     * @return - error message or null
     */
    private String checkCampaignCountByName(Campaign campaign, Map params) {
        int count = campaignService.countByNameIlikeAndCompanyIdAndIdNotEqual(params.name, campaign.companyId, campaign.id)
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
        AND iit.campaign_id = :campaignId
    """

    /**
     * Check un-approve transactions for auto approve
     *
     * @param params -serialized parameters from UI
     * @param campaign -object of Campaign
     * @return -a string containing relevant message or null value depending on count of un-approved transactions
     */
//    private String checkUnApproveTransactionsForAutoApprove(Map params) {
//        Campaign campaign = (Campaign) params.get(PROJECT)
//        Campaign newCampaign = new Campaign(params)
//
//        String msg = null
//        long transactionTypeId
//        long transactionEntityTypeId
//        int count
//        if (!campaign.isApproveConsumption && newCampaign.isApproveConsumption) {
//            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdConsumption()
//            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdNone()
//            Map queryParams = [
//                    transactionTypeId: transactionTypeId,
//                    transactionEntityTypeId: transactionEntityTypeId,
//                    campaignId: campaign.id
//            ]
//            List result = executeSelectSql(STR_QUERY, queryParams)
//            count = result[0].count
//            if (count > 0) {
//                return count + HAS_UNAPPROVED_CONSUMPTION
//            }
//        }
//        if (!campaign.isApproveProduction && newCampaign.isApproveProduction) {
//            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdProduction()
//            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdNone()
//            Map queryParams = [
//                    transactionTypeId: transactionTypeId,
//                    transactionEntityTypeId: transactionEntityTypeId,
//                    campaignId: campaign.id
//            ]
//            List result = executeSelectSql(STR_QUERY, queryParams)
//            count = result[0].count
//            if (count > 0) {
//                return count + HAS_UNAPPROVED_PRODUCTION
//            }
//        }
//        if (!campaign.isApproveInvOut && newCampaign.isApproveInvOut) {
//            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdOut()
//            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdInventory()
//            Map queryParams = [
//                    transactionTypeId: transactionTypeId,
//                    transactionEntityTypeId: transactionEntityTypeId,
//                    campaignId: campaign.id
//            ]
//            List result = executeSelectSql(STR_QUERY, queryParams)
//            count = result[0].count
//            if (count > 0) {
//                return count + HAS_UNAPPROVED_OUT
//            }
//        }
//        if (!campaign.isApproveInFromInventory && newCampaign.isApproveInFromInventory) {
//            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdIn()
//            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdInventory()
//            Map queryParams = [
//                    transactionTypeId: transactionTypeId,
//                    transactionEntityTypeId: transactionEntityTypeId,
//                    campaignId: campaign.id
//            ]
//            List result = executeSelectSql(STR_QUERY, queryParams)
//            count = result[0].count
//            if (count > 0) {
//                return count + HAS_UNAPPROVED_IN_FROM_INVENTORY
//            }
//        }
//        if (!campaign.isApproveInFromSupplier && newCampaign.isApproveInFromSupplier) {
//            transactionTypeId = invInventoryImplService.getInvTransactionTypeIdIn()
//            transactionEntityTypeId = invInventoryImplService.getTransactionEntityTypeIdSupplier()
//            Map queryParams = [
//                    transactionTypeId: transactionTypeId,
//                    transactionEntityTypeId: transactionEntityTypeId,
//                    campaignId: campaign.id
//            ]
//            List result = executeSelectSql(STR_QUERY, queryParams)
//            count = result[0].count
//            if (count > 0) {
//                return count + HAS_UNAPPROVED_IN_FROM_SUPPLIER
//            }
//        }
//        return msg
//    }
}
