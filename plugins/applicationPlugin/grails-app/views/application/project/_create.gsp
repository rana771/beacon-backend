<%@ page import="com.athena.mis.PluginConnector; com.athena.mis.application.service.AppSystemEntityCacheService" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridProject'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Project
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="projectForm" name="projectForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: project.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: project.version"/>

                    <app:systemEntityByReserved
                            name="entityTypeId"
                            typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT_ENTITY}"
                            reservedId="${AppSystemEntityCacheService.SYS_ENTITY_CONTENT_ENTITY_PROJECT}"
                            pluginId="${PluginConnector.PLUGIN_ID}">
                    </app:systemEntityByReserved>

                    <app:systemEntityByReserved
                            name="appUserEntityTypeId"
                            typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY}"
                            reservedId="${AppSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_PROJECT}"
                            pluginId="${PluginConnector.PLUGIN_ID}">
                    </app:systemEntityByReserved>

                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="name">Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                           data-bind="value: project.name" maxlength="255"
                                           placeholder="Unique Project Name" required validationMessage="Required"
                                           autofocus/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="name"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="code">Code:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="code" name="code" tabindex="2"
                                           data-bind="value: project.code" maxlength="255"
                                           placeholder="Should be Unique" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="code"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="startDate">Start Date:</label>

                                <div class="col-md-6">
                                    <app:dateControl
                                            name="startDate"
                                            required="true"
                                            validationMessage="Required"
                                            tabindex="3"
                                            value=""
                                            data-bind="value: project.startDate">
                                    </app:dateControl>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="startDate"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="endDate">End Date:</label>

                                <div class="col-md-6">
                                    <app:dateControl
                                            name="endDate"
                                            required="true"
                                            validationMessage="Required"
                                            tabindex="4"
                                            value=""
                                            data-bind="value: project.endDate">
                                    </app:dateControl>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="endDate"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="description">Description:</label>

                                <div class="col-md-9">
                                    <textarea type="text" class="k-textbox" id="description" name="description" rows="1"
                                              tabindex="5" data-bind="value: project.description" maxlength="255"
                                              placeholder="255 Char Max"></textarea>
                                </div>
                            </div>
                        </div>

                        <app:checkSystemUser isConfigManager="true">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label class="col-md-6 control-label"
                                           for="isApproveInFromSupplier">Auto Approve(In From Supplier):</label>

                                    <div class="col-md-6">
                                        <input type="checkbox" id="isApproveInFromSupplier" tabindex="6"
                                               name="isApproveInFromSupplier"
                                               data-bind="checked: project.isApproveInFromSupplier"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-6 control-label"
                                           for="isApproveInFromInventory">Auto Approve(In From Inventory):</label>

                                    <div class="col-md-6">
                                        <input type="checkbox" id="isApproveInFromInventory" tabindex="7"
                                               name="isApproveInFromInventory"
                                               data-bind="checked: project.isApproveInFromInventory"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-6 control-label"
                                           for="isApproveInvOut">Auto Approve(Inventory Out):</label>

                                    <div class="col-md-6">
                                        <input type="checkbox" id="isApproveInvOut" tabindex="8"
                                               name="isApproveInvOut"
                                               data-bind="checked: project.isApproveInvOut"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-6 control-label"
                                           for="isApproveConsumption">Auto Approve(Consumption):</label>

                                    <div class="col-md-6">
                                        <input type="checkbox" id="isApproveConsumption" tabindex="9"
                                               name="isApproveConsumption"
                                               data-bind="checked: project.isApproveConsumption"/>
                                    </div>
                                </div>

                                <div class="form-group">
                                    <label class="col-md-6 control-label"
                                           for="isApproveProduction">Auto Approve(Production):</label>

                                    <div class="col-md-6">
                                        <input type="checkbox" id="isApproveProduction" tabindex="10"
                                               name="isApproveProduction"
                                               data-bind="checked: project.isApproveProduction"/>
                                    </div>
                                </div>
                            </div>
                        </app:checkSystemUser>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/project/create,/project/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button"
                                aria-disabled="false" tabindex="11"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button"
                            aria-disabled="false" onclick='resetForm();' tabindex="12"><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridProject"></div>
    </div>
</div>