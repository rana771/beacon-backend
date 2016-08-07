<%@ page import="com.athena.mis.application.service.AppSystemEntityCacheService; com.athena.mis.PluginConnector" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridItemInventory'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Inventory Item
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id='itemInventoryForm' name='itemInventoryForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" id="id" name="id" data-bind="value: item.id"/>
                    <input type="hidden" id="version" name="version" data-bind="value: item.version"/>

                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="name">Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                           maxlength="255"
                                           data-bind="value: item.name"
                                           placeholder="Should be Unique" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="name"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="code">Code:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="code" name="code" tabindex="2"
                                           maxlength="255"
                                           data-bind="value: item.code"
                                           placeholder="Code" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="code"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="unit">Unit:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="unit" name="unit" tabindex="3"
                                           maxlength="255"
                                           data-bind="value: item.unit"
                                           placeholder="Unit" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="unit"></span>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-4 control-label label-required" for="itemTypeId">Item Type:</label>

                                <div class="col-md-5">
                                    <app:dropDownItemType
                                            dataModelName="dropDownItemType"
                                            required="true"
                                            validationMessage="Required"
                                            name="itemTypeId"
                                            tabindex="4"
                                            data-bind="value: item.itemTypeId"
                                            categoryId="${AppSystemEntityCacheService.SYS_ENTITY_ITEM_CATEGORY_INVENTORY}">
                                    </app:dropDownItemType>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="itemTypeId"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-4 control-label label-required"
                                       for="valuationTypeId">Valuation Type:</label>

                                <div class="col-md-5">
                                    <app:dropDownSystemEntity
                                            dataModelName="dropDownValuationType"
                                            required="true"
                                            validationMessage="Required"
                                            name="valuationTypeId"
                                            id="valuationTypeId"
                                            tabindex="5"
                                            data-bind="value: item.valuationTypeId"
                                            typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_VALUATION}"
                                            pluginId="${PluginConnector.PLUGIN_ID}">
                                    </app:dropDownSystemEntity>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="valuationTypeId"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-4 control-label" for="isFinishedProduct">Finished Product:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" id="isFinishedProduct" tabindex="6" name="isFinishedProduct"
                                                data-bind="checked: item.isFinishedProduct"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="isFinishedProduct"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/item/createInventoryItem,/item/updateInventoryItem">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="7"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="8"
                            aria-disabled="false" onclick='resetItemInventoryForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridItemInventory"></div>
    </div>
</div>