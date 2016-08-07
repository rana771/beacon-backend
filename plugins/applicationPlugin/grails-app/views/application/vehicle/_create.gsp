<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridVehicle'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Vehicle
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="vehicleForm" name="vehicleForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: vehicle.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: vehicle.version"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="name">Name:</label>

                        <div class="col-md-4">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="1" maxlength="255"
                                   data-bind="value: vehicle.name" placeholder="Unique vehicle Name"
                                   required validationMessage="Required" autofocus/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>

                        <label class="col-md-1 control-label label-optional" for="description">Description:</label>

                        <div class="col-md-4">
                            <textarea type="text" class="k-textbox" id="description" name="description" rows="1"
                                      tabindex="2" data-bind="value: vehicle.description" maxlength="255"
                                      placeholder="255 Char Max"></textarea>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/vehicle/create,/vehicle/update">
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
        <div id="gridVehicle"></div>
    </div>
</div>




