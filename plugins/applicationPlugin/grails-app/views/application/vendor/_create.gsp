<%@ page import="com.athena.mis.application.service.AppSystemEntityCacheService; com.athena.mis.PluginConnector" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridVendor'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Vendor
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <g:form id='vendorForm' name='vendorForm' enctype="multipart/form-data" class="form-horizontal form-widgets"
                    role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" id="id" name="id" data-bind="value: vendor.id"/>
                    <input type="hidden" id="version" name="version" data-bind="value: vendor.version"/>

                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="name">Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                           maxlength="255"
                                           data-bind="value: vendor.name"
                                           placeholder="Unique Name" required validationMessage="Required" autofocus/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="name"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="vendorId">Vendor:</label>

                                <div class="col-md-6">
                                    <app:dropDownSystemEntity
                                            dataModelName="dropDownDbVendor"
                                            required="true"
                                            validationMessage="Required"
                                            data-bind="value: vendor.vendorId"
                                            name="vendorId"
                                            tabindex="2"
                                            typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_VENDOR}"
                                            pluginId="${PluginConnector.PLUGIN_ID}">
                                    </app:dropDownSystemEntity>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="vendorId"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="dbTypeId">DB Type:</label>

                                <div class="col-md-6">
                                    <app:dropDownSystemEntity
                                            dataModelName="dropDownDbType"
                                            required="true"
                                            validationMessage="Required"
                                            data-bind="value: vendor.dbTypeId"
                                            name="dbTypeId"
                                            tabindex="3"
                                            typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_DB_INSTANCE}"
                                            pluginId="${PluginConnector.PLUGIN_ID}">
                                    </app:dropDownSystemEntity>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="dbTypeId"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required"
                                       for="sequence">Order:</label>

                                <div class="col-md-6">
                                    <input type="text" id="sequence" name="sequence" tabindex="4"
                                           placeholder="Unique order"
                                           data-bind="value: vendor.sequence" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="sequence"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label" for="description">Description:</label>

                                <div class="col-md-9">
                                    <textarea type="text" class="k-textbox" id="description" name="description" rows="3"
                                              data-bind="value: vendor.description"
                                              placeholder="255 Char Max" tabindex="5" maxlength="255"></textarea>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="smallImage">Small Image:</label>

                                <div class="col-md-6">
                                    <input type="file" tabindex="7" id="smallImage" name="smallImage"/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="thumbImage">Thumb Image:</label>

                                <div class="col-md-6">
                                    <input type="file" tabindex="8" id="thumbImage" name="thumbImage"/>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAnyUrl urls="/vendor/create,/vendor/update">
                        <button name="create" id="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="9"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAnyUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="10"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </g:form>
        </div>
    </div>

    <div class="row">
        <div id="gridVendor"></div>
    </div>
</div>
