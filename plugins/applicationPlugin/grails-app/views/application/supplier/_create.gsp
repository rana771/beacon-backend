<%@ page import="com.athena.mis.application.service.AppSystemEntityCacheService; com.athena.mis.PluginConnector" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridSupplier'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Supplier
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="supplierForm" name="supplierForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" id="id" name="id" data-bind="value: supplier.id"/>
                    <input type="hidden" id="version" name="version" data-bind="value: supplier.version"/>

                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required"
                                       for="supplierTypeId">Supplier Type:</label>

                                <div class="col-md-6">
                                    <app:dropDownSystemEntity
                                            dataModelName="dropDownSupplierType"
                                            name="supplierTypeId"
                                            required="true"
                                            validationMessage="Required"
                                            data-bind="value: supplier.supplierTypeId"
                                            typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_SUPPLIER}"
                                            tabindex="1"
                                            pluginId="${PluginConnector.PLUGIN_ID}">
                                    </app:dropDownSystemEntity>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="supplierTypeId"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="name">Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="name" name="name" tabindex="2"
                                           maxlength="255"
                                           data-bind="value: supplier.name"
                                           placeholder="Unique Supplier Name" required validationMessage="Required"
                                           autofocus/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="name"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional" for="bankName">Bank Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="bankName" name="bankName" maxlength="255"
                                           data-bind="value: supplier.bankName"
                                           placeholder="Bank Name" tabindex="3"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="bankName"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required"
                                       for="accountName">Account Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="accountName" name="accountName"
                                           tabindex="4"
                                           data-bind="value: supplier.accountName"
                                           placeholder="Account Name" required validationMessage="Required"
                                           maxlength="255"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="accountName"></span>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="bankAccount">Account No:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="bankAccount" name="bankAccount"
                                           maxlength="255"
                                           data-bind="value: supplier.bankAccount"
                                           placeholder="Account No" tabindex="5"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="bankAccount"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional" for="address">Address:</label>

                                <div class="col-md-9">
                                    <textarea type="text" class="k-textbox" id="address" name="address" rows="4"
                                              data-bind="value: supplier.address"
                                              placeholder="255 Char Max" tabindex="6" maxlength="255"></textarea>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/supplier/create,/supplier/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="7"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="8"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridSupplier"></div>
    </div>
</div>
