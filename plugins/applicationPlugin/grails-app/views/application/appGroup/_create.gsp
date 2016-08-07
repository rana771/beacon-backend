<%@ page import="com.athena.mis.PluginConnector; com.athena.mis.application.service.AppSystemEntityCacheService" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridAppGroup'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Group
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="appGroupForm" name="appGroupForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: appGroup.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: appGroup.version"/>

                    <app:systemEntityByReserved
                            name="entityTypeId"
                            typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_USER_ENTITY}"
                            reservedId="${AppSystemEntityCacheService.SYS_ENTITY_USER_ENTITY_GROUP}"
                            pluginId="${PluginConnector.PLUGIN_ID}">
                    </app:systemEntityByReserved>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="name">Name:</label>

                        <div class="col-md-3">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                   data-bind="value: appGroup.name" maxlength="255"
                                   placeholder="Unique Group Name" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="2"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="3"
                            aria-disabled="false" onclick='resetAppGroupForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridAppGroup"></div>
    </div>
</div>