<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridCompany'))">
        <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

        <div class="panel-title">
            Create Company
        </div>
    </div>

    <div id="createCompanyPanel" class="panel-body" style="display: none; height: 290px">

        <ul class="nav nav-tabs">
            <li class="active"><a href="#fragment-1" data-toggle="tab">Company</a></li>
            <li><a href="#fragment-2" data-toggle="tab">Country</a></li>
            <li><a href="#fragment-3" data-toggle="tab">Currency</a></li>
        </ul>

        <div class="tab-content">
            <div class="tab-pane active" id="fragment-1">
                <g:render template='/application/company/companyForm'/>
            </div>

            <div class="tab-pane" id="fragment-2">
                <g:render template='/application/company/countryForm'/>
            </div>

            <div class="tab-pane" id="fragment-3">
                <g:render template='/application/company/currencyForm'/>
            </div>
        </div>
    </div>

    <div class="panel-footer" style="display: none">
        <app:ifAllUrl urls="/company/create,/company/updateForReseller">
            <button id="create" name="create" type="submit" data-role="button"
                    class="k-button k-button-icontext" onclick="onSubmitCompany();"
                    role="button" tabindex="18"
                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
            </button>
        </app:ifAllUrl>

        <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                class="k-button k-button-icontext" role="button" tabindex="19"
                aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
        </button>
    </div>
</div>

<div id="gridCompany"></div>

<g:render template='/application/company/scriptForReseller'/>


