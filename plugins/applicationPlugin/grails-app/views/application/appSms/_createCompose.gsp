<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridSms'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Compose SMS
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="smsForm" name='smsForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" id="id" name="id" data-bind="value: sms.id"/>
                    <input type="hidden" id="version" name="version" data-bind="value: sms.version"/>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-optional" for="roleId">Role:</label>

                        <div class="col-md-3">
                            <app:dropDownRole
                                    dataModelName="dropDownRole"
                                    name="roleId"
                                    tabindex="1"
                                    addAllAttributes="true"
                                    hintsText="ALL">
                            </app:dropDownRole>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-1 control-label label-required" for="body">Body:</label>

                        <div class="col-md-11">
                            <textarea type="text" class="k-textbox" id="body" name="body" rows="5" maxlength="2040"
                                      placeholder="Body part of SMS..." required="" tabindex="2"
                                      data-bind="value: sms.body"
                                      validationMessage="Value is Required"></textarea>
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
                            aria-disabled="false" onclick='resetSmsForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridSms"></div>
    </div>
</div>