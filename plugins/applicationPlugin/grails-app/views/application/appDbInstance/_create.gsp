<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridDbInstance'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create DB Instance
                    <app:myFavourite>
                    </app:myFavourite>
                    <app:historyBack
                            o_id="${oId}"
                            url="${url}">
                    </app:historyBack>
                </div>
            </div>

            <form id="dbInstanceForm" name="dbInstanceForm" class="form-horizontal form-widgets" role="form"
                  method="post">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: appDbInstance.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: appDbInstance.version"/>
                    <input type="hidden" name="reservedVendorId" id="reservedVendorId"
                           data-bind="value: appDbInstance.reservedVendorId"/>

                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="name">Instance Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                           placeholder="DB Instance Name (e.g. Postgres)"
                                           data-bind="value: appDbInstance.name" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="name"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="vendorId">Vendor:</label>

                                <div class="col-md-6">
                                    <app:dropDownVendor
                                            data_model_name="dropDownVendor"
                                            required="true"
                                            validation_message="Required"
                                            data-bind="value: appDbInstance.vendorId"
                                            id="vendorId"
                                            name="vendorId"
                                            tabindex="2">
                                    </app:dropDownVendor>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="vendorId"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="url">URL:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="url" name="url" tabindex="3"
                                           placeholder="e.g. jdbc:mysql://192.168.1.1"
                                           data-bind="value: appDbInstance.url" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="url"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="schemaName">Schema Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="schemaName" name="schemaName" tabindex="4"
                                           placeholder="DB Schema name"
                                           data-bind="value: appDbInstance.schemaName"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label"
                                       for="isSlave">Is Slave:</label>

                                <div class="col-md-1">
                                    <input type="checkbox" id="isSlave" tabindex="5" name="isSlave"
                                                data-bind="checked: appDbInstance.isSlave"/>
                                </div>

                                <label class="col-md-3 control-label"
                                       for="isDeletable">Is Deletable:</label>

                                <div class="col-md-1">
                                    <input type="checkbox" id="isDeletable" tabindex="5" name="isDeletable"
                                                data-bind="checked: appDbInstance.isDeletable"/>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="port">Port:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="port" name="port" tabindex="6"
                                           placeholder="DB connection port"
                                           data-bind="value: appDbInstance.port" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="port"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="dbName">Database Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="dbName" name="dbName" tabindex="7"
                                           data-bind="value: appDbInstance.dbName" required
                                           validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="dbName"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="userName">User Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="userName" name="userName" tabindex="8"
                                           placeholder="DB username"
                                           data-bind="value: appDbInstance.userName" required
                                           validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="userName"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional" for="password">Password:</label>

                                <div class="col-md-6">
                                    <input type="password" class="k-textbox" id="password" name="password" tabindex="9"
                                           placeholder="DB password" data-bind="value: appDbInstance.password"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label"
                                       for="isReadOnly">Read Only:</label>

                                <div class="col-md-3">
                                    <input type="checkbox" id="isReadOnly" tabindex="10" name="isReadOnly"
                                                data-bind="checked: appDbInstance.isReadOnly"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <button name="create" id="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="11"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="12"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridDbInstance"></div>
    </div>
</div>
