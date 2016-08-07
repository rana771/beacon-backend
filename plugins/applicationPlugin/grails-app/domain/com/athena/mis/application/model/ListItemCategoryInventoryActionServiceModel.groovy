package com.athena.mis.application.model

class ListItemCategoryInventoryActionServiceModel {
    public static final String SQL_LIST_ITEM_CATEGORY_INVENTORY_MODEL = """
    DROP TABLE IF EXISTS list_item_category_inventory_action_service_model;
    DROP VIEW IF EXISTS list_item_category_inventory_action_service_model;
    CREATE OR REPLACE VIEW list_item_category_inventory_action_service_model AS
        SELECT
            item.id,
            item.version,
            item.name,
            item.code,
            item.unit,
            item.item_type_id,
            item.valuation_type_id,
            item.category_id,
            item.company_id,
            item.is_finished_product,
            it.name AS item_type_name,
            se.key AS valuation_type_name,
            item.is_dirty
        FROM Item
        LEFT JOIN item_type it ON it.id = item.item_type_id
        LEFT JOIN system_entity se ON se.id = item.valuation_type_id
    """

    long id                             // Item.id
    long version                        // Item.version
    String name                         // Item.name
    String code                         // Item.code
    String unit                         // Item.unit
    long itemTypeId                     // Item.itemTypeId
    long categoryId                     // Item.categoryId    SystemEntity.id (Inventory, NonInventory, FixedAsset)
    long valuationTypeId                // Item.valuationTypeId                SystemEntity.id (e.g : FIFO, LIFO etc)
    boolean isFinishedProduct           // is the item finished product (for item type Inventory)
    long companyId                      // Item.companyId
    String itemTypeName                 // ItemType.name
    String valuationTypeName            // SystemEntity.key matching Item.valuationType
    Boolean isDirty                     // Item.isDirty

    static mapping = {
        cache usage: "read-only"
    }
}
