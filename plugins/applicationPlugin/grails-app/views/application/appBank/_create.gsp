<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridBank'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Bank
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id='bankForm' name='bankForm' class="form-horizontal form-widgets" role="form" method="post">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: bank.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: bank.version"/>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="name">Name:</label>

                        <div class="col-md-3">
                            <input type="text" class="k-textbox" id="name" name="name"
                                   data-bind="value: bank.name" tabindex="1"
                                   required validationMessage="Required"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="code">Code:</label>

                        <div class="col-md-3">
                            <input type="text" class="k-textbox" id="code" name="code"
                                   data-bind="value: bank.code" tabindex="2"
                                   required validationMessage="Required"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="code"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="countryId">Country:</label>

                        <div class="col-md-3">
                            <app:dropDownCountry dataModelName="dropDownCountry"
                                                 name="countryId"
                                                 required="true"
                                                 validationMessage="Required"
                                                 data-bind="value: bank.countryId"
                                                 tabindex="3"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="countryId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional"
                               for="isSystemBank">System Bank:</label>

                        <div class="col-md-3">
                            <input type="checkbox" id="isSystemBank" tabindex="4" name="isSystemBank"
                                        data-bind="checked: bank.isSystemBank"/>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <button name="create" id="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="5"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="6"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridBank"></div>
    </div>
</div>









