<%@ page import="com.athena.mis.application.service.AppSystemEntityCacheService; com.athena.mis.PluginConnector" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridContentCategory'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Content Category
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form name='contentCategoryForm' id='contentCategoryForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" id="id" name="id" data-bind="value: contentCategory.id"/>
                    <input type="hidden" id="version" name="version" data-bind="value: contentCategory.version"/>

                    <app:systemEntityByReserved
                            name="hidContentTypeDocumentId"
                            reservedId="${AppSystemEntityCacheService.SYS_ENTITY_CONTENT_DOCUMENT}"
                            typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT}"
                            pluginId="${PluginConnector.PLUGIN_ID}">
                    </app:systemEntityByReserved>

                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required"
                                       for="contentTypeId">Content Type:</label>

                                <div class="col-md-6">
                                    <app:dropDownSystemEntity
                                            required="true"
                                            validationMessage="Required"
                                            tabindex="1"
                                            name="contentTypeId"
                                            data-bind="value: contentCategory.contentTypeId"
                                            onchange="toggleWidthHeight()"
                                            typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_CONTENT}"
                                            dataModelName="dropDownContentType"
                                            pluginId="${PluginConnector.PLUGIN_ID}">
                                    </app:dropDownSystemEntity>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="contentTypeId"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="name">Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="name" name="name" tabindex="2"
                                           maxlength="255"
                                           data-bind="value: contentCategory.name"
                                           placeholder="Content Name" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="name"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional" for="extension">Extension:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="extension" name="extension" maxlength="255"
                                           data-bind="value: contentCategory.extension"
                                           placeholder="Content Extension" tabindex="3"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="extension"></span>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">

                            <div class="form-group">
                                <label id="labelWidth" class="col-md-3 control-label label-required"
                                       for="width">Width:</label>

                                <div class="col-md-6">
                                    <input type="text" id="width" name="width" tabindex="4"
                                           data-bind="value: contentCategory.width"
                                           placeholder="( in pixel )" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="width"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label id="labelHeight" class="col-md-3 control-label label-required"
                                       for="height">Height:</label>

                                <div class="col-md-6">
                                    <input type="text" id="height" name="height" tabindex="5"
                                           data-bind="value: contentCategory.height"
                                           placeholder="( in pixel )" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="height"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="maxSize">Max Size:</label>

                                <div class="col-md-6">
                                    <input type="text" id="maxSize" name="maxSize" class="" tabindex="6"
                                           data-bind="value: contentCategory.maxSize"
                                           placeholder="( in bytes )" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="maxSize"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/contentCategory/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="7"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="8"
                            aria-disabled="false" onclick='resetContentCategoryForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridContentCategory"></div>
    </div>
</div>
