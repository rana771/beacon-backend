package beacon

import com.mis.beacon.Beacon
import com.mis.beacon.BeaconCampaign
import com.mis.beacon.Campaign
import com.mis.beacon.CustomerMessageLog
import com.mis.beacon.CustomerPointDetails
import com.mis.beacon.CustomerRewardPoint
import com.mis.beacon.Marchant
import grails.converters.JSON
import groovy.sql.Sql
import org.apache.commons.lang.StringEscapeUtils

import javax.sql.DataSource

class RestController {


    def beacon() {
        Map output = [:]
        String result
        println(params)
        try {
            Beacon beacon = Beacon.findByUuid(params?.uuid)

            if (beacon) {
//                BeaconCampaign beaconCampaign = BeaconCampaign.findByBeacon(beacon)
                String strSql="""select
                                campaign.id
                                from
                                campaign
                                inner join beacon_campaign ON beacon_campaign.campaign_id=campaign.id
                                WHERE
                                beacon_campaign.beacon_id=${beacon.id}
                                AND
                                campaign.is_schedule_always is true
                                ORDER BY campaign.id DESC"""
                Sql sql=new Sql(dataSource)
                List list=sql.rows(strSql)
                Campaign campaign
                if(list && list.size()>0){
                    campaign=Campaign.read(Long.parseLong(list[0].id.toString()));
                }
                if (campaign) {
                    result = campaign?.template ? StringEscapeUtils.unescapeHtml(campaign?.template) : "No Campaign Found"
                    CustomerMessageLog customerMessageLog = new CustomerMessageLog()
                    customerMessageLog.campaign = campaign;
                    customerMessageLog.dateVisited = new Date()
                    customerMessageLog.customerId = params?.android_id
                    customerMessageLog.save()
                    output = [id     : campaign.id.toString(), title: campaign.subject,
                              details: result, msgTitle: campaign?.title ?: "", points: "${campaign.bonusRewardPoint ?: 0}",
                              msg    : campaign?.message ?: "", ticker: campaign?.ticker ?: "", "expired_date": campaign.endTime]
                } else {
                    output = [id     : -1, title: "Campaign Not Created",
                              details: "Vendor has not created any campaign."]
                }
            } else {
                if (params.id) {
                    Campaign campaign = Campaign.read(Long.parseLong(params?.id?.toString()))
                    result = campaign?.template ? StringEscapeUtils.unescapeHtml(campaign?.template) : "No Campaign Found"
                    CustomerMessageLog customerMessageLog = new CustomerMessageLog()
                    customerMessageLog.campaign = campaign;
                    customerMessageLog.dateVisited = new Date()
                    customerMessageLog.customerId = params?.android_id
                    customerMessageLog.save()
                    output = [id     : campaign.id.toString(), title: campaign.subject,
                              details: result, msgTitle: campaign?.title ?: "", points: "${campaign.bonusRewardPoint ?: 0}",
                              msg    : campaign?.message ?: "", ticker: campaign?.ticker ?: "", "expired_date": campaign.endTime]
                } else {
                    output = [id     : -1, title: "Unknown Beacon",
                              details: """You have not created your beacon yet.
                             Please create your beacon first"""]
                }
            }
        } catch (Exception ex) {
            output = [id     : -1, title: "Error in server",
                      details: """You have not created your merchant portal properly.
                             Please create your merchant properly"""]
        }
        render output as JSON
    }

    def point() {
        println(params)
        String result = "";
        try {
            Campaign campaign = Campaign.read(Long.parseLong(params?.id?.toString()))
            CustomerRewardPoint customerRewardPoint = CustomerRewardPoint.findByUserId(1)

            if (!customerRewardPoint) {
                customerRewardPoint = new CustomerRewardPoint(id: 1, version: 0, userId: 1, rewardPoint: 0, allTotalPoint: 0, redeemPoint: 0).save()

            }

//            result += """Your have got ${campaign.bonusRewardPoint} reward point for scanning.
//                        Your previews reward point was ${customerRewardPoint.rewardPoint}.
//                        Your current reward point after bonus is ${
//                customerRewardPoint.rewardPoint + campaign.bonusRewardPoint
//            }
//                """

            result += """Congratulations! You have received ${campaign.bonusRewardPoint} reward points. See dashboard for details. You need additional ${20000-(customerRewardPoint.rewardPoint + campaign.bonusRewardPoint)} points to win an iPad. Keep Going!
                """
            customerRewardPoint.rewardPoint += campaign.bonusRewardPoint
            customerRewardPoint.allTotalPoint += campaign.bonusRewardPoint
            customerRewardPoint.lastScanPoint = campaign.bonusRewardPoint
            customerRewardPoint.save();
            CustomerPointDetails customerPointDetails = new CustomerPointDetails()
            customerPointDetails.customerRewardPoint = customerRewardPoint
            customerPointDetails.campaign=campaign
            customerPointDetails.datePointReceive = new Date()
            customerPointDetails.point = campaign.bonusRewardPoint
            customerPointDetails.save()
        } catch (Exception ex) {
            result = "You have not configured your beacon properly."
        }
        render result

    }

    DataSource dataSource
    def myPoint = {
        println(params)
        Sql sql = new Sql(dataSource)
        String strSql = """
                        SELECT
			                COALESCE(customer_reward_point.last_scan_point,0) AS last_scan_point,
                            customer_reward_point.reward_point as my_scan_point,
                            COALESCE(customer_reward_point.all_total_point,0) AS all_total_point,
                            COALESCE(customer_reward_point.redeem_point,0) AS redeem_point,
                            to_date(customer_point_details.date_point_receive::text,'yyyy-mm-dd'),
                            COALESCE(SUM(customer_point_details.point),0) AS today_point
                            FROM
                            customer_reward_point
                            LEFT OUTER JOIN customer_point_details ON customer_reward_point.id=customer_point_details.customer_reward_point_id
                            AND
                            to_date(customer_point_details.date_point_receive::text,'yyyy-mm-dd')=CURRENT_DATE
                            GROUP BY
                            customer_reward_point.reward_point,
                            customer_reward_point.all_total_point,customer_reward_point.redeem_point,
                            to_date(customer_point_details.date_point_receive::text,'yyyy-mm-dd'),last_scan_point
                    """

        List result = sql.rows(strSql)

        if (result && result.size() > 0)
            render result[0] as JSON
    }
}
