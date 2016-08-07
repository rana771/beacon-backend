<%@ page import="com.athena.mis.application.service.AppSystemEntityCacheService; com.athena.mis.PluginConnector" %>

<div class="col-md-9 no-padding-margin">
    <div id="application_top_panel" class="panel panel-primary">
        <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridDbInstanceQuery'))">
            <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

            <div class="panel-title">
                DB Instance Query
                <app:myFavourite>
                </app:myFavourite>
                <app:historyBack
                        o_id="${oId ? oId : pId}"
                        url="${pUrl ? pUrl : url}">
                </app:historyBack>
            </div>
        </div>

        <form id='dbInstanceQueryForm' name='dbInstanceQueryForm' class="form-horizontal form-widgets"
              role="form" method="post">
            <div class="panel-body" style="display: none">
                <app:systemEntityByReserved
                        name="entityTypeId"
                        reservedId="${AppSystemEntityCacheService.SYS_ENTITY_NOTE_DB_QUERY}"
                        typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_NOTE_ENTITY}"
                        pluginId="${PluginConnector.PLUGIN_ID}">
                </app:systemEntityByReserved>

                <input type="hidden" name="id" id="id" data-bind="value: dbInstanceQuery.id"/>
                <input type="hidden" name="version" id="version" data-bind="value: dbInstanceQuery.version"/>
                <input type="hidden" name="dbInstanceId" id="dbInstanceId"
                       data-bind="value: dbInstanceQuery.dbInstanceId"/>

                <div class="form-group">
                    <label class="col-md-2 control-label">Database:</label>

                    <div class="col-md-4">
                        <span id="schemaName">${appDbInstance ? appDbInstance.dbName : ""}</span>
                    </div>
                    <label class="col-md-1 control-label">Vendor:</label>

                    <div class="col-md-5">
                        <span id="vendorName">${vendorName ? vendorName : ""}</span>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="name">Name:</label>

                    <div class="col-md-10">
                        <input type="text" class="k-textbox" id="name" name="name"
                               data-bind="value: dbInstanceQuery.name" maxlength="255"
                               tabindex="1" placeholder="Unique Name (Max 255 char)" required
                               validationMessage="Required"/>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="name">Result Per Page:</label>

                    <div class="col-md-3">
                        <input type="text" id="resultPerPage" name="resultPerPage" tabindex="2"
                               data-bind="value: dbInstanceQuery.resultPerPage"
                               placeholder="Result per page" required
                               validationMessage="Required"/>
                    </div>

                    <div class="col-md-2 pull-left">
                        <span class="k-invalid-msg" data-for="resultPerPage"></span>
                    </div>

                    <label class="col-md-2 control-label label-optional" for="queryTypeId">Query Type:</label>

                    <div class="col-md-3">
                        <app:dropDownSystemEntity
                                dataModelName="dropDownQueryType"
                                data-bind="value: dbInstanceQuery.queryTypeId"
                                name="queryTypeId"
                                tabindex="3"
                                typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_QUERY}"
                                pluginId="${PluginConnector.PLUGIN_ID}">
                        </app:dropDownSystemEntity>
                    </div>
                </div>

                <div class="form-group">
                    <label class="col-md-2 control-label label-required" for="sqlQuery">SQL:</label>

                    <div class="col-md-10">
                        <textarea class="k-textbox" id="sqlQuery" name="sqlQuery"
                                  data-bind="value: dbInstanceQuery.sqlQuery"
                                  tabindex="4" rows="4"
                                  placeholder="Enter query e.g. [SELECT * FROM my_table ]"
                                  required validationMessage="Required" maxlength="5000"/>
                    </div>
                </div>
            </div>

            <div class="panel-footer" style="display: none">
                <button name="create" id="create" type="submit" data-role="button"
                        class="k-button k-button-icontext"
                        role="button" tabindex="5"
                        aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                </button>

                <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                        class="k-button k-button-icontext" role="button" tabindex="6"
                        aria-disabled="false" onclick='resetDbInstanceQueryForm();'><span
                        class="k-icon k-i-close"></span>Cancel
                </button>

                <button id="executeButton" name="executeButton" type="button" data-role="button"
                        class="k-button k-button-icontext pull-right" role="button" tabindex="7"
                        aria-disabled="false" onclick='executeQuery();'><span
                        class="k-icon k-i-tick"></span>Execute
                </button>
            </div>
        </form>
    </div>

    <div id="gridDbInstanceQuery"></div>
</div>

<div class="col-md-3 no-right-padding">
    <div id="gridDbTableList"></div>
</div>
