<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridAppMail'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    <g:message code="app.announcement.compose"/>
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="appMailForm" name='appMailForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" id="id" name="id" data-bind="value: appMail.id"/>
                    <input type="hidden" id="version" name="version" data-bind="value: appMail.version"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional" for="roleIds">Role:</label>

                        <div class="col-md-3">
                            <app:dropDownRole
                                    dataModelName="dropDownRole"
                                    name="roleIds"
                                    tabindex="1"
                                    addAllAttributes="true"
                                    hintsText="ALL">
                            </app:dropDownRole>
                        </div>

                        <label class="col-md-2 control-label label-required" for="displayName">Display Name:</label>

                        <div class="col-md-3">
                            <input type="text" class="k-textbox" id="displayName" name="displayName" tabindex="2"
                                   maxlength="255" data-bind="value: appMail.displayName"
                                   placeholder="Sender's Name" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-2 pull-left">
                            <span class="k-invalid-msg" data-for="displayName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="subject">Subject:</label>

                        <div class="col-md-11">
                            <input type="text" class="k-textbox" id="subject" name="subject" tabindex="3"
                                   maxlength="255"
                                   data-bind="value: appMail.subject"
                                   placeholder="Mail Subject" required validationMessage="Required"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="body">Body:</label>

                        <div class="col-md-11">
                            <textarea type="text" class="k-textbox" id="body" name="body" rows="5" maxlength="2040"
                                      placeholder="Dear '$'{userName}, Here is the Body part....." required=""
                                      tabindex="4"
                                      data-bind="value: appMail.body"
                                      validationMessage="Required"></textarea>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-11 pull-right">
                            <span class="k-invalid-msg" data-for="body"></span>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <button id="create" name="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="5"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Create
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="6"
                            aria-disabled="false" onclick='resetMailForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridAppMail"></div>
    </div>
</div>