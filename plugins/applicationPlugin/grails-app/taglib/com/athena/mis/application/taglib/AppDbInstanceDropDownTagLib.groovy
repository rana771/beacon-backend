package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownAppDbInstanceTagLibActionService
import com.athena.mis.application.actions.taglib.GetDropDownTableColumnTagLibActionService
import com.athena.mis.application.actions.taglib.GetDropDownTableTagLibActionService

class AppDbInstanceDropDownTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownAppDbInstanceTagLibActionService getDropDownAppDbInstanceTagLibActionService
    GetDropDownTableTagLibActionService getDropDownTableTagLibActionService
    GetDropDownTableColumnTagLibActionService getDropDownTableColumnTagLibActionService

    /**
     * Render html select of AppDbInstance
     * example: <app:dropDownAppDbInstance id="dbInstance" name="dbInstanceId"  data_model_name="dbInstance"></app:dropDownAppDbInstance>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr include_vendor_id - indicate which vendor only visible
     * @attr exclude_vendor_id - indicate which vendor not visible
     * @attr required - boolean value (true/false), if true append required
     * @attr validation_message - validation message to be shown (Default is 'Required')
     * @attr is_tested - if true then shows only successfully tested dbInstances; if false shows all dbInstances
     * @attr is_native - if true then shows only native db dbInstances; if false shows all dbInstances
     * @attr is_mapped - true means appServerInstance and dbInstance mapping
     * @attr is_source - 1 means Db instance type source otherwise target
     * @attr server_instance_id - if is_mapped is true then server_instance_id is required
     * @attr default_selected_value - default value to be shown as selected (Default is '') which is already exists in datasource and should not be pushed again
     */
    def dropDownAppDbInstance = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownAppDbInstanceTagLibActionService, attrs)
        out << (String) attrs.html
    }

    /**
     * Render html select of table name
     * example: <app:dropDownTable id="tableName" name="tableName"  data_model_name="tableName">  </app:dropDownTable>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name of html component
     * @attr db_instance_id REQUIRED - DbInstanceId
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr url - controller action to render this taglib
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validation_message - validation message to be shown (Default is 'Required')
     * @attr dpl_data_export_id - DplDataExport.id
     * @attr dpl_data_import_id - DplDataImport.id
     */
    def dropDownTable = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownTableTagLibActionService, attrs)
        out << (String) attrs.html
    }

    /**
     * Render html select of table name
     * example: <app:dropDownTable id="tableName" name="tableName"  data_model_name="tableName">  </app:dropDownTable>
     *
     * @attr id REQUIRED - id of html component
     * @attr name REQUIRED - name of html component
     * @attr db_instance_id REQUIRED - DbInstanceId
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text (Default is Please Select...)
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr dpl_data_export_id - id of DplDataExport
     * @attr validation_message - validation message to be shown (Default is 'Required')
     * @attr is_primary - boolean value (true/false), if true select only primary keys
     */
    def dropDownTableColumn = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownTableColumnTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
