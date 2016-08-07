<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridUserRole'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create User Role
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="userRoleFormForCompanyUser" name="userRoleFormForCompanyUser" class="form-horizontal form-widgets"
                  role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="userId" id="userId" data-bind="value: userRole.userId"/>
                    <input type="hidden" name="existingRoleId" id="existingRoleId"
                           data-bind="value: userRole.roleId"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional">User:</label>

                        <div class="col-md-3">
                            <span id="roleName">${userName}</span>
                        </div>

                        <label class="col-md-1 control-label label-required" for="pluginId">Module:</label>

                        <div class="col-md-3">
                            <app:dropDownModules
                                    data_model_name="dropDownModule"
                                    name="pluginId"
                                    id="pluginId"
                                    required="true"
                                    validationmessage="Required"
                                    tabindex="1"
                                    onchange="populateRoleList();"
                                    data_bind="value: userRole.pluginId">
                            </app:dropDownModules>
                        </div>

                        <label class="col-md-1 control-label label-required" for="roleId">Role:</label>

                        <div class="col-md-3">
                            <app:dropDownRoleForCompanyUser
                                    user_id="${userId}"
                                    data_model_name="dropDownRole"
                                    url="${createLink(controller: 'userRole', action: 'dropDownRoleForCompanyUserReload')}"
                                    id="roleId"
                                    name="roleId"
                                    required="true"
                                    validationmessage="Required"
                                    tabindex="2">
                            </app:dropDownRoleForCompanyUser>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/userRole/createForCompanyUser,/userRole/updateForCompanyUser">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="3"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="4"
                            aria-disabled="false" onclick='resetUserRoleForCompanyUserForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridUserRole"></div>
    </div>
</div>