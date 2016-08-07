package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownDistrictTagLibActionService

class DistrictDropDownTagLib extends BaseTagLibExecutor {
    static namespace = "app"

    GetDropDownDistrictTagLibActionService getDropDownDistrictTagLibActionService

    /**
     * Render html select of district filtered
     * example: <app:dropDownDistrict name ="districtId"></app:dropDownDistrict>
     * @attr name REQUIRED - name of html component
     * @attr bank_id - bank id to filter district by bank,0 to get all districts
     * @attr country_id - country id to filter district by country
     * @attr show_hints - Hints-text will be shown (Default is 'true')
     * @attr id REQUIRED - id of html component
     * @attr data_model_name REQUIRED - name of dataModel of Kendo dropDownList
     * @attr tab_index - component tab index
     * @attr onchange - on change event call
     * @attr hints_text - No selection text(Default is Please Select....)
     * @attr default_value - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationmessage- validation message to be shown (Default is 'Required')
     * @attr url - url for data source
     * @attr process - reserved id of RmsProcessType
     * @attr instrument - reserved id of RmsInstrumentType
     * @attr data-bind - bind data for kendo
     */

    def dropDownDistrict = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownDistrictTagLibActionService, attrs)
        out << (String) attrs.html

    }
}
