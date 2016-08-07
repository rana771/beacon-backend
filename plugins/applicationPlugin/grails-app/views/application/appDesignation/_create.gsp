<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridDesignation'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Designation
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="designationForm" name="designationForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" id="id" name="id" data-bind="value: designation.id"/>
                    <input type="hidden" id="version" name="version" data-bind="value: designation.version"/>

                    <div class="form-group">
                        <div class="col-md-12">
                            <div class="form-group">
                                <div class="col-md-6">
                                    <label class="col-md-3 control-label label-required" for="name">Name:</label>

                                    <div class="col-md-6">
                                        <input type="text" class="k-textbox" id="name" name="name" maxlength="255"
                                               placeholder="Unique Designation Name" required
                                               validationMessage="Required" data-bind="value: designation.name"
                                               tabindex="1" autofocus/>
                                    </div>

                                    <div class="col-md-3 pull-left">
                                        <span class="k-invalid-msg" data-for="name"></span>
                                    </div>
                                </div>


                                <div class="col-md-6">
                                    <label class="col-md-3 control-label label-required"
                                           for="shortName">Short Name:</label>

                                    <div class="col-md-6">
                                        <input type="text" class="k-textbox" id="shortName" name="shortName"
                                               tabindex="2" data-bind="value: designation.shortName"
                                               placeholder="Unique Short Name" required validationMessage="Required"
                                               maxlength="255"/>
                                    </div>

                                    <div class="col-md-3 pull-left">
                                        <span class="k-invalid-msg" data-for="shortName"></span>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="3"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

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
        <div id="gridDesignation"></div>
    </div>
</div>

