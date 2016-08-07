<div class="container-fluid">
    <div class="row">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading expand-div panel-collapsed" onclick="expandCreatePanel(this, $('#gridCurrency'))">
                <span class="pull-left"><i class="glyphicon glyphicon-chevron-down"></i></span>

                <div class="panel-title">
                    Create Currency
                    <app:myFavourite>
                    </app:myFavourite>
                </div>
            </div>

            <g:form name='currencyForm' class="form-horizontal form-widgets" role="form">
                <div class="panel-body" style="display: none">
                    <g:hiddenField name="id" data-bind="value: currency.id"/>
                    <g:hiddenField name="version" data-bind="value: currency.version"/>
                    <div class="form-group">
                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="name">Name:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                           placeholder="Currency Name" required validationMessage="Required"
                                           data-bind="value: currency.name" autofocus/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="name"></span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-md-3 control-label label-required" for="symbol">Symbol:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="symbol" name="symbol" tabindex="2"
                                           maxlength="3" data-bind="value: currency.symbol"
                                           placeholder="Should be Unique" required validationMessage="Required"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="symbol"></span>
                                </div>
                            </div>

                        </div>

                        <div class="col-md-6">
                            <div class="form-group">
                                <label class="col-md-3 control-label label-optional" for="otherCode">Other Code:</label>

                                <div class="col-md-6">
                                    <input type="text" class="k-textbox" id="otherCode" name="otherCode"
                                           tabindex="3" data-bind="value: currency.otherCode"/>
                                </div>

                                <div class="col-md-3 pull-left">
                                    <span class="k-invalid-msg" data-for="otherCode"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-footer" style="display: none">
                    <app:ifAllUrl urls="/currency/create,/currency/update">
                        <button id="create" name="create" type="submit" data-role="button"
                                class="k-button k-button-icontext" role="button"
                                aria-disabled="false" tabindex="3"><span class="k-icon k-i-plus"></span>Create
                        </button>
                    </app:ifAllUrl>

                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button"
                            aria-disabled="false" onclick='resetForm();' tabindex="4"><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </g:form>
        </div>
    </div>

    <div class="row">
        <div id="gridCurrency"></div>
    </div>
</div>

