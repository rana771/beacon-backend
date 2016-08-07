package com.athena.mis.application.actions.appuserentity

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.AppUserService
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger

class ListAppUserByEntityTypeAndEntityService extends BaseService implements ActionServiceIntf {

    AppUserService appUserService
    AppSystemEntityCacheService appSystemEntityCacheService

    private Logger log = Logger.getLogger(getClass())

    private static final String USER_LIST = "userList"
    private static final String ENTITY_TYPE_ID = "entityTypeId"
    private static final String ENTITY_ID = "entityId"

    /**
     *
     * @param params - Request parameters
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePreCondition(Map params) {
        if (!params.reservedTypeId || !params.entityId) {
            return setError(params, ERROR_FOR_INVALID_INPUT)
        }
        long reservedTypeId = Long.parseLong(params.reservedTypeId.toString());
        SystemEntity entityType = appSystemEntityCacheService.readByReservedId(reservedTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, companyId)
        long entityId = Long.parseLong(params.entityId.toString());
        params.put(ENTITY_TYPE_ID, entityType.id)
        params.put(ENTITY_ID, entityId.toLong())
        return params
    }

    /**
     * get list of AppUser by entity type id and entity id
     * @param parameters -parameters send from UI
     * @param obj -N/A
     * @return -a map containing list of necessary objects for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map execute(Map result) {
        try {
            long entityTypeId = (long) result.get(ENTITY_TYPE_ID)
            long entityId = (long) result.get(ENTITY_ID)
            List<GroovyRowResult> userList = userList(entityTypeId, entityId)
            List lstUser = appUserService.listForKendoDropdown(userList, null, "ALL")
            result.put(USER_LIST, lstUser)
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
     * Since there is no success message return the same map
     * @param result -map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result -map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    private static final String SELECT_QUERY = """
        SELECT au.id AS id, au.username AS name
        FROM app_user_entity aue
            LEFT JOIN app_user au ON au.id = aue.app_user_id
        WHERE aue.entity_id = :entityId AND aue.entity_type_id = :entityTypeId
            AND au.company_id = :companyId
        ORDER BY username
    """

    private List<GroovyRowResult> userList(long entityTypeId, long entityId) {
        Map queryParams = [
                entityTypeId: entityTypeId,
                entityId: entityId,
                companyId: getCompanyId()
        ]
        List<GroovyRowResult> userList = executeSelectSql(SELECT_QUERY, queryParams)
        return userList
    }
}
