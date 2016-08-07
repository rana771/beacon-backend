package com.athena.mis.application.model

class ListVendorForDplDashboardActionServiceModel {

    public static final String SQL_LIST_VENDOR_FOR_DPL_DASH_BOARD = """
        DROP TABLE IF EXISTS list_vendor_for_dpl_dashboard_action_service_model;
        DROP VIEW IF EXISTS list_vendor_for_dpl_dashboard_action_service_model;
        CREATE OR REPLACE VIEW list_vendor_for_dpl_dashboard_action_service_model AS
            SELECT vendor.id, vendor.version, vendor.name, vendor.description,
            vendor.driver, vendor.db_type_id, vendor.vendor_id, vendor.sequence,
            vendor.company_id
            FROM vendor
    """

    long id
    long version
    String name
    String description
    String driver
    long dbTypeId
    long vendorId
    int sequence
    long companyId

    static mapping = {
        cache usage: "read-only"
    }
}
