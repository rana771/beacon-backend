<%@ page import="com.athena.mis.PluginConnector; com.athena.mis.application.service.AppSystemEntityCacheService" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary" xmlns="http://www.w3.org/1999/html">
            <div class="panel-heading expand-div panel-collapsed"
                 onclick="expandCreatePanel(this, $('#appSqlScriptGrid'), initScriptDivHeight)">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create SQL Script
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="sqlScriptForm" name="sqlScriptForm" class="form-horizontal form-widgets" role="form"
                  method="post">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: appShellScript.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: appShellScript.version"/>
                    <input type="hidden" name="pluginId" id="pluginId" data-bind="value: appShellScript.pluginId"/>

                    <app:systemEntityByReserved
                            name="scriptTypeId"
                            reservedId="${AppSystemEntityCacheService.SYS_ENTITY_SCRIPT_SQL}"
                            typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_SCRIPT}"
                            pluginId="${PluginConnector.PLUGIN_ID}">
                    </app:systemEntityByReserved>
                    <app:systemEntityByReserved
                            name="entityTypeId"
                            reservedId="${AppSystemEntityCacheService.SYS_ENTITY_NOTE_SCRIPT}"
                            typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY}"
                            pluginId="${PluginConnector.PLUGIN_ID}">
                    </app:systemEntityByReserved>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="name">Name:</label>

                        <div class="col-md-3">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="1" maxlength="255"
                                   data-bind="value: appShellScript.name"
                                   placeholder="Name" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>

                        <label class="col-md-2 control-label label-required"
                               for="serverInstanceId">DB Instance:</label>

                        <div class="col-md-2">
                            <app:dropDownAppDbInstance
                                    id="serverInstanceId"
                                    data_model_name="dbInstance"
                                    name="serverInstanceId"
                                    data-bind="value: appShellScript.serverInstanceId"
                                    required="true"
                                    is_tested="true"
                                    tabindex="5">
                            </app:dropDownAppDbInstance>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="serverInstanceId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="script">SQL:</label>

                        <div class="col-md-11">
                            <textarea class="k-textbox" id="script" name="script" tabindex="3" rows="8"
                                      data-bind="value: appShellScript.script"
                                      placeholder="Max 1500 characters" required validationMessage="Required"
                                      maxlength="1500">
                            </textarea>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <button name="create" id="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="4"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="5"
                            aria-disabled="false" onclick='clearShellScriptForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div class="col-md-6 no-padding-margin" id="gridContainer" style="padding-right: 10px">
            <div id="appSqlScriptGrid"></div>
        </div>

        <div class="col-md-6 no-padding-margin">
            <textarea class="k-textbox" id="evaluatedSqlScript" name="evaluatedSqlScript" style="width: 100%"
                      placeholder="Output window" readonly></textarea>
        </div>
    </div>
</div>



