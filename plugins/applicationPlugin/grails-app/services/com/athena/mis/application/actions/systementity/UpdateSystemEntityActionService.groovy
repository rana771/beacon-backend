package com.athena.mis.application.actions.systementity

import com.athena.mis.ActionServiceIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.*
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.fixedasset.FxdPluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update system entity object and grid data
 *  For details go through Use-Case doc named 'UpdateSystemEntityActionService'
 */
class UpdateSystemEntityActionService extends BaseService implements ActionServiceIntf {

    private Logger log = Logger.getLogger(getClass())

    SystemEntityService systemEntityService
    AppSystemEntityCacheService appSystemEntityCacheService
    AppUserEntityService appUserEntityService
    AppAttachmentService appAttachmentService
    AppNoteService appNoteService
    SupplierService supplierService
    ItemService itemService
    ContentCategoryService contentCategoryService
    @Autowired(required = false)
    BudgPluginConnector budgBudgetImplService
    @Autowired(required = false)
    AccPluginConnector accAccountingImplService
    @Autowired(required = false)
    InvPluginConnector invInventoryImplService
    @Autowired(required = false)
    ExchangeHousePluginConnector exchangeHouseImplService
    @Autowired(required = false)
    PtPluginConnector ptProjectTrackImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired(required = false)
    SarbPluginConnector sarbImplService
    @Autowired(required = false)
    DocumentPluginConnector documentImplService
    @Autowired(required = false)
    ELearningPluginConnector elearningImplService
    @Autowired(required = false)
    DataPipeLinePluginConnector dataPipeLineImplService

