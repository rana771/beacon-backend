<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridSupplierItem'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Supplier's Item
                    <app:myFavourite>
                    </app:myFavourite>
                    <app:historyBack
                            o_id="${oId}"
                            url="${url}">
                    </app:historyBack>
                </div>
            </div>

            <form id="supplierItemForm" name="supplierItemForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" id="id" name="id" data-bind="value: supplierItem.id"/>
                    <input type="hidden" id="version" name="version" data-bind="value: supplierItem.version"/>
                    <input type="hidden" id="supplierId" name="supplierId" value="${supplier ? supplier.id : 0L}"/>

                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="supplierName">Supplier Name:</label>

                                <div class="col-md-9">
                                    <span id="supplierName">${supplier ? supplier.name : "None"}</span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="supplierAddress">Address:</label>

                                <div class="col-md-9">
                                    <span id="supplierAddress">${supplier ? supplier.address : "None"}</span>
                                </div>
                            </div>


                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="supplierAccount">Bank Account:</label>

                                <div class="col-md-9">
                                    <span id="supplierAccount">${supplier ? supplier.bankAccount : "None"}</span>
                                </div>
                            </div>


                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="supplierBankName">Bank Name:</label>

                                <div class="col-md-9">
                                    <span id="supplierBankName">${supplier ? supplier.bankName : "None"}</span>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="itemTypeId">Item Type:</label>

                                <div class="col-md-6">
                                    <app:dropDownItemType
                                            required="true"
                                            validationMessage="Required"
                                            dataModelName="dropDownItemType"
                                            name="itemTypeId" tabindex="1"
                                            data-bind="value: supplierItem.itemTypeId"
                                            onchange="onChangeItemType();">
                                    </app:dropDownItemType>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="itemTypeId"></span>
                                </div>

                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="itemId">Item:</label>

                                <div class="col-md-6">
                                    <app:dropDownSupplierItem
                                            data_model_name="dropDownSupplierItem"
                                            id="itemId"
                                            name="itemId"
                                            supplier_id="${0L}"
                                            type_id="${0L}"
                                            tabindex="2"
                                            required="true"
                                            url="${createLink(controller: 'supplierItem', action: 'dropDownSupplierItemReload')}"
                                            validation_message="Required"
                                            default_value="0"
                                            onchange="onChangeItem();"
                                            data-bind="value: supplierItem.itemId">
                                    </app:dropDownSupplierItem>

                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="itemId"></span>
                                </div>

                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional" for="itemCode">Code:</label>

                                <div class="col-md-9">
                                    <span id="itemCode" data-bind="text: supplierItem.code"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional" for="itemUnit">Unit:</label>

                                <div class="col-md-9">
                                    <span id="itemUnit" data-bind="text: supplierItem.unit"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="3"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>
                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="4"
                            aria-disabled="false" onclick='resetFormSupplierItem();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridSupplierItem"></div>
    </div>
</div>

