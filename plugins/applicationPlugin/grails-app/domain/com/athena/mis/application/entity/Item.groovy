package com.athena.mis.application.entity

/**
 * <p>
 * <strong>Module:</strong> Application </br>
 * <strong>Purpose:</strong> Items are required to do the works of a Project.
 * Item has association with few other domains as listed below.
 * </p>
 *
 * <p><strong>Foreign Reference:</strong> Other domain, which has foreign key reference of Item:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.SupplierItem#itemId}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccVoucherDetails#sourceId}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccCancelledVoucherDetails#sourceId}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccLc#itemId}</li>
 *     <li>{@link com.athena.mis.accounting.entity.AccLeaseAccount#itemId}</li>
 *     <li>{@link com.athena.mis.budget.entity.BudgBudgetDetails#itemId}</li>
 *     <li>{@link com.athena.mis.budget.entity.BudgSchema#itemId}</li>
 *     <li>{@link com.athena.mis.fixedasset.entity.FxdCategoryMaintenanceType#itemId}</li>
 *     <li>{@link com.athena.mis.fixedasset.entity.FxdFixedAssetDetails#itemId}</li>
 *     <li>{@link com.athena.mis.fixedasset.entity.FxdFixedAssetTrace#itemId}</li>
 *     <li>{@link com.athena.mis.fixedasset.entity.FxdMaintenance#itemId}</li>
 *     <li>{@link com.athena.mis.inventory.entity.InvInventoryTransactionDetails#itemId}</li>
 *     <li>{@link com.athena.mis.inventory.entity.InvProductionDetails#materialId}</li>
 *     <li>{@link com.athena.mis.procurement.entity.ProcPurchaseRequestDetails#itemId}</li>
 *     <li>{@link com.athena.mis.procurement.entity.ProcPurchaseOrderDetails#itemId}</li>
 *     <li>{@link com.athena.mis.procurement.entity.ProcCancelledPODetails#itemId}</li>
 * </ul>
 *
 * <p><strong>Local Reference:</strong> Has-a relationship with other domains:</p>
 * <ul>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as createdBy}</li>
 *     <li>{@link com.athena.mis.application.entity.AppUser#id as updatedBy}</li>
 *     <li>{@link com.athena.mis.application.entity.Company#id as companyId}</li>
 *     <li>{@link com.athena.mis.application.entity.ItemType#id as itemTypeId}</li>
 *     <li>{@link com.athena.mis.application.entity.SystemEntity#id as categoryId}</li>
 *     <li>{@link com.athena.mis.application.entity.SystemEntity#id as valuationTypeId}</li>
 * </ul>
 *
 * <p><strong>Cross Reference:</strong> many-to-many reference with Project:</p>
 * <ul>
 *     <li>Item VS {@link com.athena.mis.fixedasset.entity.FxdMaintenanceType} </br>
 *      FxdMaintenanceType has many Item in {@link com.athena.mis.fixedasset.entity.FxdCategoryMaintenanceType#itemId} </br>
 *      Item has many FxdMaintenanceType in {@link com.athena.mis.fixedasset.entity.FxdCategoryMaintenanceType#maintenanceTypeId} </br>
 *     </li>
 * </ul>
 *
 */
class Item {

    public static final String DEFAULT_SORT_FIELD = "name"
    // Delete query for deleting test data of item domain
    public static final String DELETE_TEST_DATA_QUERY = """ DELETE FROM item WHERE id < 0 and company_id = :companyId """

    long id                             // primary key (Auto generated by its own sequence)
    long version                         // entity version in the persistence layer
    String name                         // Unique item name by category
    String code                         // Item Code
    String unit                         // Item Unit
    long itemTypeId                     // ItemType.id
    long categoryId                     // SystemEntity.id (Inventory, NonInventory, FixedAsset)
    long valuationTypeId                // SystemEntity.id (e.g : FIFO, LIFO etc)
    boolean isIndividualEntity          // flag for individual entity (for item type Fixed Asset)
    boolean isFinishedProduct           // is the item finished product (for item type Inventory)
    long companyId                      // Company.id
    long createdBy                      // AppUser.id
    Date createdOn                      // Object creation DateTime
    long updatedBy                      // AppUser.id
    Date updatedOn                      // Object Updated DateTime
    boolean isDirty                     // true = if items valuation type or overhead cost changes; otherwise false

    static mapping = {
        id generator: 'sequence', params: [sequence: 'item_id_seq']
        name index: 'item_name_idx'
        itemTypeId index: 'item_item_type_id_idx'
        valuationTypeId index: 'item_valuation_type_id_idx'
        companyId index: 'item_company_id_idx'
        createdBy index: 'item_created_by_idx'
        updatedBy index: 'item_updated_by_idx'

        // unique index on "name" and "category_id" using ItemService.createDefaultSchema()
        // <domain_name><property_name_1><property_name_2>idx
    }

    static constraints = {
        name(nullable: false)
        code(nullable: false)
        unit(nullable: false)
        itemTypeId(nullable: false)
        categoryId(nullable: false)
        valuationTypeId(nullable: false)
        isIndividualEntity(nullable: false)
        isFinishedProduct(nullable: false)
        companyId(nullable: false)
        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
    }

    public String toString() {
        return this.name
    }
}
