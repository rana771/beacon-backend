package com.athena.mis.application.actions.appfaq

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppFaqService
import com.athena.mis.application.service.AppNoteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.utility.DateUtility
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger

class ListAppFaqForListViewActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    AppFaqService appFaqService
    AppSystemEntityCacheService appSystemEntityCacheService

    Map executePreCondition(Map parameters) {
        Map result = buildAppFaqList(parameters)
        parameters.put(LIST, result.list)
        parameters.put(COUNT, result.count)
        return parameters
    }

    Map execute(Map result) {
        return result
    }

    Map executePostCondition(Map result) {
        return result
    }

    Map buildSuccessResultForUI(Map result) {
        return result
    }

    Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * build AppNote map for list view
     * @param entityId
     * @return - list of map for list view
     */
    private Map buildAppFaqList(Map params) {
        long entityId = Long.parseLong(params.entityId)
        long entityTypeId = Long.parseLong(params.entityTypeId)
        String order = params.sortOrder
        int start = Integer.parseInt(params.skip)
        int rp = Integer.parseInt(params.take)
        List<Map> lstResult = []
        long companyId = super.getCompanyId()
        SystemEntity entityType = appSystemEntityCacheService.readByReservedId(entityTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_FAQ, companyId)

        String strQuery = """
            SELECT faq.question, faq.answer, faq.created_on, au.username
            FROM app_faq faq
            LEFT JOIN app_user au ON au.id = faq.created_by
            WHERE faq.company_id = :companyId
            AND faq.entity_type_id = :entityTypeId
            AND faq.entity_id = :entityId
            ORDER BY faq.id ${order}
            LIMIT ${rp} OFFSET ${start}
        """

        Map queryParams = [
                companyId   : companyId,
                entityTypeId: entityType.id,
                entityId    : entityId
        ]

        List<GroovyRowResult> lstAppNote = executeSelectSql(strQuery, queryParams)
        int total = appFaqService.countByEntityTypeIdAndEntityIdAndCompanyId(entityType.id, entityId, companyId)

        for (int i = 0; i < lstAppNote.size(); i++) {
            Map obj = [
                    createdBy: lstAppNote[i].username,
                    createdOn: DateUtility.getDateTimeFormatAsString(lstAppNote[i].created_on),
                    question     : lstAppNote[i].question,
                    answer     : lstAppNote[i].answer
            ]
            lstResult << obj
        }
        return [list: lstResult, count: total]
    }
}
