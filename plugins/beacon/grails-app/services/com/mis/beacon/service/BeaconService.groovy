package com.mis.beacon.service

import com.athena.mis.BaseDomainService
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.TestDataModelService
import com.mis.beacon.Beacon
import com.athena.mis.utility.DateUtility
import groovy.sql.Sql
import org.springframework.transaction.annotation.Transactional


class BeaconService extends BaseDomainService {

    TestDataModelService testDataModelService

    @Override
    public void init() {
        domainClass = Beacon.class
    }

    /**
     * Get list of Beacon by list of ids
     * @param lstBeaconIds - list of AppGroup.id
     * @return - list of Beacon by ids
     */
    public List<Beacon> findAllByIdInList(List<Long> lstBeaconIds) {
        List<Beacon> lstBeacon = Beacon.findAllByIdInList(lstBeaconIds, [sort: Beacon.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true])
        return lstBeacon
    }

    @Transactional

    public Map list(BaseService action) {
        Sql sql = new Sql(dataSource)
        AppUser appUser = super.getAppUser();
        String strSql = """



                     SELECT
                    beacon.id,beacon.name,beacon.uuid,beacon.major,beacon.minor,beacon.latitude,
                    beacon.longitude,beacon.signal_interval,beacon.transmission_power,
                    beacon.zone_id,zone.name AS zone_name,STRING_AGG(beacon_tag.tag,',') as tag
                    FROM
                    beacon
                    INNER JOIN marchant ON beacon.marchant_id=marchant.id
                    LEFT OUTER JOIN zone on zone.id=beacon.zone_id
                    LEFT OUTER JOIN beacon_tag ON beacon_tag.beacon_id=beacon.id
                    WHERE
                   marchant.app_user_id=${appUser.id}
		            GROUP BY
		            beacon.id,beacon.name,beacon.uuid,beacon.major,beacon.minor,beacon.latitude,
                    beacon.longitude,beacon.signal_interval,beacon.transmission_power,
                    beacon.zone_id,zone.name

        """
        List list = sql.rows(strSql)
        int count = Beacon.count();
        return [list: list, count: count]
    }

    @Override
    public List<Beacon> list() {
        return Beacon.listOrderByName();
    }

    @Transactional
    public void delete(Long id) {
        try {
            String query = """
                    DELETE FROM beacon
                      WHERE id=${id}
                    """
            int deleteCount = executeUpdateSql(query)
            if (deleteCount <= 0) {
                throw new RuntimeException("Error occurred while deleting Beacon.")
            }
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    public int countByUUIDAndAppUser(String uuid) {
        String query = """
                    SELECT
                    COUNT(beacon.id) as count
                    FROM
                    beacon
                    INNER JOIN marchant ON marchant.id=beacon.marchant_id
                    WHERE
                    beacon.uuid='${uuid}' AND marchant.app_user_id=${appUser.id}"""
        Sql sql = new Sql(dataSource)
        List result = sql.rows(query)
        int count=0;
        if(result && result.size()>0){
            count=Integer.parseInt(""+result.get(0).count)
        }
        return count;
    }

    private static final String UPDATE_CONTENT_COUNT_QUERY = """
        UPDATE beacon
        SET content_count = content_count + :contentCount,
        version = version + 1
        WHERE
        id=:id
    """

    // update content count for beacon during create, update and delete content
    public int updateContentCountForBeacon(long beaconId, int count) {
        Map queryParams = [
                contentCount: count,
                id          : beaconId
        ]
        int updateCount = executeUpdateSql(UPDATE_CONTENT_COUNT_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException("Content count updated for Beacon")
        }
        return updateCount
    }

    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index beacon_name_company_id_idx on beacon(lower(name),company_id);"
        executeSql(nameIndex)
        String codeIndex = "create unique index beacon_code_company_id_idx on beacon(lower(code),company_id);"
        executeSql(codeIndex)
    }


    private static final String INSERT_QUERY = """
                INSERT INTO beacon( id, version, name, code, description, content_count, start_date, end_date, company_id,
                created_by, created_on, updated_by, is_approve_in_from_supplier, is_approve_in_from_inventory,
                is_approve_inv_out, is_approve_consumption, is_approve_production)
                VALUES (:id, :version, :name, :code, :description, :contentCount, :startDate, :endDate, :companyId, :createdBy,
                :createdOn, :updatedBy, :isApproveInFromSupplier, :isApproveInFromInventory, :isApproveInvOut, :isApproveConsumption,
                :isApproveProduction);
        """

    @Override
    public boolean createTestData(long companyId, long userId) {
        Beacon beacon1 = new Beacon(name: "Dhaka Flyover", code: "DF", description: 'A Flyover all over the dhaka', contentCount: 0, startDate: new Date(), endDate: new Date() + 30, companyId: companyId, createdBy: userId, createdOn: new Date(), updatedBy: 0)
        Beacon beacon2 = new Beacon(name: "School Construction", code: "SC", description: 'Construction of 4 storied school building', contentCount: 0, startDate: new Date() - 7, endDate: new Date() + 60, companyId: companyId, createdBy: userId, createdOn: new Date() - 7, updatedBy: 0)
        Beacon beacon3 = new Beacon(name: "Roads Construction", code: "RC", description: 'Construction of Dhaka Chittagong 6 Lane Highway', contentCount: 0, startDate: new Date() - 25, endDate: new Date() + 90, companyId: companyId, createdBy: userId, createdOn: new Date() - 25, updatedBy: 0)

        runSqlForCreateTestData(beacon1)
        runSqlForCreateTestData(beacon2)
        runSqlForCreateTestData(beacon3)
        return true
    }

    public void runSqlForCreateTestData(Beacon parameter) {
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
