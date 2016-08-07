package com.athena.mis.application.entity

/**
 * <p><strong>Procedure to create a new System Entity.</strong></p>
 * 1. ID = companyId+sequence(7 digits ) (ex: companyId=1, sequence=0003000, id=10003000).
 * 2. Add new System Entity to 'SystemEntityService'.
 *   i. add system entity type to allowed method for corresponding type.
 *      e.g - 'createDefaultInventoryType()' allowed for inventory type.
 * 3. If system entity is reserved...
 *   i. go to 'ReservedSystemEntity.groovy' for details information about reserved system entity.
 *   ii. declare constant of reserved system entity in corresponding system entity cache service.
 * 4. Add new module for SystemEntity Type to... .. .
 *   i. 'CreateSystemEntityActionService'(N/A if already exists).
 *   ii. 'UpdateSystemEntityActionService'(N/A if already exists).
 *   iii. 'DeleteSystemEntityActionService'(N/A if already exists).
 *   iv. Association check for update & delete.
 *   v. 'SystemEntityDropDownTagLib'(N/A if already exists). [Used for DropDown]
 *   - location: GetDropDownSystemEntityTagLibActionService -- >> listSystemEntity().
 *   vi. 'SystemEntityByReservedTagLib'(N/A if already exists).
 * 5. Script should be written for new system entity for existing Database.
 */

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> Contains the SystemEntity data of a Company.
 * SystemEntity has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Foreign Reference:</strong> Other domain, which has foreign key reference of SystemEntity:</p>
 *
 * <p>
 * <strong>Type:</strong> User Mapping Entity</br>
 * Refer to: {@link com.athena.mis.application.service.AppSystemEntityCacheService#SYS_ENTITY_TYPE_USER_ENTITY}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.AppUserEntity#entityTypeId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Content</br>
 * Refer to: {@link com.athena.mis.application.service.AppSystemEntityCacheService#SYS_ENTITY_TYPE_CONTENT}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.ContentCategory#contentTypeId}</li>
 *     <li>{@link com.athena.mis.application.entity.AppAttachment#contentTypeId (redundent)}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Content Entity</br>
 * Refer to: {@link com.athena.mis.application.service.AppSystemEntityCacheService#SYS_ENTITY_TYPE_CONTENT_ENTITY}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.AppAttachment#entityTypeId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Note Entity</br>
 * Refer to: {@link com.athena.mis.application.service.AppSystemEntityCacheService#SYS_ENTITY_TYPE_NOTE_ENTITY}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link AppNote#entityTypeId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Item Category</br>
 * Refer to: {@link com.athena.mis.application.service.AppSystemEntityCacheService#SYS_ENTITY_TYPE_ITEM_CATEGORY}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.ItemType#categoryId}</li>
 *     <li>{@link com.athena.mis.application.entity.Item#categoryId (redundent)}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Inventory Valuation</br>
 * Refer to: {@link com.athena.mis.application.service.AppSystemEntityCacheService#SYS_ENTITY_TYPE_VALUATION}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.Item#valuationTypeId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Supplier</br>
 * Refer to: {@link com.athena.mis.application.service.AppSystemEntityCacheService#SYS_ENTITY_TYPE_SUPPLIER}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.Supplier#supplierTypeId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Unit</br>
 * Refer to: {@link com.athena.mis.application.service.AppSystemEntityCacheService#SYS_ENTITY_TYPE_UNIT}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.budget.entity.BudgBudget#unitId}</li>
 *     <li>{@link com.athena.mis.budget.model.BudgBudgetProjectModel#unitName}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Schedule Type</br>
 * Refer to: {@link com.athena.mis.application.service.AppSystemEntityCacheService#SYS_ENTITY_TYPE_SCHEDULE}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.AppSchedule#scheduleTypeId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Budget Task Status</br>
 * Refer to: {@link com.athena.mis.budget.service.BudgSystemEntityCacheService#SYS_ENTITY_TYPE_BUDG_TASK_STATUS}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.budget.entity.BudgTask#statusId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Payment Method</br>
 * Refer to: {@link com.athena.mis.application.service.AppSystemEntityCacheService#SYS_ENTITY_TYPE_PAYMENT_METHOD}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.procurement.entity.ProcPurchaseOrder#paymentMethodId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Acc Voucher</br>
 * Refer to: {@link com.athena.mis.budget.service.AccSystemEntityCacheService#SYS_ENTITY_TYPE_ACC_VOUCHER}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.accounting.entity.AccVoucher#voucherTypeId}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccCancelledVoucher#voucherTypeId}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccVoucherTypeCoa#accVoucherTypeId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Acc Instrument</br>
 * Refer to: {@link com.athena.mis.budget.service.AccSystemEntityCacheService#SYS_ENTITY_TYPE_ACC_INSTRUMENT}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.accounting.entity.AccVoucher#instrumentTypeId}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccCancelledVoucher#instrumentTypeId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Acc Source</br>
 * Refer to: {@link com.athena.mis.budget.service.AccSystemEntityCacheService#SYS_ENTITY_TYPE_ACC_SOURCE}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.accounting.entity.AccChartOfAccount#accSourceId}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccVoucherDetails#sourceTypeId (redundent)}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccCancelledVoucherDetails#sourceTypeId (redundent)}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Supplier</br>
 * Refer to: {@link com.athena.mis.application.service.AppSystemEntityCacheService#SYS_ENTITY_TYPE_SUPPLIER}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.accounting.entity.AccChartOfAccount#sourceCategoryId}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccVoucherDetails#sourceCategoryId (redundent)}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccCancelledVoucherDetails#sourceCategoryId (redundent)}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Inventory</br>
 * Refer to: {@link com.athena.mis.inventory.service.InvSystemEntityCacheService#SYS_ENTITY_TYPE_INVENTORY}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.inventory.entity.InvInventory#typeId}</li>
 *     <li>{@link com.athena.mis.inventory.entity.InvInventoryTransaction#inventoryTypeId (redundent)}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Inventory Transaction</br>
 * Refer to: {@link com.athena.mis.inventory.service.InvSystemEntityCacheService#SYS_ENTITY_TYPE_INV_TRANSACTION}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.inventory.entity.InvInventoryTransaction#transactionTypeId}</li>
 *     <li>{@link com.athena.mis.inventory.entity.InvInventoryTransaction#transactionTypeId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Inventory Transaction Entity</br>
 * Refer to: {@link com.athena.mis.inventory.service.InvSystemEntityCacheService#SYS_ENTITY_TYPE_INV_TRANSACTION_ENTITY}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.inventory.entity.InvInventoryTransaction#transactionEntityTypeId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Inventory Production Item</br>
 * Refer to: {@link com.athena.mis.inventory.service.InvSystemEntityCacheService#SYS_ENTITY_TYPE_INV_PRODUCTION_LINE}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.inventory.entity.InvProductionDetails#productionItemTypeId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Gender</br>
 * Refer to: {@link com.athena.mis.application.service.AppSystemEntityCacheService#SYS_ENTITY_TYPE_GENDER}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhCustomer#genderId}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhCustomerTrace#genderId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Acceptance Criteria Status</br>
 * Refer to: {@link com.athena.mis.projecttrack.service.PtSystemEntityCacheService#SYS_ENTITY_TYPE_PT_ACCEPTANCE_CRITERIA_STATUS}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.projecttrack.entity.PtAcceptanceCriteria#statusId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Acceptance Criteria Type</br>
 * Refer to: {@link com.athena.mis.projecttrack.service.PtSystemEntityCacheService#SYS_ENTITY_TYPE_PT_ACCEPTANCE_CRITERIA}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.projecttrack.entity.PtAcceptanceCriteria#type}</li>
 *     <li>{@link com.athena.mis.projecttrack.entity.PtChangeRequest#type}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Backlog Status</br>
 * Refer to: {@link com.athena.mis.projecttrack.service.PtSystemEntityCacheService#SYS_ENTITY_TYPE_PT_BACKLOG_STATUS}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.projecttrack.entity.PtBacklog#statusId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Backlog Priority</br>
 * Refer to: {@link com.athena.mis.projecttrack.service.PtSystemEntityCacheService#SYS_ENTITY_TYPE_PT_BACKLOG_PRIORITY}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.projecttrack.entity.PtBacklog#priorityId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Bug Status</br>
 * Refer to: {@link com.athena.mis.projecttrack.service.PtSystemEntityCacheService#SYS_ENTITY_TYPE_PT_BUG_STATUS}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.projecttrack.entity.PtBug#status}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Bug Type</br>
 * Refer to: {@link com.athena.mis.projecttrack.service.PtSystemEntityCacheService#SYS_ENTITY_TYPE_PT_BUG}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.projecttrack.entity.PtBug#type}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Bug Severity</br>
 * Refer to: {@link com.athena.mis.projecttrack.service.PtSystemEntityCacheService#SYS_ENTITY_TYPE_PT_BUG_SEVERITY}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.projecttrack.entity.PtBug#severity}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Sprint Status</br>
 * Refer to: {@link com.athena.mis.projecttrack.service.PtSystemEntityCacheService#SYS_ENTITY_TYPE_PT_SPRINT_STATUS}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.projecttrack.entity.PtSprint#statusId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Exchange house task status</br>
 * Refer to: {@link com.athena.mis.exchangehouse.service.ExhSystemEntityCacheService#SYS_ENTITY_TYPE_EXH_TASK_STATUS}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTask#current_status}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTaskTrace#current_status}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Exchange house payment method</br>
 * Refer to: {@link com.athena.mis.exchangehouse.service.ExhSystemEntityCacheService#SYS_ENTITY_TYPE_EXH_PAYMENT_METHOD}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTask#paymentMethod}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTaskTrace#paymentMethod}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Exchange house paid by</br>
 * Refer to: {@link com.athena.mis.exchangehouse.service.ExhSystemEntityCacheService#SYS_ENTITY_TYPE_EXH_PAID_BY}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTask#paidBy}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTaskTrace#paidBy}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Exchange house task type</br>
 * Refer to: {@link com.athena.mis.exchangehouse.service.ExhSystemEntityCacheService#SYS_ENTITY_TYPE_EXH_TASK_TYPE}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTask#taskTypeId}</li>
 *     <li>{@link com.athena.mis.exchangehouse.entity.ExhTaskTrace#taskTypeId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Sarb currency conversion</br>
 * Refer to: {@link com.athena.mis.sarb.service.SarbSystemEntityCacheService#SYS_ENTITY_TYPE_SARB_CURRENCY_CONVERSION_TYPE}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.sarb.entity.SarbCurrencyConversion#currencyConversionTypeId}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Sarb revise task status</br>
 * Refer to: {com.athena.mis.sarb.service.SarbSystemEntityCacheService#SYS_ENTITY_TYPE_SARB_TASK_REVISE_STATUS}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.sarb.entity.SarbTaskDetails#reviseStatus}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> Sarb current task status</br>
 * Refer to: {@link com.athena.mis.sarb.service.SarbSystemEntityCacheService#SYS_ENTITY_TYPE_SARB_TASK_CURRENT_STATUS}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.sarb.entity.SarbTaskDetails#currentStatus}</li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> ARMS process type</br>
 * Refer to: {@link com.athena.mis.arms.service.RmsSystemEntityCacheService#SYS_ENTITY_TYPE_ARMS_PROCESS}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.arms.entity.RmsProcessInstrumentMapping#processType}</li>
 *     <li>{@link com.athena.mis.arms.entity.RmsTask#processType}</li>
 * </ul>
 *
 * <p><strong>Cross Reference:</strong> many-to-many reference with ARMS process type:</p>
 * <ul>
 *     <li>{@link com.athena.mis.arms.service.RmsSystemEntityCacheService#SYS_ENTITY_TYPE_ARMS_PROCESS} VS {@link com.athena.mis.arms.service.RmsSystemEntityCacheService#SYS_ENTITY_TYPE_ARMS_INSTRUMENT} </br>
 *     ARMS process type has many ARMS instrument type in {@link com.athena.mis.arms.entity.RmsProcessInstrumentMapping#instrumentType} </br>
 *     ARMS instrument type has many ARMS process type in {@link com.athena.mis.arms.entity.RmsProcessInstrumentMapping#processType}
 *     </li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> ARMS instrument type</br>
 * Refer to: {@link com.athena.mis.arms.service.RmsSystemEntityCacheService#SYS_ENTITY_TYPE_ARMS_INSTRUMENT}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.arms.entity.RmsProcessInstrumentMapping#instrumentType}</li>
 *     <li>{@link com.athena.mis.arms.entity.RmsPurchaseInstrumentMapping#instrumentType}</li>
 *     <li>{@link com.athena.mis.arms.entity.RmsTask#instrumentType}</li>
 * </ul>
 *
 * <p><strong>Cross Reference:</strong> many-to-many reference with ARMS instrument type:</p>
 * <ul>
 *     <li>{@link com.athena.mis.arms.service.RmsSystemEntityCacheService#SYS_ENTITY_TYPE_ARMS_INSTRUMENT} VS {@link com.athena.mis.arms.service.RmsSystemEntityCacheService#SYS_ENTITY_TYPE_ARMS_PROCESS} </br>
 *     ARMS instrument type has many ARMS process type in {@link com.athena.mis.arms.entity.RmsProcessInstrumentMapping#processType} </br>
 *     ARMS process type has many ARMS instrument type in {@link com.athena.mis.arms.entity.RmsProcessInstrumentMapping#instrumentType}
 *     </li>
 * </ul>
 * </p>
 *
 * <p>
 * <strong>Type:</strong> ARMS task status</br>
 * Refer to: {@link com.athena.mis.arms.service.RmsSystemEntityCacheService#SYS_ENTITY_TYPE_ARMS_TASK_STATUS}</br>
 * Affected Domains are given below:
 * <ul>
 *     <li>{@link com.athena.mis.arms.entity.RmsTaskTrace#previousStatus}</li>
 *     <li>{@link com.athena.mis.arms.entity.RmsTaskTrace#currentStatus}</li>
 * </ul>
 * </p>
 *
 *
 * <ul>
 *     <li>Used in <strong> Document Plugin</strong> </li>
 *     <li>{@link com.athena.mis.document.entity.DocQuestion#answerTypeId}</li>
 *     <li>{@link com.athena.mis.document.entity.DocQuestion#difficultyLevelId}</li>
 *     <li>{@link com.athena.mis.document.entity.AppDbInstance#vendorId}</li>
 *
 *     // todo write all foreign key references for all domain in all plugins
 * </ul>
 *
 *  <ul>
 *     <li>Used in <strong> Data Pipeline Plugin</strong> </li>
 *     <li>{@link com.athena.mis.datapipeline.entity.DplOfflineDataFeedFile#statusId}</li>*
 * </ul>
 *
 * <p><strong>Local Reference:</strong> Has-a relationship with other domains:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as createdBy}</li>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as updatedBy}</li>
 *     <li>{@link com.athena.mis.application.entity.Company#id as companyId}</li>
 *     <li>{@link com.athena.mis.application.entity.SystemEntityType#id as type}</li>
 *     <li>{@link com.athena.mis.application.entity.ReservedSystemEntity#id as reservedId}</li>
 * </ul>
 *
 * <p><strong>Cross Reference:</strong> many-to-many reference with SystemEntity:</p>
 * <ul>
 *     <li>SystemEntity VS {@link com.athena.mis.accounting.entity.AccChartOfAccount} </br>
 *      Refer to: {@link com.athena.mis.budget.service.AccSystemEntityCacheService#SYS_ENTITY_TYPE_ACC_VOUCHER}</br>
 *      SystemEntity has many AccChartOfAccount in {@link com.athena.mis.accounting.entity.AccVoucherTypeCoa#coaId} </br>
 *      AccChartOfAccount has many SystemEntity in {@link com.athena.mis.accounting.entity.AccVoucherTypeCoa#accVoucherTypeId} </br>
 *     </li>
 * </ul>
 */
class SystemEntity {

    public static final String DEFAULT_SORT_FIELD = "key"

    long id              // primary key (companyId + sequence(always 7 digits) ex: companyId=1,sequence=0000157, id=10000157)
    long version         // entity version in the persistence layer
    String key           // key of SystemEntity
    String value         // value of SystemEntity
    long type            // SystemEntityType.id (payment method type, unit type etc.)
    boolean isActive     // flag for whether system entity is active or not
    long companyId       // Company.id
    long reservedId      // ReservedSystemEntity.id
    long pluginId        // id of plugin (e.g. 1 for Application, 3 for Budget etc.)
    long createdBy       // AppUser.id
    Date createdOn       // Object creation DateTime
    long updatedBy       // AppUser.id
    Date updatedOn       // Object Updated DateTime

    static constraints = {
        key(nullable: false)
        value(nullable: true)
        type(nullable: false)
        isActive(nullable: false)
        companyId(nullable: false)
        reservedId(nullable: false)
        pluginId(nullable: false)
        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedOn(nullable: true)
        updatedBy(nullable: false)
    }

    static mapping = {
        id generator: 'assigned'
        key index: 'system_entity_key_idx'
        type index: 'system_entity_type_idx'
        companyId index: 'system_entity_company_id_idx'
        reservedId index: 'system_entity_reserved_id_idx'
        pluginId index: 'system_entity_plugin_id_idx'
        createdBy index: 'system_entity_created_by_idx'
        updatedBy index: 'system_entity_updated_by_idx'

        // unique index on "key","type" and "reserved_id" using SystemEntityService.createDefaultSchema()
        // <domain_name><property_name_1><property_name_2><property_name_3>idx
    }
}
