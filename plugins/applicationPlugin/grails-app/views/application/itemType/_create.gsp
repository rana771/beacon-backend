<%@ page import="com.athena.mis.application.service.AppSystemEntityCacheService; com.athena.mis.PluginConnector" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridItemType'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Item Type
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form name='itemTypeForm' id="itemTypeForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: itemType.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: itemType.version"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="categoryId">Category:</label>

                        <div class="col-md-3">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownItemCategory"
                                    required="true"
                                    validationMessage="Required"
                                    name="categoryId"
                                    tabindex="1"
                                    typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_ITEM_CATEGORY}"
                                    data-bind="value: itemType.categoryId"
                                    pluginId="${PluginConnector.PLUGIN_ID}">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="categoryId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="name">Name:</label>

                        <div class="col-md-3">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="2"
                                   maxlength="255"
                                   placeholder="Should be Unique" required validationMessage="Required"
                                   data-bind="value: itemType.name"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/itemType/create,/itemType/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="3"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="4"
                            aria-disabled="false" onclick='resetItemTypeForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridItemType"></div>
    </div>
</div>
