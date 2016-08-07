package com.athena.mis.application.actions.appnote

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppShellScript
import com.athena.mis.application.entity.DbInstanceQuery
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.AppShellScriptService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.DbInstanceQueryService
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import grails.converters.JSON
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for AppNote CRUD and list of AppNote for grid
 *  For details go through Use-Case doc named 'ShowAppNoteActionService'
 */
class ShowAppNoteActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    DbInstanceQueryService dbInstanceQueryService
    AppShellScriptService appShellScriptService
    AppMyFavouriteService appMyFavouriteService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Autowired(required = false)
    PtPluginConnector ptProjectTrackImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService

    private static final String APP_NOTE_MAP = "appNoteMap"
    private static final String ENTITY_TYPE_ID = "entityTypeId"
    private static final String ENTITY_ID = "entityId"
    private static final String REFERENCE = "Ref No"
    private static final String NAME = "Name"
    private static final String CREATE_NOTE_FOR_TASK = "Create Note for Task"
    private static final String CREATE_NOTE_FOR_BUG = "Create Note for Bug"
    private static final String CREATE_NOTE_FOR_CUSTOMER = "Create Note for Customer"
    private static final String CREATE_NOTE_FOR_QUERY = "Create Note for Query"
    private static final String CREATE_NOTE_FOR_SCRIPT = "Create Note for Script"

    /**
     * Do nothing for pre condition
     */
    public Map executePreCondition(Map parameters) {
        return parameters
    }

    /**
     * Get list of AppNote object(s) for grid
     * @param parameters -parameters send from UI
     * @return -a map containing list of item object(s) for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            String entityIdStr = result.oId ? result.oId : result.cId ? result.cId : result.entityId
            if (!result.entityTypeId || !entityIdStr) {
                return super.setError(result, ERROR_FOR_INVALID_INPUT)
            }
            long entityTypeId = Long.parseLong(result.entityTypeId)
            long entityId = Long.parseLong(entityIdStr)
            String leftMenu = EMPTY_SPACE
            if (result.leftMenu) {
                leftMenu = result.leftMenu        //different left menus for exchange house
            }
            SystemEntity systemEntity = appSystemEntityCacheService.read(entityTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, super.getCompanyId())
            Map appNoteMap = buildAppNoteMap(systemEntity, entityId, leftMenu)
            if (appNoteMap.isError) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, appNoteMap.errMsg.toString())
            }
            result.put(APP_NOTE_MAP, appNoteMap as JSON)
            result.put(ENTITY_TYPE_ID, entityTypeId)
            result.put(ENTITY_ID, entityId)
            return result
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

    /**
     * Build app note map with necessary objects
     * @param systemEntity -object of SystemEntity
     * @param entityId -entity id
     * @return -a map containing entity type name, entity name, plugin id and corresponding left menu link
     */
    private Map buildAppNoteMap(SystemEntity systemEntity, long entityId, String leftMenu) {
        String entityTypeName = EMPTY_SPACE
        String entityName = EMPTY_SPACE
        String panelTitle = EMPTY_SPACE
        boolean isError = false
        String errMsg
        int pluginId = 1
        switch (systemEntity.reservedId) {
            case appSystemEntityCacheService.SYS_ENTITY_NOTE_PT_TASK:
                Object ptTask = ptProjectTrackImplService.readTask(entityId)
                if (!ptTask) {
                    isError = true
                    errMsg = "Select task not found"
                    break
                }
                entityName = ptTask.idea
                entityTypeName = systemEntity.key + COLON
                pluginId = PtPluginConnector.PLUGIN_ID
                leftMenu = '#ptBacklog/showMyBacklog'
                panelTitle = CREATE_NOTE_FOR_TASK
                break
            case appSystemEntityCacheService.SYS_ENTITY_NOTE_PT_BUG:
                Object bug = ptProjectTrackImplService.readBug(entityId)
                if (!bug) {
                    isError = true
                    errMsg = "Select bug not found"
                    break
                }
                entityName = bug.title
                entityTypeName = systemEntity.key + COLON
                pluginId = PtPluginConnector.PLUGIN_ID
                panelTitle = CREATE_NOTE_FOR_BUG
                break
            case appSystemEntityCacheService.SYS_ENTITY_NOTE_TASK:
                Object exhTask = exchangeHouseImplService.readTask(entityId)
                if (!exhTask) {
                    isError = true
                    errMsg = "Select task not found"
                    break
                }
                entityName = exhTask.refNo
                entityTypeName = REFERENCE + COLON
                pluginId = ExchangeHousePluginConnector.PLUGIN_ID
                panelTitle = CREATE_NOTE_FOR_TASK
                break
            case appSystemEntityCacheService.SYS_ENTITY_NOTE_CUSTOMER:
                Object exhCustomer = exchangeHouseImplService.readCustomer(entityId)
                if (!exhCustomer) {
                    isError = true
                    errMsg = "Select customer not found"
                    break
                }
                entityName = exhCustomer.surname ? exhCustomer.name + SINGLE_SPACE + exhCustomer.surname : exhCustomer.name
                entityTypeName = NAME + COLON
                pluginId = ExchangeHousePluginConnector.PLUGIN_ID
                panelTitle = CREATE_NOTE_FOR_CUSTOMER
                break
            case appSystemEntityCacheService.SYS_ENTITY_NOTE_DB_QUERY:
                DbInstanceQuery query = dbInstanceQueryService.read(entityId)
                if (!query) {
                    isError = true
                    errMsg = "Select query not found"
                    break
                }
                entityName = query.name
                entityTypeName = systemEntity.key + COLON
                pluginId = PluginConnector.PLUGIN_ID
                panelTitle = CREATE_NOTE_FOR_QUERY
                break
            case appSystemEntityCacheService.SYS_ENTITY_NOTE_SCRIPT:
                AppShellScript script = appShellScriptService.read(entityId)
                if (!script) {
                    isError = true
                    errMsg = "Select script not found"
                    break
                }
                SystemEntity scriptType = appSystemEntityCacheService.read(script.scriptTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_SCRIPT, super.getCompanyId())
                entityName = script.name
                entityTypeName = systemEntity.key + COLON
                pluginId = PluginConnector.PLUGIN_ID
                panelTitle = CREATE_NOTE_FOR_SCRIPT
                if (scriptType.reservedId == appSystemEntityCacheService.SYS_ENTITY_SCRIPT_SHELL) {
                    leftMenu = "#appShellScript/show?plugin=" + pluginId
                } else if (scriptType.reservedId == appSystemEntityCacheService.SYS_ENTITY_SCRIPT_SQL) {
                    leftMenu = "#appShellScript/showSql?plugin=" + pluginId
                }
                break
            default:
                break
        }
        Map appNoteMap = [
                entityTypeName: entityTypeName,
                entityName    : entityName,
                pluginId      : pluginId,
                leftMenu      : leftMenu,
                panelTitle    : panelTitle,
                isError       : isError,
                errMsg        : errMsg
        ]
        return appNoteMap
    }
}
