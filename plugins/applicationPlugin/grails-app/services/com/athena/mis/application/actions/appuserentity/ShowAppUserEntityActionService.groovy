package com.athena.mis.application.actions.appuserentity

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppBankBranch
import com.athena.mis.application.entity.AppGroup
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.*
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for AppUserEntity(user entity mapping) CRUD & list of AppUserEntity object(s) for grid
 *  For details go through Use-Case doc named 'ShowAppUserEntityActionService'
 */
class ShowAppUserEntityActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String MAP_APP_USER_ENTITY = "appUserEntityMap"
    private static final String ENTITY_TYPE_ID = "entityTypeId"
    private static final String ENTITY_ID = "entityId"
    private static final String RESERVED_ID = "reservedId"

    ProjectService projectService
    AppGroupService appGroupService
    AppBankBranchService appBankBranchService
    AppMyFavouriteService appMyFavouriteService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService
    @Autowired(required = false)
    PtPluginConnector ptProjectTrackImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService

    /**
     * Do nothing for pre operation
     */
    public Map executePreCondition(Map params) {
        return params
    }

    /**
     * 1. check required parameters
     * 2. get list of AppUserEntity(user entity mapping) object(s) for grid by entity type id and entity id
     * 3. build a map with necessary properties of AppUserEntity object to show on UI
     * @param parameters -parameters send from UI
     * @return -a map containing list of necessary objects for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            String entityIdStr = result.oId ? result.oId : result.cId ? result.cId : result.entityId
            // check required parameters
            if (!result.entityTypeId || !entityIdStr) {
                return super.setError(result, ERROR_FOR_INVALID_INPUT)
            }
            long entityTypeId = Long.parseLong(result.entityTypeId.toString())
            long entityId = Long.parseLong(entityIdStr)
            // get AppUserEntity(user entity mapping) list for grid
            SystemEntity appUserEntityType = appSystemEntityCacheService.read(entityTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, super.getCompanyId())
            Map appUserEntityMap = buildAppUserEntityMap(appUserEntityType, entityId)
            if (appUserEntityMap.isError) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, appUserEntityMap.errMsg.toString())
            }
            result.put(MAP_APP_USER_ENTITY, appUserEntityMap)
            result.put(ENTITY_TYPE_ID, entityTypeId)
            result.put(ENTITY_ID, entityId)
            result.put(RESERVED_ID, appUserEntityType.reservedId)
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
     * Wrap AppUserEntity(user entity mapping) object list
     * @param obj -a map received from execute method
     * @return -all necessary objects to show on UI
     * map contains isError(true/false) depending on method success
     */
    public Map buildSuccessResultForUI(Map result) {
        result
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build app user entity map with necessary properties
     * @param appUserEntityType -object of SystemEntity (app user entity type e.g. PROJECT, INVENTORY etc.)
     * @param entityId -entity id
     * @return -a map containing entity type name, entity name, plugin id and corresponding left menu link
     */
    private Map buildAppUserEntityMap(SystemEntity appUserEntityType, long entityId) {
        String entityTypeName = appUserEntityType.key
        String entityName = EMPTY_SPACE
        String leftMenu = EMPTY_SPACE
        boolean isError = false
        String errMsg
        int pluginId = 0
        switch (appUserEntityType.reservedId) {
            case appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_PROJECT:
                Project project = projectService.read(entityId)
                if (!project) {
                    isError = true
                    errMsg = "Selected project not found"
                    break
                }
                entityName = project.name
                pluginId = PluginConnector.PLUGIN_ID
                leftMenu = '#project/show'
                break
            case appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_EXCHANGE_HOUSE:
                Object rmsExchangeHouse = armsImplService.readByExchangeHouseId(entityId)
                if (!rmsExchangeHouse) {
                    isError = true
                    errMsg = "Selected exchange house not found"
                    break
                }
                entityName = rmsExchangeHouse.name
                pluginId = ArmsPluginConnector.PLUGIN_ID
                leftMenu = '#rmsExchangeHouse/show'
                break
            case appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_BANK_BRANCH:
                AppBankBranch bankBranch = (AppBankBranch) appBankBranchService.read(entityId)
                if (!bankBranch) {
                    isError = true
                    errMsg = "Selected branch not found"
                    break
                }
                entityName = bankBranch.name
                pluginId = PluginConnector.PLUGIN_ID
                leftMenu = '#appBank/show'
                break
            case appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_GROUP:
                AppGroup group = appGroupService.read(entityId)
                if (!group) {
                    isError = true
                    errMsg = "Selected group not found"
                    break
                }
                entityName = group.name
                pluginId = PluginConnector.PLUGIN_ID
                leftMenu = '#appGroup/show'
                break
            case appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_INVENTORY:
                Object inventory = invInventoryImplService.readInventory(entityId)
                if (!inventory) {
                    isError = true
                    errMsg = "Selected inventory not found"
                    break
                }
                entityName = inventory.name
                pluginId = InvPluginConnector.PLUGIN_ID
                leftMenu = '#invInventory/show'
                break
            case appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_PT_PROJECT:
                Object project = ptProjectTrackImplService.readPtProject(entityId)
                if (!project) {
                    isError = true
                    errMsg = "Selected project not found"
                    break
                }
                entityName = project.name
                pluginId = PtPluginConnector.PLUGIN_ID
                leftMenu = '#ptProject/show'
                break
            case appSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_AGENT:
                Object agent = exchangeHouseImplService.readAgentById(entityId)
                if (!agent) {
                    isError = true
                    errMsg = "Selected agent not found"
                    break
                }
                entityName = agent.name
                pluginId = ExchangeHousePluginConnector.PLUGIN_ID
                leftMenu = '#exhAgent/show'
                break
            default:
                break
        }
        Map appAttachmentMap = [
                entityTypeName: entityTypeName,
                entityName    : entityName,
                pluginId      : pluginId,
                leftMenu      : leftMenu,
                isError       : isError,
                errMsg        : errMsg
        ]
        return appAttachmentMap
    }
}
