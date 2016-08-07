<div id="application_top_panel" class="panel panel-primary">
    <div class="panel-heading">
        <span class="pull-left"><i class="panel-icon fa fa-ellipsis-v"></i></span>
        <div class="panel-title">
            Search By Role
            <app:myFavourite></app:myFavourite>
        </div>
    </div>

    <form id="frmSearchRole" name="frmSearchRole" class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <g:hiddenField name="hidRole"/>
            <g:hiddenField name="hidPluginId"/>

            <div class="form-group">
                <label class="col-md-1 control-label label-required" for="roleId">Role:</label>

                <div class="col-md-3">
                    <app:dropDownRole
                            dataModelName="dropDownRole"
                            validationMessage="Required"
                            required="true"
                            name="roleId"
                            onchange="onChangeDropdownRole()"
                            tabindex="1">
                    </app:dropDownRole>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="roleId"></span>
                </div>

                <label class="col-md-1 control-label label-required" for="pluginId">Modules:</label>

                <div class="col-md-3">
                    <app:dropDownModules
                            data_model_name="dropDownModule"
                            name="pluginId"
                            id="pluginId"
                            url="${createLink(controller: 'roleModule', action: 'dropDownRoleModuleReload')}"
                            required="true"
                            validationmessage="Required"
                            tabindex="2"
                            role_id="0">
                    </app:dropDownModules>
                </div>

                <div class="col-md-2 pull-left">
                    <span class="k-invalid-msg" data-for="pluginId"></span>
                </div>

            </div>
        </div>

        <div class="panel-footer">
            <app:checkSystemUser isConfigManager="true">
                <app:ifAllUrl urls="/requestMap/listAvailableRole,/requestMap/listAssignedRole">
                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="3"
                            aria-disabled="false"><span class="k-icon k-i-search"></span>Search
                    </button>
                </app:ifAllUrl>
                <sec:access url="/requestMap/resetRequestMap">
                    <button id="reSet" name="reSet" type="button" data-role="button" class="k-button k-button-icontext"
                            role="button" tabindex="4" onclick='authenticateRequestMapReset();'
                            aria-disabled="false"><span class="k-icon k-i-restore"></span>Reset Module For Role
                    </button>
                </sec:access>
            </app:checkSystemUser>
        </div>
    </form>
</div>

