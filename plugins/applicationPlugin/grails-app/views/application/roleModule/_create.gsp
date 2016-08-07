<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridRoleModule'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Role Module
                    <app:myFavourite>
                    </app:myFavourite>
                    <app:historyBack
                            o_id="${oId}"
                            url="${url}">
                    </app:historyBack>
                </div>
            </div>

            <form id="roleModuleForm" name="roleModuleForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: roleModule.id"/>
                    <input type="hidden" name="roleId" id="roleId" data-bind="value: roleModule.roleId"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional">Role:</label>

                        <div class="col-md-11">
                            <span id="roleName">${roleName}</span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="moduleId">Module:</label>

                        <div class="col-md-3">
                            <app:dropDownModules
                                    data_model_name="dropDownModule"
                                    id="moduleId"
                                    name="moduleId"
                                    url="${createLink(controller: 'roleModule', action: 'dropDownRoleModuleReload')}"
                                    is_mapped="true"
                                    role_id="${oId ? oId : "0"}"
                                    default_value="0"
                                    required="true"
                                    validationmessage="Required"
                                    tabindex="1"
                                    data_bind="value: roleModule.moduleId">
                            </app:dropDownModules>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="moduleId"></span>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/roleModule/create,/roleModule/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="2"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="3"
                            aria-disabled="false" onclick='resetForm();'><span class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridRoleModule"></div>
    </div>
</div>