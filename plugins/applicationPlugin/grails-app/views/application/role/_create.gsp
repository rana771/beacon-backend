<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridRole'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Role
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="roleForm" name="roleForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: role.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: role.version"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="name">Name:</label>

                        <div class="col-md-3">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                   placeholder="Unique Role Name" required validationMessage="Required"
                                   maxlength="255" autofocus data-bind="value: role.name"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/role/create,/role/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="2"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="3"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridRole"></div>
    </div>
</div>