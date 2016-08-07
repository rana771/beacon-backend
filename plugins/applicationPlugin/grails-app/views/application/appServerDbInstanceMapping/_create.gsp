<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridServerMapping'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Add Server Mapping
                    <app:myFavourite>
                    </app:myFavourite>
                    <app:historyBack
                            o_id="${oId}"
                            url="${url}">
                    </app:historyBack>
                </div>
            </div>

            <form id="appServerMappingForm" name="appServerMappingForm" class="form-horizontal form-widgets"
                  role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" id="id" name="id" data-bind="value: appServerDbInstanceMapping.id"/>
                    <input type="hidden" id="version" name="version"
                           data-bind="value: appServerDbInstanceMapping.version"/>
                    <input type="hidden" id="appServerInstanceId" name="appServerInstanceId"
                           data-bind="value: appServerDbInstanceMapping.appServerInstanceId"/>
                    <input type="hidden" id="dbVendorId" name="dbVendorId"
                           data-bind="value: appServerDbInstanceMapping.dbVendorId"/>

                    <div class="form-group">
                        <div class="col-md-12">
                            <div class="form-group">
                                <div class="col-md-6">
                                    <label class="col-md-3 control-label label-optional"><span
                                            id="categoryLabel1" for="categoryName"></span>Server Name:</label>

                                    <div class="col-md-6">
                                        <span id="categoryName">${appServerInstanceName}</span>
                                    </div>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="col-md-6">
                                    <label class="col-md-3 control-label label-required"
                                           for="appDbInstanceId">DB Instance:</label>

                                    <div class="col-md-6">
                                        <app:dropDownAppDbInstance
                                                id="appDbInstanceId"
                                                data_model_name="dropDownDbInstance"
                                                name="appDbInstanceId"
                                                server_instance_id="${oId}"
                                                data-bind="value: appServerDbInstanceMapping.appDbInstanceId"
                                                url="${createLink(controller: 'appDbInstance', action: 'dropDownDbInstanceReload')}"
                                                is_mapped="true"
                                                default_value="0"
                                                is_tested="true"
                                                required="true"
                                                tabindex="1">
                                        </app:dropDownAppDbInstance>
                                    </div>

                                    <div class="col-md-3 pull-left">
                                        <span class="k-invalid-msg" data-for="appDbInstanceId"></span>
                                    </div>
                                </div>
                            </div>
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
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridServerMapping"></div>
    </div>
</div>