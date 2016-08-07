package com.athena.mis.beacon.taglib

import com.athena.mis.BaseTagLibExecutor
import com.mis.beacon.taglib.GetDropDownBeaconTagLibActionService

class BeaconDropDownTagLib extends BaseTagLibExecutor{

    static namespace = "app"

    GetDropDownBeaconTagLibActionService getDropDownBeaconTagLibActionService

    /**
     * Render html select of country
     * example: <app:dropDownCountry name="countryId"}"></app:dropDownCountry>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr textMember - property of country to be shown as text member
     * @attr tabindex - component tab index
     * @attr onchange - on change event call
     * @attr hintsText - No selection text (Default is Please Select...)
     * @attr showHints - Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     * @attr data-bind - bind with kendo observable
     */
    def dropDownBeacon = { attrs, body ->
        attrs.body = body
        executeTag(getDropDownBeaconTagLibActionService, attrs)
        out << attrs.html
    }

}
