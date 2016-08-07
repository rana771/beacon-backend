package com.athena.mis.application.actions.appattachment

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppMail
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppMailService
import com.athena.mis.application.service.AppMyFavouriteService
import com.athena.mis.application.service.AppSystemEntityCacheService
import com.athena.mis.application.service.ProjectService
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.utility.DateUtility
import grails.converters.JSON
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for AppAttachment CRUD & list of AppAttachment object(s) for grid
 *  For details go through Use-Case doc named 'ShowAppAttachmentActionService'
 */
class ShowAppAttachmentActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String LEFT_MENU = "leftMenu"
    private static final String ENTITY_ID = "entityId"
    private static final String ENTITY_TYPE_ID = "entityTypeId"
    private static final String APP_ATTACHMENT_MAP = "appAttachmentMap"
    private static final String URL = "url"

    AppMailService appMailService
    ProjectService projectService
    AppMyFavouriteService appMyFavouriteService
    AppSystemEntityCacheService appSystemEntityCacheService

    @Autowired(required = false)
    BudgPluginConnector budgBudgetImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    PtPluginConnector ptProjectTrackImplService

    /**
     * do nothing for pre condition
     */
    public Map executePreCondition(Map params) {
        String leftMenu = EMPTY_SPACE
        if (params.leftMenu) {
            leftMenu = params.leftMenu
            if (leftMenu.find("#")) {
                leftMenu = params.leftMenu.toString()
            } else {
                leftMenu = '#' + params.leftMenu.toString()
            }
            if (params.url) {
                params.put(URL, params.url)
            } else {
                params.put(URL, leftMenu)
            }
        }
        params.put(LEFT_MENU, leftMenu)
        return params
    }

    /**
     * get list of AppAttachment object(s) for grid
     * @param result -parameters from execute pre condition method
     * @return -a map containing list of item object(s) for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Map execute(Map result) {
        try {
            String entityStr = result.oId ? result.oId : result.cId
            long entityTypeId = Long.parseLong(result.entityTypeId.toString())
            long entityId = Long.parseLong(entityStr)
            String leftMenu = result.get(LEFT_MENU)
            SystemEntity systemEntity = appSystemEntityCacheService.read(entityTypeId, appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, super.getCompanyId())
            Map appAttachmentMap = buildAppAttachmentMap(systemEntity, entityId, leftMenu)
            result.put(APP_ATTACHMENT_MAP, appAttachmentMap as JSON)
            result.put(ENTITY_ID, entityId)
            result.put(ENTITY_TYPE_ID, entityTypeId)
            if (appAttachmentMap.isError) {
                appMyFavouriteService.setIsDirtyAndIsDefault(result)
                return super.setError(result, appAttachmentMap.errMsg)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * There is no postCondition, so return the same map as received
     * @param result - resulting map from execute
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map executePostCondition(Map result) {
        return result
    }

    /**
     * Since there is no success message return the same map
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return result
    }

    /**
     * The input-parameter Map must have "isError:true" with corresponding message
     * @param result - map returned from previous methods
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildFailureResultForUI(Map result) {
        return result
    }

    /**
     * Build AppAttachment with dynamic left menu
     * @param systemEntity - object of SystemEntity
     * @param entityId - entity id
     * @param leftMenu - leftMenu
     * @return -  a map containing entity type name, entity name, plugin id and corresponding left menu link
     */
    private Map buildAppAttachmentMap(SystemEntity systemEntity, long entityId, String leftMenu) {
        String entityTypeName = systemEntity.key + COLON
        String entityName = EMPTY_SPACE
        String panelTitle = EMPTY_SPACE
        boolean isError = false
        String errMsg = null
        int pluginId = 0
        switch (systemEntity.reservedId) {
            case appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_BUDG_SPRINT:
                pluginId = BudgPluginConnector.PLUGIN_ID
                leftMenu = '#budgSprint/show'
                panelTitle = 'Create Attachment for Sprint'
                Object budgSprint = budgBudgetImplService.readBudgSprint(entityId)
                if (budgSprint == null) {
                    isError = true
                    errMsg = "Selected sprint not found."
                    break
                }
                entityName = budgSprint.name
                break
            case appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_BUDGET:
                pluginId = BudgPluginConnector.PLUGIN_ID
                panelTitle = 'Create Attachment for Budget'
                leftMenu = '#budgBudget/show'
                Object budget = budgBudgetImplService.readBudget(entityId)
                if (budget == null) {
                    isError = true
                    errMsg = "Selected budget not found."
                    break
                }
                if (budget.isProduction) {
                    leftMenu = '#budgBudget/showForProduction'
                }
                entityName = budget.budgetItem
                break
            case appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_PROJECT:
                pluginId = PluginConnector.PLUGIN_ID
                leftMenu = '#project/show'
                panelTitle = 'Create Attachment for Project'
                Project project = projectService.read(entityId)
                if (project == null) {
                    isError = true
                    errMsg = "Selected project not found."
                    break
                }
                entityName = project.name
                break
            case appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_APP_MAIL:
                pluginId = PluginConnector.PLUGIN_ID
                leftMenu = '#appMail/showForComposeMail'
                panelTitle = 'Create Attachment for Mail'
                AppMail mail = appMailService.read(entityId)
                if (mail == null) {
                    isError = true
                    errMsg = "Selected mail not found."
                    break
                }
                entityName = mail.subject
                break
            case appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_FINANCIAL_YEAR:
                pluginId = AccPluginConnector.PLUGIN_ID
                leftMenu = '#accFinancialYear/show'
                panelTitle = 'Create Attachment for Financial Year'
                Object financialYear = accAccountingImplService.readFinancialYear(entityId)
                if (financialYear == null) {
                    isError = true
                    errMsg = "Selected financial year not found."
                    break
                }
                entityName = DateUtility.getLongDateForUI(financialYear.startDate) + TO + DateUtility.getLongDateForUI(financialYear.endDate)
                break
            case appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_EXH_CUSTOMER:
                pluginId = ExchangeHousePluginConnector.PLUGIN_ID
                leftMenu = "#exhCustomer/showCustomerDetails"
                panelTitle = 'Create Attachment for Exchange House Customer'
                Object exhCustomer = exchangeHouseImplService.readCustomer(entityId)
                if (exhCustomer == null) {
                    isError = true
                    errMsg = "Selected custormer not found."
                    break
                }
                entityName = exhCustomer.name + (exhCustomer.surname ? (SINGLE_SPACE + exhCustomer.surname) : EMPTY_SPACE) + PARENTHESIS_START + exhCustomer.code + PARENTHESIS_END
                break
            case appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_PT_BACKLOG:
                pluginId = PtPluginConnector.PLUGIN_ID
                leftMenu = '#ptBacklog/show'
                panelTitle = 'Create Attachment for Backlog'
                Object backlog = ptProjectTrackImplService.readTask(entityId)
                if (backlog == null) {
                    isError = true
                    errMsg = "Selected backlog not found."
                    break
                }
                entityName = backlog.idea
                break
            case appSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_PT_BUG:
                pluginId = PtPluginConnector.PLUGIN_ID
                panelTitle = 'Create Attachment for Bug'
                Object bug = ptProjectTrackImplService.readBug(entityId)
                if (bug == null) {
                    isError = true
                    errMsg = "Selected bug not found."
                    break
                }
                entityName = bug.title
                break
            default:
                break
        }
        Map appAttachmentMap = [
                entityTypeName: entityTypeName,
                entityName    : entityName,
                pluginId      : pluginId,
                leftMenu      : leftMenu,
                panelTitle    : panelTitle,
                isError       : isError,
                errMsg        : errMsg
        ]
        return appAttachmentMap
    }
}
