<%@ page import="com.athena.mis.application.service.AppSystemEntityCacheService; com.athena.mis.PluginConnector" %>

<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridServerInstance'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Server Instance
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="serverInstanceForm" name="serverInstanceForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: serverInstance.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: serverInstance.version"/>

                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="name">Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                           data-bind="value: serverInstance.name" maxlength="255"
                                           placeholder="Unique Server Instance Name" required
                                           validationMessage="Required"
                                           autofocus/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="name"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="sshHost">SSH Host:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="sshHost" name="sshHost"
                                           tabindex="3" placeholder="SSH Host Name"
                                           data-bind="value: serverInstance.sshHost" maxlength="255" required
                                           validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="sshHost"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required"
                                       for="sshUserName">User:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="sshUserName" name="sshUserName"
                                           tabindex="4"
                                           data-bind="value: serverInstance.sshUserName" maxlength="255"
                                           placeholder="SSH User Name" required
                                           validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="sshUserName"></span>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="osVendorId">OS Vendor:</label>

                                <div class="col-md-6">
                                    <app:dropDownSystemEntity
                                            dataModelName="osVendorId"
                                            id="osVendorId"
                                            name="osVendorId"
                                            tabindex="2"
                                            data-bind="value: serverInstance.osVendorId"
                                            required="true"
                                            validationMessage="Required"
                                            typeId="${AppSystemEntityCacheService.SYS_ENTITY_TYPE_OS_VENDOR}"
                                            pluginId="${PluginConnector.PLUGIN_ID}">
                                    </app:dropDownSystemEntity>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="osVendorId"></span>
                                </div>
                            </div>


                            <div class="form-group">
                                <label class="col-md-3 control-label label-required"
                                       for="sshPassword">Password:</label>

                                <div class="col-md-6">
                                    <input type="password" class="k-textbox" id="sshPassword" name="sshPassword"
                                           tabindex="5" placeholder="SSH Password"
                                           data-bind="value: serverInstance.sshPassword" maxlength="255" required
                                           validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="sshPassword"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="sshPort">Port:</label>

                                <div class="col-md-6">
                                    <input type="text" id="sshPort" name="sshPort"
                                           tabindex="6" data-bind="value: serverInstance.sshPort"
                                           required validationMessage="Required" placeholder="SSH Port"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="sshPort"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/appServerInstance/create,/appServerInstance/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext" role="button"
                                aria-disabled="false" tabindex="11"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button"
                            aria-disabled="false" onclick='resetForm();' tabindex="12"><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridServerInstance"></div>
    </div>
</div>