    private static final String UPDATE_SUCCESS_MESSAGE = "System entity has been updated successfully"
    private static final String SYSTEM_ENTITY = "systemEntity"
    private static final String DUPLICATE_ENTITY_MSG = "Same system entity already exists"
    private static final String IS_RESERVED_MSG = "Only key and value can be updated for a reserved system entity"
    private static
    final String HAS_ASSOCIATION_PURCHASE_ORDER = " purchase order(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_BUDGET = " budget(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_COA = " chart of account(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_FXD_ASSET_DETAILS = " fixed asset details(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_VOUCHER = " voucher(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_VOUCHER_TYPE_COA = " voucher type coa(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_APP_ATTACHMENT = " attachment(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_APP_NOTE = " note(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_CONTENT_CATEGORY = " content category(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_SUPPLIER = " supplier(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_ITEM = " item(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_PRODUCTION_DETAILS = " production details(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_INVENTORY_TRANSACTION = " inventory transaction(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_INVENTORY = " inventory(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_USER_ENTITY = " user entity mapping(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_EXH_TASK = " exh task(s) associated with selected system entity, only key and value can be updated"
    private static final String HAS_ASSOCIATION_SARB_TASK = " sarb task(s) associated with selected system entity"
    private static
    final String HAS_ASSOCIATION_BACKLOG_CHANGE_REQUEST = " backlog change request(s) are associated with selected system entity"
    private static final String HAS_ASSOCIATION_WITH_SPRINT = " sprint(s) are associated with selected system entity"
    private static final String HAS_ASSOCIATION_WITH_BACKLOG = " backlog(s) are associated with selected system entity"
    private static
    final String HAS_ASSOCIATION_WITH_BACKLOG_STATUS = " backlog(s) are associated with selected system entity"
    private static
    final String HAS_ASSOCIATION_WITH_ACCEPTANCE_STATUS = " acceptance criteria(s) are associated with selected system entity"
    private static final String HAS_ASSOCIATION_WITH_BUG = " bug(s) are associated with selected system entity"
    private static final String HAS_ASSOCIATION_WITH_BUG_STATUS = " bug(s) are associated with selected system entity"
    private static final String HAS_ASSOCIATION_WITH_BUG_TYPE = " bug(s) are associated with selected system entity"
    private static
    final String HAS_ASSOCIATION_WITH_ENTITY_RELATION_TYPE = " foreign key(s) are associated with selected system entity"
    private static final String HAS_ASSOCIATION_WITH_FIELD = " field(s) are associated with selected system entity"
    private static
    final String HAS_ASSOCIATION_WITH_ACCEPTANCE_TYPE = " acceptance criteria(s) are associated with selected system entity"
    private static
    final String HAS_ASSOCIATION_WITH_BUDGET_TASK = " budget task(s) are associated with selected system entity"
    private static
    final String HAS_ASSOCIATION_WITH_APP_SCHEDULE = " application schedule(s) are associated with selected system entity"
    private static final String HAS_ASSOCIATION_WITH_NOTE = " note(s) are associated with selected system entity"
    private static
    final String HAS_ASSOCIATION_WITH_TRANSACTION_LOG = " transaction log(s) are associated with selected system entity"
    private static
    final String HAS_ASSOCIATION_DOC_QUESTION = " question(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_DOC_DOCUMENT_INDEX_TYPE = " document index type(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_DOC_DOCUMENT = " document(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_DOC_DOCUMENT_TRASH = " document trash(es) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_DOC_DOCUMENT_CATEGORY_TYPE = " document category type(s) associated with selected system entity"
    private static
    final String HAS_ASSOCIATION_DPL_OFFLINE_DATA_FEED_STATUS = " offline data feed file(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_DPL_DATA_EXPORT = " data export associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_WITH_CHANGE_REQUEST_TYPE = " Change request(s) are associated with selected system entity"
    private static
    final String HAS_ASSOCIATION_WITH_DB_INSTANCE_QUERY = " Query(s) are associated with selected system entity"
    private static
    final String HAS_ASSOCIATION_WITH_DB_INSTANCE = "Db instances are associated with selected system entity"
    private static
    final String HAS_ASSOCIATION_EL_COURSE = " course(s) associated with selected system entity, only key and value can be updated"
    private static
    final String HAS_ASSOCIATION_EL_ASSIGNMENT = " assignment(s) associated with selected system entity, only key and value can be updated"

    /**
     * Check pre conditions before updating system entity
     * 1. Check input validation
     * 2. Check association with other domains
     * 3. Build new system entity object
     * 4. Check duplicate system entity
     * 5. Validate SystemEntity object
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return - a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Map executePreCondition(Map params) {
        try {
            // Check required params
            if (!params.id) {
                return super.setError(params, ERROR_FOR_INVALID_INPUT)
            }
            long systemEntityId = Long.parseLong(params.id.toString())
            long version = Long.parseLong(params.version.toString())
            boolean isActive = params.isActive ? true : false
            SystemEntity oldSysEntity = systemEntityService.read(systemEntityId)
            String msg
            // check input validation
            msg = checkInputValidation(oldSysEntity, isActive, version)
            if (msg) {
                return super.setError(params, msg)
            }

            // Association check with different domains
            if (oldSysEntity.isActive && !isActive) {
                long companyId = oldSysEntity.companyId
                msg = hasAssociation(oldSysEntity, companyId)
                if (msg) {
                    return super.setError(params, msg)
                }
            }

            // Build new system entity object
            SystemEntity newSysEntity = buildSystemEntityObject(params, oldSysEntity)
            // Check duplicate system entity
            int duplicateCount = systemEntityService.countByTypeAndKeyIlikeAndIdNotEqualAndCompanyId(newSysEntity.type, newSysEntity.key, newSysEntity.id, newSysEntity.companyId)
            if (duplicateCount > 0) {
                return super.setError(params, DUPLICATE_ENTITY_MSG)
            }
            // validate new system entity
            newSysEntity.validate()
            if (newSysEntity.hasErrors()) {
                return setError(params, ERROR_FOR_INVALID_INPUT)
            }
            params.put(SYSTEM_ENTITY, newSysEntity)
            return params
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Update system entity object in DB & update cache utility
     * 1. This function is in transactional block and will roll back in case of any exception
     * 2. Get system entity from executePreCondition method
     * @param parameters - N/A
     * @param obj - map from executePreCondition method
     * @return - new system entity object
     */
    @Transactional
    public Map execute(Map result) {
        try {
            SystemEntity newSysEntity = (SystemEntity) result.get(SYSTEM_ENTITY)
            systemEntityService.update(newSysEntity)
            updateSystemEntityCacheService(newSysEntity)
            result.put(SYSTEM_ENTITY, newSysEntity)
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
     * 1. put success message
     * @param result - map from execute/executePost method
     * @return - same map of input-parameter containing isError(true/false)
     */
    public Map buildSuccessResultForUI(Map result) {
        return super.setSuccess(result, UPDATE_SUCCESS_MESSAGE)
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
     * 1. check object existence
     * 2. check if SystemEntity is reserved or not
     * @param oldSysEntity - object of SystemEntity
     * @param isActive - boolean value (true/false)
     * @return - a string containing error message or null value depending on object validation
     */
    private String checkInputValidation(SystemEntity oldSysEntity, boolean isActive, long version) {
        // check existence of old system entity
        if (!oldSysEntity || oldSysEntity.version != version) {
            return ENTITY_NOT_FOUND_ERROR_MESSAGE
        }
        // check the system is reserved, is active or not
        if (oldSysEntity.reservedId > 0 && (oldSysEntity.isActive != isActive)) {
            return IS_RESERVED_MSG
        }
        return null
    }

    /**
     * Update system entity cache service
     * @param systemEntity - object of SystemEntity
     * @return true/false
     */
    private boolean updateSystemEntityCacheService(SystemEntity systemEntity) {
        switch (systemEntity.pluginId) {
            case PluginConnector.PLUGIN_ID:
                appSystemEntityCacheService.initByType(systemEntity.type)
                break
            case DocumentPluginConnector.PLUGIN_ID:
                documentImplService.initByType(systemEntity.type)
                break
            case ELearningPluginConnector.PLUGIN_ID:
                elearningImplService.initByType(systemEntity.type)
                break
            case DataPipeLinePluginConnector.PLUGIN_ID:
                dataPipeLineImplService.initByType(systemEntity.type)
                break
            case AccPluginConnector.PLUGIN_ID:
                accAccountingImplService.initByType(systemEntity.type)
                break
            case InvPluginConnector.PLUGIN_ID:
                invInventoryImplService.initByType(systemEntity.type)
                break
            case BudgPluginConnector.PLUGIN_ID:
                budgBudgetImplService.initByType(systemEntity.type)
                break
            case ExchangeHousePluginConnector.PLUGIN_ID:
                exchangeHouseImplService.initByType(systemEntity.type)
                break
            case SarbPluginConnector.PLUGIN_ID:
                sarbImplService.initByType(systemEntity.type)
                break
            case ArmsPluginConnector.PLUGIN_ID:
                armsImplService.initByType(systemEntity.type)
                break
            case PtPluginConnector.PLUGIN_ID:
                ptProjectTrackImplService.initByType(systemEntity.type)
                break
            default:
                return false
        }
        return true
    }

    /**
     * Build system entity object
     * @param params - serialized parameters from UI
     * @param oldSystemEntity - old SystemEntity object
     * @return - built system entity object
     */
    private SystemEntity buildSystemEntityObject(Map params, SystemEntity oldSystemEntity) {
        SystemEntity sysEntity = new SystemEntity(params)
        oldSystemEntity.key = sysEntity.key
        oldSystemEntity.value = sysEntity.value
        oldSystemEntity.isActive = sysEntity.isActive
        oldSystemEntity.updatedBy = super.getAppUser().id
        oldSystemEntity.updatedOn = new Date()
        return oldSystemEntity
    }

    /**
     * Association check with different domains
     * @param systemEntity - object of SystemEntity
     * @param companyId - company id
     * @return - a string containing error message or null value depending on association check
     */
    private String hasAssociation(SystemEntity systemEntity, long companyId) {
        long systemEntityId = systemEntity.id
        long systemEntityTypeId = systemEntity.type
        Integer count = 0

        switch (systemEntityTypeId) {
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_PAYMENT_METHOD:
                if (PluginConnector.isPluginInstalled(ProcPluginConnector.PLUGIN_NAME)) {
                    count = countPurchaseOrder(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_PURCHASE_ORDER
                    }
                }
                break
            case accAccountingImplService?.getSystemEntityTypeSource():
                if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
                    count = countCOAForSourceType(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_COA
                    }
                }
                break
            case accAccountingImplService?.getSystemEntityTypeVoucher():
                if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
                    count = countVoucherForVoucherType(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_VOUCHER
                    }

                    count = countVoucherTypeCOAForVoucherType(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_VOUCHER_TYPE_COA
                    }
                }
                break
            case invInventoryImplService?.getSystemEntityTypeProductionLine():
                if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
                    count = countProductionDetails(systemEntityId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_PRODUCTION_DETAILS
                    }
                }
                break
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT:
                if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
                    count = countBudget(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_BUDGET
                    }
                }
                break
            case invInventoryImplService?.getSystemEntityTypeTransaction():
                if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
                    count = countInventoryTransaction(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_INVENTORY_TRANSACTION
                    }
                }
                break
            case invInventoryImplService?.getSystemEntityTypeTransactionEntity():
                if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
                    count = countInventoryTransactionForEntityType(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_INVENTORY_TRANSACTION
                    }
                }
                break
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_VALUATION:
                count = itemService.countByValuationTypeId(systemEntityId)
                if (count > 0) {
                    return count.toString() + HAS_ASSOCIATION_ITEM
                }
                break
            case invInventoryImplService?.getSystemEntityTypeInventory():
                if (PluginConnector.isPluginInstalled(InvPluginConnector.PLUGIN_NAME)) {
                    count = countInventory(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_INVENTORY
                    }
                }
                break
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_OWNER:
                if (PluginConnector.isPluginInstalled(FxdPluginConnector.PLUGIN_NAME)) {
                    count = countFixedAssetDetails(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_FXD_ASSET_DETAILS
                    }
                }
                break
            case accAccountingImplService?.getSystemEntityTypeInstrument():
                if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
                    count = countVoucher(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_VOUCHER
                    }
                }
                break
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY:
                count = appUserEntityService.countByEntityTypeId(systemEntityId)
                if (count > 0) {
                    return count.toString() + HAS_ASSOCIATION_USER_ENTITY
                }
                break
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY:
                count = appAttachmentService.countByEntityTypeIdAndCompanyId(systemEntityId, companyId)
                if (count > 0) {
                    return count.toString() + HAS_ASSOCIATION_APP_ATTACHMENT
                }
                break
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT:
                count = contentCategoryService.countByContentTypeId(systemEntityId)
                if (count > 0) {
                    return count.toString() + HAS_ASSOCIATION_CONTENT_CATEGORY
                }
                break
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY:
                count = appNoteService.countByEntityTypeIdAndCompanyId(systemEntityId, companyId)
                if (count > 0) {
                    return count.toString() + HAS_ASSOCIATION_APP_NOTE
                }
                break
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_SUPPLIER:
                count = supplierService.countBySupplierTypeIdAndCompanyId(systemEntityId, super.companyId)
                if (count > 0) {
                    return count.toString() + HAS_ASSOCIATION_SUPPLIER
                }

                if (PluginConnector.isPluginInstalled(AccPluginConnector.PLUGIN_NAME)) {
                    count = countCOA(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_COA
                    }
                }
                break
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY:
                count = itemService.countByCategoryId(systemEntityId)
                if (count > 0) {
                    return count.toString() + HAS_ASSOCIATION_ITEM
                }
                break
            case exchangeHouseImplService?.getSystemEntityTypePaidBy():
                if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
                    count = countExhTaskForPaidBy(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_EXH_TASK
                    }
                }
                break
            case exchangeHouseImplService?.getSystemEntityTypePaymentMethod():
                if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
                    count = countExhTaskForPaymentMethod(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_EXH_TASK
                    }
                }
                break
            case exchangeHouseImplService?.getSystemEntityTypeTaskStatus():
                if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
                    count = countExhTaskForTaskStatus(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_EXH_TASK
                    }
                }
                break
            case exchangeHouseImplService?.getSystemEntityTypeTaskType():
                if (PluginConnector.isPluginInstalled(ExchangeHousePluginConnector.PLUGIN_NAME)) {
                    count = countExhTaskForTaskType(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_EXH_TASK
                    }
                }
                break
            case sarbImplService?.getSystemEntityReviseTask():
                if (PluginConnector.isPluginInstalled(SarbPluginConnector.PLUGIN_NAME)) {
                    count = countSarbReviseTaskStatusType(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_SARB_TASK
                    }
                }
                break
            case ptProjectTrackImplService?.getSystemEntityTypeChangeRequestStatus():
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    count = countBacklogForChangeRequestStatus(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_BACKLOG_CHANGE_REQUEST
                    }
                }
                break
            case ptProjectTrackImplService?.getSystemEntityTypeSprintStatus():
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    count = countSprintForSprintStatus(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_WITH_SPRINT
                    }
                }
                break
            case ptProjectTrackImplService?.getSystemEntityTypeBacklogPriority():
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    count = countBacklogForPriorityStatus(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_WITH_BACKLOG
                    }
                }
                break
            case ptProjectTrackImplService?.getSystemEntityTypeBacklogStatus():
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    count = countBacklogForBacklogStatus(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_WITH_BACKLOG_STATUS
                    }
                }
                break
            case ptProjectTrackImplService?.getSystemEntityTypeAcceptanceCriteriaStatus():
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    count = countAcceptanceCriteriaForStatus(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_WITH_ACCEPTANCE_STATUS
                    }
                }
                break
            case ptProjectTrackImplService?.getSystemEntityTypeAcceptanceCriteriaType():
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    count = countAcceptanceCriteriaForType(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_WITH_ACCEPTANCE_TYPE
                    }
                    count = countChangeRequestForType(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_WITH_CHANGE_REQUEST_TYPE
                    }
                }
                break
            case ptProjectTrackImplService?.getSystemEntityTypeBugSeverity():
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    count = countBugForBugSeverity(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_WITH_BUG
                    }
                }
                break
            case ptProjectTrackImplService?.getSystemEntityTypeBugStatus():
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    count = countBugForBugStatus(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_WITH_BUG_STATUS
                    }
                }
                break
            case ptProjectTrackImplService?.getSystemEntityTypeBugType():
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    count = countBugForBugType(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_WITH_BUG_TYPE
                    }
                }
                break
            case ptProjectTrackImplService?.getSystemEntityTypeEntityRelationType():
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    count = countPtForeignKey(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_WITH_ENTITY_RELATION_TYPE
                    }
                }
                break
            case ptProjectTrackImplService?.getSystemEntityTypeDataType():
                if (PluginConnector.isPluginInstalled(PtPluginConnector.PLUGIN_NAME)) {
                    count = countFieldForDataType(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_WITH_FIELD
                    }
                }
                break

            case budgBudgetImplService?.getSystemEntityTypeBudgetTaskStatus():
                if (PluginConnector.isPluginInstalled(BudgPluginConnector.PLUGIN_NAME)) {
                    count = countBudgTaskForStatus(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_WITH_BUDGET_TASK
                    }
                }
                break
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_SCHEDULE:
                count = countAppScheduleForScheduleType(systemEntityId, companyId)
                if (count > 0) {
                    return count.toString() + HAS_ASSOCIATION_WITH_APP_SCHEDULE
                }
                break
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE:
                count = countAppNoteForType(systemEntityId, companyId)
                if (count > 0) {
                    return count.toString() + HAS_ASSOCIATION_WITH_NOTE
                }
                break
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG:
                count = countTransactionLogForType(systemEntityId, companyId)
                if (count > 0) {
                    return count.toString() + HAS_ASSOCIATION_WITH_TRANSACTION_LOG
                }
                break
            case documentImplService?.getSystemEntityTypeAnswer():
                if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
                    count = countDocQuestion(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_DOC_QUESTION
                    }
                }
                break
            case documentImplService?.getSystemEntityTypeDifficultyLevel():
                if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
                    count = countDocQuestionByDifficultyLevel(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_DOC_QUESTION
                    }
                }
                break
            case documentImplService?.getSystemEntityTypeDocumentIndex():
                if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
                    count = countDocDocumentType(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_DOC_DOCUMENT_INDEX_TYPE
                    }
                }
                break
            case documentImplService?.getSystemEntityTypeDocumentStatus():
                if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
                    count = countDocDocument(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_DOC_DOCUMENT
                    }
                    count = countDocDocumentTrash(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_DOC_DOCUMENT_TRASH
                    }
                }
                break
            case elearningImplService?.getSystemEntityTypeLanguage():
                if (PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)) {
                    count = countElCourse(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_EL_COURSE
                    }
                }
                break
            case elearningImplService?.getSystemEntityTypeAssignment():
                if (PluginConnector.isPluginInstalled(ELearningPluginConnector.PLUGIN_NAME)) {
                    count = countElAssignment(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_EL_ASSIGNMENT
                    }
                }
                break
            case documentImplService?.getSystemEntityTypeDocumentCategory():
                if (PluginConnector.isPluginInstalled(DocumentPluginConnector.PLUGIN_NAME)) {
                    count = countDocDocumentCategoryType(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_DOC_DOCUMENT_CATEGORY_TYPE
                    }
                }
                break
            case dataPipeLineImplService?.getSystemEntityTypeDataFeedStatus():
                if (PluginConnector.isPluginInstalled(DataPipeLinePluginConnector.PLUGIN_NAME)) {
                    count = countDplOfflineDataFeedFile(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_DPL_OFFLINE_DATA_FEED_STATUS
                    }
                }
                break
            case dataPipeLineImplService?.getSystemEntityTypeDataFeed():
                if (PluginConnector.isPluginInstalled(DataPipeLinePluginConnector.PLUGIN_NAME)) {
                    count = countDplDataExport(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_DPL_DATA_EXPORT
                    }
                }
                break
            case dataPipeLineImplService?.getSystemEntityTypeDataFeedCsv():
                if (PluginConnector.isPluginInstalled(DataPipeLinePluginConnector.PLUGIN_NAME)) {
                    count = countDplDataExport(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_DPL_DATA_EXPORT
                    }
                }
                break
            case dataPipeLineImplService?.getSystemEntityTypeDataFeedText():
                if (PluginConnector.isPluginInstalled(DataPipeLinePluginConnector.PLUGIN_NAME)) {
                    count = countDplDataExport(systemEntityId, companyId)
                    if (count > 0) {
                        return count.toString() + HAS_ASSOCIATION_DPL_DATA_EXPORT
                    }
                }
                break
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_QUERY:
                count = countDbInstanceQuery(systemEntityId, companyId)
                if (count > 0) {
                    return count.toString() + HAS_ASSOCIATION_WITH_DB_INSTANCE_QUERY
                }
                break
            case appSystemEntityCacheService.SYS_ENTITY_TYPE_DB_INSTANCE:
                count = countDbInstanceType(systemEntityId, companyId)
                if (count > 0) {
                    return count.toString() + HAS_ASSOCIATION_WITH_DB_INSTANCE
                }
                break
            default:
                break
        }
        return null
    }

    private static final String QUERY_PROC_PURCHASE_ORDER = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_order
            WHERE payment_method_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count Purchase order id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of Purchase order id
     */
    private int countPurchaseOrder(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_PROC_PURCHASE_ORDER, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_BUDG_BUDGET = """
            SELECT COUNT(id) AS count
            FROM budg_budget
            WHERE unit_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count Budget id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of budget id
     */
    private int countBudget(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_BUDG_BUDGET, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_ACC_CHART_OF_ACCOUNT = """
            SELECT COUNT(id) AS count
            FROM acc_chart_of_account
            WHERE source_category_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count chart of account id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of chart of account id
     */
    private int countCOA(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_ACC_CHART_OF_ACCOUNT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_COA_ACC_SOURCE_ID = """
            SELECT COUNT(id) AS count
            FROM acc_chart_of_account
            WHERE acc_source_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count chart of account id for source type by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of chart of account id
     */
    private int countCOAForSourceType(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_COA_ACC_SOURCE_ID, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_FXD_FIXED_ASSET_DETAILS = """
            SELECT COUNT(id) AS count
            FROM fxd_fixed_asset_details
            WHERE owner_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count fixed asset details id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of fixed asset details id
     */
    private int countFixedAssetDetails(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_FXD_FIXED_ASSET_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_ACC_VOUCHER = """
            SELECT COUNT(id) AS count
            FROM acc_voucher
            WHERE instrument_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count voucher id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of voucher id
     */
    private int countVoucher(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_ACC_VOUCHER, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_VOUCHER_TYPE_ID = """
            SELECT COUNT(id) AS count
            FROM acc_voucher
            WHERE voucher_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count voucher id for voucher type by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of voucher id
     */
    private int countVoucherForVoucherType(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_VOUCHER_TYPE_ID, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_ACC_VOUCHER_TYPE_COA = """
            SELECT COUNT(id) AS count
            FROM acc_voucher_type_coa
            WHERE acc_voucher_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count acc voucher type coa id for voucher type by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of acc voucher type coa id
     */
    private int countVoucherTypeCOAForVoucherType(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_ACC_VOUCHER_TYPE_COA, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_INV_PRODUCTION_DETAILS = """
            SELECT COUNT(id) AS count
            FROM inv_production_details
            WHERE production_item_type_id = :systemEntityId
    """

    /**
     * Count inventory production details id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of inventory production details id
     */
    private int countProductionDetails(long systemEntityId) {
        Map queryParams = [
                systemEntityId: systemEntityId
        ]
        List results = executeSelectSql(QUERY_INV_PRODUCTION_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_INV_INVENTORY_TRANSACTION = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction
            WHERE transaction_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count inventory transaction id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of inventory transaction id
     */
    private int countInventoryTransaction(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_INV_INVENTORY_TRANSACTION, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_TRANSACTION_ENTITY_TYPE_ID = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction
            WHERE transaction_entity_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count inventory transaction id for entity type by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of inventory transaction id
     */
    private int countInventoryTransactionForEntityType(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_TRANSACTION_ENTITY_TYPE_ID, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_INV_INVENTORY = """
            SELECT COUNT(id) AS count
            FROM inv_inventory
            WHERE type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count inventory id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of inventory id
     */
    private int countInventory(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_INV_INVENTORY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_PAID_BY = """
            SELECT COUNT(id) AS count
            FROM exh_task
            WHERE paid_by = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count exchange task id by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of exchange task id
     */
    private int countExhTaskForPaidBy(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_PAID_BY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_PAYMENT_METHOD = """
            SELECT COUNT(id) AS count
            FROM exh_task
            WHERE payment_method = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count exchange task id for payment method by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of exchange task id
     */
    private int countExhTaskForPaymentMethod(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_PAYMENT_METHOD, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_EXH_TASK = """
            SELECT COUNT(id) AS count
            FROM exh_task
            WHERE current_status = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count exchange task id for current status by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of exchange task id
     */
    private int countExhTaskForTaskStatus(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_EXH_TASK, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_TASK_TYPE_ID = """
            SELECT COUNT(id) AS count
            FROM exh_task
            WHERE task_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count exchange task id for task type by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of exchange task id
     */
    private int countExhTaskForTaskType(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_TASK_TYPE_ID, queryParams)
        int count = results[0].count
        return count
    }

    private static final String QUERY_SARB_REVISE_TASK_STATUS = """
            SELECT COUNT(id) AS count
            FROM sarb_task_details
            WHERE task_type_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * Count sarb task id for task type by system entity id
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - an integer count of sarb task id
     */
    private int countSarbReviseTaskStatusType(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(QUERY_SARB_REVISE_TASK_STATUS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_BACKLOG_FOR_SUGGESTION_STATUS = """
            SELECT COUNT(id) AS count
            FROM pt_suggestion
            WHERE status_id = :systemEntityId AND
                  company_id = :companyId
    """

    /**
     * get count of PtSuggestion by statusId
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of PtSuggestion
     */
    private int countBacklogForChangeRequestStatus(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_BACKLOG_FOR_SUGGESTION_STATUS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_SPRINT_FOR_SPRINT_STATUS = """
            SELECT COUNT(id) AS count
            FROM pt_sprint
            WHERE status_id = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of PtSprint by statusId
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of PtSprint
     */
    private int countSprintForSprintStatus(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_SPRINT_FOR_SPRINT_STATUS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_BACKLOG_FOR_PRIORITY_STATUS = """
            SELECT COUNT(id) AS count
            FROM pt_backlog
            WHERE priority_id = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of PtBacklog by priorityId
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of PtBacklog
     */
    private int countBacklogForPriorityStatus(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_BACKLOG_FOR_PRIORITY_STATUS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_BACKLOG_FOR_BACKLOG_STATUS = """
            SELECT COUNT(id) AS count
            FROM pt_backlog
            WHERE status_id = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of PtBacklog by statusId
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of PtBacklog
     */
    private int countBacklogForBacklogStatus(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_BACKLOG_FOR_BACKLOG_STATUS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACCEPTANCE_CRITERIA_FOR_STATUS = """
            SELECT COUNT(id) AS count
            FROM pt_acceptance_criteria
            WHERE status_id = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of PtAcceptanceCriteria by statusId
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of PtAcceptanceCriteria
     */
    private int countAcceptanceCriteriaForStatus(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_ACCEPTANCE_CRITERIA_FOR_STATUS, queryParams)
        int count = results[0].count
        return count
    }
    private static final String COUNT_BUG_FOR_BUG_SEVERITY = """
            SELECT COUNT(id) AS count
            FROM pt_bug
            WHERE severity = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of PtBug by severity
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of PtBug
     */
    private int countBugForBugSeverity(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_BUG_FOR_BUG_SEVERITY, queryParams)
        int count = results[0].count
        return count
    }
    private static final String COUNT_BUG_FOR_BUG_STATUS = """
            SELECT COUNT(id) AS count
            FROM pt_bug
            WHERE status = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of PtBug by status
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of PtBug
     */
    private int countBugForBugStatus(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_BUG_FOR_BUG_STATUS, queryParams)
        int count = results[0].count
        return count
    }
    private static final String COUNT_BUG_FOR_BUG_TYPE = """
            SELECT COUNT(id) AS count
            FROM pt_bug
            WHERE type = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of PtBug by type
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of PtBug
     */
    private int countBugForBugType(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_BUG_FOR_BUG_TYPE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PT_FOREIGN_KEY = """
        SELECT COUNT(id) AS count
        FROM pt_foreign_key
        WHERE relation_type_id = :systemEntityId
        AND company_id = :companyId
    """
    /**
     * get count of PtForeignKdy by type
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of PtBug
     */
    private int countPtForeignKey(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_PT_FOREIGN_KEY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_FIELD_FOR_DATA_TYPE = """
            SELECT COUNT(id) AS count
            FROM pt_field
            WHERE data_type_id = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of PtField by dataType
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of PtField
     */
    private int countFieldForDataType(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_FIELD_FOR_DATA_TYPE, queryParams)
        int count = results[0].count
        return count
    }
    private static final String COUNT_ACCEPTANCE_CRITERIA_FOR_TYPE = """
            SELECT COUNT(id) AS count
            FROM pt_acceptance_criteria
            WHERE type = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of PtAcceptanceCriteria by type
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of PtAcceptanceCriteria
     */
    private int countAcceptanceCriteriaForType(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_ACCEPTANCE_CRITERIA_FOR_TYPE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_BUDGET_TASK_FOR_STATUS = """
            SELECT COUNT(id) AS count
            FROM budg_task
            WHERE status_id = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of budgTask by status
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of budgTask
     */
    private int countBudgTaskForStatus(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_BUDGET_TASK_FOR_STATUS, queryParams)
        int count = results[0].count
        return count
    }
    private static final String COUNT_APP_SCHEDULE_FOR_TYPE = """
            SELECT COUNT(id) AS count
            FROM app_schedule
            WHERE schedule_type_id = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of appSchedule by status
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of appSchedule
     */
    private int countAppScheduleForScheduleType(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_APP_SCHEDULE_FOR_TYPE, queryParams)
        int count = results[0].count
        return count
    }
    private static final String COUNT_APP_NOTE_FOR_TYPE = """
            SELECT COUNT(id) AS count
            FROM app_note
            WHERE entity_note_type_id = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of AppNote by entityNoteTypeId
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of AppNote
     */
    private int countAppNoteForType(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_APP_NOTE_FOR_TYPE, queryParams)
        int count = results[0].count
        return count
    }
    private static final String COUNT_TRANSACTION_LOG_FOR_TYPE = """
            SELECT COUNT(id) AS count
            FROM transaction_log
            WHERE entity_type_id = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of transactionLog by entityTypeId
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of transactionLog
     */
    private int countTransactionLogForType(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_TRANSACTION_LOG_FOR_TYPE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_DOC_QUESTION = """
            SELECT COUNT(id) AS count
            FROM doc_question
            WHERE answer_type_id = :systemEntityId
            AND company_id = :companyId
    """

    /**
     * get count of docQuestion
     * @param systemEntityId
     * @param companyId
     * @return - count of docQuestion
     */
    private int countDocQuestion(long systemEntityId, long companyId) {
        Map queryParams = [systemEntityId: systemEntityId, companyId: companyId]
        List results = executeSelectSql(COUNT_DOC_QUESTION, queryParams)
        int count = results[0].count
        return count
    }
    private static final String COUNT_DOC_QUESTION_BY_DIFFICULTY_LEVEL = """
            SELECT COUNT(id) AS count
            FROM doc_question
            WHERE difficulty_level_id = :systemEntityId
            AND company_id = :companyId
    """

    /**
     * get count of docQuestion
     * @param systemEntityId
     * @param companyId
     * @return - count of docQuestion
     */
    private int countDocQuestionByDifficultyLevel(long systemEntityId, long companyId) {
        Map queryParams = [systemEntityId: systemEntityId, companyId: companyId]
        List results = executeSelectSql(COUNT_DOC_QUESTION_BY_DIFFICULTY_LEVEL, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_DOC_DOCUMENT_TYPE = """
            SELECT COUNT(id) AS count
            FROM doc_document_type
            WHERE index_type_id = :systemEntityId
            AND company_id = :companyId
    """

    /**
     * get count of docDocumentType
     * @param systemEntityId
     * @param companyId
     * @return - count of docDocumentType
     */
    private int countDocDocumentType(long systemEntityId, long companyId) {
        Map queryParams = [systemEntityId: systemEntityId, companyId: companyId]
        List results = executeSelectSql(COUNT_DOC_DOCUMENT_TYPE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_DOC_DOCUMENT = """
            SELECT COUNT(id) AS count
            FROM doc_document
            WHERE document_status_id = :systemEntityId
            AND company_id = :companyId
    """

    /**
     * get count of docDocument
     * @param systemEntityId
     * @param companyId
     * @return - count of docDocument
     */
    private int countDocDocument(long systemEntityId, long companyId) {
        Map queryParams = [systemEntityId: systemEntityId, companyId: companyId]
        List results = executeSelectSql(COUNT_DOC_DOCUMENT, queryParams)
        int count = results[0].count
        return count
    }
    private static final String COUNT_DOC_DOCUMENT_TRASH = """
            SELECT COUNT(id) AS count
            FROM doc_document_trash
            WHERE document_status_id = :systemEntityId
            AND company_id = :companyId
    """

    /**
     * get count of docDocumentTrash
     * @param systemEntityId
     * @param companyId
     * @return - count of docDocumentTrash
     */
    private int countDocDocumentTrash(long systemEntityId, long companyId) {
        Map queryParams = [systemEntityId: systemEntityId, companyId: companyId]
        List results = executeSelectSql(COUNT_DOC_DOCUMENT_TRASH, queryParams)
        int count = results[0].count
        return count
    }

    /**
     * get count of docDocumentCategoryType
     * @param documentTypeId
     * @param companyId
     * @return - count of docDocumentCategoryType
     */
    private int countDocDocumentCategoryType(long systemEntityId, long companyId) {
        Map queryParams = [systemEntityId: systemEntityId, companyId: companyId]
        List results = executeSelectSql(COUNT_DOC_DOCUMENT_CATEGORY_TYPE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_DOC_DOCUMENT_CATEGORY_TYPE = """
        SELECT COUNT(id) AS count
        FROM doc_document
        WHERE document_category_type_id = :systemEntityId
        AND company_id = :companyId
    """

    private static final String COUNT_DPL_OFFLINE_DATA_FEED_FILE = """
            SELECT COUNT(id) AS count
            FROM dpl_offline_data_feed_file
            WHERE status_id = :systemEntityId
            AND company_id = :companyId
    """

    /**
     * get count of DplOfflineDataFeedFile
     * @param systemEntityId
     * @param companyId
     * @return - count of DplOfflineDataFeedFile
     */
    private int countDplOfflineDataFeedFile(long systemEntityId, long companyId) {
        Map queryParams = [systemEntityId: systemEntityId, companyId: companyId]
        List results = executeSelectSql(COUNT_DPL_OFFLINE_DATA_FEED_FILE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_DPL_DATA_EXPORT = """
            SELECT COUNT(id) AS count
            FROM dpl_data_export
            WHERE data_feed_type_id = :systemEntityId
            AND company_id = :companyId
    """

    /**
     * get count of DplDataExport
     * @param systemEntityId
     * @param companyId
     * @return - count of DplDataExport
     */
    private int countDplDataExport(long systemEntityId, long companyId) {
        Map queryParams = [systemEntityId: systemEntityId, companyId: companyId]
        List results = executeSelectSql(COUNT_DPL_DATA_EXPORT, queryParams)
        int count = results[0].count
        return count
    }


    private static final String COUNT_CHANGE_REQUEST_FOR_TYPE = """
            SELECT COUNT(id) AS count
            FROM pt_change_request
            WHERE type = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of PtChangeRequest by type
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of PtChangeRequest
     */
    private int countChangeRequestForType(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_CHANGE_REQUEST_FOR_TYPE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_DB_INSTANCE_QUERY = """
            SELECT COUNT(id) AS count
            FROM db_instance_query
            WHERE query_type_id = :systemEntityId AND
                  company_id = :companyId
    """
    /**
     * get count of query by type
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of query
     */
    private int countDbInstanceQuery(long systemEntityId, long companyId) {
        Map queryParams = [
                systemEntityId: systemEntityId,
                companyId     : companyId
        ]
        List results = executeSelectSql(COUNT_DB_INSTANCE_QUERY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_DB_INSTANCE_TYPE = """
            SELECT COUNT(id) AS count
            FROM app_db_instance
            WHERE type_id = :systemEntityId
            AND company_id = :companyId
    """

    /**
     * get count of db instance by type
     * @param systemEntityId - SystemEntity.id
     * @param companyId - Company.id
     * @return - count of query
     */
    private int countDbInstanceType(long systemEntityId, long companyId) {
        Map queryParams = [systemEntityId: systemEntityId, companyId: companyId]
        List results = executeSelectSql(COUNT_DB_INSTANCE_TYPE, queryParams)
        int count = results[0].count
        return count
    }


    private static final String COUNT_EL_COURSE = """
            SELECT COUNT(id) AS count FROM el_course
            WHERE language_type_id = :systemEntityId
            AND company_id = :companyId
    """

    /**
     * get count of ElCourse
     * @param systemEntityId
     * @param companyId
     * @return - count of ElCourse
     */
    private int countElCourse(long systemEntityId, long companyId) {
        Map queryParams = [systemEntityId: systemEntityId, companyId: companyId]
        List results = executeSelectSql(COUNT_EL_COURSE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EL_ASSIGNMENT = """
            SELECT COUNT(id) AS count FROM el_assignment
            WHERE assignment_type_id = :systemEntityId
            AND company_id = :companyId
    """

    /**
     * get count of ElAssignment
     * @param systemEntityId
     * @param companyId
     * @return - count of ElAssignment
     */
    private int countElAssignment(long systemEntityId, long companyId) {
        Map queryParams = [systemEntityId: systemEntityId, companyId: companyId]
        List results = executeSelectSql(COUNT_EL_ASSIGNMENT, queryParams)
        int count = results[0].count
        return count
    }
}
