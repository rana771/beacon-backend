<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridUserRole'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create User Role
                    <app:myFavourite>
                    </app:myFavourite>
                    <app:historyBack
                        o_id="${oId}"
                        url="${url}">
                    </app:historyBack>
                </div>
            </div>

            <form id="userRoleForm" name="userRoleForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="existingUserId" id="existingUserId"
                           data-bind="value: userRoleMapping.existingUserId"/>
                    <input type="hidden" name="roleId" id="roleId" data-bind="value: userRoleMapping.roleId"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional">Role:</label>

                        <div class="col-md-11">
                            <span id="roleName">${roleName}</span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="userId">User:</label>

                        <div class="col-md-3">
                            <app:dropDownAppUserRole
                                    role_id="${oId}"
                                    data_model_name="dropDownAppUserRole"
                                    url="${createLink(controller: 'userRole', action: 'dropDownAppUserForRoleReload')}"
                                    id="userId"
                                    name="userId"
                                    required="true"
                                    validationmessage="Required"
                                    tabindex="1"
                                    data-bind="value: userRoleMapping.userId">
                            </app:dropDownAppUserRole>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="userId"></span>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:checkSystemUser isPowerUser="true">
                        <app:ifAllUrl urls="/userRole/create,/userRole/update">
                            <button id="create" name="create" type="submit" data-role="button"
                                    class="k-button k-button-icontext"
                                    role="button" tabindex="2"
                                    aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                            </button>
                        </app:ifAllUrl>
                    </app:checkSystemUser>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="3"
                            aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div></div>

    <div class="row">
        <div id="gridUserRole"></div>
    </div>
</div>