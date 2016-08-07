package com.athena.mis.application.model

class ListItemCategoryFixedAssetActionServiceModel {

    public static final String SQL_LIST_ITEM_CATEGORY_FIX_ASSET_MODEL = """
    DROP TABLE IF EXISTS list_item_category_fixed_asset_action_service_model;
    DROP VIEW IF EXISTS list_item_category_fixed_asset_action_service_model;
    CREATE OR REPLACE VIEW list_item_category_fixed_asset_action_service_model AS
        SELECT
            item.id,
            item.version,
            item.name,
            item.code,
            item.unit,
            item.item_type_id,
            item.category_id,
            item.company_id,
            item.is_individual_entity,
            it.name AS item_type_name
        FROM Item
        LEFT JOIN item_type it ON it.id = item.item_type_id
    """
    long id                             // primary key (Auto generated by its own sequence)
    long version                        // entity version in the persistence layer
    long companyId                      // Company.id
    String name                         // Unique item name by category
    String code                         // Item Code
    String unit                         // Item Unit
    Boolean isIndividualEntity          // Item isIndividualEntity
    long categoryId                     // SystemEntity.id (Inventory, NonInventory, FixedAsset)
    long itemTypeId                     // ItemType.id
    String itemTypeName                 // ItemType.name Matching Item.item.itemTypeId

    static mapping = {
        cache usage: "read-only"
    }
}
