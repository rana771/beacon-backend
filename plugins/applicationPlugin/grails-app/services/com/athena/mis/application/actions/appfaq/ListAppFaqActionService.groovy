package com.athena.mis.application.actions.appfaq

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.model.ListAppFaqActionServiceModel
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of AppFaq for grid
 *  For details go through Use-Case doc named 'ListAppFaqActionService'
 */
class ListAppFaqActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())
    private static final String NULL = "null"

    /**
     * Do nothing for pre condition
     */
    public Map executePreCondition(Map parameters) {
        return parameters
    }

    /**
     * Get list of AppFaq objects
     * @param result -parameters from UI
     * @return -a map contains list of AppFaq objects  and count
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            // check required parameters
            if (!result.entityId) {
                result.put(LIST, [])
                result.put(COUNT, 0)
                return result
            }
            Map searchResult
            if (Boolean.parseBoolean(result.isShowElFaq.toString())) {
                if (result.faqSearchTxt) {
                    searchResult = getSearchResult(result)
                    result.put(LIST, searchResult.list)
                    result.put(COUNT, searchResult.count)
                    return result
                } else {
                    searchResult = getListResult(result)
                    result.put(LIST, searchResult.list)
                    result.put(COUNT, searchResult.count)
                    return result
                }

            } else {
                long entityTypeId = Long.parseLong(result.entityTypeId.toString())
                long entityId = Long.parseLong(result.entityId.toString())
                Closure additionalFilter = {
                    eq('entityTypeId', entityTypeId)
                    eq('entityId', entityId)
                }
                searchResult = super.getSearchResult(result, ListAppFaqActionServiceModel, additionalFilter)
                result.put(LIST, searchResult.list)
                result.put(COUNT, searchResult.count)
                return result
            }
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Do nothing for post condition
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Do nothing for post condition
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * Do nothing for post condition
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    private Map getSearchResult(Map result) {
        long entityId = Long.parseLong(result.entityId.toString())
        int start = Integer.parseInt(result.skip.toString())
        int rp = Integer.parseInt(result.take.toString())
        List<GroovyRowResult> lstFaq = []
        int total = 0
        String searchText = null
        if (!result.faqSearchTxt.equals(NULL)) {
            searchText = result.faqSearchTxt.toString()
        }

        lstFaq = searchFaq(entityId, searchText, start, rp, companyId)
        total = countSearchFaq(entityId, searchText, companyId)

        return [list: lstFaq, count: total]
    }

    private List<GroovyRowResult> searchFaq(long entityId, String searchText, int start, int rp, long companyId) {
        String additionalQuery = EMPTY_SPACE
        if (searchText) {
            additionalQuery = ' AND (question ilike :searchText OR answer ilike :searchText)'
        }
        String str_query = """
            SELECT
                faq.id,
                faq.version,
                faq.company_id,
                faq.entity_type_id,
                faq.entity_id,
                faq.question,
                faq.answer,
                0 AS sub_category_id
            FROM app_faq faq
            WHERE faq.entity_id = ${entityId}
            AND faq.entity_type_id = (SELECT id FROM system_entity WHERE reserved_id = 10000341 AND company_id = faq.company_id)
            AND faq.company_id = ${companyId}
            ${additionalQuery}

            UNION

            SELECT
                faq.id,
                faq.version,
                faq.company_id,
                faq.entity_type_id,
                faq.entity_id,
                faq.question,
                faq.answer,
                doc.sub_category_id
            FROM app_faq faq
            LEFT JOIN doc_document doc ON doc.id = faq.entity_id
            WHERE faq.entity_type_id = (SELECT id FROM system_entity WHERE reserved_id = 10000339 AND company_id = faq.company_id)
            AND doc.sub_category_id = ${entityId}
            AND faq.company_id = ${companyId}
            ${additionalQuery}
            LIMIT ${rp} OFFSET ${start}
        """
        Map params = [searchText: PERCENTAGE + searchText + PERCENTAGE, companyId: companyId]
        List<GroovyRowResult> lstResult = executeSelectSql(str_query, params)
        return lstResult
    }


    private int countSearchFaq(long entityId, String searchText, long companyId) {
        String additionalQuery = EMPTY_SPACE
        if (searchText) {
            additionalQuery = ' AND (question ilike :searchText OR answer ilike :searchText)'
        }
        String str_query = """
            SELECT COUNT(faq.id)
            FROM app_faq faq
            WHERE faq.entity_id = ${entityId}
            AND faq.entity_type_id = (SELECT id FROM system_entity WHERE reserved_id = 10000341 AND company_id = faq.company_id)
            AND faq.company_id = ${companyId}
            ${additionalQuery}
        UNION
            SELECT COUNT(faq.id)
            FROM app_faq faq
            LEFT JOIN doc_document doc ON doc.id = faq.entity_id
            WHERE faq.entity_type_id = (SELECT id FROM system_entity WHERE reserved_id = 10000339 AND company_id = faq.company_id)
            AND doc.sub_category_id = ${entityId}
            AND faq.company_id = ${companyId}
            ${additionalQuery}
        """
        Map params = [companyId: companyId, searchText: PERCENTAGE + searchText + PERCENTAGE]
        List<GroovyRowResult> lstResult = executeSelectSql(str_query, params)
        int countForSubCatFaq = (int) lstResult[0].count
        int countForDocFaq = 0
        if (lstResult.size() > 1) {
            for (int i = 1; i < lstResult.size(); i++)
                countForDocFaq =+ (int) lstResult[i].count
        }
        return countForSubCatFaq + countForDocFaq
    }

    private int countListFaq(long entityId, long companyId) {
        String str_query = """
            SELECT COUNT(faq.id)
            FROM app_faq faq
            WHERE faq.entity_id = ${entityId}
            AND faq.entity_type_id = (SELECT id FROM system_entity WHERE reserved_id = 10000341 AND company_id = faq.company_id)
            AND faq.company_id = ${companyId}
        UNION
            SELECT COUNT(faq.id)
            FROM app_faq faq
            LEFT JOIN doc_document doc ON doc.id = faq.entity_id
            WHERE faq.entity_type_id = (SELECT id FROM system_entity WHERE reserved_id = 10000339 AND company_id = faq.company_id)
            AND doc.sub_category_id = ${entityId}
            AND faq.company_id = ${companyId}
        """
        List<GroovyRowResult> lstResult = executeSelectSql(str_query)
        int countForSubCatFaq = (int) lstResult[0].count
        int countForDocFaq = 0
        if (lstResult.size() > 1) {
            for (int i = 1; i < lstResult.size(); i++)
                countForDocFaq =+ (int) lstResult[i].count
        }
        return countForSubCatFaq + countForDocFaq
    }

    private Map getListResult(Map result) {
        long entityId = Long.parseLong(result.entityId.toString())
        int start = Integer.parseInt(result.skip.toString())
        int rp = Integer.parseInt(result.take.toString())
        List<GroovyRowResult> lstFaq = []
        int total = 0
        lstFaq = listFaq(entityId, start, rp, companyId)
        total = countListFaq(entityId, companyId)

        return [list: lstFaq, count: total]
    }

    private List<GroovyRowResult> listFaq(long entityId, int start, int rp, long companyId) {
        String str_query = """
            SELECT
                faq.id,
                faq.version,
                faq.company_id,
                faq.entity_type_id,
                faq.entity_id,
                faq.question,
                faq.answer,
                0 AS sub_category_id
            FROM app_faq faq
            WHERE faq.entity_id = ${entityId}
            AND faq.entity_type_id = (SELECT id FROM system_entity WHERE reserved_id = 10000341 AND company_id = faq.company_id)
            AND faq.company_id = ${companyId}
            UNION
            SELECT
                faq.id,
                faq.version,
                faq.company_id,
                faq.entity_type_id,
                faq.entity_id,
                faq.question,
                faq.answer,
                doc.sub_category_id
            FROM app_faq faq
            LEFT JOIN doc_document doc ON doc.id = faq.entity_id
            WHERE faq.entity_type_id = (SELECT id FROM system_entity WHERE reserved_id = 10000339 AND company_id = faq.company_id)
            AND faq.company_id = ${companyId}
            AND doc.sub_category_id = ${entityId}
            LIMIT ${rp} OFFSET ${start}
        """
        List<GroovyRowResult> lstResult = executeSelectSql(str_query)
        return lstResult
    }
}
