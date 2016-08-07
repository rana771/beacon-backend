package com.athena.mis.application.taglib

import com.athena.mis.BaseTagLibExecutor
import com.athena.mis.application.actions.taglib.GetDropDownBenchmarkTagLibActionService

class BenchmarkDropDownTagLib extends BaseTagLibExecutor {

    static namespace = "app"

    GetDropDownBenchmarkTagLibActionService getDropDownBenchmarkTagLibActionService

    /**
     * Render html select of benchmark
     * example: <app:dropDownBenchmark name="benchmarkId"}"></app:dropDownBenchmark>
     *
     * @attr name REQUIRED - name & id of html component
     * @attr dataModelName REQUIRED - name of dataModel of Kendo dropdownList
     * @attr class - css or validation class
     * @attr tabindex - component tab index
     * @attr hintsText - No selection text (Default is Please Select...)
     * @attr showHints - Hints-text will be shown (Default is 'true')
     * @attr defaultValue - default value to be shown as selected (Default is '')
     * @attr required - boolean value (true/false), if true append required
     * @attr validationMessage - validation message to be shown (Default is 'Required')
     */
    def dropDownBenchmark = { attrs, body ->
        attrs.body = body
        super.executeTag(getDropDownBenchmarkTagLibActionService, attrs)
        out << (String) attrs.html
    }
}
