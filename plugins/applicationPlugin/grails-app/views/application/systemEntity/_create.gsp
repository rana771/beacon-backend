<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary" xmlns="http://www.w3.org/1999/html">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridSystemEntity'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create System Entity
                    <app:myFavourite>
                    </app:myFavourite>
                    <app:historyBack
                            o_id="${oId}"
                            url="${url}"
                            plugin="${plugin}">
                    </app:historyBack>
                </div>
            </div>

            <form name='systemEntityForm' id="systemEntityForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: systemEntity.id"/>
                    <input type="hidden" name="version" id="version" data-bind="value: systemEntity.version"/>
                    <input type="hidden" name="systemEntityTypeId" id="systemEntityTypeId"/>

                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional"
                                       for="systemEntityTypeName">Entity Type:</label>

                                <div class="col-md-6">
                                    <span id="systemEntityTypeName">${systemEntityTypeName}</span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="key">Key:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="key" name="key" tabindex="1"
                                           maxlength="255" data-bind="value: systemEntity.key"
                                           placeholder="Should be Unique" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="key"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional" for="value">Value:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="value" name="value" maxlength="255"
                                           placeholder="System Entity Value" tabindex="2" data-bind="value: systemEntity.value"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="value"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional" for="isActive">Active:</label>

                                <div class="col-md-6">
                                    <input type="checkbox" tabindex="3" id="isActive" name="isActive"
                                                data-bind="checked: systemEntity.isActive"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="isActive"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:checkSystemUser isConfigManager="true">
                        <app:ifAllUrl urls="/systemEntity/create,/systemEntity/update">
                            <button id="create" name="create" type="submit" data-role="button"
                                    class="k-button k-button-icontext" role="button"
                                    aria-disabled="false" tabindex="4"><span class="k-icon k-i-plus"></span>Create
                            </button>
                        </app:ifAllUrl>
                    </app:checkSystemUser>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button"
                            aria-disabled="false" onclick='resetSystemEntityForm();' tabindex="5"><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridSystemEntity"></div>
    </div>
</div>
