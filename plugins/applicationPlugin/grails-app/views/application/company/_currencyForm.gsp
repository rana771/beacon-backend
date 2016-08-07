<div id="currency_panel">
    <g:form id='currencyFormForReseller' name='currencyFormForReseller' class="form-horizontal form-widgets" role="form">
        <div class="panel-body" style="display: none;">
            <g:hiddenField name="currencyId" data-bind="value: company.currencyId"/>
            <g:hiddenField name="currencyVersion" data-bind="value: company.currencyVersion"/>
            <div class="form-group">
                <div class="col-md-6">
                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="currencyName">Name:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="currencyName" name="currencyName" tabindex="1"
                                   placeholder="Currency Name" required validationMessage="Required"
                                   data-bind="value: company.currencyName" autofocus/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="currencyName"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-required" for="symbol">Symbol:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="symbol" name="symbol" tabindex="2"
                                   maxlength="3" data-bind="value: company.symbol"
                                   placeholder="Should be Unique" required validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="symbol"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-md-3 control-label label-optional" for="otherCode">Other Code:</label>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="otherCode" name="otherCode"
                                   tabindex="3" data-bind="value: company.otherCode"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="otherCode"></span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </g:form>
</div>
