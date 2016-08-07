package com.mis.beacon.service

import com.athena.mis.BaseDomainService
import com.athena.mis.application.service.TestDataModelService
import com.mis.beacon.BeaconCampaign
import org.springframework.transaction.annotation.Transactional

class BeaconCampaignService extends BaseDomainService {

    TestDataModelService testDataModelService

    @Override
    public void init() {
        domainClass = BeaconCampaign.class
    }


    @Transactional
    public void delete(Long id) {
        try {
            String query = """
                    DELETE FROM beacon_campaign
                      WHERE id=${id}
                    """
            int deleteCount = executeUpdateSql(query)
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
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

        return true
    }

    public void runSqlForCreateTestData(BeaconCampaign parameter) {
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
