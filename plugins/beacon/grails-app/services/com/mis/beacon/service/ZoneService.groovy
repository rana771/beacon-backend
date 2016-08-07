package com.mis.beacon.service

import com.athena.mis.BaseDomainService
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.mis.beacon.Zone
import com.athena.mis.utility.DateUtility
import grails.transaction.Transactional
import groovy.sql.Sql


class ZoneService extends BaseDomainService {


    @Override
    public void init() {
        domainClass = Zone.class
    }

    /**
     * Pull zone object
     * @return - list of zone
     */
    @Override
    public List<Zone> list() {
//              return Zone.findAllByCompanyId(companyId, [sort: Zone.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER, readOnly: true]);
    }

    @Transactional(readOnly = true)
    public Map list(BaseService action) {
        Sql sql = new Sql(dataSource)
        AppUser appUser = super.getAppUser();

        String strSQL="""
                    SELECT
                    zone.id,zone.version,zone.name,zone.color,zone.description,
                    STRING_AGG(beacon.id::text,',') AS beacon_ids,STRING_AGG(beacon.name,',')as beacon
                    FROM
                    zone
                    INNER JOIN marchant ON marchant.id=zone.marchant_id
                    LEFT OUTER JOIN beacon ON beacon.zone_id=zone.id
                    WHERE
                    marchant.app_user_id=${appUser.id}
                    GROUP BY
                    zone.id,zone.version,zone.name,zone.color,zone.description
                    ORDER BY ${action.sortColumn}  ${action.sortOrder}
                    OFFSET ${action.start}
                    LIMIT ${action.resultPerPage}
                    """

        List list = sql.rows(strSQL)
        int count = Zone.count();
        return [list: list, count: count]
    }


    @Override
    public void createDefaultSchema() {
        String nameIndex = "create unique index zone_name_company_id_idx on zone(lower(name),company_id);"
        executeSql(nameIndex)
        String codeIndex = "create unique index zone_code_company_id_idx on zone(lower(code),company_id);"
        executeSql(codeIndex)
    }


    @Override
    public boolean createTestData(long companyId, long userId) {
        //Write your default data insert statement here
        return true
    }


}
