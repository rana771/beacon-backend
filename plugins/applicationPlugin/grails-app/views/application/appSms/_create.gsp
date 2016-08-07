<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary" xmlns="http://www.w3.org/1999/html">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridSms'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Update SMS
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form name='smsForm' id="smsForm" class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" name="id" id="id" data-bind="value: sms.id"/>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional" for="transactionCode">Trans. Code:</label>

                        <div class="col-md-5"><span id="transactionCode" data-bind="text: sms.transactionCode"></span>
                        </div>

                        <label class="col-md-1 control-label label-optional" for="isActive">Active:</label>

                        <div class="col-md-4">
                            <input type="checkbox" tabindex="1" data-bind="checked: sms.isActive" id="isActive"
                                                          name="isActive"/></div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional" for="description">Description:</label>

                        <div class="col-md-10"><span id="description" data-bind="text: sms.description"></span></div>
                    </div>

                    <div class="form-group">
                        <label id="labelRecipients" class="col-md-2 control-label label-optional"
                               for="recipients">Recipients:</label>

                        <div class="col-md-10">
                            <input type="text" class="k-textbox" id="recipients" name="recipients" tabindex="2"
                                   maxlength="255" data-bind="value: sms.recipients" required="" validationMessage="Required"
                                   placeholder="Comma separated phone numbers e.g. +88017XXXXXXXX,+88018XXXXXXXX"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="body">Body:</label>

                        <div class="col-md-10">
                            <textarea type="text" class="k-textbox" id="body" name="body" rows="5" maxlength="2040"
                                      data-bind="value: sms.body"
                                      placeholder="Body part of SMS..." required="" tabindex="3"
                                      validationMessage="Required"></textarea>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/appSms/updateSms">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext"
                                role="button" tabindex="4"
                                aria-disabled="false"><span class="k-icon k-i-plus"></span>Update
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="5"
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


