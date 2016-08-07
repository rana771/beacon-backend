package com.athena.mis.application.service

import com.athena.mis.BaseDomainService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.integration.accounting.AccPluginConnector
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.budget.BudgPluginConnector
import com.athena.mis.integration.datapipeline.DataPipeLinePluginConnector
import com.athena.mis.integration.document.DocumentPluginConnector
import com.athena.mis.integration.elearning.ELearningPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.integration.inventory.InvPluginConnector
import com.athena.mis.integration.procurement.ProcPluginConnector
import com.athena.mis.integration.projecttrack.PtPluginConnector
import com.athena.mis.integration.sarb.SarbPluginConnector
import org.apache.log4j.Logger
import org.springframework.transaction.annotation.Transactional

/**
 * SystemEntityService is used to handle only CRUD related object manipulation
 * (e.g. read, create, list, update etc.)
 */
class SystemEntityService extends BaseDomainService {

    private Logger log = Logger.getLogger(getClass())

    AppSystemEntityCacheService appSystemEntityCacheService

    private static final String SORT_BY_KEY = 'key'

    @Override
    public void init() {
        domainClass = SystemEntity.class
    }

    /**
     * Method to read SystemEntity by id
     * @param id - SystemEntity.id
     * @return - SystemEntity object
     */
    @Override
    public SystemEntity read(long id) {
        SystemEntity systemEntity = SystemEntity.read(id)
        systemEntity.discard()  // This domain checking dirty with .read(), discard() resolves it
        return systemEntity
    }

    /**
     * Method to get list system entity by type
     * @param type - system entity type id
     */
    @Transactional(readOnly = true)
    public List listByType(long type) {
        return SystemEntity.findAllByType(type, [readOnly: true, sort: SystemEntity.DEFAULT_SORT_FIELD, order: ASCENDING_SORT_ORDER])
    }

    @Transactional(readOnly = true)
    public List listByType(long type, String sortColumn) {
        return SystemEntity.findAllByType(type, [readOnly: true, sort: sortColumn, order: ASCENDING_SORT_ORDER])
    }

    private static
    final String GENERATE_SE_ID_SEQUENCE_SQL = "SELECT (:companyId || LPAD(NEXTVAL('system_entity_id_seq')::text,9,'0'))::bigint as id ;"

    /**
     * get customized id as : companyId + latest system_entity_id from system_entity_id_sequence
     * 1. sequence must be 9 digits.
     * e.g : 1000000003(1 = companyId, 000000003 = latest id of system_entity_id_sequence)
     * @param companyId -id of company
     * @return -long value of id
     */
    public long getSystemEntityId(long companyId) {
        Map params = [companyId: companyId]
        List results = executeSelectSql(GENERATE_SE_ID_SEQUENCE_SQL, params)
        long systemEntityId = results[0].id
        return systemEntityId
    }

    public int countByTypeAndKeyIlikeAndCompanyId(long type, String key, long companyId) {
        int count = SystemEntity.countByTypeAndKeyIlikeAndCompanyId(type, key, companyId)
        return count
    }

    public int countByTypeAndKeyIlikeAndIdNotEqualAndCompanyId(long type, String key, long id, long companyId) {
        int count = SystemEntity.countByTypeAndKeyIlikeAndIdNotEqualAndCompanyId(type, key, id, companyId)
        return count
    }

    public int countByType(long type) {
        int count = SystemEntity.countByType(type)
        return count
    }

    public int countByTypeAndCompanyId(long type, long companyId) {
        int count = SystemEntity.countByTypeAndCompanyId(type, companyId)
        return count
    }

    public SystemEntity findByReservedIdAndCompanyId(long userEntityType, long companyId) {
        SystemEntity systemEntity = SystemEntity.findByReservedIdAndCompanyId(userEntityType, companyId, [readOnly: true])
        return systemEntity
    }

    public List<SystemEntity> findAllByReservedIdInListAndCompanyId(List<Long> lstReservedIds, long companyId) {
        List<SystemEntity> lstSystemEntity = SystemEntity.findAllByReservedIdInListAndCompanyId(lstReservedIds, companyId, [readOnly: true])
        return lstSystemEntity
    }

    public SystemEntity findByKeyAndCompanyId(String key, long companyId) {
        return SystemEntity.findByKeyAndCompanyId(key, companyId, [readOnly: true])
    }

    public List<SystemEntity> findAllByPluginId(long pluginId) {
        return SystemEntity.findAllByPluginId(pluginId, [readOnly: true])
    }

    public List<SystemEntity> findAllByType(long typeId) {
        return SystemEntity.findAllByType(typeId, [readOnly: true])
    }

    private static final String DELETE_ALL = """
        DELETE FROM system_entity WHERE company_id = :companyId
    """

    /**
     * Delete all systemEntity by companyId
     * @param companyId - id of company
     */
    public void deleteAllByCompanyId(long companyId) {
        Map queryParams = [
                companyId: companyId
        ]
        executeUpdateSql(DELETE_ALL, queryParams)
    }

