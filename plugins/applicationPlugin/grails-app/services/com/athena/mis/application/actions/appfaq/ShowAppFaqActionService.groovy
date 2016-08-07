package com.athena.mis.application.actions.appfaq

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.integration.document.DocumentPluginConnector
import grails.converters.JSON
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for AppFaq CRUD and list of AppFaq for grid
 *  For details go through Use-Case doc named 'ShowAppFaqActionService'
 */
class ShowAppFaqActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    AppMyFavouriteService appMyFavouriteService
    AppSystemEntityCacheService appSystemEntityCacheService


    @Autowired(required = false)
    DocumentPluginConnector documentImplService

    private static final String APP_FAQ_MAP = "appFaqMap"
    private static final String ENTITY_ID = "entityId"
    private static final String ENTITY_TYPE_ID = "entityTypeId"
    private static final String CREATE_FAQ_FOR_DOC_DOCUMENT = "Create Faq for "
    private static final String CREATE_FAQ_FOR_DOC_SUB_CATEGORY = "Create Faq for "

    /**
     * Do nothing for pre condition
     */
    public Map executePreCondition(Map parameters) {
        return parameters
    }

    /**
     * Get list of AppFaq object(s) for grid
     * @param parameters -parameters send from UI
     * @return -a map containing list of item object(s) for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            String versionIdStr = result.cId ? result.cId : result.oId
            if (!result.entityTypeId || !versionIdStr || !result.entityId) {
                return super.setError(result, ERROR_FOR_INVALID_INPUT)
            }
            long entityTypeId = Long.parseLong(result.entityTypeId.toString())
            long entityId = Long.parseLong(result.entityId.toString())
            String leftMenu = EMPTY_SPACE
            if (result.leftMenu) {
                leftMenu = result.leftMenu
            }
            SystemEntity systemEntity = appSystemEntityCacheService.read(entityTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_FAQ, super.getCompanyId())
            Map appNoteMap = getAppFaqMap(result, systemEntity, entityId, leftMenu)
            if (appNoteMap.isError) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, appNoteMap.errMsg.toString())
            }
            result.put(ENTITY_ID, entityId)
            result.put(ENTITY_TYPE_ID, entityTypeId)
            result.put(APP_FAQ_MAP, appNoteMap as JSON)
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
    private Map getAppFaqMap(Map result, SystemEntity systemEntity, long entityId, String leftMenu) {
        String entityTypeName = EMPTY_SPACE
        String entityName = EMPTY_SPACE
        String panelTitle = EMPTY_SPACE
        boolean isError = false
        String errMsg
        int pluginId = 1
        switch (systemEntity.reservedId) {
            case appSystemEntityCacheService.SYS_ENTITY_FAQ_DOC_DOCUMENT:
                Object object = documentImplService.readDocDocumentVersion(entityId)
                if (!object) {
                    isError = true
                    errMsg = "Select document could not be found."
                    break
                }
                entityTypeName = result.entityTypeName + COLON
                entityName = object.title ? object.title : "No Title"
                pluginId = DocumentPluginConnector.PLUGIN_ID
                panelTitle = CREATE_FAQ_FOR_DOC_DOCUMENT + result.entityTypeName
                break
            case appSystemEntityCacheService.SYS_ENTITY_FAQ_DOC_SUB_CATEGORY:
                Object object = documentImplService.readDocSubCategory(entityId)
                if (!object) {
                    isError = true
                    errMsg = "Select sub category could not be found."
                    break
                }
                entityTypeName = result.entityTypeName + COLON
                entityName = object.name
                pluginId = DocumentPluginConnector.PLUGIN_ID
                panelTitle = CREATE_FAQ_FOR_DOC_SUB_CATEGORY + result.entityTypeName
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
