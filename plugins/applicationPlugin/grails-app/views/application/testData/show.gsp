<%@ page import="com.athena.mis.application.service.AppSystemEntityCacheService; com.athena.mis.PluginConnector" %>
<g:render template="/application/testData/script"/>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <span class="pull-left"><i class="panel-icon fa fa-table"></i></span>
                <div class="panel-title">
                    Search Table Information
                    <app:myFavourite>
                    </app:myFavourite>
                    <app:historyBack
                            o_id="${oId}"
                            url="${url}">
                    </app:historyBack>
                </div>
            </div>

            <form id="testDataForm" name="testDataForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body">
                    <div class="form-group">
                        <label class="col-md-2 control-label label-required"
                               for="dbInstanceId">DB Instance:</label>

                        <div class="col-md-3">
                            <app:dropDownAppDbInstance
                                    id="dbInstanceId"
                                    data_model_name="dropDownDbInstance"
                                    name="dbInstanceId"
                                    required="true"
                                    is_tested="true"
                                    validationMessage="Required"
                                    tabindex="1">
                            </app:dropDownAppDbInstance>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="dbInstanceId"></span>
                        </div>

                        <label class="col-md-1 control-label label-required" for="typeId">Type:</label>

                        <div class="col-md-2">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownType"
                                    required="true"
                                    validationMessage="Required"
                                    name="typeId"
                                    tabindex="2"
                                    typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_DB_OBJECT}"
                                    pluginId="${PluginConnector.PLUGIN_ID}">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="typeId"></span>
                        </div>
                    </div>
                </div>

                <div class="panel-footer">
                    <button id="search" name="search" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="3"
                            aria-disabled="false"><span class="k-icon k-i-search"></span>Search
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div class="col-md-6 no-padding-margin" style="padding-right: 5px">
            <app:testDataGrid on_delete="truncateTestData()" id="gridTestData" grid_model="gridTestData"
                              on_create="loadTestData()" on_select="onSelectTestTable">
            </app:testDataGrid>
        </div>

        <div class="col-md-6 no-padding-margin">
            <app:schemaInfoGrid grid_model="gridMetaData" id="gridMetaData">
            </app:schemaInfoGrid>
        </div>
    </div>
</div>