    /**
     * Method to create default budget task status
     */
    private void createDefaultBudgetTaskStatus(long companyId, long sysUserId, def budgSystemEntityCacheService) {
        long pluginId = BudgPluginConnector.PLUGIN_ID
        SystemEntity budgTaskStatusDefined = new SystemEntity(version: 0, key: 'Defined', value: 'Defined', type: budgSystemEntityCacheService.SYS_ENTITY_TYPE_BUDG_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 389L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        budgTaskStatusDefined.id = getSystemEntityId(companyId)
        create(budgTaskStatusDefined)
        SystemEntity budgTaskStatusInProgress = new SystemEntity(version: 0, key: 'In Progress', value: 'In Progress', type: budgSystemEntityCacheService.SYS_ENTITY_TYPE_BUDG_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 390L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        budgTaskStatusInProgress.id = getSystemEntityId(companyId)
        create(budgTaskStatusInProgress)
        SystemEntity budgTaskStatusCompleted = new SystemEntity(version: 0, key: 'Completed', value: 'Completed', type: budgSystemEntityCacheService.SYS_ENTITY_TYPE_BUDG_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 391L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        budgTaskStatusCompleted.id = getSystemEntityId(companyId)
        create(budgTaskStatusCompleted)
    }

    public boolean createDefaultForAccounting(long companyId, long sysUserId, def accSystemEntityCacheService) {
        try {
            createDefaultAccSource(companyId, sysUserId, accSystemEntityCacheService)
            createDefaultAccVoucherType(companyId, sysUserId, accSystemEntityCacheService)
            createDefaultAccInstrumentType(companyId, sysUserId, accSystemEntityCacheService)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default account source
     */
    private void createDefaultAccSource(long companyId, long sysUserId, def accSystemEntityCacheService) {
        long pluginId = AccPluginConnector.PLUGIN_ID
        SystemEntity systemEntityNone = new SystemEntity(version: 0, key: 'None', value: 'None', type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 21L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityNone.id = getSystemEntityId(companyId)
        create(systemEntityNone)
        SystemEntity systemEntityCustomer = new SystemEntity(version: 0, key: 'Customer', value: 'Customer', type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 22L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityCustomer.id = getSystemEntityId(companyId)
        create(systemEntityCustomer)
        SystemEntity systemEntityEmployee = new SystemEntity(version: 0, key: 'Employee', value: 'Employee', type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 23L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityEmployee.id = getSystemEntityId(companyId)
        create(systemEntityEmployee)
        SystemEntity systemEntityCashSubAccount = new SystemEntity(version: 0, key: 'Sub-Account', value: 'Sub-Account', type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 24L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityCashSubAccount.id = getSystemEntityId(companyId)
        create(systemEntityCashSubAccount)
        SystemEntity systemEntitySupplier = new SystemEntity(version: 0, key: 'Supplier', value: 'Supplier', type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 25L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntitySupplier.id = getSystemEntityId(companyId)
        create(systemEntitySupplier)
        SystemEntity systemEntityItem = new SystemEntity(version: 0, key: 'Item', value: 'Item', type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 26L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityItem.id = getSystemEntityId(companyId)
        create(systemEntityItem)
        SystemEntity systemEntityLc = new SystemEntity(version: 0, key: 'LC', value: 'LC', type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 27L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityLc.id = getSystemEntityId(companyId)
        create(systemEntityLc)
        SystemEntity systemEntityIpc = new SystemEntity(version: 0, key: 'IPC', value: 'IPC', type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 28L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityIpc.id = getSystemEntityId(companyId)
        create(systemEntityIpc)
        SystemEntity systemEntityLeaseAcc = new SystemEntity(version: 0, key: 'Lease Account', value: 'Lease Account', type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_SOURCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 29L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityLeaseAcc.id = getSystemEntityId(companyId)
        create(systemEntityLeaseAcc)
    }

    /**
     * Method to create default Account Instrument type
     */
    private void createDefaultAccInstrumentType(long companyId, long sysUserId, def accSystemEntityCacheService) {
        long pluginId = AccPluginConnector.PLUGIN_ID
        SystemEntity systemEntityIouTrace = new SystemEntity(version: 0, key: 'IOU Trace', value: null, type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_INSTRUMENT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 234L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityIouTrace.id = getSystemEntityId(companyId)
        create(systemEntityIouTrace)
        SystemEntity systemEntityPoTrace = new SystemEntity(version: 0, key: 'PO Trace', value: null, type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_INSTRUMENT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 235L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityPoTrace.id = getSystemEntityId(companyId)
        create(systemEntityPoTrace)
    }

    /**
     * Method to create default Account Voucher type
     */
    private void createDefaultAccVoucherType(long companyId, long sysUserId, def accSystemEntityCacheService) {
        long pluginId = AccPluginConnector.PLUGIN_ID
        SystemEntity systemEntityPaymentVoucherBank = new SystemEntity(version: 0, key: 'Payment Voucher-Bank', value: 'PB', type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_VOUCHER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 210L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityPaymentVoucherBank.id = getSystemEntityId(companyId)
        create(systemEntityPaymentVoucherBank)
        SystemEntity systemEntityPaymentVoucherCash = new SystemEntity(version: 0, key: 'Payment Voucher-Cash', value: 'PC', type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_VOUCHER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 211L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityPaymentVoucherCash.id = getSystemEntityId(companyId)
        create(systemEntityPaymentVoucherCash)
        SystemEntity systemEntityReceivedVoucherBank = new SystemEntity(version: 0, key: 'Received Voucher-Bank', value: 'RB', type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_VOUCHER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 212L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityReceivedVoucherBank.id = getSystemEntityId(companyId)
        create(systemEntityReceivedVoucherBank)
        SystemEntity systemEntityReceivedVoucherCash = new SystemEntity(version: 0, key: 'Received Voucher-Cash', value: 'RC', type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_VOUCHER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 213L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityReceivedVoucherCash.id = getSystemEntityId(companyId)
        create(systemEntityReceivedVoucherCash)
        SystemEntity systemEntityJournal = new SystemEntity(version: 0, key: 'Journal', value: 'JR', type: accSystemEntityCacheService.SYS_ENTITY_TYPE_ACC_VOUCHER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 214L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemEntityJournal.id = getSystemEntityId(companyId)
        create(systemEntityJournal)
    }

    /**
     * Method to create default inventory production item type
     */

    public boolean createDefaultForInventory(long companyId, long sysUserId, def invSystemEntityCacheService) {
        try {
            createDefaultInvProductionItemType(companyId, sysUserId, invSystemEntityCacheService)
            createDefaultTransactionType(companyId, sysUserId, invSystemEntityCacheService)
            createDefaultTransactionEntityType(companyId, sysUserId, invSystemEntityCacheService)
            createDefaultInventoryType(companyId, sysUserId, invSystemEntityCacheService)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private void createDefaultInvProductionItemType(long companyId, long sysUserId, def invSystemEntityCacheService) {
        long pluginId = InvPluginConnector.PLUGIN_ID
        SystemEntity invProductionItemTypeRawMaterial = new SystemEntity(version: 0, key: 'Raw material', value: null, type: invSystemEntityCacheService.SYS_ENTITY_TYPE_INV_PRODUCTION_LINE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 415L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        invProductionItemTypeRawMaterial.id = getSystemEntityId(companyId)
        create(invProductionItemTypeRawMaterial)
        SystemEntity invProductionItemTypeFinishedProduct = new SystemEntity(version: 0, key: 'Finished Product', value: null, type: invSystemEntityCacheService.SYS_ENTITY_TYPE_INV_PRODUCTION_LINE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 416L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        invProductionItemTypeFinishedProduct.id = getSystemEntityId(companyId)
        create(invProductionItemTypeFinishedProduct)
    }

    /**
     * Method to create default transaction type
     */
    private void createDefaultTransactionType(long companyId, long sysUserId, def invSystemEntityCacheService) {

        long pluginId = InvPluginConnector.PLUGIN_ID
        SystemEntity transactionTypeIn = new SystemEntity(version: 0, key: 'In', value: null, type: invSystemEntityCacheService.SYS_ENTITY_TYPE_INV_TRANSACTION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 417L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        transactionTypeIn.id = getSystemEntityId(companyId)
        create(transactionTypeIn)
        SystemEntity transactionTypeOut = new SystemEntity(version: 0, key: 'Out', value: null, type: invSystemEntityCacheService.SYS_ENTITY_TYPE_INV_TRANSACTION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 418L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        transactionTypeOut.id = getSystemEntityId(companyId)
        create(transactionTypeOut)
        SystemEntity transactionTypeConsumption = new SystemEntity(version: 0, key: 'Consumption', value: null, type: invSystemEntityCacheService.SYS_ENTITY_TYPE_INV_TRANSACTION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 419L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        transactionTypeConsumption.id = getSystemEntityId(companyId)
        create(transactionTypeConsumption)
        SystemEntity transactionTypeProduction = new SystemEntity(version: 0, key: 'Production', value: null, type: invSystemEntityCacheService.SYS_ENTITY_TYPE_INV_TRANSACTION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 420L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        transactionTypeProduction.id = getSystemEntityId(companyId)
        create(transactionTypeProduction)
        SystemEntity transactionTypeAdjustment = new SystemEntity(version: 0, key: 'Adjustment', value: null, type: invSystemEntityCacheService.SYS_ENTITY_TYPE_INV_TRANSACTION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 421L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        transactionTypeAdjustment.id = getSystemEntityId(companyId)
        create(transactionTypeAdjustment)
        SystemEntity transactionTypeReverseAdjustment = new SystemEntity(version: 0, key: 'Reverse Adjustment', value: null, type: invSystemEntityCacheService.SYS_ENTITY_TYPE_INV_TRANSACTION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 422L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        transactionTypeReverseAdjustment.id = getSystemEntityId(companyId)
        create(transactionTypeReverseAdjustment)
    }

    /**
     * Method to create default transaction entity type
     */
    private void createDefaultTransactionEntityType(long companyId, long sysUserId, def invSystemEntityCacheService) {

        long pluginId = InvPluginConnector.PLUGIN_ID
        SystemEntity transactionEntityTypeStore = new SystemEntity(version: 0, key: 'Inventory', value: null, type: invSystemEntityCacheService.SYS_ENTITY_TYPE_INV_TRANSACTION_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 423L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        transactionEntityTypeStore.id = getSystemEntityId(companyId)
        create(transactionEntityTypeStore)
        SystemEntity transactionEntityTypeSupplier = new SystemEntity(version: 0, key: 'Supplier', value: null, type: invSystemEntityCacheService.SYS_ENTITY_TYPE_INV_TRANSACTION_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 424L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        transactionEntityTypeSupplier.id = getSystemEntityId(companyId)
        create(transactionEntityTypeSupplier)
        SystemEntity transactionEntityTypeNone = new SystemEntity(version: 0, key: 'None', value: null, type: invSystemEntityCacheService.SYS_ENTITY_TYPE_INV_TRANSACTION_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 425L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        transactionEntityTypeNone.id = getSystemEntityId(companyId)
        create(transactionEntityTypeNone)
        SystemEntity transactionEntityTypeCustomer = new SystemEntity(version: 0, key: 'Customer', value: null, type: invSystemEntityCacheService.SYS_ENTITY_TYPE_INV_TRANSACTION_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 426L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        transactionEntityTypeCustomer.id = getSystemEntityId(companyId)
        create(transactionEntityTypeCustomer)
    }

    /**
     * Method to create default valuation type for application
     */
    private void createDefaultValuationTypeForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity valuationTypeFIFO = new SystemEntity(version: 0, key: 'FIFO', value: 'Valuation type FIFO', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_VALUATION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 127L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        valuationTypeFIFO.id = getSystemEntityId(companyId)
        create(valuationTypeFIFO)
        SystemEntity valuationTypeLIFO = new SystemEntity(version: 0, key: 'LIFO', value: 'Valuation type LIFO', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_VALUATION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 128L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        valuationTypeLIFO.id = getSystemEntityId(companyId)
        create(valuationTypeLIFO)
        SystemEntity valuationTypeAVG = new SystemEntity(version: 0, key: 'AVG', value: 'Valuation type AVG', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_VALUATION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 129L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        valuationTypeAVG.id = getSystemEntityId(companyId)
        create(valuationTypeAVG)
    }

    /**
     * Method to create default valuation type for application
     */
    private void createDefaultHierarchyTypeForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity valuationTypeFIFO = new SystemEntity(version: 0, key: 'Root', value: 'Root', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_HIERARCHY_LEVEL, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000346L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        valuationTypeFIFO.id = getSystemEntityId(companyId)
        create(valuationTypeFIFO)
        SystemEntity valuationTypeLIFO = new SystemEntity(version: 0, key: 'Selected node', value: 'Selected node', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_HIERARCHY_LEVEL, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000347L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        valuationTypeLIFO.id = getSystemEntityId(companyId)
        create(valuationTypeLIFO)
    }

    private void createDefaultPageTypeForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity pageTypeBlog = new SystemEntity(version: 0, key: 'Blog', value: 'Blog', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_PAGE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000355L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        pageTypeBlog.id = getSystemEntityId(companyId)
        create(pageTypeBlog)

        SystemEntity pageTypePost = new SystemEntity(version: 0, key: 'Post', value: 'Post', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_PAGE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000361L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        pageTypePost.id = getSystemEntityId(companyId)
        create(pageTypePost)
    }

    /**
     * Method to create default payment method
     */
    public boolean createDefaultPaymentMethod(long companyId, long userId) {
        try {
            long pluginId = ProcPluginConnector.PLUGIN_ID
            SystemEntity systemEntityCash = new SystemEntity(version: 0, key: 'Cash', value: 'Payment through Cash', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_PAYMENT_METHOD, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            systemEntityCash.id = getSystemEntityId(companyId)
            create(systemEntityCash)
            SystemEntity systemEntityCheque = new SystemEntity(version: 0, key: 'Cheque', value: 'Payment through Cheque', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_PAYMENT_METHOD, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            systemEntityCheque.id = getSystemEntityId(companyId)
            create(systemEntityCheque)
            SystemEntity systemEntityLc = new SystemEntity(version: 0, key: 'LC', value: 'Payment through LC', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_PAYMENT_METHOD, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            systemEntityLc.id = getSystemEntityId(companyId)
            create(systemEntityLc)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    /**
     * Method to create default unit for application
     */
    private boolean createDefaultUnitForApp(long companyId, long userId) {
        try {
            long pluginId = PluginConnector.PLUGIN_ID
            SystemEntity unit1 = new SystemEntity(version: 0, key: 'cu.cm.', value: 'cu.cm.', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit1.id = getSystemEntityId(companyId)
            create(unit1)
            SystemEntity unit2 = new SystemEntity(version: 0, key: 'Foot', value: 'FT', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit2.id = getSystemEntityId(companyId)
            create(unit2)
            SystemEntity unit3 = new SystemEntity(version: 0, key: 'Bags', value: 'Bags', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit3.id = getSystemEntityId(companyId)
            create(unit3)
            SystemEntity unit4 = new SystemEntity(version: 0, key: 'NOS', value: 'NOS', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit4.id = getSystemEntityId(companyId)
            create(unit4)
            SystemEntity unit5 = new SystemEntity(version: 0, key: 'Liters', value: 'liters', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit5.id = getSystemEntityId(companyId)
            create(unit5)
            SystemEntity unit6 = new SystemEntity(version: 0, key: 'METER', value: 'METER', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit6.id = getSystemEntityId(companyId)
            create(unit6)
            SystemEntity unit7 = new SystemEntity(version: 0, key: 'Months', value: 'months', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit7.id = getSystemEntityId(companyId)
            create(unit7)
            SystemEntity unit8 = new SystemEntity(version: 0, key: 'P.S', value: 'P.S', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit8.id = getSystemEntityId(companyId)
            create(unit8)
            SystemEntity unit9 = new SystemEntity(version: 0, key: 'KG', value: 'kg', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit9.id = getSystemEntityId(companyId)
            create(unit9)
            SystemEntity unit10 = new SystemEntity(version: 0, key: 'CU. M.', value: 'CU. M.', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit10.id = getSystemEntityId(companyId)
            create(unit10)
            SystemEntity unit11 = new SystemEntity(version: 0, key: 'L.S.', value: 'L.S.', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit11.id = getSystemEntityId(companyId)
            create(unit11)
            SystemEntity unit12 = new SystemEntity(version: 0, key: 'SQM', value: 'SQM', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit12.id = getSystemEntityId(companyId)
            create(unit12)
            SystemEntity unit13 = new SystemEntity(version: 0, key: 'Set', value: 'Set', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit13.id = getSystemEntityId(companyId)
            create(unit13)
            SystemEntity unit14 = new SystemEntity(version: 0, key: 'Tonnes', value: 'Tonnes', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit14.id = getSystemEntityId(companyId)
            create(unit14)
            SystemEntity unit15 = new SystemEntity(version: 0, key: 'CFT', value: 'CFT', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_UNIT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            unit15.id = getSystemEntityId(companyId)
            create(unit15)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    private boolean createDefaultMimiTypeForApp(long companyId, long userId) {
        try {
            long pluginId = PluginConnector.PLUGIN_ID
            SystemEntity pdf = new SystemEntity(version: 0, key: 'PDF', value: 'application/pdf', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000323L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            pdf.id = getSystemEntityId(companyId)
            create(pdf)

            SystemEntity text = new SystemEntity(version: 0, key: 'TXT', value: 'text/plain', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000324L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            text.id = getSystemEntityId(companyId)
            create(text)

            SystemEntity doc = new SystemEntity(version: 0, key: 'DOC', value: 'application/msword', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000325L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            doc.id = getSystemEntityId(companyId)
            create(doc)

            SystemEntity docx = new SystemEntity(version: 0, key: 'DOCX', value: 'application/msword', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000326L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            docx.id = getSystemEntityId(companyId)
            create(docx)

            SystemEntity xls = new SystemEntity(version: 0, key: 'XLS', value: 'application/vnd.ms-excel', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000327L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            xls.id = getSystemEntityId(companyId)
            create(xls)

            SystemEntity xlsx = new SystemEntity(version: 0, key: 'XLSX', value: 'application/vnd.ms-excel', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000328L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            xlsx.id = getSystemEntityId(companyId)
            create(xlsx)

            SystemEntity ppt = new SystemEntity(version: 0, key: 'PPT', value: 'application/vnd.ms-powerpoint', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000329L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            ppt.id = getSystemEntityId(companyId)
            create(ppt)

            SystemEntity pptx = new SystemEntity(version: 0, key: 'PPTX', value: 'application/vnd.ms-powerpoint', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000330L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            pptx.id = getSystemEntityId(companyId)
            create(pptx)

            SystemEntity csv = new SystemEntity(version: 0, key: 'CSV', value: 'text/csv', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000331L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            csv.id = getSystemEntityId(companyId)
            create(csv)

            SystemEntity jpg = new SystemEntity(version: 0, key: 'JPG', value: 'image/jpeg', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000332L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            jpg.id = getSystemEntityId(companyId)
            create(jpg)

            SystemEntity jpej = new SystemEntity(version: 0, key: 'JPEG', value: 'image/jpeg', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000333L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            jpej.id = getSystemEntityId(companyId)
            create(jpej)

            SystemEntity png = new SystemEntity(version: 0, key: 'PNG', value: 'image/png', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000334L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            png.id = getSystemEntityId(companyId)
            create(png)

            SystemEntity gif = new SystemEntity(version: 0, key: 'GIF', value: 'image/gif', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000335L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            gif.id = getSystemEntityId(companyId)
            create(gif)

            SystemEntity mp3 = new SystemEntity(version: 0, key: 'MP3', value: 'audio/mpeg', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000336L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            mp3.id = getSystemEntityId(companyId)
            create(mp3)

            SystemEntity mp4 = new SystemEntity(version: 0, key: 'MP4', value: 'video/mp4', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000337L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            mp4.id = getSystemEntityId(companyId)
            create(mp4)

            SystemEntity bmp = new SystemEntity(version: 0, key: 'BPM', value: 'image/bmp', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MIME_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000338L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            bmp.id = getSystemEntityId(companyId)
            create(bmp)

            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    /**
     * Method to create default owner type for application
     */
    private void createDefaultOwnerTypeForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity ownerType1 = new SystemEntity(version: 0, key: 'Purchased', value: 'Object has been Purchased', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_OWNER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 132L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        ownerType1.id = getSystemEntityId(companyId)
        create(ownerType1)
        SystemEntity ownerType2 = new SystemEntity(version: 0, key: 'Rental', value: 'Object has been Rental', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_OWNER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 133L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        ownerType2.id = getSystemEntityId(companyId)
        create(ownerType2)
    }

    /**
     * Method to create default content entity type for application
     */
    private void createDefaultContentEntityTypeForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity entityTypeAppUser = new SystemEntity(version: 0, key: 'User', value: 'Attachment for Application User', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 142L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        entityTypeAppUser.id = getSystemEntityId(companyId)
        create(entityTypeAppUser)

        SystemEntity userDocument = new SystemEntity(version: 0, key: 'User Document', value: 'Attachment for User Document', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000363L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        userDocument.id = getSystemEntityId(companyId)
        create(userDocument)

        SystemEntity entityTypeCompany = new SystemEntity(version: 0, key: 'Company', value: 'Attachment for Company', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 143L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        entityTypeCompany.id = getSystemEntityId(companyId)
        create(entityTypeCompany)
        SystemEntity entityTypeExhCustomer = new SystemEntity(version: 0, key: 'Customer', value: 'Attachment for Exh Customer', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 144L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        entityTypeExhCustomer.id = getSystemEntityId(companyId)
        create(entityTypeExhCustomer)
        SystemEntity entityTypeProject = new SystemEntity(version: 0, key: 'Project', value: 'Attachment for Project', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 145L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        entityTypeProject.id = getSystemEntityId(companyId)
        create(entityTypeProject)
        SystemEntity entityTypePtBacklog = new SystemEntity(version: 0, key: 'Backlog', value: 'Attachment for Project Track Backlog', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000158L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        entityTypePtBacklog.id = getSystemEntityId(companyId)
        create(entityTypePtBacklog)
        SystemEntity entityTypeBudget = new SystemEntity(version: 0, key: 'BOQ Line Item', value: 'Attachment for BOQ Line Item', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 184L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        entityTypeBudget.id = getSystemEntityId(companyId)
        create(entityTypeBudget)
        SystemEntity entityTypeBudgetSprint = new SystemEntity(version: 0, key: 'Sprint', value: 'Attachment for Project Track Sprint', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 192L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        entityTypeBudgetSprint.id = getSystemEntityId(companyId)
        create(entityTypeBudgetSprint)
        SystemEntity entityTypeFinancialYear = new SystemEntity(version: 0, key: 'Financial Year', value: 'Attachment for Accounting Financial Year', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 185L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        entityTypeFinancialYear.id = getSystemEntityId(companyId)
        create(entityTypeFinancialYear)
        SystemEntity entityTypeMail = new SystemEntity(version: 0, key: 'Mail', value: 'Attachment for mail', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000322L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        entityTypeMail.id = getSystemEntityId(companyId)
        create(entityTypeMail)

        SystemEntity entityTypeCourse = new SystemEntity(version: 0, key: 'Course', value: 'Attachment for Course', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000344L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        entityTypeCourse.id = getSystemEntityId(companyId)
        create(entityTypeCourse)

        SystemEntity entityTypeLesson = new SystemEntity(version: 0, key: 'Lesson', value: 'Attachment for Lesson', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000345L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        entityTypeLesson.id = getSystemEntityId(companyId)
        create(entityTypeLesson)

        SystemEntity entityTypeAssignment = new SystemEntity(version: 0, key: 'El Assignment', value: 'Attachment for Assignment', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000354L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        entityTypeAssignment.id = getSystemEntityId(companyId)
        create(entityTypeAssignment)
    }

    /**
     * Method to create default note entity type for application
     */
    private void createDefaultNoteEntityTypeForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity noteTask = new SystemEntity(version: 0, key: 'Task', value: 'Note for Project Track Task', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 148L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        noteTask.id = getSystemEntityId(companyId)
        create(noteTask)
        SystemEntity noteCustomer = new SystemEntity(version: 0, key: 'Customer', value: 'Note for Exh Customer', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 149L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        noteCustomer.id = getSystemEntityId(companyId)
        create(noteCustomer)
        SystemEntity query = new SystemEntity(version: 0, key: 'Query', value: 'Note for Query', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000183L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        query.id = getSystemEntityId(companyId)
        create(query)
        SystemEntity dbInstanceQuery = new SystemEntity(version: 0, key: 'Script', value: 'Note for Script', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000185L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        dbInstanceQuery.id = getSystemEntityId(companyId)
        create(dbInstanceQuery)

        SystemEntity blog = new SystemEntity(version: 0, key: 'Blog', value: 'Note for Blog', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000356L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        blog.id = getSystemEntityId(companyId)
        create(blog)

        SystemEntity notePost = new SystemEntity(version: 0, key: 'Post', value: 'Note for Post', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000362L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        notePost.id = getSystemEntityId(companyId)
        create(notePost)
    }


    /**
     * Method to create default faq type for application
     */
    private void createDefaultFaqEntityTypeForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity faqEntity = new SystemEntity(version: 0, key: 'Doc Document', value: 'Doc Document', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_FAQ, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: appSystemEntityCacheService.SYS_ENTITY_FAQ_DOC_DOCUMENT, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        faqEntity.id = getSystemEntityId(companyId)
        create(faqEntity)

        SystemEntity docSubCategory = new SystemEntity(version: 0, key: 'Doc Sub Category', value: 'Doc Sub Category', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_FAQ, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: appSystemEntityCacheService.SYS_ENTITY_FAQ_DOC_SUB_CATEGORY, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        docSubCategory.id = getSystemEntityId(companyId)
        create(docSubCategory)
    }


    /**
     * Method to create default supplier entity type for application
     */
    private boolean createDefaultSupplierEntityTypeForApp(long companyId, long userId) {
        try {
            long pluginId = PluginConnector.PLUGIN_ID
            SystemEntity serviceProvider = new SystemEntity(version: 0, key: 'Material Provider', value: 'Material Provider', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_SUPPLIER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            serviceProvider.id = getSystemEntityId(companyId)
            create(serviceProvider)

            SystemEntity materialProvider = new SystemEntity(version: 0, key: 'Service Provider', value: 'Service Provider', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_SUPPLIER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            materialProvider.id = getSystemEntityId(companyId)
            create(materialProvider)

            SystemEntity materialAndServiceProvider = new SystemEntity(version: 0, key: 'Material & Service Provider', value: 'Material & Service Provider', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_SUPPLIER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 0L, createdOn: new Date(), createdBy: userId, updatedBy: 0)
            materialAndServiceProvider.id = getSystemEntityId(companyId)
            create(materialAndServiceProvider)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    /**
     * Method to create default item category for application
     */
    private void createDefaultItemCategoryForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity inventory = new SystemEntity(version: 0, key: 'Inventory', value: 'Inventory', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        inventory.id = getSystemEntityId(companyId)
        create(inventory)

        SystemEntity nonInventory = new SystemEntity(version: 0, key: 'Non Inventory', value: 'Non-Inventory', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 151L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        nonInventory.id = getSystemEntityId(companyId)
        create(nonInventory)

        SystemEntity fixedAsset = new SystemEntity(version: 0, key: 'Fixed Asset', value: 'Fixed Asset', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 152L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        fixedAsset.id = getSystemEntityId(companyId)
        create(fixedAsset)
    }

    /**
     * Method to create default content type for application
     */
    private void createDefaultContentTypeForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity contentTypeDocument = new SystemEntity(version: 0, key: 'Document', value: 'Document', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 146L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        contentTypeDocument.id = getSystemEntityId(companyId)
        create(contentTypeDocument)

        SystemEntity contentTypeImage = new SystemEntity(version: 0, key: 'Image', value: 'Image', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 147L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        contentTypeImage.id = getSystemEntityId(companyId)
        create(contentTypeImage)
    }

    /**
     * Method to create default query type for application
     */
    private void createDefaultQueryTypeForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity queryTypeDiagnostic = new SystemEntity(version: 0, key: 'Diagnostic', value: 'Query for diagnostic report', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_QUERY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000184L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        queryTypeDiagnostic.id = getSystemEntityId(companyId)
        create(queryTypeDiagnostic)

        SystemEntity queryTypeMaintenance = new SystemEntity(version: 0, key: 'Maintenance', value: 'Query for Maintenance SQL Scheduler', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_QUERY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000186L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        queryTypeMaintenance.id = getSystemEntityId(companyId)
        create(queryTypeMaintenance)
    }

    /**
     * Method to create default db instance type for application
     */
    private void createDefaultDbInstanceTypeForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity sourceDb = new SystemEntity(version: 0, key: 'Source DB', value: 'DB Instance Type for Source Database', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_DB_INSTANCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000187L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        sourceDb.id = getSystemEntityId(companyId)
        create(sourceDb)

        SystemEntity targetBd = new SystemEntity(version: 0, key: 'Target DB', value: 'DB Instance Type for Target Database', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_DB_INSTANCE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000188L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        targetBd.id = getSystemEntityId(companyId)
        create(targetBd)
    }

    /**
     * Method to create default inventory type
     */
    private void createDefaultInventoryType(long companyId, long sysUserId, def invSystemEntityCacheService) {

        long pluginId = InvPluginConnector.PLUGIN_ID
        SystemEntity invTypeStore = new SystemEntity(version: 0, key: 'STORE', value: null, type: invSystemEntityCacheService.SYS_ENTITY_TYPE_INVENTORY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 430L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        invTypeStore.id = getSystemEntityId(companyId)
        create(invTypeStore)
        SystemEntity invTypeSite = new SystemEntity(version: 0, key: 'SITE', value: null, type: invSystemEntityCacheService.SYS_ENTITY_TYPE_INVENTORY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 431L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        invTypeSite.id = getSystemEntityId(companyId)
        create(invTypeSite)
    }

    /**
     * Method to create default schedule type
     */
    private void createDefaultScheduleType(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity scheduleTypeSimple = new SystemEntity(version: 0, key: 'Simple', value: 'Schedules Job using Repeat Interval & Repeat Count', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_SCHEDULE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1148L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        scheduleTypeSimple.id = getSystemEntityId(companyId)
        create(scheduleTypeSimple)
        SystemEntity scheduleTypeCron = new SystemEntity(version: 0, key: 'Cron', value: "Schedules Job using Corn Expression", type: appSystemEntityCacheService.SYS_ENTITY_TYPE_SCHEDULE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1149L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        scheduleTypeCron.id = getSystemEntityId(companyId)
        create(scheduleTypeCron)
    }

    /**
     * Method to create db object type
     */
    private void createDefaultDbObjectType(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity dbObjectTable = new SystemEntity(version: 0, key: 'Table', value: 'Table', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_DB_OBJECT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000179L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        dbObjectTable.id = getSystemEntityId(companyId)
        create(dbObjectTable)
        SystemEntity dbObjectView = new SystemEntity(version: 0, key: 'View', value: 'View', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_DB_OBJECT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000180L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        dbObjectView.id = getSystemEntityId(companyId)
        create(dbObjectView)
    }

    /**
     * Method to create db object type
     */
    private void createDefaultScriptType(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity script = new SystemEntity(version: 0, key: 'Shell', value: 'Shell', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_SCRIPT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000181L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        script.id = getSystemEntityId(companyId)
        create(script)
        SystemEntity sql = new SystemEntity(version: 0, key: 'SQL', value: 'SQL', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_SCRIPT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000182L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        sql.id = getSystemEntityId(companyId)
        create(sql)
    }

    /**
     * Create default system entity for exchange house
     * @param companyId
     * @param systemUserId
     * @param exhSystemEntityCacheService
     * @return ( true / false ) depending on success
     */
    public boolean createDefaultDataForExh(long companyId, long systemUserId, def exhSystemEntityCacheService) {
        try {
            createDefaultDataPaidByForExh(companyId, systemUserId, exhSystemEntityCacheService)
            createDefaultDataPaymentMethodForExh(companyId, systemUserId, exhSystemEntityCacheService)
            createDefaultDataTaskStatusForExh(companyId, systemUserId, exhSystemEntityCacheService)
            createDefaultDataTaskTypeForExh(companyId, systemUserId, exhSystemEntityCacheService)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    /**
     * Method to create default data paid by for exchange house
     */
    private void createDefaultDataPaidByForExh(long companyId, long systemUserId, def exhSystemEntityCacheService) {
        long pluginId = ExchangeHousePluginConnector.PLUGIN_ID
        SystemEntity paidByCash = new SystemEntity(version: 0, key: 'Cash', value: 'Cash', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_PAID_BY, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 950)
        paidByCash.id = getSystemEntityId(companyId)
        create(paidByCash)

        SystemEntity paidByOnline = new SystemEntity(version: 0, key: 'Online Transfer', value: 'Online Transfer', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_PAID_BY, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 951)
        paidByOnline.id = getSystemEntityId(companyId)
        create(paidByOnline)

        SystemEntity paidByCardDebit = new SystemEntity(version: 0, key: 'Card Payment Debit', value: 'Card Payment Debit', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_PAID_BY, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 952)
        paidByCardDebit.id = getSystemEntityId(companyId)
        create(paidByCardDebit)

        SystemEntity paidByCardCredit = new SystemEntity(version: 0, key: 'Card Payment Credit', value: 'Card Payment Credit', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_PAID_BY, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 9121)
        paidByCardCredit.id = getSystemEntityId(companyId)
        create(paidByCardCredit)
    }

    /**
     * Method to create default data payment method for exchange house
     */
    private void createDefaultDataPaymentMethodForExh(long companyId, long systemUserId,
                                                      def exhSystemEntityCacheService) {
        long pluginId = ExchangeHousePluginConnector.PLUGIN_ID
        SystemEntity paymentMethodCash = new SystemEntity(version: 0, key: 'Bank Deposit', value: '1', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_PAYMENT_METHOD, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 953)
        paymentMethodCash.id = getSystemEntityId(companyId)
        create(paymentMethodCash)

        SystemEntity paymentMethodOnline = new SystemEntity(version: 0, key: 'Cash Collection', value: '2', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_PAYMENT_METHOD, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 954)
        paymentMethodOnline.id = getSystemEntityId(companyId)
        create(paymentMethodOnline)
    }

    /**
     * Method to create default data task status for exchange house
     */
    private void createDefaultDataTaskStatusForExh(long companyId, long systemUserId, def exhSystemEntityCacheService) {
        long pluginId = ExchangeHousePluginConnector.PLUGIN_ID
        SystemEntity taskStatus1 = new SystemEntity(version: 0, key: 'Cancelled', value: 'Cancelled', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 956)
        taskStatus1.id = getSystemEntityId(companyId)
        create(taskStatus1)

        SystemEntity taskStatusPending = new SystemEntity(version: 0, key: 'Pending Task', value: 'Pending Task', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 957)
        taskStatusPending.id = getSystemEntityId(companyId)
        create(taskStatusPending)

        SystemEntity taskStatus2 = new SystemEntity(version: 0, key: 'New Task', value: 'New Task', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 958)
        taskStatus2.id = getSystemEntityId(companyId)
        create(taskStatus2)

        SystemEntity taskStatus3 = new SystemEntity(version: 0, key: 'Sent to bank', value: 'Sent to bank', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 959)
        taskStatus3.id = getSystemEntityId(companyId)
        create(taskStatus3)

        SystemEntity taskStatus4 = new SystemEntity(version: 0, key: 'Sent to other bank', value: 'Sent to other bank', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 960)
        taskStatus4.id = getSystemEntityId(companyId)
        create(taskStatus4)

        SystemEntity taskStatus5 = new SystemEntity(version: 0, key: 'Paid', value: 'Paid', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 961)
        taskStatus5.id = getSystemEntityId(companyId)
        create(taskStatus5)

        SystemEntity taskStatus6 = new SystemEntity(version: 0, key: 'Unapproved Task', value: 'Unapproved Task', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, createdBy: systemUserId, pluginId: pluginId, createdOn: new Date(), reservedId: 962)
        taskStatus6.id = getSystemEntityId(companyId)
        create(taskStatus6)

        SystemEntity taskStatus7 = new SystemEntity(version: 0, key: 'Refund Task', value: 'Refund Task', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_TASK_STATUS, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 997)
        taskStatus7.id = getSystemEntityId(companyId)
        create(taskStatus7)
    }

    /**
     * Method to create default data task type for exchange house
     */
    private void createDefaultDataTaskTypeForExh(long companyId, long systemUserId, def exhSystemEntityCacheService) {

        long pluginId = ExchangeHousePluginConnector.PLUGIN_ID
        SystemEntity taskTypeExh = new SystemEntity(version: 0, key: 'Exh Task', value: 'Exh Task', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_TASK_TYPE, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 963)
        taskTypeExh.id = getSystemEntityId(companyId)
        create(taskTypeExh)

        SystemEntity taskTypeAgent = new SystemEntity(version: 0, key: 'Agent Task', value: 'Agent Task', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_TASK_TYPE, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 964)
        taskTypeAgent.id = getSystemEntityId(companyId)
        create(taskTypeAgent)

        SystemEntity taskTypeCustomer = new SystemEntity(version: 0, key: 'Customer Task', value: 'Customer Task', type: exhSystemEntityCacheService.SYS_ENTITY_TYPE_EXH_TASK_TYPE, isActive: true, companyId: companyId, createdBy: systemUserId, createdOn: new Date(), pluginId: pluginId, reservedId: 965)
        taskTypeCustomer.id = getSystemEntityId(companyId)
        create(taskTypeCustomer)
    }

    /**
     * Method to create default data user entity type for application
     */
    private void createDefaultDataUserEntityTypeForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity userEntityTypeCust = new SystemEntity(version: 0, key: 'Customer', value: 'User Customer Mapping', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 136L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        userEntityTypeCust.id = getSystemEntityId(companyId)
        create(userEntityTypeCust)

        SystemEntity userEntityTypeBankBr = new SystemEntity(version: 0, key: 'Bank Branch', value: 'User Bank Branch Mapping', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 137L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        userEntityTypeBankBr.id = getSystemEntityId(companyId)
        create(userEntityTypeBankBr)

        SystemEntity userEntityTypeProject = new SystemEntity(version: 0, key: 'Project', value: 'User Project Mapping', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 138L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        userEntityTypeProject.id = getSystemEntityId(companyId)
        create(userEntityTypeProject)

        SystemEntity userEntityTypePtProject = new SystemEntity(version: 0, key: 'PtProject', value: 'User PT Project Mapping', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1059L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        userEntityTypePtProject.id = getSystemEntityId(companyId)
        create(userEntityTypePtProject)

        SystemEntity userEntityTypeInventory = new SystemEntity(version: 0, key: 'Inventory', value: 'User Inventory Mapping', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 139L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        userEntityTypeInventory.id = getSystemEntityId(companyId)
        create(userEntityTypeInventory)

        SystemEntity userEntityTypeGroup = new SystemEntity(version: 0, key: 'Group', value: 'User Group Mapping', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        userEntityTypeGroup.id = getSystemEntityId(companyId)
        create(userEntityTypeGroup)

        SystemEntity userEntityTypeAgent = new SystemEntity(version: 0, key: 'Agent', value: 'User Agent Mapping', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 141L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        userEntityTypeAgent.id = getSystemEntityId(companyId)
        create(userEntityTypeAgent)
    }

    /**
     * Method to create default data user entity type for application
     */
    private void createDefaultDataGenderForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity male = new SystemEntity(version: 0, key: 'Male', value: 'M', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_GENDER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 176L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        male.id = getSystemEntityId(companyId)
        create(male)

        SystemEntity female = new SystemEntity(version: 0, key: 'Female', value: 'F', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_GENDER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 177L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        female.id = getSystemEntityId(companyId)
        create(female)
    }

    /**
     * Method to create default data app note system entity for application
     */
    private void createDefaultDataAppNoteTypeForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity none = new SystemEntity(version: 0, key: 'None', value: 'None', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1118L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        none.id = getSystemEntityId(companyId)
        create(none)

        SystemEntity systemNote = new SystemEntity(version: 0, key: 'System Note', value: 'System Note', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1119L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        systemNote.id = getSystemEntityId(companyId)
        create(systemNote)

        SystemEntity verifiedAndApproved = new SystemEntity(version: 0, key: 'Verified And Approved', value: 'Verified And Approved', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1120L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        verifiedAndApproved.id = getSystemEntityId(companyId)
        create(verifiedAndApproved)
    }

    /**
     * Method to create default data benchmark system entity for application
     */
    private void createDefaultDataBenchmarkTypeForApp(long companyId, long sysUserId) {

        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity small = new SystemEntity(version: 0, key: 'Small', value: 'Small', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_BENCHMARK, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1137L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        small.id = getSystemEntityId(companyId)
        create(small)

        SystemEntity medium = new SystemEntity(version: 0, key: 'Medium', value: 'Medium', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_BENCHMARK, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1138L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        medium.id = getSystemEntityId(companyId)
        create(medium)

        SystemEntity large = new SystemEntity(version: 0, key: 'Large', value: 'Large', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_BENCHMARK, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1139L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        large.id = getSystemEntityId(companyId)
        create(large)
    }

    /**
     * Method to create default transfer log system entity for application
     */
    private void createDefaultDataTransactionLogTypeForApp(long companyId, long sysUserId) {

        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity dataExport = new SystemEntity(version: 0, key: 'Data Export', value: 'Log for Data Export', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000151L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        dataExport.id = getSystemEntityId(companyId)
        create(dataExport)

        SystemEntity benchmark = new SystemEntity(version: 0, key: 'Benchmark', value: 'Log for Benchmark', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000152L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        benchmark.id = getSystemEntityId(companyId)
        create(benchmark)

        SystemEntity benchmarkStar = new SystemEntity(version: 0, key: 'Benchmark Star', value: 'Log for Benchmark Star', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000153L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        benchmarkStar.id = getSystemEntityId(companyId)
        create(benchmarkStar)

        SystemEntity dataImport = new SystemEntity(version: 0, key: 'Data Import', value: 'Log for Data Import', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_TRANSACTION_LOG, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000154L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        dataImport.id = getSystemEntityId(companyId)
        create(dataImport)
    }

    /**
     * Method to create default content entity type for Project Track
     */
    private void createDefaultContentEntityTypeForPT(long companyId) {
        try {
            long pluginId = PluginConnector.PLUGIN_ID
            SystemEntity userEntityTypePtProject = new SystemEntity(version: 0, key: 'User Mapping PtProject', value: 'User Mapping PtProject', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1059L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            userEntityTypePtProject.id = getSystemEntityId(companyId)
            create(userEntityTypePtProject)

            SystemEntity entityTypePtBug = new SystemEntity(version: 0, key: 'PT BUG', value: 'PT BUG', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1058L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            entityTypePtBug.id = getSystemEntityId(companyId)
            create(entityTypePtBug)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Create default system entity for pt
     * @param companyId
     * @param ptSystemEntityCacheService
     * @return
     */
    public boolean createDefaultDataForPt(long companyId, def ptSystemEntityCacheService) {
        try {
            createDefaultDataBacklogPriorityForPT(companyId, ptSystemEntityCacheService)
            createDefaultDataBacklogStatusForPT(companyId, ptSystemEntityCacheService)
            createDefaultDataAcceptanceCriteriaStatusForPT(companyId, ptSystemEntityCacheService)
            createDefaultDataBugSeverityForPT(companyId, ptSystemEntityCacheService)
            createDefaultDataBugStatusForPT(companyId, ptSystemEntityCacheService)
            createDefaultDataSprintStatusForPt(companyId, ptSystemEntityCacheService)
            createDefaultDataBugTypeForPT(companyId, ptSystemEntityCacheService)
            createDefaultDataAcceptanceCriteriaTypeForPT(companyId, ptSystemEntityCacheService)
            createDefaultBacklogSuggestionTypeForPT(companyId, ptSystemEntityCacheService)
            createDefaultSchemaRelationTypeForPT(companyId, ptSystemEntityCacheService)
            createDefaultDataTypeForPT(companyId, ptSystemEntityCacheService)
            createDefaultContentEntityTypeForPT(companyId)
            createDefaultNoteEntityTypeForPT(companyId)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    /**
     * Method to create default backlog priority type for ProjectTrack
     */
    private void createDefaultDataBacklogPriorityForPT(long companyId, def ptSystemEntityCacheService) {
        try {
            long pluginId = PtPluginConnector.PLUGIN_ID
            SystemEntity backlogPriorityHigh = new SystemEntity(version: 0, key: 'High', value: 'High', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BACKLOG_PRIORITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1032L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            backlogPriorityHigh.id = getSystemEntityId(companyId)
            create(backlogPriorityHigh)

            SystemEntity backlogPriorityMedium = new SystemEntity(version: 0, key: 'Medium', value: 'Medium', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BACKLOG_PRIORITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1033L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            backlogPriorityMedium.id = getSystemEntityId(companyId)
            create(backlogPriorityMedium)

            SystemEntity backlogPriorityLow = new SystemEntity(version: 0, key: 'Low', value: 'Low', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BACKLOG_PRIORITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1034L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            backlogPriorityLow.id = getSystemEntityId(companyId)
            create(backlogPriorityLow)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private void createDefaultDataBacklogStatusForPT(long companyId, def ptSystemEntityCacheService) {
        try {
            long pluginId = PtPluginConnector.PLUGIN_ID
            SystemEntity backlogStatusDefined = new SystemEntity(version: 0, key: 'Defined', value: 'Defined', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BACKLOG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1035L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            backlogStatusDefined.id = getSystemEntityId(companyId)
            create(backlogStatusDefined)

            SystemEntity backlogStatusInProgress = new SystemEntity(version: 0, key: 'In Progress', value: 'In Progress', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BACKLOG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1036L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            backlogStatusInProgress.id = getSystemEntityId(companyId)
            create(backlogStatusInProgress)

            SystemEntity backlogStatusCompleted = new SystemEntity(version: 0, key: 'Completed', value: 'Completed', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BACKLOG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1037L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            backlogStatusCompleted.id = getSystemEntityId(companyId)
            create(backlogStatusCompleted)

            SystemEntity backlogStatusAccepted = new SystemEntity(version: 0, key: 'Accepted', value: 'Accepted', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BACKLOG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1038L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            backlogStatusAccepted.id = getSystemEntityId(companyId)
            create(backlogStatusAccepted)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private void createDefaultDataSprintStatusForPt(long companyId, def ptSystemEntityCacheService) {
        try {
            long pluginId = PtPluginConnector.PLUGIN_ID
            SystemEntity sprintStatusDefined = new SystemEntity(version: 0, key: 'Defined', value: 'Defined', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_SPRINT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1039L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            sprintStatusDefined.id = getSystemEntityId(companyId)
            create(sprintStatusDefined)

            SystemEntity sprintStatusInProgress = new SystemEntity(version: 0, key: 'In Progress', value: 'In Progress', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_SPRINT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1040L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            sprintStatusInProgress.id = getSystemEntityId(companyId)
            create(sprintStatusInProgress)

            SystemEntity sprintStatusCompleted = new SystemEntity(version: 0, key: 'Completed', value: 'Completed', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_SPRINT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1041L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            sprintStatusCompleted.id = getSystemEntityId(companyId)
            create(sprintStatusCompleted)

            SystemEntity sprintStatusClosed = new SystemEntity(version: 0, key: 'Closed', value: 'Closed', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_SPRINT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1095L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            sprintStatusClosed.id = getSystemEntityId(companyId)
            create(sprintStatusClosed)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private void createDefaultDataAcceptanceCriteriaStatusForPT(long companyId, def ptSystemEntityCacheService) {
        try {
            long pluginId = PtPluginConnector.PLUGIN_ID
            SystemEntity acceptanceStatusDefined = new SystemEntity(version: 0, key: 'Defined', value: 'Defined', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_ACCEPTANCE_CRITERIA_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1042L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            acceptanceStatusDefined.id = getSystemEntityId(companyId)
            create(acceptanceStatusDefined)

            SystemEntity acceptanceStatusInProgress = new SystemEntity(version: 0, key: 'In Progress', value: 'In Progress', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_ACCEPTANCE_CRITERIA_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1043L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            acceptanceStatusInProgress.id = getSystemEntityId(companyId)
            create(acceptanceStatusInProgress)

            SystemEntity acceptanceStatusCompleted = new SystemEntity(version: 0, key: 'Completed', value: 'Completed', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_ACCEPTANCE_CRITERIA_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1044L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            acceptanceStatusCompleted.id = getSystemEntityId(companyId)
            create(acceptanceStatusCompleted)

            SystemEntity acceptanceStatusBlocked = new SystemEntity(version: 0, key: 'Blocked', value: 'Blocked', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_ACCEPTANCE_CRITERIA_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1045L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            acceptanceStatusBlocked.id = getSystemEntityId(companyId)
            create(acceptanceStatusBlocked)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private void createDefaultDataBugSeverityForPT(long companyId, def ptSystemEntityCacheService) {
        try {
            long pluginId = PtPluginConnector.PLUGIN_ID
            SystemEntity bugSeverityHigh = new SystemEntity(version: 0, key: 'High', value: 'High', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG_SEVERITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1046L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugSeverityHigh.id = getSystemEntityId(companyId)
            create(bugSeverityHigh)

            SystemEntity bugSeverityMedium = new SystemEntity(version: 0, key: 'Medium', value: 'Medium', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG_SEVERITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1047L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugSeverityMedium.id = getSystemEntityId(companyId)
            create(bugSeverityMedium)

            SystemEntity bugSeverityLow = new SystemEntity(version: 0, key: 'Low', value: 'Low', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG_SEVERITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1048L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugSeverityLow.id = getSystemEntityId(companyId)
            create(bugSeverityLow)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private void createDefaultDataBugStatusForPT(long companyId, def ptSystemEntityCacheService) {
        try {
            long pluginId = PtPluginConnector.PLUGIN_ID
            SystemEntity bugStatusSubmitted = new SystemEntity(version: 0, key: 'Submitted', value: 'Submitted', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1049L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugStatusSubmitted.id = getSystemEntityId(companyId)
            create(bugStatusSubmitted)

            SystemEntity bugStatusOpen = new SystemEntity(version: 0, key: 'Re-opened', value: 'Re-opened', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1050L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugStatusOpen.id = getSystemEntityId(companyId)
            create(bugStatusOpen)

            SystemEntity bugStatusFixed = new SystemEntity(version: 0, key: 'Fixed', value: 'Fixed', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1051L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugStatusFixed.id = getSystemEntityId(companyId)
            create(bugStatusFixed)

            SystemEntity bugStatusClosed = new SystemEntity(version: 0, key: 'Closed', value: 'Closed', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1052L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugStatusClosed.id = getSystemEntityId(companyId)
            create(bugStatusClosed)

            SystemEntity bugStatusNotADefect = new SystemEntity(version: 0, key: 'Not a Defect', value: 'Not a Defect', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000155L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugStatusNotADefect.id = getSystemEntityId(companyId)
            create(bugStatusNotADefect)

            SystemEntity bugStatusCanNotReproduce = new SystemEntity(version: 0, key: 'Could Not Reproduce', value: 'Could Not Reproduce', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000156L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugStatusCanNotReproduce.id = getSystemEntityId(companyId)
            create(bugStatusCanNotReproduce)

            SystemEntity bugStatusDuplicate = new SystemEntity(version: 0, key: 'Duplicate', value: 'Duplicate', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000157L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugStatusDuplicate.id = getSystemEntityId(companyId)
            create(bugStatusDuplicate)

            SystemEntity bugStatusInProgress = new SystemEntity(version: 0, key: 'In Progress', value: 'In Progress', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 100000178L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugStatusInProgress.id = getSystemEntityId(companyId)
            create(bugStatusInProgress)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private void createDefaultDataBugTypeForPT(long companyId, def ptSystemEntityCacheService) {
        try {
            long pluginId = PtPluginConnector.PLUGIN_ID
            SystemEntity bugTypeFunctional = new SystemEntity(version: 0, key: 'Functional', value: 'Functional', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1053L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugTypeFunctional.id = getSystemEntityId(companyId)
            create(bugTypeFunctional)

            SystemEntity bugTypeUserInterface = new SystemEntity(version: 0, key: 'User Interface', value: 'User Interface', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1054L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugTypeUserInterface.id = getSystemEntityId(companyId)
            create(bugTypeUserInterface)

            SystemEntity bugTypeInconsistency = new SystemEntity(version: 0, key: 'Inconsistency', value: 'Inconsistency', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1055L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugTypeInconsistency.id = getSystemEntityId(companyId)
            create(bugTypeInconsistency)

            SystemEntity bugTypePerformance = new SystemEntity(version: 0, key: 'Performance', value: 'Performance', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1056L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugTypePerformance.id = getSystemEntityId(companyId)
            create(bugTypePerformance)

            SystemEntity bugTypeSuggestion = new SystemEntity(version: 0, key: 'Suggestion', value: 'Suggestion', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_BUG, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1057L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            bugTypeSuggestion.id = getSystemEntityId(companyId)
            create(bugTypeSuggestion)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private void createDefaultDataAcceptanceCriteriaTypeForPT(long companyId, def ptSystemEntityCacheService) {
        try {
            long pluginId = PtPluginConnector.PLUGIN_ID
            SystemEntity acceptanceCriteriaTypePreCondition = new SystemEntity(version: 0, key: 'Pre-condition', value: 'Pre-condition', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_ACCEPTANCE_CRITERIA, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1078L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            acceptanceCriteriaTypePreCondition.id = getSystemEntityId(companyId)
            create(acceptanceCriteriaTypePreCondition)

            SystemEntity acceptanceCriteriaTypeBusinessLogic = new SystemEntity(version: 0, key: 'Business Logic', value: 'Business Logic', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_ACCEPTANCE_CRITERIA, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1079L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            acceptanceCriteriaTypeBusinessLogic.id = getSystemEntityId(companyId)
            create(acceptanceCriteriaTypeBusinessLogic)

            SystemEntity acceptanceCriteriaTypePostCondition = new SystemEntity(version: 0, key: 'Post-condition', value: 'Post-condition', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_ACCEPTANCE_CRITERIA, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1080L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            acceptanceCriteriaTypePostCondition.id = getSystemEntityId(companyId)
            create(acceptanceCriteriaTypePostCondition)

            SystemEntity acceptanceCriteriaTypeOthers = new SystemEntity(version: 0, key: 'Others', value: 'Others', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_ACCEPTANCE_CRITERIA, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1096L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            acceptanceCriteriaTypeOthers.id = getSystemEntityId(companyId)
            create(acceptanceCriteriaTypeOthers)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default note entity type for project track
     */
    private void createDefaultNoteEntityTypeForPT(long companyId) {
        try {
            long pluginId = PluginConnector.PLUGIN_ID
            SystemEntity notePtTask = new SystemEntity(version: 0, key: 'Task', value: 'Note Entity Type Pt Task', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1094L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            notePtTask.id = getSystemEntityId(companyId)
            create(notePtTask)
            SystemEntity notePtBug = new SystemEntity(version: 0, key: 'Bug', value: 'Note Entity Type Pt Bug', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 100000169L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            notePtBug.id = getSystemEntityId(companyId)
            create(notePtBug)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default note entity type for project track
     */
    private void createDefaultBacklogSuggestionTypeForPT(long companyId, def ptSystemEntityCacheService) {
        try {
            long pluginId = PtPluginConnector.PLUGIN_ID

            SystemEntity backlogSuggestionTypeProposed = new SystemEntity(version: 0, key: 'Proposed', value: 'Proposed', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_CHANGE_REQUEST_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10140L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            backlogSuggestionTypeProposed.id = getSystemEntityId(companyId)
            create(backlogSuggestionTypeProposed)

            SystemEntity backlogSuggestionTypeApproved = new SystemEntity(version: 0, key: 'Approved', value: 'Approved', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_CHANGE_REQUEST_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10141L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            backlogSuggestionTypeApproved.id = getSystemEntityId(companyId)
            create(backlogSuggestionTypeApproved)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default note entity type for project track
     */
    private void createDefaultSchemaRelationTypeForPT(long companyId, def ptSystemEntityCacheService) {
        try {
            long pluginId = PtPluginConnector.PLUGIN_ID

            SystemEntity oneToMany = new SystemEntity(version: 0, key: 'One-to-many', value: 'One-to-many', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_ENTITY_RELATION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 100000311L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            oneToMany.id = getSystemEntityId(companyId)
            create(oneToMany)

            SystemEntity manyToOne = new SystemEntity(version: 0, key: 'Many-to-one', value: 'Many-to-one', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_ENTITY_RELATION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 100000340L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            manyToOne.id = getSystemEntityId(companyId)
            create(manyToOne)

            SystemEntity manyToMany = new SystemEntity(version: 0, key: 'Many-to-many', value: 'Many-to-many', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_ENTITY_RELATION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 100000312L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            manyToMany.id = getSystemEntityId(companyId)
            create(manyToMany)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    /**
     * Method to create default note entity type for project track
     */
    private void createDefaultDataTypeForPT(long companyId, def ptSystemEntityCacheService) {
        try {
            long pluginId = PtPluginConnector.PLUGIN_ID

            SystemEntity integerType = new SystemEntity(version: 0, key: 'Integer', value: 'Integer', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_DATA_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 100000313L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            integerType.id = getSystemEntityId(companyId)
            create(integerType)

            SystemEntity longType = new SystemEntity(version: 0, key: 'Long', value: 'Long', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_DATA_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 100000314L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            longType.id = getSystemEntityId(companyId)
            create(longType)

            SystemEntity floatType = new SystemEntity(version: 0, key: 'Float', value: 'Float', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_DATA_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 100000315L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            floatType.id = getSystemEntityId(companyId)
            create(floatType)

            SystemEntity doubleType = new SystemEntity(version: 0, key: 'Double', value: 'Double', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_DATA_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 100000316L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            doubleType.id = getSystemEntityId(companyId)
            create(doubleType)

            SystemEntity characterType = new SystemEntity(version: 0, key: 'Character', value: 'Character', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_DATA_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 100000317L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            characterType.id = getSystemEntityId(companyId)
            create(characterType)

            SystemEntity stringType = new SystemEntity(version: 0, key: 'String', value: 'String', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_DATA_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 100000318L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            stringType.id = getSystemEntityId(companyId)
            create(stringType)

            SystemEntity booleanType = new SystemEntity(version: 0, key: 'Boolean', value: 'Boolean', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_DATA_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 100000319L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            booleanType.id = getSystemEntityId(companyId)
            create(booleanType)

            SystemEntity dateType = new SystemEntity(version: 0, key: 'Date', value: 'Date', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_DATA_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 100000320L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            dateType.id = getSystemEntityId(companyId)
            create(dateType)

            SystemEntity byteType = new SystemEntity(version: 0, key: 'Byte', value: 'Byte', type: ptSystemEntityCacheService.SYS_ENTITY_TYPE_PT_DATA_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 100000321L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
            byteType.id = getSystemEntityId(companyId)
            create(byteType)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public boolean createDefaultDataForArms(long companyId, def rmsSystemEntityCacheService) {
        try {
            createDefaultDataProcessTypeForArms(companyId, rmsSystemEntityCacheService)
            createDefaultDataInstrumentTypeForArms(companyId, rmsSystemEntityCacheService)
            createDefaultDataPaymentMethodForArms(companyId, rmsSystemEntityCacheService)
            createDefaultDataTaskStatusForArms(companyId, rmsSystemEntityCacheService)
            createDefaultDataForRmsTaskNote(companyId)
            createDefaultDataForArmsAppUserEntity(companyId)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    private void createDefaultDataProcessTypeForArms(long companyId, def rmsSystemEntityCacheService) {
        long pluginId = ArmsPluginConnector.PLUGIN_ID

        SystemEntity processTypeIssue = new SystemEntity(version: 0, key: 'Issue', value: 'Issue', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_PROCESS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1150L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        processTypeIssue.id = getSystemEntityId(companyId)
        create(processTypeIssue)

        SystemEntity processTypeForward = new SystemEntity(version: 0, key: 'Forward', value: 'Forward', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_PROCESS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1151L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        processTypeForward.id = getSystemEntityId(companyId)
        create(processTypeForward)

        SystemEntity processTypePurchase = new SystemEntity(version: 0, key: 'Purchase', value: 'Purchase', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_PROCESS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1152L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        processTypePurchase.id = getSystemEntityId(companyId)
        create(processTypePurchase)
    }

    private void createDefaultDataInstrumentTypeForArms(long companyId, def rmsSystemEntityCacheService) {
        long pluginId = ArmsPluginConnector.PLUGIN_ID

        SystemEntity instrumentTypePo = new SystemEntity(version: 0, key: 'PO', value: 'PO', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_INSTRUMENT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1153L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        instrumentTypePo.id = getSystemEntityId(companyId)
        create(instrumentTypePo)

        SystemEntity instrumentTypeEft = new SystemEntity(version: 0, key: 'EFT', value: 'EFT', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_INSTRUMENT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1154L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        instrumentTypeEft.id = getSystemEntityId(companyId)
        create(instrumentTypeEft)

        SystemEntity instrumentTypeOnline = new SystemEntity(version: 0, key: 'Online', value: 'Online', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_INSTRUMENT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1155L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        instrumentTypeOnline.id = getSystemEntityId(companyId)
        create(instrumentTypeOnline)

        SystemEntity instrumentTypeCashCollection = new SystemEntity(version: 0, key: 'Cash collection', value: 'Cash collection', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_INSTRUMENT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1156L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        instrumentTypeCashCollection.id = getSystemEntityId(companyId)
        create(instrumentTypeCashCollection)

        SystemEntity instrumentTypeTt = new SystemEntity(version: 0, key: 'TT', value: 'TT', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_INSTRUMENT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1157L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        instrumentTypeTt.id = getSystemEntityId(companyId)
        create(instrumentTypeTt)

        SystemEntity instrumentTypeMt = new SystemEntity(version: 0, key: 'MT', value: 'MT', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_INSTRUMENT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1158L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        instrumentTypeMt.id = getSystemEntityId(companyId)
        create(instrumentTypeMt)
    }

    private void createDefaultDataPaymentMethodForArms(long companyId, def rmsSystemEntityCacheService) {
        long pluginId = ArmsPluginConnector.PLUGIN_ID

        SystemEntity payMethodBankDeposit = new SystemEntity(version: 0, key: 'Bank deposit', value: 'BD', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_PAYMENT_METHOD, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1160L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        payMethodBankDeposit.id = getSystemEntityId(companyId)
        create(payMethodBankDeposit)

        SystemEntity payMethodCashCollection = new SystemEntity(version: 0, key: 'Cash collection', value: 'CC', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_PAYMENT_METHOD, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1161L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        payMethodCashCollection.id = getSystemEntityId(companyId)
        create(payMethodCashCollection)
    }

    private void createDefaultDataTaskStatusForArms(long companyId, def rmsSystemEntityCacheService) {
        long pluginId = ArmsPluginConnector.PLUGIN_ID

        SystemEntity pendingTask = new SystemEntity(version: 0, key: 'Pending task', value: 'Pending task', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1162L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        pendingTask.id = getSystemEntityId(companyId)
        create(pendingTask)

        SystemEntity taskStatusNewTask = new SystemEntity(version: 0, key: 'New task', value: 'New task', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1163L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        taskStatusNewTask.id = getSystemEntityId(companyId)
        create(taskStatusNewTask)

        SystemEntity taskStatusIncludeInLst = new SystemEntity(version: 0, key: 'Included in list', value: 'Included in list', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1164L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        taskStatusIncludeInLst.id = getSystemEntityId(companyId)
        create(taskStatusIncludeInLst)

        SystemEntity taskStatusDecisionTaken = new SystemEntity(version: 0, key: 'Decision taken', value: 'Decision taken', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1165L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        taskStatusDecisionTaken.id = getSystemEntityId(companyId)
        create(taskStatusDecisionTaken)

        SystemEntity taskStatusDecisionApproved = new SystemEntity(version: 0, key: 'Decision approved', value: 'Decision approved', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1166L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        taskStatusDecisionApproved.id = getSystemEntityId(companyId)
        create(taskStatusDecisionApproved)

        SystemEntity taskStatusDisbursed = new SystemEntity(version: 0, key: 'Disbursed', value: 'Disbursed', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1167L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        taskStatusDisbursed.id = getSystemEntityId(companyId)
        create(taskStatusDisbursed)

        SystemEntity taskStatusCanceled = new SystemEntity(version: 0, key: 'Canceled', value: 'Canceled', type: rmsSystemEntityCacheService.SYS_ENTITY_TYPE_ARMS_TASK_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1168L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        taskStatusCanceled.id = getSystemEntityId(companyId)
        create(taskStatusCanceled)
    }

    private void createDefaultDataForRmsTaskNote(long companyId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity rmsTaskNote = new SystemEntity(version: 0, key: 'RmsTask', value: 'Note Entity Type RmsTask', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1181L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        rmsTaskNote.id = getSystemEntityId(companyId)
        create(rmsTaskNote)
    }

    private void createDefaultDataForArmsAppUserEntity(long companyId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity exchangeHouse = new SystemEntity(version: 0, key: 'Exchange House', value: 'User Mapping Exchange House', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1186L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        exchangeHouse.id = getSystemEntityId(companyId)
        create(exchangeHouse)
    }

    private void createDefaultDataForDocDatabaseVendor(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity vendorPostgre = new SystemEntity(version: 0, key: 'PostgreSQL', value: 'org.postgresql.Driver', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1143L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        vendorPostgre.id = getSystemEntityId(companyId)
        create(vendorPostgre)

        SystemEntity vendorMysql = new SystemEntity(version: 0, key: 'MySQL', value: 'com.mysql.jdbc.Driver', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1144L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        vendorMysql.id = getSystemEntityId(companyId)
        create(vendorMysql)

        SystemEntity vendorOracle = new SystemEntity(version: 0, key: 'Oracle', value: 'oracle.jdbc.driver.OracleDriver', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1145L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        vendorOracle.id = getSystemEntityId(companyId)
        create(vendorOracle)

        SystemEntity vendorMsSQL2008 = new SystemEntity(version: 0, key: 'MS SQLServer 2008', value: 'com.microsoft.sqlserver.jdbc.SQLServerDriver', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1146L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        vendorMsSQL2008.id = getSystemEntityId(companyId)
        create(vendorMsSQL2008)

        SystemEntity vendorMsSQL2012 = new SystemEntity(version: 0, key: 'MS SQLServer 2012', value: 'com.microsoft.sqlserver.jdbc.SQLServerDriver', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000305L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        vendorMsSQL2012.id = getSystemEntityId(companyId)
        create(vendorMsSQL2012)

        SystemEntity vendorAmazonRedShift = new SystemEntity(version: 0, key: 'Amazon RedShift', value: 'com.amazon.redshift.jdbc4.Driver', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1147L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        vendorAmazonRedShift.id = getSystemEntityId(companyId)
        create(vendorAmazonRedShift)

        SystemEntity greenPlum = new SystemEntity(version: 0, key: 'Greenplum', value: 'org.postgresql.Driver', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000189L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        greenPlum.id = getSystemEntityId(companyId)
        create(greenPlum)
    }

    private void createDefaultDataForAppOSVendor(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID

        SystemEntity vendorLinux = new SystemEntity(version: 0, key: 'Linux', value: 'Linux', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_OS_VENDOR, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000171L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        vendorLinux.id = getSystemEntityId(companyId)
        create(vendorLinux)

        SystemEntity vendorFreeBDS = new SystemEntity(version: 0, key: 'Free BDS', value: 'Free BDS', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_OS_VENDOR, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000172L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        vendorFreeBDS.id = getSystemEntityId(companyId)
        create(vendorFreeBDS)
    }

    public void createDefaultDataForDocument(long companyId, long sysUserId, def docSystemEntityCacheService) {
        try {
            createDefaultDataForDocAnswerType(companyId, sysUserId, docSystemEntityCacheService)
            createDefaultDataForDocDifficultyLevel(companyId, sysUserId, docSystemEntityCacheService)
            createDefaultDataForDocDocumentStatus(companyId, sysUserId, docSystemEntityCacheService)
            createDefaultDataForDocDocumentIndexTypeStatus(companyId, sysUserId, docSystemEntityCacheService)
            createDefaultDataForDocumentComment(companyId, sysUserId)
            createDefaultDataForDocDocumentCategoryType(companyId, sysUserId, docSystemEntityCacheService)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    public void createDefaultDataForElearning(long companyId, long sysUserId, def elSystemEntityCacheService) {
        try {
            createDefaultDataForElLanguageType(companyId, sysUserId, elSystemEntityCacheService)
            createDefaultDataForElCourseStatus(companyId, sysUserId, elSystemEntityCacheService)
            createDefaultDataForElAssignmentType(companyId, sysUserId, elSystemEntityCacheService)
            createDefaultDataForElResultEntityType(companyId, sysUserId, elSystemEntityCacheService)
            createDefaultDataForElUserPointType(companyId, sysUserId, elSystemEntityCacheService)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private void createDefaultDataForElLanguageType(long companyId, long systemUserId, def elSystemEntityCacheService) {
        long pluginId = ELearningPluginConnector.PLUGIN_ID

        SystemEntity bangla = new SystemEntity(version: 0, key: 'Bangla', value: 'Bangla', type: 15756, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000342L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        bangla.id = getSystemEntityId(companyId)
        create(bangla)

        SystemEntity english = new SystemEntity(version: 0, key: 'English', value: 'English', type: 15756, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000343L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        english.id = getSystemEntityId(companyId)
        create(english)

    }

    private void createDefaultDataForElAssignmentType(long companyId, long systemUserId, def elSystemEntityCacheService) {
        long pluginId = ELearningPluginConnector.PLUGIN_ID

        SystemEntity individual = new SystemEntity(version: 0, key: 'Individual', value: 'Individual', type: 15759L, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000352L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        individual.id = getSystemEntityId(companyId)
        create(individual)

        SystemEntity group = new SystemEntity(version: 0, key: 'Group', value: 'Group', type: 15759L, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000353L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        group.id = getSystemEntityId(companyId)
        create(group)

    }

    private void createDefaultDataForElCourseStatus(long companyId, long systemUserId, def elSystemEntityCacheService) {
        long pluginId = ELearningPluginConnector.PLUGIN_ID

        SystemEntity courseStatusRunning = new SystemEntity(version: 0, key: 'Running', value: 'Running', type: 15758, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000348L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        courseStatusRunning.id = getSystemEntityId(companyId)
        create(courseStatusRunning)

        SystemEntity courseStatusUpcoming = new SystemEntity(version: 0, key: 'Upcoming', value: 'Upcoming', type: 15758, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000349L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        courseStatusUpcoming.id = getSystemEntityId(companyId)
        create(courseStatusUpcoming)

        SystemEntity courseStatusPast = new SystemEntity(version: 0, key: 'Past', value: 'Past', type: 15758, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000350L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        courseStatusPast.id = getSystemEntityId(companyId)
        create(courseStatusPast)

        SystemEntity courseStatusWatchlist = new SystemEntity(version: 0, key: 'Watchlist', value: 'Watchlist', type: 15758, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000351L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        courseStatusWatchlist.id = getSystemEntityId(companyId)
        create(courseStatusWatchlist)

    }

    private createDefaultDataForElResultEntityType(long companyId, long systemUserId, def elSystemEntityCacheService) {
        long pluginId = ELearningPluginConnector.PLUGIN_ID
        SystemEntity resultEntityExam = new SystemEntity(version: 0, key: 'Exam', value: 'Exam', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_RESULT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000357L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        resultEntityExam.id = getSystemEntityId(companyId)
        create(resultEntityExam)

        SystemEntity resultEntityQuiz = new SystemEntity(version: 0, key: 'Quiz', value: 'Quiz', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_RESULT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000358L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        resultEntityQuiz.id = getSystemEntityId(companyId)
        create(resultEntityQuiz)

        SystemEntity resultEntityAssignment = new SystemEntity(version: 0, key: 'Assignment', value: 'Assignment', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_RESULT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000359L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        resultEntityAssignment.id = getSystemEntityId(companyId)
        create(resultEntityAssignment)

        SystemEntity resultEntityActivity = new SystemEntity(version: 0, key: 'Activity', value: 'Activity', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_RESULT_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000360L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        resultEntityActivity.id = getSystemEntityId(companyId)
        create(resultEntityActivity)
    }

    private createDefaultDataForElUserPointType(long companyId, long systemUserId, def elSystemEntityCacheService) {
        long pluginId = ELearningPluginConnector.PLUGIN_ID

        SystemEntity userPointRegistration = new SystemEntity(version: 0, key: 'Registration', value: '1000', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_USER_POINT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000366L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        userPointRegistration.id = getSystemEntityId(companyId)
        create(userPointRegistration)

        SystemEntity userPointProfileUpdate = new SystemEntity(version: 0, key: 'Profile Update', value: '100', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_USER_POINT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000367L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        userPointProfileUpdate.id = getSystemEntityId(companyId)
        create(userPointProfileUpdate)

        SystemEntity userPointCourseEnroll = new SystemEntity(version: 0, key: 'Course Enroll', value: '1000', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_USER_POINT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000368L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        userPointCourseEnroll.id = getSystemEntityId(companyId)
        create(userPointCourseEnroll)

        SystemEntity userPointContentRead = new SystemEntity(version: 0, key: 'Content Read', value: '100', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_USER_POINT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000369L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        userPointContentRead.id = getSystemEntityId(companyId)
        create(userPointContentRead)

        SystemEntity userPointAssignmentSubmit = new SystemEntity(version: 0, key: 'Assignment Submit', value: '200', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_USER_POINT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000370L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        userPointAssignmentSubmit.id = getSystemEntityId(companyId)
        create(userPointAssignmentSubmit)

        SystemEntity userPointPopQuizParticipate = new SystemEntity(version: 0, key: 'Pop Quiz Participate', value: '100', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_USER_POINT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000371L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        userPointPopQuizParticipate.id = getSystemEntityId(companyId)
        create(userPointPopQuizParticipate)

        SystemEntity userPointExamParticipate = new SystemEntity(version: 0, key: 'Exam Participate', value: '200', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_USER_POINT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000372L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        userPointExamParticipate.id = getSystemEntityId(companyId)
        create(userPointExamParticipate)

        SystemEntity userPointDailyLogin = new SystemEntity(version: 0, key: 'Daily Login', value: '100', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_USER_POINT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000373L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        userPointDailyLogin.id = getSystemEntityId(companyId)
        create(userPointDailyLogin)


        SystemEntity userPointDiscussionWriting = new SystemEntity(version: 0, key: 'Discussion Writing', value: '200', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_USER_POINT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000374L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        userPointDiscussionWriting.id = getSystemEntityId(companyId)
        create(userPointDiscussionWriting)

        SystemEntity userPointBlogComment = new SystemEntity(version: 0, key: 'Blog Comment', value: '300', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_USER_POINT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000375L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        userPointBlogComment.id = getSystemEntityId(companyId)
        create(userPointBlogComment)

        SystemEntity userPointCourseComplete = new SystemEntity(version: 0, key: 'Course Completion', value: '1000', type: elSystemEntityCacheService.SYS_ENTITY_TYPE_USER_POINT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 150000376L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        userPointCourseComplete.id = getSystemEntityId(companyId)
        create(userPointCourseComplete)
    }

    private void createDefaultDataForDocAnswerType(long companyId, long systemUserId, def docSystemEntityCacheService) {
        long pluginId = DocumentPluginConnector.PLUGIN_ID
        SystemEntity checkBox = new SystemEntity(version: 0, key: 'Check Box', value: 'Check Box', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_ANSWER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13106L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        checkBox.id = getSystemEntityId(companyId)
        create(checkBox)
        SystemEntity radioButton = new SystemEntity(version: 0, key: 'Radio Button', value: 'Radio Button', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_ANSWER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13107L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        radioButton.id = getSystemEntityId(companyId)
        create(radioButton)
        SystemEntity textArea = new SystemEntity(version: 0, key: 'Text Area', value: 'Text Area', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_ANSWER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13108L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        textArea.id = getSystemEntityId(companyId)
        create(textArea)

        SystemEntity textBox = new SystemEntity(version: 0, key: 'Text Box', value: 'Text Box', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_ANSWER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 130000364, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        textBox.id = getSystemEntityId(companyId)
        create(textBox)

        SystemEntity dropDown = new SystemEntity(version: 0, key: 'Drop Down', value: 'Drop Down', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_ANSWER, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 130000365, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dropDown.id = getSystemEntityId(companyId)
        create(dropDown)
    }

    private void createDefaultDataForDocDifficultyLevel(long companyId, long systemUserId,
                                                        def docSystemEntityCacheService) {
        long pluginId = DocumentPluginConnector.PLUGIN_ID
        SystemEntity hard = new SystemEntity(version: 0, key: 'Hard', value: 'Hard', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DIFFICULTY_LEVEL, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13109L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        hard.id = getSystemEntityId(companyId)
        create(hard)

        SystemEntity moderate = new SystemEntity(version: 0, key: 'Moderate', value: 'Moderate', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DIFFICULTY_LEVEL, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13110L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        moderate.id = getSystemEntityId(companyId)
        create(moderate)

        SystemEntity easy = new SystemEntity(version: 0, key: 'Easy', value: 'Easy', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DIFFICULTY_LEVEL, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13111L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        easy.id = getSystemEntityId(companyId)
        create(easy)
    }

    private void createDefaultDataForDocDocumentStatus(long companyId, long systemUserId,
                                                       def docSystemEntityCacheService) {
        long pluginId = DocumentPluginConnector.PLUGIN_ID

        SystemEntity newDocument = new SystemEntity(version: 0, key: 'New Document', value: 'New Document', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13126L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        newDocument.id = getSystemEntityId(companyId)
        create(newDocument)

        SystemEntity requestIndex = new SystemEntity(version: 0, key: 'Request for Index', value: 'Request for Index', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13127L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        requestIndex.id = getSystemEntityId(companyId)
        create(requestIndex)

        SystemEntity indexed = new SystemEntity(version: 0, key: 'Indexed', value: 'Indexed', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13128L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        indexed.id = getSystemEntityId(companyId)
        create(indexed)

        SystemEntity indexedFailed = new SystemEntity(version: 0, key: 'Index Failed', value: 'Indexed Failed', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13129L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        indexedFailed.id = getSystemEntityId(companyId)
        create(indexedFailed)

        SystemEntity submitProcess = new SystemEntity(version: 0, key: 'Submitted to Process', value: 'Submitted to Process', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13130L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        submitProcess.id = getSystemEntityId(companyId)
        create(submitProcess)

        SystemEntity queueProcess = new SystemEntity(version: 0, key: 'Queued to Process', value: 'Queued to Process', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13131L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        queueProcess.id = getSystemEntityId(companyId)
        create(queueProcess)

        SystemEntity conversionProcess = new SystemEntity(version: 0, key: 'Conversion in progress', value: 'Conversion in progress', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13132L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        conversionProcess.id = getSystemEntityId(companyId)
        create(conversionProcess)

        SystemEntity noCredit = new SystemEntity(version: 0, key: 'Not enough Credit', value: 'Not enough Credit', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13133L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        noCredit.id = getSystemEntityId(companyId)
        create(noCredit)
    }

    private void createDefaultDataForDocDocumentIndexTypeStatus(long companyId, long systemUserId,
                                                                def docSystemEntityCacheService) {
        long pluginId = DocumentPluginConnector.PLUGIN_ID

        SystemEntity none = new SystemEntity(version: 0, key: 'None', value: 'None', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_INDEX, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13134L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        none.id = getSystemEntityId(companyId)
        create(none)

        SystemEntity application = new SystemEntity(version: 0, key: 'Application', value: 'Application', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_INDEX, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13135L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        application.id = getSystemEntityId(companyId)
        create(application)

        SystemEntity ocr = new SystemEntity(version: 0, key: 'OCR', value: 'OCR', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_INDEX, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 13136L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        ocr.id = getSystemEntityId(companyId)
        create(ocr)
    }

    private void createDefaultDataForDocumentComment(long companyId, long systemUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity docComment = new SystemEntity(version: 0, key: 'Document Comment', value: 'Document Comment', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 130000162L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        docComment.id = getSystemEntityId(companyId)
        create(docComment)
    }

    private void createDefaultDataForDocDocumentCategoryType(long companyId, long systemUserId,
                                                             def docSystemEntityCacheService) {
        long pluginId = DocumentPluginConnector.PLUGIN_ID
        SystemEntity article = new SystemEntity(version: 0, key: 'Article', value: 'Article', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_CATEGORY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 130000163L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        article.id = getSystemEntityId(companyId)
        create(article)
        SystemEntity file = new SystemEntity(version: 0, key: 'File', value: 'File', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_CATEGORY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 130000164L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        file.id = getSystemEntityId(companyId)
        create(file)
        SystemEntity image = new SystemEntity(version: 0, key: 'Image', value: 'Image', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_CATEGORY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 130000165L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        image.id = getSystemEntityId(companyId)
        create(image)
        SystemEntity audio = new SystemEntity(version: 0, key: 'Audio', value: 'Audio', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_CATEGORY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 130000166L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        audio.id = getSystemEntityId(companyId)
        create(audio)
        SystemEntity video = new SystemEntity(version: 0, key: 'Video', value: 'Video', type: docSystemEntityCacheService.SYS_ENTITY_TYPE_DOCUMENT_CATEGORY, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 130000167L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        video.id = getSystemEntityId(companyId)
        create(video)
    }

    public boolean createDefaultDataForSarb(long companyId, def sarbSystemEntityCacheService) {
        try {
            createDefaultDataForSarbTaskReviseStatus(companyId, sarbSystemEntityCacheService)
            createDefaultDataForSarbTaskCurrentStatus(companyId, sarbSystemEntityCacheService)
            createDefaultDataForSarbConversionType(companyId, sarbSystemEntityCacheService)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return false
        }
    }

    private void createDefaultDataForSarbTaskReviseStatus(long companyId, def sarbSystemEntityCacheService) {
        long pluginId = SarbPluginConnector.PLUGIN_ID
        SystemEntity movedForCancel = new SystemEntity(version: 0, key: 'Moved for cancel', value: 'Moved for cancel', type: sarbSystemEntityCacheService.SYS_ENTITY_TYPE_SARB_TASK_REVISE_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1298L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        movedForCancel.id = getSystemEntityId(companyId)
        create(movedForCancel)

        SystemEntity movedForReplace = new SystemEntity(version: 0, key: 'Moved for replace', value: 'Moved for replace', type: sarbSystemEntityCacheService.SYS_ENTITY_TYPE_SARB_TASK_REVISE_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1299L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        movedForReplace.id = getSystemEntityId(companyId)
        create(movedForReplace)
    }

    private void createDefaultDataForSarbTaskCurrentStatus(long companyId, def sarbSystemEntityCacheService) {
        long pluginId = SarbPluginConnector.PLUGIN_ID
        SystemEntity taskStatusRegular = new SystemEntity(version: 0, key: 'Regular', value: 'Regular', type: sarbSystemEntityCacheService.SYS_ENTITY_TYPE_SARB_TASK_CURRENT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 12112L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        taskStatusRegular.id = getSystemEntityId(companyId)
        create(taskStatusRegular)

        SystemEntity taskStatusCancel = new SystemEntity(version: 0, key: 'Cancel', value: 'Cancel', type: sarbSystemEntityCacheService.SYS_ENTITY_TYPE_SARB_TASK_CURRENT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 12113L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        taskStatusCancel.id = getSystemEntityId(companyId)
        create(taskStatusCancel)

        SystemEntity taskStatusReplace = new SystemEntity(version: 0, key: 'Replace', value: 'Replace', type: sarbSystemEntityCacheService.SYS_ENTITY_TYPE_SARB_TASK_CURRENT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 12114L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        taskStatusReplace.id = getSystemEntityId(companyId)
        create(taskStatusReplace)

        SystemEntity taskStatusRefund = new SystemEntity(version: 0, key: 'Refund', value: 'Refund', type: sarbSystemEntityCacheService.SYS_ENTITY_TYPE_SARB_TASK_CURRENT_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 12115L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        taskStatusRefund.id = getSystemEntityId(companyId)
        create(taskStatusRefund)
    }

    private void createDefaultDataForSarbConversionType(long companyId, def sarbSystemEntityCacheService) {
        long pluginId = SarbPluginConnector.PLUGIN_ID
        SystemEntity typeBuy = new SystemEntity(version: 0, key: 'Buy', value: 'Buy', type: sarbSystemEntityCacheService.SYS_ENTITY_TYPE_SARB_CURRENCY_CONVERSION_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 12116L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        typeBuy.id = getSystemEntityId(companyId)
        create(typeBuy)

        SystemEntity typeSell = new SystemEntity(version: 0, key: 'Sell', value: 'Sell', type: sarbSystemEntityCacheService.SYS_ENTITY_TYPE_SARB_CURRENCY_CONVERSION_TYPE, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 12117L, createdOn: new Date(), createdBy: 1, updatedBy: 0)
        typeSell.id = getSystemEntityId(companyId)
        create(typeSell)
    }

    public void createDefaultDataForDataPipeline(long companyId, long sysUserId, def dplSystemEntityCacheService) {
        try {
            createDefaultDataForDplOfflineDataFeedStatus(companyId, sysUserId, dplSystemEntityCacheService)
            createDefaultDataForDplDataTypeMappingForMsSqlToGreenPlum(companyId, sysUserId, dplSystemEntityCacheService)
            createDefaultDataForDplDataTypeMappingForMsSqlToRedShift(companyId, sysUserId, dplSystemEntityCacheService)
            createDefaultDataForDplDataTypeMappingForMySqlToGreenPlum(companyId, sysUserId, dplSystemEntityCacheService)
            createDefaultDataForDplDataTypeMappingForMySqlToRedShift(companyId, sysUserId, dplSystemEntityCacheService)
            createDefaultDataForDplDataFeedType(companyId, sysUserId, dplSystemEntityCacheService)
            createDefaultDataForDplDataFeedTypeCSV(companyId, sysUserId, dplSystemEntityCacheService)
            createDefaultDataForDplDataFeedTypeText(companyId, sysUserId, dplSystemEntityCacheService)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    private void createDefaultDataForDplOfflineDataFeedStatus(long companyId, long systemUserId,
                                                              def dplSystemEntityCacheService) {
        long pluginId = DataPipeLinePluginConnector.PLUGIN_ID

        SystemEntity received = new SystemEntity(version: 0, key: 'Received', value: 'Received', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_FEED_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000159L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        received.id = getSystemEntityId(companyId)
        create(received)

        SystemEntity preProcess = new SystemEntity(version: 0, key: 'Pre-Process', value: 'Pre-Process', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_FEED_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000160L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        preProcess.id = getSystemEntityId(companyId)
        create(preProcess)

        SystemEntity loaded = new SystemEntity(version: 0, key: 'Loaded', value: 'Loaded', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_FEED_STATUS, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000161L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        loaded.id = getSystemEntityId(companyId)
        create(loaded)
    }

    private void createDefaultDataForDplDataFeedType(long companyId, long systemUserId,
                                                     def dplSystemEntityCacheService) {
        long pluginId = DataPipeLinePluginConnector.PLUGIN_ID

        SystemEntity dataFeedTypeCsv = new SystemEntity(version: 0, key: 'CSV', value: 'CSV', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_FEED, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000298L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataFeedTypeCsv.id = getSystemEntityId(companyId)
        create(dataFeedTypeCsv)

        SystemEntity dataFeedTypeText = new SystemEntity(version: 0, key: 'TEXT', value: 'TEXT', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_FEED, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000299L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataFeedTypeText.id = getSystemEntityId(companyId)
        create(dataFeedTypeText)
    }

    private void createDefaultDataForDplDataFeedTypeCSV(long companyId, long systemUserId,
                                                        def dplSystemEntityCacheService) {
        long pluginId = DataPipeLinePluginConnector.PLUGIN_ID

        SystemEntity received = new SystemEntity(version: 0, key: 'Column Separator', value: ',', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_FEED_CSV, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000300L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        received.id = getSystemEntityId(companyId)
        create(received)

        SystemEntity preProcess = new SystemEntity(version: 0, key: 'Quote Character', value: '"', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_FEED_CSV, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000301L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        preProcess.id = getSystemEntityId(companyId)
        create(preProcess)

        SystemEntity loaded = new SystemEntity(version: 0, key: 'Escape Character', value: '\\', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_FEED_CSV, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000302L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        loaded.id = getSystemEntityId(companyId)
        create(loaded)
    }

    private void createDefaultDataForDplDataFeedTypeText(long companyId, long systemUserId,
                                                         def dplSystemEntityCacheService) {
        long pluginId = DataPipeLinePluginConnector.PLUGIN_ID

        SystemEntity received = new SystemEntity(version: 0, key: 'Column Separator', value: '|', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_FEED_TEXT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000303L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        received.id = getSystemEntityId(companyId)
        create(received)

        SystemEntity preProcess = new SystemEntity(version: 0, key: 'Escape Character', value: '\\', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_FEED_TEXT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000304L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        preProcess.id = getSystemEntityId(companyId)
        create(preProcess)

    }

    private void createDefaultDataForDplDataTypeMappingForMsSqlToGreenPlum(long companyId, long systemUserId,
                                                                           def dplSystemEntityCacheService) {
        long pluginId = DataPipeLinePluginConnector.PLUGIN_ID

        SystemEntity dataTypeInt = new SystemEntity(version: 0, key: 'int', value: 'integer', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000190L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeInt.id = getSystemEntityId(companyId)
        create(dataTypeInt)

        SystemEntity dataTypeTinyInt = new SystemEntity(version: 0, key: 'tinyint', value: 'smallint', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000191L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTinyInt.id = getSystemEntityId(companyId)
        create(dataTypeTinyInt)

        SystemEntity dataTypeSmallint = new SystemEntity(version: 0, key: 'smallint', value: 'smallint', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000192L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeSmallint.id = getSystemEntityId(companyId)
        create(dataTypeSmallint)

        SystemEntity dataTypeBigInt = new SystemEntity(version: 0, key: 'bigint', value: 'bigint', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000193L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeBigInt.id = getSystemEntityId(companyId)
        create(dataTypeBigInt)

        SystemEntity dataTypeMoney = new SystemEntity(version: 0, key: 'money', value: 'money', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000194L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeMoney.id = getSystemEntityId(companyId)
        create(dataTypeMoney)

        SystemEntity dataTypeSmallMoney = new SystemEntity(version: 0, key: 'smallmoney', value: 'money', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1140000195L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeSmallMoney.id = getSystemEntityId(companyId)
        create(dataTypeSmallMoney)

        SystemEntity dataTypeDecimal = new SystemEntity(version: 0, key: 'decimal', value: 'numeric', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000196L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDecimal.id = getSystemEntityId(companyId)
        create(dataTypeDecimal)

        SystemEntity dataTypeNumeric = new SystemEntity(version: 0, key: 'numeric', value: 'numeric', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000197L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeNumeric.id = getSystemEntityId(companyId)
        create(dataTypeNumeric)

        SystemEntity dataTypeFloat = new SystemEntity(version: 0, key: 'float', value: 'real', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000198L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeFloat.id = getSystemEntityId(companyId)
        create(dataTypeFloat)

        SystemEntity dataTypeReal = new SystemEntity(version: 0, key: 'real', value: 'real', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000199L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeReal.id = getSystemEntityId(companyId)
        create(dataTypeReal)

        SystemEntity dataTypeDate = new SystemEntity(version: 0, key: 'date', value: 'date', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000200L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDate.id = getSystemEntityId(companyId)
        create(dataTypeDate)

        SystemEntity dataTypeDateTime = new SystemEntity(version: 0, key: 'datetime', value: 'timestamp without time zone', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000201L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDateTime.id = getSystemEntityId(companyId)
        create(dataTypeDateTime)

        SystemEntity dataTypeDateTime2 = new SystemEntity(version: 0, key: 'datetime2', value: 'timestamp without time zone', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000202L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDateTime2.id = getSystemEntityId(companyId)
        create(dataTypeDateTime2)

        SystemEntity dataTypeDateTimeOffset = new SystemEntity(version: 0, key: 'datetimeoffset', value: 'timestamp with time zone', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000203L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDateTimeOffset.id = getSystemEntityId(companyId)
        create(dataTypeDateTimeOffset)

        SystemEntity dataTypeSmallDateTime = new SystemEntity(version: 0, key: 'smalldatetime', value: 'timestamp without time zone', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000204L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeSmallDateTime.id = getSystemEntityId(companyId)
        create(dataTypeSmallDateTime)

        SystemEntity dataTypeTime = new SystemEntity(version: 0, key: 'time', value: 'time without time zone', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000205L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTime.id = getSystemEntityId(companyId)
        create(dataTypeTime)

        SystemEntity dataTypeChar = new SystemEntity(version: 0, key: 'char', value: 'character varying', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000206L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeChar.id = getSystemEntityId(companyId)
        create(dataTypeChar)

        SystemEntity dataTypeNchar = new SystemEntity(version: 0, key: 'nchar', value: 'character varying', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000207L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeNchar.id = getSystemEntityId(companyId)
        create(dataTypeNchar)

        SystemEntity dataTypeVarChar = new SystemEntity(version: 0, key: 'varchar', value: 'character varying', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000208L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeVarChar.id = getSystemEntityId(companyId)
        create(dataTypeVarChar)

        SystemEntity dataTypeNvarChar = new SystemEntity(version: 0, key: 'nvarchar', value: 'character varying', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000209L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeNvarChar.id = getSystemEntityId(companyId)
        create(dataTypeNvarChar)

        SystemEntity dataTypeBit = new SystemEntity(version: 0, key: 'bit', value: 'bit varying', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000210L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeBit.id = getSystemEntityId(companyId)
        create(dataTypeBit)

        SystemEntity dataTypeBinary = new SystemEntity(version: 0, key: 'binary', value: 'bytea', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000211L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeBinary.id = getSystemEntityId(companyId)
        create(dataTypeBinary)

        SystemEntity dataTypeVarBinary = new SystemEntity(version: 0, key: 'varbinary', value: 'bytea', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000212L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeVarBinary.id = getSystemEntityId(companyId)
        create(dataTypeVarBinary)

        SystemEntity dataTypeXml = new SystemEntity(version: 0, key: 'xml', value: 'xml', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000213L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeXml.id = getSystemEntityId(companyId)
        create(dataTypeXml)

        SystemEntity dataTypeRowVersion = new SystemEntity(version: 0, key: 'rowversion', value: 'bigint', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000214L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeRowVersion.id = getSystemEntityId(companyId)
        create(dataTypeRowVersion)

        SystemEntity dataTypeUniqueIdentifier = new SystemEntity(version: 0, key: 'uniqueidentifier', value: 'smallint', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000215L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeUniqueIdentifier.id = getSystemEntityId(companyId)
        create(dataTypeUniqueIdentifier)

        SystemEntity dataTypeText = new SystemEntity(version: 0, key: 'text', value: 'text', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000216L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeText.id = getSystemEntityId(companyId)
        create(dataTypeText)

        SystemEntity dataTypeNtext = new SystemEntity(version: 0, key: 'ntext', value: 'text', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000217L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeNtext.id = getSystemEntityId(companyId)
        create(dataTypeNtext)

        SystemEntity dataTypeImage = new SystemEntity(version: 0, key: 'image', value: 'bytea', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MSSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000218L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeImage.id = getSystemEntityId(companyId)
        create(dataTypeImage)
    }

    private void createDefaultDataForDplDataTypeMappingForMsSqlToRedShift(long companyId, long systemUserId,
                                                                          def dplSystemEntityCacheService) {
        long pluginId = DataPipeLinePluginConnector.PLUGIN_ID

        SystemEntity dataTypeInt = new SystemEntity(version: 0, key: 'int', value: 'INTEGER', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000219L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeInt.id = getSystemEntityId(companyId)
        create(dataTypeInt)

        SystemEntity dataTypeTinyInt = new SystemEntity(version: 0, key: 'tinyint', value: 'SMALLINT', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000220L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTinyInt.id = getSystemEntityId(companyId)
        create(dataTypeTinyInt)

        SystemEntity dataTypeSmallint = new SystemEntity(version: 0, key: 'smallint', value: 'SMALLINT', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000221L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeSmallint.id = getSystemEntityId(companyId)
        create(dataTypeSmallint)

        SystemEntity dataTypeBigInt = new SystemEntity(version: 0, key: 'bigint', value: 'BIGINT', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000222L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeBigInt.id = getSystemEntityId(companyId)
        create(dataTypeBigInt)

        SystemEntity dataTypeMoney = new SystemEntity(version: 0, key: 'money', value: 'BIGINT', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000223L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeMoney.id = getSystemEntityId(companyId)
        create(dataTypeMoney)

        SystemEntity dataTypeSmallMoney = new SystemEntity(version: 0, key: 'smallmoney', value: 'INTEGER', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 1140000234L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeSmallMoney.id = getSystemEntityId(companyId)
        create(dataTypeSmallMoney)

        SystemEntity dataTypeDecimal = new SystemEntity(version: 0, key: 'decimal', value: 'DECIMAL', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000225L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDecimal.id = getSystemEntityId(companyId)
        create(dataTypeDecimal)

        SystemEntity dataTypeNumeric = new SystemEntity(version: 0, key: 'numeric', value: 'DECIMAL', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000226L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeNumeric.id = getSystemEntityId(companyId)
        create(dataTypeNumeric)

        SystemEntity dataTypeFloat = new SystemEntity(version: 0, key: 'float', value: 'REAL', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000227L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeFloat.id = getSystemEntityId(companyId)
        create(dataTypeFloat)

        SystemEntity dataTypeReal = new SystemEntity(version: 0, key: 'real', value: 'REAL', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000228L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeReal.id = getSystemEntityId(companyId)
        create(dataTypeReal)

        SystemEntity dataTypeDate = new SystemEntity(version: 0, key: 'date', value: 'DATE', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000229L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDate.id = getSystemEntityId(companyId)
        create(dataTypeDate)

        SystemEntity dataTypeDateTime = new SystemEntity(version: 0, key: 'datetime', value: 'TIMESTAMP WITHOUT TIME ZONE', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000230L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDateTime.id = getSystemEntityId(companyId)
        create(dataTypeDateTime)

        SystemEntity dataTypeDateTime2 = new SystemEntity(version: 0, key: 'datetime2', value: 'TIMESTAMP WITHOUT TIME ZONE', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000231L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDateTime2.id = getSystemEntityId(companyId)
        create(dataTypeDateTime2)

        SystemEntity dataTypeDateTimeOffset = new SystemEntity(version: 0, key: 'datetimeoffset', value: 'TIMESTAMP WITHOUT TIME ZONE', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000232L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDateTimeOffset.id = getSystemEntityId(companyId)
        create(dataTypeDateTimeOffset)

        SystemEntity dataTypeSmallDateTime = new SystemEntity(version: 0, key: 'smalldatetime', value: 'TIMESTAMP WITHOUT TIME ZONE', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000233L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeSmallDateTime.id = getSystemEntityId(companyId)
        create(dataTypeSmallDateTime)

        SystemEntity dataTypeTime = new SystemEntity(version: 0, key: 'time', value: '[Unsupported Data Type:time]', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000287L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTime.id = getSystemEntityId(companyId)
        create(dataTypeTime)

        SystemEntity dataTypeChar = new SystemEntity(version: 0, key: 'char', value: 'VARCHAR', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000234L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeChar.id = getSystemEntityId(companyId)
        create(dataTypeChar)

        SystemEntity dataTypeNchar = new SystemEntity(version: 0, key: 'nchar', value: 'VARCHAR', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000235L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeNchar.id = getSystemEntityId(companyId)
        create(dataTypeNchar)

        SystemEntity dataTypeVarChar = new SystemEntity(version: 0, key: 'varchar', value: 'VARCHAR', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000236L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeVarChar.id = getSystemEntityId(companyId)
        create(dataTypeVarChar)

        SystemEntity dataTypeNvarChar = new SystemEntity(version: 0, key: 'nvarchar', value: 'VARCHAR', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000237L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeNvarChar.id = getSystemEntityId(companyId)
        create(dataTypeNvarChar)

        SystemEntity dataTypeBit = new SystemEntity(version: 0, key: 'bit', value: '[Unsupported Data Type:bit]', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000288L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeBit.id = getSystemEntityId(companyId)
        create(dataTypeBit)

        SystemEntity dataTypeBinary = new SystemEntity(version: 0, key: 'binary', value: '[Unsupported Data Type:binary]', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000289L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeBinary.id = getSystemEntityId(companyId)
        create(dataTypeBinary)

        SystemEntity dataTypeVarBinary = new SystemEntity(version: 0, key: 'varbinary', value: '[Unsupported Data Type:varbinary]', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000290L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeVarBinary.id = getSystemEntityId(companyId)
        create(dataTypeVarBinary)

        SystemEntity dataTypeXml = new SystemEntity(version: 0, key: 'xml', value: '[Unsupported Data Type:xml]', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000291L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeXml.id = getSystemEntityId(companyId)
        create(dataTypeXml)

        SystemEntity dataTypeRowVersion = new SystemEntity(version: 0, key: 'rowversion', value: 'BIGINT', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000238L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeRowVersion.id = getSystemEntityId(companyId)
        create(dataTypeRowVersion)

        SystemEntity dataTypeUniqueIdentifier = new SystemEntity(version: 0, key: 'uniqueidentifier', value: 'SMALLINT', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000239L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeUniqueIdentifier.id = getSystemEntityId(companyId)
        create(dataTypeUniqueIdentifier)

        SystemEntity dataTypeText = new SystemEntity(version: 0, key: 'text', value: 'VARCHAR', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000240L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeText.id = getSystemEntityId(companyId)
        create(dataTypeText)

        SystemEntity dataTypeNtext = new SystemEntity(version: 0, key: 'ntext', value: 'VARCHAR', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000241L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeNtext.id = getSystemEntityId(companyId)
        create(dataTypeNtext)

        SystemEntity dataTypeImage = new SystemEntity(version: 0, key: 'image', value: '[Unsupported Data Type:image]', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYkPE_MAPPING_FROM_MSSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000292L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeImage.id = getSystemEntityId(companyId)
        create(dataTypeImage)
    }

    private void createDefaultDataForDplDataTypeMappingForMySqlToGreenPlum(long companyId, long systemUserId,
                                                                           def dplSystemEntityCacheService) {
        long pluginId = DataPipeLinePluginConnector.PLUGIN_ID

        SystemEntity dataTypeIntMySqlToGreenPlum = new SystemEntity(version: 0, key: 'INT', value: 'integer', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000242L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeIntMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeIntMySqlToGreenPlum)

        SystemEntity dataTypeTinyIntMySqlToGreenPlum = new SystemEntity(version: 0, key: 'TINYINT', value: 'smallint', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000243L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTinyIntMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeTinyIntMySqlToGreenPlum)

        SystemEntity dataTypeSmallIntMySqlToGreenPlum = new SystemEntity(version: 0, key: 'SMALLINT', value: 'smallint', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000244L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeSmallIntMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeSmallIntMySqlToGreenPlum)

        SystemEntity dataTypeMediumIntMySqlToGreenPlum = new SystemEntity(version: 0, key: 'MEDIUMINT', value: 'integer', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000245L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeMediumIntMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeMediumIntMySqlToGreenPlum)

        SystemEntity dataTypeBigIntMySqlToGreenPlum = new SystemEntity(version: 0, key: 'BIGINT', value: 'bigint', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000246L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeBigIntMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeBigIntMySqlToGreenPlum)

        SystemEntity dataTypeFloatMySqlToGreenPlum = new SystemEntity(version: 0, key: 'FLOAT', value: 'real', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000247L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeFloatMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeFloatMySqlToGreenPlum)

        SystemEntity dataTypeDoubleMySqlToGreenPlum = new SystemEntity(version: 0, key: 'DOUBLE', value: 'double precision', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000248L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDoubleMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeDoubleMySqlToGreenPlum)

        SystemEntity dataTypeRealMySqlToGreenPlum = new SystemEntity(version: 0, key: 'REAL', value: 'double precision', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000249L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeRealMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeRealMySqlToGreenPlum)

        SystemEntity dataTypeDecimalMySqlToGreenPlum = new SystemEntity(version: 0, key: 'DECIMAL', value: 'decimal', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000250L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDecimalMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeDecimalMySqlToGreenPlum)

        SystemEntity dataTypeNumericMySqlToGreenPlum = new SystemEntity(version: 0, key: 'NUMERIC', value: 'decimal', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000251L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeNumericMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeNumericMySqlToGreenPlum)

        SystemEntity dataTypeDateMySqlToGreenPlum = new SystemEntity(version: 0, key: 'DATE', value: 'date', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000252L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDateMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeDateMySqlToGreenPlum)

        SystemEntity dataTypeDateTimeMySqlToGreenPlum = new SystemEntity(version: 0, key: 'DATETIME', value: 'timestamp without time zone', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000253L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDateTimeMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeDateTimeMySqlToGreenPlum)

        SystemEntity dataTypeTimeStampMySqlToGreenPlum = new SystemEntity(version: 0, key: 'TIMESTAMP', value: 'timestamp without time zone', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000254L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTimeStampMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeTimeStampMySqlToGreenPlum)

        SystemEntity dataTypeTimeMySqlToGreenPlum = new SystemEntity(version: 0, key: 'TIME', value: 'time without time zone', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000255L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTimeMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeTimeMySqlToGreenPlum)

        SystemEntity dataTypeCharMySqlToGreenPlum = new SystemEntity(version: 0, key: 'CHAR', value: 'character varying', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000256L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeCharMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeCharMySqlToGreenPlum)

        SystemEntity dataTypeVarCharMySqlToGreenPlum = new SystemEntity(version: 0, key: 'VARCHAR', value: 'character varying', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000257L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeVarCharMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeVarCharMySqlToGreenPlum)

        SystemEntity dataTypeBlobMySqlToGreenPlum = new SystemEntity(version: 0, key: 'BLOB', value: 'bytea', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000258L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeBlobMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeBlobMySqlToGreenPlum)

        SystemEntity dataTypeTextMySqlToGreenPlum = new SystemEntity(version: 0, key: 'TEXT', value: 'text', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000259L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTextMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeTextMySqlToGreenPlum)

        SystemEntity dataTypeTinyBlobMySqlToGreenPlum = new SystemEntity(version: 0, key: 'TINYBLOB', value: 'bytea', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000260L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTinyBlobMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeTinyBlobMySqlToGreenPlum)

        SystemEntity dataTypeTinyTextMySqlToGreenPlum = new SystemEntity(version: 0, key: 'TINYTEXT', value: 'text', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000261L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTinyTextMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeTinyTextMySqlToGreenPlum)

        SystemEntity dataTypeMediumBlobMySqlToGreenPlum = new SystemEntity(version: 0, key: 'MEDIUMBLOB', value: 'bytea', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000262L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeMediumBlobMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeMediumBlobMySqlToGreenPlum)

        SystemEntity dataTypeMediumTextMySqlToGreenPlum = new SystemEntity(version: 0, key: 'MEDIUMTEXT', value: 'text', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000263L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeMediumTextMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeMediumTextMySqlToGreenPlum)

        SystemEntity dataTypeLongBlobMySqlToGreenPlum = new SystemEntity(version: 0, key: 'LONGBLOB', value: 'bytea', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000264L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeLongBlobMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeLongBlobMySqlToGreenPlum)

        SystemEntity dataTypeLongTextMySqlToGreenPlum = new SystemEntity(version: 0, key: 'LONGTEXT', value: 'text', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000265L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeLongTextMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeLongTextMySqlToGreenPlum)

        SystemEntity dataTypeYearMySqlToGreenPlum = new SystemEntity(version: 0, key: 'YEAR', value: 'smallint', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_GREENPLUM, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000285L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeYearMySqlToGreenPlum.id = getSystemEntityId(companyId)
        create(dataTypeYearMySqlToGreenPlum)
    }

    private void createDefaultDataForDplDataTypeMappingForMySqlToRedShift(long companyId, long systemUserId,
                                                                          def dplSystemEntityCacheService) {
        long pluginId = DataPipeLinePluginConnector.PLUGIN_ID

        SystemEntity dataTypeIntMySqlToRedShift = new SystemEntity(key: 'INT', value: 'INTEGER', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000266L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeIntMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeIntMySqlToRedShift)

        SystemEntity dataTypeTinyIntMySqlToRedShift = new SystemEntity(key: 'TINYINT', value: 'SMALLINT', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000267L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTinyIntMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeTinyIntMySqlToRedShift)

        SystemEntity dataTypeSmallIntMySqlToRedShift = new SystemEntity(key: 'SMALLINT', value: 'SMALLINT', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000268L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeSmallIntMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeSmallIntMySqlToRedShift)

        SystemEntity dataTypeMediumIntMySqlToRedShift = new SystemEntity(key: 'MEDIUMINT', value: 'INTEGER', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000269L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeMediumIntMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeMediumIntMySqlToRedShift)

        SystemEntity dataTypeBigIntMySqlToRedShift = new SystemEntity(key: 'BIGINT', value: 'BIGINT', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000270L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeBigIntMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeBigIntMySqlToRedShift)

        SystemEntity dataTypeFloatMySqlToRedShift = new SystemEntity(key: 'FLOAT', value: 'REAL', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000271L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeFloatMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeFloatMySqlToRedShift)

        SystemEntity dataTypeDoubleMySqlToRedShift = new SystemEntity(key: 'DOUBLE', value: 'DOUBLE PRECISION', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000272L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDoubleMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeDoubleMySqlToRedShift)

        SystemEntity dataTypeRealMySqlToRedShift = new SystemEntity(key: 'REAL', value: 'DOUBLE PRECISION', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000273L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeRealMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeRealMySqlToRedShift)

        SystemEntity dataTypeDecimalMySqlToRedShift = new SystemEntity(key: 'DECIMAL', value: 'DECIMAL', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000274L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDecimalMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeDecimalMySqlToRedShift)

        SystemEntity dataTypeNumericMySqlToRedShift = new SystemEntity(key: 'NUMERIC', value: 'DECIMAL', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000275L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeNumericMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeNumericMySqlToRedShift)

        SystemEntity dataTypeDateMySqlToRedShift = new SystemEntity(key: 'DATE', value: 'DATE', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000276L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDateMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeDateMySqlToRedShift)

        SystemEntity dataTypeDateTimeMySqlToRedShift = new SystemEntity(key: 'DATETIME', value: 'TIMESTAMP', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000277L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeDateTimeMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeDateTimeMySqlToRedShift)

        SystemEntity dataTypeTimeStampMySqlToRedShift = new SystemEntity(key: 'TIMESTAMP', value: 'TIMESTAMP', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000278L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTimeStampMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeTimeStampMySqlToRedShift)

        SystemEntity dataTypeTimeMySqlToRedShift = new SystemEntity(version: 0, key: 'TIME', value: '[Unsupported Data Type:TIME]', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000293L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTimeMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeTimeMySqlToRedShift)

        SystemEntity dataTypeCharMySqlToRedShift = new SystemEntity(key: 'CHAR', value: 'VARCHAR', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000279L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeCharMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeCharMySqlToRedShift)

        SystemEntity dataTypeVarCharMySqlToRedShift = new SystemEntity(key: 'VARCHAR', value: 'VARCHAR', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000280L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeVarCharMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeVarCharMySqlToRedShift)

        SystemEntity dataTypeBlobMySqlToRedShift = new SystemEntity(version: 0, key: 'BLOB', value: '[Unsupported Data Type:BLOB]', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000294L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeBlobMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeBlobMySqlToRedShift)

        SystemEntity dataTypeTinyBlobMySqlToRedShift = new SystemEntity(version: 0, key: 'TINYBLOB', value: '[Unsupported Data Type:TINYBLOB]', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000295L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTinyBlobMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeTinyBlobMySqlToRedShift)

        SystemEntity dataTypeMediumBlobMySqlToRedShift = new SystemEntity(version: 0, key: 'MEDIUMBLOB', value: '[Unsupported Data Type:MEDIUMBLOB]', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000296L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeMediumBlobMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeMediumBlobMySqlToRedShift)

        SystemEntity dataTypeLongBlobMySqlToRedShift = new SystemEntity(version: 0, key: 'LONGBLOB', value: '[Unsupported Data Type:LONGBLOB]', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000297L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeLongBlobMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeLongBlobMySqlToRedShift)

        SystemEntity dataTypeTextMySqlToRedShift = new SystemEntity(key: 'TEXT', value: 'VARCHAR', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000281L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTextMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeTextMySqlToRedShift)

        SystemEntity dataTypeTinyTextMySqlToRedShift = new SystemEntity(key: 'TINYTEXT', value: 'VARCHAR', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000282L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeTinyTextMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeTinyTextMySqlToRedShift)

        SystemEntity dataTypeMediumTextMySqlToRedShift = new SystemEntity(key: 'MEDIUMTEXT', value: 'VARCHAR', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000283L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeMediumTextMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeMediumTextMySqlToRedShift)

        SystemEntity dataTypeLongTextMySqlToRedShift = new SystemEntity(key: 'LONGTEXT', value: 'VARCHAR', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000284L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeLongTextMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeLongTextMySqlToRedShift)

        SystemEntity dataTypeYearMySqlToRedShift = new SystemEntity(key: 'YEAR', value: 'SMALLINT', type: dplSystemEntityCacheService.SYS_ENTITY_TYPE_DATA_TYPE_MAPPING_FROM_MYSQL_TO_REDSHIFT, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 140000286L, createdOn: new Date(), createdBy: systemUserId, updatedBy: 0)
        dataTypeYearMySqlToRedShift.id = getSystemEntityId(companyId)
        create(dataTypeYearMySqlToRedShift)
    }

    /**
     * Method to create default mail configuration type for application
     */
    private void createDefaultMailConfigurationForApp(long companyId, long sysUserId) {
        long pluginId = PluginConnector.PLUGIN_ID
        SystemEntity emailFrom = new SystemEntity(version: 0, key: 'Email From', value: 'noreply@athena.com.bd', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MAIL_CONFIGURATION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000306L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        emailFrom.id = getSystemEntityId(companyId)
        create(emailFrom)

        SystemEntity smtpHost = new SystemEntity(version: 0, key: 'Smtp Host', value: 'smtp.gmail.com', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MAIL_CONFIGURATION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000307L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        smtpHost.id = getSystemEntityId(companyId)
        create(smtpHost)

        SystemEntity smtpPort = new SystemEntity(version: 0, key: 'Smtp Port', value: '465', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MAIL_CONFIGURATION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000308L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        smtpPort.id = getSystemEntityId(companyId)
        create(smtpPort)

        SystemEntity smtpEmail = new SystemEntity(version: 0, key: 'Smtp Email', value: 'noreply.athenamis@gmail.com', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MAIL_CONFIGURATION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000309L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        smtpEmail.id = getSystemEntityId(companyId)
        create(smtpEmail)

        SystemEntity smtpPwd = new SystemEntity(version: 0, key: 'Smtp Password', value: 'athena@123', type: appSystemEntityCacheService.SYS_ENTITY_TYPE_MAIL_CONFIGURATION, isActive: true, companyId: companyId, pluginId: pluginId, reservedId: 10000310L, createdOn: new Date(), createdBy: sysUserId, updatedBy: 0)
        smtpPwd.id = getSystemEntityId(companyId)
        create(smtpPwd)
    }

    private static final String SQL_SEQ_SYSTEM_ENTITY = """
        CREATE SEQUENCE system_entity_id_seq
        INCREMENT 1
        MINVALUE 1
        MAXVALUE 9223372036854775807
        START 1
        CACHE 1;
    """

    @Override
    public void createDefaultSchema() {
        String keyIndex = "create unique index system_entity_key_type_reserved_id_company_id_idx on system_entity(lower(key),type,reserved_id,company_id);"
        executeSql(keyIndex)
        executeSql(SQL_SEQ_SYSTEM_ENTITY)
    }

    @Override
    public boolean createTestData(long companyId, long systemUserId) { return true }

    // create default data for application
    public boolean createDefaultDataForApp(long companyId, long sysUserId) {
        try {
            createDefaultContentEntityTypeForApp(companyId, sysUserId)
            createDefaultNoteEntityTypeForApp(companyId, sysUserId)
            createDefaultItemCategoryForApp(companyId, sysUserId)
            createDefaultContentTypeForApp(companyId, sysUserId)
            createDefaultOwnerTypeForApp(companyId, sysUserId)
            createDefaultValuationTypeForApp(companyId, sysUserId)
            createDefaultDataUserEntityTypeForApp(companyId, sysUserId)
            createDefaultDataGenderForApp(companyId, sysUserId)
            createDefaultDataAppNoteTypeForApp(companyId, sysUserId)
            createDefaultDataBenchmarkTypeForApp(companyId, sysUserId)
            createDefaultDataForDocDatabaseVendor(companyId, sysUserId)
            createDefaultDataForAppOSVendor(companyId, sysUserId)
            createDefaultScheduleType(companyId, sysUserId)
            createDefaultDataTransactionLogTypeForApp(companyId, sysUserId)
            createDefaultDbObjectType(companyId, sysUserId)
            createDefaultScriptType(companyId, sysUserId)
            createDefaultQueryTypeForApp(companyId, sysUserId)
            createDefaultDbInstanceTypeForApp(companyId, sysUserId)
            createDefaultMailConfigurationForApp(companyId, sysUserId)
            createDefaultSupplierEntityTypeForApp(companyId, sysUserId)
            createDefaultUnitForApp(companyId, sysUserId)
            createDefaultMimiTypeForApp(companyId, sysUserId)
            createDefaultFaqEntityTypeForApp(companyId, sysUserId)
            createDefaultHierarchyTypeForApp(companyId, sysUserId)
            createDefaultPageTypeForApp(companyId, sysUserId)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }

    // create default data for budget
    public boolean createDefaultDataForBudg(long companyId, long sysUserId, def budgSystemEntityCacheService) {
        try {
            createDefaultBudgetTaskStatus(companyId, sysUserId, budgSystemEntityCacheService)
            return true
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(ex)
        }
    }
}