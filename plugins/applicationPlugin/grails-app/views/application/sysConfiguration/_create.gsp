<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridSystemConfiguration'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Edit System Configuration Information
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <form id='sysConfigurationForm' name='sysConfigurationForm' class="form-horizontal form-widgets"
                  role="form">
                <div class="panel-body" style="display: none">
                    <input type="hidden" id="id" name="id" data-bind="value: systemConfiguration.id"/>
                    <input type="hidden" id="version" name="version" data-bind="value: systemConfiguration.version"/>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional" for="key">Key:</label>

                        <div class="col-md-10">
                            <span id="key" data-bind="text: systemConfiguration.key"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional"
                               for="transactionCode">Transaction Code:</label>

                        <div class="col-md-10">
                            <span id="transactionCode" data-bind="text: systemConfiguration.transactionCode"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional"
                               for="description">Description:</label>

                        <div class="col-md-10">
                            <span id="description" data-bind="text: systemConfiguration.description"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-required" for="value">Value:</label>

                        <div class="col-md-10">
                            <input type="text" class="k-textbox" id="value" name="value" tabindex="1"
                                   maxlength="500" data-bind="value: systemConfiguration.value"
                                   placeholder="Value of System Configuration" required
                                   validationMessage="Required"/>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-2 control-label label-optional" for="message">Message:</label>

                        <div class="col-md-10">
                            <textarea type="text" class="k-textbox" id="message" name="message" rows="1"
                                      maxlength="255" data-bind="value: systemConfiguration.message"
                                      placeholder="Message for System Configuration" tabindex="2"></textarea>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:checkSystemUser isConfigManager="true">
                        <app:ifAllUrl urls="/systemConfiguration/update">
                            <button id="create" name="create" type="submit" data-role="button"
                                    class="k-button k-button-icontext" role="button"
                                    aria-disabled="false" tabindex="3"><span class="k-icon k-i-plus"></span>Update
                            </button>
                        </app:ifAllUrl>
                    </app:checkSystemUser>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button"
                            aria-disabled="false" onclick='resetForm();' tabindex="4"><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="row">
        <div id="gridSystemConfiguration"></div>
    </div>
</div>


