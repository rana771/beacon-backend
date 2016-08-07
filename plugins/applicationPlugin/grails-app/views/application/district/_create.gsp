<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridDistrict'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create District
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form name='districtForm' id='districtForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <g:hiddenField name="id" data-bind="value: district.id"/>
                    <g:hiddenField name="version" id="version" data-bind="value: district.version"/>
                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="name">Name:</label>

                        <div class="col-md-3">
                            <input type="text" class="k-textbox" id="name" name="name"
                                   data-bind="value: district.name" tabindex="1"
                                   placeholder="" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="countryId">Country:</label>

                        <div class="col-md-3">
                            <app:dropDownCountry dataModelName="dropDownCountry"
                                                 name="countryId"
                                                 required="true"
                                                 validationMessage="Required"
                                                 data-bind="value: district.countryId"
                                                 tabindex="2"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="countryId"></span>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/district/create,/district/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="3"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="4"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridDistrict"></div>
    </div>
</div>