package com.mis.beacon.service

import com.athena.mis.BaseDomainService
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.TestDataModelService
import com.mis.beacon.Campaign
import com.athena.mis.utility.DateUtility
import groovy.sql.Sql
import org.springframework.transaction.annotation.Transactional


class CampaignService  extends BaseDomainService {

     TestDataModelService testDataModelService

            @Override
            public void init() {
                domainClass = Campaign.class
            }



    /**
    * Get list of Campaign by list of ids
    * @param lstCampaignIds - list of AppGroup.id
    * @return - list of Campaign by ids
    */
    public List<Campaign> findAllByIdInList(List<Long> lstCampaignIds) {
        List<Campaign> lstCampaign = Campaign.findAllByIdInList(lstCampaignIds, [sort: Campaign.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstCampaign
    }

    @Transactional

    public Map list(BaseService action) {
        Sql sql = new Sql(dataSource)
        String strSql = """
          SELECT
                DISTINCT campaign.id,campaign.name,campaign.subject,
                campaign.title, campaign.ticker,campaign.message,
                campaign.start_time,campaign.end_time,campaign.bonus_reward_point,
                STRING_AGG(beacon.name,',') AS beacons,STRING_AGG(beacon.id::text,',') AS beacon_ids,
                campaign.template,campaign.is_schedule_always
                FROM campaign
                INNER JOIN beacon_campaign ON beacon_campaign.campaign_id=campaign.id
                INNER JOIN beacon ON beacon.id=beacon_campaign.beacon_id
                INNER JOIN marchant ON marchant.id=beacon.marchant_id
                WHERE
                marchant.app_user_id=${appUser.id}
                GROUP BY
                campaign.id,campaign.name,campaign.subject,
                campaign.title, campaign.ticker,campaign.message,campaign.is_schedule_always,
                campaign.start_time,campaign.end_time,campaign.bonus_reward_point,campaign.template


        """
        List list = sql.rows(strSql)
        int count=list.size();
        return [list: list, count: count]
    }

    @Transactional
    public void delete(Long id) {
        try {
            String query = """
                    DELETE FROM campaign
                      WHERE id=${id}
                    """

            String query2 = """
                    DELETE FROM beacon_campaign
                      WHERE campaign_id=${id}
                    """
                        executeUpdateSql(query2)
            int deleteCount = executeUpdateSql(query)
            if (deleteCount <= 0) {
                throw new RuntimeException("Error occurred while deleting Campaign.")
            }
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }
    private static final String UPDATE_CONTENT_COUNT_QUERY = """
        UPDATE campaign
        SET content_count = content_count + :contentCount,
        version = version + 1
        WHERE
        id=:id
    """

    // update content count for campaign during create, update and delete content
    public int updateContentCountForCampaign(long campaignId, int count) {
        Map queryParams = [
            contentCount: count,
            id          : campaignId
        ]
        int updateCount = executeUpdateSql(UPDATE_CONTENT_COUNT_QUERY, queryParams)
        if (updateCount <= 0) {
             throw new RuntimeException("Content count updated for Campaign")
        }
        return updateCount
    }

    @Override
    public void createDefaultSchema() {
            String nameIndex = "create unique index campaign_name_company_id_idx on campaign(lower(name),company_id);"
            executeSql(nameIndex)
            String codeIndex = "create unique index campaign_code_company_id_idx on campaign(lower(code),company_id);"
            executeSql(codeIndex)
        }




        private static final String INSERT_QUERY = """
                INSERT INTO campaign( id, version, name, code, description, content_count, start_date, end_date, company_id,
                created_by, created_on, updated_by, is_approve_in_from_supplier, is_approve_in_from_inventory,
                is_approve_inv_out, is_approve_consumption, is_approve_production)
                VALUES (:id, :version, :name, :code, :description, :contentCount, :startDate, :endDate, :companyId, :createdBy,
                :createdOn, :updatedBy, :isApproveInFromSupplier, :isApproveInFromInventory, :isApproveInvOut, :isApproveConsumption,
                :isApproveProduction);
        """

    @Override
    public boolean createTestData(long companyId, long userId) {
        Campaign campaign1 = new Campaign(name: "Dhaka Flyover", code: "DF", description: 'A Flyover all over the dhaka', contentCount: 0, startDate: new Date(), endDate: new Date() + 30, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        Campaign campaign2 = new Campaign(name: "School Construction", code: "SC", description: 'Construction of 4 storied school building', contentCount: 0, startDate: new Date() - 7, endDate: new Date() + 60, companyId: companyId, createdBy: userId, createdOn: new Date() - 7, updatedBy: 0)
        Campaign campaign3 = new Campaign(name: "Roads Construction", code: "RC", description: 'Construction of Dhaka Chittagong 6 Lane Highway', contentCount: 0, startDate: new Date() - 25, endDate: new Date() + 90, companyId: companyId, createdBy: userId, createdOn: new Date() - 25, updatedBy: 0)

        runSqlForCreateTestData(campaign1)
        runSqlForCreateTestData(campaign2)
        runSqlForCreateTestData(campaign3)
        return true
    }

    public void runSqlForCreateTestData(Campaign parameter) {
//        Map queryParams = [
//                    id                      : testDataModelService.getNextIdForTestData(),
//                    version                 : 0L,
//                    name                    : parameter.name,
//                    code                    : parameter.code,
//                    description             : parameter.description,
//                    contentCount            : parameter.contentCount,
//                    startDate               : DateUtility.getSqlDate(parameter.startDate),
//                    endDate                 : DateUtility.getSqlDate(parameter.endDate),
//                    companyId               : parameter.companyId,
//                    createdBy               : parameter.createdBy,
//                    createdOn               : DateUtility.getSqlDateWithSeconds(parameter.createdOn),
//                    updatedBy               : parameter.updatedBy,
//                    isApproveInFromSupplier : false,
//                    isApproveInFromInventory: false,
//                    isApproveInvOut         : false,
//                    isApproveConsumption    : false,
//                    isApproveProduction     : false
//            ]
//            executeInsertSql(INSERT_QUERY, queryParams)
    }
}
