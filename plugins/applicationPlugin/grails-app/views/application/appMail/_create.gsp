<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridAppMail'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Update Mail
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id="appMailForm" name='appMailForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" id="id" name="id" data-bind="value: appMail.id"/>
                    <input type="hidden" id="version" name="version" data-bind="value: appMail.version"/>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional" for="transactionCode">Trans. Code:</label>

                        <div class="col-md-4"><span id="transactionCode"
                                                    data-bind="text: appMail.transactionCode"></span></div>

                        <label class="col-md-2 control-label label-optional" for="isActive">Active:</label>

                        <div class="col-md-4">
                            <input type="checkbox" tabindex="2" data-bind="checked: appMail.isActive"
                                                          id="isActive" name="isActive"/></div>
                    </div>

                    <div class="form-group">
                        <label id="labelRoleIds" class="col-md-2 control-label label-optional"
                               for="roleIds">Role(s):</label>

                        <div class="col-md-10">
                            <input type="text" class="k-textbox" id="roleIds" name="roleIds" tabindex="1"
                                   maxlength="255"
                                   data-bind="value: appMail.roleIds"
                                   placeholder="Comma separated Role ID(s)" autofocus/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label id="labelRecipients" class="col-md-2 control-label label-optional"
                               for="recipients">Recipient(s):</label>

                        <div class="col-md-10">
                            <input type="text" class="k-textbox" id="recipients" name="recipients" tabindex="2"
                                   data-bind="value: appMail.recipients"
                                   placeholder="Comma separated valid email id" maxlength="255" autofocus/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional"
                               for="displayName">Display Name:</label>

                        <div class="col-md-10">
                            <input type="text" class="k-textbox" id="displayName" name="displayName" tabindex="4"
                                   data-bind="value: appMail.displayName"
                                   placeholder="Sender's name" maxlength="255" autofocus/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label id="labelRecipientsCc" class="col-md-2 control-label label-optional"
                               for="recipientsCc">Recipients(Cc):</label>

                        <div class="col-md-10">
                            <input type="text" class="k-textbox" id="recipientsCc" name="recipientsCc" tabindex="3"
                                   data-bind="value: appMail.recipientsCc"
                                   placeholder="Comma separated valid email id" maxlength="255" autofocus/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="subject">Subject:</label>

                        <div class="col-md-10">
                            <input type="text" class="k-textbox" id="subject" name="subject" tabindex="5"
                                   maxlength="255"
                                   data-bind="value: appMail.subject"
                                   placeholder="Mail Subject" required validationMessage="Required"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="body">Body:</label>

                        <div class="col-md-10">
                            <textarea type="text" class="k-textbox" id="body" name="body" rows="5" maxlength="2040"
                                      placeholder="Body part of mail..." required="" tabindex="6"
                                      data-bind="value: appMail.body"
                                      validationMessage="Value is Required"></textarea>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <button id="update" name="update" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="7"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Update
                    </button>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="8"
                            aria-disabled="false" onclick='resetForm();'><span
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


